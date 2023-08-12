package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on CylinderPick No
 */

public class CylinderPickComparatorNo implements Comparator<CylinderPick> {

    // Static
    private static final String LOG_TAG = "CylinderPickComparatorNo";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public CylinderPickComparatorNo(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(CylinderPick dp1, CylinderPick dp2) {
        long l = mod*dp1.getCylinderNo() - dp2.getCylinderNo();
        return (int) l;
    }
}
