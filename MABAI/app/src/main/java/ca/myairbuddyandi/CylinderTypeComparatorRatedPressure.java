package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on CylinderType Rated Pressure
 */

class CylinderTypeComparatorRatedPressure implements Comparator<CylinderType> {

    // Static
    private static final String LOG_TAG = "CylinderTypeComparatorRatedPressure";

    // Public

    // Protect

    // Private
    private Double mod = 1.0;

    // End of variables

    CylinderTypeComparatorRatedPressure(boolean desc) {
        if (desc) mod = -1.0;
    }

    @Override
    public int compare(CylinderType dp1, CylinderType dp2) {
        double result = mod*dp1.getRatedPressure() - dp2.getRatedPressure();
        return (int) result;
    }
}
