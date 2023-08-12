package ca.myairbuddyandi;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Michel on 2017-01-08.
 * Holds all of the logic for the MyCalcImperial class
 */

public class MyCalcImperial extends MyCalc {

    // Static
    private static final String LOG_TAG = "MyCalcImperial";

    // Public

    // Protected

    // Private
    private final Context mContext;

    // End of variables

    // Public constructor
    public MyCalcImperial(Context context) {
        mContext = context;
    }

    // CALCULATION functions

    // Get the number of Atmospheres from the Depth
    public Double getAta(Double depth, boolean salinity){
       Double saltFresh = (salinity ? getSeaWater() : getFreshWater());
       return (depth / saltFresh) + 1;
    }

    // Get the Cylinder Conversion Factor
    public Double getCCF(Double ratedVolume, Double ratedPressure){
        if (ratedPressure.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return ratedVolume / ratedPressure;
        }
    }

    // Get the pressure (psi) from Volume
    public Double getPressure(Double volumeUsed, Double ccf){
        if (ccf.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return volumeUsed / ccf;
        }
    }

    // Get the pressure (psi) per minute at depth
    public Double getPressureMin(Double sac, Double ata, Double time){
        return sac * ata * time;
    }

    public Double getPressureMin(Double rmv, Double ccf, Double ata, Double time){
        if (ccf.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return (rmv / ccf) * ata * time;
        }
    }

    // Get the time at depth, based on pressure
    public Double getTimePressure(Double pressure, Double sac, Double ata){
        if ((sac * ata) == MyConstants.ZERO_D) {
            return MyConstants.ZERO_D;
        } else {
            return pressure / (sac * ata);
        }
    }

    public Double getTimePressure2(Double ccf, Double rmv, Double ata){
        if ((rmv * ata) == MyConstants.ZERO_D) {
            return MyConstants.ZERO_D;
        } else {
            return ccf / (rmv * ata);
        }
    }

    // Get the SAC
    public Double getSac(Double beginningPressure, Double endingPressure, Double time, Double depth, boolean salinity){
        if (time == MyConstants.ZERO_I) {
            return MyConstants.ZERO_D;
        } else {
            return ((beginningPressure - endingPressure) / time) / getAta(depth, salinity);
        }
    }

    public Double getSac(Double pressureUsed, Double time, Double ata){
        if (time == MyConstants.ZERO_I) {
            return MyConstants.ZERO_D;
        } else if (pressureUsed / time == MyConstants.ZERO_D) {
            return MyConstants.ZERO_D;
        } else if (ata.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return (pressureUsed / time) / ata;
        }
    }

    public Double getSac(Double rmv, Double ccf) {
        // This function is used for dive of status Plan
        // where the RMV has precedence over the SAC
        // ratedVolume not used in Imperial
        if (ccf.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return rmv / ccf;
        }
    }

    // Get the RMV
    public Double getRmv(Double sac, Double ratedVolume, Double ratedPressure){
        return sac * getCCF(ratedVolume, ratedPressure);
    }

    // Get the volume
    public Double getVolume(Double rmv, Double ata, Double minute) {
        return rmv * minute * ata;
    }

    public Double getVolume(Double pressure, Double ccf) {
        return pressure * ccf;
    }

    // Get the average depth
    public Double getAverageAta(Double ata1, Double ata2) {
        // Going down from 2 to 4 ATA
        // ata1: 4
        // ata2: 2
        // Going up from 4 to 2 ATA
        // ata1: 4
        // ata2: 2
        // No negative ATA is allowed
        return (ata1 + ata2 ) / 2;
    }

    // Get the  average depth
    public Double getAverageDepth(Double salinity, Double averageAta) {
        // The call is under the form of (salinity) ? mMyCalc.getSeaWater() : mMyCalc.getFreshWater()
        return (averageAta - 1) * salinity;
    }

