package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort GrouppCylinder Volume
 */

public class GrouppCylinderComparatorVolume implements Comparator<GrouppCylinder> {

    // Static
    private static final String LOG_TAG = "GrouppCylinderComparatorVolume";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public GrouppCylinderComparatorVolume(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(GrouppCylinder dp1, GrouppCylinder dp2) {
        double d = mod*dp1.getVolume() - dp2.getVolume();
        return (int) d;
    }
}
