package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on CylinderPick Rated Pressure
 */

public class CylinderPickComparatorRatedPressure implements Comparator<CylinderPick> {

    // Static
    private static final String LOG_TAG = "CylinderPickComparatorRatedPressure";

    // Public

    // Protected

    // Private
    private Double mod = 1.0;

    // End of variables

    // Public constructor
    public CylinderPickComparatorRatedPressure(boolean desc) {
        if (desc) mod = -1.0;
    }

    @Override
    public int compare(CylinderPick dp1, CylinderPick dp2) {
        double result = mod*dp1.getRatedPressure() - dp2.getRatedPressure();
        return (int) result;
    }
}
