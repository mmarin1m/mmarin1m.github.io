package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on CylinderType Volume
 */

public class CylinderTypeComparatorVolume implements Comparator<CylinderType> {

    // Static
    private static final String LOG_TAG = "CylinderTypeComparatorVolume";

    // Public

    // Protect

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public CylinderTypeComparatorVolume(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(CylinderType dp1, CylinderType dp2) {
        double d = mod*dp1.getVolume() - dp2.getVolume();
        return (int) d;
    }
}
