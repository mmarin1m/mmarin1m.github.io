package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DivePick My Buddy
 */

public class DivePickComparatorMyBuddy implements Comparator<DivePick> {

    // Static
    private static final String LOG_TAG = "DivePickComparatorMyBuddy";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public DivePickComparatorMyBuddy(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(DivePick dp1, DivePick dp2) {
        if (dp1.getMyBuddyFullName().compareTo(dp2.getMyBuddyFullName()) != MyConstants.ZERO_I) {
            return mod;
        } else if (dp1.getMyBuddyFullName().compareTo(dp2.getMyBuddyFullName()) == MyConstants.ZERO_I) {
            return 0;
        } else {
            return mod*-1;
        }
    }
}
