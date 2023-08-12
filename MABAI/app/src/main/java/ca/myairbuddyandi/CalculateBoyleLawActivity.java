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

import ca.myairbuddyandi.databinding.CalculateBoyleLawActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateBoyleLawActivity class
 */

public class CalculateBoyleLawActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateBoyleLawActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateBoyleLaw mCalculateBoyleLaw = new CalculateBoyleLaw();
    private CalculateBoyleLawActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private Double mDefaultP1;
    private Double mDefaultP2;
    private Double mDefaultV1;
    private Double mDefaultV2;
    private String mDefaultUnit;

    // Other
    private Double mOtherP1;
    private Double mOtherP2;
    private Double mOtherV1;
    private Double mOtherV2;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_boyle_law_activity);

        mCalculateBoyleLaw.mBinding = mBinding;

        mBinding.setCalculateBoyleLaw(mCalculateBoyleLaw);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        //

        mBinding.p1Button.setOnClickListener(view -> p1());

        mBinding.p1Button2.setOnClickListener(view -> p1());

        //

        mBinding.v1Button.setOnClickListener(view -> v1());

        mBinding.v1Button2.setOnClickListener(view -> v1());

        //

        mBinding.p2Button.setOnClickListener(view -> p2());

        mBinding.p2Button2.setOnClickListener(view -> p2());

        //

        mBinding.v2Button.setOnClickListener(view -> v2());

        mBinding.v2Button2.setOnClickListener(view -> v2());

        //

        mBinding.clearButton.setOnClickListener(view -> clear());

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateBoyleLawActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_boyle_law));
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

    private void p1() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Get all entered values first
            mDefaultV1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV1(),2);
            mDefaultP2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP2(),2);
            mDefaultV2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV2(),2);

            // Reset the values with the decimal place
            mCalculateBoyleLaw.setDefaultV1(mDefaultV1);
            mCalculateBoyleLaw.setDefaultP2(mDefaultP2);
            mCalculateBoyleLaw.setDefaultV2(mDefaultV2);

            // Convert psi to ata
            Double defaultAtaP2 = mMyCalcImperial.convertPressureToAta(mDefaultP2);

            mDefaultP1 = mMyCalcImperial.getP1(mDefaultV1,defaultAtaP2,mDefaultV2);

            // Convert ata to psi
            mDefaultP1 = MyFunctions.roundUp(mMyCalcImperial.convertAtaToPressure(mDefaultP1),2);

            mCalculateBoyleLaw.setDefaultP1(mDefaultP1);

            mBinding.defaultP1.setText(String.valueOf(mCalculateBoyleLaw.getDefaultP1()));

            // Calculate all Metric values
            convertToMetricP1();
            convertToMetricV1();
            convertToMetricP2();
            convertToMetricV2();
        } else {
            // Get all entered values first
            mDefaultV1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV1(),2);
            mDefaultP2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP2(),2);
            mDefaultV2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV2(),2);

            // Reset the values with the decimal place
            mCalculateBoyleLaw.setDefaultV1(mDefaultV1);
            mCalculateBoyleLaw.setDefaultP2(mDefaultP2);
            mCalculateBoyleLaw.setDefaultV2(mDefaultV2);

            // In metric, P2 is already in bar, no need to convert
            mDefaultP1 = mMyCalcMetric.getP1(mDefaultV1,mDefaultP2,mDefaultV2);

            mCalculateBoyleLaw.setDefaultP1(mDefaultP1);

            mBinding.defaultP1.setText(String.valueOf(mCalculateBoyleLaw.getDefaultP1()));

            // Calculate all Imperial values
            convertToImperialP1();
            convertToImperialV1();
            convertToImperialP2();
            convertToImperialV2();
        }

        requestFocus(mBinding.defaultP1,false);
    }

    private void v1() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Get all entered values first
            mDefaultP1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP1(), 2);
            mDefaultP2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP2(), 2);
            mDefaultV2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV2(), 2);

            mCalculateBoyleLaw.setDefaultP1(mDefaultP1);
            mCalculateBoyleLaw.setDefaultP2(mDefaultP2);
            mCalculateBoyleLaw.setDefaultV2(mDefaultV2);

            // Convert psi to ata
            Double defaultAtaP1 = mMyCalcImperial.convertPressureToAta(mDefaultP1);
            Double defaultAtaP2 = mMyCalcImperial.convertPressureToAta(mDefaultP2);

            mDefaultV1 = MyFunctions.roundUp(mMyCalcImperial.getV1(defaultAtaP1, defaultAtaP2, mDefaultV2),2);

            mCalculateBoyleLaw.setDefaultV1(mDefaultV1);

            mBinding.defaultV1.setText(String.valueOf(mCalculateBoyleLaw.getDefaultV1()));

            // Calculate all Metric values
            convertToMetricP1();
            convertToMetricV1();
            convertToMetricP2();
            convertToMetricV2();
        } else {
            // Get all entered values first
            mDefaultP1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP1(), 2);
            mDefaultP2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP2(), 2);
            mDefaultV2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV2(), 2);

            mCalculateBoyleLaw.setDefaultP1(mDefaultP1);
            mCalculateBoyleLaw.setDefaultP2(mDefaultP2);
            mCalculateBoyleLaw.setDefaultV2(mDefaultV2);

            // P1 and P2 are already in bar

            mDefaultV1 = MyFunctions.roundUp(mMyCalcMetric.getV1(mDefaultP1, mDefaultP2, mDefaultV2),2);

            mCalculateBoyleLaw.setDefaultV1(mDefaultV1);

            mBinding.defaultV1.setText(String.valueOf(mCalculateBoyleLaw.getDefaultV1()));

            // Calculate all Imperial values
            convertToImperialP1();
            convertToImperialV1();
            convertToImperialP2();
            convertToImperialV2();
        }

        requestFocus(mBinding.defaultV1, false);
    }

    private void p2() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Get all entered values first
            mDefaultP1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP1(),2);
            mDefaultV1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV1(),2);
            mDefaultV2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV2(),2);

            mCalculateBoyleLaw.setDefaultP1(mDefaultP1);
            mCalculateBoyleLaw.setDefaultV1(mDefaultV1);
            mCalculateBoyleLaw.setDefaultV2(mDefaultV2);

            // Convert psi to ata
            Double defaultAtaP1 = mMyCalcImperial.convertPressureToAta(mDefaultP1);
            Double defaultAtaP2 = mMyCalcImperial.getP2(mDefaultV2,defaultAtaP1,mDefaultV1);

            // Convert ata to psi
            mDefaultP2 = MyFunctions.roundUp(mMyCalcImperial.convertAtaToPressure(defaultAtaP2),2);

            mCalculateBoyleLaw.setDefaultP2(mDefaultP2);

            mBinding.defaultP2.setText(String.valueOf(mCalculateBoyleLaw.getDefaultP2()));

            // Calculate all Metric values
            convertToMetricP1();
            convertToMetricV1();
            convertToMetricP2();
            convertToMetricV2();
        } else {
            // Get all entered values first
            mDefaultP1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP1(),2);
            mDefaultV1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV1(),2);
            mDefaultV2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV2(),2);

            mCalculateBoyleLaw.setDefaultP1(mDefaultP1);
            mCalculateBoyleLaw.setDefaultV1(mDefaultV1);
            mCalculateBoyleLaw.setDefaultV2(mDefaultV2);

            // In metric, P1 is already in bar, no need to convert
            mDefaultP2 = mMyCalcMetric.getP2(mDefaultV2,mDefaultP1,mDefaultV1);

            mCalculateBoyleLaw.setDefaultP2(mDefaultP2);

            mBinding.defaultP2.setText(String.valueOf(mCalculateBoyleLaw.getDefaultP2()));

            // Calculate all Imperial values
            convertToImperialP1();
            convertToImperialV1();
            convertToImperialP2();
            convertToImperialV2();
        }

        requestFocus(mBinding.defaultP2, false);
    }

    private void v2() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Get all entered values first
            mDefaultP1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP1(),2);
            mDefaultV1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV1(),2);
            mDefaultP2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP2(),2);

            mCalculateBoyleLaw.setDefaultP1(mDefaultP1);
            mCalculateBoyleLaw.setDefaultV1(mDefaultV1);
            mCalculateBoyleLaw.setDefaultP2(mDefaultP2);

            // Convert psi to ata
            Double defaultAtaP1 = mMyCalcImperial.convertPressureToAta(mDefaultP1);
            Double defaultAtaP2 = mMyCalcImperial.convertPressureToAta(mDefaultP2);

            mDefaultV2 =  MyFunctions.roundUp(mMyCalcImperial.getV2(defaultAtaP2,defaultAtaP1,mDefaultV1),2);

            mCalculateBoyleLaw.setDefaultV2(mDefaultV2);

            requestFocus(mBinding.defaultV2, false);

            mBinding.defaultV2.setText(String.valueOf(mCalculateBoyleLaw.getDefaultV2()));

            // Calculate all Metric values
            convertToMetricP1();
            convertToMetricV1();
            convertToMetricP2();
            convertToMetricV2();
        } else {
            // Get all entered values first
            mDefaultP1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP1(),2);
            mDefaultV1 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultV1(),2);
            mDefaultP2 = MyFunctions.roundUp(mCalculateBoyleLaw.getDefaultP2(),2);

            mCalculateBoyleLaw.setDefaultP1(mDefaultP1);
            mCalculateBoyleLaw.setDefaultV1(mDefaultV1);
            mCalculateBoyleLaw.setDefaultP2(mDefaultP2);

            // // P1 and P2 are already in bar

            mDefaultV2 =  MyFunctions.roundUp(mMyCalcMetric.getV2(mDefaultP2,mDefaultP1,mDefaultV1),2);

            mCalculateBoyleLaw.setDefaultV2(mDefaultV2);

            requestFocus(mBinding.defaultV2,false);

            mBinding.defaultV2.setText(String.valueOf(mCalculateBoyleLaw.getDefaultV2()));

            // Calculate all Imperial values
            convertToImperialP1();
            convertToImperialV1();
            convertToImperialP2();
            convertToImperialV2();
        }
    }

    private void clear() {
        // Reset the Default
        mCalculateBoyleLaw.setDefaultP1(MyConstants.ZERO_D);
        mCalculateBoyleLaw.setDefaultV1(MyConstants.ZERO_D);
        mCalculateBoyleLaw.setDefaultP2(MyConstants.ZERO_D);
        mCalculateBoyleLaw.setDefaultV2(MyConstants.ZERO_D);

        mBinding.defaultP1.setText("0.0");
        mBinding.defaultV1.setText("0.0");
        mBinding.defaultP2.setText("0.0");
        mBinding.defaultV2.setText("0.0");

        // Reset the Other
        mCalculateBoyleLaw.setOtherP1(MyConstants.ZERO_D);
        mCalculateBoyleLaw.setOtherV1(MyConstants.ZERO_D);
        mCalculateBoyleLaw.setOtherP2(MyConstants.ZERO_D);
        mCalculateBoyleLaw.setOtherV2(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultP1,false);
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
        String otherP1 = mBinding.otherP1.getText().toString();
        String otherP1Lbl = mBinding.otherP1Lbl.getText().toString();
        String otherV1 = mBinding.otherV1.getText().toString();
        String otherV1Lbl = mBinding.otherV1Lbl.getText().toString();
        String otherP2 = mBinding.otherP2.getText().toString();
        String otherP2Lbl = mBinding.otherP2Lbl.getText().toString();
        String otherV2 = mBinding.otherV2.getText().toString();
        String otherV2Lbl = mBinding.otherV2Lbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());
        mBinding.otherP1.setText(mBinding.defaultP1.getText().toString());
        mBinding.otherP1Lbl.setText(mBinding.defaultP1Lbl.getText().toString());
        mBinding.otherV1.setText(mBinding.defaultV1.getText().toString());
        mBinding.otherV1Lbl.setText(mBinding.defaultV1Lbl.getText().toString());
        mBinding.otherP2.setText(mBinding.defaultP2.getText().toString());
        mBinding.otherP2Lbl.setText(mBinding.defaultP2Lbl.getText().toString());
        mBinding.otherV2.setText(mBinding.defaultV2.getText().toString());
        mBinding.otherV2Lbl.setText(mBinding.defaultV2Lbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);
        mBinding.defaultP1.setText(otherP1);
        mBinding.defaultP1Lbl.setText(otherP1Lbl);
        mBinding.defaultV1.setText(otherV1);
        mBinding.defaultV1Lbl.setText(otherV1Lbl);
        mBinding.defaultP2.setText(otherP2);
        mBinding.defaultP2Lbl.setText(otherP2Lbl);
        mBinding.defaultV2.setText(otherV2);
        mBinding.defaultV2Lbl.setText(otherV2Lbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateBoyleLawActivity) {
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

    public void convertToMetricP1() {
        // P1 is in psi
        mOtherP1 = MyFunctions.roundUp(mMyCalcMetric.convertPsiToBar(mDefaultP1),2);
        mCalculateBoyleLaw.setOtherP1(mOtherP1);
    }

    public void convertToMetricV1() {
        mOtherV1 = MyFunctions.roundUp(mMyCalcMetric.convertCuftToLiter(mDefaultV1),2);
        mCalculateBoyleLaw.setOtherV1(mOtherV1);
    }

    public void convertToMetricP2() {
        // P2 is already in ata
        mOtherP2 = MyFunctions.roundUp(mMyCalcMetric.convertPsiToBar(mDefaultP2),2);
        mCalculateBoyleLaw.setOtherP2(mOtherP2);

    }

    public void convertToMetricV2() {
        mOtherV2 = MyFunctions.roundUp(mMyCalcMetric.convertCuftToLiter(mDefaultV2),2);
        mCalculateBoyleLaw.setOtherV2(mOtherV2);
    }

    public void convertToImperialP1() {
        // P1 is already in bar
        mOtherP1 = MyFunctions.roundUp(mMyCalcImperial.convertBarToPsi(mDefaultP1),2);
        mCalculateBoyleLaw.setOtherP1(mOtherP1);
    }

    public void convertToImperialV1() {
        mOtherV1 = MyFunctions.roundUp(mMyCalcImperial.convertLiterToCuft(mDefaultV1),2);
        mCalculateBoyleLaw.setOtherV1(mOtherV1);
    }

    public void convertToImperialP2() {
        // P2 is already in bar
        mOtherP2 = MyFunctions.roundUp(mMyCalcImperial.convertBarToPsi(mDefaultP2),2);
        mCalculateBoyleLaw.setOtherP2(mOtherP2);

    }

    public void convertToImperialV2() {
        mOtherV2 = MyFunctions.roundUp(mMyCalcImperial.convertLiterToCuft(mDefaultV2),2);
        mCalculateBoyleLaw.setOtherV2(mOtherV2);
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
