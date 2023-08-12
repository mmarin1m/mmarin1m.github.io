package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.Date;

import ca.myairbuddyandi.databinding.CylinderActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the CylinderActivity class
 */

public class CylinderActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CylinderActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private CylinderActivityBinding mBinding = null;
    private Cylinder mCylinder = new Cylinder();
    private CylinderType mCylinderType = new CylinderType();
    private MyCalc mMyCalc;
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.cylinder_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mCylinder = getIntent().getParcelableExtra(MyConstants.CYLINDER, Cylinder.class);
        } else {
            mCylinder = getIntent().getParcelableExtra(MyConstants.CYLINDER);
        }
        assert mCylinder != null;
        mCylinder.setContext(this);

        if (mCylinder.getCylinderNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mCylinder.getCylinderNo());
            }
        }

        mCylinder.mBinding = mBinding;

        //Set the data in the Spinner CylinderType
        mAirDa.open();
        ArrayList<CylinderType> cylinderTypeList = mAirDa.getAllCylinderTypesSpinner();
        // Get the 1st CylinderType in the list as the default
        mCylinderType.setCylinderType(cylinderTypeList.get(0).getCylinderType());
        mAirDa.getCylinderType(mCylinderType);
        ArrayAdapter<CylinderType> adapterCylinderType = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, cylinderTypeList);
        adapterCylinderType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCylinder.setAdapterCylinderType(adapterCylinderType);
        mCylinder.setItemsCylinderType(cylinderTypeList);

        if (mCylinder.getCylinderNo() == MyConstants.ZERO_L) {
            // Add mode (New Cylinder)
            // Data must be in the Model first in order to bind
            // All data initialization must be done to the Model and not the View to avoid Data Changed Event
            // Set the default values
            mCylinder.setCylinderType(mCylinderType.getCylinderType());
            mCylinder.setVolume(mCylinderType.getVolume());
            mCylinder.setRatedPressure(mCylinderType.getRatedPressure());
            // Use Today's date
            mCylinder.setLastVip(MyFunctions.getTodaysDate());
            mCylinder.setLastHydro(MyFunctions.getTodaysDate());
        }  else {
            // Edit mode
            mAirDa.getCylinder(mCylinder.getCylinderNo(),mCylinder);
        }

        mBinding.setCylinder(mCylinder);

        // Set the listeners
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mCylinder.getHasDataChanged()) {
                mDialogs.confirm(CylinderActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        mBinding.saveButton.setOnClickListener(view -> {
            // Validate data
            submitForm();
        });

        // To Add a Cylinder Type
        mBinding.addCylinderType.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CylinderTypeActivity.class);
            CylinderType cylinderType = new CylinderType();
            cylinderType.setCylinderType("");
            intent.putExtra(MyConstants.CYLINDER_TYPE, cylinderType);
            addLauncher.launch(intent);
        });

        // To Set the Last VIP date
        mBinding.editTextLV.setOnClickListener(view -> {
            Date dt = mCylinder.getLastVip();
            new DatePickerDialog(CylinderActivity.this, onDateSetListenerLV, MyFunctions.getYear(dt), MyFunctions.getMonthOfYear(dt), MyFunctions.getDayOfMonth(dt)).show();
        });

        // To Set the Last Hydro date
        mBinding.editTextLH.setOnClickListener(view -> {
            Date dt = mCylinder.getLastHydro();
            new DatePickerDialog(CylinderActivity.this, onDateSetListenerLH, MyFunctions.getYear(dt), MyFunctions.getMonthOfYear(dt), MyFunctions.getDayOfMonth(dt)).show();
        });

        mBinding.spinnerCylinderType.setFocusable(true);
        mBinding.spinnerCylinderType.setFocusableInTouchMode(true);
        mBinding.spinnerCylinderType.clearFocus();
        mBinding.spinnerCylinderType.requestFocus();

        mCylinder.setHasDataChanged(false);

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
    public void onBackPressed() {
        // Hard button on Phone
        if (mCylinder.getHasDataChanged()) {
            mDialogs.confirm(CylinderActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_cylinder_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mCylinder.getHasDataChanged()) {
                mDialogs.confirm(CylinderActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED CylinderType from the CylinderType activity
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mCylinderType = data.getParcelableExtra(MyConstants.CYLINDER_TYPE,CylinderType.class);
                    } else {
                        mCylinderType = data.getParcelableExtra(MyConstants.CYLINDER_TYPE);
                    }
                    // Populate the Spinner with the new value
                    mAirDa.open();
                    ArrayList<CylinderType> cylinderTypeList = mAirDa.getAllCylinderTypesSpinner();
                    //Find the CylinderType that just got added to the list
                    int position = cylinderTypeList.indexOf(mCylinderType);
                    mCylinderType.setCylinderType(cylinderTypeList.get(position).getCylinderType());
                    mAirDa.getCylinderType(mCylinderType);
                    ArrayAdapter<CylinderType> adapterCylinderType = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, cylinderTypeList);
                    adapterCylinderType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    mCylinder.setAdapterCylinderType(adapterCylinderType);
                    mCylinder.setItemsCylinderType(cylinderTypeList);
                    mBinding.setCylinder(mCylinder);
                    mBinding.spinnerCylinderType.setSelection(position + 1);
                }
            });

    // Validating and Saving functions

    public Runnable yesProc(){
        return () -> {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        };
    }

    public Runnable noProc(){
        return () -> {
            //Do nothing, stay on this activity
        };
    }

    DatePickerDialog.OnDateSetListener onDateSetListenerLV = (view, year, month, day) -> mBinding.editTextLV.setText(MyFunctions.formatDateString(getApplicationContext(), year, month, day));

    DatePickerDialog.OnDateSetListener onDateSetListenerLH = (view, year, month, day) -> mBinding.editTextLH.setText(MyFunctions.formatDateString(getApplicationContext(), year, month, day));

    private void submitForm() {
        if (!validateVolume()) {
            return;
        }

        if (!validateRatedPressure()) {
            return;
        }

        // Make sure the cylinder specs have not changed for a cylinder that belongs to a real dive
        if (!areSpecsTheSame()) {
            return;
        }

        // Save CYLINDER data
        if (mCylinder.getCylinderNo() == MyConstants.ZERO_L) {
            // Add mode
            mAirDa.createCylinder(mCylinder,false);
        }  else {
            // Edit mode
            mAirDa.updateCylinder(mCylinder);
        }

        // Return the parcel object with the newly saved data
        Intent intent = new Intent();
        intent.putExtra(MyConstants.CYLINDER,mCylinder);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateVolume() {
        // Required
        // Must be between 1 and 149
        if (mBinding.editTextVO.getText().toString().trim().isEmpty() || mBinding.editTextVO.getText().toString().trim().isEmpty() || !isValidVolume(mCylinder.getVolume())) {
            @SuppressLint("StringFormatMatches") String message = String.format(getResources().getString(R.string.msg_volume),mMyCalc.getMinVolume(),mMyCalc.getMaxVolume(),mMyCalc.getVolumeUnit());
            mBinding.editTextVO.setError(message);
            requestFocus(mBinding.editTextVO);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateRatedPressure() {
        // Required
        // Must be between 500 and 4350 psi
        if (mBinding.editTextRP.getText().toString().trim().isEmpty() || !isValidRatedPressure(mCylinder.getRatedPressure())) {
            @SuppressLint("StringFormatMatches") String message = String.format(getResources().getString(R.string.msg_rated_pressure), mMyCalc.getMinRatedPressure(), mMyCalc.getMaxRatedPressure(),mMyCalc.getPressureUnit());
            mBinding.editTextRP.setError(message);
            requestFocus(mBinding.editTextRP);
            return false;
        } else {
            return true;
        }
    }

    private boolean areSpecsTheSame() {
        if (mCylinder.getCylinderNo() != MyConstants.ZERO_L) {
            // Existing cylinder
            // In Edit mode
            if (mAirDa.cylinderUsedByRealDive(mCylinder.getCylinderNo()) > 0
            && mCylinder.getHasSpecChanged()) {
                showError(getString(R.string.msg_groupp_used_real_dive));
                return false;
            }
        }
        return true;
    }

    private boolean isValidVolume(Double volume) {
        return (volume >= mMyCalc.getMinVolume() && volume <= mMyCalc.getMaxVolume());
    }

    private boolean isValidRatedPressure(Double ratedPressure) {
        return (ratedPressure >= mMyCalc.getMinRatedPressure() && ratedPressure <= mMyCalc.getMaxRatedPressure());
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

    public void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
