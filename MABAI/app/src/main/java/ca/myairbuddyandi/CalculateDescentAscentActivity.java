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

import ca.myairbuddyandi.databinding.CalculateDescentAscentActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateDescentAscentActivity class
 */

public class CalculateDescentAscentActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateDescentAscentActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateDescentAscent mCalculateDa = new CalculateDescentAscent();
    private CalculateDescentAscentActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private Double mDefaultDepth;
    private Double mDefaultRate;
    private Double mDefaultTime;
    private String mDefaultUnit;

    // Other
    private Double mOtherDepth;
    private Double mOtherRate;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_descent_ascent_activity);

        mCalculateDa.mBinding = mBinding;

        mBinding.setCalculateDA(mCalculateDa);

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

        mBinding.calculateDescentAscentRateActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_descent_ascent));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            saveDefaultValues();
            Intent intent = new Intent();
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
            calculateImperialTime();
            convertToMetricRate();
            convertToMetricDepth();
            convertToMetricTime();
        } else {
            calculateMetricTime();
            convertToImperialRate();
            convertToImperialDepth();
            convertToImperialTime();
        }

        mBinding.defaultRate.setText(String.valueOf(mCalculateDa.getDefaultRate()));
        mBinding.defaultDepth.setText(String.valueOf(mCalculateDa.getDefaultDepth()));
        mBinding.defaultTime.setText(String.valueOf(mCalculateDa.getDefaultTime()));

        requestFocus(mBinding.defaultDepth, false);
    }

    private void calculateImperialTime() {
        // Get all entered values first
        mDefaultRate = mCalculateDa.getDefaultRate();
        mDefaultDepth = mCalculateDa.getDefaultDepth();

        // Calculate Time
        if (mDefaultRate.equals(MyConstants.ZERO_D)) {
            mDefaultTime = MyConstants.ZERO_D;
        } else {
            mDefaultTime = mDefaultDepth / mDefaultRate;
        }

        // Set the calculated values in the POJO
        mCalculateDa.setDefaultTime(MyFunctions.roundUp(mDefaultTime,1));
    }

    private void calculateMetricTime() {
        // Get all entered values first
        mDefaultRate = mCalculateDa.getDefaultRate();
        mDefaultDepth = mCalculateDa.getDefaultDepth();

        // Calculate Time
        if (mDefaultRate.equals(MyConstants.ZERO_D)) {
            mDefaultTime = MyConstants.ZERO_D;
        } else {
            mDefaultTime = mDefaultDepth / mDefaultRate;
        }

        // Set the calculated values in the POJO
        mCalculateDa.setDefaultTime(MyFunctions.roundUp(mDefaultTime,1));
    }

    private void convertToImperialRate() {
        // Convert meter/min to feet/min
        mOtherRate = mMyCalcImperial.convertMeterToFeet(mDefaultRate);

        // Set the calculated converted values in the POJO
        mCalculateDa.setOtherRate(MyFunctions.roundUp(mOtherRate,1));
    }

    private void convertToImperialDepth() {
        // Convert meter to feet
        mOtherDepth = mMyCalcImperial.convertMeterToFeet(mDefaultDepth);

        // Set the calculated converted values in the POJO
        mCalculateDa.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
    }

    private void convertToImperialTime() {
        // Same value as metric
        mCalculateDa.setOtherTime(MyFunctions.roundUp(mDefaultTime,1));
    }

    private void convertToMetricRate() {
        // Convert feet/min to meter/min
        mOtherRate = mMyCalcMetric.convertFeetToMeter(mDefaultRate);

        // Set the calculated converted values in the POJO
        mCalculateDa.setOtherRate(MyFunctions.roundUp(mOtherRate,1));
    }

    private void convertToMetricDepth() {
        // Convert feet to meter
        mOtherDepth = mMyCalcMetric.convertFeetToMeter(mDefaultDepth);

        // Set the calculated converted values in the POJO
        mCalculateDa.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
    }

    private void convertToMetricTime() {
        // Same value as imperial
        mCalculateDa.setOtherTime(MyFunctions.roundUp(mDefaultTime,1));
    }

    private void clear() {
        // Reset the Default
        mCalculateDa.setDefaultRate(MyConstants.ZERO_D);
        mCalculateDa.setDefaultDepth(MyConstants.ZERO_D);
        mCalculateDa.setDefaultTime(MyConstants.ZERO_D);

        mBinding.defaultRate.setText("0.0");
        mBinding.defaultDepth.setText("0.0");
        mBinding.defaultTime.setText("0.0");

        // Reset the Other
        mCalculateDa.setOtherRate(MyConstants.ZERO_D);
        mCalculateDa.setOtherDepth(MyConstants.ZERO_D);
        mCalculateDa.setOtherTime(MyConstants.ZERO_D);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateDescentAscentRateActivity) {
            requestFocus(view, true);
        }
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

        String otherRate = mBinding.otherRate.getText().toString();
        String otherRateLbl = mBinding.otherRateLbl.getText().toString();
        String otherDepth = mBinding.otherDepth.getText().toString();
        String otherDepthLbl = mBinding.otherDepthLbl.getText().toString();
        String otherTime = mBinding.otherTime.getText().toString();
        String otherTimeLbl = mBinding.otherTimeLbl.getText().toString();


        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherRate.setText(mBinding.defaultRate.getText().toString());
        mBinding.otherRateLbl.setText(mBinding.defaultRateLbl.getText().toString());
        mBinding.otherDepth.setText(mBinding.defaultDepth.getText().toString());
        mBinding.otherDepthLbl.setText(mBinding.defaultDepthLbl.getText().toString());
        mBinding.otherTime.setText(mBinding.defaultTime.getText().toString());
        mBinding.otherTimeLbl.setText(mBinding.defaultTimeLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultRate.setText(otherRate);
        mBinding.defaultRateLbl.setText(otherRateLbl);
        mBinding.defaultDepth.setText(otherDepth);
        mBinding.defaultDepthLbl.setText(otherDepthLbl);
        mBinding.defaultTime.setText(otherTime);
        mBinding.defaultTimeLbl.setText(otherTimeLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateDescentAscentRateActivity) {
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
