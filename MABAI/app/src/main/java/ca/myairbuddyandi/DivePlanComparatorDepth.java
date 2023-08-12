package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DivePlan Depth
 */

public class DivePlanComparatorDepth implements Comparator<DivePlan> {

    // Static
    private static final String LOG_TAG = "DivePlanComparatorDepth";

    // Public

    // Protected

    // Private
    private Double mod = 1.0;

    // End of variables

    // Public constructor
    public DivePlanComparatorDepth(boolean desc) {
        if (desc) mod = -1.0;
    }

    @Override
    public int compare(DivePlan dp1, DivePlan dp2) {
        double result = mod*dp1.getDepth() - dp2.getDepth();
        return (int) result;
    }
}
