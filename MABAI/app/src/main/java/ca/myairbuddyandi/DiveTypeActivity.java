package ca.myairbuddyandi;

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

import ca.myairbuddyandi.databinding.DiveTypeActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all the logic fo the DiveTypeActivity class
 */

public class DiveTypeActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DiveTypeActivity";

    // Public

    // Protected

    // Private
    private Boolean mNewRecord = false;
    private final AirDA mAirDA = new AirDA(this);
    private DiveTypeActivityBinding mBinding = null;
    private DiveType mDiveType = new DiveType();
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.dive_type_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mDiveType = getIntent().getParcelableExtra(MyConstants.DIVE_TYPE,DiveType.class);
        } else {
            mDiveType = getIntent().getParcelableExtra(MyConstants.DIVE_TYPE);
        }

        assert mDiveType != null;
        if (mDiveType.getDiveType().trim().isEmpty() || mDiveType.getDiveType() == null) {
            // Add mode (New Dive Type)
            // Data must be in the Model first in order to bind
            // All data initialization must be done to the Model and not the View to avoid Data Changed Event
            // No default values
            mNewRecord = true;
            mDiveType.setDescription("");
            mDiveType.setSortOrder(0);
            mDiveType.setInPicker("Y");
            mBinding.editTextDT.setSelectAllOnFocus(true);
        }  else {
            // Edit mode
            // Cannot update PK in Edit mode
            mBinding.editTextDT.setEnabled(false);
            mBinding.editTextDT.setInputType(InputType.TYPE_NULL);
        }

        mBinding.setDiveType(mDiveType);

        // Set the listeners
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mDiveType.getHasDataChanged()) {
                mDialogs.confirm(DiveTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

        mDiveType.setHasDataChanged(false);

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_dive_type_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mDiveType.getHasDataChanged()) {
                mDialogs.confirm(DiveTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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
        if (mDiveType.getHasDataChanged()) {
            mDialogs.confirm(DiveTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

        // Save DIVE_TYPE data
        Integer rc;
        if (mNewRecord) {
            // Add mode
            mAirDA.open();
            mAirDA.beginTransaction();
            try {
                rc = mAirDA.createDiveType(mDiveType);
                // End transaction with success
                mAirDA.setTransactionSuccessful();
            } finally {
                // No transaction left behind
                mAirDA.endTransaction();
            }
            mAirDA.close();
        }  else {
            // Edit mode
            mAirDA.open();
            mAirDA.beginTransaction();
            try {
                rc = mAirDA.updateDiveType(mDiveType);
                // End transaction with success
                mAirDA.setTransactionSuccessful();
            } finally {
                // No transaction left behind
                mAirDA.endTransaction();
            }
            mAirDA.close();
        }

        // Return the parcel object with the newly saved data
        if (rc.equals(MyConstants.ZERO_I)) {
            Intent intent = new Intent();
            intent.putExtra(MyConstants.DIVE_TYPE,mDiveType);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Resources res = getResources();
            String message = String.format(res.getString(R.string.msg_update_fk_constraint),res.getString(R.string.mn_dive_type),mDiveType.getDiveType());
            showUpdateResults(message);
        }
    }

    private boolean validateType() {
        // Required
        if (mBinding.editTextDT.getText().toString().trim().isEmpty()) {
            mBinding.editTextDT.setError(getString(R.string.msg_dive_type));
            requestFocus(mBinding.editTextDT);
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
                    requestFocus(mBinding.editTextDT);
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
