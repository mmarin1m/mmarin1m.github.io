package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort GrouppCylinder Usage
 */

public class GrouppCylinderComparatorUsage implements Comparator<GrouppCylinder> {

    // Static
    private static final String LOG_TAG = "GrouppCylinderComparatorUsage";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public GrouppCylinderComparatorUsage(boolean desc) {
        if (desc) mod = -1;
    }

    @Override
    public int compare(GrouppCylinder dp1, GrouppCylinder dp2) {
        if (dp1.getUsageDescription().compareToIgnoreCase(dp2.getUsageDescription()) == MyConstants.ONE_I) {
            return mod;
        } else if (dp1.getUsageDescription().compareToIgnoreCase(dp2.getUsageDescription()) == MyConstants.ZERO_I) {
            return 0;
        } else {
            return mod*-1;
        }
    }
}
