package ca.myairbuddyandi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;

import ca.myairbuddyandi.databinding.RuleofthirdsActivityBinding;

/**
 * Created by Michel on 2017-01-03.
 * Holds all the logic for the RuleOfThirdsActivity class
 */

public class RuleOfThirdsActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "RuleOfThirdsActivity";

    // Public

    // Protected

    // Private
    private int mMyMinPressure;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<DiveSegment> mMyDiverSegmentDescentList = new ArrayList<>();
    private ArrayList<DiveSegment> mMyDiverSegmentAscentList = new ArrayList<>();
    private ArrayList<DiveSegment> mMyBuddyDiverSegmentDescentList = new ArrayList<>();
    private ArrayList<DiveSegment> mMyBuddyDiverSegmentAscentList = new ArrayList<>();
    private DiveForGraphic mDiveForGraphic;
    private DivePick mDivePick;
    private final DiveSegmentDetail mMyDiverDiveSegmentDetail = new DiveSegmentDetail();
    private final DiveSegmentDetail mMyBuddyDiveSegmentDetail = new DiveSegmentDetail();
    private RuleOfThirdsManageDiveSegment mRuleOfThirdsManageDiveSegment;
    private RuleofthirdsActivityBinding mBinding = null;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Get the data from the intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mDivePick = getIntent().getParcelableExtra(MyConstants.DIVE_PICK,DivePick.class);
        } else {
            mDivePick = getIntent().getParcelableExtra(MyConstants.DIVE_PICK);
        }

        mDiveForGraphic = new DiveForGraphic(this);
        mRuleOfThirdsManageDiveSegment = new RuleOfThirdsManageDiveSegment(this);
        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        // Need to populate mDiveForGraphic in order to get the LogBookNo
        mRuleOfThirdsManageDiveSegment.getDiveForGraphic(mDivePick.getDiveNo(), mDiveForGraphic);

        mBinding = DataBindingUtil.setContentView(this, R.layout.ruleofthirds_activity);

        if (mDiveForGraphic.getLogBookNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDiveForGraphic.getLogBookNo());
            }
        }

        // Showing the original Dive plan
        displayView(MyConstants.PLAN);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mMyMinPressure = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.MY_MIN_PRESSURE, mRuleOfThirdsManageDiveSegment.getMyMinPressureDefault()))));

        if ((mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L && mDiveForGraphic.getMyEndingPressure() < mMyMinPressure)
                || (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L && mDiveForGraphic.getMyBuddyEndingPressure() < mMyMinPressure)) {
            // Display warning
            showError(getResources().getString(R.string.msg_dive_alert));
        } else if (mDiveForGraphic.getMyEndingPressure() < ((mDiveForGraphic.getMyBeginningPressure() / 3) - (mDiveForGraphic.getMyBeginningPressure() / 3 * .01))
                || mDiveForGraphic.getMyBuddyEndingPressure() < ((mDiveForGraphic.getMyBuddyBeginningPressure() / 3) - (mDiveForGraphic.getMyBuddyBeginningPressure() / 3 * .01))) {
            // Display warning
            showError(getResources().getString(R.string.msg_rule_of_thirds_alert));
        }

        // Set the listeners
        mBinding.meLbl.setOnClickListener(view -> {
            // Showing the Dive plan for Me, if any
            mBinding.ruleOfThirdsView.setDiveSegmentDetail(mMyDiverDiveSegmentDetail);
            mBinding.ruleOfThirdsView.setDiveSegmentDescent(mMyDiverSegmentDescentList);
            mBinding.ruleOfThirdsView.setDiveSegmentAscent(mMyDiverSegmentAscentList);
            enableMyBuddy();
            mBinding.ruleOfThirdsView.invalidate();
        });

        mBinding.lblPlan.setOnClickListener(view -> {
            // Showing the original Dive plan
            displayView(MyConstants.PLAN);

            if ((mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L && mDiveForGraphic.getMyEndingPressure() < mMyMinPressure)
                    || (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L && mDiveForGraphic.getMyBuddyEndingPressure() < mMyMinPressure)) {
                // Display warning
                showError(getResources().getString(R.string.msg_dive_alert));
            }

            if (mDiveForGraphic.getMyEndingPressure() < ((mDiveForGraphic.getMyBeginningPressure() / 3) - (mDiveForGraphic.getMyBeginningPressure() / 3 * .01))
                    || mDiveForGraphic.getMyBuddyEndingPressure() < ((mDiveForGraphic.getMyBuddyBeginningPressure() / 3) - (mDiveForGraphic.getMyBuddyBeginningPressure() / 3 * .01))) {
                // Display warning
                showError(getResources().getString(R.string.msg_rule_of_thirds_alert));
            }
        });

        mBinding.maximize.setOnClickListener(view -> {
            // Maximizing the Dive plan
            displayView(MyConstants.PLUS);

            if ((mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L && mDiveForGraphic.getMyEndingPressure() < mMyMinPressure)
                    || (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L && mDiveForGraphic.getMyBuddyEndingPressure() < mMyMinPressure)) {
                // Display warning
                showError(getResources().getString(R.string.msg_dive_alert));
            }

            if (mDiveForGraphic.getMyEndingPressure() < ((mDiveForGraphic.getMyBeginningPressure() / 3) - (mDiveForGraphic.getMyBeginningPressure() / 3 * .01))
                    || mDiveForGraphic.getMyBuddyEndingPressure() < ((mDiveForGraphic.getMyBuddyBeginningPressure() / 3) - (mDiveForGraphic.getMyBuddyBeginningPressure() / 3 * .01))) {
                // Display warning
                showError(getResources().getString(R.string.msg_rule_of_thirds_alert));
            }
        });

        mBinding.myBuddyLbl.setOnClickListener(view -> {
            // Showing the Dive plan for My Buddy, if any
            mBinding.ruleOfThirdsView.setDiveSegmentDetail(mMyBuddyDiveSegmentDetail);
            mBinding.ruleOfThirdsView.setDiveSegmentDescent(mMyBuddyDiverSegmentDescentList);
            mBinding.ruleOfThirdsView.setDiveSegmentAscent(mMyBuddyDiverSegmentAscentList);
            enableMe();
            mBinding.ruleOfThirdsView.invalidate();
        });

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
            intent.putExtra(getString(R.string.app_help_topic), getString(R.string.act_rule_of_thirds));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        super.onBackPressed();
        finish();
    }

    // My functions

    public void displayView(String optimize) {
        mAirDa.open();

        mDiveForGraphic = new DiveForGraphic(this);

        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        mRuleOfThirdsManageDiveSegment.getDiveForGraphic(mDivePick.getDiveNo(), mDiveForGraphic);

        // Generate the DIVE_SEGMENT
        mRuleOfThirdsManageDiveSegment.generateDive(optimize, mDiveForGraphic);

        if (mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L) {
            mAirDa.getDiveSegmentDetail(mDiveForGraphic.getMyDiverNo(), mDivePick.getDiveNo(), mMyDiverDiveSegmentDetail);
            mMyDiverSegmentDescentList = mAirDa.getDiveSegmentsDescent(mDiveForGraphic.getMyDiverNo(), mDiveForGraphic.getDiveNo());
            mMyDiverSegmentAscentList = mAirDa.getDiveSegmentsAscent(mDiveForGraphic.getMyDiverNo(), mDiveForGraphic.getDiveNo());
        }

        if (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            mAirDa.getDiveSegmentDetail(mDiveForGraphic.getMyBuddyDiverNo(), mDivePick.getDiveNo(), mMyBuddyDiveSegmentDetail);
            mMyBuddyDiverSegmentDescentList = mAirDa.getDiveSegmentsDescent(mDiveForGraphic.getMyBuddyDiverNo(), mDiveForGraphic.getDiveNo());
            mMyBuddyDiverSegmentAscentList = mAirDa.getDiveSegmentsAscent(mDiveForGraphic.getMyBuddyDiverNo(), mDiveForGraphic.getDiveNo());
        }

        // Get the Dive data - Second pass, reads the current Dive Info and the current Dive Segment summary
        mAirDa.getDiveForGraphic(mDivePick.getDiveNo(), mDiveForGraphic);

        mAirDa.close();

        // Do DataBinding
        mBinding.setDiveForGraphic(mDiveForGraphic);

        // If the Turnaround pressures are identical, both stay Black
        // The lowest Turnaround pressure is set to Red
        if (mDiveForGraphic.getMyDiverNo() != 0 && mDiveForGraphic.getMyTurnaroundPressure() < mDiveForGraphic.getMyBuddyTurnaroundPressure()) {
            mBinding.meTurnaroundLbl.setTextColor(ContextCompat.getColor(this, R.color.red));
            mBinding.myBuddyTurnaroundLbl.setTextColor(ContextCompat.getColor(this, R.color.black));
        } else if (mDiveForGraphic.getMyBuddyDiverNo() != 0 && mDiveForGraphic.getMyBuddyTurnaroundPressure() < mDiveForGraphic.getMyTurnaroundPressure()) {
            mBinding.meTurnaroundLbl.setTextColor(ContextCompat.getColor(this, R.color.black));
            mBinding.myBuddyTurnaroundLbl.setTextColor(ContextCompat.getColor(this, R.color.red));
        }

        if (mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L) {
            mBinding.ruleOfThirdsView.setDiveSegmentDetail(mMyDiverDiveSegmentDetail);
            mBinding.ruleOfThirdsView.setDiveSegmentDescent(mMyDiverSegmentDescentList);
            mBinding.ruleOfThirdsView.setDiveSegmentAscent(mMyDiverSegmentAscentList);
            enableMyBuddy();
        } else {
            mBinding.ruleOfThirdsView.setDiveSegmentDetail(mMyBuddyDiveSegmentDetail);
            mBinding.ruleOfThirdsView.setDiveSegmentDescent(mMyBuddyDiverSegmentDescentList);
            mBinding.ruleOfThirdsView.setDiveSegmentAscent(mMyBuddyDiverSegmentAscentList);
            enableMe();
        }

        if (mDiveForGraphic.getMyDiverNo() == MyConstants.ZERO_L) {
            // Me is not present
            // Disable for good
            mBinding.meLbl.setEnabled(false);
        }

        if (mDiveForGraphic.getMyBuddyDiverNo() == MyConstants.ZERO_L) {
            // MY Buddy is not present
            // Disable for good
            mBinding.myBuddyLbl.setEnabled(false);
        }

        mBinding.ruleOfThirdsView.invalidate();
    }

    private void enableMe() {
        // The user click on My Buddy
        // Enable Me so the user can click on Me the next time
        if (mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L) {
            mBinding.meLbl.setText(mDiveForGraphic.getMeLabel());
            mBinding.meLbl.setEnabled(true);
            mBinding.meLbl.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
        }
        // Disable and underline My Buddy so the user cannot click it
        if (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            mBinding.myBuddyLbl.setEnabled(false);
            SpannableString content = new SpannableString(mDiveForGraphic.getMyBuddyFullName());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mBinding.myBuddyLbl.setText(content);
            mBinding.myBuddyLbl.setTextColor(Color.BLACK);
        }
    }

    private void enableMyBuddy() {
        // The user click on Me
        // Enable My Buddy so the user can click on My Buddy the next time
        if (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            mBinding.myBuddyLbl.setText(mDiveForGraphic.getMyBuddyFullName());
            mBinding.myBuddyLbl.setEnabled(true);
            mBinding.myBuddyLbl.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
        }
        // Disable and underline Me so the user cannot click it
        if (mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L) {
            mBinding.meLbl.setEnabled(false);
            SpannableString content = new SpannableString(mDiveForGraphic.getMeLabel());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mBinding.meLbl.setText(content);
            mBinding.meLbl.setTextColor(Color.BLACK);
        }
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.msg_alert))
                .setIcon(R.drawable.ic_stop)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}

