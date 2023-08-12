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

import ca.myairbuddyandi.databinding.CalculateGasActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateGasActivity class
 */

public class CalculateGasActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateGasActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateGas mCalculateGas = new CalculateGas();
    private CalculateGasActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private Double mDefaultPressure;
    private Double mDefaultRatedPressure;
    private Double mDefaultTankFactor;
    private Double mDefaultTankVolume;
    private String mDefaultUnit;
    private Double mDefaultVolume;

    // Other
    private Double mOtherPressure;
    private Double mOtherTankVolume;
    private Double mOtherVolume;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_gas_activity);

        mCalculateGas.mBinding = mBinding;

        mBinding.setCalculateGas(mCalculateGas);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        mBinding.volumeButton.setOnClickListener(view -> calculateVolume());

        mBinding.volumeButton2.setOnClickListener(view -> calculateVolume());

        mBinding.pressureButton.setOnClickListener(view -> calculatePressure());

        mBinding.pressureButton2.setOnClickListener(view -> calculatePressure());

        mBinding.clearButton.setOnClickListener(view -> clear());

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateGasActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_gas));
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

    private void calculatePressure() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Calculate Imperial Volume
            calculateImperialPressure();
            // Calculate all Metric values
            convertToMetricPressure();
            convertToMetricVolume();
        } else {
            // Calculate Metric Volume
            calculateMetricPressure();
            // Calculate all Imperial values
            convertToImperialPressure();
            convertToImperialVolume();
        }

        mBinding.defaultPressure.setText(String.valueOf(mCalculateGas.getDefaultPressure()));
        mBinding.defaultTankVolume.setText(String.valueOf(mCalculateGas.getDefaultTankVolume()));
        mBinding.defaultRatedPressure.setText(String.valueOf(mCalculateGas.getDefaultRatedPressure()));
        mBinding.defaultTankFactor.setText(String.valueOf(mCalculateGas.getDefaultTankFactor()));
        mBinding.defaultVolume.setText(String.valueOf(mCalculateGas.getDefaultVolume()));
        mBinding.otherVolume.setText(String.valueOf(mCalculateGas.getOtherVolume()));

        requestFocus(mBinding.defaultPressure, false);
    }

    private void calculateVolume() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Calculate Imperial Volume
            calculateImperialVolume();
            // Calculate all Metric values
            convertToMetricPressure();
            convertToMetricVolume();
        } else {
            // Calculate Metric Volume
            calculateMetricVolume();
            // Calculate all Imperial values
            convertToImperialPressure();
            convertToImperialVolume();
        }

        mBinding.defaultPressure.setText(String.valueOf(mCalculateGas.getDefaultPressure()));
        mBinding.defaultTankVolume.setText(String.valueOf(mCalculateGas.getDefaultTankVolume()));
        mBinding.defaultRatedPressure.setText(String.valueOf(mCalculateGas.getDefaultRatedPressure()));
        mBinding.defaultTankFactor.setText(String.valueOf(mCalculateGas.getDefaultTankFactor()));
        mBinding.defaultVolume.setText(String.valueOf(mCalculateGas.getDefaultVolume()));
        mBinding.otherVolume.setText(String.valueOf(mCalculateGas.getOtherVolume()));

        requestFocus(mBinding.defaultPressure, false);
    }

    private void calculateImperialPressure() {
        // Get all entered values first
        mDefaultTankVolume = mCalculateGas.getDefaultTankVolume();
        mDefaultRatedPressure = mCalculateGas.getDefaultRatedPressure();
        mDefaultVolume = mCalculateGas.getDefaultVolume();
        mDefaultTankFactor = mMyCalcImperial.getCCF(mDefaultTankVolume, mDefaultRatedPressure);

        // Calculate Pressure
        mDefaultPressure = mMyCalcImperial.convertVolumeToPressure(mDefaultVolume,mDefaultTankFactor);

        // Set the calculated values in the POJO
        mCalculateGas.setDefaultTankFactor(MyFunctions.roundDown(mDefaultTankFactor,5));
        mCalculateGas.setDefaultPressure(MyFunctions.roundUp(mDefaultPressure,1));
    }

    private void calculateImperialVolume() {
        // Get all entered values first
        mDefaultPressure = mCalculateGas.getDefaultPressure();
        mDefaultTankVolume = mCalculateGas.getDefaultTankVolume();
        mDefaultRatedPressure = mCalculateGas.getDefaultRatedPressure();
        mDefaultTankFactor = mMyCalcImperial.getCCF(mDefaultTankVolume, mDefaultRatedPressure);

        // Calculate Volume
        mDefaultVolume = mMyCalcImperial.convertPressureToVolume(mDefaultPressure,mDefaultTankFactor);

        // Set the calculated values in the POJO
        mCalculateGas.setDefaultTankFactor(MyFunctions.roundDown(mDefaultTankFactor,5));
        mCalculateGas.setDefaultVolume(MyFunctions.roundUp(mDefaultVolume,2));
    }

    private void calculateMetricPressure() {
        // Get all entered values first
        mDefaultTankVolume = mCalculateGas.getDefaultTankVolume();
        mDefaultVolume = mCalculateGas.getDefaultVolume();

        // Calculate Pressure
        mDefaultPressure = mMyCalcMetric.convertVolumeToPressure(mDefaultVolume,mDefaultTankVolume);

        // Set the calculated values in the POJO
        mCalculateGas.setDefaultPressure(MyFunctions.roundUp(mDefaultPressure,1));
    }

    private void calculateMetricVolume() {
        // Get all entered values first
        mDefaultTankVolume = mCalculateGas.getDefaultTankVolume();
        mDefaultPressure = mCalculateGas.getDefaultPressure();

        // Calculate Volume
        mDefaultVolume = mMyCalcMetric.convertPressureToVolume(mDefaultPressure,mDefaultTankVolume);

        // Set the calculated values in the POJO
        mCalculateGas.setDefaultVolume(MyFunctions.roundUp(mDefaultVolume,2));
    }

    private void convertToImperialPressure() {
        // Convert bar to psi
        mOtherPressure = mMyCalcImperial.convertBarToPsi(mDefaultPressure);

        // Set the calculated converted values in the POJO
        mCalculateGas.setOtherPressure(MyFunctions.roundUp(mOtherPressure,1));
    }

    private void convertToImperialVolume() {
        // Convert liter to cuft
        mOtherPressure = mCalculateGas.getOtherPressure();
        mOtherTankVolume = mCalculateGas.getOtherTankVolume();
        Double mOtherRatedPressure = mCalculateGas.getOtherRatedPressure();
        Double mOtherTankFactor = mMyCalcImperial.getCCF(mOtherTankVolume, mOtherRatedPressure);

        // Calculate Volume
        mOtherVolume = mMyCalcImperial.convertPressureToVolume(mOtherPressure, mOtherTankFactor);

        // Set the calculated converted values in the POJO
        mCalculateGas.setOtherVolume(MyFunctions.roundUp(mOtherVolume,2));
    }

    private void convertToMetricPressure() {
        // Convert psi to bar
        mOtherPressure = mMyCalcMetric.convertPsiToBar(mDefaultPressure);

        // Set the calculated converted values in the POJO
        mCalculateGas.setOtherPressure(MyFunctions.roundUp(mOtherPressure,1));
    }

    private void convertToMetricVolume() {
        mOtherTankVolume = mCalculateGas.getOtherTankVolume();

        // Convert cuft to liter
        mOtherVolume = mMyCalcMetric.convertPressureToVolume(mOtherPressure,mOtherTankVolume);

        // Set the calculated converted values in the POJO
        mCalculateGas.setOtherVolume(MyFunctions.roundUp(mOtherVolume,2));
    }

    private void clear() {
        // Reset the Default
        mCalculateGas.setDefaultPressure(MyConstants.ZERO_D);
        mCalculateGas.setDefaultTankVolume(MyConstants.ZERO_D);
        mCalculateGas.setDefaultRatedPressure(MyConstants.ZERO_D);
        mCalculateGas.setDefaultTankFactor(MyConstants.ZERO_D);
        mCalculateGas.setDefaultVolume(MyConstants.ZERO_D);

        mBinding.defaultPressure.setText("0.0");
        mBinding.defaultTankVolume.setText("0.0");
        mBinding.defaultRatedPressure.setText("0.0");
        mBinding.defaultTankFactor.setText("0.0");
        mBinding.defaultVolume.setText("0.0");

        // Reset the Other
        mCalculateGas.setOtherPressure(MyConstants.ZERO_D);
        mCalculateGas.setOtherTankVolume(MyConstants.ZERO_D);
        mCalculateGas.setOtherRatedPressure(MyConstants.ZERO_D);
        mCalculateGas.setOtherTankFactor(MyConstants.ZERO_D);
        mCalculateGas.setOtherVolume(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultPressure, false);
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
        String otherPressure = mBinding.otherPressure.getText().toString();
        String otherPressureLbl = mBinding.otherPressureLbl.getText().toString();
        String otherTankVolume = mBinding.otherTankVolume.getText().toString();
        String otherTankVolumeLbl = mBinding.otherTankVolumeLbl.getText().toString();
        String otherRatedPressure = mBinding.otherRatedPressure.getText().toString();
        int otherRatedPressureVisibility = mBinding.otherRatedPressure.getVisibility();
        String otherRatedPressureLbl = mBinding.otherRatedPressureLbl.getText().toString();
        String otherTankFactor = mBinding.otherTankFactor.getText().toString();
        int otherTankFactorVisibility = mBinding.otherTankFactor.getVisibility();
        String otherTankFactorLbl = mBinding.otherTankFactorLbl.getText().toString();
        String otherVolume = mBinding.otherVolume.getText().toString();
        String otherVolumeLbl = mBinding.otherVolumeLbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());
        mBinding.otherPressure.setText(mBinding.defaultPressure.getText().toString());
        mBinding.otherPressureLbl.setText(mBinding.defaultPressureLbl.getText().toString());
        mBinding.otherTankVolume.setText(mBinding.defaultTankVolume.getText().toString());
        mBinding.otherTankVolumeLbl.setText(mBinding.defaultTankVolumeLbl.getText().toString());
        mBinding.otherRatedPressure.setText(mBinding.defaultRatedPressure.getText().toString());
        mBinding.otherRatedPressure.setVisibility(mBinding.defaultRatedPressure.getVisibility());
        mBinding.otherRatedPressureLbl.setText(mBinding.defaultRatedPressureLbl.getText().toString());
        mBinding.otherTankFactor.setText(mBinding.defaultTankFactor.getText().toString());
        mBinding.otherTankFactorLbl.setText(mBinding.defaultTankFactorLbl.getText().toString());
        mBinding.otherTankFactor.setVisibility(mBinding.defaultTankFactor.getVisibility());
        mBinding.otherVolume.setText(mBinding.defaultVolume.getText().toString());
        mBinding.otherVolumeLbl.setText(mBinding.defaultVolumeLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);
        mBinding.defaultPressure.setText(otherPressure);
        mBinding.defaultPressureLbl.setText(otherPressureLbl);
        mBinding.defaultTankVolume.setText(otherTankVolume);
        mBinding.defaultTankVolumeLbl.setText(otherTankVolumeLbl);
        mBinding.defaultRatedPressure.setText(otherRatedPressure);
        mBinding.defaultRatedPressure.setVisibility(otherRatedPressureVisibility);
        mBinding.defaultRatedPressureLbl.setText(otherRatedPressureLbl);
        mBinding.defaultTankFactor.setText(otherTankFactor);
        mBinding.defaultTankFactor.setVisibility(otherTankFactorVisibility);
        mBinding.defaultTankFactorLbl.setText(otherTankFactorLbl);
        mBinding.defaultVolume.setText(otherVolume);
        mBinding.defaultVolumeLbl.setText(otherVolumeLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateGasActivity) {
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
