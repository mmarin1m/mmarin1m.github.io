package ca.myairbuddyandi;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.Objects;

import ca.myairbuddyandi.databinding.CalculateDaltonLawActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateDaltonLawActivity class
 */

public class CalculateDaltonLawActivity extends AppCompatActivity {
    // Static
    private static final String LOG_TAG = "CalculateDaltonLawActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateDaltonLaw mCalculateDaltonLaw = new CalculateDaltonLaw();
    private CalculateDaltonLawActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    // If the phone is in Imperial, Default variables will contains Imperial values
    // And Metric values upon the Switch from Imperial to Metric
    // If the phone is in Metric, Default variables will contains Metric values
    // And Imperial values upon the Switch from Metric to Imperial
    private boolean mDefaultSalinity; // true = Salt = 0 position, false = Fresh = 1 position
    private Double mDefaultAta;
    private Double mDefaultBestMixHe;
    private Double mDefaultBestMixN2;
    private Double mDefaultBestMixO2;
    private Double mDefaultEabd;
    private Double mDefaultEnd;
    private Double mDefaultEndN2Narc;
    private Double mDefaultEndN2O2Narc;
    private Double mDefaultEndN2O2HeNarc;
    private Double mDefaultMod;
    private Double mDefaultPartialPressure;
    private Double mPreferenceEnd;
    private String mDefaultUnit;

    // Other
    // Contains the opposite unit of measure than the Default
    // If the phone is in Imperial, Other variables will contains Metric values
    // And Imperial values upon the Switch from Imperial to Metric
    // If the phone is in Metric, Other variables will contains Imperial values
    // And Metric values upon the Switch from Metric to Imperial
    private Double mOtherAta;
    private Double mOtherBestMixHe;
    private Double mOtherBestMixN2;
    private Double mOtherBestMixO2;
    private Double mOtherEabd;
    private Double mOtherEnd;
    private Double mOtherEndN2Narc;
    private Double mOtherEndN2O2Narc;
    private Double mOtherEndN2O2HeNarc;
    private Double mOtherMod;
    private Double mOtherPartialPressure;

    // End of variables

    // Example: With             1.4 ata PPO2  22.00% O2  27.90% He  50.10% N2, 177.0 ft MOD END 100.0
    // When Calculating PPO2     ?.? ata PPO2  22.00% O2  27.90% He  --.00% N2, 177.0 ft MOD
    //                           1.4 ata                             50.10% N2
    // When calculating Best Mix 1.4 ata PPO2  ??.??% O2  ??.??% He  --.--% N2, 177.0 ft MOD END 100.0
    //                                         22.00% O2  27.90% He  50.10%
    // When calculating MOD      1.4 ata PPO2  22.00% O2  27.90% He  --.--% N2, ???.? ft MOD END 100.0
    //                                                               50.10%     177.0 ft

    // Example: With             1.4 ata PPO2  21.69% O2  28.91% He  49.40% N2, 180.0 ft MOD END 100.0
    // When Calculating PPO2     ?.? ata PPO2  21.69% O2  28.91% He  --.--% N2, 180.0 ft MOD
    //                           1.4 ata                             49.40% N2
    // When calculating Best Mix 1.4 ata PPO2  ??.??% O2  ??.??% He  --.--% N2, 180.0 ft MOD END 100.0
    //                                         21.69% O2  28.91% He  49.40%
    // When calculating MOD      1.4 ata PPO2  21.69% O2  28.92% He  --.--% N2, ???.? ft MOD END 100.0
    //                                                               49.40%     180.0 ft

