package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;

import ca.myairbuddyandi.databinding.CylinderTypeActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CylinderTypeActivity class
 */

public class CylinderTypeActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CylinderTypeActivity";

    // Public

    // Protected

    // Private
    private Boolean mNewRecord = false;
    private CylinderType mCylinderType = new CylinderType();
    private final AirDA mAirDA = new AirDA(this);
    private CylinderTypeActivityBinding mBinding = null;
    private MyCalc mMyCalc;
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.cylinder_type_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mCylinderType = getIntent().getParcelableExtra(MyConstants.CYLINDER_TYPE,CylinderType.class);
        } else {
            mCylinderType = getIntent().getParcelableExtra(MyConstants.CYLINDER_TYPE);
        }

        assert mCylinderType != null;
        if (mCylinderType.getCylinderType().trim().isEmpty() || mCylinderType.getCylinderType() == null) {
            // Add mode (New Cylinder Type)
            // Data must be in the Model first in order to bind
            // All data initialization must be done to the Model and not the View to avoid Data Changed Event
            mNewRecord = true;
            mCylinderType.setDescription("");
            mCylinderType.setRatedPressure(3000.0);
            mCylinderType.setVolume(80.0);
            mBinding.editTextCT.setSelectAllOnFocus(true);
        }  else {
            // Edit mode
            // Cannot update PK in Edit mode
            mBinding.editTextCT.setEnabled(false);
            mBinding.editTextCT.setInputType(InputType.TYPE_NULL);
        }

        mBinding.setCylinderType(mCylinderType);

        // Set the listeners
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mCylinderType.getHasDataChanged()) {
                mDialogs.confirm(CylinderTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

        mCylinderType.setHasDataChanged(false);

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_cylinder_type_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mCylinderType.getHasDataChanged()) {
                mDialogs.confirm(CylinderTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
                return true;
            } else {
                Intent intent = new Intent();
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
        if (mCylinderType.getHasDataChanged()) {
            mDialogs.confirm(CylinderTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
    }

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

    private void submitForm() {

        if (!validateType()) {
            return;
        }

        if (!validateDescription()) {
            return;
        }

        if (!validateVolume()) {
            return;
        }

        if (!validateRatedPressure()) {
            return;
        }

        // Save CYLINDER_TYPE data
        Integer rc;
        if (mNewRecord) {
            // Add mode
            mAirDA.open();
            rc = mAirDA.createCylinderType(mCylinderType);
            // End transaction with success
            // The close happens in the CylinderTypePickActivity.onActivityResult()
        }  else {
            // Edit mode
            mAirDA.open();
            rc = mAirDA.updateCylinderType(mCylinderType);
            // End transaction with success
            // The close happens in the CylinderTypePickActivity.onActivityResult()
        }

        // Return the parcel object with the newly saved data
        if (rc.equals(MyConstants.ZERO_I)) {
            Intent intent = new Intent();
            intent.putExtra(MyConstants.CYLINDER_TYPE,mCylinderType);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Resources res = getResources();
            String message = String.format(res.getString(R.string.msg_update_fk_constraint),res.getString(R.string.mn_cylinder_type),mCylinderType.getCylinderType());
            showUpdateResults(message);
        }
    }

    private boolean validateType() {
        // Required
        if (mBinding.editTextCT.getText().toString().trim().isEmpty()) {
            mBinding.editTextCT.setError(getString(R.string.msg_cylinder_type));
            requestFocus(mBinding.editTextCT);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateDescription() {
        // Required
        if (mBinding.editTextDE.getText().toString().trim().isEmpty()) {
            mBinding.editTextDE.setError(getString(R.string.msg_description));
            requestFocus(mBinding.editTextDE);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateVolume() {
        // Required
        // But must be between 1 and 149

        if (mBinding.editTextVO.getText().toString().trim().isEmpty() || !isValidVolume(mCylinderType.getVolume())) {
            String message = String.format(getResources().getString(R.string.msg_volume), mMyCalc.getMinVolume().toString(),mMyCalc.getMaxVolume().toString(),mMyCalc.getVolumeUnit());
            mBinding.editTextVO.setError(message);
            requestFocus(mBinding.editTextVO);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateRatedPressure() {
        // Required
        // But must be between 500 and 4350 psi

        if (mBinding.editTextVO.getText().toString().trim().isEmpty() || !isValidRatedPressure(mCylinderType.getRatedPressure())) {
            String message = String.format(getResources().getString(R.string.msg_rated_pressure), mMyCalc.getMinPressure().toString(), mMyCalc.getMaxRatedPressure().toString(), mMyCalc.getPressureUnit());
            mBinding.editTextRP.setError(message);
            requestFocus(mBinding.editTextRP);
            return false;
        } else {
            return true;
        }
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

    public void showUpdateResults(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> {
                    requestFocus(mBinding.editTextCT);
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
