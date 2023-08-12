package ca.myairbuddyandi;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Michel on 2017-09-25.
 * Hold all of the logic for the RockbottomManageManageDiveSegment class
 */

public class RockbottomManageDiveSegment {

    // Static
    private static final String LOG_TAG = "RockbottomManageManageDiveSegment";

    // Public

    // Protected

    // Private
    private final Context mContext;
    private MyCalc mMyCalc;

    // End of variables

    // Public constructor
    public RockbottomManageDiveSegment(Context context) {
        mContext = context;
    }

    Double generateDiveSegment(
              Long diverNo
            , Long diveNo
            , String diveStatus
            , Boolean salinity
            , Double sac
            , Double rmv
            , Double beginningVolume
            , Double beginningPressure
            , Double ratedPressure
            , Double ratedVolume
            , Double forcedBottomTime) {

        AirDA airDa = new AirDA(mContext);

        airDa.open();

        // Delete previous Dive Segments
        airDa.deleteDiveSegmentByDiverNoDiveNo(diverNo, diveNo);

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        MyCalc mMyCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(mContext);
        } else {
            mMyCalc = new MyCalcMetric(mContext);
        }

        Double bubbleCheckDepth = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.BUBBLE_CHECK_DEPTH, mMyCalc.getBubbleCheckDepthDefault()))));
        int bubbleCheckTime = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.BUBBLE_CHECK_TIME, "1"))));
        int descentRate = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DESCENT_RATE, mMyCalc.getDescentRateDefault()))));
        int turnaroundTime = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.TURNAROUND_TIME, "1"))));
        int ascentRateToDs = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ASCENT_RATE_TO_DS, mMyCalc.getAscentRateToDsDefault()))));
        int ascentRateToSs = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ASCENT_RATE_TO_SS, mMyCalc.getAscentRateToSsDefault()))));
        int ascentRateToSu = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ASCENT_RATE_TO_SU, mMyCalc.getAscentRateToSuDefault()))));
        int deepStopDive = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DEEP_STOP_DIVE, mMyCalc.getDeepStopDiveDefault()))));
        int deepStopPercent = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DEEP_STOP_PERCENT, "50"))));
        int deepStopTime = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DEEP_STOP_TIME, "1"))));
        int safetyStopDive = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.SAFETY_STOP_DIVE, mMyCalc.getSafetyStopDiveDefault()))));
        Double safetyStopDepth = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.SAFETY_STOP_DEPTH, mMyCalc.getSafetyStopDepthDefault()))));
        safetyStopDepth = mMyCalc.getMinimumSafetyStopDepth(safetyStopDepth);
        int safetyStopTime = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.SAFETY_STOP_TIME, "3"))));
        int rockBottomMinPressure = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ROCK_BOTTOM_MIN_PRESSURE, mMyCalc.getRockbottomMinPressureDefault()))));
        Double rockbottomSac = Double.parseDouble(Objects.requireNonNull(preferences.getString(MyConstants.ROCK_BOTTOM_SAC, mMyCalc.getRockbottomSacDefault())));
        Double rockbottomRmv = Double.parseDouble(Objects.requireNonNull(preferences.getString(MyConstants.ROCK_BOTTOM_RMV, mMyCalc.getRockbottomRmvDefault())));
        String subtractDeepStopTime = (preferences.getBoolean(MyConstants.SUBTRACT_DEEP_STOP_TIME,false)) ? MyConstants.YES : MyConstants.NO;
        int ooaTurnaroundTime = Integer.parseInt(MyFunctions.replaceEmptyByOne(Objects.requireNonNull(preferences.getString(MyConstants.OOA_TURNAROUND_TIME, mMyCalc.getOoaTurnaroundTimeDefault()))));

        // Find the Working (W) SAC and RMV from previous dives
        // Working SAC 29.0 and RMV 0.8 for Me,       if not assume 35.0 and 1.0
        // Working SAC 31.0 and RMV 1.0 for My Buddy, if not assume 35.0 and 1.0
        //             ====         ===                             ====     ===
        //  Total      60.0         1.8                             70.0     2.0
        SacRmvWorking sacRmvWorking = airDa.getWorkingSacRmv(diverNo, diveNo, rockbottomSac, rockbottomRmv);

        // If none, use the one from the Application Settings

        // Generate the Dive Segments
        airDa.insertDiveSegmentRockbottom(
                 diverNo
                ,diveNo
                ,(salinity) ? mMyCalc.getSeaWater() : mMyCalc.getFreshWater()
                ,bubbleCheckDepth
                ,bubbleCheckTime
                ,descentRate
                ,turnaroundTime
                ,ascentRateToDs
                ,ascentRateToSs
                ,ascentRateToSu
                ,deepStopDive
                ,deepStopPercent
                ,deepStopTime
                ,safetyStopDive
                ,safetyStopDepth
                ,safetyStopTime
                ,ooaTurnaroundTime
                ,subtractDeepStopTime
                ,MyConstants.NO
                ,MyConstants.NO
        );

        // Load the Dive Segments into an Array
        ArrayList<DiveSegment> diveSegmentList;
        diveSegmentList = airDa.getAllDiveSegments(diverNo,diveNo);

        if (diveSegmentList.size() == MyConstants.ZERO_I) {
            Toast.makeText(mContext, "Segments not generated for Diver: " + diverNo + " and " + diveNo, Toast.LENGTH_SHORT).show();
            return MyConstants.ZERO_D;
        }

        // Find the Turnaround (TA) Segment
        DiveSegment diveSegment;
        int iTa = MyFunctionsSegments.findTurnaroundSegment(diveSegmentList);
        int iBtBeforeTa = iTa - 1;
        int iBTAfterTa = iTa + 1;
        Double sumPressure = MyConstants.ZERO_D;

        // Divide the Time of the BT before and after the Turnaround (TA) by 2
        // A planned BT of 10 minutes, becomes 2 BT of 5 minutes; to and back
        if (forcedBottomTime .equals(MyConstants.ZERO_D)) {
            diveSegment = diveSegmentList.get(iBtBeforeTa);
            diveSegment.setMinute(diveSegment.getMinute() / 2);
            diveSegmentList.set(iBtBeforeTa, diveSegment);

            diveSegment = diveSegmentList.get(iBTAfterTa);
            diveSegment.setMinute(diveSegment.getMinute() / 2);
            diveSegmentList.set(iBTAfterTa, diveSegment);
        } else {
            // An already calculated BT of 10 minutes, becomes 2 BT of 5 minutes; to and back
            diveSegment = diveSegmentList.get(iBtBeforeTa);
            diveSegment.setMinute(forcedBottomTime / 2);
            diveSegmentList.set(iBtBeforeTa, diveSegment);

            diveSegment = diveSegmentList.get(iBTAfterTa);
            diveSegment.setMinute(forcedBottomTime / 2);
            diveSegmentList.set(iBTAfterTa, diveSegment);
        }

        // Perform calculations
        for (int i=0;i<diveSegmentList.size();i++)
        {
            diveSegment = diveSegmentList.get(i);
            Double depth;
            double min;
            String nextSegmentType;

            // Set the Minute
            switch(diveSegment.getSegmentType()) {
                case "DE": // Descent
                    depth = MyFunctionsSegments.findPreviousDepth(diveSegmentList,i);
                    min = (diveSegment.getDepth() - depth)  / descentRate;
                    diveSegment.setMinute(min);
                    break;

                case "ADS": // Ascent to Deep Stop
                    depth = MyFunctionsSegments.findNextDepth(diveSegmentList,i);
                    min = (diveSegment.getDepth() - depth)  / ascentRateToDs;
                    diveSegment.setMinute(min);
                    break;

                case "ASS": // Ascent to Safety Stop
                    min = (diveSegment.getDepth() - safetyStopDepth)  / ascentRateToSs;
                    diveSegment.setMinute(min);
                    break;

                case "AS": // Ascent to next BT or Surface
                    nextSegmentType = MyFunctionsSegments.findNextSegmentType(diveSegmentList,i);
                    if (nextSegmentType.equals("STO")) {
                        min = (diveSegment.getDepth() - 0)  / ascentRateToSu;
                    } else {
                        depth = MyFunctionsSegments.findNextDepth(diveSegmentList,i);
                        min = (diveSegment.getDepth() - depth)  / ascentRateToSs;
                    }
                    diveSegment.setMinute(min);
                    break;
            }

            // Set calc_average_ata
            switch(diveSegment.getSegmentType()) {
                // Same depth segment
                case "STA": // Start
                case "BC":  // Bubble Check
                case "BT":  // Bottom Time
                case "TA":  // Turnaround
                case "DS":  // Deep Stop
                case "SS":  // Safety Stop
                case "STO": // Stop
                case "OOA": // Out Of Air
                case "DD":  // Deep Deco
                case "DEC": // Deco
                    diveSegment.setCalcAverageAta(diveSegment.getCalcAta());
                    break;

                // Going down segments
                case "DE":  // Descent
                    diveSegment.setCalcAverageAta(mMyCalc.getAverageAta(MyFunctionsSegments.findPreviousCalcAta(diveSegmentList,i),diveSegment.getCalcAta()));
                    break;

                // Going up segments
                case "ADD": // Ascent to Deep Deco
                case "AD":  // Ascent to Deco
                case "ADS": // Ascent to Deep Stop
                case "ASS": // Ascent to Safety Stop
                case "AS":  // Ascent to Surface
                    diveSegment.setCalcAverageAta(mMyCalc.getAverageAta(MyFunctionsSegments.findNextCalcAta(diveSegmentList,i),diveSegment.getCalcAta()));
                    break;
            }

            // Set the regular consumption for all descent and bottom time segments
            // Set the OOA consumption starting for all ascent segments to calculate the rockbottom just after the last Bottom Time
            // It makes a difference if the OOA occurs at the beginning ot the BT or at the end of the bottom time
            // If at the beginning the divers probably have enough air to come up
            // If at the end, need to make sure the divers have enough air to come up
            switch(diveSegment.getSegmentType()) {
                // All of the Ascent type, use the Working SAC and RMV
                case "OOA": // Out of Air
                case "ADS": // Ascent to Deep Stop
                case "DS":  // Deep Stop
                case "ASS": // Ascent to Safety Stop
                case "SS":  // Safety Stop
                case "AS":  // Ascend to Surface
                    // Set the air_consumption_pressure
                    diveSegment.setAirConsumptionPressure(sacRmvWorking.getSacBoth() * diveSegment.getCalcAverageAta() * diveSegment.getMinute());

                    // Set the air_consumption_volume
                    diveSegment.setAirConsumptionVolume(sacRmvWorking.getRmvBoth() * diveSegment.getMinute() * diveSegment.getCalcAverageAta());
                    break;

                // All of the Descent type, use the regular calculations for SAC and RMV
                default:
                    // Set the air_consumption_pressure
                    if (diveStatus.equals(MyConstants.PLAN)) {
                        // In case there is some discrepancies between the SAC and the RMV, calculates the SAC from the RMV
                        diveSegment.setAirConsumptionPressure(mMyCalc.getSac(rmv, mMyCalc.getCCF(ratedVolume, ratedPressure)) * diveSegment.getCalcAverageAta() * diveSegment.getMinute());
                    } else {
                        // Use the real SAC, calculated by this app on a Real dive!
                        diveSegment.setAirConsumptionPressure(sac * diveSegment.getCalcAverageAta() * diveSegment.getMinute());
                    }

                    // Set the air_consumption_volume
                    diveSegment.setAirConsumptionVolume(rmv * diveSegment.getMinute() * diveSegment.getCalcAverageAta());
                    break;
            }

            // Set calc_average_depth
            diveSegment.setCalcAverageDepth(mMyCalc.getAverageDepth( (salinity) ? mMyCalc.getSeaWater() : mMyCalc.getFreshWater()
                                                                    ,diveSegment.getCalcAverageAta()
                                                                   ));

            if (!diveSegment.getSegmentType().equals("BT")) {
                sumPressure += diveSegment.getAirConsumptionPressure();
            }

            diveSegmentList.set(i,diveSegment);
        }

        Double totalOptimizedBottomTime = MyConstants.ZERO_D;

        // Start optimizing the Bottom Times
        // Optimize the Bottom Time (BT) BEFORE and AFTER the Turnaround (TA)
        //                               ======     =====
        Double deltaPressure = (beginningPressure - rockBottomMinPressure - sumPressure) / 2;

        // Optimizing the Bottom Time (BT) BEFORE the Turnaround (TA)
        //                                 ======
        diveSegment = diveSegmentList.get(iBtBeforeTa);

        if (deltaPressure > MyConstants.ZERO_D && forcedBottomTime.equals(MyConstants.ZERO_D)) {
            diveSegment.setAirConsumptionPressure(deltaPressure);
        }

        if (forcedBottomTime.equals(MyConstants.ZERO_D)) {
            // In case there is some discrepancies between the SAC and the RMV, calculates the SAC from the RMV
            if (diveStatus.equals(MyConstants.PLAN)) {
                // In case there is some discrepancies between the SAC and the RMV, calculates the SAC from the RMV
                diveSegment.setMinute(mMyCalc.getTimePressure(diveSegment.getAirConsumptionPressure(), mMyCalc.getSac(rmv, mMyCalc.getCCF(ratedVolume, ratedPressure)), diveSegment.getCalcAverageAta()));
            } else {
                // Use the real SAC
                diveSegment.setMinute(mMyCalc.getTimePressure(diveSegment.getAirConsumptionPressure(), sac, diveSegment.getCalcAverageAta()));
            }
            totalOptimizedBottomTime += diveSegment.getMinute();
        }

        // Not based on the Delta Volume but rather on the formula using the new figures just calculated
        diveSegment.setAirConsumptionVolume(mMyCalc.getVolume(rmv, diveSegment.getCalcAverageAta(), diveSegment.getMinute()));

        diveSegmentList.set(iBtBeforeTa,diveSegment);

        // Optimizing the Bottom Time (BT) after the Turnaround (TA)
        //                                 =====
        diveSegment = diveSegmentList.get(iBTAfterTa);

        if (deltaPressure > MyConstants.ZERO_D && forcedBottomTime.equals(MyConstants.ZERO_D)) {
            diveSegment.setAirConsumptionPressure(deltaPressure);
        }

        if (forcedBottomTime.equals(MyConstants.ZERO_D)) {
            if (diveStatus.equals(MyConstants.PLAN)) {
                // In case there is some discrepancies between the SAC and the RMV, calculates the SAC from the RMV
                diveSegment.setMinute(mMyCalc.getTimePressure(diveSegment.getAirConsumptionPressure(), mMyCalc.getSac(rmv, mMyCalc.getCCF(ratedVolume, ratedPressure)), diveSegment.getCalcAverageAta()));
            } else {
                // Use the real SAC
                diveSegment.setMinute(mMyCalc.getTimePressure(diveSegment.getAirConsumptionPressure(), sac, diveSegment.getCalcAverageAta()));
            }
            totalOptimizedBottomTime += diveSegment.getMinute();
        }

        // Not based on the Delta Volume but rather on the formula using the new figures just calculated
        diveSegment.setAirConsumptionVolume(mMyCalc.getVolume(rmv, diveSegment.getCalcAverageAta(), diveSegment.getMinute()));

        diveSegmentList.set(iBTAfterTa,diveSegment);

        // End optimizing the Bottom Times

        // Calculate the decreasing pressure and volume for all segments
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            // NOTE: Leave as is
            diveSegmentList = MyFunctionsSegments.calculateDecreasingPressureVolume(diveSegmentList, beginningPressure, beginningVolume);
        } else {
            // NOTE: Leave as is
            diveSegmentList = MyFunctionsSegments.calculateDecreasingPressureVolumeMetric(diveSegmentList, ratedVolume, beginningPressure, beginningVolume);
        }

        // Update Dive Segments
        for (int i=0;i<diveSegmentList.size();i++)
        {
            diveSegment = diveSegmentList.get(i);
            airDa.updateDiveSegment(diveSegment);
        }

        return totalOptimizedBottomTime;
    }

    public void getDivesForCompare(
            DivesForCompare divesForCompare
    ) {
        AirDA airDa = new AirDA(mContext);

        airDa.open();

        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        airDa.getDivesForCompare(divesForCompare);

        // Get the phone unit according to the Locale
        // Phone unit
        String mUnit = MyFunctions.getUnit();


        if (mUnit.equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(mContext);
        } else {
            mMyCalc = new MyCalcMetric(mContext);
        }

        // At this point, only the Beginning Pressure is available
        // Turnaround Pressure and Ending Pressure will be calculated in a later pass
        // In other words, we only need to adjust the Beginning Pressure with the temperatures in order to also obtain and Adjusted and Ending pressures
        // Private

        if (divesForCompare.getAirTemp() > MyConstants.ZERO_D && divesForCompare.getWaterTempBottom() > MyConstants.ZERO_D) {
            divesForCompare.setMyBeginningPressure1(MyFunctions.roundUp(mMyCalc.getCP2(divesForCompare.getMyBeginningPressure1(),divesForCompare.getAirTemp(),divesForCompare.getWaterTempBottom()),1));
            divesForCompare.setMyBuddyBeginningPressure1(MyFunctions.roundUp(mMyCalc.getCP2(divesForCompare.getMyBuddyBeginningPressure1(),divesForCompare.getAirTemp(),divesForCompare.getWaterTempBottom()),1));
        }
    }

    public void generateDive(DiveForGraphic diveForGraphic) {
        Double bottomTimeMe = MyConstants.ZERO_D;
        Double bottomTimeMb = MyConstants.ZERO_D;
        // Generate the DIVE_SEGMENT
        // Me
        if (diveForGraphic.getMyDiverNo() != MyConstants.ZERO_L && !diveForGraphic.getMyBeginningVolume().equals(MyConstants.ZERO_D) && !diveForGraphic.getMyRatedPressure().equals(MyConstants.ZERO_D)) {
            bottomTimeMe = insertMySegments(MyConstants.ZERO_D, diveForGraphic);
        }
        // My Buddy
        if (diveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L && !diveForGraphic.getMyBuddyBeginningVolume().equals(MyConstants.ZERO_D) && !diveForGraphic.getMyBuddyRatedPressure().equals(MyConstants.ZERO_D)) {
            bottomTimeMb = insertMyBuddySegments(MyConstants.ZERO_D, diveForGraphic);
        }

        // Regenerate the dive segments for the diver that had the highest Bottom Time, with the lowest Bottom Time
        // That diver will end up with a much higher turnaround pressure
        // But buddy's are supposed to dive together after all!
        if (diveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            if (!bottomTimeMe.equals(bottomTimeMb)) {
                if (bottomTimeMe < bottomTimeMb) {
                    // Regenerate my buddy with a lower bottom time
                    insertMyBuddySegments(bottomTimeMe, diveForGraphic);
                } else {
                    // Regenerate me with a lower bottom time
                    insertMySegments(bottomTimeMb, diveForGraphic);
                }
            }
        }
    }

    public void generateDive(DivesForCompare divesForCompare, Context context) {
        DiveForGraphic diveForGraphic = new DiveForGraphic(context);

        // Dive 1
        diveForGraphic.setDiveNo(divesForCompare.getDiveNo1());
        diveForGraphic.setSalinity(divesForCompare.getSalinity());
        diveForGraphic.setMyDiverNo(divesForCompare.getMeMyBuddy1());
        diveForGraphic.setStatus(divesForCompare.getStatus1());
        diveForGraphic.setMySac(divesForCompare.getSac1());
        diveForGraphic.setMyRmv(divesForCompare.getRmv1());
        diveForGraphic.setMyBeginningPressure(divesForCompare.getMyBeginningPressure1());
        diveForGraphic.setMyBeginningVolume(divesForCompare.getMyBeginningVolume1());
        diveForGraphic.setMyRatedPressure(divesForCompare.getMyRatedPressure1());
        diveForGraphic.setMyRatedVolume(divesForCompare.getMyRatedVolume1());
        diveForGraphic.setMyBuddyDiverNo(divesForCompare.getMyBuddyDiverNo1());
        diveForGraphic.setMyBuddySac(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyRmv(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyBeginningVolume(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyRatedPressure(MyConstants.ZERO_D);
        generateDive(diveForGraphic);

        // Dive 2
        diveForGraphic.setDiveNo(divesForCompare.getDiveNo2());
        diveForGraphic.setMyDiverNo(divesForCompare.getMeMyBuddy2());
        diveForGraphic.setStatus(divesForCompare.getStatus2());
        diveForGraphic.setMySac(divesForCompare.getSac2());
        diveForGraphic.setMyRmv(divesForCompare.getRmv2());
        diveForGraphic.setMyBeginningPressure(divesForCompare.getMyBeginningPressure2());
        diveForGraphic.setMyBeginningVolume(divesForCompare.getMyBeginningVolume2());
        diveForGraphic.setMyRatedPressure(divesForCompare.getMyRatedPressure2());
        diveForGraphic.setMyRatedVolume(divesForCompare.getMyRatedVolume2());
        diveForGraphic.setMyBuddyDiverNo(divesForCompare.getMyBuddyDiverNo2());
        diveForGraphic.setMyBuddySac(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyRmv(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyBeginningVolume(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyRatedPressure(MyConstants.ZERO_D);
        generateDive(diveForGraphic);

        // Dive 3
        diveForGraphic.setDiveNo(divesForCompare.getDiveNo3());
        diveForGraphic.setMyDiverNo(divesForCompare.getMeMyBuddy3());
        diveForGraphic.setStatus(divesForCompare.getStatus3());
        diveForGraphic.setMySac(divesForCompare.getSac3());
        diveForGraphic.setMyRmv(divesForCompare.getRmv3());
        diveForGraphic.setMyBeginningPressure(divesForCompare.getMyBeginningPressure3());
        diveForGraphic.setMyBeginningVolume(divesForCompare.getMyBeginningVolume3());
        diveForGraphic.setMyRatedPressure(divesForCompare.getMyRatedPressure3());
        diveForGraphic.setMyRatedVolume(divesForCompare.getMyRatedVolume3());
        diveForGraphic.setMyBuddyDiverNo(divesForCompare.getMyBuddyDiverNo3());
        diveForGraphic.setMyBuddySac(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyRmv(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyBeginningVolume(MyConstants.ZERO_D);
        diveForGraphic.setMyBuddyRatedPressure(MyConstants.ZERO_D);
        generateDive(diveForGraphic);
    }

    public void getDiveForGraphicRockbottom(Long diveNo, DiveForGraphic diveForGraphic) {
        AirDA airDa = new AirDA(mContext);

        airDa.open();

        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        airDa.getDiveForGraphicRockbottom(diveNo, diveForGraphic);

        // Get the phone unit according to the Locale
        // Phone unit
        String mUnit = MyFunctions.getUnit();

        if (mUnit.equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(mContext);
        } else {
            mMyCalc = new MyCalcMetric(mContext);
        }

        // At this point, only the Beginning Pressure is available
        // Turnaround Pressure and Ending Pressure will be calculated in a later pass
        // In other words, we only need to adjust the Beginning Pressure with the temperatures in order to also obtain and Adjusted and Ending pressures
        // Private

        if (diveForGraphic.getAirTemp() > MyConstants.ZERO_D && diveForGraphic.getWaterTempBottom() > MyConstants.ZERO_D) {
            diveForGraphic.setMyBeginningPressure(MyFunctions.roundUp(mMyCalc.getCP2(diveForGraphic.getMyBeginningPressure(),diveForGraphic.getAirTemp(),diveForGraphic.getWaterTempBottom()),1));
            diveForGraphic.setMyBuddyBeginningPressure(MyFunctions.roundUp(mMyCalc.getCP2(diveForGraphic.getMyBuddyBeginningPressure(),diveForGraphic.getAirTemp(),diveForGraphic.getWaterTempBottom()),1));
        }
    }

    String getRockbottomMinPressureDefault () {
        return mMyCalc.getRockbottomMinPressureDefault();
    }

    Double insertMySegments(Double forcedBottomTime, DiveForGraphic diveForGraphic) {
        return generateDiveSegment(
                  diveForGraphic.getMyDiverNo()
                , diveForGraphic.getDiveNo()
                , diveForGraphic.getStatus()
                , diveForGraphic.getSalinity()
                , diveForGraphic.getMySac()
                , diveForGraphic.getMyRmv()
                , diveForGraphic.getMyBeginningVolume()
                , diveForGraphic.getMyBeginningPressure()
                , diveForGraphic.getMyRatedPressure()
                , diveForGraphic.getMyRatedVolume()
                , forcedBottomTime
        );
    }

    Double insertMyBuddySegments(Double forcedBottomTime, DiveForGraphic diveForGraphic) {
        return generateDiveSegment(
                  diveForGraphic.getMyBuddyDiverNo()
                , diveForGraphic.getDiveNo()
                , diveForGraphic.getStatus()
                , diveForGraphic.getSalinity()
                , diveForGraphic.getMyBuddySac()
                , diveForGraphic.getMyBuddyRmv()
                , diveForGraphic.getMyBuddyBeginningVolume()
                , diveForGraphic.getMyBuddyBeginningPressure()
                , diveForGraphic.getMyBuddyRatedPressure()
                , diveForGraphic.getMyBuddyRatedVolume()
                , forcedBottomTime
        );
    }
}