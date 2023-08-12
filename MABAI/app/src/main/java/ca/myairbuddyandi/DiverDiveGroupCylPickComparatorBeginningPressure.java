package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DiverDiveGroupCyl Rated Pressure
 */

public class DiverDiveGroupCylPickComparatorBeginningPressure implements Comparator<DiverDiveGroupCyl> {

    // Static
    private static final String LOG_TAG = "DiverDiveGroupCylPickComparatorBeginningPressure";

    // Public

    // Protected

    // Private
    private Double mod = 1.0;

    // End of variables

    // Public constructor
    public DiverDiveGroupCylPickComparatorBeginningPressure(boolean desc) {
        if (desc) mod = -1.0;
    }

    @Override
    public int compare(DiverDiveGroupCyl dp1, DiverDiveGroupCyl dp2) {
        double result = mod*dp1.getBeginningPressure() - dp2.getBeginningPressure();
        return (int) result;
    }
}
