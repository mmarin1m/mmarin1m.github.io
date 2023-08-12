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

import ca.myairbuddyandi.databinding.UsageTypeActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all the logic for the UsageTypeActivity class
 */

public class UsageTypeActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "UsageTypeActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private Boolean mNewRecord = false;
    private final MyDialogs mDialogs = new MyDialogs();
    private UsageType mUsageType = new UsageType();
    private UsageTypeActivityBinding mBinding = null;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.usage_type_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mUsageType = getIntent().getParcelableExtra(MyConstants.USAGE_TYPE,UsageType.class);
        } else {
            mUsageType = getIntent().getParcelableExtra(MyConstants.USAGE_TYPE);
        }

        assert mUsageType != null;
        if (mUsageType.getUsageType().trim().isEmpty() || mUsageType.getUsageType() == null) {
            // Add mode (New Usage Type)
            // Data must be in the Model first in order to bind
            // All data initialization must be done to the Model and not the View to avoid Data Changed Event
            // No default values
            mNewRecord = true;
            mUsageType.setDescription("");
            mUsageType.setSystemDefined("N");
            mBinding.editTextUT.setSelectAllOnFocus(true);
        }  else {
            // Edit mode
            // Cannot update PK in Edit mode
            mBinding.editTextUT.setEnabled(false);
            mBinding.editTextUT.setInputType(InputType.TYPE_NULL);
        }

        mBinding.setUsageType(mUsageType);

        // Set the listeners
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mUsageType.getHasDataChanged()) {
                mDialogs.confirm(UsageTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

        mUsageType.setHasDataChanged(false);

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_usage_type_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mUsageType.getHasDataChanged()) {
                mDialogs.confirm(UsageTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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
        if (mUsageType.getHasDataChanged()) {
            mDialogs.confirm(UsageTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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
            mAirDa.open();
            mAirDa.beginTransaction();
            try {
                rc = mAirDa.createUsageType(mUsageType);
                // End transaction with success
                mAirDa.setTransactionSuccessful();
            } finally {
                // No transaction left behind
                mAirDa.endTransaction();
            }
            mAirDa.close();
        }  else {
            // Edit mode
            mAirDa.open();
            mAirDa.beginTransaction();
            try {
                rc = mAirDa.updateUsageType(mUsageType);
                // End transaction with success
                mAirDa.setTransactionSuccessful();
            } finally {
                // No transaction left behind
                mAirDa.endTransaction();
            }
            mAirDa.close();
        }

        // Return the parcel object with the newly saved data
        if (rc.equals(MyConstants.ZERO_I)) {
            Intent intent = new Intent();
            intent.putExtra(MyConstants.USAGE_TYPE,mUsageType);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Resources res = getResources();
            String message = String.format(res.getString(R.string.msg_update_fk_constraint),res.getString(R.string.mn_usage_type),mUsageType.getUsageType());
            showUpdateResults(message);
        }
    }

    private boolean validateType() {
        // Required
        if (mBinding.editTextUT.getText().toString().trim().isEmpty()) {
            mBinding.editTextUT.setError(getString(R.string.msg_usage_type));
            requestFocus(mBinding.editTextUT);
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
                    requestFocus(mBinding.editTextUT);
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
