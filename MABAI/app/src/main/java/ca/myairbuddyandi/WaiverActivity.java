package ca.myairbuddyandi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Michel on 2016-11-24.
 * Holds all of the logic for the WaiverActivity class
 */

public class WaiverActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "WaiverActivity";

    // Public

    // Protected

    // Private

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        setContentView(R.layout.waiver_activity);

        TextView textview = findViewById(R.id.textViewWaiver);
        textview.setText(MyFunctions.fromHtml(getString(R.string.help_waiver)));

        // Set the listeners
        Button buttonAccept = findViewById(R.id.accept_button);
        buttonAccept.setOnClickListener(view -> {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        });

        Button buttonRefuse = findViewById(R.id.refuse_button);
        buttonRefuse.setOnClickListener(view -> {
        finishAffinity();
        System.exit(0);
        });

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
