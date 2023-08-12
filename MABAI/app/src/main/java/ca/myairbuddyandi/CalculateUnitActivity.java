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

import ca.myairbuddyandi.databinding.CalculateUnitActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateUnitActivity class
 */

public class CalculateUnitActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateUnitActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateUnit mConvertUnit = new CalculateUnit();
    private CalculateUnitActivityBinding mBinding = null;
    private final MyCalc mMyCalc = new MyCalc();
    private String mDefaultUnit;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_unit_activity);

        mConvertUnit.mBinding = mBinding;

        mBinding.setCalculateUnit(mConvertUnit);

        // Set the listeners
        mBinding.calculateButton.setOnClickListener(view -> calculate());

        mBinding.calculateButton2.setOnClickListener(view -> calculate());

        mBinding.calculateButton3.setOnClickListener(view -> calculate());

        mBinding.calculateButton4.setOnClickListener(view -> calculate());

        mBinding.calculateButton5.setOnClickListener(view -> calculate());

        mBinding.calculateButton6.setOnClickListener(view -> calculate());

        mBinding.calculateButton7.setOnClickListener(view -> calculate());

        mBinding.clearButton.setOnClickListener(view -> clear());

        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateUnitActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_unit));
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
        // NOTE: Leave as is
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Convert from Imperial to Metric
            // ata
            Double ata = Double.valueOf(mBinding.defaultAta.getText().toString()); // In ata
            ata = mMyCalc.convertAtaToBar(ata); //  In bar
            mConvertUnit.setOtherAta(MyFunctions.roundUp(ata,2));

            // Pressure
            Double pressure = Double.valueOf(mBinding.defaultPressure.getText().toString());
            pressure = mMyCalc.convertPsiToBar(pressure);
            mConvertUnit.setOtherPressure(MyFunctions.roundUp(pressure,1));

            // Depth
            Double depth = Double.valueOf(mBinding.defaultDepth.getText().toString());
            depth = mMyCalc.convertFeetToMeter(depth);
            mConvertUnit.setOtherDepth(MyFunctions.roundUp(depth,1));

            // Temperature
            Double temperature = Double.valueOf(mBinding.defaultTemperature.getText().toString());
            temperature = mMyCalc.convertFahrenheitToCelsius(temperature);
            mConvertUnit.setOtherTemperature(MyFunctions.roundUp(temperature,1));

            // Weight
            Double weight = Double.valueOf(mBinding.defaultWeight.getText().toString());
            weight = mMyCalc.convertPoundToKilogram(weight);
            mConvertUnit.setOtherWeight(MyFunctions.roundUp(weight,1));

            // 2020/03/27 New unit conversion - Volume
            Double volume = Double.valueOf(mBinding.defaultVolume.getText().toString());
            volume = mMyCalc.convertCuftToLiter(volume);
            mConvertUnit.setOtherVolume(MyFunctions.roundUp(volume,1));
        } else {
            // Convert from Metric to Imperial
            // ata
            Double ata = Double.valueOf(mBinding.defaultAta.getText().toString()); // In bar
            ata = mMyCalc.convertBarToAta(ata); // In ata
            mConvertUnit.setOtherAta(MyFunctions.roundUp(ata,2));

            // Pressure
            Double pressure = Double.valueOf(mBinding.defaultPressure.getText().toString());
            pressure = mMyCalc.convertBarToPsi(pressure);
            mConvertUnit.setOtherPressure(MyFunctions.roundUp(pressure,1));

            // Depth
            Double depth = Double.valueOf(mBinding.defaultDepth.getText().toString());
            depth = mMyCalc.convertMeterToFeet(depth);
            mConvertUnit.setOtherDepth(MyFunctions.roundUp(depth,1));

            // Temperature
            Double temperature = Double.valueOf(mBinding.defaultTemperature.getText().toString());
            temperature = mMyCalc.convertCelsiusToFahrenheit(temperature);
            mConvertUnit.setOtherTemperature(MyFunctions.roundUp(temperature,1));

            // Weight
            Double weight = Double.valueOf(mBinding.defaultWeight.getText().toString());
            weight = mMyCalc.convertKilogramToPound(weight);
            mConvertUnit.setOtherWeight(MyFunctions.roundUp(weight,1));

            // 2020/03/27 New unit conversion - Volume
            double volume = Double.parseDouble(mBinding.defaultVolume.getText().toString());
            volume = mMyCalc.convertLiterToCuft(volume);
            mConvertUnit.setOtherVolume(MyFunctions.roundUp(volume,1));
        }

        mBinding.defaultAta.setText(String.valueOf(mConvertUnit.getDefaultAta()));
        mBinding.defaultDepth.setText(String.valueOf(mConvertUnit.getDefaultDepth()));
        mBinding.defaultPressure.setText(String.valueOf(mConvertUnit.getDefaultPressure()));
        mBinding.defaultTemperature.setText(String.valueOf(mConvertUnit.getDefaultTemperature()));
        mBinding.defaultWeight.setText(String.valueOf(mConvertUnit.getDefaultWeight()));
        mBinding.defaultVolume.setText(String.valueOf(mConvertUnit.getDefaultVolume()));

        requestFocus(mBinding.defaultAta, false);
    }

    private void clear() {
        // Reset the Default
        mConvertUnit.setDefaultAta(MyConstants.ZERO_D);
        mConvertUnit.setDefaultPressure(MyConstants.ZERO_D);
        mConvertUnit.setDefaultDepth(MyConstants.ZERO_D);
        mConvertUnit.setDefaultTemperature(MyConstants.ZERO_D);
        mConvertUnit.setDefaultWeight(MyConstants.ZERO_D);
        mConvertUnit.setDefaultVolume(MyConstants.ZERO_D);

        mBinding.defaultAta.setText("0.0");
        mBinding.defaultDepth.setText("0.0");
        mBinding.defaultPressure.setText("0.0");
        mBinding.defaultTemperature.setText("0.0");
        mBinding.defaultWeight.setText("0.0");
        mBinding.defaultVolume.setText("0.0");

        // Reset the Other
        mConvertUnit.setOtherAta(MyConstants.ZERO_D);
        mConvertUnit.setOtherPressure(MyConstants.ZERO_D);
        mConvertUnit.setOtherDepth(MyConstants.ZERO_D);
        mConvertUnit.setOtherTemperature(MyConstants.ZERO_D);
        mConvertUnit.setOtherWeight(MyConstants.ZERO_D);
        mConvertUnit.setOtherVolume(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultAta, false);
    }

    private void requestFocus(View view, boolean showKeyboard) {
        if (view instanceof EditText) {
            // Only works for EditText
            view.clearFocus();
            view.requestFocus();
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

        String otherAta = mBinding.otherAta.getText() .toString();
        String otherAtaLbl = mBinding.otherAtaLbl.getText().toString();
        String otherPressure = mBinding.otherPressure.getText() .toString();
        String otherPressureLbl = mBinding.otherPressureLbl.getText().toString();
        String otherDepth = mBinding.otherDepth.getText().toString();
        String otherDepthLbl = mBinding.otherDepthLbl.getText().toString();
        String otherTemperature = mBinding.otherTemperature.getText().toString();
        String otherTemperatureLbl = mBinding.otherTemperatureLbl.getText().toString();
        String otherWeight = mBinding.otherWeight.getText().toString();
        String otherWeightLbl = mBinding.otherWeightLbl.getText().toString();
        String otherVolume = mBinding.otherVolume.getText().toString();
        String otherVolumeLbl = mBinding.otherVolumeLbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherAta.setText(mBinding.defaultAta.getText().toString());
        mBinding.otherAtaLbl.setText(mBinding.defaultAtaLbl.getText().toString());
        mBinding.otherPressure.setText(mBinding.defaultPressure.getText().toString());
        mBinding.otherPressureLbl.setText(mBinding.defaultPressureLbl.getText().toString());
        mBinding.otherDepth.setText(mBinding.defaultDepth.getText().toString());
        mBinding.otherDepthLbl.setText(mBinding.defaultDepthLbl.getText().toString());
        mBinding.otherTemperature.setText(mBinding.defaultTemperature.getText().toString());
        mBinding.otherTemperatureLbl.setText(mBinding.defaultTemperatureLbl.getText().toString());
        mBinding.otherWeight.setText(mBinding.defaultWeight.getText().toString());
        mBinding.otherWeightLbl.setText(mBinding.defaultWeightLbl.getText().toString());
        mBinding.otherVolume.setText(mBinding.defaultVolume.getText().toString());
        mBinding.otherVolumeLbl.setText(mBinding.defaultVolumeLbl.getText().toString());

        // Switch Other Side to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultAta.setText(otherAta);
        mBinding.defaultAtaLbl.setText(otherAtaLbl);
        mBinding.defaultPressure.setText(otherPressure);
        mBinding.defaultPressureLbl.setText(otherPressureLbl);
        mBinding.defaultDepth.setText(otherDepth);
        mBinding.defaultDepthLbl.setText(otherDepthLbl);
        mBinding.defaultTemperature.setText(otherTemperature);
        mBinding.defaultTemperatureLbl.setText(otherTemperatureLbl);
        mBinding.defaultWeight.setText(otherWeight);
        mBinding.defaultWeightLbl.setText(otherWeightLbl);
        mBinding.defaultVolume.setText(otherVolume);
        mBinding.defaultVolumeLbl.setText(otherVolumeLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateUnitActivity) {
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