package ca.myairbuddyandi;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Insets;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Michel on 2017-01-08.
 * Holds all of the logic for the MyFunctions class
 */

final class MyFunctions {

    // Static
    private static final String LOG_TAG = "MyFunctions";

    // Public

    // Protected

    // Private

    // End of variables

    // Private constructor
    private MyFunctions() {
    }

    // Debug functions
    // DEBUG: Used to discover metrics on a given device
    public static void getMetrics(Activity activity) {
        // DEBUG: Leave as is
        int widthPixels;
        int heightPixels;

        // Version of the SDK running in the ADB or on the phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            widthPixels = windowMetrics.getBounds().width() - insets.left - insets.right;
            heightPixels = windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            widthPixels = displayMetrics.widthPixels; // 1080
            heightPixels = displayMetrics.heightPixels; // 1794
        }
    }

    // DEBUG: Used to discover metrics on a given device
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // DEBUG: Used to discover metrics on a given device
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // DEBUG Used to discover size fo the sp for a given EditText
    public static float getDensity(Context context, EditText editText) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return editText.getTextSize() / scaledDensity;
    }

    // DEBUG Used to discover IP address when using the ADB on the phone
    public static String getIpAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = null;
        try {
            ip = InetAddress.getByAddress(
                    ByteBuffer
                            .allocate(Integer.BYTES)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .putInt(wm.getConnectionInfo().getIpAddress())
                            .array()
            ).getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return ip;
    }

    // Date functions
    public static String getDatePattern(Context context) {
        Format dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return ((SimpleDateFormat) dateFormat).toLocalizedPattern();
    }

    public static Date getTodaysDate() {
        return Calendar.getInstance().getTime();
    }

    public static Date getNow() {
        return Calendar.getInstance().getTime();
    }

    public static Date getBirthDate() {
        // According to https://thedivelab.dan.org/2014/12/17/scuba-diving-participation-in-2014/
        // The most common age starts at 25
        // Get the birth year for 25
        // Month starts at 0
        // January is 0
        // December is 11
        return formatDateDate(getYear(getNow()) - MyConstants.AVERAGE_AGE, 0, 1);
    }

    public static String formatDateString(Context context, int year, int month, int day) {
        return DateFormat.getDateFormat(context).format(formatDateDate(year, month, day));
    }

    public static Date formatDateDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    public static int getYear(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonthOfYear(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // NOTE: Reserved for future use
    public static String convertDateFromLongToString(Context context, Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return DateFormat.getDateFormat(context).format(calendar.getTime());
    }

    public static Date convertDateFromLongToDate(Long date) {
        return new Date(date);
    }

    public static String convertDateFromDateToString(Context context, Date date) { return DateFormat.getDateFormat(context).format(date); }

    public static Long convertDateFromDateToLong(Date date) {
        return date.getTime();
    }

    public static Long convertDateTimeToLong(Date date, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,getYear(date));
        calendar.set(Calendar.MONTH,getMonthOfYear(date));
        calendar.set(Calendar.DAY_OF_MONTH,getDayOfMonth(date));
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        return calendar.getTimeInMillis();
    }

    public static Date convertDateFromStringToDate(Context context, String date) {
        Date dt = null;
        try {
            dt = DateFormat.getDateFormat(context).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    // Time functions
    @SuppressLint("DefaultLocale")
    public static String formatBottomTime(String bottomTime) {
        // 2020/03/25 Optimized to support bottomTime mm:ss
        // Return either a valid Bottom Time 00:00 to 300:00 or empty if it is not a valid Bottom Time

        // Valid format:
        // Minimum:   0:00
        // ...       00:01
        // ...       00:59
        // ...        1:00
        // ...      299:59
        // Maximum: 300:00

        // If minute > 300, set it to empty
        // If second > 59, set it to empty

        String formattedString;

        if (bottomTime.indexOf(':') == -1) {
            // User entered "99" with no :
            bottomTime += ":00";
        }

        if (bottomTime.length() == bottomTime.indexOf(':') + 1 ) {
            // User entered "99:" without any seconds
            bottomTime += "00";
        }

        String[] split = bottomTime.split(":");
        String minute = split[0];
        String second = split[1];

        // Minutes
        if (minute.isEmpty()) {
            return "";
        }

        if (Integer.parseInt(minute) > MyConstants.MAX_BOTTOM_TIME) {
            return "";
        }

        // Seconds
        if (second.isEmpty()) {
            second = "00";
        }

        if (Integer.parseInt(second) > 59) {
            return "";
        }

        formattedString = minute + ":" + String.format("%02d",Integer.parseInt(second));

        if (MyFunctions.convertMmSs(formattedString) > MyConstants.MAX_BOTTOM_TIME) {
            return "";
        }

        return formattedString;
    }

    // NOTE: Reserved for future use
    public static String getTime(Context context) {
        Date date = Calendar.getInstance().getTime();
        return getTimeFromDate(context, date);
    }

    public static String getTimeFromDate(Context context, Date date) {
        String timeFormat;

        if (DateFormat.is24HourFormat(context)) {
            timeFormat = MyConstants.TIME_PATTERN_24;
        } else {
            timeFormat = MyConstants.TIME_PATTERN_12;
        }

        // Get time from date
        SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat,Locale.getDefault());
        return timeFormatter.format(date);
    }

    public static int getHour(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getHour(String time) {
        // Get the Hour out of 11:12
        // Return 11
        String[] split = time.split(":");
        return Integer.parseInt(split[0]);
    }

    // NOTE: Reserved for future use
    public static String getHourString(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
    }

    public static int getMinute(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getMinute(String time) {
        // Get the Minutes out of 11:12
        // Return 12
        String[] split = time.split(":");
        return Integer.parseInt(split[1]);
    }

    // NOTE: Reserved for future use
    public static String getMinuteString(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return String.format(Locale.getDefault(),"%02d",calendar.get(Calendar.MINUTE));
    }

    private static int getSecond(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    public static String convertMinToString(Double minute) {
        int minuteInt = minute.intValue();
        double decimal = minute - minuteInt;
        String seconds;

        if (decimal == 0.0) {
            seconds = "00";
        } else {
            double secondDouble = decimal * 60;
            int secondInt = (int) secondDouble;
            seconds = String.format(Locale.getDefault(),"%02d",secondInt);
        }

        return minuteInt + ":" + seconds;
    }

    public static String convertTimeToString(Context context, Date date, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,getYear(date));
        calendar.set(Calendar.MONTH,getMonthOfYear(date));
        calendar.set(Calendar.DAY_OF_MONTH,getDayOfMonth(date));
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        return getTimeFromDate(context,calendar.getTime());
    }

    public static Long addMinuteToDateTime(Long dateTime, Double minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime);
        calendar.add(Calendar.MINUTE,(int)Math.round(minute));
        return calendar.getTimeInMillis();
    }

    public static Double convertMmSs(String duration) {
        String[] split = duration.split(":");
        double minute = Double.parseDouble(split[0]);
        double second = Double.parseDouble(split[1]);
        return minute + (second / 60.0);
    }

    @SuppressLint("DefaultLocale")
    public static String convertToMmSs(Double duration) {
        Integer minute = (int)Math.abs(duration);
        double secondD = duration - minute;
        Integer secondI = (int)Math.abs(MyFunctions.roundUp(secondD * 60,2));
        return minute + ":" + String.format("%02d",secondI);
    }

    public static Integer getMm(Double duration) {
        return (int)Math.abs(duration);
    }

    public static Integer getSs(Double duration) {
        Integer minute = (int)Math.abs(duration);
        double secondD = duration - minute;
        return (int)Math.abs(MyFunctions.roundUp(secondD * 60,2));
    }

    @SuppressLint("DefaultLocale")
    public static String convertToHhMmSs(Double duration) {
        int minute = (int)Math.abs(duration);
        double secondD = duration - minute;
        Integer secondI = (int)Math.abs(MyFunctions.roundUp(secondD * 60,2));
        double hourD = duration /60;
        int hour = (int) hourD;
        minute = (int)Math.round(duration % 60);
        return hour + ":" + String.format("%02d",minute) + ":" + String.format("%02d",secondI);
    }

    // Datetime functions
    public static String formatDatetimeString(Context context, Date dateTime) {
        return formatDatetimeString(context, getYear(dateTime), getMonthOfYear(dateTime), getDayOfMonth(dateTime), getHour(dateTime), getMinute(dateTime));
    }

    private static String formatDatetimeString(Context context, int year, int month, int day, int hour, int minute) {
        return DateFormat.getDateFormat(context).format(formatDateDatetime(year, month, day, hour, minute))
                + " "
                + DateFormat.getTimeFormat(context).format(formatDateDatetime(year, month, day, hour, minute));
    }

    private static Date formatDateDatetime(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    // NOTE: Reserved for future use
    public static int daysBetween(final long fromTime, final long toTime) {
        int result = 0;

        Calendar c = Calendar.getInstance();

        if (toTime <= fromTime) return result;

        c.setTimeInMillis(toTime);
        final int toYear = c.get(Calendar.YEAR);
        result += c.get(Calendar.DAY_OF_YEAR);

        c.setTimeInMillis(fromTime);
        result -= c.get(Calendar.DAY_OF_YEAR);

        while (c.get(Calendar.YEAR) < toYear) {
            result += c.getActualMaximum(Calendar.DAY_OF_YEAR);
            c.add(Calendar.YEAR, 1);
        }

        return result;
    }

    public static String elapsedBetween(Date fromTime, Date toTime, String daysLabel) {

        if (toTime.getTime() < fromTime.getTime()) {
            // The dive has not finished yet!
            // The divers are still diving!
            toTime = fromTime;
        }

        long milliSecondsDiff = toTime.getTime() - fromTime.getTime();
        long diffInHours = TimeUnit.MILLISECONDS.toHours(milliSecondsDiff);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(milliSecondsDiff);

        int days = (int)Math.abs(diffInHours / 24);
        int hours = (int)Math.abs(diffInHours - (days * 24L));
        int minutes = (int)Math.abs(diffInMinutes - ((long) days * 24 * 60) - (hours * 60L));

        return days + " " + daysLabel + "\n" + hours + ":" + minutes;
    }

    // NOTE: Leave as is
    public static Date addTimeToDate(Date date, int hours, int minutes, int seconds) {

        // NOTE: Refer to addMinuteToDateTime() for another solution
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,getYear(date));
        calendar.set(Calendar.MONTH,getMonthOfYear(date));
        calendar.set(Calendar.DAY_OF_MONTH,getDayOfMonth(date));
        calendar.set(Calendar.HOUR_OF_DAY,getHour(date) + hours);
        calendar.set(Calendar.MINUTE,getMinute(date) + minutes);
        calendar.set(Calendar.SECOND,getSecond(date) + seconds);
        calendar.set(Calendar.MILLISECOND,0);

        return calendar.getTime();
    }

    // byte[] functions

    /**
     * Make a safe copy of a nullable byte array
     *
     * @param source byte array to copy
     * @return non-null copy of the source byte array or an empty array if source was null
     */
    @NotNull
    public static byte[] copyOf(@Nullable final byte[] source) {
        return (source == null) ? new byte[0] : Arrays.copyOf(source, source.length);
    }

    /**
     * Make a byte array nonnull by either returning the original byte array if non-null or an empty bytearray
     *
     * @param source byte array to make nonnull
     * @return the source byte array or an empty array if source was null
     */
    @NotNull
    public static byte[] nonnullOf(@Nullable final byte[] source) {
        return (source == null) ? new byte[0] : source;
    }

    // Double functions
    // Round Double value to n decimal places
    // Behaves has RoundUp
    // NOTE: Reserved for future use
    public static double round(double value, int places) {
        return roundDown(value, places);
    }

    // Round Double value DOWN to n decimal places
    // Scale is 1: 123.456 returns 123.4
    // Scale is 2: 123.456 returns 123.45
    public static double roundDown(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.DOWN);//123.5
        return bd.doubleValue();
    }

    // Round Double value UP to n decimal places
    // Scale is 1: 123.456 returns 123.5
    // Scale is 2: 123.456 returns 123.46
    public static double roundUp(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    // Integer functions
    public static Integer byteArrayToInteger(byte[] c, int offset) {
        //byte[] value = c.getValue();
        int lowerByte = (int) c[offset] & 0xFF;
        int mediumByte = (int) c[offset + 1] & 0xFF;
        int upperByte = (int) c[offset + 2] & 0xFF;
        return (upperByte << 16) + (mediumByte << 8) + lowerByte;
    }

    // String functions
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // NOTE: Reserved for future use
    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    // NOTE: Reserved for future use
    public static String removeFirstString(String str1, String str2) {
        if (str1.contains(str2)) {
            return removeFirstChar(str1, str2.length());
        } else {
            return str1;
        }
    }

    // NOTE: Leave as is
    public static String removeLastString(String str1, String str2) {
        if (str1.lastIndexOf(str2) > MyConstants.ZERO_I) {
            return removeLastChar(str1, str2.length());
        } else {
            return str1;
        }
    }

    private static String removeFirstChar(String str, Integer nbrCharacter) {
        // Get the substring from the beginning plus nbrCharacter until the end of the string
        // Example: " ,A, B" becomes "A, B"
        return str.substring(nbrCharacter);
    }

    private static String removeLastChar(String str, Integer nbrCharacter) {
        // Get the substring from the beginning until the end of the string minus nbrCharacter
        // Example: "A, B, " becomes "A, B"
        return str.substring(0, str.length() - nbrCharacter);
    }

    public static String replaceEmptyByOne(String string) {
        if (string.isEmpty()) {
            return "1";
        } else {
            return string;
        }
    }

    public static String replaceEmptyByZero(String string) {
        if (string.isEmpty()) {
            return "0";
        } else {
            return string;
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static String replaceXmlSpecialChars(String xml)
    {
        xml = xml.replaceAll("&", "&amp;");
        xml = xml.replaceAll("\"<([^<]*)>", "\"&lt;$1&gt;");
        xml = xml.replaceAll("</([^<]*)>\"", "&lt;/$1&gt;\"");
        xml = xml.replaceAll("\"([^<]*)<([^<]*)>([^<]*)\"", "\"$1&lt;$2&gt;$3\"");

        return xml;
    }

    public static boolean validateMacAddress(String macAddress) {
        // Regex to check valid MAC Address
        String regex = "^([0-9A-Fa-f]{2}[:-])"
                + "{5}([0-9A-Fa-f]{2})|"
                + "([0-9a-fA-F]{4}\\."
                + "[0-9a-fA-F]{4}\\."
                + "[0-9a-fA-F]{4})$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(macAddress.trim());

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    // Phone functions
    @SuppressLint("SwitchIntDef")
    public static String getRotation(Context context){
        int rotation;

        Activity activity = (Activity) context;
        rotation = activity.getResources().getConfiguration().orientation;

        switch (rotation) {
            default:
                return "reverse landscape";
            case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                return "landscape";
            case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                return "portrait";
            case android.content.res.Configuration.ORIENTATION_UNDEFINED:
                return "undefined";
        }
    }

    // Local functions
    public static String getUnit() {
        if (UnitLocale.getDefault() == UnitLocale.Imperial) {
            return MyConstants.IMPERIAL;
        } else {
            return MyConstants.METRIC;
        }
    }
}
