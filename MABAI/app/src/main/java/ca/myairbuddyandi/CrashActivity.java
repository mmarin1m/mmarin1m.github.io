package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Michel on 2017-02-26.
 * Holds all of the logic for the CrashActivity class
 */

public class CrashActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CrashActivity";

    // Public

    // Protected

    // End of variables

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        setContentView(R.layout.crash_activity);

         TextView mError = findViewById(R.id.crash_error);

        mError.setText(getIntent().getStringExtra("error"));

        Log.d(LOG_TAG, "onCreate done");
    }
}
