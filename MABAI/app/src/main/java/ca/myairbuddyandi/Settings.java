package ca.myairbuddyandi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

//import java.util.List;

/**
 * Created by Michel on 2016-11-23.
 * Holds all of the logic for the Settings class
 */

public class Settings extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "Settings";

    // Public

    // Protected

    // Private

    // End of variables

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dive_settings);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dive_settings, new DiveSettingsFragment())
                .commit();
    }

    public static class DiveSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.dive_settings, rootKey);
        }
    }

}