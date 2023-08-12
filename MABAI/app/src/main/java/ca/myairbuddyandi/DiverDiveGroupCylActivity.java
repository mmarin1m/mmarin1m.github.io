package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
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

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.DiverDiveGrouppCylinderActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the DiverDiveGroupCylActivity class
 */

public class DiverDiveGroupCylActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DiverDiveGroupCylActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private DiverDiveGroupCyl mDiverDiveGroupCyl = new DiverDiveGroupCyl();
    private DiverDiveGrouppCylinderActivityBinding mBinding = null;
    private MyCalc mMyCalc;
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.diver_dive_groupp_cylinder_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mDiverDiveGroupCyl = getIntent().getParcelableExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, DiverDiveGroupCyl.class);
        } else {
            mDiverDiveGroupCyl = getIntent().getParcelableExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER);
        }

        assert mDiverDiveGroupCyl != null;
        if (mDiverDiveGroupCyl.getLogBookNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDiverDiveGroupCyl.getLogBookNo());
            }
        }

        //Get data for the Diver Dive Groups
        //Retrieving into the same POJO
        mAirDa.open();
        mDiverDiveGroupCyl = mAirDa.getDiverDiveGroupCylinderByGroup(mDiverDiveGroupCyl);

        mBinding.setDiverDiveGroupCyl(mDiverDiveGroupCyl);

        // Set the listeners
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mDiverDiveGroupCyl.getHasDataChanged()) {
                mDialogs.confirm(DiverDiveGroupCylActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
            } else {
                Intent intent = new Intent();
                intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, mDiverDiveGroupCyl);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        mBinding.saveButton.setOnClickListener(view -> {
            // Validate data
            submitForm();
        });

        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(this);
        } else {
            mMyCalc = new MyCalcMetric(this);
        }

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_consumption_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mDiverDiveGroupCyl.getHasDataChanged()) {
                mDialogs.confirm(DiverDiveGroupCylActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
                return true;
            } else {
                Intent intent = new Intent();
                intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, mDiverDiveGroupCyl);
                setResult(RESULT_CANCELED, intent);
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        if (mDiverDiveGroupCyl.getHasDataChanged()) {
            mDialogs.confirm(DiverDiveGroupCylActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
        } else {
            Intent intent = new Intent();
            intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, mDiverDiveGroupCyl);
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
    }

    // Validating and Saving functions

    public Runnable yesProc(){
        return () -> {
            Intent intent = new Intent();
            intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, mDiverDiveGroupCyl);
            setResult(RESULT_CANCELED, intent);
            finish();
        };
    }

    public Runnable noProc(){
        return () -> {
            //Do nothing, stay on this activity
        };
    }

    private void submitForm() {
        if (!validateBeginningPressure()) {
            return;
        }

        if (!validateEndingPressure()) {
            return;
        }

        if (!validateBpAndEp()) {
            return;
        }

        if (!validateO2()) {
            return;
        }

        if (!validateN()) {
            return;
        }

        if (!validateHe()) {
            return;
        }

        if (!validate_o2_n_he()) {
            return;
        }

        // Loop through the list and update all cylinders with same attribute values
        ArrayList<DiverDiveGroupCyl> diverDiveGroupCylList;
        diverDiveGroupCylList = mAirDa.getAllDiverDiveGroupCylinderByGroup(mDiverDiveGroupCyl);
        for (int i = 0; i < diverDiveGroupCylList.size(); i++) {
            DiverDiveGroupCyl diverDiveGroupCyl;
            diverDiveGroupCyl = diverDiveGroupCylList.get(i);
            diverDiveGroupCyl.setBeginningPressure(mDiverDiveGroupCyl.getBeginningPressure());
            diverDiveGroupCyl.setEndingPressure(mDiverDiveGroupCyl.getEndingPressure());
            diverDiveGroupCyl.setO2(mDiverDiveGroupCyl.getO2());
            diverDiveGroupCyl.setN(mDiverDiveGroupCyl.getN());
            diverDiveGroupCyl.setHe(mDiverDiveGroupCyl.getHe());
            diverDiveGroupCyl.setUsageType(getString(R.string.cd_blank));
            mAirDa.updateDiverDiveGroupCylinder(diverDiveGroupCyl);
        }

        // Return the parcel object with the newly saved data
        Intent intent = new Intent();
        intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, mDiverDiveGroupCyl);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateBeginningPressure() {
        // Required
        if (mBinding.editTextBP.getText().toString().trim().isEmpty() || !isValidBeginningPressure(mDiverDiveGroupCyl.getBeginningPressure())) {
            String message = String.format(getResources().getString(R.string.msg_beginning_pressure), mMyCalc.getMaxRatedPressure().toString(),mMyCalc.getPressureUnit());
            mBinding.editTextBP.setError(message);
            requestFocus(mBinding.editTextBP);
            return false;


        } else {
            return true;
        }
    }

    private boolean validateEndingPressure() {
        // Required
        if (mBinding.editTextEP.getText().toString().trim().isEmpty() || !isValidEndingPressure(mDiverDiveGroupCyl.getEndingPressure())) {
            String message = String.format(getResources().getString(R.string.msg_ending_pressure), mMyCalc.getMaxRatedPressure().toString(),mMyCalc.getPressureUnit());
            mBinding.editTextEP.setError(message);
            requestFocus(mBinding.editTextEP);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateBpAndEp() {
        // Required
        // Make sure the Ending Pressure is <= than the Beginning Pressure
        if (mDiverDiveGroupCyl.getEndingPressure() > mDiverDiveGroupCyl.getBeginningPressure()) {
            mBinding.editTextEP.setError(getString(R.string.msg_ending_pressure_smaller));
            requestFocus(mBinding.editTextEP);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateO2() {
        // Required
        if (mBinding.editTextO2.getText().toString().trim().isEmpty() || !isValidO2(mDiverDiveGroupCyl.getO2())) {
            mBinding.editTextO2.setError(getString(R.string.msg_o2));
            requestFocus(mBinding.editTextO2);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateN() {
        // Required
        if (mBinding.editTextN.getText().toString().trim().isEmpty() || !isValidN(mDiverDiveGroupCyl.getN())) {
            mBinding.editTextN.setError(getString(R.string.msg_n));
            requestFocus(mBinding.editTextN);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateHe() {
        // Required
        if (mBinding.editTextHE.getText().toString().trim().isEmpty() || !isValidHe(mDiverDiveGroupCyl.getHe())) {
            mBinding.editTextHE.setError(getString(R.string.msg_he));
            requestFocus(mBinding.editTextHE);
            return false;
        } else {
            return true;
        }
    }

    private boolean validate_o2_n_he() {
        // Required
        if (mDiverDiveGroupCyl.getO2() + mDiverDiveGroupCyl.getN() + mDiverDiveGroupCyl.getHe() != MyConstants.HUNDRED_PERCENT) {
            mBinding.editTextO2.setError(getString(R.string.msg_total_gas_mix));
            requestFocus(mBinding.editTextO2);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidBeginningPressure(Double beginningPressure) {
        return (beginningPressure >= MyConstants.ONE_D && beginningPressure <= mMyCalc.getMaxBeginningPressure());
    }

    private boolean isValidEndingPressure(Double EndingPressure) {
        return (EndingPressure >= MyConstants.ZERO_D && EndingPressure <= mMyCalc.getMaxBeginningPressure());
    }

    private static boolean isValidO2(Integer o2) {
        return (o2 >= MyConstants.TEN_PERCENT && o2 <=  MyConstants.HUNDRED_PERCENT);
    }

    private static boolean isValidN(Integer n) {
        return (n >= MyConstants.ZERO_I && n <= MyConstants.NINETY_PERCENT);
    }

    private static boolean isValidHe(Integer hE) {
        return (hE >= MyConstants.ZERO_I && hE <= MyConstants.NINETY_PERCENT);
    }

    private void requestFocus(View view) {
        if (view instanceof EditText) {
            // Only works for EditText
            view.clearFocus();
            view.requestFocus();
            ((EditText) view).selectAll();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            view.clearFocus();
            view.requestFocus();
        }
    }
}