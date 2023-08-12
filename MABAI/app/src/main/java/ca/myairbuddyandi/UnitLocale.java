package ca.myairbuddyandi;

import java.util.Locale;

/**
 * Created by Michel on 2017-07-21.
 * Holds all of the logic for the UnitLocale class
 */

public final class UnitLocale {

    // Static
    private static final String LOG_TAG = "UnitLocale";
    static String getCountryCode() {
        return getCountry(Locale.getDefault());
    }
    static String getLanguageCode() {
        return getLanguage(Locale.getDefault());
    }
    public static UnitLocale Imperial = new UnitLocale();
    public static UnitLocale Metric = new UnitLocale();
    public static UnitLocale getDefault() {
        return getFrom(Locale.getDefault());
    }

    // Public

    // Protected

    // Private

    // End of variables

    // Private constructor
    private static UnitLocale getFrom(Locale locale) {
        String countryCode = locale.getCountry().toUpperCase();
        switch (countryCode) {
            case "US": // USA is in Imperial-USA
            case "CA": // Canada is Metric but all divers are in psi
                // NOTE: For possible future use
//            case "LR": // Liberia is Imperial but all divers are in bar
//            case "MM": // Myanmar is Imperial but all divers are in bar
//            case "GB": // Great Britain is Imperial but all divers are in bar
                return Imperial;
            default:
                return Metric;
        }
    }

    private static String getCountry(Locale locale) {
        return locale.getCountry().toUpperCase();
    }

    private static String getLanguage(Locale locale) {
        return locale.getLanguage().toUpperCase();
    }
}