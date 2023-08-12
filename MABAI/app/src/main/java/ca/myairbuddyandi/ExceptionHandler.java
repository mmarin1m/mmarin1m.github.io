package ca.myairbuddyandi;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Michel on 2017-02-25.
 * Holds all of the logic for the ExceptionHandler class
 */

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler{

    // Static
    private static final String LOG_TAG = "ExceptionHandler";

    // Public

    // Protected

    // Private
    private final Activity mContext;

    // End of variables

    // Public constructor
    public ExceptionHandler(Activity context) {
        mContext = context;
    }

    // NOTE: Do not annotate @NonNUll
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void uncaughtException( Thread t, Throwable e) {
        String LINE_SEPARATOR = "\n";
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        String errorReport;
        errorReport = "************ CAUSE OF ERROR ************\n\n";
        errorReport += stackTrace.toString();
        errorReport += stackTrace.toString();
        errorReport += "\n************ DEVICE INFORMATION ***********\n";
        errorReport += "Brand: ";
        errorReport += Build.BRAND;
        errorReport += LINE_SEPARATOR;
        errorReport += "Device: ";
        errorReport += Build.DEVICE;
        errorReport += LINE_SEPARATOR;
        errorReport += "Model: ";
        errorReport += Build.MODEL;
        errorReport += LINE_SEPARATOR;
        errorReport += "Id: ";
        errorReport += Build.ID;
        errorReport += LINE_SEPARATOR;
        errorReport += "Product: ";
        errorReport += Build.PRODUCT;
        errorReport += LINE_SEPARATOR;
        errorReport += "\n************ FIRMWARE ************\n";
        errorReport += "SDK: ";
        errorReport += Build.VERSION.SDK_INT;
        errorReport += LINE_SEPARATOR;
        errorReport += "Release: ";
        errorReport += Build.VERSION.RELEASE;
        errorReport += LINE_SEPARATOR;
        errorReport += "Incremental: ";
        errorReport += Build.VERSION.INCREMENTAL;
        errorReport += LINE_SEPARATOR;

        Intent intent = new Intent(mContext, CrashActivity.class);
        intent.putExtra("error", errorReport);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        mContext.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
