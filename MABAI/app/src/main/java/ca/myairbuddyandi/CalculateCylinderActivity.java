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

import ca.myairbuddyandi.databinding.CalculateCylinderActivityBinding;

/**
 * Created by Michel on 2020-09-15.
 * Hold all of the logic for the CalculateCylinderActivity class
 */

public class CalculateCylinderActivity extends AppCompatActivity {
    // Static
    private static final String LOG_TAG = "CalculateCylinderActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateCylinder mCalculateCylinder= new CalculateCylinder();
    private CalculateCylinderActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private Double mDefaultRatedVolume;
    private String mDefaultUnit;

    // Other
    private Double mOtherRatedVolume;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_cylinder_activity);

        mCalculateCylinder.mBinding = mBinding;

        mBinding.setCalculateCylinder(mCalculateCylinder);

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

        mBinding.calculateCylinderActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_cylinder));
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
            calculateMetricVolume();
        } else {
            calculateImperialVolume();
        }

        mBinding.otherRatedVolume.setText(String.valueOf(mCalculateCylinder.getOtherRatedVolume()));

        requestFocus(mBinding.defaultRatedVolume, false);
    }

    private void calculateMetricVolume() {
        // Get all entered values first
        mDefaultRatedVolume = mCalculateCylinder.getDefaultRatedVolume();
        Double mDefaultRatedPressure = mCalculateCylinder.getDefaultRatedPressure();

        // Calculate Metric Volume
        mOtherRatedVolume = mMyCalcMetric.convertCylinderImperialVolume(mDefaultRatedVolume, mDefaultRatedPressure);

        // Set the calculated values in the POJO
        mCalculateCylinder.setOtherRatedVolume(MyFunctions.roundUp(mOtherRatedVolume,1));
    }

    private void calculateImperialVolume() {
        // Get all entered values first
        mDefaultRatedVolume = mCalculateCylinder.getDefaultRatedVolume();
        Double mOtherRatedPressure = mCalculateCylinder.getOtherRatedPressure();

        // Calculate Imperial Volume
        mOtherRatedVolume = mMyCalcImperial.convertCylinderMetricVolume(mDefaultRatedVolume, mOtherRatedPressure);

        // Set the calculated values in the POJO
        mCalculateCylinder.setOtherRatedVolume(MyFunctions.roundUp(mOtherRatedVolume,1));
    }

    private void clear() {
        // Reset the Default
        mCalculateCylinder.setDefaultRatedVolume(MyConstants.ZERO_D);
        mCalculateCylinder.setDefaultRatedPressure(MyConstants.ZERO_D);

        mBinding.defaultRatedVolume.setText("0.0");
        mBinding.defaultRatedPressure.setText("0.0");

        // Reset the Other
        mCalculateCylinder.setOtherRatedVolume(MyConstants.ZERO_D);
        mCalculateCylinder.setOtherRatedPressure(MyConstants.ZERO_D);

        mBinding.otherRatedVolume.setText("0.0");
        mBinding.otherRatedPressure.setText("0.0");

        requestFocus(mBinding.defaultRatedVolume, false);
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

        String otherRatedVolume = mBinding.otherRatedVolume.getText().toString();
        String otherRatedVolumeLbl = mBinding.otherRatedVolumeLbl.getText().toString();
        String otherRatedPressure = mBinding.otherRatedPressure.getText().toString();
        String otherRatedPressureLbl = mBinding.otherRatedPressureLbl.getText().toString();


        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherRatedVolume.setText(mBinding.defaultRatedVolume.getText().toString());
        mBinding.otherRatedVolumeLbl.setText(mBinding.defaultRatedVolumeLbl.getText().toString());
        mBinding.otherRatedPressure.setText(mBinding.defaultRatedPressure.getText().toString());
        mBinding.otherRatedPressureLbl.setText(mBinding.defaultRatedPressureLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultRatedVolume.setText(otherRatedVolume);
        mBinding.defaultRatedVolumeLbl.setText(otherRatedVolumeLbl);
        mBinding.defaultRatedPressure.setText(otherRatedPressure);
        mBinding.defaultRatedPressureLbl.setText(otherRatedPressureLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateCylinderActivity) {
            requestFocus(view, true);
        }
    }

    private void hideShowAttributes() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // otherRatedPressure (bar) is not needed
            mBinding.otherRatedPressure.setVisibility(View.INVISIBLE);
            mBinding.otherRatedPressureLbl.setVisibility(View.INVISIBLE);
            // Show defaultRatedPressure (psi)
            mBinding.defaultRatedPressure.setVisibility(View.VISIBLE);
            mBinding.defaultRatedPressureLbl.setVisibility(View.VISIBLE);
        } else {
            // defaultRatedPressure (bar) is not needed
            mBinding.defaultRatedPressure.setVisibility(View.INVISIBLE);
            mBinding.defaultRatedPressureLbl.setVisibility(View.INVISIBLE);
            // Show otherRatedPressure (psi)
            mBinding.otherRatedPressure.setVisibility(View.VISIBLE);
            mBinding.otherRatedPressureLbl.setVisibility(View.VISIBLE);
        }
    }

    private void switchToOtherUnit() {
        // Set the new Default Unit
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            mDefaultUnit = MyConstants.METRIC;
        } else {
            mDefaultUnit = MyConstants.IMPERIAL;
        }

        hideShowAttributes();
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

        hideShowAttributes();
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.apply();
    }
}
