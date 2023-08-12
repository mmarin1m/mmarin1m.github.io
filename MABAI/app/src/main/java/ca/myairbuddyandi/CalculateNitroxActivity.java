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

import ca.myairbuddyandi.databinding.CalculateNitroxActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateNitroxActivity class
 */

public class CalculateNitroxActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateNitroxActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final AirDA mAirDa = new AirDA(this);
    private final CalculateNitrox mCalculateNitrox = new CalculateNitrox();
    private CalculateNitroxActivityBinding mBinding = null;
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_nitrox_activity);

        mCalculateNitrox.mBinding = mBinding;

        mBinding.setCalculateNitrox(mCalculateNitrox);

        // Set the listeners
        mBinding.calculateStartButton.setOnClickListener(view -> calculateStart());

        mBinding.calculateDesiredButton.setOnClickListener(view -> calculateDesired());

        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mCalculateNitrox.setMixO2Fill(Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.OXYGEN_MIX, "100.0")))));
        mCalculateNitrox.setMixTopOff(Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.TOP_OFF_MIX, "20.9")))));

        // Set the labels
        String mOxygenAddLbl = String.format(getResources().getString(R.string.lbl_add_oxygen), mCalculateNitrox.getMixO2Fill().toString() + "%");
        String mTopOffAddLbl = String.format(getResources().getString(R.string.lbl_add_top_off), mCalculateNitrox.getMixTopOff().toString() + "%");

        mCalculateNitrox.setOxygenAddLbl(mOxygenAddLbl);
        mCalculateNitrox.setTopOffAddLbl(mTopOffAddLbl);

        mAirDa.open();
        Diver diver = new Diver();
        mAirDa.getDiver(MyConstants.ONE_L,diver);
        mDiverEmail = diver.getEmail();
        mAirDa.close();

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateNitroxActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_nitrox));
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
        int pressureOxygenAdd;
        Double mixO2Start = mCalculateNitrox.getMixO2Start();
        int pressureDesired = mCalculateNitrox.getPressureDesired();
        Double mixO2Desired = mCalculateNitrox.getMixO2Desired();

        // Reset the previous calculated values to zero
        mCalculateNitrox.setPressureOxygenAdd(MyConstants.ZERO_I);
        mCalculateNitrox.setPressureOxygenTotal(MyConstants.ZERO_I);
        mCalculateNitrox.setPressureTopOffAdd(MyConstants.ZERO_I);
        mCalculateNitrox.setPressureTopOffTotal(MyConstants.ZERO_I);

        // Make the red warning invisible
        mBinding.bleedTankLbl.setVisibility(View.INVISIBLE);
        mBinding.bleedTankTotal.setVisibility(View.INVISIBLE);
        mBinding.bleedTankTotalLbl.setVisibility(View.INVISIBLE);

        // Calculate
        if (pressureDesired > MyConstants.ZERO_I && mixO2Desired >= mCalculateNitrox.getMixTopOff()) {
            // Need to bleed the tank
            if (mixO2Desired > mixO2Start) {
                int bleedPressure = mMyCalcBlending.getPressureStart(mixO2Start, pressureDesired, mixO2Desired, mCalculateNitrox.getMixO2Fill(), mCalculateNitrox.getMixTopOff());
                pressureOxygenAdd = mMyCalcBlending.getPressureO2Fill(bleedPressure, mixO2Start, pressureDesired, mixO2Desired, mCalculateNitrox.getMixO2Fill(), mCalculateNitrox.getMixTopOff());
                mCalculateNitrox.setPressureOxygenAdd(pressureOxygenAdd);
                mCalculateNitrox.setPressureOxygenTotal(bleedPressure + pressureOxygenAdd);
                mCalculateNitrox.setPressureTopOffAdd(MyConstants.ZERO_I);
                mCalculateNitrox.setPressureTopOffTotal(MyConstants.ZERO_I);
                // Make the red warning visible
                mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotal.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotalLbl.setVisibility(View.VISIBLE);
                mCalculateNitrox.setPressureBleedTankTotal(bleedPressure);
            } else {
                int bleedPressure = mMyCalcBlending.getPressureStart(mixO2Start, pressureDesired, mixO2Desired, mCalculateNitrox.getMixO2Fill(), mCalculateNitrox.getMixTopOff());
                mCalculateNitrox.setPressureOxygenAdd(MyConstants.ZERO_I);
                mCalculateNitrox.setPressureOxygenTotal(bleedPressure);
                mCalculateNitrox.setPressureTopOffAdd(pressureDesired - bleedPressure);
                mCalculateNitrox.setPressureTopOffTotal(pressureDesired);
                // Make the red warning visible
                mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotal.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotalLbl.setVisibility(View.VISIBLE);
                mCalculateNitrox.setPressureBleedTankTotal(bleedPressure);
            }
        }
    }

    private void calculateDesired() {
        // Get the values
        int pressureOxygenAdd;
        int pressureStart = mCalculateNitrox.getPressureStart();
        Double mixO2Start = mCalculateNitrox.getMixO2Start();
        int pressureDesired = mCalculateNitrox.getPressureDesired();
        Double mixO2Desired = mCalculateNitrox.getMixO2Desired();

        // Reset the previous calculated values to zero
        mCalculateNitrox.setPressureOxygenAdd(MyConstants.ZERO_I);
        mCalculateNitrox.setPressureOxygenTotal(MyConstants.ZERO_I);
        mCalculateNitrox.setPressureTopOffAdd(MyConstants.ZERO_I);
        mCalculateNitrox.setPressureTopOffTotal(MyConstants.ZERO_I);

        // Make the red warning invisible
        mBinding.bleedTankLbl.setText(R.string.lbl_bleed_cylinder);
        mBinding.bleedTankLbl.setVisibility(View.INVISIBLE);
        mBinding.bleedTankTotal.setVisibility(View.INVISIBLE);
        mBinding.bleedTankTotalLbl.setVisibility(View.INVISIBLE);

        // Calculate
        if (pressureDesired > pressureStart && pressureDesired > MyConstants.ZERO_I && mixO2Desired >= mCalculateNitrox.getMixTopOff()) {
            // A user entered a Desired Pressure
            pressureOxygenAdd = mMyCalcBlending.getPressureO2Fill(pressureStart, mixO2Start, pressureDesired, mixO2Desired, mCalculateNitrox.getMixO2Fill(), mCalculateNitrox.getMixTopOff());
            if (pressureOxygenAdd >= MyConstants.ZERO_I) {
                mCalculateNitrox.setPressureOxygenAdd(pressureOxygenAdd);
                mCalculateNitrox.setPressureOxygenTotal(pressureStart + pressureOxygenAdd);
                if (mCalculateNitrox.getPressureOxygenTotal() > pressureDesired) {
                    mCalculateNitrox.setPressureTopOffAdd(MyConstants.ZERO_I);
                    mCalculateNitrox.setPressureTopOffTotal(MyConstants.ZERO_I);
                } else {
                    mCalculateNitrox.setPressureTopOffAdd(pressureDesired - (pressureStart + pressureOxygenAdd));
                    mCalculateNitrox.setPressureTopOffTotal(pressureDesired);
                }
            } else {
                // Need to bleed the tank
                int bleedPressure = mMyCalcBlending.getPressureStart(pressureDesired, mixO2Desired, mixO2Start, mCalculateNitrox.getMixTopOff());
                mCalculateNitrox.setPressureOxygenTotal(bleedPressure);
                mCalculateNitrox.setPressureTopOffAdd(pressureDesired - bleedPressure);
                mCalculateNitrox.setPressureTopOffTotal(pressureDesired);
                // Make the red warning visible
                mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotal.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotalLbl.setVisibility(View.VISIBLE);
                mCalculateNitrox.setPressureBleedTankTotal(bleedPressure);
            }
        } else if (mDiverEmail.equals(MyConstants.MY_EMAIL) && mixO2Desired > mixO2Start && mixO2Desired >= mCalculateNitrox.getMixTopOff()) {
            // Me, MM, entered 0 as the Desired Pressure to optimize
            // And mixO2Desired is > mixO2Start
            pressureOxygenAdd = mMyCalcBlending.getPressureO2Desired(pressureStart, mixO2Start, mixO2Desired, mCalculateNitrox.getMixO2Fill(), mCalculateNitrox.getMixTopOff());
            if (pressureOxygenAdd > MyConstants.ZERO_I) {
                mCalculateNitrox.setPressureOxygenAdd(pressureOxygenAdd);
                mCalculateNitrox.setPressureOxygenTotal(pressureStart + pressureOxygenAdd);
                mCalculateNitrox.setPressureTopOffAdd(MyConstants.ZERO_I);
                mCalculateNitrox.setPressureTopOffTotal(pressureDesired);
            } else {
                // Need to bleed the tank
                int bleedPressure = mMyCalcBlending.getPressureStart(pressureDesired, mixO2Desired, mixO2Start, mCalculateNitrox.getMixTopOff());
                mCalculateNitrox.setPressureTopOffAdd(pressureDesired - bleedPressure);
                mCalculateNitrox.setPressureTopOffTotal(pressureDesired);
                // Make the red warning visible
                mBinding.bleedTankLbl.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotal.setVisibility(View.VISIBLE);
                mBinding.bleedTankTotalLbl.setVisibility(View.VISIBLE);
                mCalculateNitrox.setPressureBleedTankTotal(bleedPressure);
            }
        } else if (mDiverEmail.equals(MyConstants.MY_EMAIL) && mixO2Desired >= mCalculateNitrox.getMixTopOff()) {
            // Me, MM, entered 0 as the Desired Pressure to optimize
            // And mixO2Desired is <= mixO2Start
            pressureOxygenAdd = mMyCalcBlending.getPressureO2Desired(pressureStart, mixO2Start, mixO2Desired, mCalculateNitrox.getMixO2Fill(), mCalculateNitrox.getMixTopOff());
            mCalculateNitrox.setPressureOxygenAdd(MyConstants.ZERO_I);
            mCalculateNitrox.setPressureOxygenTotal(pressureStart);
            mCalculateNitrox.setPressureTopOffAdd(pressureOxygenAdd - pressureStart);
            mCalculateNitrox.setPressureTopOffTotal(pressureOxygenAdd);
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

            int otherPressureStartI = mCalculateNitrox.getPressureStart();
            double otherPressureStartD = MyFunctions.roundUp(mMyCalcMetric.convertPsiToBar((double) otherPressureStartI),0);
            otherPressureStartI = (int) otherPressureStartD;
            String otherPressureStartLbl = getResources().getString(R.string.lbl_metric_pressure_unit);

            int otherPressureDesiredI = mCalculateNitrox.getPressureDesired();
            double otherPressureDesiredD = MyFunctions.roundUp(mMyCalcMetric.convertPsiToBar((double) otherPressureDesiredI),0);
            otherPressureDesiredI = (int) otherPressureDesiredD;
            String otherPressureDesiredLbl = getResources().getString(R.string.lbl_metric_pressure_unit);

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
            mCalculateNitrox.setPressureStart(otherPressureStartI);
            mBinding.pressureStartLbl.setText(otherPressureStartLbl);

            mCalculateNitrox.setPressureDesired(otherPressureDesiredI);
            mBinding.pressureDesiredLbl.setText(otherPressureDesiredLbl);

            mCalculateNitrox.setPressureOxygenAdd(otherPressureOxygenAdd);
            mCalculateNitrox.setPressureOxygenTotal(otherPressureOxygenTotal);
            mBinding.oxygenAddUnitLbl.setText(otherOxygenAddUnitLbl);

            mCalculateNitrox.setPressureTopOffAdd(otherPressureTopOffAdd);
            mCalculateNitrox.setPressureTopOffTotal(otherPressureTopOffTotal);
            mBinding.topOffAddUnitLbl.setText(otherTOffAddUnitLbl);

            mCalculateNitrox.setPressureBleedTankTotal(otherPressureBleedTankTotal);
            mBinding.bleedTankTotalLbl.setText(otherBleedTankTotalLbl);
        } else {
            // The other side is actually the Imperial
            // But not displayed!
            // Except for Metric <--> Imperial
            String otherUnit = mBinding.otherUnit.getText().toString();

            int otherPressureStartI = mCalculateNitrox.getPressureStart();
            double otherPressureStartD = MyFunctions.roundUp(mMyCalcImperial.convertBarToPsi((double) otherPressureStartI),0);
            otherPressureStartI = (int) otherPressureStartD;
            String otherPressureStartLbl = getResources().getString(R.string.lbl_imperial_pressure_unit);

            int otherPressureDesiredI = mCalculateNitrox.getPressureDesired();
            double otherPressureDesiredD = MyFunctions.roundUp(mMyCalcImperial.convertBarToPsi((double) otherPressureDesiredI),0);
            otherPressureDesiredI = (int) otherPressureDesiredD;
            String otherPressureDesiredLbl = getResources().getString(R.string.lbl_imperial_pressure_unit);

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
            mCalculateNitrox.setPressureStart(otherPressureStartI);
            mBinding.pressureStartLbl.setText(otherPressureStartLbl);

            mCalculateNitrox.setPressureDesired(otherPressureDesiredI);
            mBinding.pressureDesiredLbl.setText(otherPressureDesiredLbl);

            mCalculateNitrox.setPressureOxygenAdd(otherPressureOxygenAdd);
            mCalculateNitrox.setPressureOxygenTotal(otherPressureOxygenTotal);
            mBinding.oxygenAddUnitLbl.setText(otherOxygenAddUnitLbl);

            mCalculateNitrox.setPressureTopOffAdd(otherPressureTopOffAdd);
            mCalculateNitrox.setPressureTopOffTotal(otherPressureTopOffTotal);
            mBinding.topOffAddUnitLbl.setText(otherTOffAddUnitLbl);

            mCalculateNitrox.setPressureBleedTankTotal(otherPressureBleedTankTotal);
            mBinding.bleedTankTotalLbl.setText(otherBleedTankTotalLbl);
        }

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateNitroxActivity) {
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

        int pressureStart = preferences.getInt("nitroxPressureStart",MyConstants.ZERO_I);
        int pressureDesired = preferences.getInt("nitroxPressureDesired",MyConstants.ZERO_I);
        Double mixStartO2 = Double.parseDouble(Float.valueOf(preferences.getFloat("nitroxMixStartO2", MyConstants.ZERO_F)).toString());
        Double mixDesiredO2 = Double.parseDouble(Float.valueOf(preferences.getFloat("nitroxMixDesiredO2", MyConstants.ZERO_F)).toString());

        mCalculateNitrox.setPressureStart(pressureStart);
        mCalculateNitrox.setPressureDesired(pressureDesired);
        mCalculateNitrox.setMixO2Start(mixStartO2);
        mCalculateNitrox.setMixO2Desired(mixDesiredO2);
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putInt("nitroxPressureStart",mCalculateNitrox.getPressureStart());
        edit.putInt("nitroxPressureDesired",mCalculateNitrox.getPressureDesired());
        edit.putFloat("nitroxMixStartO2",Float.parseFloat(String.valueOf(mCalculateNitrox.getMixO2Start())));
        edit.putFloat("nitroxMixDesiredO2",Float.parseFloat(String.valueOf(mCalculateNitrox.getMixO2Desired())));
        edit.apply();
    }
}