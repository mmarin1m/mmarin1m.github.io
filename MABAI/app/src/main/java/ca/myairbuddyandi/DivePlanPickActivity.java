package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Objects;

import ca.myairbuddyandi.databinding.DivePlanPickActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the DivePlanPickActivity class
 */

public class DivePlanPickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DivePlanPickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mDivePlansToDelete = 1;
    private int mPosition;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<DivePlan> mDivePlanPickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private DivePlan mDivePlan = new DivePlan();
    private DivePlanPickActivityBinding mBinding = null;
    private DivePlanPickAdapter mDivePlanPickAdapter;
    private String mOriginalTitle;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.dive_plan_pick_activity);

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

        //Get data for the DivePlans
        mAirDa.open();
        mDivePlanPickList = mAirDa.getAllDivePlanByDiveNo(mDivePlan.getDiveNo());

        // Create and load the data in the Recycler View Adapter
        if (mDivePlanPickAdapter == null) {
            mDivePlanPickAdapter = new DivePlanPickAdapter(this, mDivePlanPickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mDivePlanPickList.size() == MyConstants.ZERO_I) {
                mDivePlanPickAdapter.setDivePlan(mDivePlan);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mDivePlanPickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);

        // Set the listener for the FAB
        mBinding.fabDivePlan.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DivePlanActivity.class);
            DivePlan divePlan = new DivePlan();
            divePlan.setDivePlanNo(MyConstants.ZERO_L);
            divePlan.setDiveNo(mDivePlan.getDiveNo());
            divePlan.setLogBookNo(mDivePlan.getLogBookNo());
            divePlan.setDepth(MyConstants.ZERO_D);
            intent.putExtra(MyConstants.DIVE_PLAN, divePlan);
            addLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the DivePlan in the collection
        mPosition = mDivePlanPickList.indexOf(mDivePlan);

        if (mPosition == -1 && mDivePlanPickList.size() >= 1) {
            // Can't find the DivePlan
            // Select first row
            mPosition = 0;
        }

        if (mDivePlanPickAdapter.getDivePlanPickList().size() >= 1) {
            // There is at least one Dive Plan in the collection
            // Scroll to the Dive Plan
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mDivePlanPickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        final MenuItem deleteItem = menu.findItem(R.id.action_delete);
        // TODO: Implement Share
//        final MenuItem shareItem = menu.findItem(R.id.action_share);
        final MenuItem checkAll = menu.findItem(R.id.action_check_all);
        final MenuItem editItem = menu.findItem(R.id.action_edit);

        if (mDivePlan.getInMultiEditMode()) {
            deleteItem.setVisible(true);
            checkAll.setVisible(true);
            deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            checkAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            deleteItem.setVisible(false);
            checkAll.setVisible(false);
            deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            checkAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        editItem.setVisible(true);
        // TODO: Implement Share
//        shareItem.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_check_all) {
            item.setChecked(!item.isChecked());
            selectAll(item.isChecked());
        } else if (id == R.id.action_delete) {
            deleteMultiMode();
        } else if (id == R.id.action_edit) {
            // Same as onLongClick
            // Enter Single Edit Mode
            mDivePlan = mDivePlanPickAdapter.getDivePlan();
            Intent intent = new Intent(this, DivePlanActivity.class);
            intent.putExtra(MyConstants.DIVE_PLAN, mDivePlan);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_dive_plan_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mDivePlan.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabDivePlan.setVisibility(View.VISIBLE);
                    setVisibility(0,false);
                    return true;
                }
            } else {
                Intent intent = new Intent();
                // Get the DivePlan from the Adapter
                intent.putExtra(MyConstants.DIVE_PLAN, mDivePlan);
                setResult(RESULT_OK, intent);
                super.onBackPressed();
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        if (mDivePlan.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabDivePlan.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(MyConstants.DIVE_PLAN, mDivePlan);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
            finish();
        }
    }

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED DivePlan from the DivePlan activity
                    DivePlan divePlan;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        divePlan = data.getParcelableExtra(MyConstants.DIVE_PLAN,DivePlan.class);
                    } else {
                        divePlan = data.getParcelableExtra(MyConstants.DIVE_PLAN);
                    }
                    // The DivePlan has already been added to the Database
                    // Need to add it to the recyclerView
                    mDivePlanPickAdapter.addDivePlan(divePlan);
                    mDivePlan.setHasDataChanged(true);
                    mDivePlan = divePlan;
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED DivePlan from the DivePlan activity
                    DivePlan divePlan;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        divePlan = data.getParcelableExtra(MyConstants.DIVE_PLAN,DivePlan.class);
                    } else {
                        divePlan = data.getParcelableExtra(MyConstants.DIVE_PLAN);
                    }
                    // The DivePlan has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    mDivePlanPickAdapter.modifyDivePlan(divePlan);
                    mDivePlan.setHasDataChanged(true);
                    mDivePlan = divePlan;
                }
            });

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mDivePlan.setInMultiEditMode(mInMultiEditMode);
        if (mDivePlan.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add DivePlan floating button
            mBinding.fabDivePlan.setVisibility(View.INVISIBLE);
            // Showing Checkboxes on all DivePlan items
            for (int i = 0; i < mDivePlanPickList.size(); i++) {
                DivePlan divePlan = mDivePlanPickList.get(i);
                divePlan.setVisible(true);
                if (position - HEADER_OFFSET == i) {
                    divePlan.setChecked(true);
                }
            }
            mDivePlanPickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mDivePlansToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add DivePlan floating button
            mBinding.fabDivePlan.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all DivePlan items
            for (int i = 0; i < mDivePlanPickList.size(); i++) {
                DivePlan divePlan = mDivePlanPickList.get(i);
                divePlan.setVisible(false);
                divePlan.setChecked(false);
            }
            mDivePlanPickAdapter.notifyDataSetChanged();
            mDivePlansToDelete = 1;
            mDivePlanPickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mDivePlansToDelete = 0;
        } else {
            mDivePlansToDelete = mDivePlanPickList.size();
        }
        for (int i = 0; i < mDivePlanPickList.size(); i++) {
            DivePlan mDivePlan = mDivePlanPickList.get(i);
            mDivePlan.setChecked(checked);
            if (checked) {
                mDivePlansToDelete++;
            } else {
                mDivePlansToDelete--;
            }
        }
        mDivePlanPickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mDivePlansToDelete);
        if (getSupportActionBar() != null) {
            if (mDivePlansToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countDivePlans(Boolean checked) {
        if (checked) {
            mDivePlansToDelete++;
        } else {
            mDivePlansToDelete--;
        }
        mAppTitleCount = String.valueOf(mDivePlansToDelete);
        if (getSupportActionBar() != null) {
            if (mDivePlansToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mDivePlansToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mDivePlansToDelete + " " + getString(R.string.msg_dive_plan_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mDivePlanPickList.size() - 1;
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            DivePlan mDivePlan = mDivePlanPickList.get(position);
                            if (mDivePlan.getChecked()) {
                                mDivePlan = mDivePlanPickAdapter.getDivePlan(position);
                                mDivePlanPickAdapter.deleteDivePlan(position);
                                mAirDa.deleteDivePlanByDiveNoOrderNo(mDivePlan.getDiveNo(), mDivePlan.getOrderNo());
                                mDivePlansToDelete--;
                                mDivePlan.setHasDataChanged(true);
                            }
                        }
                        mDivePlan.setHasDataChanged(true);
                        if (mDivePlanPickList.size() > 0) {
                            // Set the current position in the Adapter
                            mDivePlanPickAdapter.setSelectedPosition(0);
                            // Scroll to the DivePlan, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mDivePlanPickAdapter.notifyDataSetChanged();

                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(mOriginalTitle);
                            mBinding.fabDivePlan.setVisibility(View.VISIBLE);
                            setVisibility(0,false);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, null)
                    .show();
        }
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added DivePlan
        // The screen does not scroll if the newly added DivePlan is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }
}