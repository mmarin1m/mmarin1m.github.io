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
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import ca.myairbuddyandi.databinding.CalculateRmvActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateRmvActivity class
 */

public class CalculateRmvActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateRmvActivity";

    // Public

    // Protected

    // Private
    private String mUnit; // Phone unit
    private final CalculateRmv mCalculateRmv = new CalculateRmv();
    private CalculateRmvActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private boolean mDefaultSalinity; // true = Salt = 0 position, false = Fresh = 1 position
    private Double mDefaultAta;
    private Double mDefaultAvgDepth;
    private Double mDefaultBeginningPressure;
    private Double mDefaultEndingPressure;
    private Double mDefaultPressureUsed;
    private Double mDefaultRatedPressure;
    private Double mDefaultRmv;
    private Double mDefaultSac;
    private Double mDefaultTankFactor;
    private Double mDefaultTankVolume;
    private Double mDefaultVolumeAvailable;
    private Double mDefaultVolumeUsed;
    private Double mDefaultBottomTime;
    private String mDefaultUnit;

    // Other
    private Double mOtherAta;
    private Double mOtherAvgDepth;
    private Double mOtherBeginningPressure;
    private Double mOtherEndingPressure;
    private Double mOtherPressureUsed;
    private Double mOtherRatedPressure;
    private Double mOtherRmv;
    private Double mOtherSac;
    private Double mOtherTankFactor;
    private Double mOtherTankVolume;
    private Double mOtherVolumeUsed;
    private Double mOtherVolumeAvailable;
    private Double mOtherBottomTime;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_rmv_activity);

        mCalculateRmv.mBinding = mBinding;

        mBinding.setCalculateRmv(mCalculateRmv);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        //

        mBinding.rmvButton.setOnClickListener(view -> calculateRmv());

        mBinding.rmvButton2.setOnClickListener(view -> calculateRmv());

        //

        mBinding.timeButton.setOnClickListener(view -> calculateTime());

        mBinding.timeButton2.setOnClickListener(view -> calculateTime());

        //

        mBinding.pressureButton.setOnClickListener(view -> calculatePressure());

        mBinding.pressureButton2.setOnClickListener(view -> calculatePressure());

        //

        mBinding.volumeButton.setOnClickListener(view -> calculateVolume());

        mBinding.volumeButton2.setOnClickListener(view -> calculateVolume());

        //

        mBinding.clearButton.setOnClickListener(view -> clear());

        //Set the data in the Spinner Salinity
        String[] itemsDefaultSalinity = getResources().getStringArray(R.array.salinity_arrays);
        ArrayAdapter<String> adapterDefaultSalinity = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsDefaultSalinity);
        adapterDefaultSalinity.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculateRmv.setAdapterDefaultSalinity(adapterDefaultSalinity);

        // Get the phone unit according to the Locale
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateRmvActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_rmv));
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

    private void calculateRmv() {
        mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherVU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherPU.setTextColor(ContextCompat.getColor(this, R.color.black));

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Calculate Imperial RMV

            // Get all entered values first
            mDefaultSalinity = mCalculateRmv.getDefaultSalinity();
            mDefaultPressureUsed = getDefaultConsumption();
            mDefaultAvgDepth = mCalculateRmv.getDefaultAvgDepth();
            mDefaultBottomTime = mCalculateRmv.getDefaultBottomTime();
            mDefaultTankVolume = mCalculateRmv.getDefaultTankVolume();
            mDefaultRatedPressure = mCalculateRmv.getDefaultRatedPressure();
            mDefaultVolumeUsed = mCalculateRmv.getDefaultVolumeUsed();

            // Do intermediate calculations
            mDefaultAta = mMyCalcImperial.getAta(mDefaultAvgDepth,mDefaultSalinity);
            mDefaultSac = mMyCalcImperial.getSac(mDefaultPressureUsed,mDefaultBottomTime,mDefaultAta);
            mDefaultTankFactor = mMyCalcImperial.getCCF(mDefaultTankVolume, mDefaultRatedPressure);

            // Calculate psi from Volume Used
            if (mDefaultPressureUsed.equals(MyConstants.ZERO_D) && mDefaultVolumeUsed > MyConstants.ZERO_D) {
                mDefaultPressureUsed = mMyCalcImperial.getPressure(mDefaultVolumeUsed,mDefaultTankFactor);
            }

            // Calculate RMV and Volume Used
            mDefaultRmv = mMyCalcImperial.getRmv(mDefaultSac,mDefaultTankVolume,mDefaultRatedPressure);
            mDefaultVolumeAvailable = MyFunctions.roundDown(mMyCalcImperial.getVolume(mDefaultBeginningPressure,mDefaultTankFactor),1);

            if (mDefaultVolumeUsed.equals(MyConstants.ZERO_D)) {
                mDefaultVolumeUsed = MyFunctions.roundUp(mMyCalcImperial.getVolume(mDefaultRmv, mDefaultAta, mDefaultBottomTime), 1);
            }

            // Set the calculated values in the POJO
            mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
            mCalculateRmv.setDefaultAta(mDefaultAta);
            mCalculateRmv.setDefaultSac(mDefaultSac);
            mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
            mCalculateRmv.setDefaultRmv(mDefaultRmv);
            mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
            mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

            // Set the calculated values on the activity
            mBinding.defaultBT.setText(mCalculateRmv.getDefaultBottomTimeStringX());
            mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
            mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
            mBinding.defaultSac.setText(String.format("%1s", MyFunctions.roundUp(mDefaultSac,2)));
            mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
            mBinding.defaultRmv.setText(String.format("%1s",MyFunctions.roundUp(mDefaultRmv,2)));
            mBinding.defaultVU.setText(String.format("%1s",mDefaultVolumeUsed));
            mBinding.defaultVA.setText(String.format("%1s",mDefaultVolumeAvailable));
            mBinding.defaultVU.setText(String.format("%1s",mDefaultVolumeUsed));

            // NOTE: No warning when calculating RMV
            //  Cannot have more Volume then entered by the user

            // Calculate Metric RMV

            // Convert entered value first
            mOtherBeginningPressure = mMyCalcImperial.convertPsiToBar(mDefaultBeginningPressure);
            mOtherEndingPressure = mMyCalcImperial.convertPsiToBar(mDefaultEndingPressure);
            mOtherPressureUsed = mMyCalcImperial.convertPsiToBar(mDefaultPressureUsed);
            mOtherBottomTime = mDefaultBottomTime;
            mOtherAvgDepth = mMyCalcImperial.convertFeetToMeter(mDefaultAvgDepth);
            // Tank volume can't really be converted
            // Must be entered by the user
            mOtherTankVolume = mCalculateRmv.getOtherTankVolume();
            // Rated Pressure is not needed in Metric calculation
            mOtherRatedPressure = MyConstants.ZERO_D;

            // Convert intermediate calculations
            mOtherAta = mMyCalcMetric.getBar(mOtherAvgDepth,mDefaultSalinity);
            mOtherSac = mMyCalcMetric.getSac(mOtherPressureUsed,mOtherBottomTime,mOtherAta);
            mOtherTankFactor = MyConstants.ZERO_D;

            // Calculate converted RMV and Volume Used
            mOtherRmv = mMyCalcMetric.getRmv(mOtherSac,mOtherTankVolume,MyConstants.ZERO_D);
            mOtherVolumeAvailable = MyFunctions.roundDown(mMyCalcMetric.getVolume(mOtherBeginningPressure,mOtherTankVolume),1);
            mOtherVolumeUsed = MyFunctions.roundUp(mMyCalcMetric.getVolume(mOtherRmv,mOtherAta, mOtherBottomTime),1);

            // Set the calculated converted values in the POJO
            mCalculateRmv.setOtherBeginningPressure(mOtherBeginningPressure);
            mCalculateRmv.setOtherPressureUsed(mOtherPressureUsed);
            mCalculateRmv.setOtherBottomTime(mOtherBottomTime);
            mCalculateRmv.setOtherAvgDepth(mOtherAvgDepth);
            mCalculateRmv.setOtherAta(mOtherAta);
            mCalculateRmv.setOtherSac(mOtherSac);
            mCalculateRmv.setOtherTankFactor(mOtherTankFactor);
            mCalculateRmv.setOtherRmv(mOtherRmv);
            mCalculateRmv.setOtherVolumeAvailable(mOtherVolumeAvailable);
            mCalculateRmv.setOtherVolumeUsed(mOtherVolumeUsed);

            // Set the calculated converted values on the activity

            mBinding.otherBP.setText(String.format("%1s",MyFunctions.roundUp(mOtherBeginningPressure,1)));
            mBinding.otherEP.setText(String.format("%1s",MyFunctions.roundUp(mOtherEndingPressure,1)));
            mBinding.otherPU.setText(String.format("%1s",MyFunctions.roundUp(mOtherPressureUsed,1)));
            mBinding.otherBT.setText(mCalculateRmv.getDefaultBottomTimeStringX());
            mBinding.otherAD.setText(String.format("%1s",MyFunctions.roundUp(mOtherAvgDepth,1)));
            mBinding.otherAta.setText(String.format("%1s",MyFunctions.roundUp(mOtherAta,2)));
            mBinding.otherSac.setText(String.format("%1s", MyFunctions.roundUp(mOtherSac,2)));
            mBinding.otherTF.setText(String.format("%1s",MyFunctions.roundDown(mOtherTankFactor,5)));
            mBinding.otherRmv.setText(String.format("%1s",MyFunctions.roundUp(mOtherRmv,2)));
            mBinding.otherVA.setText(String.format("%1s",mOtherVolumeAvailable));
            mBinding.otherVU.setText(String.format("%1s",mOtherVolumeUsed));

            // NOTE: No warning when calculating RMV
            //  Cannot have more Volume then entered by the user

        } else {
            // Calculate Metric RMV

            // Get all entered values first
            mDefaultSalinity = mCalculateRmv.getDefaultSalinity();
            mDefaultPressureUsed = getDefaultConsumption();
            mDefaultAvgDepth = mCalculateRmv.getDefaultAvgDepth();
            mDefaultBottomTime = mCalculateRmv.getDefaultBottomTime();
            mDefaultTankVolume = mCalculateRmv.getDefaultTankVolume();
            mDefaultRatedPressure = mCalculateRmv.getDefaultRatedPressure();
            mDefaultVolumeUsed = mCalculateRmv.getDefaultVolumeUsed();

            // Do intermediate calculations
            mDefaultAta = mMyCalcMetric.getBar(mDefaultAvgDepth,mDefaultSalinity);
            mDefaultSac = mMyCalcMetric.getSac(mDefaultPressureUsed,mDefaultBottomTime,mDefaultAta);
            mDefaultTankFactor = mMyCalcMetric.getCCF(mDefaultTankVolume, mDefaultRatedPressure);

            // Calculate bar from Volume Used
            if (mDefaultPressureUsed.equals(MyConstants.ZERO_D) && mDefaultVolumeUsed > MyConstants.ZERO_D) {
                mDefaultPressureUsed = mMyCalcMetric.getPressure(mDefaultVolumeUsed,mDefaultTankVolume);
            }

            // Calculate RMV and Volume Used
            mDefaultRmv = mMyCalcMetric.getRmv(mDefaultSac,mDefaultTankVolume,MyConstants.ZERO_D);
            mDefaultVolumeAvailable = MyFunctions.roundDown(mMyCalcMetric.getVolume(mDefaultBeginningPressure,mDefaultTankVolume),1);

            if (mDefaultVolumeUsed.equals(MyConstants.ZERO_D)) {
                mDefaultVolumeUsed = MyFunctions.roundUp(mMyCalcMetric.getVolume(mDefaultRmv, mDefaultAta, mDefaultBottomTime), 1);
            }

            // Set the calculated values in the POJO
            mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
            mCalculateRmv.setDefaultAta(mDefaultAta);
            mCalculateRmv.setDefaultSac(mDefaultSac);
            mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
            mCalculateRmv.setDefaultRmv(mDefaultRmv);
            mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
            mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

            // Set the calculated values on the activity
            mBinding.defaultBT.setText(mCalculateRmv.getDefaultBottomTimeStringX());
            mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
            mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
            mBinding.defaultSac.setText(String.format("%1s", MyFunctions.roundUp(mDefaultSac,2)));
            mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
            mBinding.defaultRmv.setText(String.format("%1s",MyFunctions.roundUp(mDefaultRmv,2)));
            mBinding.defaultVA.setText(String.format("%1s",mDefaultVolumeAvailable));
            mBinding.defaultVU.setText(String.format("%1s",mDefaultVolumeUsed));

            // NOTE: No warning when calculating RMV
            //  Cannot have more Volume then entered by the user

            // Calculate Imperial RMV

            // Convert entered value first
            mOtherBeginningPressure = mMyCalcMetric.convertBarToPsi(mDefaultBeginningPressure);
            mOtherEndingPressure = mMyCalcMetric.convertBarToPsi(mDefaultEndingPressure);
            mOtherPressureUsed = mMyCalcMetric.convertBarToPsi(mDefaultPressureUsed);
            mOtherBottomTime = mDefaultBottomTime;
            mOtherAvgDepth = mMyCalcMetric.convertMeterToFeet(mDefaultAvgDepth);
            // Tank volume can't really be converted
            // Must be entered by the user
            mOtherTankVolume = mCalculateRmv.getOtherTankVolume();
            // Rated Pressure can't really be converted
            // Must be entered by the user
            mOtherRatedPressure = mCalculateRmv.getOtherRatedPressure();

            // Convert intermediate calculations
            mOtherAta = mMyCalcImperial.getAta(mOtherAvgDepth,mDefaultSalinity);
            mOtherSac = mMyCalcImperial.getSac(mOtherPressureUsed,mOtherBottomTime,mOtherAta);
            mOtherTankFactor = mMyCalcImperial.getCCF(mOtherTankVolume, mOtherRatedPressure);

            // Calculate converted RMV and Volume Used
            mOtherRmv = mMyCalcImperial.getRmv(mOtherSac,mOtherTankVolume,mOtherRatedPressure);
            mOtherVolumeAvailable = MyFunctions.roundDown(mMyCalcImperial.getVolume(mOtherBeginningPressure,mDefaultTankFactor),1);
            mOtherVolumeUsed = MyFunctions.roundUp(mMyCalcImperial.getVolume(mOtherRmv,mOtherAta,mOtherBottomTime),1);

            // Set the calculated converted values in the POJO
            mCalculateRmv.setOtherBeginningPressure(mOtherBeginningPressure);
            mCalculateRmv.setOtherPressureUsed(mOtherPressureUsed);
            mCalculateRmv.setOtherBottomTime(mOtherBottomTime);
            mCalculateRmv.setOtherAvgDepth(mOtherAvgDepth);
            mCalculateRmv.setOtherAta(mOtherAta);
            mCalculateRmv.setOtherSac(mOtherSac);
            mCalculateRmv.setOtherTankFactor(mOtherTankFactor);
            mCalculateRmv.setOtherRmv(mOtherRmv);
            mCalculateRmv.setOtherVolumeAvailable(mOtherVolumeAvailable);
            mCalculateRmv.setOtherVolumeUsed(mOtherVolumeUsed);

            // Set the calculated converted values on the activity

            mBinding.otherBP.setText(String.format("%1s",MyFunctions.roundUp(mOtherBeginningPressure,1)));
            mBinding.otherEP.setText(String.format("%1s",MyFunctions.roundUp(mOtherEndingPressure,1)));
            mBinding.otherPU.setText(String.format("%1s",MyFunctions.roundUp(mOtherPressureUsed,1)));
            mBinding.otherBT.setText(mCalculateRmv.getDefaultBottomTimeStringX());
            mBinding.otherAD.setText(String.format("%1s",MyFunctions.roundUp(mOtherAvgDepth,1)));
            mBinding.otherAta.setText(String.format("%1s",MyFunctions.roundUp(mOtherAta,2)));
            mBinding.otherSac.setText(String.format("%1s", MyFunctions.roundUp(mOtherSac,2)));
            mBinding.otherTF.setText(String.format("%1s",MyFunctions.roundDown(mOtherTankFactor,5)));
            mBinding.otherRmv.setText(String.format("%1s",MyFunctions.roundUp(mOtherRmv,2)));
            mBinding.otherVA.setText(String.format("%1s",mOtherVolumeAvailable));
            mBinding.otherVU.setText(String.format("%1s",mOtherVolumeUsed));

            // NOTE: No warning when calculating RMV
            //  Cannot have more Volume then entered by the user
        }

        requestFocus(mBinding.defaultBP, false);
    }

    private void calculateTime() {
        mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherVU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherPU.setTextColor(ContextCompat.getColor(this, R.color.black));

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Calculate Imperial Time

            // Get all entered values first
            mDefaultSalinity = mCalculateRmv.getDefaultSalinity();
            mDefaultPressureUsed = getDefaultConsumption();
            mDefaultAvgDepth = mCalculateRmv.getDefaultAvgDepth();
            mDefaultTankVolume = mCalculateRmv.getDefaultTankVolume();
            mDefaultRatedPressure = mCalculateRmv.getDefaultRatedPressure();
            mDefaultRmv = mCalculateRmv.getDefaultRmv();
            mDefaultVolumeUsed = mCalculateRmv.getDefaultVolumeUsed();

            // Do intermediate calculations
            mDefaultAta = mMyCalcImperial.getAta(mDefaultAvgDepth,mDefaultSalinity);
            mDefaultTankFactor = mMyCalcImperial.getCCF(mDefaultTankVolume, mDefaultRatedPressure);
            mDefaultSac = mMyCalcImperial.getSac(mDefaultRmv,mDefaultTankFactor);

            // Calculate psi from Volume Used
            if (mDefaultPressureUsed.equals(MyConstants.ZERO_D) && mDefaultVolumeUsed > MyConstants.ZERO_D) {
                mDefaultPressureUsed = mMyCalcImperial.getPressure(mDefaultVolumeUsed,mDefaultTankFactor);
            }

            mDefaultBottomTime = mMyCalcImperial.getTimePressure(mDefaultPressureUsed,mDefaultSac,mDefaultAta);
            mDefaultVolumeAvailable = MyFunctions.roundDown(mMyCalcImperial.getVolume(mDefaultBeginningPressure,mDefaultTankFactor),1);

            if (mDefaultVolumeUsed.equals(MyConstants.ZERO_D)) {
                mDefaultVolumeUsed = MyFunctions.roundUp(mMyCalcImperial.getVolume(mDefaultRmv, mDefaultAta, mDefaultBottomTime), 1);
            }

            // Set the calculated values in the POJO
            mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
            mCalculateRmv.setDefaultAta(mDefaultAta);
            mCalculateRmv.setDefaultSac(mDefaultSac);
            mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
            mCalculateRmv.setDefaultBottomTime(mDefaultBottomTime);
            mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
            mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

            // Set the calculated values on the activity
            mBinding.defaultPU.setText(String.format("%1s",MyFunctions.roundUp(mDefaultPressureUsed,1)));
            mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
            mBinding.defaultSac.setText(String.format("%1s",MyFunctions.roundUp(mDefaultSac,2)));
            mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
            mBinding.defaultBT.setText(MyFunctions.convertMinToString(mDefaultBottomTime));
            mBinding.defaultVA.setText(String.format("%1s",mDefaultVolumeAvailable));
            mBinding.defaultVU.setText(String.format("%1s",mDefaultVolumeUsed));

            if (mDefaultVolumeUsed > mDefaultTankVolume) {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        } else {
            // Calculate Metric Time

            // Get all entered values first
            mDefaultSalinity = mCalculateRmv.getDefaultSalinity();
            mDefaultPressureUsed = getDefaultConsumption();
            mDefaultAvgDepth = mCalculateRmv.getDefaultAvgDepth();
            mDefaultTankVolume = mCalculateRmv.getDefaultTankVolume();
            mDefaultRatedPressure = mCalculateRmv.getDefaultRatedPressure();
            mDefaultRmv = mCalculateRmv.getDefaultRmv();
            mDefaultVolumeUsed = mCalculateRmv.getDefaultVolumeUsed();

            // Do intermediate calculations
            mDefaultAta = mMyCalcMetric.getBar(mDefaultAvgDepth,mDefaultSalinity);
            mDefaultTankFactor = MyConstants.ZERO_D;
            mDefaultSac = mMyCalcMetric.getSac(mDefaultRmv,mDefaultTankVolume);

            // Calculate bar from Volume Used
            if (mDefaultPressureUsed.equals(MyConstants.ZERO_D) && mDefaultVolumeUsed > MyConstants.ZERO_D) {
                mDefaultPressureUsed = mMyCalcMetric.getPressure(mDefaultVolumeUsed,mDefaultTankVolume);
            }

            // Calculate Time and Volume Used
            mDefaultBottomTime = mMyCalcMetric.getTimePressure(mDefaultPressureUsed,mDefaultSac,mDefaultAta);
            mDefaultVolumeAvailable = MyFunctions.roundDown(mMyCalcMetric.getVolume(mDefaultBeginningPressure,mDefaultTankVolume),1);

            if (mDefaultVolumeUsed.equals(MyConstants.ZERO_D)) {
                mDefaultVolumeUsed = MyFunctions.roundUp(mMyCalcMetric.getVolume(mDefaultRmv,mDefaultAta,mDefaultBottomTime),1);
            }

            // Set the calculated values in the POJO
            mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
            mCalculateRmv.setDefaultAta(mDefaultAta);
            mCalculateRmv.setDefaultSac(mDefaultSac);
            mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
            mCalculateRmv.setDefaultBottomTime(mDefaultBottomTime);
            mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
            mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

            // Set the calculated values on the activity
            mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
            mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
            mBinding.defaultSac.setText(String.format("%1s",MyFunctions.roundUp(mDefaultSac,2)));
            mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
            mBinding.defaultBT.setText(MyFunctions.convertMinToString(mDefaultBottomTime));
            mBinding.defaultVA.setText(String.format("%1s",mDefaultVolumeAvailable));
            mBinding.defaultVU.setText(String.format("%1s",mDefaultVolumeUsed));

            if (mDefaultVolumeUsed > (MyFunctions.roundUp(mDefaultTankVolume * mDefaultPressureUsed,1))) {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        }

        requestFocus(mBinding.defaultBP, false);
    }

    private void calculatePressure() {
        mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherVU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherPU.setTextColor(ContextCompat.getColor(this, R.color.black));

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Calculate Imperial Pressure

            // Get all entered values first
            mDefaultSalinity = mCalculateRmv.getDefaultSalinity();
            mDefaultBeginningPressure = MyConstants.ZERO_D;
            mDefaultEndingPressure = MyConstants.ZERO_D;
            mDefaultAvgDepth = mCalculateRmv.getDefaultAvgDepth();
            mDefaultBottomTime = mCalculateRmv.getDefaultBottomTime();
            mDefaultTankVolume = mCalculateRmv.getDefaultTankVolume();
            mDefaultRatedPressure = mCalculateRmv.getDefaultRatedPressure();
            mDefaultRmv = mCalculateRmv.getDefaultRmv();

            // Do intermediate calculations
            mDefaultAta = mMyCalcImperial.getAta(mDefaultAvgDepth,mDefaultSalinity);
            mDefaultTankFactor = mMyCalcImperial.getCCF(mDefaultTankVolume, mDefaultRatedPressure);
            mDefaultSac = mMyCalcImperial.getSac(mDefaultRmv,mDefaultTankFactor);

            // Calculate Pressure and Volume Used
            mDefaultPressureUsed = MyFunctions.roundUp(mMyCalcImperial.getPressureMin(mDefaultSac,mDefaultAta,mDefaultBottomTime),1);
            mDefaultVolumeAvailable = MyFunctions.roundDown(mMyCalcImperial.getVolume(mDefaultPressureUsed,mDefaultTankFactor),1);
            mDefaultVolumeUsed = MyFunctions.roundUp(mMyCalcImperial.getVolume(mDefaultRmv,mDefaultAta,mDefaultBottomTime),1);

            // Set the calculated values in the POJO
            mCalculateRmv.setDefaultBeginningPressure(mDefaultBeginningPressure);
            mCalculateRmv.setDefaultEndingPressure(mDefaultEndingPressure);
            mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
            mCalculateRmv.setDefaultAta(mDefaultAta);
            mCalculateRmv.setDefaultSac(mDefaultSac);
            mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
            mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
            mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

            // Set the calculated values on the activity
            mBinding.defaultBT.setText(mCalculateRmv.getDefaultBottomTimeStringX());
            mBinding.defaultBP.setText(String.format("%1s",mDefaultBeginningPressure));
            mBinding.defaultEP.setText(String.format("%1s",mDefaultEndingPressure));
            mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
            mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
            mBinding.defaultSac.setText(String.format("%1s",MyFunctions.roundUp(mDefaultSac,2)));
            mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
            mBinding.defaultVA.setText(String.format("%1s",mDefaultVolumeAvailable));
            mBinding.defaultVU.setText(String.format("%1s",mDefaultVolumeUsed));

            // Just to tell the user that the pressure needed exceeds the rated pressure
            if (mDefaultPressureUsed > mDefaultRatedPressure) {
                mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }

            //Just to tell the user that the volume needed exceeds the tank volume
            if (mDefaultVolumeUsed > mDefaultTankVolume) {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        } else {
            // Calculate Metric Pressure

            // Get all entered values first
            mDefaultSalinity = mCalculateRmv.getDefaultSalinity();
            mDefaultBeginningPressure = MyConstants.ZERO_D;
            mDefaultEndingPressure = MyConstants.ZERO_D;
            mDefaultAvgDepth = mCalculateRmv.getDefaultAvgDepth();
            mDefaultBottomTime = mCalculateRmv.getDefaultBottomTime();
            mDefaultTankVolume = mCalculateRmv.getDefaultTankVolume();
            mDefaultRatedPressure = mCalculateRmv.getDefaultRatedPressure();
            mDefaultRmv = mCalculateRmv.getDefaultRmv();

            // Do intermediate calculations
            mDefaultAta = mMyCalcMetric.getBar(mDefaultAvgDepth,mDefaultSalinity);
            mDefaultTankFactor = mMyCalcMetric.getCCF(mDefaultTankVolume, mDefaultRatedPressure);
            mDefaultSac = mMyCalcMetric.getSac(mDefaultRmv,mDefaultTankVolume);

            // Calculate Pressure and Volume Used
            mDefaultPressureUsed = MyFunctions.roundUp(mMyCalcMetric.getPressureMin(mDefaultSac,mDefaultAta,mDefaultBottomTime),1);
            mDefaultVolumeAvailable = MyFunctions.roundDown(mMyCalcMetric.getVolume(mDefaultPressureUsed,mDefaultTankVolume),1);
            mDefaultVolumeUsed = MyFunctions.roundUp(mMyCalcMetric.getVolume(mDefaultRmv,mDefaultAta, mDefaultBottomTime),1);

            // Set the calculated values in the POJO
            mCalculateRmv.setDefaultBeginningPressure(mDefaultBeginningPressure);
            mCalculateRmv.setDefaultEndingPressure(mDefaultEndingPressure);
            mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
            mCalculateRmv.setDefaultAta(mDefaultAta);
            mCalculateRmv.setDefaultSac(mDefaultSac);
            mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
            mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
            mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

            // Set the calculated values on the activity
            mBinding.otherBT.setText(mCalculateRmv.getDefaultBottomTimeStringX());
            mBinding.defaultBP.setText(String.format("%1s",mDefaultBeginningPressure));
            mBinding.defaultEP.setText(String.format("%1s",mDefaultEndingPressure));
            mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
            mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
            mBinding.defaultSac.setText(String.format("%1s",MyFunctions.roundUp(mDefaultSac,2)));
            mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
            mBinding.defaultVA.setText(String.format("%1s",mDefaultVolumeAvailable));
            mBinding.defaultVU.setText(String.format("%1s",mDefaultVolumeUsed));

            // Just to tell the user that the pressure needed exceeds the rated pressure
            if (mDefaultPressureUsed > mDefaultRatedPressure) {
                mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }

            // Just to tell the user that the volume needed exceeds the tank volume
            if (mDefaultVolumeUsed > (mDefaultTankVolume * mDefaultRatedPressure)) {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        }

        requestFocus(mBinding.defaultBP, false);
    }

    private void calculateVolume() {
        mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherVU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherPU.setTextColor(ContextCompat.getColor(this, R.color.black));

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Calculate Imperial Volume

            // Get all entered values first
            mDefaultSalinity = mCalculateRmv.getDefaultSalinity();
            mDefaultAvgDepth = mCalculateRmv.getDefaultAvgDepth();
            mDefaultBottomTime = mCalculateRmv.getDefaultBottomTime();
            mDefaultTankVolume = mCalculateRmv.getDefaultTankVolume();
            mDefaultRatedPressure = mCalculateRmv.getDefaultRatedPressure();
            mDefaultRmv = mCalculateRmv.getDefaultRmv();

            // Do intermediate calculations
            mDefaultAta = mMyCalcImperial.getAta(mDefaultAvgDepth,mDefaultSalinity);
            mDefaultTankFactor = mMyCalcImperial.getCCF(mDefaultTankVolume, mDefaultRatedPressure);

            // Calculate Volume Needed
            mDefaultVolumeUsed = mMyCalcImperial.getVolume(mDefaultRmv,mDefaultAta,mDefaultBottomTime);

            // Calculate Volume Available
            // Must be the same as the Volume Needed
            mDefaultVolumeAvailable = mDefaultVolumeUsed;

            // Calculate the Pression based on the Volume
            mDefaultPressureUsed = MyFunctions.roundUp(mMyCalcImperial.convertVolumeToPressure(mDefaultVolumeUsed, mDefaultTankFactor), 1);

            // Calculate the SAC
            mDefaultSac = MyFunctions.roundUp(mMyCalcImperial.getSac(mDefaultPressureUsed,mDefaultBottomTime,mDefaultAta), 2);

            // Set the calculated values in the POJO
            mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
            mCalculateRmv.setDefaultAta(mDefaultAta);
            mCalculateRmv.setDefaultSac(mDefaultSac);
            mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
            mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
            mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

            // Set the calculated values on the activity
            mBinding.defaultBT.setText(mCalculateRmv.getDefaultBottomTimeStringX());
            mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
            mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
            mBinding.defaultSac.setText(String.format("%1s",mDefaultSac));
            mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
            mBinding.defaultVA.setText(String.format("%1s",MyFunctions.roundUp(mDefaultVolumeAvailable,1)));
            mBinding.defaultVU.setText(String.format("%1s",MyFunctions.roundUp(mDefaultVolumeUsed,1)));

            // Just to tell the user that the pressure needed exceeds the rated pressure
            if (mDefaultPressureUsed > mDefaultRatedPressure) {
                mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }

            // Just to tell the user that the volume needed exceeds the tank volume
            if (mDefaultVolumeUsed > mDefaultTankVolume) {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        } else {
            // Calculate Metric Volume

            // Get all entered values first
            mDefaultSalinity = mCalculateRmv.getDefaultSalinity();
            mDefaultAvgDepth = mCalculateRmv.getDefaultAvgDepth();
            mDefaultBottomTime = mCalculateRmv.getDefaultBottomTime();
            mDefaultTankVolume = mCalculateRmv.getDefaultTankVolume();
            mDefaultRatedPressure = mCalculateRmv.getDefaultRatedPressure();
            mDefaultRmv = mCalculateRmv.getDefaultRmv();

            // Do intermediate calculations
            mDefaultAta = mMyCalcMetric.getBar(mDefaultAvgDepth,mDefaultSalinity);
            mDefaultTankFactor = mMyCalcMetric.getCCF(mDefaultTankVolume, mDefaultRatedPressure);

            // Calculate RMV
            mDefaultVolumeUsed = mMyCalcMetric.getVolume(mDefaultRmv,mDefaultAta,mDefaultBottomTime);

            // Calculate Volume Available
            // Must be the same as the Volume Needed
            mDefaultVolumeAvailable = mDefaultVolumeUsed;

            // Calculate the Pression based on the Volume
            mDefaultPressureUsed = MyFunctions.roundUp(mMyCalcMetric.convertVolumeToPressure(mDefaultVolumeUsed, mDefaultTankVolume), 1);

            // Calculate SAC
            mDefaultSac = MyFunctions.roundUp(mMyCalcMetric.getSac(mDefaultPressureUsed,mDefaultBottomTime,mDefaultAta), 2);

            // Set the calculated values in the POJO
            mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
            mCalculateRmv.setDefaultAta(mDefaultAta);
            mCalculateRmv.setDefaultSac(mDefaultSac);
            mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
            mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
            mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

            // Set the calculated values on the activity
            mBinding.otherBT.setText(mCalculateRmv.getDefaultBottomTimeStringX());
            mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
            mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
            mBinding.defaultSac.setText(String.format("%1s",mDefaultSac));
            mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
            mBinding.defaultVA.setText(String.format("%1s",MyFunctions.roundUp(mDefaultVolumeAvailable,1)));
            mBinding.defaultVU.setText(String.format("%1s",MyFunctions.roundUp(mDefaultVolumeUsed,1)));

            // Just to tell the user that the pressure needed exceeds the rated pressure
            if (mDefaultPressureUsed > mDefaultRatedPressure) {
                mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }

            // Just to tell the user that the volume needed exceeds the tank volume
            if (mDefaultVolumeUsed > (mDefaultTankVolume * mDefaultRatedPressure)) {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        }

        requestFocus(mBinding.defaultBP, false);
    }

    private void clear() {
        // Reset the Default
        mDefaultBeginningPressure = MyConstants.ZERO_D;
        mDefaultEndingPressure = MyConstants.ZERO_D;
        mDefaultPressureUsed = MyConstants.ZERO_D;
        mDefaultBottomTime = MyConstants.ZERO_D;
        mDefaultAvgDepth = MyConstants.ZERO_D;
        mDefaultTankVolume = MyConstants.ZERO_D;
        mDefaultRatedPressure = MyConstants.ZERO_D;
        mDefaultAta = MyConstants.ZERO_D;
        mDefaultSac = MyConstants.ZERO_D;
        mDefaultTankFactor = MyConstants.ZERO_D;
        mDefaultRmv = MyConstants.ZERO_D;
        mDefaultVolumeUsed = MyConstants.ZERO_D;
        mDefaultVolumeAvailable = MyConstants.ZERO_D;

        mCalculateRmv.setDefaultBeginningPressure(mDefaultBeginningPressure);
        mCalculateRmv.setDefaultEndingPressure(mDefaultEndingPressure);
        mCalculateRmv.setDefaultPressureUsed(mDefaultPressureUsed);
        mCalculateRmv.setDefaultBottomTime(mDefaultBottomTime);
        mCalculateRmv.setDefaultAvgDepth(mDefaultAvgDepth);
        mCalculateRmv.setDefaultTankVolume(mDefaultTankVolume);
        mCalculateRmv.setDefaultRatedPressure(mDefaultRatedPressure);
        mCalculateRmv.setDefaultSalinity(mDefaultSalinity);
        mCalculateRmv.setDefaultAta(mDefaultAta);
        mCalculateRmv.setDefaultSac(mDefaultSac);
        mCalculateRmv.setDefaultTankFactor(mDefaultTankFactor);
        mCalculateRmv.setDefaultRmv(mDefaultRmv);
        mCalculateRmv.setDefaultVolumeAvailable(mDefaultVolumeAvailable);
        mCalculateRmv.setDefaultVolumeUsed(mDefaultVolumeUsed);

        mBinding.defaultBP.setText(String.format("%1s", mDefaultBeginningPressure));
        mBinding.defaultEP.setText(String.format("%1s",mDefaultEndingPressure));
        mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
        mBinding.defaultBT.setText(getString(R.string.code_zero_time));
        mBinding.defaultAD.setText(String.format("%1s",mDefaultAvgDepth));
        mBinding.defaultTV.setText(String.format("%1s",mDefaultTankVolume));
        mBinding.defaultRP.setText(String.format("%1s",mDefaultRatedPressure));
        mBinding.defaultPU.setText(String.format("%1s",mDefaultPressureUsed));
        mBinding.defaultAta.setText(String.format("%1s",MyFunctions.roundUp(mDefaultAta,2)));
        mBinding.defaultSac.setText(String.format("%1s",mDefaultSac));
        mBinding.defaultTF.setText(String.format("%1s",MyFunctions.roundDown(mDefaultTankFactor,5)));
        mBinding.defaultRmv.setText(String.format("%1s",MyFunctions.roundUp(mDefaultRmv,2)));
        mBinding.defaultVA.setText(String.format("%1s",mDefaultVolumeAvailable));
        mBinding.defaultVU.setText(String.format("%1s",mDefaultVolumeUsed));

        mBinding.defaultPU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.defaultVU.setTextColor(ContextCompat.getColor(this, R.color.black));

        // Reset the Other
        mOtherBeginningPressure = MyConstants.ZERO_D;
        mOtherEndingPressure = MyConstants.ZERO_D;
        mOtherPressureUsed = MyConstants.ZERO_D;
        mOtherBottomTime = MyConstants.ZERO_D;
        mOtherAvgDepth = MyConstants.ZERO_D;
        mOtherTankVolume = MyConstants.ZERO_D;
        mOtherRatedPressure = MyConstants.ZERO_D;
        mOtherAta = MyConstants.ZERO_D;
        mOtherSac = MyConstants.ZERO_D;
        mOtherTankFactor = MyConstants.ZERO_D;
        mOtherRmv = MyConstants.ZERO_D;
        mOtherVolumeAvailable = MyConstants.ZERO_D;
        mOtherVolumeUsed = MyConstants.ZERO_D;

        mCalculateRmv.setOtherBeginningPressure(mOtherBeginningPressure);
        mCalculateRmv.setOtherPressureUsed(mOtherPressureUsed);
        mCalculateRmv.setOtherBottomTime(mOtherBottomTime);
        mCalculateRmv.setOtherAvgDepth(mOtherAvgDepth);
        mCalculateRmv.setOtherTankVolume(mOtherTankVolume);
        mCalculateRmv.setOtherRatedPressure(mOtherRatedPressure);
        mCalculateRmv.setOtherAta(mOtherAta);
        mCalculateRmv.setOtherSac(mOtherSac);
        mCalculateRmv.setOtherTankFactor(mOtherTankFactor);
        mCalculateRmv.setOtherRmv(mOtherRmv);
        mCalculateRmv.setOtherVolumeAvailable(mOtherVolumeAvailable);
        mCalculateRmv.setOtherVolumeUsed(mOtherVolumeUsed);

        mBinding.otherBP.setText(String.format("%1s", mOtherBeginningPressure));
        mBinding.otherEP.setText(String.format("%1s",mOtherEndingPressure));
        mBinding.otherPU.setText(String.format("%1s",mOtherPressureUsed));
        mBinding.otherBT.setText(String.format("%1s",mOtherBottomTime));
        mBinding.otherAD.setText(String.format("%1s",mOtherAvgDepth));
        mBinding.otherTV.setText(String.format("%1s",mOtherTankVolume));
        mBinding.otherRP.setText(String.format("%1s",mOtherRatedPressure));
        mBinding.otherPU.setText(String.format("%1s",mOtherPressureUsed));
        mBinding.otherAta.setText(String.format("%1s",MyFunctions.roundUp(mOtherAta,2)));
        mBinding.otherSac.setText(String.format("%1s",mOtherSac));
        mBinding.otherTF.setText(String.format("%1s",MyFunctions.roundDown(mOtherTankFactor,5)));
        mBinding.otherRmv.setText(String.format("%1s",MyFunctions.roundUp(mOtherRmv,2)));
        mBinding.otherVA.setText(String.format("%1s",mOtherVolumeAvailable));
        mBinding.otherVU.setText(String.format("%1s",mOtherVolumeUsed));

        mBinding.otherPU.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.otherVU.setTextColor(ContextCompat.getColor(this, R.color.black));

        requestFocus(mBinding.defaultBP, false);
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
        // Since the App's default is in IMPERIAL
        // The FIRST switch is ALWAYS from IMPERIAL to METRIC

        // Save the Other side
        String otherUnit = mBinding.otherUnit.getText().toString();

        String otherBeginningPressure;
        String otherEndingPressure;
        String otherPressureUsed;
        String otherAverageDepth;

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Convert new other values to Imperial
            // If default values are different than 0
            // and other values are equal to 0
            if (!mCalculateRmv.getDefaultBeginningPressure().equals(MyConstants.ZERO_D)
                && mCalculateRmv.getOtherBeginningPressure().equals(MyConstants.ZERO_D)) {
                Double pressure = mCalculateRmv.getDefaultBeginningPressure();
                pressure = mMyCalcMetric.convertPsiToBar(pressure);
                mCalculateRmv.setOtherBeginningPressure(pressure);
                otherBeginningPressure = String.format("%1s",MyFunctions.roundUp(pressure,1));
            } else {
                otherBeginningPressure = mBinding.otherBP.getText().toString();
            }
            if (!mCalculateRmv.getDefaultEndingPressure().equals(MyConstants.ZERO_D)
                    && mCalculateRmv.getOtherEndingPressure().equals(MyConstants.ZERO_D)) {
                Double pressure = mCalculateRmv.getDefaultEndingPressure();
                pressure = mMyCalcMetric.convertPsiToBar(pressure);
                mCalculateRmv.setOtherEndingPressure(pressure);
                otherEndingPressure = String.format("%1s",MyFunctions.roundUp(pressure,1));
            } else {
                otherEndingPressure = mBinding.otherEP.getText().toString();
            }
            if (!mCalculateRmv.getDefaultPressureUsed().equals(MyConstants.ZERO_D)
                    && mCalculateRmv.getOtherPressureUsed().equals(MyConstants.ZERO_D)) {
                Double pressure = mCalculateRmv.getDefaultPressureUsed();
                pressure = mMyCalcMetric.convertPsiToBar(pressure);
                mCalculateRmv.setOtherPressureUsed(pressure);
                otherPressureUsed = String.format("%1s",MyFunctions.roundUp(pressure,1));
            } else {
                otherPressureUsed = mBinding.otherPU.getText().toString();
            }
            if (!mCalculateRmv.getDefaultAvgDepth().equals(MyConstants.ZERO_D)
                    && mCalculateRmv.getOtherAvgDepth().equals(MyConstants.ZERO_D)) {
                Double depth = mCalculateRmv.getDefaultAvgDepth();
                depth = mMyCalcMetric.convertFeetToMeter(depth);
                mCalculateRmv.setOtherAvgDepth(depth);
                otherAverageDepth = String.format("%1s",MyFunctions.roundUp(depth,1));
            } else {
                otherAverageDepth = mBinding.otherAD.getText().toString();
            }
        } else {
            // Convert new other values to Metric
            // If default values are different than 0
            // and other values are equal to 0
            if (!mCalculateRmv.getDefaultBeginningPressure().equals(MyConstants.ZERO_D)
                    && mCalculateRmv.getOtherBeginningPressure().equals(MyConstants.ZERO_D)) {
                Double pressure = mCalculateRmv.getDefaultBeginningPressure();
                pressure = mMyCalcImperial.convertBarToPsi(pressure);
                mCalculateRmv.setOtherBeginningPressure(pressure);
                otherBeginningPressure = String.format("%1s",MyFunctions.roundUp(pressure,1));
            } else {
                otherBeginningPressure = mBinding.otherBP.getText().toString();
            }
            if (!mCalculateRmv.getDefaultEndingPressure().equals(MyConstants.ZERO_D)
                    && mCalculateRmv.getOtherEndingPressure().equals(MyConstants.ZERO_D)) {
                Double pressure = mCalculateRmv.getDefaultEndingPressure();
                pressure = mMyCalcImperial.convertBarToPsi(pressure);
                mCalculateRmv.setOtherEndingPressure(pressure);
                otherEndingPressure = String.format("%1s",MyFunctions.roundUp(pressure,1));
            } else {
                otherEndingPressure = mBinding.otherEP.getText().toString();
            }
            if (!mCalculateRmv.getDefaultPressureUsed().equals(MyConstants.ZERO_D)
                    && mCalculateRmv.getOtherPressureUsed().equals(MyConstants.ZERO_D)) {
                Double pressure = mCalculateRmv.getDefaultPressureUsed();
                pressure = mMyCalcImperial.convertBarToPsi(pressure);
                mCalculateRmv.setOtherPressureUsed(pressure);
                otherPressureUsed = String.format("%1s",MyFunctions.roundUp(pressure,1));
            } else {
                otherPressureUsed = mBinding.otherEP.getText().toString();
            }
            if (!mCalculateRmv.getDefaultAvgDepth().equals(MyConstants.ZERO_D)
                    && mCalculateRmv.getOtherAvgDepth().equals(MyConstants.ZERO_D)) {
                Double depth = mCalculateRmv.getDefaultAvgDepth();
                depth = mMyCalcImperial.convertMeterToFeet(depth);
                mCalculateRmv.setOtherAvgDepth(depth);
                otherAverageDepth = String.format("%1s",MyFunctions.roundUp(depth,1));
            } else {
                otherAverageDepth = mBinding.otherAD.getText().toString();
            }
        }

        String otherBeginningPressureLbl = mBinding.otherBPLbl.getText().toString();
        String otherBottomTime = mBinding.defaultBT.getText().toString();
        String otherAverageDepthLbl = mBinding.otherADLbl.getText().toString();
        String otherTankVolume = mBinding.otherTV.getText().toString();
        String otherTankVolumeLbl = mBinding.otherTVLbl.getText().toString();
        String otherRatedPressure = mBinding.otherRP.getText().toString();
        String otherRatedPressureLbl = mBinding.otherRPLbl.getText().toString();
        String otherAta = mBinding.otherAta.getText().toString();
        String otherAtaLbl = mBinding.otherAtaLbl.getText().toString();
        String otherSac = mBinding.otherSac.getText().toString();
        String otherSacLbl = mBinding.otherSacLbl.getText().toString();
        int otherTankFactorVisibility = mBinding.otherTF.getVisibility();
        String otherTankFactor = mBinding.otherTF.getText().toString();
        String otherTankFactorLbl = mBinding.otherTFLbl.getText().toString();
        String otherRmv = mBinding.otherRmv.getText().toString();
        String otherRmvLbl = mBinding.otherRmvLbl.getText().toString();
        String otherVolumeAvailable = mBinding.otherVA.getText().toString();
        String otherVolumeAvailableLbl = mBinding.otherVALbl.getText().toString();
        String otherVolumeUsed = mBinding.otherVU.getText().toString();
        String otherVolumeUsedLbl = mBinding.otherVULbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherBP.setText(mBinding.defaultBP.getText().toString());
        mBinding.otherBPLbl.setText(mBinding.defaultBPLbl.getText().toString());
        mBinding.otherEP.setText(mBinding.defaultEP.getText().toString());
        mBinding.otherEPLbl.setText(mBinding.defaultEPLbl.getText().toString());
        mBinding.otherPU.setText(mBinding.defaultPU.getText().toString());
        mBinding.otherPULbl.setText(mBinding.defaultPULbl.getText().toString());
        mBinding.otherBT.setText(mBinding.defaultBT.getText().toString());
        mBinding.otherAD.setText(mBinding.defaultAD.getText().toString());
        mBinding.otherADLbl.setText(mBinding.defaultADLbl.getText().toString());
        mBinding.otherTV.setText(mBinding.defaultTV.getText().toString());
        mBinding.otherTVLbl.setText(mBinding.defaultTVLbl.getText().toString());
        mBinding.otherRP.setVisibility(mBinding.defaultRP.getVisibility());
        mBinding.otherRP.setText(mBinding.defaultRP.getText().toString());
        mBinding.otherRPLbl.setVisibility(mBinding.defaultRPLbl.getVisibility());
        mBinding.otherRPLbl.setText(mBinding.defaultRPLbl.getText().toString());
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            // Imperial becomes the Other
            mBinding.otherRP.setVisibility(View.VISIBLE);
            mBinding.otherRPLbl.setVisibility(View.VISIBLE);
        } else {
            // Metric becomes the Other
            mBinding.otherRP.setVisibility(View.INVISIBLE);
            mBinding.otherRPLbl.setVisibility(View.INVISIBLE);
        }
        mBinding.otherTF.setVisibility(mBinding.defaultTF.getVisibility());
        mBinding.otherTF.setText(mBinding.defaultTF.getText().toString());
        mBinding.otherTFLbl.setText(mBinding.defaultTFLbl.getText().toString());
        mBinding.otherAta.setText(mBinding.defaultAta.getText().toString());
        mBinding.otherAtaLbl.setText(mBinding.defaultAtaLbl.getText().toString());
        mBinding.otherSac.setText(mBinding.defaultSac.getText().toString());
        mBinding.otherSacLbl.setText(mBinding.defaultSacLbl.getText().toString());
        mBinding.otherRmv.setText(mBinding.defaultRmv.getText().toString());
        mBinding.otherRmvLbl.setText(mBinding.defaultRmvLbl.getText().toString());
        mBinding.otherVA.setText(mBinding.defaultVA.getText().toString());
        mBinding.otherVALbl.setText(mBinding.defaultVALbl.getText().toString());
        mBinding.otherVU.setText(mBinding.defaultVU.getText().toString());
        mBinding.otherVULbl.setText(mBinding.defaultVULbl.getText().toString());

        // Switch Other Side to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultBP.setText(otherBeginningPressure);
        mBinding.defaultBPLbl.setText(otherBeginningPressureLbl);
        mBinding.defaultEP.setText(otherEndingPressure);
        mBinding.defaultEPLbl.setText(otherBeginningPressureLbl);
        mBinding.defaultPU.setText(otherPressureUsed);
        mBinding.defaultPULbl.setText(otherBeginningPressureLbl);
        mBinding.defaultBT.setText(otherBottomTime);
        mBinding.defaultAD.setText(otherAverageDepth);
        mBinding.defaultADLbl.setText(otherAverageDepthLbl);
        mBinding.defaultTV.setText(otherTankVolume);
        mBinding.defaultTVLbl.setText(otherTankVolumeLbl);

        mBinding.defaultRP.setText(otherRatedPressure);
        mBinding.defaultRPLbl.setText(otherRatedPressureLbl);

        mBinding.defaultTF.setVisibility(otherTankFactorVisibility);
        mBinding.defaultTF.setText(otherTankFactor);
        mBinding.defaultTFLbl.setText(otherTankFactorLbl);
        mBinding.defaultAta.setText(otherAta);
        mBinding.defaultAtaLbl.setText(otherAtaLbl);
        mBinding.defaultSac.setText(otherSac);
        mBinding.defaultSacLbl.setText(otherSacLbl);
        mBinding.defaultRmv.setText(otherRmv);
        mBinding.defaultRmvLbl.setText(otherRmvLbl);
        mBinding.defaultVA.setText(otherVolumeAvailable);
        mBinding.defaultVALbl.setText(otherVolumeAvailableLbl);
        mBinding.defaultVU.setText(otherVolumeUsed);
        mBinding.defaultVULbl.setText(otherVolumeUsedLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateRmvActivity) {
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

    private Double getDefaultConsumption() {
        mDefaultBeginningPressure = mCalculateRmv.getDefaultBeginningPressure();
        mDefaultEndingPressure = mCalculateRmv.getDefaultEndingPressure();
        if (       !mBinding.defaultBP.getText().toString().trim().isEmpty()
                && !mBinding.defaultEP.getText().toString().trim().isEmpty()
                && !mCalculateRmv.getDefaultBeginningPressure().equals(MyConstants.ZERO_D)
                && !mCalculateRmv.getDefaultEndingPressure().equals(MyConstants.ZERO_D)) {
            mDefaultPressureUsed = mCalculateRmv.getDefaultBeginningPressure() - mCalculateRmv.getDefaultEndingPressure();
            return MyFunctions.roundUp(mDefaultPressureUsed,1);
        } else
            mDefaultPressureUsed = mCalculateRmv.getDefaultBeginningPressure();
        return MyFunctions.roundUp(mDefaultPressureUsed,1);
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
        mDefaultTankVolume = Double.parseDouble(Float.valueOf(preferences.getFloat(getString(R.string.code_default_tank_volume), MyConstants.ZERO_F)).toString());
        mDefaultRatedPressure = Double.parseDouble(Float.toString(preferences.getFloat(getString(R.string.code_default_rated_pressure2), MyConstants.ZERO_F)));

        // Set the values in the POJO
        mCalculateRmv.setDefaultSalinity(mDefaultSalinity);
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            mCalculateRmv.setDefaultSalinityPosition(0);
        } else {
            mCalculateRmv.setDefaultSalinityPosition(1);
        }
        mCalculateRmv.setDefaultTankVolume(mDefaultTankVolume);
        mCalculateRmv.setDefaultRatedPressure(mDefaultRatedPressure);

        // Set the values on the activity
        mBinding.defaultTV.setText(String.format("%1s",mDefaultTankVolume));
        mBinding.defaultRP.setText(String.format("%1s",mDefaultRatedPressure));
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putBoolean(getString(R.string.code_default_salinity), mCalculateRmv.getDefaultSalinity());
        edit.putFloat(getString(R.string.code_default_tank_volume),Float.parseFloat(String.valueOf(mCalculateRmv.getDefaultTankVolume())));
        edit.putFloat(getString(R.string.code_default_rated_pressure2),Float.parseFloat(String.valueOf(mCalculateRmv.getDefaultRatedPressure())));
        edit.apply();
    }
}
