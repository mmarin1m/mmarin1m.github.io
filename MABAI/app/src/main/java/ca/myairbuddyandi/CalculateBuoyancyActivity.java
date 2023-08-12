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

import ca.myairbuddyandi.databinding.CalculateBuoyancyActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateBuoyancyActivity class
 */

public class CalculateBuoyancyActivity extends AppCompatActivity {
    // Static
    private static final String LOG_TAG = "CalculateBuoyancyActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateBuoyancy mCalculateBuoyancy = new CalculateBuoyancy();
    private CalculateBuoyancyActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private boolean mDefaultSalinity; // true = Salt = 0 position, false = Fresh = 1 position
    private Double mDefaultWeight;
    private Double mDefaultDisplacement;
    private Double mDefaultBuoyancy;
    private String mDefaultUnit;
    // Other
    private Double mOtherWeight;
    private Double mOtherDisplacement;
    private Double mOtherBuoyancy;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_buoyancy_activity);

        mCalculateBuoyancy.mBinding = mBinding;

        mBinding.setCalculateBuoyancy(mCalculateBuoyancy);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        mBinding.buoyancyButton.setOnClickListener(view -> calculateBuoyancy());

        mBinding.buoyancyButton2.setOnClickListener(view -> calculateBuoyancy());

        mBinding.weightButton.setOnClickListener(view -> calculateWeight());

        mBinding.weightButton2.setOnClickListener(view -> calculateWeight());

        mBinding.clearButton.setOnClickListener(view -> clear());

        //Set the data in the Spinner Salinity
        String[] itemsDefaultSalinity = getResources().getStringArray(R.array.salinity_arrays);
        ArrayAdapter<String> adapterDefaultSalinity = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsDefaultSalinity);
        adapterDefaultSalinity.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculateBuoyancy.setAdapterDefaultSalinity(adapterDefaultSalinity);

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateBuoyancyActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_buoyancy));
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

    private void calculateBuoyancy() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialBuoyancy();
            convertToMetricWeight();
            convertToMetricDisplacement();
            convertToMetricBuoyancy();
        } else {
            calculateMetricBuoyancy();
            convertToImperialWeight();
            convertToImperialDisplacement();
            convertToImperialBuoyancy();
        }

        mBinding.defaultWeight.setText(String.valueOf(mCalculateBuoyancy.getDefaultWeight()));
        mBinding.defaultDisplacement.setText(String.valueOf(mCalculateBuoyancy.getDefaultDisplacement()));
        mBinding.defaultBuoyancy.setText(String.valueOf(mCalculateBuoyancy.getDefaultBuoyancy()));

        requestFocus(mBinding.defaultWeight, false);
    }

    private void calculateWeight() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialWeight();
            convertToMetricWeight();
            convertToMetricDisplacement();
            mDefaultBuoyancy = MyConstants.ZERO_D;
            convertToMetricBuoyancy();
        } else {
            calculateMetricWeight();
            convertToImperialWeight();
            convertToImperialDisplacement();
            mDefaultBuoyancy = MyConstants.ZERO_D;
            convertToImperialBuoyancy();
        }

        mBinding.defaultWeight.setText(String.valueOf(mCalculateBuoyancy.getDefaultWeight()));
        mBinding.defaultDisplacement.setText(String.valueOf(mCalculateBuoyancy.getDefaultDisplacement()));
        mBinding.defaultBuoyancy.setText(String.valueOf(mCalculateBuoyancy.getDefaultBuoyancy()));

        requestFocus(mBinding.defaultWeight, false);
    }

    private void calculateImperialBuoyancy() {
        // Get all entered values first
        mDefaultSalinity = mCalculateBuoyancy.getDefaultSalinity();
        mDefaultWeight = mCalculateBuoyancy.getDefaultWeight();
        mDefaultDisplacement = mCalculateBuoyancy.getDefaultDisplacement();

        // Calculate Buoyancy
        mDefaultBuoyancy = mMyCalcImperial.getBuoyancy(mDefaultWeight,mDefaultDisplacement,mDefaultSalinity);

        // Set the calculated values in the POJO
        mCalculateBuoyancy.setDefaultBuoyancy(MyFunctions.roundUp(mDefaultBuoyancy,2));
    }

    private void calculateImperialWeight() {
        // Get all entered values first
        mDefaultSalinity = mCalculateBuoyancy.getDefaultSalinity();
        mDefaultDisplacement = mCalculateBuoyancy.getDefaultDisplacement();

        // Calculate Weight
        mDefaultWeight = mMyCalcImperial.getWeight(mDefaultDisplacement,mDefaultSalinity);

        // Set the calculated values in the POJO
        mCalculateBuoyancy.setDefaultWeight(MyFunctions.roundUp(mDefaultWeight,2));
        mCalculateBuoyancy.setDefaultBuoyancy(MyFunctions.roundUp(MyConstants.ZERO_D,2));
    }

    private void calculateMetricBuoyancy() {
        // Get all entered values first
        mDefaultSalinity = mCalculateBuoyancy.getDefaultSalinity();
        mDefaultWeight = mCalculateBuoyancy.getDefaultWeight();
        mDefaultDisplacement = mCalculateBuoyancy.getDefaultDisplacement();

        // Calculate Buoyancy
        mDefaultBuoyancy = mMyCalcMetric.getBuoyancy(mDefaultWeight,mDefaultDisplacement,mDefaultSalinity);

        // Set the calculated values in the POJO
        mCalculateBuoyancy.setDefaultBuoyancy(MyFunctions.roundUp(mDefaultBuoyancy,2));
    }

    private void calculateMetricWeight() {
        // Get all entered values first
        mDefaultSalinity = mCalculateBuoyancy.getDefaultSalinity();
        mDefaultDisplacement = mCalculateBuoyancy.getDefaultDisplacement();

        // Calculate Weight
        mDefaultWeight = mMyCalcMetric.getWeight(mDefaultDisplacement,mDefaultSalinity);

        // Set the calculated values in the POJO
        mCalculateBuoyancy.setDefaultWeight(MyFunctions.roundUp(mDefaultWeight,2));
        mCalculateBuoyancy.setDefaultBuoyancy(MyFunctions.roundUp(MyConstants.ZERO_D,2));
    }

    private void convertToImperialWeight() {
        // Convert kilogram to pound
        mOtherWeight = mMyCalcImperial.convertKilogramToPound(mDefaultWeight);

        // Set the calculated converted values in the POJO
        mCalculateBuoyancy.setOtherWeight(MyFunctions.roundUp(mOtherWeight,2));
    }

    private void convertToImperialDisplacement() {
        // Convert liter to cubic foot
        mOtherDisplacement = mMyCalcImperial.convertLiterToCuft(mDefaultDisplacement);

        // Set the calculated converted values in the POJO
        mCalculateBuoyancy.setOtherDisplacement(MyFunctions.roundUp(mOtherDisplacement,2));
    }

    private void convertToImperialBuoyancy() {
        // Convert liter to cubic foot
        mOtherBuoyancy = mMyCalcImperial.convertLiterToCuft(mDefaultBuoyancy);

        // Set the calculated converted values in the POJO
        mCalculateBuoyancy.setOtherBuoyancy(MyFunctions.roundUp(mOtherBuoyancy,2));
    }

    private void convertToMetricWeight() {
        // Convert pounds to kilogram
        mOtherWeight = mMyCalcMetric.convertPoundToKilogram(mDefaultWeight);

        // Set the calculated converted values in the POJO
        mCalculateBuoyancy.setOtherWeight(MyFunctions.roundUp(mOtherWeight,2));
    }

    private void convertToMetricDisplacement() {
        // Same cubit feet to liter
        mOtherDisplacement = mMyCalcMetric.convertCuftToLiter(mDefaultDisplacement);

        // Set the calculated converted values in the POJO
        mCalculateBuoyancy.setOtherDisplacement(MyFunctions.roundUp(mOtherDisplacement,2));
    }

    private void convertToMetricBuoyancy() {
        // Same cubit feet to liter
        mOtherBuoyancy = mMyCalcMetric.convertCuftToLiter(mDefaultBuoyancy);

        // Set the calculated converted values in the POJO
        mCalculateBuoyancy.setOtherBuoyancy(MyFunctions.roundUp(mOtherBuoyancy,2));
    }

    private void clear() {
        // Reset the Default
        mCalculateBuoyancy.setDefaultWeight(MyConstants.ZERO_D);
        mCalculateBuoyancy.setDefaultDisplacement(MyConstants.ZERO_D);
        mCalculateBuoyancy.setDefaultBuoyancy(MyConstants.ZERO_D);

        mBinding.defaultWeight.setText("0.0");
        mBinding.defaultDisplacement.setText("0.0");
        mBinding.defaultBuoyancy.setText("0.0");

        // Reset the Other
        mCalculateBuoyancy.setOtherWeight(MyConstants.ZERO_D);
        mCalculateBuoyancy.setOtherDisplacement(MyConstants.ZERO_D);
        mCalculateBuoyancy.setOtherBuoyancy(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultWeight,false);
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

        String otherWeight = mBinding.otherWeight.getText().toString();
        String otherWeightLbl = mBinding.otherWeightLbl.getText().toString();
        String otherDisplacement = mBinding.otherDisplacement.getText().toString();
        String otherDisplacementLbl = mBinding.otherDisplacementLbl.getText().toString();
        String otherBuoyancy= mBinding.otherBuoyancy.getText().toString();
        String otherBuoyancyLbl = mBinding.otherBuoyancyLbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherWeight.setText(mBinding.defaultWeight.getText().toString());
        mBinding.otherWeightLbl.setText(mBinding.defaultWeightLbl.getText().toString());
        mBinding.otherDisplacement.setText(mBinding.defaultDisplacement.getText().toString());
        mBinding.otherDisplacementLbl.setText(mBinding.defaultDisplacementLbl.getText().toString());
        mBinding.otherBuoyancy.setText(mBinding.defaultBuoyancy.getText().toString());
        mBinding.otherBuoyancyLbl.setText(mBinding.defaultBuoyancyLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultWeight.setText(otherWeight);
        mBinding.defaultWeightLbl.setText(otherWeightLbl);
        mBinding.defaultDisplacement.setText(otherDisplacement);
        mBinding.defaultDisplacementLbl.setText(otherDisplacementLbl);
        mBinding.defaultBuoyancy.setText(otherBuoyancy);
        mBinding.defaultBuoyancyLbl.setText(otherBuoyancyLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateBuoyancyActivity) {
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

        // Last values stored as always the same as the Preferred Unit
        mDefaultSalinity = preferences.getBoolean(getString(R.string.code_default_salinity), true);

        // Set the values in the POJO
        mCalculateBuoyancy.setDefaultSalinity(mDefaultSalinity);
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            mCalculateBuoyancy.setDefaultSalinityPosition(0);
        } else {
            mCalculateBuoyancy.setDefaultSalinityPosition(1);
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putBoolean(getString(R.string.code_default_salinity), mCalculateBuoyancy.getDefaultSalinity());
        edit.apply();
    }
}
