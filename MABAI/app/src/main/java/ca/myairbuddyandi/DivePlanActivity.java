package ca.myairbuddyandi;

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

import ca.myairbuddyandi.databinding.DivePlanActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the DivePlanActivity class
 */

public class DivePlanActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DivePlanActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private DivePlan mDivePlan = new DivePlan();
    private DivePlanActivityBinding mBinding = null;
    private MyCalc mMyCalc;
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.dive_plan_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mDivePlan = getIntent().getParcelableExtra(MyConstants.DIVE_PLAN,DivePlan.class);
        } else {
            mDivePlan = getIntent().getParcelableExtra(MyConstants.DIVE_PLAN);
        }

        assert mDivePlan != null;
        if (mDivePlan.getLogBookNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDivePlan.getLogBookNo());
            }
        }

        mBinding.setDivePlan(mDivePlan);

        mAirDa.open();

        if (mDivePlan.getDivePlanNo() == MyConstants.ZERO_L) {
            // Add mode (New DivePlan)
            // Data must be in the Model first in order to bind
            // All data initialization must be done to the Model and not the View to avoid Data Changed Event
            mDivePlan.setOrderNo(mAirDa.getDivePlanLastOrderNo(mDivePlan.getDiveNo()) + 10);
            mDivePlan.setDepth(MyConstants.ZERO_D);
            mDivePlan.setMinute(0);
        }  else {
            // Edit mode
            mAirDa.getDivePlan(mDivePlan);
        }

        // Set the listeners
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mDivePlan.getHasDataChanged()) {
                mDialogs.confirm(DivePlanActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

        mDivePlan.setHasDataChanged(false);

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_dive_plan_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mDivePlan.getHasDataChanged()) {
                mDialogs.confirm(DivePlanActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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
        // Action Bar Up button
        if (mDivePlan.getHasDataChanged()) {
            mDialogs.confirm(DivePlanActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

        if (!validateOrderNo()) {
            return;
        }

        if (!validateDepth()) {
            return;
        }

        if (!validateOrderNoAndDepth()) {
            return;
        }

        if (!validateMinute()) {
            return;
        }

        // Save DIVE_PLAN data
        if (mDivePlan.getDivePlanNo() == MyConstants.ZERO_L) {
            // Add mode
            mAirDa.createDivePlan(mDivePlan, false);

        }  else {
            // Edit mode
            mAirDa.updateDivePlan(mDivePlan);
        }

        // Return the parcel object with the newly saved data
        Intent intent = new Intent();
        intent.putExtra(MyConstants.DIVE_PLAN,mDivePlan);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateOrderNo() {
        // Required
        // Must be between 0 and 12000
        if (mBinding.editTextON.getText().toString().trim().isEmpty() || !isValidOrderNo(mDivePlan.getOrderNo())) {
            mBinding.editTextON.setError(getString(R.string.msg_order_no));
            requestFocus(mBinding.editTextON);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateDepth() {
        // Required
        // Must be between 1.0 and 328.0
        if (mBinding.editTextDE.getText().toString().trim().isEmpty() || !isValidDepth(mDivePlan.getDepth())) {
            String message = String.format(getResources().getString(R.string.msg_depth_plan),mMyCalc.getMaxDepthAllowed().toString(),mMyCalc.getDepthUnit());
            mBinding.editTextDE.setError(message);
            requestFocus(mBinding.editTextDE);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateOrderNoAndDepth() {
        // Required
        // Order No and Depth must be unique and sequential
        if ((mDivePlan.getDivePlanNo() == MyConstants.ZERO_L) && !isValidOrderNoAndDepth()) {
            mBinding.editTextDE.setError(getString(R.string.msg_order_no_depth));
            requestFocus(mBinding.editTextDE);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateMinute() {
        // Required
        // But must be between 1 and 300
        if (mBinding.editTextMI.getText().toString().trim().isEmpty() || !isValidMinute(mDivePlan.getMinute())) {
            mBinding.editTextMI.setError(getString(R.string.msg_minute));
            requestFocus(mBinding.editTextMI);
            return false;
        } else {
            return true;
        }
    }

    private static boolean isValidOrderNo(Long orderNo) {
        return (orderNo >= MyConstants.ZERO_I && orderNo <= MyConstants.MAX_ORDER_NO);
    }

    private boolean isValidDepth(Double depth) {
        // Check depth for both divers
        return (depth >= MyConstants.ONE_I && depth <= mMyCalc.getMaxDepthAllowed());
    }

    private boolean isValidOrderNoAndDepth() {
        // Check if the Order No and Depth are unique and sequential
        return mAirDa.isDivePlanSorted(mDivePlan.getDiveNo(),mDivePlan.getOrderNo(),mDivePlan.getDepth());
    }

    private static boolean isValidMinute(int minute) {
        return (minute >= MyConstants.MIN_MINUTE && minute <= MyConstants.MAX_MINUTE);
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
