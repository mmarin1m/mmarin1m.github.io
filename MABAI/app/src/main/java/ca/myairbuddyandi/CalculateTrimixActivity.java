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

import java.util.Objects;

import ca.myairbuddyandi.databinding.CalculateTrimixActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateTrimixActivity class
 */

public class CalculateTrimixActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateTrimixActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final AirDA mAirDa = new AirDA(this);
    private final CalculateTrimix mCalculateTrimix = new CalculateTrimix();
    private CalculateTrimixActivityBinding mBinding = null;
    private final MyCalcBlending mMyCalcBlending = new MyCalcBlending();
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);
    private String mDefaultUnit;
    private String mDiverEmail;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_trimix_activity);

        mCalculateTrimix.mBinding = mBinding;

        mBinding.setCalculateTrimix(mCalculateTrimix);

        // Set the listeners
        mBinding.calculateStartButton.setOnClickListener(view -> calculateStart());

        mBinding.calculateDesiredButton.setOnClickListener(view -> calculateDesired());

        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        mBinding.switchHeO2.setOnClickListener(view -> switchHeO2());

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mCalculateTrimix.setMixHeFill(Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.HELIUM_MIX, "100.0")))));
        mCalculateTrimix.setMixO2Fill(Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.OXYGEN_MIX, "100.0")))));
        mCalculateTrimix.setMixTopOff(Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.TOP_OFF_MIX, "20.9")))));

        // Set the labels
        String mHeliumAddLbl = String.format(getResources().getString(R.string.lbl_add_helium), mCalculateTrimix.getMixHeFill().toString() + "%");
        String mOxygenAddLbl = String.format(getResources().getString(R.string.lbl_add_oxygen), mCalculateTrimix.getMixO2Fill().toString() + "%");
        String mTopOffAddLbl = String.format(getResources().getString(R.string.lbl_add_top_off), mCalculateTrimix.getMixTopOff().toString() + "%");

        mCalculateTrimix.setHeliumAddLbl(mHeliumAddLbl);
        mCalculateTrimix.setOxygenAddLbl(mOxygenAddLbl);
        mCalculateTrimix.setTopOffAddLbl(mTopOffAddLbl);

        mAirDa.open();
        Diver diver = new Diver();
        mAirDa.getDiver(MyConstants.ONE_L,diver);
        mDiverEmail = diver.getEmail();
        mAirDa.close();

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateTrimixActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_trimix));
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

    private void calculateStart() {
        // Get the values
        int pressureHeliumAdd;
        int pressureOxygenAdd;
        Double mixHeStart = mCalculateTrimix.getMixHeStart();
        Double mixO2Start = mCalculateTrimix.getMixO2Start();
        int pressureDesired = mCalculateTrimix.getPressureDesired();
        Double mixHeDesired = mCalculateTrimix.getMixHeDesired();
        Double mixO2Desired = mCalculateTrimix.getMixO2Desired();

        // Reset the previous calculated values to zero
        mCalculateTrimix.setPressureHeliumAdd(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureHeliumTotal(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureOxygenAdd(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureOxygenTotal(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureTopOffAdd(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureTopOffTotal(MyConstants.ZERO_I);

        // Make the red warning invisible
        mBinding.bleedTankLbl.setText(R.string.lbl_bleed_cylinder);
        mBinding.bleedTankLbl.setVisibility(View.INVISIBLE);
        mBinding.bleedTankTotal.setVisibility(View.INVISIBLE);
        mBinding.bleedTankTotalLbl.setVisibility(View.INVISIBLE);

        // Calculate
        if (pressureDesired > MyConstants.ZERO_I) {

            int pressureStart = mMyCalcBlending.getPressureStart(mixO2Start, mixHeStart, pressureDesired, mixO2Desired, mixHeDesired, mCalculateTrimix.getMixTopOff());

            if (pressureStart >= MyConstants.ZERO_I) {
                pressureHeliumAdd = mMyCalcBlending.getPressureHeFill(pressureStart, mixHeStart, pressureDesired, mixHeDesired);
                mCalculateTrimix.setPressureHeliumAdd(pressureHeliumAdd);
                mCalculateTrimix.setPressureHeliumTotal(pressureStart + pressureHeliumAdd);
                int pressureHeStart = pressureStart + pressureHeliumAdd;
                pressureOxygenAdd = mMyCalcBlending.getPressureO2Fill(pressureStart, mixO2Start, pressureDesired, mixO2Desired, mCalculateTrimix.getMixTopOff(), pressureHeStart);
                mCalculateTrimix.setPressureOxygenAdd(pressureOxygenAdd);
                mCalculateTrimix.setPressureOxygenTotal(pressureHeStart + pressureOxygenAdd);
                if (mCalculateTrimix.getPressureOxygenTotal() > pressureDesired) {
                    mCalculateTrimix.setPressureTopOffAdd(MyConstants.ZERO_I);
                    mCalculateTrimix.setPressureTopOffTotal(MyConstants.ZERO_I);
                } else {
                    mCalculateTrimix.setPressureTopOffAdd(pressureDesired - (pressureHeStart + pressureOxygenAdd));
                    mCalculateTrimix.setPressureTopOffTotal(pressureDesired);
                }
                // We do not know if we need to bleed the tank since the user entered 0 as the starting pressure
                // Assume that we need to bleed the tank
                // Make the red warning visible
                mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotal.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotalLbl.setVisibility(View.VISIBLE);
                mCalculateTrimix.setPressureBleedTankTotal(pressureStart);
            } else {
                mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                mBinding.bleedTankLbl.setText(R.string.msg_cannot_calculate_blend);
            }
        }
    }

    private void calculateDesired() {
        // Get the values
        int pressureHeliumAdd;
        int pressureOxygenAdd;
        int pressureStart = mCalculateTrimix.getPressureStart();
        Double mixHeStart = mCalculateTrimix.getMixHeStart();
        Double mixO2Start = mCalculateTrimix.getMixO2Start();
        int pressureDesired = mCalculateTrimix.getPressureDesired();
        Double mixO2Desired = mCalculateTrimix.getMixO2Desired();
        Double mixHeDesired = mCalculateTrimix.getMixHeDesired();

        // Reset the previous calculated values to zero
        mCalculateTrimix.setPressureHeliumAdd(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureHeliumTotal(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureOxygenAdd(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureOxygenTotal(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureTopOffAdd(MyConstants.ZERO_I);
        mCalculateTrimix.setPressureTopOffTotal(MyConstants.ZERO_I);

        // Make the red warning invisible
        mBinding.bleedTankLbl.setText(R.string.lbl_bleed_cylinder);
        mBinding.bleedTankLbl.setVisibility(View.INVISIBLE);
        mBinding.bleedTankTotal.setVisibility(View.INVISIBLE);
        mBinding.bleedTankTotalLbl.setVisibility(View.INVISIBLE);

        // Calculate
        int originalPressureStart = pressureStart;
        if (pressureDesired > MyConstants.ZERO_I) {
            // A user entered a Desired Pressure
            pressureStart = mMyCalcBlending.getBleedingPressure(pressureStart, mixO2Start, mixHeStart, pressureDesired, mixO2Desired, mixHeDesired, mCalculateTrimix.getMixTopOff());
            if (pressureStart >= MyConstants.ZERO_I) {
                pressureHeliumAdd = mMyCalcBlending.getPressureHeFill(pressureStart, mixHeStart, pressureDesired, mixHeDesired);
                mCalculateTrimix.setPressureHeliumAdd(pressureHeliumAdd);
                mCalculateTrimix.setPressureHeliumTotal(pressureStart + pressureHeliumAdd);
                int pressureHeStart = pressureStart + pressureHeliumAdd;
                pressureOxygenAdd = mMyCalcBlending.getPressureO2Fill(pressureStart, mixO2Start, pressureDesired, mixO2Desired, mCalculateTrimix.getMixTopOff(), pressureHeStart);
                mCalculateTrimix.setPressureOxygenAdd(pressureOxygenAdd);
                mCalculateTrimix.setPressureOxygenTotal(pressureHeStart + pressureOxygenAdd);
                if (mCalculateTrimix.getPressureOxygenTotal() > pressureDesired) {
                    mCalculateTrimix.setPressureTopOffAdd(MyConstants.ZERO_I);
                    mCalculateTrimix.setPressureTopOffTotal(MyConstants.ZERO_I);
                } else {
                    mCalculateTrimix.setPressureTopOffAdd(pressureDesired - (pressureHeStart + pressureOxygenAdd));
                    mCalculateTrimix.setPressureTopOffTotal(pressureDesired);
                }
                if (pressureStart < originalPressureStart) {
                    // Need to bleed the tank
                    // Make the red warning visible
                    mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                    mBinding.bleedTankTotal.setVisibility(View.VISIBLE);
                    mBinding.bleedTankTotalLbl.setVisibility(View.VISIBLE);
                    mCalculateTrimix.setPressureBleedTankTotal(pressureStart);
                }
            } else {
                mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                mBinding.bleedTankLbl.setText(R.string.msg_cannot_calculate_blend);
            }
        } else if (mDiverEmail.equals(MyConstants.MY_EMAIL)) {
            // Me, MM, entered 0 as the Desired Pressure to optimize
            // And mixO2Desired is > mixO2Start
            pressureDesired = mMyCalcBlending.getPressureDesired(pressureStart, mixO2Start, mixHeStart, mixO2Desired, mixHeDesired, mCalculateTrimix.getMixTopOff());
            if (pressureDesired >= MyConstants.ZERO_I) {
                pressureHeliumAdd = mMyCalcBlending.getPressureHeFill(pressureStart, mixHeStart, pressureDesired, mixHeDesired);
                mCalculateTrimix.setPressureHeliumAdd(pressureHeliumAdd);
                mCalculateTrimix.setPressureHeliumTotal(pressureStart + pressureHeliumAdd);
                int pressureHeStart = pressureStart + pressureHeliumAdd;
                pressureOxygenAdd = mMyCalcBlending.getPressureO2Fill(pressureStart, mixO2Start, pressureDesired, mixO2Desired, mCalculateTrimix.getMixTopOff(), pressureHeStart);
                mCalculateTrimix.setPressureOxygenAdd(pressureOxygenAdd);
                mCalculateTrimix.setPressureOxygenTotal(pressureHeStart + pressureOxygenAdd);
                if (mCalculateTrimix.getPressureOxygenTotal() > pressureDesired) {
                    mCalculateTrimix.setPressureTopOffAdd(MyConstants.ZERO_I);
                    mCalculateTrimix.setPressureTopOffTotal(MyConstants.ZERO_I);
                } else {
                    mCalculateTrimix.setPressureTopOffAdd(pressureDesired - (pressureHeStart + pressureOxygenAdd));
                    mCalculateTrimix.setPressureTopOffTotal(pressureDesired);
                }
            } else {
                mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                mBinding.bleedTankLbl.setText(R.string.msg_cannot_calculate_blend);
            }
        } else {
            mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
            mBinding.bleedTankLbl.setText(R.string.msg_cannot_calculate_blend);
        }
    }

    // NOTE: Leave as is
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
        // FIXME: Only doing the minimum for now

        // Calculate and Save the Other side
        // Only one side display at the same time
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // The other side is actually the Metric
            // But not displayed!
            // Except for Imperial <--> Metric
            String otherUnit = mBinding.otherUnit.getText().toString();

            int otherPressureStartI = mCalculateTrimix.getPressureStart();
            double otherPressureStartD = MyFunctions.roundUp(mMyCalcMetric.convertPsiToBar((double) otherPressureStartI),0);
            otherPressureStartI = (int) otherPressureStartD;
            String otherPressureStartLbl = getResources().getString(R.string.lbl_metric_pressure_unit);

            int otherPressureDesiredI = mCalculateTrimix.getPressureDesired();
            double otherPressureDesiredD = MyFunctions.roundUp(mMyCalcMetric.convertPsiToBar((double) otherPressureDesiredI),0);
            otherPressureDesiredI = (int) otherPressureDesiredD;
            String otherPressureDesiredLbl = getResources().getString(R.string.lbl_metric_pressure_unit);

            int otherPressureHeliumAdd = MyConstants.ZERO_I;

            int otherPressureHeliumTotal = MyConstants.ZERO_I;
            String otherHeliumAddUnitLbl = getResources().getString(R.string.lbl_metric_pressure_unit);

            int otherPressureOxygenAdd = MyConstants.ZERO_I;

            int otherPressureOxygenTotal = MyConstants.ZERO_I;
            String otherOxygenAddUnitLbl = getResources().getString(R.string.lbl_metric_pressure_unit);

            int otherPressureTopOffAdd =  MyConstants.ZERO_I;

            int otherPressureTopOffTotal = MyConstants.ZERO_I;
            String otherTOffAddUnitLbl = getResources().getString(R.string.lbl_metric_pressure_unit);

            int otherPressureBleedTankTotal = MyConstants.ZERO_I;
            String otherBleedTankTotalLbl = getResources().getString(R.string.lbl_metric_pressure_unit);

            // Switch Default side to Other side
            mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

            // Switch Other Side (just saved above) to Default side
            mBinding.defaultUnit.setText(otherUnit);
            mCalculateTrimix.setPressureStart(otherPressureStartI);
            mBinding.pressureStartLbl.setText(otherPressureStartLbl);

            mCalculateTrimix.setPressureDesired(otherPressureDesiredI);
            mBinding.pressureDesiredLbl.setText(otherPressureDesiredLbl);

            mCalculateTrimix.setPressureHeliumAdd(otherPressureHeliumAdd);
            mCalculateTrimix.setPressureHeliumTotal(otherPressureHeliumTotal);
            mBinding.heliumAddUnitLbl.setText(otherHeliumAddUnitLbl);

            mCalculateTrimix.setPressureOxygenAdd(otherPressureOxygenAdd);
            mCalculateTrimix.setPressureOxygenTotal(otherPressureOxygenTotal);
            mBinding.oxygenAddUnitLbl.setText(otherOxygenAddUnitLbl);

            mCalculateTrimix.setPressureTopOffAdd(otherPressureTopOffAdd);
            mCalculateTrimix.setPressureTopOffTotal(otherPressureTopOffTotal);
            mBinding.topOffAddUnitLbl.setText(otherTOffAddUnitLbl);

            mCalculateTrimix.setPressureBleedTankTotal(otherPressureBleedTankTotal);
            mBinding.bleedTankTotalLbl.setText(otherBleedTankTotalLbl);
        } else {
            // The other side is actually the Imperial
            // But not displayed!
            // Except for Metric <--> Imperial
            String otherUnit = mBinding.otherUnit.getText().toString();

            int otherPressureStartI = mCalculateTrimix.getPressureStart();
            double otherPressureStartD = MyFunctions.roundUp(mMyCalcImperial.convertBarToPsi((double) otherPressureStartI),0);
            otherPressureStartI = (int) otherPressureStartD;
            String otherPressureStartLbl = getResources().getString(R.string.lbl_imperial_pressure_unit);

            int otherPressureDesiredI = mCalculateTrimix.getPressureDesired();
            double otherPressureDesiredD = MyFunctions.roundUp(mMyCalcImperial.convertBarToPsi((double) otherPressureDesiredI),0);
            otherPressureDesiredI = (int) otherPressureDesiredD;
            String otherPressureDesiredLbl = getResources().getString(R.string.lbl_imperial_pressure_unit);

            int otherPressureHeliumAdd = MyConstants.ZERO_I;

            int otherPressureHeliumTotal = MyConstants.ZERO_I;
            String otherHeliumAddUnitLbl = getResources().getString(R.string.lbl_imperial_pressure_unit);

            int otherPressureOxygenAdd = MyConstants.ZERO_I;

            int otherPressureOxygenTotal = MyConstants.ZERO_I;
            String otherOxygenAddUnitLbl = getResources().getString(R.string.lbl_imperial_pressure_unit);

            int otherPressureTopOffAdd = MyConstants.ZERO_I;

            int otherPressureTopOffTotal = MyConstants.ZERO_I;
            String otherTOffAddUnitLbl = getResources().getString(R.string.lbl_imperial_pressure_unit);

            int otherPressureBleedTankTotal =  MyConstants.ZERO_I;
            String otherBleedTankTotalLbl = getResources().getString(R.string.lbl_imperial_pressure_unit);

            // Switch Default side to Other side
            mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

            // Switch Other Side (saved) to Default side
            mBinding.defaultUnit.setText(otherUnit);
            mCalculateTrimix.setPressureStart(otherPressureStartI);
            mBinding.pressureStartLbl.setText(otherPressureStartLbl);

            mCalculateTrimix.setPressureDesired(otherPressureDesiredI);
            mBinding.pressureDesiredLbl.setText(otherPressureDesiredLbl);

            mCalculateTrimix.setPressureHeliumAdd(otherPressureHeliumAdd);
            mCalculateTrimix.setPressureHeliumTotal(otherPressureHeliumTotal);
            mBinding.heliumAddUnitLbl.setText(otherHeliumAddUnitLbl);

            mCalculateTrimix.setPressureOxygenAdd(otherPressureOxygenAdd);
            mCalculateTrimix.setPressureOxygenTotal(otherPressureOxygenTotal);
            mBinding.oxygenAddUnitLbl.setText(otherOxygenAddUnitLbl);

            mCalculateTrimix.setPressureTopOffAdd(otherPressureTopOffAdd);
            mCalculateTrimix.setPressureTopOffTotal(otherPressureTopOffTotal);
            mBinding.topOffAddUnitLbl.setText(otherTOffAddUnitLbl);

            mCalculateTrimix.setPressureBleedTankTotal(otherPressureBleedTankTotal);
            mBinding.bleedTankTotalLbl.setText(otherBleedTankTotalLbl);
        }

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateTrimixActivity) {
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

    private void switchHeO2() {
        // Switching Add and add totals between He and O2 and vice versa
        String pressureHeliumLbl = mCalculateTrimix.getHeliumAddLbl();
        int pressureStart = mCalculateTrimix.getPressureStart();
        int pressureHeliumAdd = mCalculateTrimix.getPressureHeliumAdd();
        String pressureOxygenLbl = mCalculateTrimix.getOxygenAddLbl();
        int pressureOxygenAdd = mCalculateTrimix.getPressureOxygenAdd();

        mCalculateTrimix.setHeliumAddLbl(pressureOxygenLbl);
        mCalculateTrimix.setPressureHeliumAdd(pressureOxygenAdd);
        mCalculateTrimix.setPressureHeliumTotal(pressureStart + pressureOxygenAdd);

        mCalculateTrimix.setOxygenAddLbl(pressureHeliumLbl);
        mCalculateTrimix.setPressureOxygenAdd(pressureHeliumAdd);
        mCalculateTrimix.setPressureOxygenTotal(pressureStart + pressureHeliumAdd + pressureOxygenAdd);
    }

    private void readDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // If no default unit (Last Used unit) exists e.g. first time ever, use the Phone unit
        mDefaultUnit = (preferences.getString(getString(R.string.code_default_unit), mUnit));

        // The preferred unit is always displayed on the left
        // The layout defaults to Imperial unit on the left
        // Might have to switch if the user last exit the activity on using the other unit

        if (mUnit.equals(MyConstants.IMPERIAL)) {
            // The phone is in IMPERIAL
            // And it is also the App default
            assert mDefaultUnit != null;
            if (!mDefaultUnit.equals(mUnit)) {
                // Switch to METRIC
                // Tour de passe passe - Starts
                // Because only one unit is shown at a time
                mDefaultUnit = MyConstants.IMPERIAL;
                switchSide();
                mDefaultUnit = MyConstants.METRIC;
                // Tout de passe passe - Ends
            }
        } else {
            assert mDefaultUnit != null;
            if (mDefaultUnit.equals(MyConstants.METRIC)) {
                // The phone is in METRIC
                // Switch to METRIC
                // Tour de passe passe - Starts
                // Because only one unit is shown at a time
                mDefaultUnit = MyConstants.IMPERIAL;
                switchSide();
                mDefaultUnit = MyConstants.METRIC;
                // Tout de passe passe - Ends
            }
        }

        int pressureStart = preferences.getInt("trimixPressureStart",MyConstants.ZERO_I);
        int pressureDesired = preferences.getInt("trimixPressureDesired",MyConstants.ZERO_I);
        Double mixStartHe = Double.parseDouble(Float.valueOf(preferences.getFloat("trimixMixStartHe", MyConstants.ZERO_F)).toString());
        Double mixDesiredHe = Double.parseDouble(Float.valueOf(preferences.getFloat("trimixMixDesiredHe", MyConstants.ZERO_F)).toString());
        Double mixStartO2 = Double.parseDouble(Float.valueOf(preferences.getFloat("trimixMixStartO2", MyConstants.ZERO_F)).toString());
        Double mixDesiredO2 = Double.parseDouble(Float.valueOf(preferences.getFloat("trimixMixDesiredO2", MyConstants.ZERO_F)).toString());

        mCalculateTrimix.setPressureStart(pressureStart);
        mCalculateTrimix.setPressureDesired(pressureDesired);
        mCalculateTrimix.setMixHeStart(mixStartHe);
        mCalculateTrimix.setMixHeDesired(mixDesiredHe);
        mCalculateTrimix.setMixO2Start(mixStartO2);
        mCalculateTrimix.setMixO2Desired(mixDesiredO2);
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putInt("trimixPressureStart",mCalculateTrimix.getPressureStart());
        edit.putInt("trimixPressureDesired",mCalculateTrimix.getPressureDesired());
        edit.putFloat("trimixMixStartHe",Float.parseFloat(String.valueOf(mCalculateTrimix.getMixHeStart())));
        edit.putFloat("trimixMixDesiredHe",Float.parseFloat(String.valueOf(mCalculateTrimix.getMixHeDesired())));
        edit.putFloat("trimixMixStartO2",Float.parseFloat(String.valueOf(mCalculateTrimix.getMixO2Start())));
        edit.putFloat("trimixMixDesiredO2",Float.parseFloat(String.valueOf(mCalculateTrimix.getMixO2Desired())));
        edit.apply();
    }
}