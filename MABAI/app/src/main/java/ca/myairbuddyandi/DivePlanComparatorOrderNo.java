package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DivePlan Order No
 */

public class DivePlanComparatorOrderNo implements Comparator<DivePlan> {

    // Static
    private static final String LOG_TAG = "DivePlanComparatorOrderNo";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public DivePlanComparatorOrderNo(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(DivePlan dp1, DivePlan dp2) {
        long l = mod*dp1.getOrderNo() - dp2.getOrderNo();
        return (int) l;
    }
}
