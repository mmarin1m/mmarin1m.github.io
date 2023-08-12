package ca.myairbuddyandi;

/**
 * Created by Michel on 2018-11-29.
 * Empty stub but must contained all methods of MyCalcImperial and MyCalcMetric
 */

public class MyCalc {

    // Static
    private static final String LOG_TAG = "MyCalc";

    // Public

    // Protected

    // Private

    // End of variables

    // Public constructor
    public MyCalc() {
    }

    // CALCULATION functions

    // Get the Cylinder Conversion Factor
    public Double getCCF(Double ratedVolume, Double ratedPressure){
        return MyConstants.ZERO_D;
    }

    // Get the pressure from Volume
    public Double getPressure(Double volumeUsed, Double ccf){
        return MyConstants.ZERO_D;
    }

    // Get the pressure per minute at depth
    public Double getPressureMin(Double sac, Double ata, Double time){
        return MyConstants.ZERO_D;
    }

    // NOTE: Reserved for future use
    public Double getPressureMin(Double rmv, Double ccf, Double ata, Double time){
        return MyConstants.ZERO_D;
    }

    // Get the time at depth, based on pressure
    public Double getTimePressure(Double pressure, Double sac, Double ata){
        return MyConstants.ZERO_D;
    }

    // NOTE: Reserved for future use
    public Double getTimePressure2(Double ccf, Double rmv, Double ata){
        return MyConstants.ZERO_D;
    }

    // Get the SAC
    public Double getSac(Double beginningPressure, Double endingPressure, Double time, Double depth, boolean salinity){ return MyConstants.ZERO_D; }

//    // Used by MyCalcImperial
//    public Double getSac(int pressureUsed, double time, Double ata){
//        return MyConstants.ZERO_D;
//    }

    public Double
    getSac(Double rmv, Double ccf){
        // This function is used for dive of status Plan
        // where the RMV has precedence over the SAC
        return MyConstants.ZERO_D;
    }

    // Get the RMV
    public Double getRmv(Double sac, Double ratedVolume, Double ratedPressure){ return MyConstants.ZERO_D; }

    // Get the volume
    public Double getVolume(Double rmv, Double ata, Double minute) {
        return MyConstants.ZERO_D;
    }

    public Double getVolume(Double pressure, Double ccf) { return MyConstants.ZERO_D; }

    // Get the average depth
    public Double getAverageAta(Double ata1, Double ata2) {
        return MyConstants.ZERO_D;
    }

    // Get the  average depth
    public Double getAverageDepth(Double salinity, Double averageAtaBar) { return MyConstants.ZERO_D; }

    // Get the calculated average depth
    public Double getCalcAverageDepth(long diverNo, long diveNo) { return MyConstants.ZERO_D; }

    // Force safetyStopDepth to its minimum depth
    public Double getMinimumSafetyStopDepth(Double safetyStopDepth) {
        return MyConstants.ZERO_D;
    }

    // Get the partial pressure
    public Double getPartialPressure(Double bestMix, Double mod) {
        return bestMix * mod;
    }

    // Get the best mix
    public Double getBestMix(Double partialPressure, Double mod) {
        if (mod.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return partialPressure / mod;
        }
    }

    // Get the MOD
    public Double getMod(Double partialPressure, Double bestMix) {
        if (bestMix.equals(MyConstants.ZERO_D)) {
            return MyConstants.ZERO_D;
        } else {
            return partialPressure / bestMix;
        }
    }

    // Calculate Charles Law

    // Get Charles P1 from Pressure
    public Double getCP1(Double cp2, Double ct1, Double ct2)  {
        return MyConstants.ZERO_D;
    }

    // Get Charles P2 from Pressure
    public Double getCP2(Double cp1, Double ct1, Double ct2)  {
        return MyConstants.ZERO_D;
    }

