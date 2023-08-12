package ca.myairbuddyandi;

import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import java.util.Date;
import java.util.Locale;

import ca.myairbuddyandi.databinding.DiverActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the DiverActivity class
 */

public class DiverActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DiverActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private Diver mDiver = new Diver();
    private DiverActivityBinding mBinding = null;
    private MyCalc mMyCalc;
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.diver_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mDiver = getIntent().getParcelableExtra(MyConstants.DIVER, Diver.class);
        } else {
            mDiver = getIntent().getParcelableExtra(MyConstants.DIVER);
        }
        assert mDiver != null;
        mDiver.setContext(this);

        if (mDiver.getLogBookNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDiver.getLogBookNo());
            }
        }

        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(this);
        } else {
            mMyCalc = new MyCalcMetric(this);
        }

        mAirDa.open();

        if (mDiver.getDiverNo().equals(MyConstants.ZERO_L)) {
            // Add mode (New Diver)
            // Data must be in the Model first in order to bind
            // Reset First and Last names
            // They were set to "No Buddy"
            mDiver.setFirstName("");
            mDiver.setLastName("");
            mDiver.setGender(true);
            mDiver.setBirthDate(MyFunctions.getBirthDate());
            mDiver.setMaxDepthAllowed(mMyCalc.getDefaultMaxDepthAllowed());
            mDiver.setMaxDepthAllowedOld(mMyCalc.getDefaultMaxDepthAllowed());
        }  else {
            // Edit mode
            mAirDa.getDiver(mDiver.getDiverNo(),mDiver);
        }

        mDiver.setHasDataChanged(false);

