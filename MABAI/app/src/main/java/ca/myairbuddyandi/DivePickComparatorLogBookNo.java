package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DivePick Log Book No
 */

public class DivePickComparatorLogBookNo implements Comparator<DivePick> {

    // Static
    private static final String LOG_TAG = "DivePickComparatorLogBookNo";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public DivePickComparatorLogBookNo(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(DivePick dp1, DivePick dp2) {
        return mod*dp1.getLogBookNo() - dp2.getLogBookNo();
    }
}
