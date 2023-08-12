package ca.myairbuddyandi;

import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by Michel on 2017-09-25.
 * Holds all of the logic for the MyFunctionSegments class
 */

public final class MyFunctionsSegments {

    // Static
    private static final String LOG_TAG = "MyFunctionsSegments";

    // Public

    // Protected

    // Private

    // End of variables

    // Private constructor
    private MyFunctionsSegments() {
    }

    // My functions

    public static ArrayList<DiveSegment> calculateDecreasingPressureVolume(ArrayList<DiveSegment> diveSegmentList, Double beginningPressure, Double beginningVolume){
        Double startingPressure = beginningPressure;
        Double startingVolume = beginningVolume;
        DiveSegment diveSegment;

        for (int i=0;i<diveSegmentList.size();i++)
        {
            diveSegment = diveSegmentList.get(i);

            // Set calc_decreasing_pressure
            diveSegment.setCalcDecreasingPressure(MyFunctions.roundUp(startingPressure,1));
            startingPressure -= diveSegment.getAirConsumptionPressure();

            // Set calc_decreasing_volume
            diveSegment.setCalcDecreasingVolume(MyFunctions.roundUp(startingVolume,1));
            startingVolume -= diveSegment.getAirConsumptionVolume();

            diveSegmentList.set(i,diveSegment);
        }
        return diveSegmentList;
    }

    public static ArrayList<DiveSegment> calculateDecreasingPressureVolumeMetric(ArrayList<DiveSegment> diveSegmentList
            , Double ratedVolume
            , Double beginningPressure
            , Double beginningVolume){

        Double startingPressure = beginningPressure;
        Double startingVolume = beginningVolume;
        DiveSegment diveSegment;

        for (int i=0;i<diveSegmentList.size();i++)
        {
            diveSegment = diveSegmentList.get(i);

            // Set calc_decreasing_pressure
            diveSegment.setCalcDecreasingPressure(MyFunctions.roundUp(startingPressure,1));
            startingPressure -= MyFunctions.roundUp(diveSegment.getAirConsumptionPressure() / ratedVolume, 1);

            // Set calc_decreasing_volume
            diveSegment.setCalcDecreasingVolume(MyFunctions.roundUp(startingVolume,1));
            startingVolume -= diveSegment.getAirConsumptionVolume();

            diveSegmentList.set(i,diveSegment);
        }
        return diveSegmentList;
    }

    public static int findTurnaroundSegment(ArrayList<DiveSegment> diveSegmentList) {
        for (int i=0;i<diveSegmentList.size();i++)
        {
            DiveSegment diveSegment = diveSegmentList.get(i);
            if (diveSegment.getSegmentType().equals("TA")) {
                return i;
            }
        }
        return -1;
    }

    public static Double findPreviousDepth(ArrayList<DiveSegment> diveSegmentList, int i) {
        DiveSegment diveSegment = diveSegmentList.get(i - 1);
        return diveSegment.getDepth();
    }

    public static Double findNextDepth(ArrayList<DiveSegment> diveSegmentList, int i) {
        DiveSegment diveSegment = diveSegmentList.get(i + 1);
        return diveSegment.getDepth();
    }

    public static String findNextSegmentType(ArrayList<DiveSegment> diveSegmentList, int i) {
        DiveSegment diveSegment = diveSegmentList.get(i + 1);
        return diveSegment.getSegmentType();
    }

    public static Double findPreviousCalcAta(ArrayList<DiveSegment> diveSegmentList, int i) {
        DiveSegment diveSegment = diveSegmentList.get(i - 1);
        return diveSegment.getCalcAta();
    }

    public static Double findNextCalcAta(ArrayList<DiveSegment> diveSegmentList,int i) {
        DiveSegment diveSegment = diveSegmentList.get(i + 1);
        return diveSegment.getCalcAta();
    }

    public static Double getTAPressure(ArrayList<DiveSegment> diveSegmentList,int i) {
        DiveSegment diveSegment = diveSegmentList.get(i);
        return diveSegment.getAirConsumptionPressure();
    }

    public static int findAscentSegment(ArrayList<DiveSegment> diveSegmentList,String ascentSegment) {
        for (int i=0;i<diveSegmentList.size();i++)
        {
            DiveSegment diveSegment = diveSegmentList.get(i);
            if (diveSegment.getSegmentType().equals(ascentSegment)) {
                return i;
            }
        }
        return -1;
    }

    public static int getAscentRateSS(ArrayList<DiveSegment> diveSegmentList,int i) {
        DiveSegment diveSegment = diveSegmentList.get(i);
        return diveSegment.getCalcAscentRate();
    }

    public static float getTextHeight(Paint paint, Rect rect, String text) {
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public static float getTextHeightTaller(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public static float getTextLength(Paint paint, Rect rect, String text) {
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }
}