    // Get Charles T1 from Pressure
    public Double getCPT1(Double cp1, Double cp2, Double ct2)  {
        return MyConstants.ZERO_D;
    }

    // Get Charles T2 from Pressure
    public Double getCPT2(Double cp1, Double cp2, Double ct1)  {
        return MyConstants.ZERO_D;
    }

    // Get Charles V1 from Volume
    public Double getCV1(Double cv2, Double ct1, Double ct2)  {
        return MyConstants.ZERO_D;
    }

    // Get Charles V2 from Volume
    public Double getCV2(Double cv1, Double ct1, Double ct2)  {
        return MyConstants.ZERO_D;
    }

    // Get Charles T1 from Volume
    public Double getCVT1(Double cv1, Double cv2, Double ct2)  {
        return MyConstants.ZERO_D;
    }

    // Get Charles T2 from Volume
    public Double getCVT2(Double cv1, Double cv2, Double ct1)  {
        return MyConstants.ZERO_D;
    }

    // Calculate Pressure

    // Get P1
    public Double getP1(Double v1, Double p2, Double v2) {
        if (v1.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return (p2 * v2) / v1;
        }
    }

    // Get P2
    public Double getP2(Double v2, Double p1, Double v1) {
        if (v2.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return (p1 * v1) / v2;
        }
    }

    // Get V1
    public Double getV1(Double p1, Double p2, Double v2) {
        if (p1.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return (p2 * v2) / p1;
        }
    }

    // Get V2
    public Double getV2(Double p2, Double p1, Double v1) {
        if (p2.equals(MyConstants.ZERO_D)) {
            return 0.0;
        }else {
            return (p1 * v1) / p2;
        }
    }

    // Get the Equivalent Air Depth (EAD)
    public Double getEad(Double n, Double depth, boolean salinity) {
        return MyConstants.ZERO_D;
    }

    // Get the Equivalent Narcotic Depth (END)
    public Double getEnd(Double he, Double depth, boolean salinity) {
        return MyConstants.ZERO_D;
    }

//    public Double getEndN2O2Narc(Double n2O2, Double ataMod, boolean salinity) { return MyConstants.ZERO_D; }

    public Double getEndN2O2HeNarc(Double he, Double ataMod, boolean salinity) { return MyConstants.ZERO_D; }

    // Get the Buoyancy needed to lift an object
    public Double getBuoyancy(Double weight, Double displacement, boolean salinity) { return MyConstants.ZERO_D; }

    // CONVERSION functions

    // Get the bar from the ata
    public Double convertAtaToBar(Double ata){ return ata * MyConstants.ATA_TO_BAR; }

//    // Get the Depth from the number of Atmospheres
//    public Double convertAtaToDepth(Double ata, boolean salinity){ return MyConstants.ZERO_D; }

    // Get the ata from the bar
    public Double convertBarToAta(Double bar){ return bar * MyConstants.BAR_TO_ATA; }

    // Convert pressure into volume
    public Double convertPressureToVolume(Double pressure, Double ccf) { return MyConstants.ZERO_D; }

    // Convert volume into pressure
    public Double convertVolumeToPressure(Double volume, Double ccf) {
        return MyConstants.ZERO_D;
    }

    public Double convertPsiToBar(Double psi) {
        return psi * MyConstants.PSI_TO_BAR;
    }

    public Double convertBarToPsi(Double bar) {
        return bar * MyConstants.BAR_TO_PSI;
    }

    public Double convertFeetToMeter(Double feet) {
        return feet * MyConstants.FT_TO_M;
    }

    public Double convertMeterToFeet(Double meter) {
        return meter * MyConstants.M_TO_FT;
    }

    public Double convertFahrenheitToCelsius(Double fahrenheit) {
        return (fahrenheit - 32) * (5.0 / 9.0);
    }

    public Double convertCelsiusToFahrenheit(Double celsius) {
        return (celsius * (9.0 / 5.0)) + 32;
    }

