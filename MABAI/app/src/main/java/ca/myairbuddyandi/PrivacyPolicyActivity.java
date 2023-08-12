package ca.myairbuddyandi;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Michel on 2016-11-24.
 * Holds all of the logic for the PrivacyPolicyActivity class
 *
 */

public class PrivacyPolicyActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "PrivacyPolicyActivity";

    // Public

    // Protected

    // Private

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) { Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this)); }

        setContentView(R.layout.privacy_policy_activity);

        TextView textview = findViewById(R.id.textViewPrivacyPolicy);
        textview.setText(MyFunctions.fromHtml(getString(R.string.help_privacy_policy)));
        textview.setMovementMethod(LinkMovementMethod.getInstance());

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if(id==android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}