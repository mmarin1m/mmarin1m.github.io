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

import ca.myairbuddyandi.databinding.CalculateEndActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateEndActivity class
 */

public class CalculateEndActivity extends AppCompatActivity {
    // Static
    private static final String LOG_TAG = "CalculateEndActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateEnd mCalculateEnd = new CalculateEnd();
    private CalculateEndActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private boolean mDefaultSalinity; // true = Salt = 0 position, false = Fresh = 1 position
    private Double mDefaultEnd;
    private Double mDefaultDepth;
    private Double mDefaultHe;
    private String mDefaultUnit;
    // Other
    private Double mOtherEnd;
    private Double mOtherDepth;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_end_activity);

        mCalculateEnd.mBinding = mBinding;

        mBinding.setCalculateEnd(mCalculateEnd);

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
        mCalculateEnd.setAdapterDefaultSalinity(adapterDefaultSalinity);

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateEndActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_end));
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
            calculateImperialEnd();
            convertToMetricHe();
            convertToMetricDepth();
            convertToMetricEnd();
        } else {
            calculateMetricEnd();
            convertToImperialHe();
            convertToImperialDepth();
            convertToImperialEnd();
        }

        mBinding.defaultEnd.setText(String.valueOf(mCalculateEnd.getDefaultEnd()));
        mBinding.defaultDepth.setText(String.valueOf(mCalculateEnd.getDefaultDepth()));
        mBinding.defaultHe.setText(String.valueOf(mCalculateEnd.getDefaultHe()));

        requestFocus(mBinding.defaultHe, false);
    }

    private void calculateImperialEnd() {
        // Get all entered values first
        mDefaultSalinity = mCalculateEnd.getDefaultSalinity();
        mDefaultHe = mCalculateEnd.getDefaultHe();
        mDefaultDepth = mCalculateEnd.getDefaultDepth();

        // Calculate END
        mDefaultEnd = Math.max(0,mMyCalcImperial.getEnd(mDefaultHe,mDefaultDepth,mDefaultSalinity));

        // Set the calculated values in the POJO
        mCalculateEnd.setDefaultEnd(MyFunctions.roundUp(mDefaultEnd,1));
    }

    private void calculateMetricEnd() {
        // Get all entered values first
        mDefaultSalinity = mCalculateEnd.getDefaultSalinity();
        mDefaultHe = mCalculateEnd.getDefaultHe();
        mDefaultDepth = mCalculateEnd.getDefaultDepth();

        // Calculate END
        mDefaultEnd = Math.max(0,mMyCalcMetric.getEnd(mDefaultHe,mDefaultDepth,mDefaultSalinity));

        // Set the calculated values in the POJO
        mCalculateEnd.setDefaultEnd(MyFunctions.roundUp(mDefaultEnd,1));
    }

    private void convertToImperialEnd() {
        // Convert meter to feet
        mOtherEnd = mMyCalcImperial.convertMeterToFeet(mDefaultEnd);

        // Set the calculated converted values in the POJO
        mCalculateEnd.setOtherEnd(MyFunctions.roundUp(mOtherEnd,1));
    }

    private void convertToImperialDepth() {
        // Convert meter to feet
        mOtherDepth = mMyCalcImperial.convertMeterToFeet(mDefaultDepth);

        // Set the calculated converted values in the POJO
        mCalculateEnd.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
    }

    private void convertToImperialHe() {
        // Same value as metric
        mCalculateEnd.setOtherHe(mDefaultHe);
    }

    private void convertToMetricEnd() {
        // Convert feet to meter
        mOtherEnd = mMyCalcMetric.convertFeetToMeter(mDefaultEnd);

        // Set the calculated converted values in the POJO
        mCalculateEnd.setOtherEnd(MyFunctions.roundUp(mOtherEnd,1));
    }

    private void convertToMetricDepth() {
        // Convert feet to meter
        mOtherDepth = mMyCalcMetric.convertFeetToMeter(mDefaultDepth);

        // Set the calculated converted values in the POJO
        mCalculateEnd.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
    }

    private void convertToMetricHe() {
        // Same value as imperial
        mCalculateEnd.setOtherHe(mDefaultHe);
    }

    private void clear() {
        // Reset the Default
        mCalculateEnd.setDefaultEnd(MyConstants.ZERO_D);
        mCalculateEnd.setDefaultDepth(MyConstants.ZERO_D);
        mCalculateEnd.setDefaultHe(MyConstants.ZERO_D);

        mBinding.defaultEnd.setText("0.0");
        mBinding.defaultDepth.setText("0.0");
        mBinding.defaultHe.setText("0.0");

        // Reset the Other
        mCalculateEnd.setOtherEnd(MyConstants.ZERO_D);
        mCalculateEnd.setOtherDepth(MyConstants.ZERO_D);
        mCalculateEnd.setOtherHe(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultHe, false);
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

        String otherHe = mBinding.otherHe.getText().toString();
        String otherDepth = mBinding.otherDepth.getText().toString();
        String otherDepthLbl = mBinding.otherDepthLbl.getText().toString();
        String otherEnd = mBinding.otherEnd.getText().toString();
        String otherEndLbl = mBinding.otherEndLbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherHe.setText(mBinding.defaultHe.getText().toString());
        mBinding.otherDepth.setText(mBinding.defaultDepth.getText().toString());
        mBinding.otherDepthLbl.setText(mBinding.defaultDepthLbl.getText().toString());
        mBinding.otherEnd.setText(mBinding.defaultEnd.getText().toString());
        mBinding.otherEndLbl.setText(mBinding.defaultEndLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultHe.setText(otherHe);
        mBinding.defaultDepth.setText(otherDepth);
        mBinding.defaultDepthLbl.setText(otherDepthLbl);
        mBinding.defaultEnd.setText(otherEnd);
        mBinding.defaultEndLbl.setText(otherEndLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateEndActivity) {
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
        mCalculateEnd.setDefaultSalinity(mDefaultSalinity);
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            mCalculateEnd.setDefaultSalinityPosition(0);
        } else {
            mCalculateEnd.setDefaultSalinityPosition(1);
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putBoolean(getString(R.string.code_default_salinity), mCalculateEnd.getDefaultSalinity());
        edit.apply();
    }
}
