package ca.myairbuddyandi;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Michel on 2017-09-25.
 * Hold all of the logic for the DriftManageDiveSegment class
 */


public class DriftManageDiveSegment {

    // Static
    private static final String LOG_TAG = "ManageDriftDiveSegment";

    // Public

    // Protected

    // Private
    private MyCalc mMyCalc;
    private final Context mContext;

    // End of variables

    // Public constructor
    public DriftManageDiveSegment(Context context) {
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
              // If = 0.0, first pass, only one diver or divers have the same bottom times
              // if > 0.0, second pass, represent the bottom time of the diver with the smallest bottom time
            , Double minimumTimeBetweenTwoDivers
            , String optimize) {

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
        int myMinPressure = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.MY_MIN_PRESSURE, mMyCalc.getMyMinPressureDefault()))));
        String subtractDeepStopTime = (preferences.getBoolean(MyConstants.SUBTRACT_DEEP_STOP_TIME,false)) ? MyConstants.YES : MyConstants.NO;


        // Generate the Dive Segments
        airDa.insertDiveSegmentDrift(
                 diverNo
                ,diveNo
                ,(salinity) ? mMyCalc.getSeaWater() : mMyCalc.getFreshWater()
                ,bubbleCheckDepth
                ,bubbleCheckTime
                ,descentRate
                ,ascentRateToDs
                ,ascentRateToSs
                ,ascentRateToSu
                ,deepStopDive
                ,deepStopPercent
                ,deepStopTime
                ,safetyStopDive
                ,safetyStopDepth
                ,safetyStopTime
                ,subtractDeepStopTime
                ,MyConstants.NO
                ,MyConstants.NO
        );

        // Load the Dive Segments into an Array
        ArrayList<DiveSegment> mDiveSegmentList = airDa.getAllDiveSegments(diverNo, diveNo);

        if (mDiveSegmentList.size() == MyConstants.ZERO_I) {
            Toast.makeText(mContext, "Segments not generated for Diver: " + diverNo + " and " + diveNo, Toast.LENGTH_SHORT).show();
            return MyConstants.ZERO_D;
        }

        DiveSegment diveSegment;
        Double sumPressure = MyConstants.ZERO_D;
        int iBt = MyFunctionsSegments.findAscentSegment(mDiveSegmentList, "AS") - 1;

        // Perform calculations
        for (int i = 0; i< mDiveSegmentList.size(); i++)
        {
            diveSegment = mDiveSegmentList.get(i);
            Double depth;
            double min;
            String nextSegmentType;

            // Set the Minute
            switch(diveSegment.getSegmentType()) {
                case "DE": // Descent
                    depth = MyFunctionsSegments.findPreviousDepth(mDiveSegmentList,i);
                    min = (diveSegment.getDepth() - depth)  / descentRate;
                    diveSegment.setMinute(min);
                    break;

                case "ADS": // Ascent to Deep Stop
                    depth = MyFunctionsSegments.findNextDepth(mDiveSegmentList,i);
                    min = (diveSegment.getDepth() - depth)  / ascentRateToDs;
                    diveSegment.setMinute(min);
                    break;

                case "ASS": // Ascent to Safety Stop
                    min = (diveSegment.getDepth() - safetyStopDepth)  / ascentRateToSs;
                    diveSegment.setMinute(min);
                    break;

                case "AS": // Ascent to next BT or Surface
                    nextSegmentType = MyFunctionsSegments.findNextSegmentType(mDiveSegmentList,i);
                    if (nextSegmentType.equals("STO")) {
                        min = (diveSegment.getDepth() - 0)  / ascentRateToSu;
                    } else {
                        depth = MyFunctionsSegments.findNextDepth(mDiveSegmentList,i);
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
                case "DS":  // Deep Stop
                case "SS":  // Safety Stop
                case "STO": // Stop
                case "DD":  // Deep Deco
                case "DEC": // Deco
                    diveSegment.setCalcAverageAta(diveSegment.getCalcAta());
                    break;

                // Going down segments
                case "DE":  // Descent
                    diveSegment.setCalcAverageAta(mMyCalc.getAverageAta(MyFunctionsSegments.findPreviousCalcAta(mDiveSegmentList,i),diveSegment.getCalcAta()));
                    break;

                // Going up segments
                case "ADD": // Ascent to Deep Deco
                case "AD":  // Ascent to Deco
                case "ADS": // Ascent to Deep Stop
                case "ASS": // Ascent to Safety Stop
                case "AS":  // Ascent to Surface
                    diveSegment.setCalcAverageAta(mMyCalc.getAverageAta(MyFunctionsSegments.findNextCalcAta(mDiveSegmentList,i),diveSegment.getCalcAta()));
                    break;
            }

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

            // Set calc_average_depth
            diveSegment.setCalcAverageDepth(mMyCalc.getAverageDepth( (salinity) ? mMyCalc.getSeaWater() : mMyCalc.getFreshWater()
                                                                    ,diveSegment.getCalcAverageAta()
            ));

            sumPressure += diveSegment.getAirConsumptionPressure();

            mDiveSegmentList.set(i,diveSegment);
        }

        // Optimize the only Bottom Time (BT)
        //              ====

        // deltaPressure represents the extra or missing psi for the Bottom Time
        // A deltaPressure of 500 psi represents an extra 250 psi; going forward
        // A deltaPressure of -500 psi represents a missing 250 psi; going forward
        Double deltaPressure = (beginningPressure - myMinPressure - sumPressure);
        Double totalOptimizedBottomTime = MyConstants.ZERO_D;

        // Start optimizing the Bottom Time as per user's request
        switch(optimize) {
            case "Minus": // Minimize the Bottom Time to fit within the dive parameters

                // Minimize the Bottom Time only if there is not enough air capacity to complete the dive
                // After minimizing the Bottom Time, it is still possible that a diver will not have enough air capacity
                if (deltaPressure >= MyConstants.ZERO_D) {
                    break;
                }

                // Optimizing the only Bottom Time (BT)
                //                ====
                diveSegment = mDiveSegmentList.get(iBt);

                // Find the Bottom time that would fit within the dive parameters
                Double timeToSubtract;
                if (minimumTimeBetweenTwoDivers.equals(MyConstants.ZERO_D)) {
                    if (diveStatus.equals(MyConstants.PLAN)) {
                        // In case there is some discrepancies between the SAC and the RMV, calculates the SAC from the RMV
                        timeToSubtract = mMyCalc.getTimePressure(deltaPressure, mMyCalc.getSac(rmv, mMyCalc.getCCF(ratedVolume, ratedPressure)), diveSegment.getCalcAverageAta());
                    } else {
                        // Use the real SAC, calculated by this app on a Real dive!
                        timeToSubtract = mMyCalc.getTimePressure(deltaPressure, sac, diveSegment.getCalcAverageAta());
                    }
                    diveSegment.setMinute(diveSegment.getMinute() + timeToSubtract);
                    totalOptimizedBottomTime += diveSegment.getMinute();
                }

                // Set the air_consumption_pressure
                if (diveStatus.equals(MyConstants.PLAN)) {
                    // In case there is some discrepancies between the SAC and the RMV, calculates the SAC from the RMV
                    diveSegment.setAirConsumptionPressure(mMyCalc.getSac(rmv, mMyCalc.getCCF(ratedVolume, ratedPressure)) * diveSegment.getCalcAverageAta() * diveSegment.getMinute());
                } else {
                    // Use the real SAC, calculated by this app on a Real dive!
                    diveSegment.setAirConsumptionPressure(sac * diveSegment.getCalcAverageAta() * diveSegment.getMinute());
                }

                // Set the air_consumption_volume
                // Not based on the Delta Volume but rather on the formula using the new figures just calculated
                diveSegment.setAirConsumptionVolume(mMyCalc.getVolume(rmv, diveSegment.getCalcAverageAta(), diveSegment.getMinute()));

                mDiveSegmentList.set(iBt,diveSegment);

                break;

            case "Plan": // Original As Is through the previous loop. No Optimizing

                break;

            case "Plus": // Maximize the Bottom Time to fit within the dive parameters

                // Optimize the Bottom Time only if there is room for improvements
                if (deltaPressure <= MyConstants.ZERO_D) {
                    break;
                }

                // Optimizing the only Bottom Time (BT)
                //                ====
                diveSegment = mDiveSegmentList.get(iBt);

                // Set teh air_consumption_pressure
                if (minimumTimeBetweenTwoDivers.equals(MyConstants.ZERO_D)) {
                    diveSegment.setAirConsumptionPressure(diveSegment.getAirConsumptionPressure() + deltaPressure);
                }

                // Find the Bottom time that would fit within the dive parameters
                if (minimumTimeBetweenTwoDivers.equals(MyConstants.ZERO_D)) {
                    if (diveStatus.equals(MyConstants.PLAN)) {
                        // In case there is some discrepancies between the SAC and the RMV, calculates the SAC from the RMV
                        diveSegment.setMinute(mMyCalc.getTimePressure(diveSegment.getAirConsumptionPressure(), mMyCalc.getSac(rmv, mMyCalc.getCCF(ratedVolume, ratedPressure)), diveSegment.getCalcAverageAta()));
                    } else {
                        // Use the real SAC, calculated by this app on a Real dive!
                        diveSegment.setMinute(mMyCalc.getTimePressure(diveSegment.getAirConsumptionPressure(), sac, diveSegment.getCalcAverageAta()));
                    }
                    totalOptimizedBottomTime += diveSegment.getMinute();
                }

                // Set teh air_consumption_volume
                // Not based on the Delta Volume but rather on the formula using the new figures just calculated
                diveSegment.setAirConsumptionVolume(mMyCalc.getVolume(rmv, diveSegment.getCalcAverageAta(), diveSegment.getMinute()));

                mDiveSegmentList.set(iBt,diveSegment);

                break;
        }

        // End optimizing the Bottom Time

        // Calculate the decreasing pressure and volume for all segments
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            // NOTE: Leave as is
            mDiveSegmentList = MyFunctionsSegments.calculateDecreasingPressureVolume(mDiveSegmentList, beginningPressure, beginningVolume);
        } else {
            // NOTE: Leave as is
            mDiveSegmentList = MyFunctionsSegments.calculateDecreasingPressureVolumeMetric(mDiveSegmentList, ratedVolume, beginningPressure, beginningVolume);
        }

        // Update Dive Segments
        for (int i = 0; i< mDiveSegmentList.size(); i++)
        {
            diveSegment = mDiveSegmentList.get(i);
            airDa.updateDiveSegment(diveSegment);
        }

        return totalOptimizedBottomTime; // For a given diver
    }

    public void getDiveForGraphic(
            Long diveNo
            , DiveForGraphic diveForGraphic
    ) {
        AirDA airDa = new AirDA(mContext);

        airDa.open();

        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        airDa.getDiveForGraphic(diveNo, diveForGraphic);

        // Get the phone unit according to the Locale
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
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

    public void getDivesForCompare(DivesForCompare divesForCompare) {
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

    public void generateDive(String optimize, DiveForGraphic diveForGraphic) {
        Double bottomTimeMe = MyConstants.ZERO_D;
        Double bottomTimeMb = MyConstants.ZERO_D;

        // Me
        if (diveForGraphic.getMyDiverNo() != MyConstants.ZERO_L && !diveForGraphic.getMyBeginningVolume().equals(MyConstants.ZERO_D) && !diveForGraphic.getMyRatedPressure().equals(MyConstants.ZERO_D)) {
            bottomTimeMe = insertMySegments(MyConstants.ZERO_D, optimize, diveForGraphic);
        }
        // My Buddy
        if (diveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L && !diveForGraphic.getMyBuddyBeginningVolume().equals(MyConstants.ZERO_D) && !diveForGraphic.getMyBuddyRatedPressure().equals(MyConstants.ZERO_D)) {
            bottomTimeMb = insertMyBuddySegments(MyConstants.ZERO_D, optimize, diveForGraphic);
        }

        // Regenerate the dive segments for the diver that had the highest Bottom Time, with the lowest Bottom Time
        // That diver will end up with a much higher available pressure
        // But buddy's are supposed to dive together after all!
        if (diveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            if (bottomTimeMe < bottomTimeMb) {
                // Regenerate my buddy with a lower bottom time
                insertMyBuddySegments(bottomTimeMe, optimize, diveForGraphic);
            } else {
                // Regenerate me with a lower bottom time
                insertMySegments(bottomTimeMb, optimize, diveForGraphic);
            }
        }
    }

    public void generateDive(String optimize, DivesForCompare divesForCompare, Context context) {
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
        generateDive(optimize, diveForGraphic);

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
        generateDive(optimize, diveForGraphic);

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
        generateDive(optimize, diveForGraphic);
    }

    String getMyMinPressureDefault () {
        return mMyCalc.getMyMinPressureDefault();
    }

    Double insertMySegments(Double forcedBottomTime, String optimize, DiveForGraphic diveForGraphic) {
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
                , optimize
        );
    }

    Double insertMyBuddySegments(Double forcedBottomTime, String optimize, DiveForGraphic diveForGraphic) {
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
                , optimize
        );
    }
}
