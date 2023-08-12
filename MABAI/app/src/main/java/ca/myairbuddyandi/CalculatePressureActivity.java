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

import ca.myairbuddyandi.databinding.CalculatePressureActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculatePressureActivity class
 */

public class CalculatePressureActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculatePressureActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculatePressure mCalculatePressure = new CalculatePressure();
    private CalculatePressureActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private boolean mDefaultSalinity; // true = Salt = 0 position, false = Fresh = 1 position
    private boolean mPreviousSalinity;
    private Double mDefaultAta;
    private Double mDefaultDepth;
    private Double mDefaultPsi;
    private Double mDefaultPsia;
    private Double mDefaultPsig;
    private String mDefaultUnit;

    // Other
    private Double mOtherAta;
    private Double mOtherDepth;
    private Double mOtherPsi;
    private Double mOtherPsia;
    private Double mOtherPsig;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_pressure_activity);

        mCalculatePressure.mBinding = mBinding;

        mBinding.setCalculatePressure(mCalculatePressure);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        mBinding.pressurePsi.setOnClickListener(view -> calculatePsi());

        mBinding.pressurePsi2.setOnClickListener(view -> calculatePsi());

        mBinding.pressurePsia.setOnClickListener(view -> calculatePsia());

        mBinding.pressurePsia2.setOnClickListener(view -> calculatePsia());

        mBinding.pressurePsig.setOnClickListener(view -> calculatePsig());

        mBinding.pressurePsig2.setOnClickListener(view -> calculatePsig());

        mBinding.depthButton.setOnClickListener(view -> calculateDepth());

        mBinding.depthButton2.setOnClickListener(view -> calculateDepth());

        mBinding.ataButton.setOnClickListener(view -> calculateAta());

        mBinding.ataButton2.setOnClickListener(view -> calculateAta());

        mBinding.clearButton.setOnClickListener(view -> clear());

        //Set the data in the Spinner Salinity
        String[] itemsDefaultSalinity = getResources().getStringArray(R.array.salinity_arrays);
        ArrayAdapter<String> adapterDefaultSalinity = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsDefaultSalinity);
        adapterDefaultSalinity.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCalculatePressure.setAdapterDefaultSalinity(adapterDefaultSalinity);

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculatePressureActivity.requestFocus();

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_pressure));
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

    private void calculateAta() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialAta();
        } else {
            // Metric calculations
            calculateMetricAta();
        }

        mBinding.defaultDepth.setText(String.valueOf(mCalculatePressure.getDefaultDepth()));
        mBinding.defaultAta.setText(String.valueOf(mCalculatePressure.getDefaultAta()));
        mBinding.defaultPsi.setText(String.valueOf(mCalculatePressure.getDefaultPsi()));
        mBinding.defaultPsia.setText(String.valueOf(mCalculatePressure.getDefaultPsia()));
        mBinding.defaultPsig.setText(String.valueOf(mCalculatePressure.getDefaultPsig()));

        requestFocus(mBinding.defaultDepth, false);
    }

    private void calculateDepth() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialDepth();
        } else {
            // Metric calculations
            calculateMetricDepth();
        }

        mBinding.defaultDepth.setText(String.valueOf(mCalculatePressure.getDefaultDepth()));
        mBinding.defaultAta.setText(String.valueOf(mCalculatePressure.getDefaultAta()));
        mBinding.defaultPsi.setText(String.valueOf(mCalculatePressure.getDefaultPsi()));
        mBinding.defaultPsia.setText(String.valueOf(mCalculatePressure.getDefaultPsia()));
        mBinding.defaultPsig.setText(String.valueOf(mCalculatePressure.getDefaultPsig()));

        requestFocus(mBinding.defaultDepth, false);
    }

    private void calculatePsi() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialPressure();
        } else {
            // Metric calculations
            calculateMetricPressure();
        }

        mBinding.defaultDepth.setText(String.valueOf(mCalculatePressure.getDefaultDepth()));
        mBinding.defaultAta.setText(String.valueOf(mCalculatePressure.getDefaultAta()));
        mBinding.defaultPsi.setText(String.valueOf(mCalculatePressure.getDefaultPsi()));
        mBinding.defaultPsia.setText(String.valueOf(mCalculatePressure.getDefaultPsia()));
        mBinding.defaultPsig.setText(String.valueOf(mCalculatePressure.getDefaultPsig()));

        requestFocus(mBinding.defaultDepth, false);
    }

    private void calculatePsia() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialPressure();
        } else {
            // Metric calculations
            calculateMetricPressure();
        }

        mBinding.defaultDepth.setText(String.valueOf(mCalculatePressure.getDefaultDepth()));
        mBinding.defaultAta.setText(String.valueOf(mCalculatePressure.getDefaultAta()));
        mBinding.defaultPsi.setText(String.valueOf(mCalculatePressure.getDefaultPsi()));
        mBinding.defaultPsia.setText(String.valueOf(mCalculatePressure.getDefaultPsia()));
        mBinding.defaultPsig.setText(String.valueOf(mCalculatePressure.getDefaultPsig()));

        requestFocus(mBinding.defaultDepth, false);
    }

    private void calculatePsig() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialPressure();
        } else {
            // Metric calculations
            calculateMetricPressure();
        }

        mBinding.defaultDepth.setText(String.valueOf(mCalculatePressure.getDefaultDepth()));
        mBinding.defaultAta.setText(String.valueOf(mCalculatePressure.getDefaultAta()));
        mBinding.defaultPsi.setText(String.valueOf(mCalculatePressure.getDefaultPsi()));
        mBinding.defaultPsia.setText(String.valueOf(mCalculatePressure.getDefaultPsia()));
        mBinding.defaultPsig.setText(String.valueOf(mCalculatePressure.getDefaultPsig()));

        requestFocus(mBinding.defaultDepth, false);
    }

    private void calculateImperialAta() {
        // Get all entered values first
        mPreviousSalinity = mDefaultSalinity;
        mDefaultSalinity = mCalculatePressure.getDefaultSalinity();

        if (mCalculatePressure.getSource().equals(getString(R.string.button_ata))) {
            // Default
            mDefaultAta = mCalculatePressure.getDefaultAta();
            mDefaultDepth =  MyConstants.ZERO_D;
            mDefaultPsi = MyConstants.ZERO_D;
            mDefaultPsia = MyConstants.ZERO_D;
            mDefaultPsig = MyConstants.ZERO_D;
            // Other
            mOtherAta = mMyCalcImperial.convertAtaToBar(mDefaultAta);
            mOtherDepth = MyConstants.ZERO_D;
            mOtherPsi = MyConstants.ZERO_D;
            mOtherPsia = MyConstants.ZERO_D;
            mOtherPsig = MyConstants.ZERO_D;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psi))) {
            // Convert pressures
            mDefaultPsi = mCalculatePressure.getDefaultPsi();
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ATA_TO_PSI;
            mOtherPsi = mMyCalcMetric.convertPsiToBar(mDefaultPsi);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ONE_L;
            // Calculate depth from psi
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsi, mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertPressureToDepth(mOtherPsi, mDefaultSalinity);
            // Calculate ata from psi
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsi);
            // ata and pressure are the same in Metric
            mOtherAta = mOtherPsi;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psia))) {
            // Convert pressure
            mDefaultPsia = mCalculatePressure.getDefaultPsia();
            mDefaultPsi = mDefaultPsia;
            mDefaultPsig = mDefaultPsia - MyConstants.ATA_TO_PSI;
            mOtherPsia = mMyCalcMetric.convertPsiToBar(mDefaultPsia);
            mOtherPsi = mOtherPsia;
            mOtherPsig = mOtherPsia - MyConstants.ONE_L;
            // Calculate depth from psia
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsia, mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertPressureToDepth(mOtherPsia, mDefaultSalinity);
            // Calculate ata from psia
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsia);
            // ata and pressure are the same in Metric
            mOtherAta = mOtherPsi;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psig))) {
            // Convert pressure
            mDefaultPsig = mCalculatePressure.getDefaultPsig();
            mDefaultPsi = mDefaultPsig + MyConstants.ATA_TO_PSI;
            mDefaultPsia = mDefaultPsi;
            mOtherPsig = mMyCalcMetric.convertPsiToBar(mDefaultPsig);
            mOtherPsi = mOtherPsig + MyConstants.ONE_L;
            mOtherPsia = mOtherPsi;
            // Calculate depth from psi and not psig
            //  0.0  psig = 1 ata = 33 fsw
            // 14.69 psig = 2 ata = 66 fsw
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsi,mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertPressureToDepth(mOtherPsi,mDefaultSalinity);
            // Calculate ata from psi and not psig
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsi);
            // ata and pressure are the same in Metric
            mOtherAta = mOtherPsi;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_depth))
                || (mCalculatePressure.getDefaultDepth().equals(MyConstants.ZERO_D) && mCalculatePressure.getDefaultPsi().equals(MyConstants.ZERO_D))
                || (mPreviousSalinity != mDefaultSalinity)) {
            // If nothing entered, assumes depth
            // Convert depth
            mDefaultDepth = mCalculatePressure.getDefaultDepth();
            mOtherDepth = mMyCalcMetric.convertFeetToMeter(mDefaultDepth);
            // Calculate pressure from depth
            mDefaultPsi = mMyCalcImperial.convertDepthToPressure(mDefaultDepth,mDefaultSalinity);
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ATA_TO_PSI;
            mOtherPsi = mMyCalcMetric.convertDepthToPressure(mOtherDepth,mDefaultSalinity);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ONE_L;
            // Calculate ata from depth
            mDefaultAta = mMyCalcImperial.getAta(mDefaultDepth,mDefaultSalinity);
            // ata and pressure are the same in Metric
            mOtherAta = mOtherPsi;
        }

        // Set the calculated values in the POJO
        // If not the first time or nothing changed, reused the same values
        mCalculatePressure.setDefaultDepth(MyFunctions.roundUp(mDefaultDepth,1));
        mCalculatePressure.setDefaultAta(MyFunctions.roundUp(mDefaultAta,2));
        mCalculatePressure.setDefaultPsi(MyFunctions.roundUp(mDefaultPsi,2));
        mCalculatePressure.setDefaultPsia(MyFunctions.roundUp(mDefaultPsia,2));
        mCalculatePressure.setDefaultPsig(MyFunctions.roundUp(mDefaultPsig,2));

        mCalculatePressure.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
        mCalculatePressure.setOtherAta(MyFunctions.roundUp(mOtherAta,2));
        mCalculatePressure.setOtherPsi(MyFunctions.roundUp(mOtherPsi,2));
        mCalculatePressure.setOtherPsia(MyFunctions.roundUp(mOtherPsia,2));
        mCalculatePressure.setOtherPsig(MyFunctions.roundUp(mOtherPsig,2));
    }

    private void calculateImperialDepth() {
        // Get all entered values first
        mPreviousSalinity = mDefaultSalinity;
        mDefaultSalinity = mCalculatePressure.getDefaultSalinity();

        if (mCalculatePressure.getSource().equals(getString(R.string.button_depth))) {
            // Default
            mDefaultDepth = mCalculatePressure.getDefaultDepth();
            mDefaultAta = MyConstants.ZERO_D;
            mDefaultPsi = MyConstants.ZERO_D;
            mDefaultPsia = MyConstants.ZERO_D;
            mDefaultPsig = MyConstants.ZERO_D;
            // Other
            mOtherDepth = mMyCalcImperial.convertFeetToMeter(mDefaultDepth);
            mOtherAta = MyConstants.ZERO_D;
            mOtherPsi = MyConstants.ZERO_D;
            mOtherPsia = MyConstants.ZERO_D;
            mOtherPsig = MyConstants.ZERO_D;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psi))) {
            // Convert pressure
            mDefaultPsi = mCalculatePressure.getDefaultPsi();
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ATA_TO_PSI;
            mOtherPsi = mMyCalcMetric.convertPsiToBar(mDefaultPsi);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ONE_L;
            // Calculate ata from psi
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsi);
            // ata and pressure are the same in Metric
            mOtherAta = mOtherPsi;
            // Calculate depth from psi
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsi, mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertPressureToDepth(mOtherPsi, mDefaultSalinity);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psia))) {
            // Convert pressure
            mDefaultPsia = mCalculatePressure.getDefaultPsia();
            mDefaultPsi = mDefaultPsia;
            mDefaultPsig = mDefaultPsia - MyConstants.ATA_TO_PSI;
            mOtherPsia = mMyCalcMetric.convertPsiToBar(mDefaultPsia);
            mOtherPsi = mOtherPsia;
            mOtherPsig = mOtherPsia - MyConstants.ONE_L;
            // Calculate ata from psia
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsia);
            // ata and pressure are the same in Metric
            mOtherAta = mOtherPsia;
            // Calculate depth from psia
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsia, mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertPressureToDepth(mOtherPsia, mDefaultSalinity);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psig))) {
            // Convert pressure
            mDefaultPsig = mCalculatePressure.getDefaultPsig();
            mDefaultPsi = mDefaultPsig + MyConstants.ATA_TO_PSI;
            mDefaultPsia = mDefaultPsi;
            mOtherPsig = mMyCalcMetric.convertPsiToBar(mDefaultPsig);
            mOtherPsi = mOtherPsig + MyConstants.ONE_L;
            mOtherPsia = mOtherPsi;
            // Calculate ata from psi and not psig
            //  0.0 fsw = 0 ata =  0.00 psig
            // 33.0 fsw = 1 ata = 14.69 psig
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsi);
            // ata and pressure are the same in Metric
            mOtherAta = mOtherPsi;
            // Calculate depth from psi and not psig
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsi,mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertPressureToDepth(mOtherPsi,mDefaultSalinity);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_ata))
                || (mCalculatePressure.getDefaultAta().equals(MyConstants.ZERO_D) && mCalculatePressure.getDefaultPsi().equals(MyConstants.ZERO_D))
                || (mPreviousSalinity != mDefaultSalinity)) {
            // If nothing entered, assumes ata
            // Convert ata
            mDefaultAta = mCalculatePressure.getDefaultAta();
            mOtherAta = mMyCalcImperial.convertAtaToBar(mDefaultAta);
            // Calculate pressure from ata
            mDefaultPsi = mMyCalcImperial.convertAtaToPressure(mDefaultAta);
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ATA_TO_PSI;
            // ata and pressure are the same in Metric
            mOtherPsi = mOtherAta;
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ONE_L;
            // Calculate depth from ata
            mDefaultDepth = mMyCalcImperial.convertAtaToDepth(mDefaultAta,mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertBarToDepth(mOtherAta,mDefaultSalinity);
        }

        // Set the calculated values in the POJO
        // If not the first time or nothing changed, reused the same values
        mCalculatePressure.setDefaultDepth(MyFunctions.roundUp(mDefaultDepth,1));
        mCalculatePressure.setDefaultAta(MyFunctions.roundUp(mDefaultAta,2));
        mCalculatePressure.setDefaultPsi(MyFunctions.roundUp(mDefaultPsi,2));
        mCalculatePressure.setDefaultPsia(MyFunctions.roundUp(mDefaultPsia,2));
        mCalculatePressure.setDefaultPsig(MyFunctions.roundUp(mDefaultPsig,2));

        mCalculatePressure.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
        mCalculatePressure.setOtherAta(MyFunctions.roundUp(mOtherAta,2));
        mCalculatePressure.setOtherPsi(MyFunctions.roundUp(mOtherPsi,2));
        mCalculatePressure.setOtherPsia(MyFunctions.roundUp(mOtherPsia,2));
        mCalculatePressure.setOtherPsig(MyFunctions.roundUp(mOtherPsig,2));
    }

    private void calculateImperialPressure() {
        // Calculate Imperial Pressure

        // Get all entered values first
        mPreviousSalinity = mDefaultSalinity;
        mDefaultSalinity = mCalculatePressure.getDefaultSalinity();

        if (mCalculatePressure.getSource().equals(getString(R.string.button_ata))) {
            // Convert ata
            mDefaultAta = mCalculatePressure.getDefaultAta();
            mOtherAta = mMyCalcImperial.convertAtaToBar(mDefaultAta);
            // Calculate depth from ata
            mDefaultDepth = mMyCalcImperial.convertAtaToDepth(mDefaultAta,mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertBarToDepth(mOtherAta,mDefaultSalinity);
            // Calculate pressures from ata
            mDefaultPsi = mMyCalcImperial.convertAtaToPressure(mDefaultAta);
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ATA_TO_PSI;
            // ata and pressure in bar are the same in Metric
            mOtherPsi = mOtherAta;
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ONE_L;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_depth))) {
//                || (mCalculatePressure.getDefaultDepth().equals(MyConstants.ZERO_D) && mCalculatePressure.getDefaultAta().equals(MyConstants.ZERO_D))
//                || (mPreviousSalinity != mDefaultSalinity)) {
            // If nothing entered, assumes Depth
            // Convert Depth
            mDefaultDepth = mCalculatePressure.getDefaultDepth();
            mOtherDepth = mMyCalcMetric.convertFeetToMeter(mDefaultDepth);
            // Calculate ata from depth
            mDefaultAta = mMyCalcImperial.getAta(mDefaultDepth, mDefaultSalinity);
            mOtherAta = mMyCalcMetric.getBar(mOtherDepth,mDefaultSalinity);
            // Calculate pressures from depth
            mDefaultPsi = mMyCalcImperial.convertDepthToPressure(mDefaultDepth,mDefaultSalinity);
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ATA_TO_PSI;
            // ata and pressure in bar are the same in Metric
            mOtherPsi = mOtherAta;
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ONE_L;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psi))) {
            mDefaultPsi = mCalculatePressure.getDefaultPsi();
            // Convert psi to depth
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsi,mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertFeetToMeter(mDefaultDepth);

            // Convert psi to ata
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsi);
            mOtherAta = mMyCalcMetric.getBar(mOtherDepth,mDefaultSalinity);

            // Convert psi to psia
            mDefaultPsia = mDefaultPsi;

            // Convert psi to psig
            mDefaultPsig = mDefaultPsi - MyConstants.ATA_TO_PSI;

            // ata and pressure in bar are the same in Metric
            mOtherPsi = mOtherAta;
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ONE_L;

        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psia))) {
            mDefaultPsia = mCalculatePressure.getDefaultPsia();
            // Convert psia to depth
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsia,mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertFeetToMeter(mDefaultDepth);

            // Convert psia to ata
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsia);
            mOtherAta = mMyCalcMetric.getBar(mOtherDepth,mDefaultSalinity);

            // Convert psia to psi
            mDefaultPsi = mDefaultPsia;

            // Convert psia to psig
            mDefaultPsig = mDefaultPsia - MyConstants.ATA_TO_PSI;

            // ata and pressure in bar are the same in Metric
            mOtherPsi = mOtherAta;
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ONE_L;

        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psig))) {
            mDefaultPsig = mCalculatePressure.getDefaultPsig();
            // Convert pressure
            mDefaultPsig = mCalculatePressure.getDefaultPsig();
            mDefaultPsi = mDefaultPsig + MyConstants.ATA_TO_PSI;
            mDefaultPsia = mDefaultPsi;
            mOtherPsig = mMyCalcMetric.convertPsiToBar(mDefaultPsig);
            mOtherPsi = mOtherPsig + MyConstants.ONE_L;
            mOtherPsia = mOtherPsi;
            // Calculate depth from psi and not psig
            //  0.0  psig = 1 ata = 33 fsw
            // 14.69 psig = 2 ata = 66 fsw
            mDefaultDepth = mMyCalcImperial.convertPressureToDepth(mDefaultPsi,mDefaultSalinity);
            mOtherDepth = mMyCalcMetric.convertPressureToDepth(mOtherPsi,mDefaultSalinity);
            // Calculate ata from psi and not psig
            mDefaultAta = mMyCalcImperial.convertPressureToAta(mDefaultPsi);
            // ata and pressure are the same in Metric
            mOtherAta = mOtherPsi;
        }

        // Set the calculated values in the POJO
        // If not the first time or nothing changed, reused the same values
        mCalculatePressure.setDefaultDepth(MyFunctions.roundUp(mDefaultDepth,1));
        mCalculatePressure.setDefaultAta(MyFunctions.roundUp(mDefaultAta,2));
        mCalculatePressure.setDefaultPsi(MyFunctions.roundUp(mDefaultPsi,2));
        mCalculatePressure.setDefaultPsia(MyFunctions.roundUp(mDefaultPsia,2));
        mCalculatePressure.setDefaultPsig(MyFunctions.roundUp(mDefaultPsig,2));

        mCalculatePressure.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
        mCalculatePressure.setOtherAta(MyFunctions.roundUp(mOtherAta,2));
        mCalculatePressure.setOtherPsi(MyFunctions.roundUp(mOtherPsi,2));
        mCalculatePressure.setOtherPsia(MyFunctions.roundUp(mOtherPsia,2));
        mCalculatePressure.setOtherPsig(MyFunctions.roundUp(mOtherPsig,2));
    }

    private void calculateMetricAta() {

        // Get all entered values first
        mPreviousSalinity = mDefaultSalinity;
        mDefaultSalinity = mCalculatePressure.getDefaultSalinity();

        if (mCalculatePressure.getSource().equals(getString(R.string.button_ata))) {
            // Default
            mDefaultAta = mCalculatePressure.getDefaultAta();
            mDefaultDepth =  MyConstants.ZERO_D;
            mDefaultPsi = MyConstants.ZERO_D;
            mDefaultPsia = MyConstants.ZERO_D;
            mDefaultPsig = MyConstants.ZERO_D;
            // Other
            mOtherAta = mMyCalcMetric.convertBarToAta(mDefaultAta);
            mOtherDepth = MyConstants.ZERO_D;
            mOtherPsi = MyConstants.ZERO_D;
            mOtherPsia = MyConstants.ZERO_D;
            mOtherPsig = MyConstants.ZERO_D;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psi))) {
            // Convert pressure
            mDefaultPsi = mCalculatePressure.getDefaultPsi();
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ONE_L;
            mOtherPsi = mMyCalcImperial.convertBarToPsi(mDefaultPsi);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ATA_TO_PSI;
            // Calculate depth from psi
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsi, mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertPressureToDepth(mOtherPsi, mDefaultSalinity);
            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsi;
            // Calculate ata from psi
            mOtherAta = mMyCalcImperial.convertPressureToAta(mOtherPsi);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psia))) {
            // Convert pressures
            mDefaultPsia = mCalculatePressure.getDefaultPsia();
            mDefaultPsi = mDefaultPsia;
            mDefaultPsig = mDefaultPsia - MyConstants.ONE_L;
            mOtherPsia = mMyCalcImperial.convertBarToPsi(mDefaultPsia);
            mOtherPsi = mOtherPsia;
            mOtherPsig = mOtherPsia - MyConstants.ATA_TO_PSI;
            // Calculate depth from psia
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsia, mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertPressureToDepth(mOtherPsia, mDefaultSalinity);
            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsia;
            // Calculate ata from psia
            mOtherAta = mMyCalcImperial.convertPressureToAta(mOtherPsia);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psig))) {
            // Convert pressure
            mDefaultPsig = mCalculatePressure.getDefaultPsig();
            mDefaultPsi = mDefaultPsig + MyConstants.ONE_L;
            mDefaultPsia = mDefaultPsi;
            mOtherPsig = mMyCalcImperial.convertBarToPsi(mDefaultPsig);
            mOtherPsi = mOtherPsig + MyConstants.ATA_TO_PSI;
            mOtherPsia = mOtherPsi;
            // Calculate depth from psi and not psig
            // 0.0 barg = 1 bar =  0 msw
            // 1.0 barg = 2 bar = 10 msw
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsi,mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertPressureToDepth(mOtherPsi,mDefaultSalinity);
            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsi;
            // Calculate ata from psi and not psig
            mOtherAta = mMyCalcImperial.convertPressureToAta(mOtherPsi);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_depth))
                || (mCalculatePressure.getDefaultDepth().equals(MyConstants.ZERO_D) && mCalculatePressure.getDefaultPsi().equals(MyConstants.ZERO_D))
                || (mPreviousSalinity != mDefaultSalinity)) {
            // If nothing entered, assumes depth
            // Convert depth
            mDefaultDepth = mCalculatePressure.getDefaultDepth();
            mOtherDepth = mMyCalcImperial.convertMeterToFeet(mDefaultDepth);
            // Calculate pressure from depth
            mDefaultPsi = mMyCalcMetric.convertDepthToPressure(mDefaultDepth,mDefaultSalinity);
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ONE_L;
            mOtherPsi = mMyCalcImperial.convertDepthToPressure(mOtherDepth,mDefaultSalinity);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ATA_TO_PSI;
            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsi;
            // Calculate ata from depth
            mOtherAta = mMyCalcImperial.getAta(mOtherDepth,mDefaultSalinity);
        }

        // Set the calculated values in the POJO
        // If not the first time or nothing changed, reused the same values
        mCalculatePressure.setDefaultDepth(MyFunctions.roundUp(mDefaultDepth,1));
        mCalculatePressure.setDefaultAta(MyFunctions.roundUp(mDefaultAta,2));
        mCalculatePressure.setDefaultPsi(MyFunctions.roundUp(mDefaultPsi,2));
        mCalculatePressure.setDefaultPsia(MyFunctions.roundUp(mDefaultPsia,2));
        mCalculatePressure.setDefaultPsig(MyFunctions.roundUp(mDefaultPsig,2));

        mCalculatePressure.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
        mCalculatePressure.setOtherAta(MyFunctions.roundUp(mOtherAta,2));
        mCalculatePressure.setOtherPsi(MyFunctions.roundUp(mOtherPsi,2));
        mCalculatePressure.setOtherPsia(MyFunctions.roundUp(mOtherPsia,2));
        mCalculatePressure.setOtherPsig(MyFunctions.roundUp(mOtherPsig,2));
    }

    private void calculateMetricDepth() {
        // Get all entered values first
        mPreviousSalinity = mDefaultSalinity;
        mDefaultSalinity = mCalculatePressure.getDefaultSalinity();

        if (mCalculatePressure.getSource().equals(getString(R.string.button_depth))) {
            // Default
            mDefaultDepth = mCalculatePressure.getDefaultDepth();
            mDefaultAta = MyConstants.ZERO_D;
            mDefaultPsi = MyConstants.ZERO_D;
            mDefaultPsia = MyConstants.ZERO_D;
            mDefaultPsig = MyConstants.ZERO_D;
            // Other
            mOtherDepth = mMyCalcMetric.convertMeterToFeet(mDefaultDepth);
            mOtherAta = MyConstants.ZERO_D;
            mOtherPsi = MyConstants.ZERO_D;
            mOtherPsia = MyConstants.ZERO_D;
            mOtherPsig = MyConstants.ZERO_D;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psi))) {
            // Convert pressure
            mDefaultPsi = mCalculatePressure.getDefaultPsi();
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ONE_L;
            mOtherPsi = mMyCalcImperial.convertBarToPsi(mDefaultPsi);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ATA_TO_PSI;
            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsi;
            // Calculate ata from psi
            mOtherAta = mMyCalcImperial.convertPressureToAta(mOtherPsi);
            // Calculate depth from psi
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsi, mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertPressureToDepth(mOtherPsi, mDefaultSalinity);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psia))) {
            // Convert pressure
            mDefaultPsia = mCalculatePressure.getDefaultPsia();
            mDefaultPsi = mDefaultPsia;
            mDefaultPsig = mDefaultPsia - MyConstants.ONE_L;
            mOtherPsia = mMyCalcImperial.convertBarToPsi(mDefaultPsia);
            mOtherPsi = mOtherPsia;
            mOtherPsig = mOtherPsia - MyConstants.ATA_TO_PSI;
            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsia;
            // Calculate ata from psia
            mOtherAta = mMyCalcImperial.convertPressureToAta(mOtherPsia);
            // Calculate depth from psia
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsia, mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertPressureToDepth(mOtherPsia, mDefaultSalinity);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psig))) {
            // Convert pressure
            mDefaultPsig = mCalculatePressure.getDefaultPsig();
            mDefaultPsi = mDefaultPsig + MyConstants.ONE_L;
            mDefaultPsia = mDefaultPsi;
            mOtherPsig = mMyCalcImperial.convertBarToPsi(mDefaultPsig);
            mOtherPsi = mOtherPsig + MyConstants.ATA_TO_PSI;
            mOtherPsia = mOtherPsi;
            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsi;
            mOtherAta = mMyCalcImperial.convertPressureToAta(mOtherPsi);
            // Calculate depth from psi and not psig
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsi,mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertPressureToDepth(mOtherPsi,mDefaultSalinity);
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_ata))
                || (mCalculatePressure.getDefaultAta().equals(MyConstants.ZERO_D) && mCalculatePressure.getDefaultPsi().equals(MyConstants.ZERO_D))
                || (mPreviousSalinity != mDefaultSalinity)) {
            // If nothing entered, assumes ata
            // Convert ata
            mDefaultAta = mCalculatePressure.getDefaultAta();
            mOtherAta = mMyCalcImperial.convertBarToAta(mDefaultAta);
            // Calculate pressure from ata
            // ata and pressure are the same in Metric
            mDefaultPsi = mDefaultAta;
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ONE_L;
            mOtherPsi = mMyCalcImperial.convertAtaToPressure(mOtherAta);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ATA_TO_PSI;
            // Calculate depth from ata
            mDefaultDepth = mMyCalcMetric.convertBarToDepth(mDefaultAta,mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertAtaToDepth(mOtherAta,mDefaultSalinity);
        }

        // Set the calculated values in the POJO
        // If not the first time or nothing changed, reused the same values
        mCalculatePressure.setDefaultDepth(MyFunctions.roundUp(mDefaultDepth,1));
        mCalculatePressure.setDefaultAta(MyFunctions.roundUp(mDefaultAta,2));
        mCalculatePressure.setDefaultPsi(MyFunctions.roundUp(mDefaultPsi,2));
        mCalculatePressure.setDefaultPsia(MyFunctions.roundUp(mDefaultPsia,2));
        mCalculatePressure.setDefaultPsig(MyFunctions.roundUp(mDefaultPsig,2));

        mCalculatePressure.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
        mCalculatePressure.setOtherAta(MyFunctions.roundUp(mOtherAta,2));
        mCalculatePressure.setOtherPsi(MyFunctions.roundUp(mOtherPsi,2));
        mCalculatePressure.setOtherPsia(MyFunctions.roundUp(mOtherPsia,2));
        mCalculatePressure.setOtherPsig(MyFunctions.roundUp(mOtherPsig,2));
    }

    private void calculateMetricPressure() {
        // Get all entered values first
        mPreviousSalinity = mDefaultSalinity;
        mDefaultSalinity = mCalculatePressure.getDefaultSalinity();

        if (mCalculatePressure.getSource().equals(getString(R.string.button_ata))) {
            // Convert ata
            mDefaultAta = mCalculatePressure.getDefaultAta();
            mOtherAta = mMyCalcImperial.convertBarToAta(mDefaultAta);
            // Calculate depth from ata
            mDefaultDepth = mMyCalcMetric.convertBarToDepth(mDefaultAta, mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertAtaToDepth(mOtherAta,mDefaultSalinity);
            // ata and pressure are the same in Metric
            mDefaultPsi = mDefaultAta;
            // Calculate other imperial pressures from ata
            mOtherPsi = mMyCalcImperial.convertAtaToPressure(mOtherAta);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ATA_TO_PSI;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_depth))) {
//                || (mCalculatePressure.getDefaultDepth().equals(MyConstants.ZERO_D) && mCalculatePressure.getDefaultAta().equals(MyConstants.ZERO_D))
//                || (mPreviousSalinity != mDefaultSalinity)) {
            // If nothing entered, assumes Depth
            // Convert Depth
            mDefaultDepth = mCalculatePressure.getDefaultDepth();
            mOtherDepth = mMyCalcImperial.convertMeterToFeet(mDefaultDepth);
            // Calculate ata from depth
            mDefaultAta = mMyCalcMetric.getBar(mDefaultDepth, mDefaultSalinity);
            mOtherAta = mMyCalcImperial.getAta(mOtherDepth,mDefaultSalinity);
            // ata and pressure are the same in Metric
            mDefaultPsi = mDefaultAta;
            mDefaultPsia = mDefaultPsi;
            mDefaultPsig = mDefaultPsi - MyConstants.ONE_L;
            // Calculate other imperial pressures from depth
            mOtherPsi = mMyCalcImperial.convertDepthToPressure(mOtherDepth,mDefaultSalinity);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ATA_TO_PSI;
        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psi))) {
            mDefaultPsi = mCalculatePressure.getDefaultPsi();
            // Convert psi to depth
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsi,mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertAtaToPressure(mDefaultDepth);

            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsi;

            mOtherAta = mMyCalcImperial.convertBarToAta(mDefaultAta);

            // Convert psi to psia
            mDefaultPsia = mDefaultPsi;

            // Convert psi to psig
            mDefaultPsig = mDefaultPsi - MyConstants.ONE_L;

            mOtherPsi = mMyCalcImperial.convertBarToPsi(mDefaultPsi);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ATA_TO_PSI;

        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psia))) {
            mDefaultPsia = mCalculatePressure.getDefaultPsia();
            // Convert psia to depth
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsia,mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertAtaToPressure(mDefaultDepth);

            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsia;

            mOtherAta = mMyCalcImperial.convertBarToAta(mDefaultAta);

            // Convert psia to psi
            mDefaultPsi = mDefaultPsia;

            // Convert psia to psig
            mDefaultPsig = mDefaultPsia - MyConstants.ONE_L;

            mOtherPsi = mMyCalcImperial.convertBarToPsi(mDefaultPsi);
            mOtherPsia = mOtherPsi;
            mOtherPsig = mOtherPsi - MyConstants.ATA_TO_PSI;

        } else if (mCalculatePressure.getSource().equals(getString(R.string.button_psig))) {
            // Convert pressure
            mDefaultPsig = mCalculatePressure.getDefaultPsig();
            mDefaultPsi = mDefaultPsig + MyConstants.ONE_L;
            mDefaultPsia = mDefaultPsi;
            mOtherPsig = mMyCalcImperial.convertBarToPsi(mDefaultPsig);
            mOtherPsi = mOtherPsig + MyConstants.ATA_TO_PSI;
            mOtherPsia = mOtherPsi;
            // Calculate depth from psi and not psig
            // 0.0 barg = 1 bar =  0 msw
            // 1.0 barg = 2 bar = 10 msw
            mDefaultDepth = mMyCalcMetric.convertPressureToDepth(mDefaultPsi,mDefaultSalinity);
            mOtherDepth = mMyCalcImperial.convertPressureToDepth(mOtherPsi,mDefaultSalinity);
            // ata and pressure are the same in Metric
            mDefaultAta = mDefaultPsi;
            // Calculate ata from psi and not psig
            mOtherAta = mMyCalcImperial.convertPressureToAta(mOtherPsi);
        }

        // Set the calculated values in the POJO
        // If not the first time or nothing changed, reused the same values
        mCalculatePressure.setDefaultDepth(MyFunctions.roundUp(mDefaultDepth,1));
        mCalculatePressure.setDefaultAta(MyFunctions.roundUp(mDefaultAta,2));
        mCalculatePressure.setDefaultPsi(MyFunctions.roundUp(mDefaultPsi,2));
        mCalculatePressure.setDefaultPsia(MyFunctions.roundUp(mDefaultPsia,2));
        mCalculatePressure.setDefaultPsig(MyFunctions.roundUp(mDefaultPsig,2));

        mCalculatePressure.setOtherDepth(MyFunctions.roundUp(mOtherDepth,1));
        mCalculatePressure.setOtherAta(MyFunctions.roundUp(mOtherAta,2));
        mCalculatePressure.setOtherPsi(MyFunctions.roundUp(mOtherPsi,2));
        mCalculatePressure.setOtherPsia(MyFunctions.roundUp(mOtherPsia,2));
        mCalculatePressure.setOtherPsig(MyFunctions.roundUp(mOtherPsig,2));
    }

    private void clear() {
        // Reset the Default
        mCalculatePressure.setDefaultDepth(MyConstants.ZERO_D);
        mCalculatePressure.setDefaultAta(MyConstants.ZERO_D);
        mCalculatePressure.setDefaultPsi(MyConstants.ZERO_D);
        mCalculatePressure.setDefaultPsia(MyConstants.ZERO_D);
        mCalculatePressure.setDefaultPsig(MyConstants.ZERO_D);

        mBinding.defaultDepth.setText("0.0");
        mBinding.defaultAta.setText("0.0");
        mBinding.defaultPsi.setText("0.0");
        mBinding.defaultPsia.setText("0.0");
        mBinding.defaultPsig.setText("0.0");

        // Reset the Other
        mCalculatePressure.setOtherDepth(MyConstants.ZERO_D);
        mCalculatePressure.setOtherAta(MyConstants.ZERO_D);
        mCalculatePressure.setOtherPsi(MyConstants.ZERO_D);
        mCalculatePressure.setOtherPsia(MyConstants.ZERO_D);
        mCalculatePressure.setOtherPsig(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultDepth, false);
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
        String otherDepth = mBinding.otherDepth.getText().toString();
        String otherDepthLbl = mBinding.otherDepthLbl.getText().toString();
        String otherAta = mBinding.otherAta.getText().toString();
        String otherAtaLbl = mBinding.otherAtaLbl.getText().toString();
        String otherPsi = mBinding.otherPsi.getText().toString();
        String otherPsiLbl = mBinding.otherPsiLbl.getText().toString();
        String otherPsia = mBinding.otherPsia.getText().toString();
        String otherPsiaLbl = mBinding.otherPsiaLbl.getText().toString();
        String otherPsig = mBinding.otherPsig.getText().toString();
        String otherPsigLbl = mBinding.otherPsigLbl.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());
        mBinding.otherDepth.setText(mBinding.defaultDepth.getText().toString());
        mBinding.otherDepthLbl.setText(mBinding.defaultDepthLbl.getText().toString());
        mBinding.otherAta.setText(mBinding.defaultAta.getText().toString());
        mBinding.otherAtaLbl.setText(mBinding.defaultAtaLbl.getText().toString());
        mBinding.otherPsi.setText(mBinding.defaultPsi.getText().toString());
        mBinding.otherPsiLbl.setText(mBinding.defaultPsiLbl.getText().toString());
        mBinding.otherPsia.setText(mBinding.defaultPsia.getText().toString());
        mBinding.otherPsiaLbl.setText(mBinding.defaultPsiaLbl.getText().toString());
        mBinding.otherPsig.setText(mBinding.defaultPsig.getText().toString());
        mBinding.otherPsigLbl.setText(mBinding.defaultPsigLbl.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);
        mBinding.defaultDepth.setText(otherDepth);
        mBinding.defaultDepthLbl.setText(otherDepthLbl);
        mBinding.defaultAta.setText(otherAta);
        mBinding.defaultAtaLbl.setText(otherAtaLbl);
        mBinding.defaultPsi.setText(otherPsi);
        mBinding.defaultPsiLbl.setText(otherPsiLbl);
        mBinding.defaultPsia.setText(otherPsia);
        mBinding.defaultPsiaLbl.setText(otherPsiaLbl);
        mBinding.defaultPsig.setText(otherPsig);
        mBinding.defaultPsigLbl.setText(otherPsigLbl);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculatePressureActivity) {
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
        mCalculatePressure.setDefaultSalinity(mDefaultSalinity);
        if (mDefaultSalinity) {
            // true = Salt = 0 position
            mCalculatePressure.setDefaultSalinityPosition(0);
        } else {
            mCalculatePressure.setDefaultSalinityPosition(1);
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.putBoolean(getString(R.string.code_default_salinity), mCalculatePressure.getDefaultSalinity());
        edit.apply();
    }
}
