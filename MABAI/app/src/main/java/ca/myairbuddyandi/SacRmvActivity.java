package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.RmvActivityBinding;

/**
 * Created by Michel on 2016-12-12.
 * Holds all of the logic for the SacRmvActivity class
 */

public class SacRmvActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "SacRmvActivity";

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<SacRmv> mSacRmvList = new ArrayList<>();
    private Diver mDiver = new Diver();
    private RmvActivityBinding mBinding = null;
    private SacRmvAdapter mSacRmvAdapter;
    private State mState = new State();

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mDiver.setContext(this);

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.rmv_activity);

        mAirDa.open();

        mAirDa.getState(mState);

        mAirDa.close();

        // Create and load the data in the Recycler View Adapter
        if (mSacRmvAdapter == null) {
            mSacRmvAdapter = new SacRmvAdapter(this, mSacRmvList);
            mSacRmvAdapter.setState(mState);
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mSacRmvAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listeners
        mBinding.myBuddy.setOnClickListener(view -> {
            // The Diver activities will be using this mAirDa transaction
            SacRmv sacRmv = mSacRmvAdapter.getSacRmv();
            mState.setDiveType(sacRmv.getDiveType());
            mAirDa.openWithFKConstraintsEnabled();
            mAirDa.beginTransaction();
            Intent intent = new Intent(getApplicationContext(), DiverPickActivity.class);
            intent.putExtra(MyConstants.DIVER, mDiver);
            diverPickBuddyLauncher.launch(intent);
        });

        mBinding.fabDive.setOnClickListener(view -> {
            saveState();
            Intent intent = new Intent(getApplicationContext(), DivePickActivity.class);
            intent.putExtra(MyConstants.STATE, mState);
            DivePick divePick = new DivePick();
            divePick.setContext(getApplicationContext());
            intent.putExtra(MyConstants.DIVE_PICK, divePick);
            divePickLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

        mAirDa.open();

        // Get data for the Diver
        mAirDa.getDiver(mState.getBuddyDiverNo(), mDiver);

        //Get data for SAC and RMV
        mSacRmvList = mAirDa.getAllSacRmv(mState.getDiveType(), String.valueOf(mState.getBuddyDiverNo()));

        mAirDa.close();

        // Create and load the data in the Recycler View Adapter
        mBinding.setDiver(mDiver);
        if (mSacRmvAdapter == null) {
            mSacRmvAdapter = new SacRmvAdapter(this,mSacRmvList);
            mBinding.recycler.setAdapter(mSacRmvAdapter);
        } else {
            mSacRmvAdapter.setSacRmvList(mSacRmvList);
            mSacRmvAdapter.notifyDataSetChanged();
        }

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_sac_and_rmv));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home)
        {
            saveState();
            Intent intent = new Intent();
            intent.putExtra(MyConstants.STATE, mState);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        saveState();
        Intent intent = new Intent();
        intent.putExtra(MyConstants.STATE, mState);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
        finish();
    }

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> diverPickBuddyLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    mAirDa.setTransactionSuccessful();
                    mAirDa.endTransaction();
                    mAirDa.close();
                    // Get the data from Pick a Diver
                    Intent data = result.getData();
                    assert data != null;
                    mDiver = data.getParcelableExtra(MyConstants.PICK_A_DIVER);
                    assert mDiver != null;
                    mDiver.setContext(getApplicationContext());
                    mState.setBuddyDiverNo(mDiver.getDiverNo());
                } else {
                    mAirDa.setTransactionSuccessful();
                    mAirDa.endTransaction();
                    mAirDa.close();
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> divePickLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    mState = data.getParcelableExtra(MyConstants.STATE);
                }
            });

    private void saveState() {
        // Save the latest from the Adapter
        if (mSacRmvAdapter.getSacRmv().getDiveType() != null ) {
            SacRmv sacRmv = mSacRmvAdapter.getSacRmv();
            mState.setDiveType(sacRmv.getDiveType());
            mState.setMyRmv(sacRmv.getMyRmv());
            // 04/20/2021 Added
            mState.setMySac(sacRmv.getMySac());
            mState.setMyBuddyRmv(sacRmv.getMyBuddyRmv());
            // 04/20/2021 Added
            mState.setMyBuddySac(sacRmv.getMyBuddySac());

            mAirDa.open();
            mAirDa.beginTransaction();
            try {
                mAirDa.updateState(mState);
                // End transaction with success
                // Including the Diver updates
                mAirDa.setTransactionSuccessful();
            } finally {
                // No transaction left behind
                mAirDa.endTransaction();
            }
            mAirDa.close();
        }
    }
}