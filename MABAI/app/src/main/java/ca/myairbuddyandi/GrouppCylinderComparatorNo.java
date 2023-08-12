package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort GrouppCylinder No
 */

class GrouppCylinderComparatorNo implements Comparator<GrouppCylinder> {

    // Static
    private static final String LOG_TAG = "GrouppCylinderComparatorNo";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    GrouppCylinderComparatorNo(boolean desc) {
        if (desc) mod =-1;
    }
    @Override
    public int compare(GrouppCylinder dp1, GrouppCylinder dp2) {
        long l = mod*dp1.getGroupNo() - dp2.getGroupNo();
        return (int) l;
    }
}