    // Get the calculated average depth
    public Double getCalcAverageDepth(long diverNo, long diveNo) {

        ArrayList<CalcAverageDepth> calcAverageDepthList = new ArrayList<>();

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int descentRate = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DESCENT_RATE, getDescentRateDefault()))));

        // Always in that order but not always present

        //  0 STA Start x
        //  1 DE  Descent x
        //  2 BC  Bubble check x
        //  3 DE  Descent x
        //  4 BT  Bottom Time. Always present. 5 minutes in at 100 feet x
        //  5 TA  Turnaround. Always present. 1 minute at 100 feet x
        //  6 BT  Bottom Time. Always present. 5 minutes out at 100 feet
        //  7 OOA Out Of Air Situation. 1 minutes at 100 feet x
        //  8 AS  Ascend to second deepest Bottom Time. If present
        //  9 BT  Second deepest Bottom Time. If present. 10 minutes at 90 feet
        // 10 AS  Ascend to third deepest Bottom Time. If present
        // 11 BT  Third deepest Bottom Time. If present. 15 minutes at 80 feet
        //  ...
        // 12 ADS Ascend to Deep Stop.   If present. Could occurs deeper than a second or third Bottom Time
        // 13 DS  Deep Stop. If present. If present. Could occurs deeper than a second or third Bottom Time
        // 14 ASS Ascent to Safety Stop. If present
        // 15 SS  Safety Stop. If present
        // 16 ASU  Ascent to Surface
        // 17 STO Stop

        //Get data for the DivePlans
        AirDA airDa = new AirDA(mContext);

        airDa.open();
        ArrayList<DiveSegment> diveSegmentList = airDa.getAllDiveSegments(diverNo, diveNo);

        boolean firstBottomTime = true;
        double distance;
        double distancePerRefreshRate;
        double startDepth;
        double time;
        DiveSegment diveSegment;
        DiveSegment lastDiveSegment;

        for (int i = 0; i < diveSegmentList.size(); i++) {
            diveSegment = diveSegmentList.get(i);

            switch (diveSegment.getSegmentType()) {
                case "STA": // Start
                case "STO": // Stop
                case "DE": // Descent
                    continue;

                case "BC": // Bubble Check
                    // Generate average depth for the Descent
                    // Previous depth is 0
                    // Example: From 0 to 20 feet
                    //          Find the time needed to swim down
                    //          Find the distance swam for every 2000 milli seconds (refresh rate)
                    distance = diveSegment.getDepth() - 0;
                    time = distance * 60 / descentRate;
                    distancePerRefreshRate = distance * (MyConstants.REFRESH_RATE / 1000) / time;
                    insertCalcAverageDepth(  (int)Math.round(time / (MyConstants.REFRESH_RATE / 1000))
                                           , distancePerRefreshRate
                                           , distancePerRefreshRate
                                           , calcAverageDepthList
                                          );

                    // Generate average depth for the Bubble Check
                    // Example: 1 minute @ 20 feet
                    insertCalcAverageDepth(  (int)Math.round(diveSegment.getMinute() * 60 / (MyConstants.REFRESH_RATE / 1000))
                                           , diveSegment.getDepth()
                                           , 0.0
                                           , calcAverageDepthList
                                          );
                    break;
                case "BT": // Bottom Time
                    lastDiveSegment = diveSegmentList.get(i - 2);
                    if (firstBottomTime) {
                        // Generate average depth for the Descent
                        // Check if the last stop was the Bubble Check
                        if (lastDiveSegment.getSegmentType().equals("BC")) {
                            // Generate an Average Depth from the Depth of the Bubble Check to the Maximum Depth
                            // Example: From 20 to 100 feet
                            distance = diveSegment.getDepth() - lastDiveSegment.getDepth();
                            startDepth = lastDiveSegment.getDepth();
                        } else {
                            // Generate an Average Depth from the surface to the Maximum Depth
                            // Example: From 0 to 100 feet
                            distance = diveSegment.getDepth();
                            startDepth = 0.0;
                        }

                        //          Find the time needed to swim down
                        //          Find the distance swam for every 2000 milli seconds (refresh rate)
                        time = distance * 60 / descentRate;
                        distancePerRefreshRate = distance * (MyConstants.REFRESH_RATE / 1000) / time;
                        insertCalcAverageDepth(  (int) Math.round(time / (MyConstants.REFRESH_RATE / 1000))
                                               , startDepth
                                               , distancePerRefreshRate
                                               , calcAverageDepthList
                                              );
                        firstBottomTime = false;
                    }

                    // Generate an Average Depth for the Bottom Time
                    // Example: 5 minutes @ 100 feet
                    insertCalcAverageDepth(  (int)Math.round(diveSegment.getMinute() * 60 / 2)
                            , diveSegment.getDepth()
                            , 0.0
                            , calcAverageDepthList
                    );
                    break;

                case "AS": // Ascent to next shallower Bottom Time or Ascent to Surface
                case "ADS" : // Ascent to Deep Stop
                case "ASS" : // Ascent to Safety Stop
                    // Get the next shallower Depth
                    lastDiveSegment = diveSegmentList.get(i + 1);

                    // Generate an Average Depth from the Depth of a Bottom Time, Deep Stop or Safety Stop
                    // to the next shallower depth
                    // Example: From 100 to 15 feet
                    distance = diveSegment.getDepth() - lastDiveSegment.getDepth();
                    //          Find the time needed to swim up
                    //          Find the distance swam for every 2000 milli seconds (refresh rate)
                    time = distance * 60 / diveSegment.getCalcAscentRate();
                    distancePerRefreshRate = distance * (MyConstants.REFRESH_RATE / 1000) / time;
                    insertCalcAverageDepth(  (int)Math.round(time / (MyConstants.REFRESH_RATE / 1000))
                            , diveSegment.getDepth()
                            , (distancePerRefreshRate * -1)
                            , calcAverageDepthList
                    );

                    break;

                case "TA": // Turnaround
                case "DS": // Deep Stop
                case "SS": // Safety Stop
                case "OOA" : // Our Of Air Situation
                    // Generate average depth for the Deep Stop
                    // Example: 1 minute @ 50 feet
                    // Generate average depth for the Safety Stop
                    // Example: 3 minutes @ 15 feet
                    insertCalcAverageDepth(  (int)Math.round(diveSegment.getMinute() * 60 / 2)
                                           , diveSegment.getDepth()
                                           , 0.0
                                           , calcAverageDepthList
                                          );
                    break;
            }
        }

        Double sumDepth = MyConstants.ZERO_D;

        for (int i = 0; i < calcAverageDepthList.size(); i++) {
            CalcAverageDepth calcAverageDepth;
            calcAverageDepth = calcAverageDepthList.get(i);
            sumDepth += calcAverageDepth.getDepth();
        }
        if (calcAverageDepthList.size() > 0) {
            return MyFunctions.roundUp(sumDepth / calcAverageDepthList.size(), 1);
        } else {
            return 0.0;
        }
    }

    private void insertCalcAverageDepth(int occurrence, double startDepth, double increaseDepth, ArrayList<CalcAverageDepth> calcAverageDepthList) {
        double segmentDepth = startDepth;
        for (int i = 1; i <= occurrence; i++) {
            CalcAverageDepth calcAverageDepth = new CalcAverageDepth();
            calcAverageDepth.setMilliSecond(calcAverageDepth.getMilliSecond() + MyConstants.REFRESH_RATE);
            calcAverageDepth.setDepth(segmentDepth);
            if (increaseDepth != 0.0) {
                segmentDepth += increaseDepth;
            }
            calcAverageDepthList.add(calcAverageDepth);
        }
    }

    // Get safetyStopDepth
    public Double getMinimumSafetyStopDepth(Double safetyStopDepth) {
        // Force safetyStopDepth to its minimum depth
        if (safetyStopDepth < 15.0) {
            return 15.0;
        } else {
            return safetyStopDepth;
        }
    }

    public Double getEabd(Double bestMixO2, Double bestMixHe, Double ataMod, boolean salinity) {
        Double saltFresh = (salinity ? getSeaWater() : getFreshWater());
        Double bestMixN2 = 1.0 - bestMixO2 - bestMixHe;
        double weight = (bestMixO2 * MyConstants.MOLECULAR_WEIGHT_O2) + (bestMixHe * MyConstants.MOLECULAR_WEIGHT_HE) + (bestMixN2 * MyConstants.MOLECULAR_WEIGHT_N2);
        weight = weight * ataMod;
        double equivalentPressure = weight / MyConstants.MOLECULAR_WEIGHT_AIR;
        return (equivalentPressure - 1) * saltFresh;
    }

    // Get the FO2
    public Double getFO2(Double partialPressure, Double ataMod) {
        if (ataMod.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return (partialPressure / ataMod);
        }
    }

    // Get the NO2
    public Double getFN2(Double ataEnd, Double ataMod) {
        if (ataMod.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return ((ataEnd * 0.791) / ataMod);
        }
    }

    // Calculate Charles Law

    // Get Charles P1 from Pressure
    public Double getCP1(Double cp2, Double ct1, Double ct2) {
        if (ct1.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return (ct1 + MyConstants.RANKINE) * cp2 / (ct2 + MyConstants.RANKINE);
        }
    }

    // Get Charles P2 from Pressure
    public Double getCP2(Double cp1, Double ct1, Double ct2) {
        if (ct1.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return (ct2 + MyConstants.RANKINE) * cp1 / (ct1+ MyConstants.RANKINE);
        }
    }

    // Get Charles T1 from Pressure
    public Double getCPT1(Double cp1, Double cp2, Double ct2) {
        if (cp2.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return ((ct2 + MyConstants.RANKINE) * cp1 / cp2) - MyConstants.RANKINE;
        }
    }

    // Get Charles T2 from Pressure
    public Double getCPT2(Double cp1, Double cp2, Double ct1) {
        if (cp1.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return ((ct1 + MyConstants.RANKINE) * cp2 / cp1) - MyConstants.RANKINE;
        }
    }

    // Get Charles V1 from Volume
    public Double getCV1(Double cv2, Double ct1, Double ct2) {
        if (ct2.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return (ct1 + MyConstants.RANKINE) * cv2 / (ct2 + MyConstants.RANKINE);
        }
    }

    // Get Charles V2 from Volume
    public Double getCV2(Double cv1, Double ct1, Double ct2) {
        if (ct1.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return (ct2 + MyConstants.RANKINE) * cv1 / (ct1 + MyConstants.RANKINE);
        }
    }

    // Get Charles T1 from Volume
    public Double getCVT1(Double cv1, Double cv2, Double ct2) {
        if (cv2.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return ((ct2 + MyConstants.RANKINE) * cv1 / cv2) - MyConstants.RANKINE;
        }
    }

    // Get Charles T2 from Volume
    public Double getCVT2(Double cv1, Double cv2, Double ct1) {
        if (cv1.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return ((ct1 + MyConstants.RANKINE) * cv2 / cv1) - MyConstants.RANKINE;
        }
    }

    // Get the Equivalent Air Depth (EAD)
    public Double getEad(Double n, Double depth, boolean salinity) {
        Double saltFresh = (salinity ? getSeaWater() : getFreshWater());
        return ((n * (depth + saltFresh)) / 79.1) - saltFresh;
    }

    // Get the Equivalent Narcotic Depth (END)
    public Double getEnd(Double he, Double depth, boolean salinity) {
        Double saltFresh = (salinity ? getSeaWater() : getFreshWater());
        return ((depth + saltFresh) * (1-(he /100))) - saltFresh;
    }

    public Double getEndN2O2HeNarc(Double he, Double ataMod, boolean salinity) {
        Double saltFresh = (salinity ? getSeaWater() : getFreshWater());
        return (0.235 * (he / 100) * ataMod * saltFresh) - saltFresh;
    }

    // Get the Buoyancy needed to lift an object
    public Double getBuoyancy(Double weight, Double displacement, boolean salinity) {
        Double saltFresh = (salinity ? MyConstants.WEIGHT_SEA_WATER_I :  MyConstants.WEIGHT_FRESH_WATER_I);
        return (weight - (displacement * saltFresh)) / saltFresh;
    }

    public Double getWeight( Double displacement, boolean salinity) {
        Double saltFresh = (salinity ? MyConstants.WEIGHT_SEA_WATER_I :  MyConstants.WEIGHT_FRESH_WATER_I);
        return displacement * saltFresh;
    }

    public Double getImperialKnot(Double distance, Double time) {
        // Calculate Knot
        if (time.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return distance / time  / MyConstants.KNOT_IMPERIAL;
        }
    }

    // CONVERSION functions

    // Include functions that converts To the metric system. e.g. xxxToAta

    // Get the Depth (ft) from the number of Atmospheres
    public Double convertAtaToDepth(Double ata, boolean salinity){
        Double saltFresh = (salinity ? getSeaWater() : getFreshWater());
        return (ata - 1) * saltFresh;
    }

    // Get the Pressure (psi) from the ata
    public Double convertAtaToPressure(Double ata){
        return ata * MyConstants.ATA_TO_PSI;
    }

    // Get the ata from the bar
    public Double convertBarToAta(Double bar){
        return bar * MyConstants.BAR_TO_ATA;
    }

    // Get the pressure (psi) from depth (ft)
    public Double convertDepthToPressure(Double depth, boolean salinity){
        Double saltFresh = (salinity ? MyConstants.PSI_TO_FSW : MyConstants.PSI_TO_FFW);
        if (depth.equals(MyConstants.ZERO_D)) {
            return MyConstants.ATA_TO_PSI;
        } else  {
            return (depth * saltFresh) + MyConstants.ATA_TO_PSI;
        }
    }

    // Convert pressure (psi) to ata
    public Double convertPressureToAta(Double pressure){
        return pressure / MyConstants.ATA_TO_PSI;
    }

    // Convert pressure (psi) to depth (ft)
    public Double convertPressureToDepth(Double pressure, boolean salinity){
        Double saltFresh = (salinity ? getSeaWater() : getFreshWater());
        if (salinity) {
            return (pressure / MyConstants.PSI_TO_FSW) - saltFresh;
        } else {
            return (pressure / MyConstants.PSI_TO_FFW) - saltFresh;
        }
    }

    // Convert pressure (psi) to volume (ft3)
    public Double convertPressureToVolume(Double pressure, Double ccf) {
        return pressure * ccf;
    }

    // Convert volume (ft3) to pressure (psi)
    public Double convertVolumeToPressure(Double volume, Double ccf) {
        if (ccf.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return volume / ccf;
        }
    }

    // Convert Knot to Miles per hour
    public Double convertKnotToMph(Double knot) {
        return knot * MyConstants.KNOT_TO_MPH;
    }

    // Convert Metric cylinder volume (l) to Imperial volume (ft3)
    public Double convertCylinderMetricVolume(Double ratedVolume, Double otherRatedPressure) {
        if (otherRatedPressure.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            double ft3 = convertLiterToCuft(ratedVolume);
            double atm = otherRatedPressure / MyConstants.ATA_TO_PSI;
            return atm * ft3;
        }
    }

    // Get the different UNITS

    // Get the Unit
    public String getUnit() {
        return "I";
    }

    // Get the Pressure Unit
    public String getPressureUnit() {
        return mContext.getResources().getString(R.string.lbl_imperial_pressure_unit);
    }

    // Get the Volume Unit
    public String getVolumeUnit() {
        return mContext.getResources().getString(R.string.lbl_imperial_volume_unit);
    }

    // Get the Rate Unit
    public String getRateUnit() {
        return mContext.getResources().getString(R.string.lbl_imperial_speed_min_unit);
    }

    // Get the Depth Unit
    public String getDepthUnit() {
        return mContext.getResources().getString(R.string.lbl_imperial_depth_unit);
    }

    // Get the RMV Unit
    public String getRmvUnit() {
        return "cf/min";
    }

    // Get the CONSTANTS

    // Get the PADI's default Maximum Depth Allowed
    public Double getDefaultMaxDepthAllowed() {
        return 60.0;
    }

    public Double getMaxAltitude() {
        return 20938.0;
    }

    public Double getMaxAverageDepth() {
        return 328.0;
    }

    public Double getMaxBeginningPressure() {
        return 4350.0;
    }

    public Double getMaxDepthAllowed() {
        return 328.0;
    }

    public Double getMaxRatedPressure() {
        return 4350.0;
    }

    public Double getMinAltitude() {
        return -1355.0;
    }

    public Double getMinPressure() {
        return 500.0;
    }

    public Double getMinRatedPressure() {
        return 500.0;
    }

    public Double getFreshWater() {
        return 34.0;
    }

    public String getFreshWaterUnit() { return mContext.getString(R.string.lbl_imperial_fw_unit);}

    public Double getSeaWater() { return 33.0; }

    public String getSeaWaterUnit() { return mContext.getString(R.string.lbl_imperial_sw_unit);}

    public Double getMaxVolume() { return 149.0; }

    public Double getMinVolume() { return 1.0; }

    public Double getMaxRmv() { return 5.9; }

    public Double getSacDefault() { return 35.0;}

    public Double getRmvDefault() { return 1.0;}

    public Double getC() { return MyConstants.C_IMPERIAL;}

    // Get the PREFERENCES default

    public String getBubbleCheckDepthDefault() {return "20";}

    public String getDescentRateDefault() {return "30";}

    public String getAscentRateToDsDefault() {return "30";}

    public String getAscentRateToSsDefault() {return "30";}

    public String getAscentRateToSuDefault() {return "10";}

    // Set to zero because not calculating deep dive stop by default
    public String getDeepStopDiveDefault() {return "0";}

    public String getSafetyStopDiveDefault() {return "20";}

    public String getSafetyStopDepthDefault() {return "15";}

    public String getMyMinPressureDefault() {return "500";}

    public String getRockbottomMinPressureDefault() {return "200";}

    public String getRockbottomSacDefault() {return "35.0";}

    public String getRockbottomRmvDefault() {return "1.0";}
}
