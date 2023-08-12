package ca.myairbuddyandi;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Michel on 2017-09-25.
 * Hold all of the logic for the EmergencyManageManageDiveSegment class
 */


public class EmergencyManageDiveSegment {

    // Static
    private static final String LOG_TAG = "EmergencyManageManageDiveSegment";

    // Public

    // Protected

    // Private
    private final Context mContext;
    private MyCalc mMyCalc;

    // End of variables

    // Public constructor
    public EmergencyManageDiveSegment(Context context) {
        mContext = context;
    }

    void generateDiveSegment(
              Long diverNo
            , Long diveNo
            , Boolean salinity
            , Double beginningVolume
            , Double beginningPressure
            , Double ratedVolume
            , String shorten
            , String both) {

        AirDA airDa = new AirDA(mContext);

        airDa.open();

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        MyCalc mMyCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(mContext);
        } else {
            mMyCalc = new MyCalcMetric(mContext);
        }

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
        Double rockbottomSac = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ROCK_BOTTOM_SAC, mMyCalc.getRockbottomSacDefault()))));
        Double rockbottomRmv = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ROCK_BOTTOM_RMV, mMyCalc.getRockbottomRmvDefault()))));
        String subtractDeepStopTime = (preferences.getBoolean(MyConstants.SUBTRACT_DEEP_STOP_TIME,false)) ? MyConstants.YES : MyConstants.NO;
        int turnaroundTime = Integer.parseInt(MyFunctions.replaceEmptyByOne(Objects.requireNonNull(preferences.getString(MyConstants.TURNAROUND_TIME, mMyCalc.getTurnaroundTimeDefault()))));
        int ooaTurnaroundTime = Integer.parseInt(MyFunctions.replaceEmptyByOne(Objects.requireNonNull(preferences.getString(MyConstants.OOA_TURNAROUND_TIME, mMyCalc.getOoaTurnaroundTimeDefault()))));

        // Find the Working (W) SAC and RMV from previous dives
        // Working SAC 29.0 and RMV 0.8 for Me,       if not assume 35.0 and 1.0
        // Working SAC 31.0 and RMV 1.0 for My Buddy, if not assume 35.0 and 1.0
        //             ====         ===                             ====     ===
        //  Total      60.0         1.8                             70.0     2.0
        SacRmvWorking sacRmvWorking = airDa.getWorkingSacRmv(diverNo, diveNo, rockbottomSac, rockbottomRmv);

        // Generate the Dive Segments
        airDa.insertDiveSegmentEmergencyPressure(
                 diverNo
                ,diveNo
                ,(salinity) ? mMyCalc.getSeaWater() : mMyCalc.getFreshWater()
                ,ascentRateToDs
                ,ascentRateToSs
                ,ascentRateToSu
                ,deepStopDive
                ,deepStopPercent
                ,deepStopTime
                ,safetyStopDive
                ,safetyStopDepth
                ,safetyStopTime
                ,turnaroundTime
                ,ooaTurnaroundTime
                ,subtractDeepStopTime
                ,MyConstants.NO
                ,MyConstants.NO
                ,shorten
        );

        // Load the Dive Segments into an Array
        ArrayList<DiveSegment> diveSegmentList;
        diveSegmentList = airDa.getAllDiveSegments(diverNo,diveNo);

        if (diveSegmentList.size() == MyConstants.ZERO_I) {
            Toast.makeText(mContext, "Segments not generated for Diver: " + diverNo + " and " + diveNo, Toast.LENGTH_SHORT).show();
            return;
        }

        // Find the Turnaround (TA) Segment
        DiveSegment diveSegment;

        // Perform calculations
        for (int i=0;i<diveSegmentList.size();i++)
        {
            diveSegment = diveSegmentList.get(i);
            Double depth;
            double min;

            // Set the Minute
            switch(diveSegment.getSegmentType()) {
                case "ADS": // Ascent to Deep Stop
                    depth = MyFunctionsSegments.findNextDepth(diveSegmentList,i);
                    min = (diveSegment.getDepth() - depth)  / ascentRateToDs;
                    diveSegment.setMinute(min);
                    break;

                case "ASS": // Ascent to Safety Stop
                    min = (diveSegment.getDepth() - safetyStopDepth)  / ascentRateToSs;
                    diveSegment.setMinute(min);
                    break;

                case "AS": // Ascent to Surface
                    if (diveSegment.getDepth().equals(safetyStopDepth)) {
                        min = (diveSegment.getDepth() - MyConstants.ZERO_D)  / ascentRateToSu;
                        diveSegment.setMinute(min);
                    } else {
                        min = (diveSegment.getDepth() - safetyStopDepth)  / ascentRateToSs;
                        diveSegment.setMinute(min);
                    }
                    break;
            }

            // Set calc_average_ata
            switch(diveSegment.getSegmentType()) {
                // Same depth segment
                case "STA": // Start
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

            // Set the air_consumption_pressure
            if (both.equals(MyConstants.YES)) {
                diveSegment.setAirConsumptionPressure(sacRmvWorking.getSacBoth() * diveSegment.getCalcAverageAta() * diveSegment.getMinute());
            } else {
                diveSegment.setAirConsumptionPressure(sacRmvWorking.getSacMe() * diveSegment.getCalcAverageAta() * diveSegment.getMinute());
            }

            // Set the air_consumption_volume
            if (both.equals(MyConstants.YES)) {
                diveSegment.setAirConsumptionVolume(sacRmvWorking.getRmvBoth() * diveSegment.getMinute() * diveSegment.getCalcAverageAta());
            } else {
                diveSegment.setAirConsumptionVolume(sacRmvWorking.getRmvMe() * diveSegment.getMinute() * diveSegment.getCalcAverageAta());
            }

            // Set calc_average_depth
            diveSegment.setCalcAverageDepth(mMyCalc.getAverageDepth( (salinity) ? mMyCalc.getSeaWater() : mMyCalc.getFreshWater()
                                                                    ,diveSegment.getCalcAverageAta()
                                                                   ));

            diveSegmentList.set(i,diveSegment);
        }

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
    }

    public void generateDive(String shorten, String both, DiveForGraphic diveForGraphic) {
        // Generate the DIVE_SEGMENT
        AirDA airDa = new AirDA(mContext);

        airDa.open();

        // Me or My Buddy
        // Delete previous Dive Segments
        airDa.deleteDiveSegmentByDiverNoDiveNo(diveForGraphic.getMyDiverNo(), diveForGraphic.getDiveNo());

        if (diveForGraphic.getMyDiverNo() != MyConstants.ZERO_L && !diveForGraphic.getMyBeginningVolume().equals(MyConstants.ZERO_D) && !diveForGraphic.getMyRatedPressure().equals(MyConstants.ZERO_D)) {
            insertMySegments(shorten, both, diveForGraphic);
        }
    }

    public void generateDive(String shorten, String both, DivesForCompare divesForCompare, Context context) {
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
        generateDive(shorten, both, diveForGraphic);

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
        generateDive(shorten, both, diveForGraphic);

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
        generateDive(shorten, both, diveForGraphic);
    }

    public void getDivesForCompareEmergency(DivesForCompare divesForCompare) {
        AirDA airDa = new AirDA(mContext);

        airDa.open();

        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        airDa.getDivesForCompareEmergency(divesForCompare);

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

    public void getDiveForGraphicEmergency(Long diveNo, DiveForGraphic diveForGraphic) {
        AirDA airDa = new AirDA(mContext);

        airDa.open();

        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        airDa.getDiveForGraphicEmergency(diveNo, diveForGraphic);

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

    public void insertMySegments(String shorten, String both, DiveForGraphic diveForGraphic) {
        generateDiveSegment(
                  diveForGraphic.getMyDiverNo()
                , diveForGraphic.getDiveNo()
                , diveForGraphic.getSalinity()
                , diveForGraphic.getMyBeginningVolume()
                , diveForGraphic.getMyBeginningPressure()
                , diveForGraphic.getMyRatedVolume()
                , shorten
                , both
        );
    }
}
