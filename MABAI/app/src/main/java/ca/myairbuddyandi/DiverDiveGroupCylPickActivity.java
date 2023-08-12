package ca.myairbuddyandi;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import java.util.Objects;

import ca.myairbuddyandi.databinding.DiverDiveGroupCylPickActivityBinding;

/**
 * Created by Michel on 2020-03/15.4.
 * Holds all of the logic for the DiverDiveGroupCylPickActivity class
 */

public class DiverDiveGroupCylPickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DiverDiveGroupCylPickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mPosition;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<DiverDiveGroupCyl> mDiverDiveGroupCylPickList = new ArrayList<>();
    private DiverDiveGroupCyl mDiverDiveGroupCyl = new DiverDiveGroupCyl();
    private DiverDiveGroupCylPickActivityBinding mBinding = null;
    private DiverDiveGroupCylPickAdapter mDiverDiveGroupCylPickAdapter;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.diver_dive_group_cyl_pick_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mDiverDiveGroupCyl = getIntent().getParcelableExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER,DiverDiveGroupCyl.class);
        } else {
            mDiverDiveGroupCyl = getIntent().getParcelableExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER);
        }

        assert mDiverDiveGroupCyl != null;
        if (mDiverDiveGroupCyl.getLogBookNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDiverDiveGroupCyl.getLogBookNo());
            }
        }

        //Get data for the DiverDiveGroupCyls
        mAirDa.open();
        mDiverDiveGroupCylPickList = mAirDa.getAllDiverDiveGroupCylinder(mDiverDiveGroupCyl);

        // Create and load the data in the Recycler View Adapter
        if (mDiverDiveGroupCylPickAdapter == null) {
            mDiverDiveGroupCylPickAdapter = new DiverDiveGroupCylPickAdapter(this, mDiverDiveGroupCylPickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mDiverDiveGroupCylPickList.size() == MyConstants.ZERO_I) {
                mDiverDiveGroupCylPickAdapter.setDiverDiveGroupCyl(mDiverDiveGroupCyl);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mDiverDiveGroupCylPickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the DiverDiveGroupCyl in the collection
        mPosition = mDiverDiveGroupCylPickList.indexOf(mDiverDiveGroupCyl);

        if (mPosition == -1 && mDiverDiveGroupCylPickList.size() >= 1) {
            // Can't find the DiverDiveGroupCyl
            // Select first row
            mPosition = 0;
        }

        if (mDiverDiveGroupCylPickAdapter.getDiverDiveGroupCylPickList().size() >= 1) {
            // There is at least one DiverDiveGroupCyl in the collection
            // Scroll to the DiverDiveGroupCyl
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mDiverDiveGroupCylPickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
            // Wait and perform a Click
            mBinding.recycler.postDelayed(() -> {
                try {
                    Objects.requireNonNull(mBinding.recycler.findViewHolderForAdapterPosition(mPosition + HEADER_OFFSET)).itemView.performClick();
                } catch (Exception e) {
                    // Do nothing
                }
            }, MyConstants.DELAY_MILLI_SECONDS);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        // TODO: Implement Share
        final MenuItem editItem = menu.findItem(R.id.action_edit);

        editItem.setVisible(true);
        // TODO: Implement Share

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            // Same as onLongClick
            // Enter Single Edit Mode
            mDiverDiveGroupCyl = mDiverDiveGroupCylPickAdapter.getDiverDiveGroupCyl();
            Intent intent = new Intent(this, DiverDiveGroupCylActivity.class);
            intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, mDiverDiveGroupCyl);
            editLauncher.launch(intent);
        } else if (id == R.id.action_contact_us) {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_consumption_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            Intent intent = new Intent();
            // Get the DiverDiveGroupCyl from the Adapter
            intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, mDiverDiveGroupCyl);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        Intent intent = new Intent();
        intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, mDiverDiveGroupCyl);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
        finish();
    }

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED DiverDiveGroupCyl from the DiverDiveGroupCyl activity
                    DiverDiveGroupCyl diverDiveGroupCyl;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        diverDiveGroupCyl = data.getParcelableExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER,DiverDiveGroupCyl.class);
                    } else {
                        diverDiveGroupCyl = data.getParcelableExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER);
                    }
                    // The DiverDiveGroupCyl has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    mDiverDiveGroupCylPickAdapter.modifyDiverDiveGroupCyl(diverDiveGroupCyl);
                    mDiverDiveGroupCyl.setHasDataChanged(true);
                    mDiverDiveGroupCyl = diverDiveGroupCyl;
                }
            });

    // Adapter functions

    public void doSmoothScroll(int position) {
        // Scroll to the newly added DiverDiveGroupCyl
        // The screen does not scroll if the newly added DiverDiveGroupCyl is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }
}