package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-11-29.
 * Comparator to sort on DiverPick Dives
 */

public class DiverPickComparatorDives implements Comparator<Diver> {

    // Static
    private static final String LOG_TAG = "DiverPickComparatorDives";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    DiverPickComparatorDives(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(Diver dp1, Diver dp2) {
        return mod*dp1.getDives() - dp2.getDives();
    }
}