    public Double convertPoundToKilogram(Double pound) {
        return pound * MyConstants.LB_TO_KG;
    }

    public Double convertKilogramToPound(Double kilogram) {
        return kilogram * MyConstants.KG_TO_LB;
    }

    public Double convertAtaToPressure(Double ata){
        return MyConstants.ZERO_D;
    }

    public Double convertPressureToAta(Double pressure){ return MyConstants.ZERO_D; }

    public Double convertDepthToPressure(Double depth, boolean salinity){
        return MyConstants.ZERO_D;
    }

    public Double convertPressureToDepth(Double pressure, boolean salinity){
        return MyConstants.ZERO_D;
    }

    public Double convertCuftToLiter(Double cuft){
        return cuft * MyConstants.CUFT_TO_LITER;
    }

    public Double convertLiterToCuft(Double liter){
        return liter * MyConstants.LITER_TO_CUFT;
    }

    // Get the different UNITS

    // Get the Unit
    public String getUnit() {
        return " ";
    }

    // Get the Pressure Unit
    public String getPressureUnit() {
        return "";
    }

    // Get the Volume Unit
    public String getVolumeUnit() {
        return "";
    }

    // Get the Rate Unit
    public String getRateUnit() {
        return "";
    }

    // Get the Depth Unit
    public String getDepthUnit() {
        return "";
    }

    // Get the RMV Unit
    public String getRmvUnit() {
        return "";
    }

    // Get the CONSTANTS

    // Get the PADI's default Maximum Depth Allowed
    public Double getDefaultMaxDepthAllowed() {
        return MyConstants.ZERO_D;
    }

    public Double getMaxAltitude() {
        return MyConstants.ZERO_D;
    }

    public Double getMaxAverageDepth() {
        return MyConstants.ZERO_D;
    }

    public Double getMaxBeginningPressure() {
        return MyConstants.ZERO_D;
    }

    public Double getMaxDepthAllowed() {
        return MyConstants.ZERO_D;
    }

    public Double getMaxRatedPressure() {
        return MyConstants.ZERO_D;
    }

    public Double getMinAltitude() {
        return MyConstants.ZERO_D;
    }

    public Double getMinPressure() {
        return MyConstants.ZERO_D;
    }

    public Double getMinRatedPressure() {
        return MyConstants.ZERO_D;
    }

    public Double getFreshWater() { return MyConstants.ZERO_D; }

    public String getFreshWaterUnit() { return "";}

    public Double getSeaWater() {
        return MyConstants.ZERO_D;
    }

    public String getSeaWaterUnit() { return "";}

    public Double getMaxVolume() {
        return MyConstants.ZERO_D;
    }

    public Double getMinVolume() {
        return MyConstants.ZERO_D;
    }

    public Double getMaxRmv() { return MyConstants.ZERO_D;}

    public Double getSacDefault() { return MyConstants.ZERO_D;}

    public Double getRmvDefault() { return MyConstants.ZERO_D;}

    public Double getC() { return MyConstants.ZERO_D;}

    // Get the PREFERENCES default
    public String getBubbleCheckDepthDefault() {return "0";}

    public String getDescentRateDefault() {return "0";}

    public String getAscentRateToDsDefault() {return "0";}

    public String getAscentRateToSsDefault() {return "0";}

    public String getAscentRateToSuDefault() {return "0";}

    public String getDeepStopDiveDefault() {return "0";}

    public String getSafetyStopDiveDefault() {return "0";}

    public String getSafetyStopDepthDefault() {return "0";}

    public String getMyMinPressureDefault() {return "0";}

    public String getOoaTurnaroundTimeDefault() {return "1";}

    public String getRockbottomMinPressureDefault() {return "0";}

    public String getRockbottomSacDefault() {return "0.0";}

    public String getRockbottomRmvDefault() {return "0.0";}

    public String getTurnaroundTimeDefault() {return "1";}

}
