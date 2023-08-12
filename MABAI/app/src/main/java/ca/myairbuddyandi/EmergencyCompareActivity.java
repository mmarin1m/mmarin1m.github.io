package ca.myairbuddyandi;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;

import ca.myairbuddyandi.databinding.EmergencyCompareActivityBinding;

/**
 * Created by Michel on 2021-01-28.
 * Holds all the logic for the EmergencyCompareActivity class
 */

public class EmergencyCompareActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "EmergencyActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private DivesForCompare mDivesForCompare;
    private final DiveSegmentDetail mMyDiverDiveSegmentDetail = new DiveSegmentDetail();
    private final DiveSegmentDetail mMyBuddyDiveSegmentDetail = new DiveSegmentDetail();
    private EmergencyCompareActivityBinding mBinding = null;
    private EmergencyManageDiveSegment mEmergencyManageDiveSegment;
    private String mShorten = MyConstants.NO;
    private String mBoth = MyConstants.NO;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Get the data from the intent
        DivesSelected divesSelected;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            divesSelected = getIntent().getParcelableExtra(MyConstants.DIVES_SELECTED,DivesSelected.class);
        } else {
            divesSelected = getIntent().getParcelableExtra(MyConstants.DIVES_SELECTED);
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.emergency_compare_activity);

        mDivesForCompare = new DivesForCompare();
        mEmergencyManageDiveSegment = new EmergencyManageDiveSegment(this);

        // Default to Me
        mDivesForCompare.setMeMyBuddy1(MyConstants.ONE_L);
        mDivesForCompare.setMeMyBuddy2(MyConstants.ONE_L);
        mDivesForCompare.setMeMyBuddy3(MyConstants.ONE_L);
        // Set the different dives selected for the comparison
        assert divesSelected != null;
        mDivesForCompare.setDiveNo1(divesSelected.getDiveNo1());
        mDivesForCompare.setDiveNo2(divesSelected.getDiveNo2());
        mDivesForCompare.setDiveNo3(divesSelected.getDiveNo3());

        // Showing the default view
        displayDives(mShorten, mBoth);

        // Enable the links for the first display
        if (!mDivesForCompare.getMeLabel().equals(this.getResources().getString(R.string.sql_no_me)) && !mBinding.myBuddyLbl.isEnabled()) {
            enableMyBuddy();
        } else if (!mDivesForCompare.getMeLabel().equals(this.getResources().getString(R.string.sql_no_me)) && mBinding.myBuddyLbl.isEnabled()) {
            enableMe();
        }

        // Set the listeners
        mBinding.meLbl.setOnClickListener(view -> {
            // Showing the Dive plan for Me, if any
            mDivesForCompare.setMeMyBuddy1(MyConstants.ONE_L);
            mDivesForCompare.setMeMyBuddy2(MyConstants.ONE_L);
            mDivesForCompare.setMeMyBuddy3(MyConstants.ONE_L);
            enableMyBuddy();
            displayDives(mShorten, mBoth);
        });

        mBinding.lblShorten.setOnClickListener(view -> {
            if (mShorten.equals(MyConstants.YES)) {
                // Add the Deep Stop and Safety Stop back
                mShorten = MyConstants.NO;
                mBinding.lblShorten.setText(getString(R.string.lbl_shorten));
            } else {
                // Remove the Deep Stop and Safety Stop
                mShorten = MyConstants.YES;
                mBinding.lblShorten.setText(getString(R.string.lbl_complete));
            }

            displayDives(mShorten, mBoth);
        });

        mBinding.lblBoth.setOnClickListener(view -> {
            if (mBoth.equals(MyConstants.YES)) {
                // Calculate for single diver
                mBoth = MyConstants.NO;
                mBinding.lblBoth.setText(getString(R.string.lbl_both));
            } else {
                // Calculate for both divers
                mBoth = MyConstants.YES;
                mBinding.lblBoth.setText(getString(R.string.lbl_single));
            }

            displayDives(mShorten, mBoth);
        });

        mBinding.myBuddyLbl.setOnClickListener(view -> {
            // Showing the Dive plan for My Buddy, if any
            mDivesForCompare.setMeMyBuddy1(mDivesForCompare.getMyBuddyDiverNo1());
            mDivesForCompare.setMeMyBuddy2(mDivesForCompare.getMyBuddyDiverNo2());
            mDivesForCompare.setMeMyBuddy3(mDivesForCompare.getMyBuddyDiverNo3());
            enableMe();
            displayDives(mShorten, mBoth);
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
            intent.putExtra(getString(R.string.app_help_topic), getString(R.string.act_compare_emergency_dives));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
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

    public void displayDives(String shorten, String both) {
        mAirDa.open();

        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        mEmergencyManageDiveSegment.getDivesForCompareEmergency(mDivesForCompare);

        // Generate the DIVE_SEGMENT
        mEmergencyManageDiveSegment.generateDive(shorten, both, mDivesForCompare, this);

        if (mDivesForCompare.getMyDiverNo1() != MyConstants.ZERO_L) {
            mAirDa.getDiveSegmentDetail(mDivesForCompare.getMyDiverNo1(), mDivesForCompare.getDiveNo1(), mMyDiverDiveSegmentDetail);
        }

        if (mDivesForCompare.getMyBuddyDiverNo1() != MyConstants.ZERO_L) {
            mAirDa.getDiveSegmentDetail(mDivesForCompare.getMyBuddyDiverNo1(), mDivesForCompare.getDiveNo1(), mMyBuddyDiveSegmentDetail);
        }

        // Get the Dive data - Second pass, reads the current Dive Info and the current Dive Segment summary
        mEmergencyManageDiveSegment.getDivesForCompareEmergency(mDivesForCompare);

        mAirDa.close();

        // Do DataBinding
        mBinding.setDivesForCompare(mDivesForCompare);

        if (       mDivesForCompare.getMyDiverNo1() == MyConstants.ZERO_L
                && mDivesForCompare.getMyDiverNo2() == MyConstants.ZERO_L
                && mDivesForCompare.getMyDiverNo3() == MyConstants.ZERO_L) {
            // Me is not present
            // Disable for good
            mBinding.meLbl.setEnabled(false);
        }

        if (       mDivesForCompare.getMyBuddyDiverNo1() == MyConstants.ZERO_L
                && mDivesForCompare.getMyBuddyDiverNo2() == MyConstants.ZERO_L
                && mDivesForCompare.getMyBuddyDiverNo3() == MyConstants.ZERO_L) {
            // MY Buddy is not present
            // Disable for good
            mBinding.myBuddyLbl.setEnabled(false);
        }
    }

    private void enableMe() {
        // Enable Me so the user can click on Me the next time
        if (!mDivesForCompare.getMeLabel().isEmpty()) {
            mBinding.meLbl.setText(mDivesForCompare.getMeLabel());
            mBinding.meLbl.setEnabled(true);
            mBinding.meLbl.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
        }
        // Disable and underline My Buddy so the user cannot click it
        if (!mDivesForCompare.getMyBuddy().isEmpty()) {
            mBinding.myBuddyLbl.setEnabled(false);
            SpannableString content = new SpannableString(mDivesForCompare.getMyBuddy());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mBinding.myBuddyLbl.setText(content);
            mBinding.myBuddyLbl.setTextColor(Color.BLACK);
        }
    }

    private void enableMyBuddy() {
        // Enable My Buddy so the user can click on My Buddy the next time
        if (!mDivesForCompare.getMyBuddy().isEmpty()) {
            mBinding.myBuddyLbl.setText(mDivesForCompare.getMyBuddy());
            mBinding.myBuddyLbl.setEnabled(true);
            mBinding.myBuddyLbl.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
        }
        // Disable and underline Me so the user cannot click it
        if (!mDivesForCompare.getMeLabel().isEmpty()) {
            mBinding.meLbl.setEnabled(false);
            SpannableString content = new SpannableString(mDivesForCompare.getMeLabel());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mBinding.meLbl.setText(content);
            mBinding.meLbl.setTextColor(Color.BLACK);
        }
    }
}