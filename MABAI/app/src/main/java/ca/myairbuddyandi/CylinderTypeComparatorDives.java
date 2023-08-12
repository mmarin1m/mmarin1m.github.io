package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DivePick Dives
 */

public class CylinderTypeComparatorDives implements Comparator<CylinderType> {

    // Static
    private static final String LOG_TAG = "CylinderTypeComparatorDives";

    // Public

    // Protect

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public CylinderTypeComparatorDives(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(CylinderType dp1, CylinderType dp2) {
        return mod*dp1.getDives() - dp2.getDives();
    }
}
