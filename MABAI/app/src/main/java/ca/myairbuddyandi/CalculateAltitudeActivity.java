package ca.myairbuddyandi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import ca.myairbuddyandi.databinding.CalculateAltitudeActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateAltitudeActivity class
 */

public class CalculateAltitudeActivity extends AppCompatActivity {
    // Static
    private static final String LOG_TAG = "CalculateAltitudeActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateAltitude mCalculateAltitude = new CalculateAltitude();
    private CalculateAltitudeActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private boolean mDefaultSalinity; // true = Salt = 0 position, false = Fresh = 1 position
    private Double mDefaultAltitude = MyConstants.ZERO_D;
    private Double mDefaultDepth = MyConstants.ZERO_D;
    private Double mDefaultSafetyStop = MyConstants.ZERO_D;
    private Double mDefaultSurfacePressure = MyConstants.ZERO_D;
    private Double mDefaultSurfacePressureMbar = MyConstants.ZERO_D;
    private Double mDefaultTheoreticalDepth = MyConstants.ZERO_D;
    private String mDefaultUnit;

    // Other
    private Double mOtherAltitude = MyConstants.ZERO_D;
    private Double mOtherDepth = MyConstants.ZERO_D;
    private Double mOtherSafetyStop = MyConstants.ZERO_D;
    private Double mOtherTheoreticalDepth = MyConstants.ZERO_D;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_altitude_activity);

        mCalculateAltitude.setContext(this);

        mCalculateAltitude.mBinding = mBinding;

        mBinding.setCalculateAltitude(mCalculateAltitude);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        mBinding.calculateButton.setOnClickListener(view -> calculate());

        mBinding.calculateButton2.setOnClickListener(view -> calculate());

        mBinding.clearButton.setOnClickListener(view -> clear());

        //Set the data in the Spinner Salinity
        String[] itemsDefaultSalinity = getResources().getStringArray(R.array.salinity_arrays);
        ArrayAdapter<String> adapterDefaultSalinity = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsDefaultSalinity);
        adapterDefaultSalinity.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculateAltitude.setAdapterDefaultSalinity(adapterDefaultSalinity);

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateAltitudeActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_altitude));
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
        if (!validateAltitude()) {
            return;
        }

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperial();
            convertToMetricAltitude();
            convertToMetricDepth();
            convertToMetricSurfacePressure();
            convertToMetricSurfacePressureMbar();
            convertToMetricTheoreticalDepth();
            convertToMetricSafetyStop();
        } else {
            calculateMetric();
            convertToImperialAltitude();
            convertToImperialDepth();
            convertToImperialSurfacePressure();
            convertToImperialSurfacePressureMbar();
            convertToImperialTheoreticalDepth();
            convertToImperialSafetyStop();
        }

        mBinding.defaultAL.setText(String.valueOf(mCalculateAltitude.getDefaultAltitude()));
        mBinding.defaultDE.setText(String.valueOf(mCalculateAltitude.getDefaultDepth()));
        mBinding.defaultSP.setText(String.valueOf(mCalculateAltitude.getDefaultSurfacePressure()));
        mBinding.defaultSPmB.setText(String.valueOf(mCalculateAltitude.getDefaultSurfacePressureMbar()));
        mBinding.defaultTD.setText(String.valueOf(mCalculateAltitude.getDefaultTheoreticalDepth()));
        mBinding.defaultSS.setText(String.valueOf(mCalculateAltitude.getDefaultSafetyStop()));

        requestFocus(mBinding.defaultAL, false);
    }

    private void calculateImperial() {
        // Get all entered values first
        mDefaultSalinity = mCalculateAltitude.getDefaultSalinity();
        mDefaultAltitude = mCalculateAltitude.getDefaultAltitude();
        mDefaultDepth = mCalculateAltitude.getDefaultDepth();

        // Calculate Atmospheric Pressure
        mDefaultSurfacePressure = Math.exp(MyConstants.ATMOSPHERIC_PRESSURE * Math.log(1-(mMyCalcImperial.getC() * mDefaultAltitude)));
        mDefaultSurfacePressureMbar = mDefaultSurfacePressure * MyConstants.ATA_TO_MBAR;
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            // NOTE: Leave as is as a reminder of the logic compared to fresh water
            mDefaultTheoreticalDepth = MyFunctions.roundUp(mDefaultDepth * (1 / mDefaultSurfacePressure) * (33.0 / 33.0),1); // Second number is the variant in Sea water
            mDefaultSafetyStop = MyFunctions.roundUp(15 * mDefaultSurfacePressure * (33.0 / 33.0),1); // First number is the variant in Sea water
        } else {
            mDefaultTheoreticalDepth = MyFunctions.roundUp(mDefaultDepth * (1 / mDefaultSurfacePressure) * (33.0 / 34.0),1); // Second number is the variant in fresh water
            mDefaultSafetyStop = MyFunctions.roundUp(15 * mDefaultSurfacePressure * (34.0 / 33.0),1); // First number is the variant in Fresh water
        }

        // Set the calculated values in the POJO
        mCalculateAltitude.setDefaultSurfacePressure(MyFunctions.roundUp(mDefaultSurfacePressure,4));
        mCalculateAltitude.setDefaultSurfacePressureMbar(MyFunctions.roundUp(mDefaultSurfacePressureMbar,1));
        mCalculateAltitude.setDefaultTheoreticalDepth(MyFunctions.roundUp(mDefaultTheoreticalDepth,1));
        mCalculateAltitude.setDefaultSafetyStop(MyFunctions.roundUp(mDefaultSafetyStop,1));
    }


    private void calculateMetric() {
        // Get all entered values first
        mDefaultSalinity = mCalculateAltitude.getDefaultSalinity();
        mDefaultAltitude = mCalculateAltitude.getDefaultAltitude();
        mDefaultDepth = mCalculateAltitude.getDefaultDepth();

        // Calculate Atmospheric Pressure
        mDefaultSurfacePressure = Math.exp(MyConstants.ATMOSPHERIC_PRESSURE * Math.log(1-(mMyCalcMetric.getC() * mDefaultAltitude)));
        mDefaultSurfacePressureMbar = mDefaultSurfacePressure * MyConstants.ATA_TO_MBAR;
        String mDefaultSafetyStopUnit;
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            mDefaultTheoreticalDepth = MyFunctions.roundUp(mDefaultDepth * (1 / mDefaultSurfacePressure) * 1.0,1);
            mDefaultSafetyStop = MyFunctions.roundUp(5 * mDefaultSurfacePressure * 1.0,1);
            mDefaultSafetyStopUnit = mMyCalcMetric.getSeaWaterUnit();
        } else {
            mDefaultTheoreticalDepth = MyFunctions.roundUp(mDefaultDepth * (1 / mDefaultSurfacePressure) * (1.0/MyConstants.WEIGHT_SEA_WATER_M),1);
            mDefaultSafetyStop = MyFunctions.roundUp(5 * mDefaultSurfacePressure * (1.0/MyConstants.WEIGHT_SEA_WATER_M),1);
            mDefaultSafetyStopUnit = mMyCalcMetric.getFreshWaterUnit();
        }

        // Set the calculated values in the POJO
        mCalculateAltitude.setDefaultSurfacePressure(MyFunctions.roundUp(mDefaultSurfacePressure,4));
        mCalculateAltitude.setDefaultSurfacePressureMbar(MyFunctions.roundUp(mDefaultSurfacePressureMbar,1));
        mCalculateAltitude.setDefaultTheoreticalDepth(MyFunctions.roundUp(mDefaultTheoreticalDepth,1));
        mCalculateAltitude.setDefaultSafetyStop(MyFunctions.roundUp(mDefaultSafetyStop,1));
        mCalculateAltitude.setDefaultSafetyStopUnit(mDefaultSafetyStopUnit);
    }

    private void convertToImperialAltitude() {
        // Convert meter to feet
        mOtherAltitude = mMyCalcImperial.convertMeterToFeet(mDefaultAltitude);

        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherAltitude(MyFunctions.roundUp(mOtherAltitude,1));
    }

    private void convertToImperialDepth() {
        // Convert meter to feet
        mOtherDepth = mMyCalcImperial.convertMeterToFeet(mDefaultDepth);

        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
    }

    private void convertToImperialSurfacePressure() {
        // No conversion, ata = ata
        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherSurfacePressure(MyFunctions.roundUp(mDefaultSurfacePressure,4));
    }

    private void convertToImperialSurfacePressureMbar() {
        // No conversion, mBar = mBar
        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherSurfacePressureMbar(MyFunctions.roundUp(mDefaultSurfacePressureMbar,1));
    }

    private void convertToImperialTheoreticalDepth() {
        // Convert meter to feet
        mOtherTheoreticalDepth = mMyCalcImperial.convertMeterToFeet(mDefaultTheoreticalDepth);

        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherTheoreticalDepth(MyFunctions.roundUp(mOtherTheoreticalDepth,1));
    }

    private void convertToImperialSafetyStop() {
        // Convert meter to feet
        mOtherSafetyStop = mMyCalcImperial.convertMeterToFeet(mDefaultSafetyStop);

        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherSafetyStop(MyFunctions.roundUp(mOtherSafetyStop,1));
    }

    private void convertToMetricAltitude() {
        // Convert feet to meter
        mOtherAltitude = mMyCalcMetric.convertFeetToMeter(mDefaultAltitude);

        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherAltitude(MyFunctions.roundUp(mOtherAltitude,1));
    }

    private void convertToMetricDepth() {
        // Convert feet to meter
        mOtherDepth = mMyCalcMetric.convertFeetToMeter(mDefaultDepth);

        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
    }

    private void convertToMetricSurfacePressure() {
        // No conversion ata = ata
        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherSurfacePressure(MyFunctions.roundUp(mDefaultSurfacePressure,4));
    }

    private void convertToMetricSurfacePressureMbar() {
        // No conversion mBar = mBar
        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherSurfacePressureMbar(MyFunctions.roundUp(mDefaultSurfacePressureMbar,1));
    }

    private void convertToMetricTheoreticalDepth() {
        // Convert feet to meter
        mOtherTheoreticalDepth = mMyCalcMetric.convertFeetToMeter(mDefaultTheoreticalDepth);

        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherTheoreticalDepth(MyFunctions.roundUp(mOtherTheoreticalDepth,1));
    }

    private void convertToMetricSafetyStop() {
        // Convert feet to meter
        mOtherSafetyStop = mMyCalcMetric.convertFeetToMeter(mDefaultSafetyStop);

        // Set the calculated converted values in the POJO
        mCalculateAltitude.setOtherSafetyStop(MyFunctions.roundUp(mOtherSafetyStop,1));
    }

    private void clear() {
        // Reset the Default
        mCalculateAltitude.setDefaultAltitude(MyConstants.ZERO_D);
        mCalculateAltitude.setDefaultDepth(MyConstants.ZERO_D);
        mCalculateAltitude.setDefaultSurfacePressure(MyConstants.ZERO_D);
        mCalculateAltitude.setDefaultSurfacePressureMbar(MyConstants.ZERO_D);
        mCalculateAltitude.setDefaultTheoreticalDepth(MyConstants.ZERO_D);
        mCalculateAltitude.setDefaultSafetyStop(MyConstants.ZERO_D);

        mBinding.defaultAL.setText("0.0");
        mBinding.defaultDE.setText("0.0");
        mBinding.defaultSP.setText("0.0");
        mBinding.defaultSPmB.setText("0.0");
        mBinding.defaultTD.setText("0.0");
        mBinding.defaultSS.setText("0.0");

        // Reset the Other
        mCalculateAltitude.setOtherAltitude(MyConstants.ZERO_D);
        mCalculateAltitude.setOtherDepth(MyConstants.ZERO_D);
        mCalculateAltitude.setOtherSurfacePressure(MyConstants.ZERO_D);
        mCalculateAltitude.setOtherSurfacePressureMbar(MyConstants.ZERO_D);
        mCalculateAltitude.setOtherTheoreticalDepth(MyConstants.ZERO_D);
        mCalculateAltitude.setOtherSafetyStop(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultAL,false);
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

        String otherAltitude = mBinding.otherAL.getText().toString();
        String otherAltitudeLbl = mBinding.otherALLbl.getText().toString();
        String otherDepth = mBinding.otherDE.getText().toString();
        String otherDepthLbl = mBinding.otherDELbl.getText().toString();
        String otherSurfacePressure = mBinding.otherSP.getText().toString();
        String otherSurfacePressureLbl = mBinding.otherSPLbl.getText().toString();
        String otherSurfacePressureMbar = mBinding.otherSPmB.getText().toString();
        String otherSurfacePressureMbarLbl = mBinding.otherSPmBLbl.getText().toString();
        String otherTheoreticalDepth = mBinding.otherTD.getText().toString();
        String otherTheoreticalDepthLbl = mBinding.otherTDLbl.getText().toString();
        String otherSafetyStop = mBinding.otherSS.getText().toString();
        String otherSafetyStopLbl = mBinding.otherSSULbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherAL.setText(mBinding.defaultAL.getText().toString());
        mBinding.otherALLbl.setText(mBinding.defaultALLbl.getText().toString());
        mBinding.otherDE.setText(mBinding.defaultDE.getText().toString());
        mBinding.otherDELbl.setText(mBinding.defaultDELbl.getText().toString());
        mBinding.otherSP.setText(mBinding.defaultSP.getText().toString());
        mBinding.otherSPLbl.setText(mBinding.defaultSPLbl.getText().toString());
        mBinding.otherSPmB.setText(mBinding.defaultSPmB.getText().toString());
        mBinding.otherSPmBLbl.setText(mBinding.defaultSPmBLbl.getText().toString());
        mBinding.otherTD.setText(mBinding.defaultTD.getText().toString());
        mBinding.otherTDLbl.setText(mBinding.defaultTDLbl.getText().toString());
        mBinding.otherSS.setText(mBinding.defaultSS.getText().toString());
        mBinding.otherSSULbl.setText(mBinding.defaultSSULbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultAL.setText(otherAltitude);
        mBinding.defaultALLbl.setText(otherAltitudeLbl);
        mBinding.defaultDE.setText(otherDepth);
        mBinding.defaultDELbl.setText(otherDepthLbl);
        mBinding.defaultSP.setText(otherSurfacePressure);
        mBinding.defaultSPLbl.setText(otherSurfacePressureLbl);
        mBinding.defaultSPmB.setText(otherSurfacePressureMbar);
        mBinding.defaultSPmBLbl.setText(otherSurfacePressureMbarLbl);
        mBinding.defaultTD.setText(otherTheoreticalDepth);
        mBinding.defaultTDLbl.setText(otherTheoreticalDepthLbl);
        mBinding.defaultSS.setText(otherSafetyStop);
        mBinding.defaultSSULbl.setText(otherSafetyStopLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateAltitudeActivity) {
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
        mCalculateAltitude.setDefaultUnit(mDefaultUnit);
    }

    private void readDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // If no default unit (Last Used unit) exists e.g. first time ever, use the Phone unit
        mDefaultUnit = (preferences.getString(getString(R.string.code_default_unit), mUnit));
        mCalculateAltitude.setDefaultUnit(mDefaultUnit);

        // The preferred unit is always displayed on the left
        // The layout defaults to Imperial unit on the left
        // Might have to switch if the user last exit the activity on using the other unit

        if (mUnit.equals(MyConstants.IMPERIAL)) {
            // The phone is in IMPERIAL
            // And it is also the App default
            if (!mDefaultUnit.equals(mUnit)) {
                // Switch to METRIC
                switchSide();
            }
        } else if (mDefaultUnit.equals(MyConstants.METRIC)) {
            // The phone is in METRIC
            // Switch to METRIC
            switchSide();
        }

        // Last values stored as always the same as the Preferred Unit
        mDefaultSalinity = preferences.getBoolean(getString(R.string.code_default_salinity), true);

        // Set the values in the POJO
        mCalculateAltitude.setDefaultSalinity(mDefaultSalinity);
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            mCalculateAltitude.setDefaultSalinityPosition(MyConstants.ZERO_I);
        } else {
            mCalculateAltitude.setDefaultSalinityPosition(MyConstants.ONE_I);
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putBoolean(getString(R.string.code_default_salinity), mCalculateAltitude.getDefaultSalinity());
        edit.apply();
    }

    // Validating and Saving functions

    public Runnable yesProc(){
        return () -> {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        };
    }

    public Runnable noProc(){
        return () -> {
            //Do nothing, stay on this activity
        };
    }

    private boolean validateAltitude() {
        // Required
        if (mBinding.defaultAL.getText().toString().trim().isEmpty() || !isValidAltitude(mCalculateAltitude.getDefaultAltitude())) {
            String message;
            if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
                // NOTE: Leave as is
                message = String.format(getResources().getString(R.string.msg_altitude), MyConstants.ZERO_D.toString(), mMyCalcImperial.getMaxAltitude().toString(),mMyCalcImperial.getDepthUnit());
            } else {
                // NOTE: Leave as is
                message = String.format(getResources().getString(R.string.msg_altitude), MyConstants.ZERO_D.toString(), mMyCalcMetric.getMaxAltitude().toString(),mMyCalcMetric.getDepthUnit());
            }
            mBinding.defaultAL.setError(message);
            requestFocus(mBinding.defaultAL, true);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidAltitude(double altitude) {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            return (altitude >=  MyConstants.ZERO_D && altitude <= mMyCalcImperial.getMaxAltitude());
        } else {
            return (altitude >=  MyConstants.ZERO_D && altitude <= mMyCalcMetric.getMaxAltitude());
        }
    }
}