    // Showing 2 decimals when calculating Best Mix because Multi Deco shows the same Best Mix on 22/29
    // between 175 and 180.
    // And only calculates with an increment of 5 feet

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_dalton_law_activity);

        mCalculateDaltonLaw.mBinding = mBinding;

        mBinding.setCalculateDaltonLaw(mCalculateDaltonLaw);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        //

        mBinding.calculatePartialPressureButton.setOnClickListener(view -> partialPressure());

        mBinding.calculatePartialPressureButton2.setOnClickListener(view -> partialPressure());

        //

        mBinding.calculateBestMixButton.setOnClickListener(view -> bestMix());

        mBinding.calculateBestMixButton2.setOnClickListener(view -> bestMix());

        //

        mBinding.calculateModButton.setOnClickListener(view -> mod());

        mBinding.calculateModButton2.setOnClickListener(view -> mod());

        //

        mBinding.calculateEndButton.setOnClickListener(view -> end());

        mBinding.calculateEndButton2.setOnClickListener(view -> end());

        //

        mBinding.clearButton.setOnClickListener(view -> clear());

        //Set the data in the Spinner Salinity
        String[] itemsDefaultSalinity = getResources().getStringArray(R.array.salinity_arrays);
        ArrayAdapter<String> adapterDefaultSalinity = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsDefaultSalinity);
        adapterDefaultSalinity.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculateDaltonLaw.setAdapterDefaultSalinity(adapterDefaultSalinity);

        //Set the data in the Spinner Trimix
        String[] itemsDefaultTrimix = getResources().getStringArray(R.array.trimix_arrays);
        ArrayAdapter<String> adapterDefaultTrimix = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsDefaultTrimix);
        adapterDefaultTrimix.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculateDaltonLaw.setAdapterDefaultTrimix(adapterDefaultTrimix);

        // Set the data in the Spinner Optimize END
        ArrayList<EndType> endTypeList = getAllEnd();
        ArrayAdapter<EndType> adapterDefaultOptimizeEnd = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, endTypeList);
        adapterDefaultOptimizeEnd.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculateDaltonLaw.setAdapterDefaultOptimizeEnd(adapterDefaultOptimizeEnd);

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateDaltonLawActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_dalton_law));
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

    // My functions

    // Main entry

    @SuppressLint("StringFormatMatches")
    private void bestMix() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            if (mCalculateDaltonLaw.getDefaultTrimix()) {
                // Calculate Nitrox
                calculateImperialBestMix();
                // Calculate all Metric values
                copyToOtherPartialPressure();
                calculateToMetricBestMixO2();
                calculateToMetricBestMixN2();
            } else {
                // Calculate Trimix
                calculateImperialBestMixTrimix();
                calculateImperialNarc(mDefaultBestMixO2, mDefaultBestMixHe, mDefaultBestMixN2);
                calculateImperialEabd(mDefaultBestMixO2, mDefaultBestMixHe);
                // Calculate all Metric values
                copyToOtherPartialPressure();
                convertToMetricMod();
                convertToMetricEnd();
                calculateToMetricBestMixTrimix();
                calculateToMetricNarc(mOtherBestMixO2, mOtherBestMixHe, mOtherBestMixN2);
                calculateToMetricEabd(mOtherBestMixO2, mOtherBestMixHe);
            }
            convertToMetricMod();
        } else {
            if (mCalculateDaltonLaw.getDefaultTrimix()) {
                // Calculate Nitrox
                calculateMetricBestMix();
                // Calculate all Imperial values
                copyToOtherPartialPressure();
                calculateToImperialBestMixO2();
                calculateToImperialBestMixN2();
            } else {
                // Calculate Trimix
                calculateMetricBestMixTrimix();
                calculateMetricNarc(mDefaultBestMixO2, mDefaultBestMixHe, mDefaultBestMixN2);
                calculateMetricEabd(mDefaultBestMixO2, mDefaultBestMixHe);
                // Calculate all Imperial values
                copyToOtherPartialPressure();
                convertToImperialMod();
                convertToImperialEnd();
                calculateToImperialBestMixTrimix();
                calculateToImperialNarc(mOtherBestMixO2, mOtherBestMixHe, mOtherBestMixN2);
                calculateToImperialEabd(mOtherBestMixO2, mOtherBestMixHe);
            }
            convertToImperialMod();
        }

        // Reset all of the Default values in order to reformat the values e.g. 23% becomes 23.0%
        mBinding.defaultPartialPressure.setText(String.valueOf(mCalculateDaltonLaw.getDefaultPartialPressure()));
        mBinding.defaultBestMixO2.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixO2()));
        mBinding.defaultBestMixHe.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixHe()));

        String lblPpHe = getString(R.string.lbl_ppHe);
        double ppHe;
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            ppHe = MyFunctions.roundUp(mMyCalcImperial.getPartialPressure(mDefaultBestMixHe / 100, mDefaultAta), 2);
        } else {
            ppHe = MyFunctions.roundUp(mMyCalcMetric.getPartialPressure(mDefaultBestMixHe / 100, mDefaultAta), 2);
        }
        lblPpHe = String.format(lblPpHe
                , (ppHe >= 4.25) ? "#FF0000" : "#000000"
                , ppHe
                , (mDefaultUnit.equals(MyConstants.IMPERIAL) ? getString(R.string.lbl_ata) : getString(R.string.lbl_bar)));
        mBinding.hdrBestMixHeLbl.setText(MyFunctions.fromHtml(lblPpHe));

        mBinding.defaultBestMixN2.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixN2()));

        String lblPpN2 = getString(R.string.lbl_ppN2);
        double ppN2;
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            ppN2 = MyFunctions.roundUp(mMyCalcImperial.getPartialPressure(mDefaultBestMixN2 / 100, mDefaultAta), 2);
        } else {
            ppN2 = MyFunctions.roundUp(mMyCalcMetric.getPartialPressure(mDefaultBestMixN2 / 100, mDefaultAta), 2);
        }
        lblPpN2 = String.format(lblPpN2, ppN2, (mDefaultUnit.equals(MyConstants.IMPERIAL) ? getString(R.string.lbl_ata) : getString(R.string.lbl_bar)));
        mBinding.hdrBestMixN2Lbl.setText(lblPpN2);

        mBinding.defaultMod.setText(String.valueOf(mCalculateDaltonLaw.getDefaultMod()));
        mBinding.defaultEnd.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEnd()));
        mBinding.defaultEndN2Narc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2Narc()));
        mBinding.defaultEndN2O2Narc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2O2Narc()));
        mBinding.defaultEndN2O2HeNarc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2O2HeNarc()));
        mBinding.defaultEabd.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEabd()));

        requestFocus(mBinding.defaultPartialPressure, false);
    }

    @SuppressLint("StringFormatMatches")
    private void end() {

        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2();
        mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe();
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();

        String lblPpHe = getString(R.string.lbl_ppHe);
        double ppHe;
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            mDefaultAta = mMyCalcImperial.getAta(mDefaultMod,mDefaultSalinity);
            ppHe = MyFunctions.roundUp(mMyCalcImperial.getPartialPressure(mDefaultBestMixHe / 100, mDefaultAta), 2);
        } else {
            mDefaultAta = mMyCalcMetric.getBar(mDefaultMod,mDefaultSalinity);
            ppHe = MyFunctions.roundUp(mMyCalcMetric.getPartialPressure(mDefaultBestMixHe / 100, mDefaultAta), 2);
        }
        lblPpHe = String.format(lblPpHe
                , (ppHe >= 4.25) ? "#FF0000" : "#000000"
                , ppHe
                , (mDefaultUnit.equals(MyConstants.IMPERIAL) ? getString(R.string.lbl_ata) : getString(R.string.lbl_bar)));
        mBinding.hdrBestMixHeLbl.setText(MyFunctions.fromHtml(lblPpHe));

        mDefaultBestMixN2 = 100.0 - mDefaultBestMixO2 - mDefaultBestMixHe;

        String lblPpN2 = getString(R.string.lbl_ppN2);
        double ppN2;
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            ppN2 = MyFunctions.roundUp(mMyCalcImperial.getPartialPressure(mDefaultBestMixN2 / 100, mDefaultAta), 2);
        } else {
            ppN2 = MyFunctions.roundUp(mMyCalcMetric.getPartialPressure(mDefaultBestMixN2 / 100, mDefaultAta), 2);
        }
        lblPpN2 = String.format(lblPpN2, ppN2, (mDefaultUnit.equals(MyConstants.IMPERIAL) ? getString(R.string.lbl_ata) : getString(R.string.lbl_bar)));
        mBinding.hdrBestMixN2Lbl.setText(lblPpN2);

        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2,2));
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();
        mDefaultEnd = MyConstants.ZERO_D;
        mCalculateDaltonLaw.setDefaultEnd(mDefaultEnd);

        mOtherBestMixO2 = mDefaultBestMixO2;
        mOtherBestMixHe = mDefaultBestMixHe;
        mOtherBestMixN2 = mDefaultBestMixN2;
        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            mOtherMod = mMyCalcMetric.convertFeetToMeter(mDefaultMod);
            mOtherAta = mMyCalcMetric.getBar(mOtherMod,mDefaultSalinity);
        } else {
            mOtherMod = mMyCalcImperial.convertMeterToFeet(mDefaultMod);
            mOtherAta = mMyCalcImperial.getAta(mOtherMod,mDefaultSalinity);
        }

        mOtherEnd = MyConstants.ZERO_D;
        mCalculateDaltonLaw.setOtherEnd(mOtherEnd);

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialNarc(mDefaultBestMixO2, mDefaultBestMixHe, mDefaultBestMixN2);//22.0, 27.9, 50.1
            calculateImperialEabd(mDefaultBestMixO2, mDefaultBestMixHe);// 22.0, 27.9
            // Calculate/Copy all Metric values
            copyToOtherPartialPressure();
            mCalculateDaltonLaw.setOtherBestMixO2(mOtherBestMixO2);//22.0
            mCalculateDaltonLaw.setOtherBestMixHe(mOtherBestMixHe);//27.9
            mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));//50.1
            mCalculateDaltonLaw.setOtherMod(MyFunctions.roundDown(mOtherMod,2));//53.94
            mCalculateDaltonLaw.setOtherEnd(MyFunctions.roundDown(mOtherEnd,2));//0.0
            calculateToMetricNarc(mOtherBestMixO2, mOtherBestMixHe, mOtherBestMixN2);//22.0, 27.9, 50.1
            calculateToMetricEabd(mOtherBestMixO2, mOtherBestMixHe);//22.0, 27.9
        } else {
            calculateMetricNarc(mDefaultBestMixO2, mDefaultBestMixHe, mDefaultBestMixN2);//22.0, 27.9, 50.1
            calculateMetricEabd(mDefaultBestMixO2, mDefaultBestMixHe);//22.0, 27.9
            // Calculate/Copy all Imperial values
            copyToOtherPartialPressure();
            mCalculateDaltonLaw.setOtherBestMixO2(mOtherBestMixO2);//22.0
            mCalculateDaltonLaw.setOtherBestMixHe(mOtherBestMixHe);//27.9
            mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));//50.1
            mCalculateDaltonLaw.setOtherMod(MyFunctions.roundDown(mOtherMod,2));//16.3
            mCalculateDaltonLaw.setOtherEnd(MyFunctions.roundDown(mOtherEnd,2));//0.0
            calculateToImperialNarc(mOtherBestMixO2, mOtherBestMixHe, mOtherBestMixN2);//22.0, 27.9, 50.1
            calculateToImperialEabd(mOtherBestMixO2, mOtherBestMixHe);//22.0, 27.9
        }

        mBinding.defaultEnd.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEnd()));
        mBinding.otherEnd.setText(String.valueOf(mCalculateDaltonLaw.getOtherEnd()));
        mBinding.defaultBestMixN2.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixN2()));
        mBinding.defaultEndN2Narc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2Narc()));
        mBinding.defaultEndN2O2Narc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2O2Narc()));
        mBinding.defaultEndN2O2HeNarc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2O2HeNarc()));
        mBinding.defaultEabd.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEabd()));
    }

    @SuppressLint("StringFormatMatches")
    private void mod() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            if (mCalculateDaltonLaw.getDefaultTrimix()) {
                // Calculate Nitrox
                calculateImperialMod();
                mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe();
                // Calculate all Metric values
                copyToOtherPartialPressure();
                calculateToMetricBestMixO2();
                calculateToMetricMod();
            } else {
                // Calculate Trimix
                calculateImperialModTrimix();
                mDefaultBestMixO2 = MyFunctions.round(mDefaultBestMixO2 * 100.0,2);
                mDefaultBestMixHe = MyFunctions.round(mDefaultBestMixHe * 100.0,2);
                mDefaultBestMixN2 = 100.0 - mDefaultBestMixO2 - mDefaultBestMixHe;
                calculateImperialEabd(mDefaultBestMixO2,mDefaultBestMixHe);
                // Calculate all Metric values
                copyToOtherBestMixO2();
                copyToOtherBestMixHe();
                copyToOtherBestMixN2();
                calculateImperialNarc(mDefaultBestMixO2, mDefaultBestMixHe, mDefaultBestMixN2);
                calculateToMetricModTrimix();
                convertToMetricEnd();
                calculateToMetricNarc(mOtherBestMixO2, mOtherBestMixHe, mOtherBestMixN2);
                calculateToMetricEabd(mOtherBestMixO2, mOtherBestMixHe);
            }
        } else {
            if (mCalculateDaltonLaw.getDefaultTrimix()) {
                // Calculate Nitrox
                calculateMetricMod();
                mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe();
                // Calculate all Imperial values
                copyToOtherPartialPressure();
                calculateToImperialBestMixO2();
                calculateToImperialMod();
            } else {
                // Calculate Trimix
                calculateMetricModTrimix();
                mDefaultBestMixO2 = MyFunctions.round(mDefaultBestMixO2 * 100.0,2);
                mDefaultBestMixHe = MyFunctions.round(mDefaultBestMixHe * 100.0,2);
                mDefaultBestMixN2 = 100.0 - mDefaultBestMixO2 - mDefaultBestMixHe;
                calculateMetricEabd(mDefaultBestMixO2,mDefaultBestMixHe);
                // Calculate all Metric values
                copyToOtherBestMixO2();
                copyToOtherBestMixHe();
                copyToOtherBestMixN2();
                calculateMetricNarc(mDefaultBestMixO2, mDefaultBestMixHe, mDefaultBestMixN2);
                calculateToImperialModTrimix();
                convertToImperialEnd();
                calculateToImperialNarc(mOtherBestMixO2, mOtherBestMixHe, mOtherBestMixN2);
                calculateToImperialEabd(mOtherBestMixO2, mOtherBestMixHe);
            }
        }

        // Reset all of the Default values in order to reformat the values e.g. 23% becomes 23.0%
        mBinding.defaultPartialPressure.setText(String.valueOf(mCalculateDaltonLaw.getDefaultPartialPressure()));
        mBinding.defaultBestMixO2.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixO2()));
        mBinding.defaultBestMixHe.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixHe()));

        String lblPpHe = getString(R.string.lbl_ppHe);
        double ppHe;
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            ppHe = MyFunctions.roundUp(mMyCalcImperial.getPartialPressure((mDefaultBestMixHe * 100.0) / 100, mDefaultAta), 2);
        } else {
            ppHe = MyFunctions.roundUp(mMyCalcMetric.getPartialPressure((mDefaultBestMixHe * 100.0) / 100, mDefaultAta), 2);
        }
        lblPpHe = String.format(lblPpHe
                , (ppHe >= 4.25) ? "#FF0000" : "#000000"
                , ppHe
                , (mDefaultUnit.equals(MyConstants.IMPERIAL) ? getString(R.string.lbl_ata) : getString(R.string.lbl_bar)));
        mBinding.hdrBestMixHeLbl.setText(MyFunctions.fromHtml(lblPpHe));

        mBinding.defaultBestMixN2.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixN2()));

        String lblPpN2 = getString(R.string.lbl_ppN2);
        double ppN2;
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            ppN2 = MyFunctions.roundUp(mMyCalcImperial.getPartialPressure((mDefaultBestMixN2 * 100.0) / 100, mDefaultAta), 2);
        } else {
            ppN2 = MyFunctions.roundUp(mMyCalcMetric.getPartialPressure((mDefaultBestMixN2 * 100.0) / 100, mDefaultAta), 2);
        }
        lblPpN2 = String.format(lblPpN2, ppN2, (mDefaultUnit.equals(MyConstants.IMPERIAL) ? getString(R.string.lbl_ata) : getString(R.string.lbl_bar)));
        mBinding.hdrBestMixN2Lbl.setText(lblPpN2);

        mBinding.defaultMod.setText(String.valueOf(mCalculateDaltonLaw.getDefaultMod()));
        mBinding.defaultEnd.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEnd()));
        mBinding.defaultEndN2Narc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2Narc()));
        mBinding.defaultEndN2O2Narc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2O2Narc()));
        mBinding.defaultEndN2O2HeNarc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2O2HeNarc()));
        mBinding.defaultEabd.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEabd()));

        requestFocus(mBinding.defaultPartialPressure, false);
    }

    @SuppressLint("StringFormatMatches")
    private void partialPressure() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {

            calculateImperialPartialPressure();
            calculateImperialNarc(MyFunctions.roundUp(mDefaultBestMixO2 * 100.0,1), MyFunctions.roundUp(mDefaultBestMixHe * 100.0,1), mDefaultBestMixN2);
            calculateImperialEabd(MyFunctions.roundUp(mDefaultBestMixO2 * 100.0,1), MyFunctions.roundUp(mDefaultBestMixHe * 100.0,1));
            // Calculate all Metric values
            copyToOtherPartialPressure();
            mDefaultBestMixO2 = MyFunctions.roundUp(mDefaultBestMixO2 * 100.0,1);
            mDefaultBestMixHe = MyFunctions.roundUp(mDefaultBestMixHe * 100.0,1);
            copyToOtherBestMixO2();
            copyToOtherBestMixHe();
            copyToOtherBestMixN2();
            convertToMetricMod();
            convertToMetricEnd();
            mOtherAta = mMyCalcMetric.getBar(mOtherMod,mDefaultSalinity);
            calculateToMetricNarc(mOtherBestMixO2, mOtherBestMixHe, mOtherBestMixN2);
            calculateToMetricEabd(mOtherBestMixO2, mOtherBestMixHe);
        } else {
            calculateMetricPartialPressure();
            calculateMetricNarc(MyFunctions.roundUp(mDefaultBestMixO2 * 100.0,1), MyFunctions.roundUp(mDefaultBestMixHe * 100.0,1), mDefaultBestMixN2);
            calculateMetricEabd(MyFunctions.roundUp(mDefaultBestMixO2 * 100.0,1), MyFunctions.roundUp(mDefaultBestMixHe * 100.0,1));
            // Calculate all Imperial values
            copyToOtherPartialPressure();
            mDefaultBestMixO2 = MyFunctions.roundUp(mDefaultBestMixO2 * 100.0,1);
            mDefaultBestMixHe = MyFunctions.roundUp(mDefaultBestMixHe * 100.0,1);
            copyToOtherBestMixO2();
            copyToOtherBestMixHe();
            copyToOtherBestMixN2();
            convertToImperialMod();
            convertToImperialEnd();
            mOtherAta = mMyCalcImperial.getAta(mOtherMod,mDefaultSalinity);
            calculateToImperialNarc(mOtherBestMixO2, mOtherBestMixHe, mOtherBestMixN2);
            calculateToImperialEabd(mOtherBestMixO2, mOtherBestMixHe);
        }

        // Reset all of the Default values in order to reformat the values e.g. 23% becomes 23.0%
        mBinding.defaultPartialPressure.setText(String.valueOf(mCalculateDaltonLaw.getDefaultPartialPressure()));
        mBinding.defaultBestMixO2.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixO2()));
        mBinding.defaultBestMixHe.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixHe()));

        String lblPpHe = getString(R.string.lbl_ppHe);
        double ppHe;
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            ppHe = MyFunctions.roundUp(mMyCalcImperial.getPartialPressure(mDefaultBestMixHe / 100, mDefaultAta), 2);
        } else {
            ppHe = MyFunctions.roundUp(mMyCalcMetric.getPartialPressure(mDefaultBestMixHe / 100, mDefaultAta), 2);
        }
        lblPpHe = String.format(lblPpHe
                , (ppHe >= 4.25) ? "#FF0000" : "#000000"
                , ppHe
                , (mDefaultUnit.equals(MyConstants.IMPERIAL) ? getString(R.string.lbl_ata) : getString(R.string.lbl_bar)));
        mBinding.hdrBestMixHeLbl.setText(MyFunctions.fromHtml(lblPpHe));

        mBinding.defaultBestMixN2.setText(String.valueOf(mCalculateDaltonLaw.getDefaultBestMixN2()));

        String lblPpN2 = getString(R.string.lbl_ppN2);
        double ppN2;
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            ppN2 = MyFunctions.roundUp(mMyCalcImperial.getPartialPressure(mDefaultBestMixN2 / 100, mDefaultAta), 2);
        } else {
            ppN2 = MyFunctions.roundUp(mMyCalcMetric.getPartialPressure(mDefaultBestMixN2 / 100, mDefaultAta), 2);
        }
        lblPpN2 = String.format(lblPpN2, ppN2, (mDefaultUnit.equals(MyConstants.IMPERIAL) ? getString(R.string.lbl_ata) : getString(R.string.lbl_bar)));
        mBinding.hdrBestMixN2Lbl.setText(lblPpN2);

        mBinding.defaultMod.setText(String.valueOf(mCalculateDaltonLaw.getDefaultMod()));
        mBinding.defaultEnd.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEnd()));
        mBinding.defaultEndN2Narc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2Narc()));
        mBinding.defaultEndN2O2Narc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2O2Narc()));
        mBinding.defaultEndN2O2HeNarc.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEndN2O2HeNarc()));
        mBinding.defaultEabd.setText(String.valueOf(mCalculateDaltonLaw.getDefaultEabd()));

        requestFocus(mBinding.defaultPartialPressure, false);
    }

    // Calculate, convert and copy functions

    private void calculateImperialBestMix() {
        // Get all entered values first
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();

        // Convert feet to ata
        mDefaultAta = mMyCalcImperial.getAta(mDefaultMod,mDefaultSalinity);

        // Calculate the Best Mix
        mDefaultBestMixO2 = mMyCalcImperial.getBestMix(mDefaultPartialPressure,mDefaultAta) * 100.0;
        mDefaultBestMixN2 = 100.0 - mDefaultBestMixO2;
        mDefaultBestMixHe = 0.0;

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultBestMixO2(MyFunctions.roundDown(mDefaultBestMixO2,2));
        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2,2));
    }

    private void calculateImperialBestMixTrimix() {
        // Get all entered values first
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

        // Convert feet to ata
        mDefaultAta = mMyCalcImperial.getAta(mDefaultMod,mDefaultSalinity);
        Double ataEnd;
        ataEnd = mMyCalcImperial.getAta(mDefaultEnd,mDefaultSalinity);

        boolean whileCondition;
        double defaultEndN2O2Narc;
        double defaultEndN2O2HeNarc;

        switch(mCalculateDaltonLaw.getDefaultOptimizeEndType()) {
            case MyConstants.ENDN2: // END N2 Narcotic
                // Calculate the FO2
                mDefaultBestMixO2 = mMyCalcImperial.getFO2(mDefaultPartialPressure, mDefaultAta) * 100.0;

                // Calculate the FN2
                mDefaultBestMixN2 = mMyCalcImperial.getFN2(ataEnd, mDefaultAta) * 100.0;
                mDefaultBestMixN2 = (100.0 - mDefaultBestMixO2 - mDefaultBestMixN2 < 0.0) ? 100.0 - mDefaultBestMixO2 : mDefaultBestMixN2;

                // Calculate the He
                mDefaultBestMixHe = 100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2);
                break;

            case MyConstants.ENDN2O2:  // END N2 O2 Narcotic
                // Calculate the FO2
                mDefaultBestMixO2 = mMyCalcImperial.getFO2(mDefaultPartialPressure, mDefaultAta) * 100.0;

                // Calculate the FN2
                mDefaultBestMixN2 = mMyCalcImperial.getFN2(ataEnd, mDefaultAta) * 100.0;
                mDefaultBestMixN2 = (100.0 - mDefaultBestMixO2 - mDefaultBestMixN2 < 0.0) ? 100.0 - mDefaultBestMixO2 : mDefaultBestMixN2;

                // Calculate the He
                mDefaultBestMixHe = 100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2);

                whileCondition = true;

                do {
                    defaultEndN2O2Narc = Math.max(0,mMyCalcImperial.getEnd(100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2),mDefaultMod,mDefaultSalinity));
                    if (defaultEndN2O2Narc <= mDefaultEnd) {
                        whileCondition = false;
                    } else {
                        mDefaultBestMixN2 = mDefaultBestMixN2 - 0.1;
                        mDefaultBestMixHe = mDefaultBestMixHe + 0.1;
                    }
                } while (whileCondition);


                break;

            case MyConstants.ENDN2O2HE: // END N2 O2 He Narcotic
                // Calculate the FO2
                mDefaultBestMixO2 = mMyCalcImperial.getFO2(mDefaultPartialPressure, mDefaultAta) * 100.0;

                // Calculate the FN2
                mDefaultBestMixN2 = mMyCalcImperial.getFN2(ataEnd, mDefaultAta) * 100.0;
                mDefaultBestMixN2 = (100.0 - mDefaultBestMixO2 - mDefaultBestMixN2 < 0.0) ? 100.0 - mDefaultBestMixO2 : mDefaultBestMixN2;

                // Calculate the He
                mDefaultBestMixHe = 100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2);

                whileCondition = true;

                do {
                    defaultEndN2O2Narc = Math.max(0,mMyCalcImperial.getEnd(100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2),mDefaultMod,mDefaultSalinity));
                    defaultEndN2O2HeNarc = defaultEndN2O2Narc + mMyCalcImperial.getEndN2O2HeNarc(mDefaultBestMixHe, mDefaultAta, mDefaultSalinity);

                    if (defaultEndN2O2HeNarc <= mDefaultEnd) {
                        whileCondition = false;
                    } else {
                        mDefaultBestMixN2 = mDefaultBestMixN2 - 0.1;
                        mDefaultBestMixHe = mDefaultBestMixHe + 0.1;
                    }
                } while (whileCondition);
                break;
        }

        mDefaultBestMixO2 = MyFunctions.round(mDefaultBestMixO2,2);
        mDefaultBestMixHe = MyFunctions.round(mDefaultBestMixHe,2);
        mDefaultBestMixN2 = MyFunctions.round(100.0 - mDefaultBestMixO2 - mDefaultBestMixHe,2);

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultBestMixO2(mDefaultBestMixO2);
        mCalculateDaltonLaw.setDefaultBestMixHe(mDefaultBestMixHe);
        mCalculateDaltonLaw.setDefaultBestMixN2(mDefaultBestMixN2);
    }

    private void calculateImperialEabd(Double defaultBestMixO2, Double defaultBestMixHe) {
        // Calculate the Equivalent Air Breathing Depth

        //                                                           Calculating PPO2     Calculating BM     Calculating MOD
        // Example: With             1.4 ata PPO2
        //                          22.00% O2                                   22.00              22.00               22.00
        //                          27.90% He                                   27.90              27.90               27.89
        //                          50.10% N2                                   --.--              --.--               --.--
        //                         177.0 ft MOD
        //                         100.0 ft END
        // Yields:
        //                               EABD                                  128.53             128.53              128.55

        // Example: With             1.4 ata PPO2
        //                          21.69% O2                                   21.70              21.69               21.69
        //                          28.91% He                                   28.90              28.91               28.91
        //                          49.40% N2                                   --.--              --.--               --.--
        //                         180.0 ft MOD
        //                         100.0 ft END
        // Yields:
        //                               EABD                                  128.98             128.96              128.96

        mDefaultEabd = mMyCalcImperial.getEabd(defaultBestMixO2 / 100, defaultBestMixHe / 100, mDefaultAta, mDefaultSalinity);

        mCalculateDaltonLaw.setDefaultEabd(MyFunctions.roundUp(mDefaultEabd,2));
    }

    private void calculateImperialMod() {
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;

        // Calculate the MOD
        mDefaultAta = mMyCalcImperial.getMod(mDefaultPartialPressure,mDefaultBestMixO2);

        // Convert ATA to feet
        mDefaultMod = Math.max(0,mMyCalcImperial.convertAtaToDepth(mDefaultAta,mDefaultSalinity));
        mDefaultBestMixN2 = (1.0 - mDefaultBestMixO2) * 100.0;

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2,2));
        mCalculateDaltonLaw.setDefaultMod(MyFunctions.roundDown(mDefaultMod,2));
    }

    private void calculateImperialModTrimix() {
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;
        mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe() / 100.0;
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

        // Calculate the FN2
        mDefaultBestMixN2 = 1.0 - mDefaultBestMixO2 - mDefaultBestMixHe;

        // Calculate the MOD
        mDefaultAta = mMyCalcImperial.getMod(mDefaultPartialPressure,mDefaultBestMixO2);

        // Convert ATA to feet
        mDefaultMod = Math.max(0,mMyCalcImperial.convertAtaToDepth(mDefaultAta,mDefaultSalinity));

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2 * 100.0,2));
        mCalculateDaltonLaw.setDefaultMod(MyFunctions.roundDown(mDefaultMod,2));
    }

    private void calculateImperialNarc(Double defaultBestMixO2, Double defaultBestMixHe, Double defaultBestMixN2) {
        // Calculate different ENDs narcotic
        // Optimize To is used to calculate Best Mix, not the END
        // Not to calculate END narcotic

        //                                                           Calculating PPO2     Calculating BM     Calculating MOD
        // Example: With             1.4 ata PPO2
        //                          22.00% O2                                   22.00              22.00               22.00
        //                          27.90% He                                   27.90              27.90               27.89
        //                          50.10% N2                                   50.10              50.10               50.11
        //                         177.0 ft MOD
        //                         100.0 ft END
        // Yields:
        //                               END N2 Narc.                          100.00             100.00              100.04
        //                               END N2 O2 Narc.                       118.41             118.41              118.43
        //                               END N2 O2 He Narc.                     99.18              99.18               99.19

        // Example: With             1.4 ata PPO2
        //                          21.69% O2                                   21.70              21.69               21.69
        //                          28.91% He                                   28.90              28.91               28.91
        //                          49.40% N2                                   48.40              49.40               49.40
        //                         180.0 ft MOD
        //                         100.0 ft END
        // Yields:
        //                               END N2 Narc.                          100.02             100.02              100.02
        //                               END N2 O2 Narc.                       118.44             118.42              118.42
        //                               END N2 O2 He Narc.                     99.91              99.90               99.89

        mDefaultEndN2Narc = mMyCalcImperial.getEad(defaultBestMixN2, mDefaultMod, mDefaultSalinity);
        mDefaultEndN2O2Narc = Math.max(0,mMyCalcImperial.getEnd(100.0 - (defaultBestMixO2 + defaultBestMixN2),mDefaultMod,mDefaultSalinity));
        mDefaultEndN2O2HeNarc = mDefaultEndN2O2Narc + mMyCalcImperial.getEndN2O2HeNarc(defaultBestMixHe, mDefaultAta, mDefaultSalinity);

        mCalculateDaltonLaw.setDefaultEndN2Narc(MyFunctions.roundUp(mDefaultEndN2Narc,2));
        mCalculateDaltonLaw.setDefaultEndN2O2Narc(MyFunctions.roundUp(mDefaultEndN2O2Narc,2));
        mCalculateDaltonLaw.setDefaultEndN2O2HeNarc(MyFunctions.roundUp(mDefaultEndN2O2HeNarc,2));
    }

    private void calculateImperialPartialPressure() {
        // Get all entered values first
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;
        mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe() / 100.0;
        mDefaultBestMixN2 = 1.0 - mDefaultBestMixO2 - mDefaultBestMixHe;
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

        // Convert feet to ata
        mDefaultAta = mMyCalcImperial.getAta(mDefaultMod,mDefaultSalinity);

        // Calculate the Partial Pressure
        mDefaultPartialPressure = mMyCalcImperial.getPartialPressure(mDefaultBestMixO2,mDefaultAta);

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultPartialPressure(MyFunctions.roundUp(mDefaultPartialPressure,2));
        mDefaultBestMixN2 = mDefaultBestMixN2 * 100.0;
        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2,2));
    }

    private void calculateMetricBestMix() {
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();

        // Convert meter to bar
        mDefaultAta = mMyCalcMetric.getBar(mDefaultMod,mDefaultSalinity);

        // Calculate the Best Mix
        mDefaultBestMixO2 = mMyCalcMetric.getBestMix(mDefaultPartialPressure,mDefaultAta) * 100.0;
        mDefaultBestMixN2 = 100.0 - mDefaultBestMixO2;

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultBestMixO2(MyFunctions.roundDown(mDefaultBestMixO2,2));
        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2,2));
    }

    private void calculateMetricBestMixTrimix() {
        // Get all entered values first
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

        // Convert meter to bar
        mDefaultAta = mMyCalcMetric.getBar(mDefaultMod,mDefaultSalinity);
        Double barEnd = mMyCalcMetric.getBar(mDefaultEnd,mDefaultSalinity);

        boolean whileCondition;
        double defaultEndN2O2HeNarc;
        double defaultEndN2O2Narc;

        switch(mCalculateDaltonLaw.getDefaultOptimizeEndType()) {
            case MyConstants.ENDN2: // END N2 Narcotic
                // Calculate the FO2
                mDefaultBestMixO2 = mMyCalcMetric.getFO2(mDefaultPartialPressure, mDefaultAta) * 100.0;

                // Calculate the FN2
                mDefaultBestMixN2 = mMyCalcMetric.getFN2(barEnd, mDefaultAta) * 100.0;
                mDefaultBestMixN2 = (100.0 - mDefaultBestMixO2 - mDefaultBestMixN2 < 0.0) ? 100.0 - mDefaultBestMixO2 : mDefaultBestMixN2;

                // Calculate the He
                mDefaultBestMixHe = 100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2);
                break;

            case MyConstants.ENDN2O2:  // END N2 O2 Narcotic
                // Calculate the FO2
                mDefaultBestMixO2 = mMyCalcMetric.getFO2(mDefaultPartialPressure, mDefaultAta) * 100.0;

                // Calculate the FN2
                mDefaultBestMixN2 = mMyCalcMetric.getFN2(barEnd, mDefaultAta) * 100.0;
                mDefaultBestMixN2 = (100.0 - mDefaultBestMixO2 - mDefaultBestMixN2 < 0.0) ? 100.0 - mDefaultBestMixO2 : mDefaultBestMixN2;

                // Calculate the He
                mDefaultBestMixHe = 100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2);

                whileCondition = true;

                do {
                    defaultEndN2O2Narc = Math.max(0,mMyCalcMetric.getEnd(100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2),mDefaultMod,mDefaultSalinity));
                    if (defaultEndN2O2Narc <= mDefaultEnd) {
                        whileCondition = false;
                    } else {
                        mDefaultBestMixN2 = mDefaultBestMixN2 - 0.1;
                        mDefaultBestMixHe = mDefaultBestMixHe + 0.1;
                    }
                } while (whileCondition);


                break;

            case MyConstants.ENDN2O2HE: // END N2 O2 He Narcotic
                // Calculate the FO2
                mDefaultBestMixO2 = mMyCalcMetric.getFO2(mDefaultPartialPressure, mDefaultAta) * 100.0;

                // Calculate the FN2
                mDefaultBestMixN2 = mMyCalcMetric.getFN2(barEnd, mDefaultAta) * 100.0;
                mDefaultBestMixN2 = (100.0 - mDefaultBestMixO2 - mDefaultBestMixN2 < 0.0) ? 100.0 - mDefaultBestMixO2 : mDefaultBestMixN2;

                // Calculate the He
                mDefaultBestMixHe = 100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2);

                whileCondition = true;

                do {
                    defaultEndN2O2Narc = Math.max(0,mMyCalcMetric.getEnd(100.0 - (mDefaultBestMixO2 + mDefaultBestMixN2),mDefaultMod,mDefaultSalinity));
                    defaultEndN2O2HeNarc = defaultEndN2O2Narc + mMyCalcMetric.getEndN2O2HeNarc(mDefaultBestMixHe, mDefaultAta, mDefaultSalinity);

                    if (defaultEndN2O2HeNarc <= mDefaultEnd) {
                        whileCondition = false;
                    } else {
                        mDefaultBestMixN2 = mDefaultBestMixN2 - 0.1;
                        mDefaultBestMixHe = mDefaultBestMixHe + 0.1;
                    }
                } while (whileCondition);
                break;
        }

        mDefaultBestMixO2 = MyFunctions.round(mDefaultBestMixO2,2);
        mDefaultBestMixHe = MyFunctions.round(mDefaultBestMixHe,2);
        mDefaultBestMixN2 = MyFunctions.round(100.0 - mDefaultBestMixO2 - mDefaultBestMixHe,2);

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultBestMixO2(mDefaultBestMixO2);
        mCalculateDaltonLaw.setDefaultBestMixHe(mDefaultBestMixHe);
        mCalculateDaltonLaw.setDefaultBestMixN2(mDefaultBestMixN2);
    }

    private void calculateMetricEabd(Double defaultBestMixO2, Double defaultBestMixHe) {
        // Calculate the Equivalent Air Breathing Depth

        mDefaultEabd = mMyCalcMetric.getEabd(defaultBestMixO2/100, defaultBestMixHe/100, mDefaultAta, mDefaultSalinity);

        mCalculateDaltonLaw.setDefaultEabd(MyFunctions.roundUp(mDefaultEabd,2));
    }

    private void calculateMetricMod() {
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;

        // Calculate the MOD
        mDefaultAta = mMyCalcMetric.getMod(mDefaultPartialPressure,mDefaultBestMixO2);

        // Convert ATA to meter
        mDefaultMod = Math.max(0,mMyCalcMetric.convertBarToDepth(mDefaultAta,mDefaultSalinity));
        mDefaultBestMixN2 = (1.0 - mDefaultBestMixO2) * 100.0;

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultMod(MyFunctions.roundDown(mDefaultMod,2));
        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2,2));
    }

    private void calculateMetricModTrimix() {
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;
        mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe() / 100.0;
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

        // Calculate the FN2
        mDefaultBestMixN2 = 1.0 - mDefaultBestMixO2 - mDefaultBestMixHe;

        // Calculate the MOD
        mDefaultAta = mMyCalcMetric.getMod(mDefaultPartialPressure,mDefaultBestMixO2);

        // Convert ATA to meter
        mDefaultMod = Math.max(0,mMyCalcMetric.convertBarToDepth(mDefaultAta,mDefaultSalinity));

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2 * 100.0,2));
        mCalculateDaltonLaw.setDefaultMod(MyFunctions.roundDown(mDefaultMod,2));
    }

    private void calculateMetricNarc(Double defaultBestMixO2, Double defaultBestMixHe, Double defaultBestMixN2) {
        // Calculate different ENDs narcotic
        // Optimize To is used to calculate Best Mix, not the END
        // Not to calculate END narcotic

        mDefaultEndN2Narc = mMyCalcMetric.getEad(defaultBestMixN2, mDefaultMod, mDefaultSalinity);
        mDefaultEndN2O2Narc = Math.max(0,mMyCalcMetric.getEnd(100.0 - (defaultBestMixO2 + defaultBestMixN2),mDefaultMod,mDefaultSalinity));
        mDefaultEndN2O2HeNarc = mDefaultEndN2O2Narc + mMyCalcMetric.getEndN2O2HeNarc(defaultBestMixHe, mDefaultAta, mDefaultSalinity);

        mCalculateDaltonLaw.setDefaultEndN2Narc(MyFunctions.roundUp(mDefaultEndN2Narc,2));
        mCalculateDaltonLaw.setDefaultEndN2O2Narc(MyFunctions.roundUp(mDefaultEndN2O2Narc,2));
        mCalculateDaltonLaw.setDefaultEndN2O2HeNarc(MyFunctions.roundUp(mDefaultEndN2O2HeNarc,2));
    }

    private void calculateMetricPartialPressure() {
        // Get all entered values first
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;
        mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe() / 100.0;
        mDefaultBestMixN2 = 1.0 - mDefaultBestMixO2 - mDefaultBestMixHe;
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

        // Convert meter to bar
        mDefaultAta = mMyCalcMetric.getBar(mDefaultMod,mDefaultSalinity);

        // Calculate the Partial Pressure
        mDefaultPartialPressure = mMyCalcMetric.getPartialPressure(mDefaultBestMixO2,mDefaultAta);

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setDefaultPartialPressure(MyFunctions.roundUp(mDefaultPartialPressure,2));
        mDefaultBestMixN2 = mDefaultBestMixN2 * 100.0;
        mCalculateDaltonLaw.setDefaultBestMixN2(MyFunctions.roundUp(mDefaultBestMixN2,2));
    }

    private void calculateToImperialBestMixN2() {
        // The Best Mix N2 is 100.0 - the Best Mix O2
        mOtherBestMixN2 = 100.0 - mOtherBestMixO2;

        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));
    }

    private void calculateToImperialBestMixO2() {
        // The Metric and Imperial values are the same e.g. 21%
        // But it needs to be calculated
        mOtherBestMixO2 = mMyCalcImperial.getBestMix(mDefaultPartialPressure,mDefaultAta) * 100.0;

        mCalculateDaltonLaw.setOtherBestMixO2(MyFunctions.roundDown(mOtherBestMixO2,2));
    }

    private void calculateToImperialBestMixTrimix() {
        // The Metric and Imperial values are almost the same e.g. 21%, 18% and 61%
        // But it needs to be calculated because of the slight difference between Imperial and Metric
        // Get all entered values first
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

//        // Convert meter to feet
//        Double mOtherMod = mMyCalcMetric.convertMeterToFeet(mDefaultMod);
//        Double mOtherEnd = mMyCalcMetric.convertMeterToFeet(mDefaultEnd);

        // Convert feet to ata
        mOtherAta = mMyCalcImperial.getAta(mOtherMod,mDefaultSalinity);
        Double ataEnd = mMyCalcImperial.getAta(mOtherEnd,mDefaultSalinity);

        boolean whileCondition;
        double otherEndN2O2Narc;
        double otherEndN2O2HeNarc;

        switch(mCalculateDaltonLaw.getDefaultOptimizeEndType()) {
            case MyConstants.ENDN2: // END N2 Narcotic
                // Calculate the FO2
                mOtherBestMixO2 = mMyCalcImperial.getFO2(mDefaultPartialPressure, mOtherAta) * 100.0;

                // Calculate the FN2
                mOtherBestMixN2 = mMyCalcImperial.getFN2(ataEnd, mOtherAta) * 100.0;
                mOtherBestMixN2 = (100.0 - mOtherBestMixO2 - mOtherBestMixN2 < 0.0) ? 100.0 - mOtherBestMixO2 : mOtherBestMixN2;

                // Calculate the He
                mOtherBestMixHe = 100.0 - (mOtherBestMixO2 + mOtherBestMixN2);

                break;

            case MyConstants.ENDN2O2:  // END N2 O2 Narcotic
                // Calculate the FO2
                mOtherBestMixO2 = mMyCalcImperial.getFO2(mDefaultPartialPressure, mOtherAta) * 100.0;

                // Calculate the FN2
                mOtherBestMixN2 = mMyCalcImperial.getFN2(ataEnd, mOtherAta) * 100.0;
                mOtherBestMixN2 = (100.0 - mOtherBestMixO2 - mOtherBestMixN2 < 0.0) ? 100.0 - mOtherBestMixO2 : mOtherBestMixN2;

                // Calculate the He
                mOtherBestMixHe = 100.0 - (mOtherBestMixO2 + mOtherBestMixN2);

                whileCondition = true;

                do {
                    otherEndN2O2Narc = Math.max(0,mMyCalcImperial.getEnd(100.0 - (mOtherBestMixO2 + mOtherBestMixN2),mOtherMod,mDefaultSalinity));
                    if (otherEndN2O2Narc <= mOtherEnd) {
                        whileCondition = false;
                    } else {
                        mOtherBestMixN2 = mOtherBestMixN2 - 0.1;
                        mOtherBestMixHe = mOtherBestMixHe + 0.1;
                    }
                } while (whileCondition);


                break;

            case MyConstants.ENDN2O2HE: // END N2 O2 He Narcotic
                // Calculate the FO2
                mOtherBestMixO2 = mMyCalcImperial.getFO2(mDefaultPartialPressure, mOtherAta) * 100.0;

                // Calculate the FN2
                mOtherBestMixN2 = mMyCalcImperial.getFN2(ataEnd, mOtherAta) * 100.0;
                mOtherBestMixN2 = (100.0 - mOtherBestMixO2 - mOtherBestMixN2 < 0.0) ? 100.0 - mOtherBestMixO2 : mOtherBestMixN2;

                // Calculate the He
                mOtherBestMixHe = 100.0 - (mOtherBestMixO2 + mOtherBestMixN2);

                whileCondition = true;

                do {
                    otherEndN2O2Narc = Math.max(0,mMyCalcImperial.getEnd(100.0 - (mOtherBestMixO2 + mOtherBestMixN2),mOtherMod,mDefaultSalinity));
                    otherEndN2O2HeNarc = otherEndN2O2Narc + mMyCalcImperial.getEndN2O2HeNarc(mOtherBestMixHe, mOtherAta, mDefaultSalinity);

                    if (otherEndN2O2HeNarc <= mOtherEnd) {
                        whileCondition = false;
                    } else {
                        mOtherBestMixN2 = mOtherBestMixN2 - 0.1;
                        mOtherBestMixHe = mOtherBestMixHe + 0.1;
                    }
                } while (whileCondition);
                break;
        }

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setOtherBestMixO2(MyFunctions.roundDown(mOtherBestMixO2,2));
        mCalculateDaltonLaw.setOtherBestMixHe(MyFunctions.roundUp(mOtherBestMixHe,2));
        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));
    }

    private void calculateToImperialEabd(Double otherBestMixO2, Double otherBestMixHe) {
        // Calculate the Equivalent Air Breathing Depth

        mOtherEabd = mMyCalcImperial.getEabd(otherBestMixO2 / 100, otherBestMixHe / 100, mOtherAta, mDefaultSalinity);

        mCalculateDaltonLaw.setOtherEabd(MyFunctions.roundUp(mOtherEabd,2));
    }

    private void calculateToImperialMod() {
        // The Metric and Imperial values are almost the same e.g. 100 ft versus 30.5 meter
        // But it needs to be calculated because of the slight difference between Imperial and Metric
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mOtherPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mOtherBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;

        // Calculate the MOD
        mOtherAta = mMyCalcImperial.getMod(mOtherPartialPressure,mOtherBestMixO2);

        // Convert ATA to feet
        mOtherMod = Math.max(0,mMyCalcImperial.convertAtaToDepth(mOtherAta,mDefaultSalinity));
        mOtherBestMixN2 = (1.0 - mOtherBestMixO2) * 100.0;

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setOtherMod(MyFunctions.roundDown(mOtherMod,2));
        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));
    }

    private void calculateToImperialModTrimix() {
        // The Metric and Imperial values are almost the same e.g. 100 ft versus 30.5 meter
        // But it needs to be calculated because of the slight difference between Imperial and Metric
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;
        mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe() / 100.0;
        mDefaultBestMixN2 = mCalculateDaltonLaw.getDefaultBestMixN2() / 100.0;
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

        // Calculate the FN2
        mOtherBestMixN2 = 1.0 - mDefaultBestMixO2 - mDefaultBestMixHe;

        // Calculate the MOD
        mOtherAta = mMyCalcImperial.getMod(mDefaultPartialPressure,mDefaultBestMixO2);

        // Convert ATA to feet
        mOtherMod = Math.max(0,mMyCalcImperial.convertAtaToDepth(mOtherAta,mDefaultSalinity));

        // Set the calculated values in the POJO
        mOtherBestMixN2 = mOtherBestMixN2 * 100.0;
        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));
        mCalculateDaltonLaw.setOtherMod(MyFunctions.roundDown(mOtherMod,2));
    }

    private void calculateToImperialNarc(Double otherBestMixO2, Double otherBestMixHe, Double otherBestMixN2) {
        // Calculate different ENDs narcotic
        // Optimize To is used to calculate Best Mix, not the END
        // Not to calculate END narcotic

        mOtherEndN2Narc = mMyCalcImperial.getEad(otherBestMixN2, mOtherMod, mDefaultSalinity);
        mOtherEndN2O2Narc = Math.max(0,mMyCalcImperial.getEnd(100.0 - (otherBestMixO2 + otherBestMixN2),mOtherMod,mDefaultSalinity));
        mOtherEndN2O2HeNarc = mOtherEndN2O2Narc + mMyCalcImperial.getEndN2O2HeNarc(otherBestMixHe, mOtherAta, mDefaultSalinity);

        mCalculateDaltonLaw.setOtherEndN2Narc(MyFunctions.roundUp(mOtherEndN2Narc,2));
        mCalculateDaltonLaw.setOtherEndN2O2Narc(MyFunctions.roundUp(mOtherEndN2O2Narc,2));
        mCalculateDaltonLaw.setOtherEndN2O2HeNarc(MyFunctions.roundUp(mOtherEndN2O2HeNarc,2));
    }

    private void calculateToMetricBestMixN2() {
        // The Best Mix N2 is 100.0 - the Best Mix O2
        mOtherBestMixN2 = (100.0 - mOtherBestMixO2);

        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));
    }

    private void calculateToMetricBestMixO2() {
        // The Metric and Imperial values are almost the same e.g. 21%
        // But it needs to be calculated because of the slight difference between Imperial and Metric
        mOtherBestMixO2 = mMyCalcMetric.getBestMix(mDefaultPartialPressure,mDefaultAta) * 100.0;

        mCalculateDaltonLaw.setOtherBestMixO2(MyFunctions.roundDown(mOtherBestMixO2,2));
    }

    private void calculateToMetricBestMixTrimix() {
        // The Metric and Imperial values are almost the same e.g. 21%, 18% and 61%
        // But it needs to be calculated because of the slight difference between Imperial and Metric
        // Get all entered values first
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultMod = mCalculateDaltonLaw.getDefaultMod();
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

//        // Convert feet to meter
//        Double mOtherMod = mMyCalcImperial.convertFeetToMeter(mDefaultMod);
//        Double mOtherEnd = mMyCalcImperial.convertFeetToMeter(mDefaultEnd);

        // Convert meter to bar
        mOtherAta = mMyCalcMetric.getBar(mOtherMod,mDefaultSalinity);
        Double barEnd = mMyCalcMetric.getBar(mOtherEnd,mDefaultSalinity);

        boolean whileCondition;
        double otherEndN2O2Narc;
        double otherEndN2O2HeNarc;

        switch(mCalculateDaltonLaw.getDefaultOptimizeEndType()) {
            case MyConstants.ENDN2: // END N2 Narcotic
                // Calculate the FO2
                mOtherBestMixO2 = mMyCalcMetric.getFO2(mDefaultPartialPressure, mOtherAta) * 100.0;

                // Calculate the FN2
                mOtherBestMixN2 = mMyCalcMetric.getFN2(barEnd, mOtherAta) * 100.0;
                mOtherBestMixN2 = (100.0 - mOtherBestMixO2 - mOtherBestMixN2 < 0.0) ? 100.0 - mOtherBestMixO2 : mOtherBestMixN2;

                // Calculate the He
                mOtherBestMixHe = 100.0 - (mOtherBestMixO2 + mOtherBestMixN2);

                break;

            case MyConstants.ENDN2O2:  // END N2 O2 Narcotic
                // Calculate the FO2
                mOtherBestMixO2 = mMyCalcMetric.getFO2(mDefaultPartialPressure, mOtherAta) * 100.0;

                // Calculate the FN2
                mOtherBestMixN2 = mMyCalcMetric.getFN2(barEnd, mOtherAta) * 100.0;
                mOtherBestMixN2 = (100.0 - mOtherBestMixO2 - mOtherBestMixN2 < 0.0) ? 100.0 - mOtherBestMixO2 : mOtherBestMixN2;

                // Calculate the He
                mOtherBestMixHe = 100.0 - (mOtherBestMixO2 + mOtherBestMixN2);

                whileCondition = true;

                do {
                    otherEndN2O2Narc = Math.max(0,mMyCalcMetric.getEnd(100.0 - (mOtherBestMixO2 + mOtherBestMixN2),mOtherMod,mDefaultSalinity));
                    if (otherEndN2O2Narc <= mOtherEnd) {
                        whileCondition = false;
                    } else {
                        mOtherBestMixN2 = mOtherBestMixN2 - 0.1;
                        mOtherBestMixHe = mOtherBestMixHe + 0.1;
                    }
                } while (whileCondition);

                break;

            case MyConstants.ENDN2O2HE: // END N2 O2 He Narcotic
                // Calculate the FO2
                mOtherBestMixO2 = mMyCalcMetric.getFO2(mDefaultPartialPressure, mOtherAta) * 100.0;

                // Calculate the FN2
                mOtherBestMixN2 = mMyCalcMetric.getFN2(barEnd, mOtherAta) * 100.0;
                mOtherBestMixN2 = (100.0 - mOtherBestMixO2 - mOtherBestMixN2 < 0.0) ? 100.0 - mOtherBestMixO2 : mOtherBestMixN2;

                // Calculate the He
                mOtherBestMixHe = 100.0 - (mOtherBestMixO2 + mOtherBestMixN2);

                whileCondition = true;

                do {
                    otherEndN2O2Narc = Math.max(0,mMyCalcMetric.getEnd(100.0 - (mOtherBestMixO2 + mOtherBestMixN2),mOtherMod,mDefaultSalinity));
                    otherEndN2O2HeNarc = otherEndN2O2Narc + mMyCalcMetric.getEndN2O2HeNarc(mOtherBestMixHe, mOtherAta, mDefaultSalinity);

                    if (otherEndN2O2HeNarc <= mOtherEnd) {
                        whileCondition = false;
                    } else {
                        mOtherBestMixN2 = mOtherBestMixN2 - 0.1;
                        mOtherBestMixHe = mOtherBestMixHe + 0.1;
                    }
                } while (whileCondition);
                break;
        }

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setOtherBestMixO2(MyFunctions.roundDown(mOtherBestMixO2,2));
        mCalculateDaltonLaw.setOtherBestMixHe(MyFunctions.roundUp(mOtherBestMixHe,2));
        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));
    }

    private void calculateToMetricEabd(Double otherBestMixO2, Double otherBestMixHe) {
        // Calculate the Equivalent Air Breathing Depth

        mOtherEabd = mMyCalcMetric.getEabd(otherBestMixO2 / 100, otherBestMixHe / 100, mOtherAta, mDefaultSalinity);

        mCalculateDaltonLaw.setOtherEabd(MyFunctions.roundUp(mOtherEabd,2));
    }

    private void calculateToMetricMod() {
        // The Metric and Imperial values are almost the same e.g. 100 ft versus 30.5 meter
        // But it needs to be calculated because of the slight difference between Imperial and Metric
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mOtherPartialPressure = mCalculateDaltonLaw.getOtherPartialPressure();
        mOtherBestMixO2 = mCalculateDaltonLaw.getOtherBestMixO2() / 100.0;

        // Calculate the MOD
        mOtherAta= mMyCalcMetric.getMod(mOtherPartialPressure,mOtherBestMixO2);

        // Convert ATA to feet
        mOtherMod = Math.max(0,mMyCalcMetric.convertBarToDepth(mOtherAta,mDefaultSalinity));
        mOtherBestMixN2 = (1.0 - mOtherBestMixO2) * 100.0;

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setOtherMod(MyFunctions.roundDown(mOtherMod,2));
        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));
    }

    private void calculateToMetricModTrimix() {
        // The Metric and Imperial values are almost the same e.g. 100 ft versus 30.5 meter
        // But it needs to be calculated because of the slight difference between Imperial and Metric
        // Get all entered values first
        mDefaultSalinity = mCalculateDaltonLaw.getDefaultSalinity();
        mDefaultPartialPressure = mCalculateDaltonLaw.getDefaultPartialPressure();
        mDefaultBestMixO2 = mCalculateDaltonLaw.getDefaultBestMixO2() / 100.0;
        mDefaultBestMixHe = mCalculateDaltonLaw.getDefaultBestMixHe() / 100.0;
        mDefaultBestMixN2 = mCalculateDaltonLaw.getDefaultBestMixN2() / 100.0;
        mDefaultEnd = mCalculateDaltonLaw.getDefaultEnd();

        // Calculate the FN2
        mOtherBestMixN2 = 1.0 - mDefaultBestMixO2 - mDefaultBestMixHe;

        // Calculate the MOD
        mOtherAta = mMyCalcMetric.getMod(mDefaultPartialPressure,mDefaultBestMixO2);

        // Convert ATA to meter
        mOtherMod = Math.max(0,mMyCalcMetric.convertBarToDepth(mOtherAta,mDefaultSalinity));

        // Set the calculated values in the POJO
        mOtherBestMixN2 = mOtherBestMixN2 * 100.0;
        mCalculateDaltonLaw.setOtherBestMixN2(MyFunctions.roundUp(mOtherBestMixN2,2));
        mCalculateDaltonLaw.setOtherMod(MyFunctions.roundDown(mOtherMod,2));
    }

    private void calculateToMetricNarc(Double otherBestMixO2, Double otherBestMixHe, Double otherBestMixN2) {
        // Calculate different ENDs narcotic
        // Optimize To is used to calculate Best Mix, not the END
        // Not to calculate END narcotic

        mOtherEndN2Narc = mMyCalcMetric.getEad(otherBestMixN2, mOtherMod, mDefaultSalinity);
        mOtherEndN2O2Narc = Math.max(0,mMyCalcMetric.getEnd(100.0 - (otherBestMixO2 + otherBestMixN2),mOtherMod,mDefaultSalinity));
        mOtherEndN2O2HeNarc = mOtherEndN2O2Narc + mMyCalcMetric.getEndN2O2HeNarc(otherBestMixHe, mOtherAta, mDefaultSalinity);

        mCalculateDaltonLaw.setOtherEndN2Narc(MyFunctions.roundUp(mOtherEndN2Narc,2));
        mCalculateDaltonLaw.setOtherEndN2O2Narc(MyFunctions.roundUp(mOtherEndN2O2Narc,2));
        mCalculateDaltonLaw.setOtherEndN2O2HeNarc(MyFunctions.roundUp(mOtherEndN2O2HeNarc,2));
    }

    private void convertToImperialEnd() {
        // The value has been entered by the user, just convert
        // Convert Meter to Feet
        mOtherEnd = mMyCalcImperial.convertMeterToFeet(mDefaultEnd);

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setOtherEnd(MyFunctions.roundDown(mOtherEnd,2));
    }

    private void convertToImperialMod() {
        // The value has been entered by the user, just convert
        // Convert Meter to Feet
        mOtherMod = mMyCalcImperial.convertMeterToFeet(mDefaultMod);

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setOtherMod(MyFunctions.roundDown(mOtherMod,2));
    }

    private void convertToMetricEnd() {
        // The value has been entered by the user, just convert
        // Convert Feet to Meter
        mOtherEnd = mMyCalcMetric.convertFeetToMeter(mDefaultEnd);

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setOtherEnd(MyFunctions.roundDown(mOtherEnd,2));
    }

    private void convertToMetricMod() {
        // The value has been entered by the user, just convert
        // Convert Feet to Meter
        mOtherMod = mMyCalcMetric.convertFeetToMeter(mDefaultMod);

        // Set the calculated values in the POJO
        mCalculateDaltonLaw.setOtherMod(MyFunctions.roundDown(mOtherMod,2));
    }

    private void copyToOtherBestMixHe() {
        // The Metric and Imperial values are the same e.g. 18%
        // The value has been entered by the user, just copy
        // Use for the calculation
        mOtherBestMixHe = mDefaultBestMixHe;
        // Use to show
        mCalculateDaltonLaw.setOtherBestMixHe(mCalculateDaltonLaw.getDefaultBestMixHe());
    }

    private void copyToOtherBestMixN2() {
        // The Metric and Imperial values are the same e.g. 61%
        // The value has been deducted from other entered values, just copy
        // Use for the calculation
        mOtherBestMixN2 = mDefaultBestMixN2;
        // Use to show
        mCalculateDaltonLaw.setOtherBestMixN2(mCalculateDaltonLaw.getDefaultBestMixN2());
    }

    private void copyToOtherBestMixO2() {
        // The Metric and Imperial values are the same e.g. 21%
        // The value has been entered by the user, just copy
        // Use for the calculation
        mOtherBestMixO2 = mDefaultBestMixO2;
        // Use to show
        mCalculateDaltonLaw.setOtherBestMixO2(mCalculateDaltonLaw.getDefaultBestMixO2());
    }

    private void copyToOtherPartialPressure() {
        // The Metric and Imperial values are the same e.g. 1.4 or 1.6
        // The value has been entered by the user, just copy
        mCalculateDaltonLaw.setOtherPartialPressure(MyFunctions.roundUp(mDefaultPartialPressure,2));
    }

    // End of calculate, convert and copy functions

    private void clear() {
        // Reset the Default
        mDefaultPartialPressure = MyConstants.DEFAULT_PARTIAL_PRESSURE;
        if (mCalculateDaltonLaw.getDefaultTrimix()) {
            // Calculating Nitrox
            mDefaultBestMixO2 = MyConstants.DEFAULT_BEST_MIX_O2;
            mDefaultBestMixHe = MyConstants.ZERO_D;
            mDefaultBestMixN2 = MyConstants.DEFAULT_BEST_MIX_N2;
        } else {
            // Calculating Trimix
            mDefaultBestMixO2 = MyConstants.ZERO_D;
            mDefaultBestMixHe = MyConstants.ZERO_D;
            mDefaultBestMixN2 = MyConstants.ZERO_D;
        }

        mDefaultMod = MyConstants.ZERO_D;

        if (mUnit.equals(MyConstants.IMPERIAL)) {
            // The phone is in IMPERIAL
            if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
                // The phone Has been switched to IMPERIAL
                mDefaultEnd = mPreferenceEnd;
            } else {
                // The phone has been switched to METRIC
                mDefaultEnd = MyFunctions.roundUp(mMyCalcMetric.convertFeetToMeter(mPreferenceEnd),2);
            }
        } else {
            // The phone is in METRIC
            if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
                // The phone Has been switched to IMPERIAL
                mDefaultEnd = mPreferenceEnd;
            } else {
                // The phone has been switched to METRIC
                mDefaultEnd = MyFunctions.roundUp(mMyCalcImperial.convertMeterToFeet(mPreferenceEnd),2);
            }
        }

        mDefaultEndN2Narc = MyConstants.ZERO_D;
        mDefaultEndN2O2Narc = MyConstants.ZERO_D;
        mDefaultEndN2O2HeNarc = MyConstants.ZERO_D;
        mDefaultEabd = MyConstants.ZERO_D;

        mCalculateDaltonLaw.setDefaultPartialPressure(mDefaultPartialPressure);
        mCalculateDaltonLaw.setDefaultBestMixO2(mDefaultBestMixO2);
        mCalculateDaltonLaw.setDefaultBestMixHe(mDefaultBestMixHe);
        mCalculateDaltonLaw.setDefaultBestMixN2(mDefaultBestMixN2);
        mCalculateDaltonLaw.setDefaultMod(mDefaultMod);
        mCalculateDaltonLaw.setDefaultEnd(mDefaultEnd);
        mCalculateDaltonLaw.setDefaultEndN2Narc(mDefaultEndN2Narc);
        mCalculateDaltonLaw.setDefaultEndN2O2Narc(mDefaultEndN2O2Narc);
        mCalculateDaltonLaw.setDefaultEndN2O2HeNarc(mDefaultEndN2O2HeNarc);
        mCalculateDaltonLaw.setDefaultEabd(mDefaultEabd);

        mBinding.defaultPartialPressure.setText(String.valueOf(mDefaultPartialPressure));
        mBinding.defaultBestMixO2.setText(String.valueOf(mDefaultBestMixO2));
        mBinding.defaultBestMixHe.setText(String.valueOf(mDefaultBestMixHe));
        mBinding.defaultBestMixN2.setText(String.valueOf(mDefaultBestMixN2));
        mBinding.defaultMod.setText(String.valueOf(mDefaultMod));
        mBinding.defaultEnd.setText(String.valueOf(mDefaultEnd));
        mBinding.defaultEndN2Narc.setText(String.valueOf(mDefaultEndN2Narc));
        mBinding.defaultEndN2O2Narc.setText(String.valueOf(mDefaultEndN2O2Narc));
        mBinding.defaultEndN2O2HeNarc.setText(String.valueOf(mDefaultEndN2O2HeNarc));
        mBinding.defaultEabd.setText(String.valueOf(mDefaultEabd));

        mBinding.hdrBestMixHeLbl.setText("");
        mBinding.hdrBestMixN2Lbl.setText("");

        // Reset the Other
        Double mOtherPartialPressure = MyConstants.DEFAULT_PARTIAL_PRESSURE;
        Double mOtherBestMixO2 = mDefaultBestMixO2;
        Double mOtherBestMixHe = mDefaultBestMixHe;
        Double mOtherBestMixN2 = mDefaultBestMixN2;
        mOtherMod = MyConstants.ZERO_D;
        mOtherEnd = MyConstants.ZERO_D;
        mOtherEndN2Narc = MyConstants.ZERO_D;
        mOtherEndN2O2Narc = MyConstants.ZERO_D;
        mOtherEndN2O2HeNarc = MyConstants.ZERO_D;
        mOtherEabd = MyConstants.ZERO_D;

        mCalculateDaltonLaw.setOtherPartialPressure(mOtherPartialPressure);
        mCalculateDaltonLaw.setOtherBestMixO2(mOtherBestMixO2);
        mCalculateDaltonLaw.setOtherBestMixHe(mOtherBestMixHe);
        mCalculateDaltonLaw.setOtherBestMixN2(mOtherBestMixN2);
        mCalculateDaltonLaw.setOtherMod(mOtherMod);
        mCalculateDaltonLaw.setOtherEnd(mOtherEnd);
        mCalculateDaltonLaw.setOtherEndN2Narc(mOtherEndN2Narc);
        mCalculateDaltonLaw.setOtherEndN2O2Narc(mOtherEndN2O2Narc);
        mCalculateDaltonLaw.setOtherEndN2O2HeNarc(mOtherEndN2O2HeNarc);
        mCalculateDaltonLaw.setOtherEabd(mOtherEabd);

        requestFocus(mBinding.defaultPartialPressure, false);
    }

    private ArrayList<EndType> getAllEnd() {
        ArrayList<EndType> endTypeList = new ArrayList<>();
        String[] endTypes;
        String[] endTypesDescription;

        endTypes = getResources().getStringArray(R.array.optimize_end_type_arrays);
        endTypesDescription = getResources().getStringArray(R.array.optimize_end_type_description_arrays);
        for (int i = 0; i < endTypes.length; i++) {
            EndType endType = new EndType();
            endType.setEndType(endTypes[i]);
            endType.setEndTypeDescription(endTypesDescription[i]);
            endTypeList.add(endType);
        }

        return endTypeList;
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

    @SuppressLint("StringFormatMatches")
    public void switchSide() {
        // Save the Other side
        String otherUnit = mBinding.otherUnit.getText().toString();
        String otherPartialPressure = mBinding.otherPartialPressure.getText().toString();
        String otherPartialPressureLbl = mBinding.otherPartialPressureLbl.getText().toString();
        String otherBestMixO2 = mBinding.otherBestMixO2.getText().toString();
        String otherBestMixO2Lbl = mBinding.otherBestMixO2Lbl.getText().toString();
        String otherBestMixN2 = mBinding.otherBestMixN2.getText().toString();
        String otherBestMixN2Lbl = mBinding.otherBestMixN2Lbl.getText().toString();
        String otherBestMixHe = mBinding.otherBestMixHe.getText().toString();
        String otherBestMixHeLbl = mBinding.otherBestMixHeLbl.getText().toString();
        String otherMod = mBinding.otherMod.getText().toString();
        String otherModLbl = mBinding.otherModLbl.getText().toString();
        String otherEnd = mBinding.otherEnd.getText().toString();
        String otherEndLbl = mBinding.otherEndLbl.getText().toString();
        String otherEndN2Narc = mBinding.otherEndN2Narc.getText().toString();
        String otherEndN2NarcLbl = mBinding.otherEndN2NarcLbl.getText().toString();
        String otherEndN2O2Narc = mBinding.otherEndN2O2Narc.getText().toString();
        String otherEndN2O2NarcLbl = mBinding.otherEndN2O2NarcLbl.getText().toString();
        String otherEndN2O2HeNarc = mBinding.otherEndN2O2HeNarc.getText().toString();
        String otherEndN2O2HeNarcLbl = mBinding.otherEndN2O2HeNarcLbl.getText().toString();
        String otherEabd = mBinding.otherEabd.getText().toString();
        String otherEabdLbl = mBinding.otherEabdLbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());
        mBinding.otherPartialPressure.setText(mBinding.defaultPartialPressure.getText().toString());
        mBinding.otherPartialPressureLbl.setText(mBinding.defaultPartialPressureLbl.getText().toString());
        mBinding.otherBestMixO2.setText(mBinding.defaultBestMixO2.getText().toString());
        mBinding.otherBestMixO2Lbl.setText(mBinding.defaultBestMixO2Lbl.getText().toString());
        mBinding.otherBestMixN2.setText(mBinding.defaultBestMixN2.getText().toString());
        mBinding.otherBestMixN2Lbl.setText(mBinding.defaultBestMixN2Lbl.getText().toString());
        mBinding.otherBestMixHe.setText(mBinding.defaultBestMixHe.getText().toString());
        mBinding.otherBestMixHeLbl.setText(mBinding.defaultBestMixHeLbl.getText().toString());
        mBinding.otherMod.setText(mBinding.defaultMod.getText().toString());
        mBinding.otherModLbl.setText(mBinding.defaultModLbl.getText().toString());
        mBinding.otherEnd.setText(mBinding.defaultEnd.getText().toString());
        mBinding.otherEndLbl.setText(mBinding.defaultEndLbl.getText().toString());
        mBinding.otherEndN2Narc.setText(mBinding.defaultEndN2Narc.getText().toString());
        mBinding.otherEndN2NarcLbl.setText(mBinding.defaultEndN2NarcLbl.getText().toString());
        mBinding.otherEndN2O2Narc.setText(mBinding.defaultEndN2O2Narc.getText().toString());
        mBinding.otherEndN2O2NarcLbl.setText(mBinding.defaultEndN2O2NarcLbl.getText().toString());
        mBinding.otherEndN2O2HeNarc.setText(mBinding.defaultEndN2O2HeNarc.getText().toString());
        mBinding.otherEndN2O2HeNarcLbl.setText(mBinding.defaultEndN2O2HeNarcLbl.getText().toString());
        mBinding.otherEabd.setText(mBinding.defaultEabd.getText().toString());
        mBinding.otherEabdLbl.setText(mBinding.defaultEabdLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);
        mBinding.defaultPartialPressure.setText(otherPartialPressure);
        mBinding.defaultPartialPressureLbl.setText(otherPartialPressureLbl);
        mBinding.defaultBestMixO2.setText(otherBestMixO2);
        mBinding.defaultBestMixO2Lbl.setText(otherBestMixO2Lbl);
        mBinding.defaultBestMixN2.setText(otherBestMixN2);
        mBinding.defaultBestMixN2Lbl.setText(otherBestMixN2Lbl);
        mBinding.defaultBestMixHe.setText(otherBestMixHe);
        mBinding.defaultBestMixHeLbl.setText(otherBestMixHeLbl);
        mBinding.defaultMod.setText(otherMod);
        mBinding.defaultModLbl.setText(otherModLbl);
        mBinding.defaultEnd.setText(otherEnd);
        mBinding.defaultEndLbl.setText(otherEndLbl);
        mBinding.defaultEndN2Narc.setText(otherEndN2Narc);
        mBinding.defaultEndN2NarcLbl.setText(otherEndN2NarcLbl);
        mBinding.defaultEndN2O2Narc.setText(otherEndN2O2Narc);
        mBinding.defaultEndN2O2NarcLbl.setText(otherEndN2O2NarcLbl);
        mBinding.defaultEndN2O2HeNarc.setText(otherEndN2O2HeNarc);
        mBinding.defaultEndN2O2HeNarcLbl.setText(otherEndN2O2HeNarcLbl);
        mBinding.defaultEabd.setText(otherEabd);
        mBinding.defaultEabdLbl.setText(otherEabdLbl);

        mBinding.hdrBestMixHeLbl.setText("");
        mBinding.hdrBestMixN2Lbl.setText("");

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateDaltonLawActivity) {
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

        // The END preference will be stored in the native phone system unit of measure
        // Most likely 100 feet for Imperial
        // Most likely 30 meters for metric
        mPreferenceEnd = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.END, getResources().getString(R.string.code_end_unit)))));

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

        if (mUnit.equals(MyConstants.IMPERIAL)) {
            // The phone is in IMPERIAL
            if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
                // The phone Has been switched to IMPERIAL
                if ( mCalculateDaltonLaw.getDefaultEnd().equals(MyConstants.ZERO_D)) {
                    mCalculateDaltonLaw.setDefaultEnd(mPreferenceEnd);
                    mCalculateDaltonLaw.setOtherEnd(MyFunctions.roundDown(mMyCalcImperial.convertFeetToMeter(mPreferenceEnd), 2));
                }
            } else {
                // The phone has been switched to METRIC
                if ( mCalculateDaltonLaw.getDefaultEnd().equals(MyConstants.ZERO_D)) {
                    mCalculateDaltonLaw.setDefaultEnd(MyFunctions.roundDown(mMyCalcMetric.convertFeetToMeter(mPreferenceEnd),2));
                    mCalculateDaltonLaw.setOtherEnd(mPreferenceEnd);
                }
            }
        } else {
            // The phone is in METRIC
            if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
                // The phone Has been switched to IMPERIAL
                if (mCalculateDaltonLaw.getDefaultEnd().equals(MyConstants.ZERO_D)) {
                    mCalculateDaltonLaw.setDefaultEnd(MyFunctions.roundDown(mMyCalcMetric.convertMeterToFeet(mPreferenceEnd), 2));
                    mCalculateDaltonLaw.setOtherEnd(mPreferenceEnd);
                }
            } else {
                // The phone has been switched to METRIC
                if (mCalculateDaltonLaw.getDefaultEnd().equals(MyConstants.ZERO_D)) {
                    mCalculateDaltonLaw.setDefaultEnd(mPreferenceEnd);
                    mCalculateDaltonLaw.setOtherEnd(MyFunctions.roundDown(mMyCalcMetric.convertMeterToFeet(mPreferenceEnd), 2));
                }
            }
        }

        // Last values stored as always the same as the Preferred Unit
        mDefaultSalinity = preferences.getBoolean(getString(R.string.code_default_salinity), true);

        // Set the values in the POJO
        mCalculateDaltonLaw.setDefaultSalinity(mDefaultSalinity);
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            mCalculateDaltonLaw.setDefaultSalinityPosition(0);
        } else {
            mCalculateDaltonLaw.setDefaultSalinityPosition(1);
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.apply();
    }
}
