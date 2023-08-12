package ca.myairbuddyandi;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Michel on 2017-08-07.
 * Comparator to sort on DiveType Type
 */

public class DiveTypeComparatorType implements Comparator<DiveType> {

    // Static
    private static final String LOG_TAG = "DiveTypeComparatorType";
    private static final Pattern PATTERN = Pattern.compile("(\\D*)(\\d*)");

    // Public

    // Protected

    // Private
    private int mod = 1;

    // End of variables

    // Public constructor
    public DiveTypeComparatorType(boolean desc) {
        if (desc) mod =-1;
    }

    public int compare(DiveType c1, DiveType c2) {
        Matcher matcher1 = PATTERN.matcher(c1.getDiveType());
        Matcher matcher2 = PATTERN.matcher(c2.getDiveType());
        // The only way find() could fail is at the end of a string
        while (matcher1.find() && matcher2.find()) {
            //non digit comparison
            int nonDigitCompare = Objects.requireNonNull(matcher1.group(1)).compareTo(Objects.requireNonNull(matcher2.group(1)));
            if (0 != nonDigitCompare) {
                return nonDigitCompare * mod;
            }
            // digit comparison
            if (Objects.requireNonNull(matcher1.group(2)).isEmpty()) {
                return (Objects.requireNonNull(matcher2.group(2)).isEmpty() ? 0 : -1) * mod;
            } else if (Objects.requireNonNull(matcher2.group(2)).isEmpty()) {
                return mod;
            }
            BigInteger number1 = new BigInteger(Objects.requireNonNull(matcher1.group(2)));
            BigInteger number2 = new BigInteger(Objects.requireNonNull(matcher2.group(2)));
            int numberCompare = number1.compareTo(number2);
            if (0 != numberCompare) {
                return (numberCompare * mod);
            }
        }
        // Handle if one string is a prefix of the other.
        return matcher1.hitEnd() && matcher2.hitEnd() ? 0 :
                matcher1.hitEnd()                ? -1 : +1;
    }
}
