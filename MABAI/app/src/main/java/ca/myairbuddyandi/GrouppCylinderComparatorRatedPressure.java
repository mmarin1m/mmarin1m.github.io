package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort GrouppCylinder Rated Pressure
 */

public class GrouppCylinderComparatorRatedPressure implements Comparator<GrouppCylinder> {
    // Static
    private static final String LOG_TAG = "GrouppCylinderComparatorRatedPressure";

    // Public

    // Protected

    // Private
    private Double mod = 1.0;

    // End of variables

    // Public constructor
    public GrouppCylinderComparatorRatedPressure(boolean desc) {
        if (desc) mod = -1.0;
    }

    @Override
    public int compare(GrouppCylinder dp1, GrouppCylinder dp2) {
        double result = mod*dp1.getRatedPressure() - dp2.getRatedPressure();
        return (int) result;
    }
}
