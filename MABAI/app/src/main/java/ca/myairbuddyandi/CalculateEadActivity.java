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

import ca.myairbuddyandi.databinding.CalculateEadActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateEadActivity class
 */

public class CalculateEadActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateEadActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateEad mCalculateEad = new CalculateEad();
    private CalculateEadActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private boolean mDefaultSalinity; // true = Salt = 0 position, false = Fresh = 1 position
    private Double mDefaultEad;
    private Double mDefaultMixO2;
    private Double mDefaultDepth;
    private Double mDefaultMixHe;
    private Double mDefaultMixN2;
    private String mDefaultUnit;

    // Other
    private Double mOtherEad;
    private Double mOtherDepth;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_ead_activity);

        mCalculateEad.mBinding = mBinding;

        mBinding.setCalculateEad(mCalculateEad);

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
        mCalculateEad.setAdapterDefaultSalinity(adapterDefaultSalinity);

        //Set the data in the Spinner Trimix
        String[] itemsDefaultTrimix = getResources().getStringArray(R.array.trimix_arrays);
        ArrayAdapter<String> adapterDefaultTrimix = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsDefaultTrimix);
        adapterDefaultTrimix.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculateEad.setAdapterDefaultTrimix(adapterDefaultTrimix);

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateEadActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_ead));
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
            calculateImperialEad();
            copyToOtherO2();
            copyToOtherHe();
            copyToOtherN2();
            convertToMetricDepth();
            convertToMetricEad();
        } else {
            calculateMetricEad();
            copyToOtherO2();
            copyToOtherHe();
            copyToOtherN2();
            convertToImperialDepth();
            convertToImperialEad();
        }

        mBinding.defaultMixO2.setText(String.valueOf(mCalculateEad.getDefaultMixO2()));
        mBinding.defaultMixHe.setText(String.valueOf(mCalculateEad.getDefaultMixHe()));
        mBinding.defaultEad.setText(String.valueOf(mCalculateEad.getDefaultEad()));
        mBinding.defaultDepth.setText(String.valueOf(mCalculateEad.getDefaultDepth()));
        mBinding.defaultMixN2.setText(String.valueOf(mCalculateEad.getDefaultMixN2()));

        requestFocus(mBinding.defaultMixO2, false);
    }

    // My functions

    // Main entry

    private void calculateImperialEad() {
        // Get all entered values first
        mDefaultSalinity = mCalculateEad.getDefaultSalinity();
        mDefaultMixO2 = mCalculateEad.getDefaultMixO2();
        mDefaultMixHe = mCalculateEad.getDefaultMixHe();
        mDefaultMixN2 = 100.0 - mDefaultMixO2 - mDefaultMixHe;
        mDefaultDepth = mCalculateEad.getDefaultDepth();

        // Calculate EAD
        mDefaultEad = Math.max(0,mMyCalcImperial.getEad(mDefaultMixN2,mDefaultDepth,mDefaultSalinity));

        // Set the calculated values in the POJO
        mCalculateEad.setDefaultMixN2(mDefaultMixN2);
        mCalculateEad.setDefaultEad(MyFunctions.roundUp(mDefaultEad,1));
    }


    private void calculateMetricEad() {
        // Get all entered values first
        mDefaultSalinity = mCalculateEad.getDefaultSalinity();
        mDefaultMixO2 = mCalculateEad.getDefaultMixO2();
        mDefaultMixHe = mCalculateEad.getDefaultMixHe();
        mDefaultMixN2 = 100.0 - mDefaultMixO2 - mDefaultMixHe;
        mDefaultDepth = mCalculateEad.getDefaultDepth();

        // Calculate EAD
        mDefaultEad = Math.max(0,mMyCalcMetric.getEad(mDefaultMixN2,mDefaultDepth,mDefaultSalinity));

        // Set the calculated values in the POJO
        mCalculateEad.setDefaultMixN2(mDefaultMixN2);
        mCalculateEad.setDefaultEad(MyFunctions.roundUp(mDefaultEad,1));
    }

    // Calculate, convert and copy functions

    private void convertToImperialDepth() {
        // Convert meter to feet
        mOtherDepth = mMyCalcImperial.convertMeterToFeet(mDefaultDepth);

        // Set the calculated converted values in the POJO
        mCalculateEad.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
    }

    private void convertToImperialEad() {
        // Convert meter to feet
        mOtherEad = mMyCalcImperial.convertMeterToFeet(mDefaultEad);

        // Set the calculated converted values in the POJO
        mCalculateEad.setOtherEad(MyFunctions.roundUp(mOtherEad,1));
    }

    private void convertToMetricDepth() {
        // Convert feet to meter
        mOtherDepth = mMyCalcMetric.convertFeetToMeter(mDefaultDepth);

        // Set the calculated converted values in the POJO
        mCalculateEad.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
    }

    private void convertToMetricEad() {
        // Convert feet to meter
        mOtherEad = mMyCalcMetric.convertFeetToMeter(mDefaultEad);

        // Set the calculated converted values in the POJO
        mCalculateEad.setOtherEad(MyFunctions.roundUp(mOtherEad,1));
    }

    private void copyToOtherHe() {
        // Same value as metric
        mCalculateEad.setOtherMixHe(mDefaultMixHe);
    }

    private void copyToOtherO2() {
        // Same value as metric
        mCalculateEad.setOtherMixO2(mDefaultMixO2);
    }

    private void copyToOtherN2() {
        // Same value as metric
        mCalculateEad.setOtherMixN2(mDefaultMixN2);
    }

    // End of calculate, convert and copy functions

    private void clear() {
        // Reset the Default

        mCalculateEad.setDefaultMixO2(MyConstants.ZERO_D);
        mCalculateEad.setDefaultMixHe(MyConstants.ZERO_D);
        mCalculateEad.setDefaultMixN2(MyConstants.ZERO_D);
        mCalculateEad.setDefaultDepth(MyConstants.ZERO_D);
        mCalculateEad.setDefaultEad(MyConstants.ZERO_D);

        mBinding.defaultMixO2.setText("0.0");
        mBinding.defaultMixHe.setText("0.0");
        mBinding.defaultMixN2.setText("0.0");
        mBinding.defaultDepth.setText("0.0");
        mBinding.defaultEad.setText("0.0");

        // Reset the Other

        mCalculateEad.setOtherMixO2(MyConstants.ZERO_D);
        mCalculateEad.setOtherMixHe(MyConstants.ZERO_D);
        mCalculateEad.setOtherMixN2(MyConstants.ZERO_D);
        mCalculateEad.setOtherDepth(MyConstants.ZERO_D);
        mCalculateEad.setOtherEad(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultMixO2, false);
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

        String otherMixO2 = mBinding.otherMixO2.getText().toString();
        String otherMixHe = mBinding.otherMixHe.getText().toString();
        String otherMixN2 = mBinding.otherMixN2.getText().toString();
        String otherDepth = mBinding.otherDepth.getText().toString();
        String otherDepthLbl = mBinding.otherDepthLbl.getText().toString();
        String otherEad = mBinding.otherEad.getText().toString();
        String otherEadLbl = mBinding.otherEadLbl.getText().toString();


        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherMixO2.setText(mBinding.defaultMixO2.getText().toString());
        mBinding.otherMixHe.setText(mBinding.defaultMixHe.getText().toString());
        mBinding.otherMixN2.setText(mBinding.defaultMixN2.getText().toString());
        mBinding.otherDepth.setText(mBinding.defaultDepth.getText().toString());
        mBinding.otherDepthLbl.setText(mBinding.defaultDepthLbl.getText().toString());
        mBinding.otherEad.setText(mBinding.defaultEad.getText().toString());
        mBinding.otherEadLbl.setText(mBinding.defaultEadLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultMixO2.setText(otherMixO2);
        mBinding.defaultMixHe.setText(otherMixHe);
        mBinding.defaultMixN2.setText(otherMixN2);
        mBinding.defaultDepth.setText(otherDepth);
        mBinding.defaultDepthLbl.setText(otherDepthLbl);
        mBinding.defaultEad.setText(otherEad);
        mBinding.defaultEadLbl.setText(otherEadLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateEadActivity) {
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
        mCalculateEad.setDefaultSalinity(mDefaultSalinity);
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            mCalculateEad.setDefaultSalinityPosition(0);
        } else {
            mCalculateEad.setDefaultSalinityPosition(1);
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putBoolean(getString(R.string.code_default_salinity), mCalculateEad.getDefaultSalinity());
        edit.apply();
    }
}
