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

import ca.myairbuddyandi.databinding.CalculateCharlesLawActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateCharlesLawActivity class
 */

public class CalculateCharlesLawActivity extends AppCompatActivity {
    // Static
    private static final String LOG_TAG = "CalculateCharlesLawActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateCharlesLaw mCalculateCharlesLaw = new CalculateCharlesLaw();
    private CalculateCharlesLawActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private boolean mDefaultCharlesConstant; // true = Constant pressure = 0 position, false = Constant volume = 1 position
    private Double mDefaultT1;
    private Double mDefaultT2;
    private Double mDefaultV1;
    private Double mDefaultV2;
    private String mDefaultUnit;
    // Other
    private Double mOtherT1;
    private Double mOtherT2;
    private Double mOtherV1;
    private Double mOtherV2;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_charles_law_activity);

        mCalculateCharlesLaw.setContext(this);

        mCalculateCharlesLaw.mBinding = mBinding;

        mBinding.setCalculateCharlesLaw(mCalculateCharlesLaw);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        //

        mBinding.pv1Button.setOnClickListener(view -> cv1());

        mBinding.pv1Button2.setOnClickListener(view -> cv1());

        //

        mBinding.pv2Button.setOnClickListener(view -> cv2());

        mBinding.pv2Button2.setOnClickListener(view -> cv2());

        //

        mBinding.t1Button.setOnClickListener(view -> ct1());

        mBinding.t1Button2.setOnClickListener(view -> ct1());

        //

        mBinding.t2Button.setOnClickListener(view -> ct2());

        mBinding.t2Button2.setOnClickListener(view -> ct2());

        //

        mBinding.clearButton.setOnClickListener(view -> clear());


        //Set the data in the Spinner Charles Constant
        String[] itemsDefaultCharlesConstant = getResources().getStringArray(R.array.charles_constant_arrays);
        ArrayAdapter<String> adapterDefaultCharlesConstant = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsDefaultCharlesConstant);
        adapterDefaultCharlesConstant.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculateCharlesLaw.setAdapterDefaultCharlesConstant(adapterDefaultCharlesConstant);

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateCharlesLawActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_charles_law));
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

    private void cv1() {
        mDefaultCharlesConstant = mCalculateCharlesLaw.getDefaultCharlesConstant();
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Get all entered values first
            mDefaultV2 = mCalculateCharlesLaw.getDefaultPV2();
            mDefaultT1 = mCalculateCharlesLaw.getDefaultT1();
            mDefaultT2 = mCalculateCharlesLaw.getDefaultT2();

            if (mDefaultCharlesConstant) {
                // // Calculating volume with constant pressure
                mDefaultV1 = MyFunctions.roundUp(mMyCalcImperial.getCV1(mDefaultV2, mDefaultT1, mDefaultT2),2);
                convertToMetricV1();
                convertToMetricV2();
            } else {
                // Calculating pressure with constant volume
                mDefaultV1 = MyFunctions.roundUp(mMyCalcImperial.getCP1(mDefaultV2, mDefaultT1, mDefaultT2),2);
                convertToMetricP1();
                convertToMetricP2();
            }

            mCalculateCharlesLaw.setDefaultPV1(mDefaultV1);

            mBinding.defaultPV1.setText(String.valueOf(mCalculateCharlesLaw.getDefaultPV1()));

            // Calculate temperature Metric values
            convertToMetricT1();
            convertToMetricT2();
        } else {
            // Get all entered values first
            mDefaultV2 = mCalculateCharlesLaw.getDefaultPV2();
            mDefaultT1 = mCalculateCharlesLaw.getDefaultT1();
            mDefaultT2 = mCalculateCharlesLaw.getDefaultT2();

            if (mDefaultCharlesConstant) {
                // // Calculating volume with constant pressure
                mDefaultV1 = MyFunctions.roundUp(mMyCalcMetric.getCV1(mDefaultV2, mDefaultT1, mDefaultT2),2);
                convertToImperialV1();
                convertToImperialV2();
            } else {
                // Calculating pressure with constant volume
                mDefaultV1 = MyFunctions.roundUp(mMyCalcMetric.getCP1(mDefaultV2, mDefaultT1, mDefaultT2),2);
                convertToImperialP1();
                convertToImperialP2();
            }

            mCalculateCharlesLaw.setDefaultPV1(mDefaultV1);

            mBinding.defaultPV1.setText(String.valueOf(mCalculateCharlesLaw.getDefaultPV1()));

            // Calculate temperature Imperial values
            convertToImperialT1();
            convertToImperialT2();
        }
    }

    private void cv2() {
        mDefaultCharlesConstant = mCalculateCharlesLaw.getDefaultCharlesConstant();
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Get all entered values first
            mDefaultV1 = mCalculateCharlesLaw.getDefaultPV1();
            mDefaultT1 = mCalculateCharlesLaw.getDefaultT1();
            mDefaultT2 = mCalculateCharlesLaw.getDefaultT2();

            if (mDefaultCharlesConstant) {
                // // Calculating volume with constant pressure
                mDefaultV2 =  MyFunctions.roundUp(mMyCalcImperial.getCV2(mDefaultV1,mDefaultT1,mDefaultT2),2);
                convertToMetricV1();
                convertToMetricV2();
            } else {
                // Calculating pressure with constant volume
                mDefaultV2 =  MyFunctions.roundUp(mMyCalcImperial.getCP2(mDefaultV1,mDefaultT1,mDefaultT2),2);
                convertToMetricP1();
                convertToMetricP2();
            }

            mCalculateCharlesLaw.setDefaultPV2(mDefaultV2);

            mBinding.defaultPV2.setText(String.valueOf(mCalculateCharlesLaw.getDefaultPV2()));

            // Calculate temperature Metric values
            convertToMetricT1();
            convertToMetricT2();
        } else {
            // Get all entered values first
            mDefaultV1 = mCalculateCharlesLaw.getDefaultPV1();
            mDefaultT1 = mCalculateCharlesLaw.getDefaultT1();
            mDefaultT2 = mCalculateCharlesLaw.getDefaultT2();

            if (mDefaultCharlesConstant) {
                // Calculating volume with constant pressure
                mDefaultV2 =  MyFunctions.roundUp(mMyCalcMetric.getCV2(mDefaultV1,mDefaultT1,mDefaultT2),2);
                convertToImperialV1();
                convertToImperialV2();
            } else {
                // Calculating pressure with constant volume
                mDefaultV2 =  MyFunctions.roundUp(mMyCalcMetric.getCP2(mDefaultV1,mDefaultT1,mDefaultT2),2);
                convertToImperialP1();
                convertToImperialP2();
            }

            mCalculateCharlesLaw.setDefaultPV2(mDefaultV2);

            mBinding.defaultPV2.setText(String.valueOf(mCalculateCharlesLaw.getDefaultPV2()));

            // Calculate temperature Imperial values
            convertToImperialT1();
            convertToImperialT2();
        }
    }

    private void ct1() {
        mDefaultCharlesConstant = mCalculateCharlesLaw.getDefaultCharlesConstant();
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Get all entered values first
            mDefaultV1 = mCalculateCharlesLaw.getDefaultPV1();
            mDefaultV2 = mCalculateCharlesLaw.getDefaultPV2();
            mDefaultT2 = mCalculateCharlesLaw.getDefaultT2();

            if (mDefaultCharlesConstant) {
                // Calculating temperature with constant pressure
                mDefaultT1 = MyFunctions.roundUp(mMyCalcImperial.getCVT1(mDefaultV1,mDefaultV2,mDefaultT2),2);
                convertToMetricV1();
                convertToMetricV2();
            } else {
                // Calculating temperature with constant volume
                mDefaultT1 = MyFunctions.roundUp(mMyCalcImperial.getCPT1(mDefaultV1,mDefaultV2,mDefaultT2),2);
                convertToMetricP1();
                convertToMetricP2();
            }

            mCalculateCharlesLaw.setDefaultT1(mDefaultT1);

            mBinding.defaultT1.setText(String.valueOf(mCalculateCharlesLaw.getDefaultT1()));

            // Calculate temperature Metric values
            convertToMetricT1();
            convertToMetricT2();
        } else {
            // Get all entered values first
            mDefaultV1 =mCalculateCharlesLaw.getDefaultPV1();
            mDefaultV2 = mCalculateCharlesLaw.getDefaultPV2();
            mDefaultT2 = mCalculateCharlesLaw.getDefaultT2();

            if (mDefaultCharlesConstant) {
                // Calculating volume with constant pressure
                mDefaultT1 =  MyFunctions.roundUp(mMyCalcMetric.getCVT1(mDefaultV1,mDefaultV2,mDefaultT2),2);
                convertToImperialV1();
                convertToImperialV2();
            } else {
                // Calculating pressure with constant volume
                mDefaultT1 =  MyFunctions.roundUp(mMyCalcMetric.getCPT1(mDefaultV1,mDefaultV2,mDefaultT2),2);
                convertToImperialP1();
                convertToImperialP2();
            }

            mCalculateCharlesLaw.setDefaultT1(mDefaultT1);

            mBinding.defaultT1.setText(String.valueOf(mCalculateCharlesLaw.getDefaultT1()));

            // Calculate temperature Imperial values
            convertToImperialT1();
            convertToImperialT2();
        }
    }

    private void ct2() {
        mDefaultCharlesConstant = mCalculateCharlesLaw.getDefaultCharlesConstant();
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Get all entered values first
            mDefaultV1 = mCalculateCharlesLaw.getDefaultPV1();
            mDefaultV2 = mCalculateCharlesLaw.getDefaultPV2();
            mDefaultT1 = mCalculateCharlesLaw.getDefaultT1();

            if (mDefaultCharlesConstant) {
                // Calculating temperature with constant pressure
                mDefaultT2 = MyFunctions.roundUp(mMyCalcImperial.getCVT2(mDefaultV1,mDefaultV2,mDefaultT1),2);
                convertToMetricV1();
                convertToMetricV2();
            } else {
                // Calculating temperature with constant volume
                mDefaultT2 = MyFunctions.roundUp(mMyCalcImperial.getCPT2(mDefaultV1,mDefaultV2,mDefaultT1),2);
                convertToMetricP1();
                convertToMetricP2();
            }

            mCalculateCharlesLaw.setDefaultT2(mDefaultT2);

            mBinding.defaultT2.setText(String.valueOf(mCalculateCharlesLaw.getDefaultT2()));

            // Calculate temperature Metric values
            convertToMetricT1();
            convertToMetricT2();
        } else {
            // Get all entered values first
            mDefaultV1 = mCalculateCharlesLaw.getDefaultPV1();
            mDefaultV2 = mCalculateCharlesLaw.getDefaultPV2();
            mDefaultT1 = mCalculateCharlesLaw.getDefaultT1();

            if (mDefaultCharlesConstant) {
                // Calculating volume with constant pressure
                mDefaultT2 = MyFunctions.roundUp(mMyCalcMetric.getCVT2(mDefaultV1,mDefaultV2,mDefaultT1),2);
                convertToImperialV1();
                convertToImperialV2();
            } else {
                // Calculating pressure with constant volume
                mDefaultT2 = MyFunctions.roundUp(mMyCalcMetric.getCPT2(mDefaultV1,mDefaultV2,mDefaultT1),2);
                convertToImperialP1();
                convertToImperialP2();
            }

            mCalculateCharlesLaw.setDefaultT2(mDefaultT2);

            mBinding.defaultT2.setText(String.valueOf(mCalculateCharlesLaw.getDefaultT2()));

            // Calculate all Imperial values
            convertToImperialT1();
            convertToImperialT2();
        }
    }

    // Conversion to Metric

    public void convertToMetricP1() {
        // P1 is in psi
        mOtherV1 = MyFunctions.roundUp(mMyCalcMetric.convertPsiToBar(mDefaultV1),2);
        mCalculateCharlesLaw.setOtherPV1(mOtherV1);
    }

    public void convertToMetricP2() {
        // P2 is in psi
        mOtherV2 = MyFunctions.roundUp(mMyCalcMetric.convertPsiToBar(mDefaultV2),2);
        mCalculateCharlesLaw.setOtherPV2(mOtherV2);
    }

    public void convertToMetricV1() {
        // V1 is in ft3
        mOtherV1 = MyFunctions.roundUp(mMyCalcMetric.convertCuftToLiter(mDefaultV1),2);
        mCalculateCharlesLaw.setOtherPV1(mOtherV1);
    }

    public void convertToMetricV2() {
        // V2 is in ft3
        mOtherV2 = MyFunctions.roundUp(mMyCalcMetric.convertCuftToLiter(mDefaultV2),2);
        mCalculateCharlesLaw.setOtherPV2(mOtherV2);
    }

    public void convertToMetricT1() {
        // T1 is in Fahrenheit
        mOtherT1 = MyFunctions.roundUp(mMyCalcMetric.convertFahrenheitToCelsius(mDefaultT1),2);
        mCalculateCharlesLaw.setOtherT1(mOtherT1);
    }

    public void convertToMetricT2() {
        // T2 is already in Fahrenheit
        mOtherT2 = MyFunctions.roundUp(mMyCalcMetric.convertFahrenheitToCelsius(mDefaultT2),2);
        mCalculateCharlesLaw.setOtherT2(mOtherT2);
    }

    // Conversion to Imperial

    public void convertToImperialP1() {
        // P1 is in bar
        mOtherV1 = MyFunctions.roundUp(mMyCalcImperial.convertBarToPsi(mDefaultV1),2);
        mCalculateCharlesLaw.setOtherPV1(mOtherV1);
    }

    public void convertToImperialP2() {
        // P2 is in bar
        mOtherV2 = MyFunctions.roundUp(mMyCalcImperial.convertBarToPsi(mDefaultV2),2);
        mCalculateCharlesLaw.setOtherPV2(mOtherV2);
    }

    public void convertToImperialV1() {
        // V1 is in l
        mOtherV1 = MyFunctions.roundUp(mMyCalcImperial.convertLiterToCuft(mDefaultV1),2);
        mCalculateCharlesLaw.setOtherPV1(mOtherV1);
    }

    public void convertToImperialV2() {
        // V2 is in l
        mOtherV2 = MyFunctions.roundUp(mMyCalcImperial.convertLiterToCuft(mDefaultV2),2);
        mCalculateCharlesLaw.setOtherPV2(mOtherV2);
    }

    public void convertToImperialT1() {
        // T1 is in Celsius
        mOtherT1 = MyFunctions.roundUp(mMyCalcImperial.convertCelsiusToFahrenheit(mDefaultT1),2);
        mCalculateCharlesLaw.setOtherT1(mOtherT1);
    }

    public void convertToImperialT2() {
        // T2 is in Celsius
        mOtherT2 = MyFunctions.roundUp(mMyCalcImperial.convertCelsiusToFahrenheit(mDefaultT2), 2);
        mCalculateCharlesLaw.setOtherT2(mOtherT2);
    }

    private void clear() {
        // Reset the Default
        mCalculateCharlesLaw.setDefaultPV1(MyConstants.ZERO_D);
        mCalculateCharlesLaw.setDefaultPV2(MyConstants.ZERO_D);
        mCalculateCharlesLaw.setDefaultT1(MyConstants.ZERO_D);
        mCalculateCharlesLaw.setDefaultT2(MyConstants.ZERO_D);

        mBinding.defaultPV1.setText("0.0");
        mBinding.defaultPV2.setText("0.0");
        mBinding.defaultT1.setText("0.0");
        mBinding.defaultT2.setText("0.0");

        // Reset the Other
        mCalculateCharlesLaw.setOtherPV1(MyConstants.ZERO_D);
        mCalculateCharlesLaw.setOtherPV2(MyConstants.ZERO_D);
        mCalculateCharlesLaw.setOtherT1(MyConstants.ZERO_D);
        mCalculateCharlesLaw.setOtherT2(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultPV1, false);
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

        String otherV1 = mBinding.otherPV1.getText().toString();
        String otherV1Lbl = mBinding.otherPV1Lbl.getText().toString();
        String otherV2 = mBinding.otherPV2.getText().toString();
        String otherV2Lbl = mBinding.otherPV2Lbl.getText().toString();
        String otherT1 = mBinding.otherT1.getText().toString();
        String otherT1Lbl = mBinding.otherT1Lbl.getText().toString();
        String otherT2 = mBinding.otherT2.getText().toString();
        String otherT2Lbl = mBinding.otherT2Lbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherPV1.setText(mBinding.defaultPV1.getText().toString());
        mBinding.otherPV1Lbl.setText(mBinding.defaultPV1Lbl.getText().toString());
        mBinding.otherPV2.setText(mBinding.defaultPV2.getText().toString());
        mBinding.otherPV2Lbl.setText(mBinding.defaultPV2Lbl.getText().toString());
        mBinding.otherT1.setText(mBinding.defaultT1.getText().toString());
        mBinding.otherT1Lbl.setText(mBinding.defaultT1Lbl.getText().toString());
        mBinding.otherT2.setText(mBinding.defaultT2.getText().toString());
        mBinding.otherT2Lbl.setText(mBinding.defaultT2Lbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultPV1.setText(otherV1);
        mBinding.defaultPV1Lbl.setText(otherV1Lbl);
        mBinding.defaultPV2.setText(otherV2);
        mBinding.defaultPV2Lbl.setText(otherV2Lbl);
        mBinding.defaultT1.setText(otherT1);
        mBinding.defaultT1Lbl.setText(otherT1Lbl);
        mBinding.defaultT2.setText(otherT2);
        mBinding.defaultT2Lbl.setText(otherT2Lbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateCharlesLawActivity) {
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
        mCalculateCharlesLaw.setDefaultUnit(mDefaultUnit);
    }

    private void readDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // If no default unit (Last Used unit) exists e.g. first time ever, use the Phone unit
        mDefaultUnit = (preferences.getString(getString(R.string.code_default_unit), mUnit));
        mCalculateCharlesLaw.setDefaultUnit(mDefaultUnit);

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
        mDefaultCharlesConstant = preferences.getBoolean(getString(R.string.code_default_charles_constant), true);

        // Set the values in the POJO
        mCalculateCharlesLaw.setDefaultCharlesConstant(mDefaultCharlesConstant);
        if (mDefaultCharlesConstant) {
            // true = Volume = 0 position
            mCalculateCharlesLaw.setDefaultCharlesConstantPosition(MyConstants.ZERO_I);
        } else {
            mCalculateCharlesLaw.setDefaultCharlesConstantPosition(MyConstants.ONE_I);
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putBoolean(getString(R.string.code_default_charles_constant), mCalculateCharlesLaw.getDefaultCharlesConstant());
        edit.apply();
    }
}
