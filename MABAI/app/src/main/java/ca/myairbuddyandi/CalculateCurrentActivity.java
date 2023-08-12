package ca.myairbuddyandi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import ca.myairbuddyandi.databinding.CalculateCurrentActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateCurrentActivity class
 */

public class CalculateCurrentActivity extends AppCompatActivity {
    // Static
    private static final String LOG_TAG = "CalculateCurrentActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateCurrent mCalculateCurrent = new CalculateCurrent();
    private CalculateCurrentActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private Double mDefaultDistance;
    private Double mDefaultSpeedKnot;
    private Double mDefaultTime;
    private String mDefaultUnit;
    // Other
    private Double mOtherDistance;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_current_activity);

        mCalculateCurrent.mBinding = mBinding;

        mBinding.setCalculateCurrent(mCalculateCurrent);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        mBinding.calculateButton.setOnClickListener(view -> calculate());

        mBinding.calculateButton2.setOnClickListener(view -> calculate());

        mBinding.clearButton.setOnClickListener(view -> clear());

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();
        mBinding.calculateCurrentActivity.requestFocus();

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_contact_us) {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_current));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            saveDefaultValues();
            Intent intent = new Intent();
            intent.putExtra(MyConstants.CURRENT_SPEED,mDefaultSpeedKnot);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        saveDefaultValues();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void calculate() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialKnot();
            convertFeetToMeter();
            convertKnotToImperialMph();
            convertKnotToImperialKph();
        } else {
            calculateMetricKnot();
            convertMeterToFeet();
            convertKnotToMetricKph();
            convertKnotToMetricMph();
        }

        mBinding.defaultSpeedKnot.setText(String.valueOf(mCalculateCurrent.getDefaultSpeedKnot()));
        mBinding.defaultSpeedMph.setText(String.valueOf(mCalculateCurrent.getDefaultSpeedMph()));
        mBinding.otherTime.setText(String.valueOf(mCalculateCurrent.getDefaultTime()));
        mBinding.otherSpeedKnot.setText(String.valueOf(mCalculateCurrent.getDefaultSpeedKnot()));
        mBinding.otherSpeedKph.setText(String.valueOf(mCalculateCurrent.getOtherSpeedKph()));

        requestFocus(mBinding.defaultDistance, false);
    }

    private void calculateImperialKnot() {
        // Get all entered values first
        mDefaultDistance = mCalculateCurrent.getDefaultDistance();
        mDefaultTime = mCalculateCurrent.getDefaultTime();

        // Calculate Knot
        mDefaultSpeedKnot = mMyCalcImperial.getImperialKnot(mDefaultDistance, mDefaultTime);

        // Set the calculated values in the POJO
        mCalculateCurrent.setDefaultSpeedKnot(MyFunctions.roundUp(mDefaultSpeedKnot,1));
    }

    private void calculateMetricKnot() {
        // Get all entered values first
        mDefaultDistance = mCalculateCurrent.getDefaultDistance();
        mDefaultTime = mCalculateCurrent.getDefaultTime();

        mDefaultSpeedKnot = mMyCalcMetric.getMetricKnot(mDefaultDistance, mDefaultTime);

        // Set the calculated values in the POJO
        mCalculateCurrent.setDefaultSpeedKnot(MyFunctions.roundUp(mDefaultSpeedKnot,1));
    }

    private void convertFeetToMeter() {
        // Convert Feet to Meter
        mOtherDistance = mMyCalcImperial.convertFeetToMeter(mDefaultDistance);

        // Set the calculated converted values in the POJO
        mCalculateCurrent.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
    }

    private void convertMeterToFeet() {
        // Convert Meter to Feet
        mOtherDistance = mMyCalcMetric.convertMeterToFeet(mDefaultDistance);

        // Set the calculated converted values in the POJO
        mCalculateCurrent.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
    }

    private void convertKnotToImperialMph() {
        // Convert Knot to Miles per hour
        Double mDefaultSpeedMph = mMyCalcImperial.convertKnotToMph(mDefaultSpeedKnot);

        // Set the calculated converted values in the POJO
        mCalculateCurrent.setDefaultSpeedMph(MyFunctions.roundUp(mDefaultSpeedMph,1));
    }

    private void convertKnotToImperialKph() {
        // Convert Knot to Kilometer per hour
        Double mOtherSpeedKph = mMyCalcMetric.convertKnotToKph(mDefaultSpeedKnot);

        // Set the calculated converted values in the POJO
        mCalculateCurrent.setOtherSpeedKph(MyFunctions.roundUp(mOtherSpeedKph,1));
    }

    private void convertKnotToMetricKph() {
        // Convert Knot to Kilometer per hour
        Double mDefaultSpeedKph = mMyCalcMetric.convertKnotToKph(mDefaultSpeedKnot);

        // Set the calculated converted values in the POJO
        mCalculateCurrent.setDefaultSpeedMph(MyFunctions.roundUp(mDefaultSpeedKph,1));
    }

    private void convertKnotToMetricMph() {
        // Convert Knot to Miles per hour
        Double mOtherSpeedMph = mMyCalcImperial.convertKnotToMph(mDefaultSpeedKnot);

        // Set the calculated converted values in the POJO
        mCalculateCurrent.setOtherSpeedKph(MyFunctions.roundUp(mOtherSpeedMph,1));
    }

    private void clear() {
        // Reset the Default
        mCalculateCurrent.setDefaultDistance(MyConstants.ZERO_D);
        mCalculateCurrent.setDefaultTime(MyConstants.ZERO_D);

        mBinding.defaultDistance.setText("0.0");
        mBinding.defaultTime.setText("0.0");
        mBinding.defaultSpeedKnot.setText("0.0");
        mBinding.defaultSpeedMph.setText("0.0");

        // Reset the Other
        mCalculateCurrent.setOtherDistance(MyConstants.ZERO_D);
        mCalculateCurrent.setOtherTime(MyConstants.ZERO_D);

        mBinding.otherSpeedKnot.setText("0.0");
        mBinding.otherSpeedKph.setText("0.0");
        mBinding.otherSpeedKnot.setText("0.0");
        mBinding.otherSpeedKph.setText("0.0");

        requestFocus(mBinding.defaultDistance, false);
    }

    private void requestFocus(View view, boolean showKeyboard) {
        if (view instanceof EditText) {
            // Only works for EditText
            view.clearFocus();
            view.requestFocus();
            ((EditText) view).selectAll();
            if (showKeyboard) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            view.clearFocus();
            view.requestFocus();
        }
    }

    public void switchSide() {
        // Save the Other side
        String otherUnit = mBinding.otherUnit.getText().toString();

        String otherDistance = mBinding.otherDistance.getText().toString();
        String otherDistanceLbl = mBinding.otherDistanceLbl.getText().toString();
        String otherSpeedKph = mBinding.otherSpeedKph.getText().toString();
        String otherSpeedKphLbl = mBinding.otherSpeedKphLbl.getText().toString();


        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherDistance.setText(mBinding.defaultDistance.getText().toString());
        mBinding.otherDistanceLbl.setText(mBinding.defaultDistanceLbl.getText().toString());
        mBinding.otherSpeedKph.setText(mBinding.defaultSpeedMph.getText().toString());
        mBinding.otherSpeedKphLbl.setText(mBinding.defaultSpeedMphLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultDistance.setText(otherDistance);
        mBinding.defaultDistanceLbl.setText(otherDistanceLbl);
        mBinding.defaultSpeedMph.setText(otherSpeedKph);
        mBinding.defaultSpeedMphLbl.setText(otherSpeedKphLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateCurrentActivity) {
            requestFocus(view, true);
        }
    }

    private void switchToOtherUnit() {
        // Set the new Default Unit
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            mDefaultUnit = MyConstants.METRIC;
        } else {
            mDefaultUnit = MyConstants.IMPERIAL;
        }
    }

    private void readDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // If no default unit (Last Used unit) exists e.g. first time ever, use the Phone unit
        mDefaultUnit = (preferences.getString(getString(R.string.code_default_unit), mUnit));

        // The preferred unit is always displayed on the left
        // The layout defaults to Imperial unit on the left
        // Might have to switch if the user last exit the activity on using the other unit

        assert mDefaultUnit != null;
        if (mUnit.equals(MyConstants.IMPERIAL)) {
            // The phone is in IMPERIAL
            // And it is also the App default
            if (!mDefaultUnit.equals(mUnit)) {
                // Switch to METRIC
                switchSide();
            }
        } else {
            if (mDefaultUnit.equals(MyConstants.METRIC)) {
                // The phone is in METRIC
                // Switch to METRIC
                switchSide();
            }
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.apply();
    }
}