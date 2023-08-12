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

import ca.myairbuddyandi.databinding.GrouppTypeActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all the logic fo the GrouppTypeActivity class
 */

public class GrouppTypeActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "GrouppTypeActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private Boolean mNewRecord = false;
    private GrouppTypeActivityBinding mBinding = null;
    private GrouppType mGrouppType = new GrouppType();
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.groupp_type_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mGrouppType = getIntent().getParcelableExtra(MyConstants.GROUPP_TYPE,GrouppType.class);
        } else {
            mGrouppType = getIntent().getParcelableExtra(MyConstants.GROUPP_TYPE);
        }

        assert mGrouppType != null;
        if (mGrouppType.getGroupType().trim().isEmpty() || mGrouppType.getGroupType() == null) {
            // Add mode (New Groupp Type)
            // Data must be in the Model first in order to bind
            // All data initialization must be done to the Model and not the View to avoid Data Changed Event
            mGrouppType.setDescription("");
            mGrouppType.setSystemDefined("N");
            mNewRecord = true;
            mBinding.editTextGT.setSelectAllOnFocus(true);
        }  else {
            // Edit mode
            // Cannot update PK in Edit mode
            // Must do a Delete first and then an Add
            mBinding.editTextGT.setEnabled(false);
            mBinding.editTextGT.setInputType(InputType.TYPE_NULL);
        }

        mBinding.setGrouppType(mGrouppType);

        // Set the listeners
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mGrouppType.getHasDataChanged()) {
                mDialogs.confirm(GrouppTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

        mGrouppType.setHasDataChanged(false);

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_groupp_type_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mGrouppType.getHasDataChanged()) {
                mDialogs.confirm(GrouppTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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
        if (mGrouppType.getHasDataChanged()) {
            mDialogs.confirm(GrouppTypeActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

        // Save GROUP_TYPE data
        Integer rc;
        // NOTE: Leave as is
        if (mNewRecord) {
            // Add mode
            mAirDa.openWithFKConstraintsEnabled();
            mAirDa.beginTransaction();
            try {
                rc = mAirDa.createGroupType(mGrouppType);
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
                rc = mAirDa.updateGroupType(mGrouppType);
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
            intent.putExtra(MyConstants.GROUPP_TYPE, mGrouppType);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Resources res = getResources();
            String message = String.format(res.getString(R.string.msg_update_fk_constraint),res.getString(R.string.mn_groupp_type),mGrouppType.getGroupType());
            showUpdateResults(message);
        }
    }

    private boolean validateType() {
        // Required
        if (mBinding.editTextGT.getText().toString().trim().isEmpty()) {
            mBinding.editTextGT.setError(getString(R.string.msg_groupp_type));
            requestFocus(mBinding.editTextGT);
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
                    requestFocus(mBinding.editTextGT);
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
