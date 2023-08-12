package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DivePick Dives
 */

public class DiveTypeComparatorDives implements Comparator<DiveType> {

    // Static
    private static final String LOG_TAG = "DiveTypeComparatorDives";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public DiveTypeComparatorDives(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(DiveType dp1, DiveType dp2) {
        return mod*dp1.getDives() - dp2.getDives();
    }
}
