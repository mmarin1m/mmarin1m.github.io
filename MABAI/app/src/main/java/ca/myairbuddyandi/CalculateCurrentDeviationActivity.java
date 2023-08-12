package ca.myairbuddyandi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import ca.myairbuddyandi.databinding.CalculateCurrentDeviationActivityBinding;

/**
 * Created by Michel on 2022-01-02.
 * Hold all of the logic for the CalculateCurrentDeviationActivity class
 */

public class CalculateCurrentDeviationActivity extends AppCompatActivity {
    // Static
    private static final String LOG_TAG = "CalculateCurrentDeviationActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateCurrentDeviation mCalculateCurrentDeviation = new CalculateCurrentDeviation();
    private CalculateCurrentDeviationActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private Double mDefaultDistance = MyConstants.ZERO_D;
    private Double mDefaultCurrentDeviation = MyConstants.ZERO_D;
    private Double mDefaultCurrentSpeedKnot = MyConstants.ZERO_D;
    private Double mDefaultSwimSpeed = MyConstants.ZERO_D;
    private Double mDefaultSwimTime = MyConstants.ZERO_D;
    private String mDefaultUnit;

    // Other
    private Double mOtherDistance = MyConstants.ZERO_D;
    private Double mOtherSwimSpeed = MyConstants.ZERO_D;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_current_deviation_activity);

        mCalculateCurrentDeviation.mBinding = mBinding;

        mBinding.setCalculateCurrentDeviation(mCalculateCurrentDeviation);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        mBinding.hdrSwimSpeedLbl.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CalculateSwimmingDistanceActivity.class);
            calculateSwimmingDistanceLauncher.launch(intent);
        });

        mBinding.hdrCurrentSpeedLbl.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CalculateCurrentActivity.class);
            calculateCurrentLauncher.launch(intent);
        });

        mBinding.calculateButton.setOnClickListener(view -> calculate());

        mBinding.calculateButton2.setOnClickListener(view -> calculate());

        mBinding.clearButton.setOnClickListener(view -> clear());

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();
        mBinding.calculateCurrentDeviationActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_current_deviation));
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

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> calculateSwimmingDistanceLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Get the data from the intent
                    assert data != null;
                    mDefaultSwimSpeed = data.getDoubleExtra(MyConstants.SWIMMING_SPEED,MyConstants.ZERO_D);
                    mCalculateCurrentDeviation.setDefaultSwimSpeed(MyFunctions.roundUp(mDefaultSwimSpeed,1));
                    mBinding.defaultSwimSpeed.setText(String.valueOf(mCalculateCurrentDeviation.getDefaultSwimSpeed()));

                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> calculateCurrentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    mDefaultCurrentSpeedKnot = data.getDoubleExtra(MyConstants.CURRENT_SPEED,MyConstants.ZERO_D);
                    mCalculateCurrentDeviation.setDefaultCurrentSpeedKnot(MyFunctions.roundUp(mDefaultCurrentSpeedKnot,1));
                    mBinding.defaultCurrentSpeedKnot.setText(String.valueOf(mCalculateCurrentDeviation.getDefaultCurrentSpeedKnot()));

                }
            });

    private void calculate() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialDeviation();
            convertFeetToMeter();
        } else {
            calculateMetricDeviation();
            convertMeterToFeet();
        }

        mCalculateCurrentDeviation.setOtherSwimSpeed(MyFunctions.roundUp(mOtherSwimSpeed,1));
        mCalculateCurrentDeviation.setOtherCurrentSpeedKnot(mDefaultCurrentSpeedKnot);
        mCalculateCurrentDeviation.setOtherCurrentDeviation(MyFunctions.roundUp(mDefaultCurrentDeviation,1));
        mCalculateCurrentDeviation.setOtherSwimTime(MyFunctions.roundUp(mDefaultSwimTime,1));

        mBinding.defaultCurrentDeviation.setText(String.valueOf(mCalculateCurrentDeviation.getDefaultCurrentDeviation()));
        mBinding.defaultSwimTime.setText(String.valueOf(mCalculateCurrentDeviation.getDefaultSwimTime()));

        mBinding.otherSwimSpeed.setText(String.valueOf(mCalculateCurrentDeviation.getOtherSwimSpeed()));
        mBinding.otherCurrentSpeedKnot.setText(String.valueOf(mCalculateCurrentDeviation.getOtherCurrentSpeedKnot()));
        mBinding.otherCurrentDeviation.setText(String.valueOf(mCalculateCurrentDeviation.getOtherCurrentDeviation()));
        mBinding.otherSwimTime.setText(String.valueOf(mCalculateCurrentDeviation.getOtherSwimTime()));

        requestFocus(mBinding.defaultDistance, false);
    }

    private void calculateImperialDeviation() {
        // Get all entered values first
        mDefaultDistance = mCalculateCurrentDeviation.getDefaultDistance();
        mDefaultSwimSpeed = mCalculateCurrentDeviation.getDefaultSwimSpeed();
        mDefaultCurrentSpeedKnot = mCalculateCurrentDeviation.getDefaultCurrentSpeedKnot();

        // Calculate the time to swim
        if (!mDefaultSwimSpeed.equals(MyConstants.ZERO_D)) {
            mDefaultSwimTime = mDefaultDistance / mDefaultSwimSpeed;
        }

        // Calculate the distance deviation
        double distanceDeviation = (mDefaultCurrentSpeedKnot * 6076.0 / 3600.0) * mDefaultSwimTime;

        // Calculate the Hypotenuse
        double hypotenuse = Math.sqrt(Math.pow(mDefaultDistance,2) + Math.pow(distanceDeviation,2));

        // Calculate the current deviation
        if (hypotenuse != MyConstants.ZERO_D) {
            mDefaultCurrentDeviation = Math.toDegrees(Math.asin(distanceDeviation / hypotenuse));
        }

        // Set the calculated values in the POJO
        mCalculateCurrentDeviation.setDefaultCurrentDeviation(MyFunctions.roundUp(mDefaultCurrentDeviation,1));
        mCalculateCurrentDeviation.setDefaultSwimTime(MyFunctions.roundUp(mDefaultSwimTime,1));
    }

    private void calculateMetricDeviation() {
        // Get all entered values first
        mDefaultDistance = mCalculateCurrentDeviation.getDefaultDistance();
        mDefaultSwimSpeed = mCalculateCurrentDeviation.getDefaultSwimSpeed();
        mDefaultCurrentSpeedKnot = mCalculateCurrentDeviation.getDefaultCurrentSpeedKnot();

        // Calculate the time to swim
        if (!mDefaultSwimSpeed.equals(MyConstants.ZERO_D)) {
            mDefaultSwimTime = mDefaultDistance / mDefaultSwimSpeed;
        }

        // Calculate the distance deviation
        double distanceDeviation = (mDefaultCurrentSpeedKnot * 1852.0 / 3600.0) * mDefaultSwimTime;

        // Calculate the Hypotenuse
        double hypotenuse = Math.sqrt(Math.pow(mDefaultDistance,2) + Math.pow(distanceDeviation,2));

        // Calculate the current deviation
        if (hypotenuse != MyConstants.ZERO_D) {
            mDefaultCurrentDeviation = Math.toDegrees(Math.asin(distanceDeviation / hypotenuse));
        }

        // Set the calculated values in the POJO
        mCalculateCurrentDeviation.setDefaultCurrentDeviation(MyFunctions.roundUp(mDefaultCurrentDeviation,1));
        mCalculateCurrentDeviation.setDefaultSwimTime(MyFunctions.roundUp(mDefaultSwimTime,1));
    }

    private void convertFeetToMeter() {
        // Convert Feet to Meter
        mOtherDistance = mMyCalcImperial.convertFeetToMeter(mDefaultDistance);
        mOtherSwimSpeed = mMyCalcImperial.convertFeetToMeter(mDefaultSwimSpeed);

        // Set the calculated converted values in the POJO
        mCalculateCurrentDeviation.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateCurrentDeviation.setOtherSwimSpeed(MyFunctions.roundUp(mOtherSwimSpeed,1));
    }

    private void convertMeterToFeet() {
        // Convert Meter to Feet
        mOtherDistance = mMyCalcMetric.convertMeterToFeet(mDefaultDistance);
        mOtherSwimSpeed = mMyCalcMetric.convertMeterToFeet(mDefaultSwimSpeed);

        // Set the calculated converted values in the POJO
        mCalculateCurrentDeviation.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateCurrentDeviation.setOtherSwimSpeed(MyFunctions.roundUp(mOtherSwimSpeed,1));
    }

    private void clear() {
        // Reset the Default
        mCalculateCurrentDeviation.setDefaultDistance(MyConstants.ZERO_D);
        mCalculateCurrentDeviation.setDefaultSwimTime(MyConstants.ZERO_D);

        mBinding.defaultDistance.setText("0.0");
        mBinding.defaultSwimSpeed.setText("0.0");
        mBinding.defaultCurrentSpeedKnot.setText("0.0");
        mBinding.defaultCurrentDeviation.setText("0.0");
        mBinding.defaultSwimTime.setText("0.0");

        // Reset the Other
        mCalculateCurrentDeviation.setOtherDistance(MyConstants.ZERO_D);
        mCalculateCurrentDeviation.setOtherSwimSpeed(MyConstants.ZERO_D);
        mCalculateCurrentDeviation.setOtherCurrentSpeedKph(MyConstants.ZERO_D);
        mCalculateCurrentDeviation.setOtherCurrentDeviation(MyConstants.ZERO_D);
        mCalculateCurrentDeviation.setOtherSwimTime(MyConstants.ZERO_D);

        mBinding.otherDistance.setText("0.0");
        mBinding.otherSwimSpeed.setText("0.0");
        mBinding.otherCurrentSpeedKnot.setText("0.0");
        mBinding.otherCurrentDeviation.setText("0.0");
        mBinding.otherSwimTime.setText("0.0");

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
        String otherSwimSpeed = mBinding.otherSwimSpeed.getText().toString();
        String otherSwimSpeedLbl = mBinding.otherSwimSpeedLbl.getText().toString();


        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherDistance.setText(mBinding.defaultDistance.getText().toString());
        mBinding.otherDistanceLbl.setText(mBinding.defaultDistanceLbl.getText().toString());
        mBinding.otherSwimSpeed.setText(mBinding.defaultSwimSpeed.getText().toString());
        mBinding.otherSwimSpeedLbl.setText(mBinding.defaultSwimSpeedLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultDistance.setText(otherDistance);
        mBinding.defaultDistanceLbl.setText(otherDistanceLbl);
        mBinding.defaultSwimSpeed.setText(otherSwimSpeed);
        mBinding.defaultSwimSpeedLbl.setText(otherSwimSpeedLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateCurrentDeviationActivity) {
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.apply();
    }
}
