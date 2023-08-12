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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.EmergencyActivityBinding;

/**
 * Created by Michel on 2017-01-03.
 * Holds all the logic for the EmergencyActivity class
 */

public class EmergencyActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "EmergencyActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    // No Segment Descent List are needed since the view is only for the Ascent portion
    private ArrayList<DiveSegment> mMyDiverSegmentAscentList = new ArrayList<>();
    private ArrayList<DiveSegment> mMyBuddyDiverSegmentAscentList = new ArrayList<>();
    private EmergencyActivityBinding mBinding = null;
    private DiveForGraphic mDiveForGraphic;
    private DivePick mDivePick;
    private final DiveSegmentDetail mMyDiverDiveSegmentDetail = new DiveSegmentDetail();
    private final DiveSegmentDetail mMyBuddyDiveSegmentDetail = new DiveSegmentDetail();
    private EmergencyManageDiveSegment mEmergencyManageDiveSegment;
    private String mShorten = MyConstants.NO;
    private String mBoth = MyConstants.NO;

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
        mEmergencyManageDiveSegment = new EmergencyManageDiveSegment(this);
        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        // Need to populate mDiveForGraphic in order to get the LogBookNo
        mEmergencyManageDiveSegment.getDiveForGraphicEmergency(mDivePick.getDiveNo(), mDiveForGraphic);

        if (mDiveForGraphic.getLogBookNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDiveForGraphic.getLogBookNo());
            }
        }

        if (   (mDiveForGraphic.getMyRatedPressure().equals(MyConstants.ZERO_D))
                && (mDiveForGraphic.getMyBuddyRatedPressure().equals(MyConstants.ZERO_D)) ) {
            // Display error
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_missing_emergency_gas),getResources().getString(R.string.msg_no_emergency_gas_alert));
        } else {
            // Do DataBinding
            mBinding = DataBindingUtil.setContentView(this, R.layout.emergency_activity);
            mBinding.setDiveForGraphic(mDiveForGraphic);

            // Showing the default view
            displayView(mShorten, mBoth);

            // Set the listeners
            mBinding.meLbl.setOnClickListener(view -> {
                // Showing the Dive plan for Me, if any
                mBinding.emergencyView.setDiveSegmentDetail(mMyDiverDiveSegmentDetail);
                mBinding.emergencyView.setDiveSegmentAscent(mMyDiverSegmentAscentList);
                enableMyBuddy();
                mBinding.emergencyView.invalidate();
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

                displayView(mShorten, mBoth);
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

                displayView(mShorten, mBoth);
            });

            mBinding.myBuddyLbl.setOnClickListener(view -> {
                // Showing the Dive plan for My Buddy, if any
                mBinding.emergencyView.setDiveSegmentDetail(mMyBuddyDiveSegmentDetail);
                mBinding.emergencyView.setDiveSegmentAscent(mMyBuddyDiverSegmentAscentList);
                enableMe();
                mBinding.emergencyView.invalidate();
            });
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
            intent.putExtra(getString(R.string.app_help_topic), getString(R.string.act_emergency));
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

    private void displayView (String shorten, String both) {

        mAirDa.open();

        // Get the Dive data - First pass, reads the current Dive Info but the previous Dive Segment summary
        mEmergencyManageDiveSegment.getDiveForGraphicEmergency(mDivePick.getDiveNo(), mDiveForGraphic);

        mEmergencyManageDiveSegment.generateDive(shorten, both, mDiveForGraphic);

        if (mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L) {
            mAirDa.getDiveSegmentDetail(mDiveForGraphic.getMyDiverNo(), mDivePick.getDiveNo(), mMyDiverDiveSegmentDetail);
            mMyDiverSegmentAscentList = mAirDa.getDiveSegmentsAscentEmergency(mDiveForGraphic.getMyDiverNo(), mDiveForGraphic.getDiveNo(), mShorten);
        }

        if (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            mAirDa.getDiveSegmentDetail(mDiveForGraphic.getMyBuddyDiverNo(), mDivePick.getDiveNo(), mMyBuddyDiveSegmentDetail);
            mMyBuddyDiverSegmentAscentList = mAirDa.getDiveSegmentsAscentEmergency(mDiveForGraphic.getMyBuddyDiverNo(), mDiveForGraphic.getDiveNo(), mShorten);
        }

        // Get the Dive data - Second pass, reads the current Dive Info and the current Dive Segment summary
        mAirDa.getDiveForGraphicEmergency(mDivePick.getDiveNo(), mDiveForGraphic);

        mAirDa.close();

        // Do DataBinding
        mBinding.setDiveForGraphic(mDiveForGraphic);

        // If the Ending pressures are identical, both stay Black
        // The lowest Ending pressure is set to Red
        if (mDiveForGraphic.getMyDiverNo() != 0 && mDiveForGraphic.getMyEndingPressure() < mDiveForGraphic.getMyBuddyEndingPressure()) {
            mBinding.meEndingLbl.setTextColor(ContextCompat.getColor(this, R.color.red));
        } else if (mDiveForGraphic.getMyBuddyDiverNo() != 0 && mDiveForGraphic.getMyBuddyEndingPressure() < mDiveForGraphic.getMyEndingPressure()) {
            mBinding.myBuddyEndingLbl.setTextColor(ContextCompat.getColor(this, R.color.red));
        }

        if (mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L && !mDiveForGraphic.getMyRatedPressure().equals(MyConstants.ZERO_D)) {
            mBinding.emergencyView.setDiveSegmentDetail(mMyDiverDiveSegmentDetail);
            mBinding.emergencyView.setDiveSegmentAscent(mMyDiverSegmentAscentList);
            enableMyBuddy();
        } else if (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L && !mDiveForGraphic.getMyBuddyRatedPressure().equals(MyConstants.ZERO_D)) {
            mBinding.emergencyView.setDiveSegmentDetail(mMyBuddyDiveSegmentDetail);
            mBinding.emergencyView.setDiveSegmentAscent(mMyBuddyDiverSegmentAscentList);
            enableMe();
        }

        if (mDiveForGraphic.getMyDiverNo() == MyConstants.ZERO_L || mDiveForGraphic.getMyEndingPressure().equals(MyConstants.ZERO_D)) {
            // Me is not present or it has no Emergency Gas (Pony)
            // Disable for good
            mBinding.meLbl.setEnabled(false);
            mBinding.meLbl.setTextColor(Color.BLACK);
        }

        if (mDiveForGraphic.getMyBuddyDiverNo() == MyConstants.ZERO_L || mDiveForGraphic.getMyBuddyEndingPressure().equals(MyConstants.ZERO_D)) {
            // MY Buddy is not present or it has no Emergency Gas (Pony)
            // Disable for good
            mBinding.myBuddyLbl.setEnabled(false);
            mBinding.myBuddyLbl.setTextColor(Color.BLACK);
        }

        mBinding.emergencyView.invalidate();

        if (       (mDiveForGraphic.getMyDiverNo() != MyConstants.ZERO_L && mDiveForGraphic.getMyEndingPressure() < 0)
                || (mDiveForGraphic.getMyBuddyDiverNo() != MyConstants.ZERO_L && mDiveForGraphic.getMyBuddyEndingPressure() < 0) ) {
            // Display warning
            showError(getResources().getString(R.string.msg_dive_alert));
            }
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

    private void showErrorAndFinish(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setIcon(R.drawable.ic_alert)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> {
                    //dialog.dismiss();
                    finish();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}