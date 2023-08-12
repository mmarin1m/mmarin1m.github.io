package ca.myairbuddyandi;

import java.util.Comparator;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on Constant Value
 */

public class ConstantComparatorValue implements Comparator<Constant> {

    // Static
    private static final String LOG_TAG = "ConstantComparatorValue";

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public ConstantComparatorValue(boolean desc) {
        if (desc) mod =-1;
    }

    @Override
    public int compare(Constant dp1, Constant dp2) {
        double result = mod*dp1.getValue() - dp2.getValue();
        return (int) result;
    }
}
