package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DivePick Dives
 */

public class GrouppPickComparatorDives implements Comparator<GrouppPick> {

    // Static
    private static final String LOG_TAG = "GrouppPickComparatorDives";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public GrouppPickComparatorDives(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(GrouppPick dp1, GrouppPick dp2) {
        return mod*dp1.getDives() - dp2.getDives();
    }
}
