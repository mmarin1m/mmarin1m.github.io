package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import ca.myairbuddyandi.databinding.DivePickActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the DivePickActivity class
 */

public class DivePickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DivePickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mDivesToDelete = 1;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<DivePick> mDivePickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private DivePick mDivePick = new DivePick();
    private DivePickActivityBinding mBinding = null;
    private DivePickAdapter mDivePickAdapter;
    private State mState = null;
    private String mOriginalTitle;

    // End of variables

    @SuppressLint("NotifyDataSetChanged")
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.dive_pick_activity);

        if (savedInstanceState != null) {
            // 2nd time in
            // Get the data from the Saved Instance State
            mState = savedInstanceState.getParcelable(MyConstants.STATE);
            mDivePick = savedInstanceState.getParcelable(MyConstants.DIVE_PICK);

            assert mDivePick != null;
            mDivePick.setContext(this);

            // Get data for the Dives
            // Get all the dives from the saved state/Already read from the DB on the first time
            mDivePickList = (ArrayList<DivePick>) savedInstanceState.getSerializable("RECYCLER_DATA");

            Parcelable mRecyclerState = savedInstanceState.getParcelable(MyConstants.LIST_STATE);
            Objects.requireNonNull(mBinding.recycler.getLayoutManager()).onRestoreInstanceState(mRecyclerState);

            // Create and load the data in the Recycler View Adapter
            if (mDivePickAdapter == null) {
                mDivePickAdapter = new DivePickAdapter(this, mDivePickList);
                mDivePickAdapter.setState(mState);
            }
        } else {
            // 1st time in
            // Get the data from the Intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mState = getIntent().getParcelableExtra(MyConstants.STATE,State.class);
                mDivePick = getIntent().getParcelableExtra(MyConstants.DIVE_PICK,DivePick.class);
            } else {
                mState = getIntent().getParcelableExtra(MyConstants.STATE);
                mDivePick = getIntent().getParcelableExtra(MyConstants.DIVE_PICK);
            }

            // Get data for the Dives from the DB
            mAirDa.open();
            // Get all the dives from the DataBase
            mDivePickList = mAirDa.getAllDivesWBuddy();
            mAirDa.close();
        }

        // Create and load the data in the Recycler View Adapter
        if (mDivePickAdapter == null) {
            mDivePickAdapter = new DivePickAdapter(this, mDivePickList);
            mDivePickAdapter.setState(mState);
        } else {
            mDivePickAdapter.setDivePickList(mDivePickList);
            mDivePickAdapter.setState(mState);
        }

        // If the list is empty, make sure there is a valid POJO in the adapter
        if (mDivePickList.size() == MyConstants.ZERO_I) {
            mDivePickAdapter.setDivePick(mDivePick);
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mDivePickAdapter);
        mBinding.recycler.setLayoutManager(new DiveLinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);

        // Set the listener for the Search
        mBinding.searchView.setOnSearchClickListener(v -> mBinding.fabDive.setVisibility(View.INVISIBLE));

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                //Filter as typing
                mDivePickAdapter.getFilter().filter(query);
                return false;
            }
        });

        mBinding.searchView.setOnCloseListener(() -> {
            mBinding.fabDive.setVisibility(View.VISIBLE);
            return false;
        });

        // Set the listener for the Dive FAB Dive
        // To add a new dive manually
        mBinding.fabDive.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DiveActivity.class);
            Dive dive = new Dive();
            dive.setContext(getApplicationContext());
            dive.setDiveNo(MyConstants.ZERO_L);
            dive.setMyGroupNo(MyConstants.ZERO_L);
            dive.setMyBuddyDiverNo(MyConstants.ZERO_L);
            dive.setMyBuddyGroupNo(MyConstants.ZERO_L);
            intent.putExtra(MyConstants.DIVE, dive);
            intent.putExtra(MyConstants.STATE, mState);
            addDiveLauncher.launch(intent);
        });

        // Set the listener for the Computer Dive FAB DiveComputer
        // To import/download computer dives
        mBinding.fabComputerDive.setOnClickListener(view -> {
            Intent intent = new Intent(this, ComputerPickActivity.class);
            // TODO: Why no launcher??
            startActivity(intent);
            // TODO: Need to refresh the list with the newly added dives from the computer
        });

        if (mDivePickAdapter.getDivePickList().size() >= 1) {
            // There is at least one Dive in the collection
            // Always scroll to the first Dive
            mBinding.recycler.smoothScrollToPosition(HEADER_OFFSET);
            // Set the current position in the Adapter
            mDivePickAdapter.setSelectedPosition(HEADER_OFFSET);
            // Wait and perform a Click
            mBinding.recycler.postDelayed(() -> {
                try {
                    Objects.requireNonNull(mBinding.recycler.findViewHolderForAdapterPosition(HEADER_OFFSET)).itemView.performClick();
                } catch (Exception e) {
                    // Do nothing
                }
            }, MyConstants.DELAY_MILLI_SECONDS);
        }

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        if (mDivePick.getInMultiEditMode()) {
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
            mDivePick = mDivePickAdapter.getDivePick();
            Intent intent = new Intent(this, DiveActivity.class);
            Dive dive = new Dive();
            dive.setContext(getApplicationContext());
            dive.setDiveNo(mDivePick.getDiveNo());
            dive.setLogBookNo(mDivePick.getLogBookNo());
            intent.putExtra(MyConstants.DIVE, dive);
            intent.putExtra(MyConstants.STATE, mState);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_dive_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mDivePick.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabDive.setVisibility(View.VISIBLE);
                    setVisibility(0,false);
                    return true;
                }
            } else {
                Intent intent = new Intent();
                // Get the DivePick from the Adapter
                mDivePick = mDivePickAdapter.getDivePick();
                intent.putExtra(MyConstants.PICK_A_DIVE, mDivePick);
                intent.putExtra(MyConstants.STATE, mState);
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
        if (mDivePick.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabDive.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            Intent intent = new Intent();
            mDivePick = mDivePickAdapter.getDivePick();
            intent.putExtra(MyConstants.PICK_A_DIVE, mDivePick);
            intent.putExtra(MyConstants.STATE, mState);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // State
        Parcelable myState = mState;
        outState.putParcelable(MyConstants.STATE, myState);

        // DivePick
        Parcelable myDivePickState = mDivePick;
        outState.putParcelable(MyConstants.DIVE_PICK, myDivePickState);

        // RecyclerView
        Serializable recyclerData = mDivePickAdapter.getDivePickList();
        outState.putSerializable("RECYCLER_DATA", recyclerData);

        Parcelable mRecyclerState = Objects.requireNonNull(mBinding.recycler.getLayoutManager()).onSaveInstanceState();
        outState.putParcelable(MyConstants.LIST_STATE,mRecyclerState);

        // Save the state
        super.onSaveInstanceState(outState);
    }

    public void onPause() {
        super.onPause();
        // Unregister the Search listener
        mBinding.searchView.setOnQueryTextListener(null);
    }

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> addDiveLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED Dive from the Dive activity
                    Dive dive;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mState = data.getParcelableExtra(MyConstants.STATE,State.class);
                        dive = data.getParcelableExtra(MyConstants.DIVE,Dive.class);
                    } else {
                        mState = data.getParcelableExtra(MyConstants.STATE);
                        dive = data.getParcelableExtra(MyConstants.DIVE);
                    }
                    assert dive != null;
                    dive.setContext(this);
                    // The Dive has already been added to the Database
                    // Need to add it to the recyclerView
                    DivePick divePick = new DivePick();
                    divePick.setContext(this);
                    divePick.setDiveNo(dive.getDiveNo());
                    divePick.setLogBookNo(dive.getLogBookNo());
                    divePick.setDate(dive.getDate());
                    divePick.setStatus(dive.getStatus());
                    divePick.setMyBuddyFullName(dive.getMyBuddyFullName());
                    divePick.setDiveType(dive.getDiveType());
                    divePick.setDiveTypeDesc(dive.getDiveTypeDesc());
                    if (dive.getLocation() == null || dive.getLocation().equals("") || dive.getLocation().equals(" ")) {
                        divePick.setLocation(this.getResources().getString(R.string.sql_location_unknown));
                    } else {
                        divePick.setLocation(dive.getLocation());
                    }
                    if (dive.getDiveSite() == null || dive.getDiveSite().equals("") || dive.getDiveSite().equals(" ")) {
                        divePick.setDiveSite(this.getResources().getString(R.string.sql_location_unknown));
                    } else {
                        divePick.setDiveSite(dive.getDiveSite());
                    }
                    mDivePickAdapter.addDive(divePick);
                    mDivePick = divePick;

                    // Work with the list in the Adapter as it might be Filtered Out
                    // Try to find the Dive in the collection
                    int position = mDivePickAdapter.getDivePickPosition(mDivePick);

                    if (position == -1 && mDivePickAdapter.getDivePickList().size() >= 1) {
                        // Can't find the Dive
                        // But there are Dives in the list
                        position = 0;
                    }

                    // Scroll to the Dive
                    // When first opening Activity, scroll to the first Dive
                    // When adding a Dive, scroll to the first Dive
                    mBinding.recycler.smoothScrollToPosition(position + HEADER_OFFSET);
                    // Set the current position in the Adapter
                    mDivePickAdapter.setSelectedPosition(position + HEADER_OFFSET);
                }
            });

    // TODO: Merge into fabDive (old)
    ActivityResultLauncher<Intent> addDiveNewLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED Dive from the Dive activity
                    Dive dive;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mState = data.getParcelableExtra(MyConstants.STATE,State.class);
                        dive = data.getParcelableExtra(MyConstants.DIVE,Dive.class);
                    } else {
                        mState = data.getParcelableExtra(MyConstants.STATE);
                        dive = data.getParcelableExtra(MyConstants.DIVE);
                    }
                    assert dive != null;
                    dive.setContext(this);
                    // The Dive has already been added to the Database
                    // Need to add it to the recyclerView
                    DivePick divePick = new DivePick();
                    divePick.setContext(this);
                    divePick.setDiveNo(dive.getDiveNo());
                    divePick.setLogBookNo(dive.getLogBookNo());
                    divePick.setDate(dive.getDate());
                    divePick.setStatus(dive.getStatus());
                    divePick.setMyBuddyFullName(dive.getMyBuddyFullName());
                    divePick.setDiveType(dive.getDiveType());
                    divePick.setDiveTypeDesc(dive.getDiveTypeDesc());
                    if (dive.getLocation() == null || dive.getLocation().equals("") || dive.getLocation().equals(" ")) {
                        divePick.setLocation(this.getResources().getString(R.string.sql_location_unknown));
                    } else {
                        divePick.setLocation(dive.getLocation());
                    }
                    if (dive.getDiveSite() == null || dive.getDiveSite().equals("") || dive.getDiveSite().equals(" ")) {
                        divePick.setDiveSite(this.getResources().getString(R.string.sql_location_unknown));
                    } else {
                        divePick.setDiveSite(dive.getDiveSite());
                    }
                    mDivePickAdapter.addDive(divePick);
                    mDivePick = divePick;

                    // Work with the list in the Adapter as it might be Filtered Out
                    // Try to find the Dive in the collection
                    int position = mDivePickAdapter.getDivePickPosition(mDivePick);

                    if (position == -1 && mDivePickAdapter.getDivePickList().size() >= 1) {
                        // Can't find the Dive
                        // But there are Dives in the list
                        position = 0;
                    }

                    // Scroll to the Dive
                    // When first opening Activity, scroll to the first Dive
                    // When adding a Dive, scroll to the first Dive
                    mBinding.recycler.smoothScrollToPosition(position + HEADER_OFFSET);
                    // Set the current position in the Adapter
                    mDivePickAdapter.setSelectedPosition(position + HEADER_OFFSET);
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED Dive from the Dive activity
                    Dive dive;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mState = data.getParcelableExtra(MyConstants.STATE,State.class);
                        dive = data.getParcelableExtra(MyConstants.DIVE,Dive.class);
                    } else {
                        mState = data.getParcelableExtra(MyConstants.STATE);
                        dive = data.getParcelableExtra(MyConstants.DIVE);
                    }
                    assert dive != null;
                    dive.setContext(this);
                    // The Dive has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    DivePick divePick = new DivePick();
                    divePick.setContext(this);
                    divePick.setDiveNo(dive.getDiveNo());
                    divePick.setLogBookNo(dive.getLogBookNo());
                    divePick.setDate(dive.getDate());
                    divePick.setStatus(dive.getStatus());
                    divePick.setMyBuddyFullName(dive.getMyBuddyFullName());
                    divePick.setDiveType(dive.getDiveType());
                    divePick.setDiveTypeDesc(dive.getDiveTypeDesc());
                    divePick.setLocation(dive.getLocation());
                    divePick.setDiveSite(dive.getDiveSite());
                    mDivePickAdapter.modifyDive(divePick);
                    mDivePick = divePick;
                }
            });

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mDivePick.setInMultiEditMode(mInMultiEditMode);
        if (mDivePick.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add Dive floating button
            mBinding.fabDive.setVisibility(View.INVISIBLE);
            // Showing Checkboxes on all Dive items
            for (int i = 0; i < mDivePickList.size(); i++) {
                DivePick divePick = mDivePickList.get(i);
                divePick.setVisible(true);
                if (position - HEADER_OFFSET == i) {
                    divePick.setChecked(true);
                }
            }
            mDivePickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mDivesToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add Dive floating button
            mBinding.fabDive.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all Dive items
            for (int i = 0; i < mDivePickList.size(); i++) {
                DivePick divePick = mDivePickList.get(i);
                divePick.setVisible(false);
                divePick.setChecked(false);
            }
            mDivePickAdapter.notifyDataSetChanged();
            mDivesToDelete = 1;
            mDivePickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mDivesToDelete = 0;
        } else {
            mDivesToDelete = mDivePickList.size();
        }
        for (int i = 0; i < mDivePickList.size(); i++) {
            DivePick mDivePick = mDivePickList.get(i);
            mDivePick.setChecked(checked);
            if (checked) {
                mDivesToDelete++;
            } else {
                mDivesToDelete--;
            }
        }
        mDivePickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mDivesToDelete);
        if (getSupportActionBar() != null) {
            if (mDivesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countDives(Boolean checked) {
        if (checked) {
            mDivesToDelete++;
        } else {
            mDivesToDelete--;
        }
        mAppTitleCount = String.valueOf(mDivesToDelete);
        if (getSupportActionBar() != null) {
            if (mDivesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mDivesToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mDivesToDelete + " " + getString(R.string.msg_dives_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mDivePickList.size() - 1;
                        mAirDa.openWithFKConstraintsEnabled();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            DivePick mDivePick = mDivePickList.get(position);
                            if (mDivePick.getChecked()) {
                                mDivePick = mDivePickAdapter.getDivePick(position);
                                mDivePickAdapter.deleteDive(position);
                                mAirDa.deleteDive(mDivePick.getDiveNo());
                                mDivesToDelete--;
                            }
                        }
                        mAirDa.close();
                        if (mDivePickList.size() > 0) {
                            // Set the current position in the Adapter
                            mDivePickAdapter.setSelectedPosition(0);
                            // Scroll to the Dive, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mDivePickAdapter.notifyDataSetChanged();

                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(mOriginalTitle);
                            mBinding.fabDive.setVisibility(View.VISIBLE);
                            setVisibility(0,false);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, null)
                    .show();
        }
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added Dive
        // The screen does not scroll if the newly added Dive is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setDivePick(DivePick divePick) {
        mDivePick = divePick;
    }
}