//        DEBUG: Used to discover metrics on a given device
//        MyFunctions.getMetrics(this);

        Log.d(LOG_TAG, "onCreate done");
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // NOTE: Leave as is
        switch (requestCode) {
            case MyConstants.REQ_CODE_GOOGLE_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    mDiver.setEmail(accountName);
                    mBinding.editTextE.setText(accountName);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.setDiver(mDiver);

        if (mDiver.getDiverNo().equals(MyConstants.ONE_L) && mDiver.getEmail().trim().isEmpty()) {
            // Get the email address of Me
            getEmail();
        }

        if (mDiver.getBirthDate() == null) {
            // Always propose a birth date if it is null
            mDiver.setBirthDate(MyFunctions.getBirthDate());
        }

        if (mDiver.getMaxDepthAllowed().equals(MyConstants.ZERO_D)) {
            // Set the Maximum Depth Allowed to 60 feet
            mDiver.setMaxDepthAllowed(mMyCalc.getDefaultMaxDepthAllowed());
        }

        mBinding.cancelButton.setOnClickListener(view -> {
            if (mDiver.getDiverNo().equals(MyConstants.ONE_L) && validateMinimumFailed()) {
                showError(getString(R.string.msg_must_complete_minimum));
            } else if (mDiver.getHasDataChanged()) {
                mDialogs.confirm(DiverActivity.this, getString(R.string.dlg_confirm_cancel), getString(R.string.dlg_cancel), getString(R.string.dlg_positive), getString(R.string.dlg_negative), yesProc(), noProc());
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

        mBinding.editTextB.setOnClickListener(view -> {
            Date dt = mDiver.getBirthDate();
            if (dt != null) {
                new DatePickerDialog(DiverActivity.this, onDateSetListener, MyFunctions.getYear(dt), MyFunctions.getMonthOfYear(dt), MyFunctions.getDayOfMonth(dt)).show();
            }
        });

        mBinding.editTextP.addTextChangedListener(new PhoneNumberFormattingTextWatcher(Locale.getDefault().getCountry()));

        mBinding.editTextLN.setSelectAllOnFocus(true);
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
        // automatically handle clicks on the H/Up button, so long
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_diver_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (validateMinimumFailed()) {
                showError(getString(R.string.msg_must_complete_minimum));
                return true;
            } else if (mDiver.getHasDataChanged()) {
                mDialogs.confirm(DiverActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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
        if (validateMinimumFailed()) {
            showError(getString(R.string.msg_must_complete_minimum));
        } else if (mDiver.getHasDataChanged()) {
            mDialogs.confirm(DiverActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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

    DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, day) -> {
        view.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        mBinding.editTextB.setText(MyFunctions.formatDateString(getApplicationContext(), year,month ,day));
    };

    private void submitForm() {
        if (validateLastName()) {
            return;
        }

        // Middle Name not required
        // Must be alphabetic and Uppercase
        // All controlled via the editText

        if (validateFirstName()) {
            return;
        }

        if (!validatePhone()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (validateMaxDepthAllowed()) {
            return;
        }

        // Save DIVER data
        if (mDiver.getDiverNo().equals(MyConstants.ZERO_L)) {
            // Add mode
            mAirDa.createDiver(mDiver, false);
        }  else {
            // Edit mode
            mAirDa.updateDiver(mDiver);
        }

        // The close happens in either:
        // - MainActivity.onActivityResult()
        // - DiveActivity.submitForm()

        // Return the parcel object with the newly saved data
        Intent intent = new Intent();
        intent.putExtra(MyConstants.DIVER,mDiver);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateMinimumFailed() {
       return ((mDiver.getLastNameOld().equals(" ") && mDiver.getFirstNameOld().equals(" ")) || mDiver.getMaxDepthAllowedOld().equals(MyConstants.ZERO_D));
    }

    private boolean validateLastName() {
        // Conditionally required
        if (mBinding.editTextLN.getText().toString().trim().isEmpty() && mBinding.editTextFN.getText().toString().trim().isEmpty()) {
            mBinding.editTextLN.setError(getString(R.string.msg_last_first_name));
            requestFocus(mBinding.editTextLN);
            return true;
        } else {
            return false;
        }
    }

    private boolean validateFirstName() {
        // Conditionally required
        if (mBinding.editTextFN.getText().toString().trim().isEmpty() && mBinding.editTextLN.getText().toString().trim().isEmpty()) {
            mBinding.editTextFN.setError(getString(R.string.msg_last_first_name));
            requestFocus(mBinding.editTextFN);
            return true;
        } else {
            return false;
        }
    }

    private boolean validatePhone() {
        // Not required
        if (!mBinding.editTextP.getText().toString().trim().isEmpty() && !isValidPhone(mDiver.getPhone())) {
            mBinding.editTextP.setError(getString(R.string.msg_phone));
            requestFocus(mBinding.editTextP);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail() {
        // Not required
        if (!mBinding.editTextE.getText().toString().trim().isEmpty() && !isValidEmail(mDiver.getEmail())) {
            mBinding.editTextE.setError(getString(R.string.msg_email));
            requestFocus(mBinding.editTextE);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateMaxDepthAllowed() {
        // Required
        if (!isValidMaxDepthAllowed(mDiver.getMaxDepthAllowed())) {
            String message = String.format(getResources().getString(R.string.msg_max_depth_allowed), mMyCalc.getMaxDepthAllowed().toString(),mMyCalc.getDepthUnit());
            mBinding.editTextMDA.setError(message);
            requestFocus(mBinding.editTextMDA);
            return true;
        } else {
            return false;
        }
    }

    private static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMaxDepthAllowed(Double maxDepthAllowed) {
        return (maxDepthAllowed > MyConstants.ZERO_I && maxDepthAllowed <= mMyCalc.getMaxDepthAllowed());
    }

    private void requestFocus(View view) {
        if (view instanceof EditText) {
            // Only works for EditText
            view.clearFocus();
            view.requestFocus();
            ((EditText) view).selectAll();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            view.clearFocus();
            view.requestFocus();
        }
    }

    @SuppressWarnings("deprecation")
    private void getEmail() {
        // Display the Account Picker, only once
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean accountShowed = preferences.getBoolean(getString(R.string.code_account_showed), false);
        if(!accountShowed) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(getString(R.string.code_account_showed), Boolean.TRUE);
            edit.apply();
            Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    false, null, null, null, null);
            startActivityForResult(intent, MyConstants.REQ_CODE_GOOGLE_ACCOUNT);
        }
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
