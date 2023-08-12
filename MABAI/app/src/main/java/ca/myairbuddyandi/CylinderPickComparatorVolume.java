package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on CylinderPick Volume
 */

public class CylinderPickComparatorVolume implements Comparator<CylinderPick> {

    // Static
    private static final String LOG_TAG = "CylinderPickComparatorVolume";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public CylinderPickComparatorVolume(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(CylinderPick dp1, CylinderPick dp2) {
        double d = mod*dp1.getVolume() - dp2.getVolume();
        return (int) d;
    }
}
