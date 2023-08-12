package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import ca.myairbuddyandi.databinding.GrouppPickActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the GrouppPickActivity class
 */

public class GrouppPickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "GroupPickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public
    
    // Protected
    
    // Private
    private int mGrouppsToDelete = 1;
    private int mPosition;
    private final AirDA mAirDa =  new AirDA(this);
    private ArrayList<GrouppPick> mGrouppPickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private GrouppPick mGrouppPick = new GrouppPick();
    private GrouppPickAdapter mGrouppPickAdapter;
    private GrouppPickActivityBinding mBinding = null;
    private final MyDialogs mDialogs = new MyDialogs();
    private String mOriginalTitle;

    // End of variables

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.groupp_pick_activity);

        if (savedInstanceState != null) {
            // 2nd time in
            // GrouppPick
            mGrouppPick = savedInstanceState.getParcelable(MyConstants.PICK_A_GROUPP);

            if (mGrouppPick != null && mGrouppPick.getLogBookNo() != MyConstants.ZERO_I) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mGrouppPick.getLogBookNo());
                }
            }

            // RecyclerView
            mGrouppPickList = (ArrayList<GrouppPick>) savedInstanceState.getSerializable("RECYCLER_DATA");

            Parcelable mRecyclerState = savedInstanceState.getParcelable(MyConstants.LIST_STATE);
            Objects.requireNonNull(mBinding.recycler.getLayoutManager()).onRestoreInstanceState(mRecyclerState);

            if (mGrouppPickAdapter == null) {
                mGrouppPickAdapter = new GrouppPickAdapter(this, mGrouppPickList);
                // If the list is empty, make sure there is a valid POJO in the adapter
                if (mGrouppPickList.size() == MyConstants.ZERO_I) {
                    mGrouppPickAdapter.setGrouppPick(mGrouppPick);
                }
            }

            mBinding.recycler.setAdapter(mGrouppPickAdapter);
            mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
            mBinding.recycler.setHasFixedSize(true);

        } else {
            // 1st time in
            // Get the data from the Intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mGrouppPick = getIntent().getParcelableExtra(MyConstants.PICK_A_GROUPP,GrouppPick.class);
            } else {
                mGrouppPick = getIntent().getParcelableExtra(MyConstants.PICK_A_GROUPP);
            }

            assert mGrouppPick != null;
            if (mGrouppPick.getLogBookNo() != MyConstants.ZERO_I) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mGrouppPick.getLogBookNo());
                }
            }

            //Get data for the Groupps
            mAirDa.open();
            mGrouppPickList = mAirDa.getAllGrouppWDesc(mGrouppPick.getDiverNo(), mGrouppPick.getDiveNo());
        }

        // Create and load the data in the Recycler View Adapter
        if (mGrouppPickAdapter == null) {
            mGrouppPickAdapter = new GrouppPickAdapter(this, mGrouppPickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mGrouppPickList.size() == MyConstants.ZERO_I) {
                mGrouppPickAdapter.setGrouppPick(mGrouppPick);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mGrouppPickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listener for the Search
        mBinding.searchView.setOnSearchClickListener(v -> mBinding.fabGroupp.setVisibility(View.INVISIBLE));

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                //Filter as typing
                mGrouppPickAdapter.getFilter().filter(query);
                return false;
            }
        });

        mBinding.searchView.setOnCloseListener(() -> {
            mBinding.fabGroupp.setVisibility(View.VISIBLE);
            return false;
        });

        // Set the listener for the Cancel button
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mGrouppPick.getHasDataChanged()) {
                mDialogs.confirm(GrouppPickActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        // Set the listener for the Pick button
        mBinding.pickButton.setOnClickListener(view -> {
            // Going back to GrouppActivity
            if (mGrouppPickList.size() > 0) {
                Intent intent = new Intent();
                mGrouppPick = mGrouppPickAdapter.getGroupPick();
                intent.putExtra(MyConstants.PICK_A_GROUPP, mGrouppPick);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        // Set the listener for the FAB
        mBinding.fabGroupp.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GrouppActivity.class);
            Groupp groupp = new Groupp();
            groupp.setGroupNo(MyConstants.ZERO_L);
            groupp.setDiverNo(mGrouppPick.getDiverNo());
            groupp.setDiveNo(mGrouppPick.getDiveNo());
            intent.putExtra(MyConstants.GROUPP, groupp);
            addLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Work with the list in the Adapter as it might be Filtered Out
        // Try to find the GrouppPick in the collection
        mPosition = mGrouppPickAdapter.getGroupPickPosition(mGrouppPick);

        if (mPosition == -1 && mGrouppPickAdapter.getGroupPickList().size() >= 1) {
            // Can't find the Groupp
            // Select first row
            mPosition = 0;
        }

        if (mGrouppPickAdapter.getGroupPickList().size() >= 1) {
            // There is at least one Groupp in the collection
            // Scroll to the Groupp
            mBinding.recycler.smoothScrollToPosition(mPosition + 1);
            // Set the current position in the Adapter
            mGrouppPickAdapter.setSelectedPosition(mPosition + 1);
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

        if (mGrouppPick.getInMultiEditMode()) {
            deleteItem.setVisible(true);
            checkAll.setVisible(true);
            deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            checkAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            mBinding.pickButton.setText(R.string.lbl_blank);
            mBinding.pickButton.setEnabled(false);
            mBinding.pickButton.setAlpha(0.5f);
        } else {
            deleteItem.setVisible(false);
            checkAll.setVisible(false);
            deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            checkAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            if (!mGrouppPick.getGroupNo().equals(MyConstants.MINUS_ONE_L)) {
                mBinding.pickButton.setText(R.string.button_pick);
                mBinding.pickButton.setEnabled(true);
                mBinding.pickButton.setAlpha(1.0f);
            } else {
                mBinding.pickButton.setText(R.string.lbl_blank);
                mBinding.pickButton.setEnabled(false);
                mBinding.pickButton.setAlpha(0.5f);
            }
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
            mGrouppPick = mGrouppPickAdapter.getGroupPick();
            Intent intent = new Intent(this, GrouppActivity.class);
            Groupp groupp = new Groupp();
            groupp.setGroupNo(mGrouppPick.getGroupNo());
            groupp.setDiveNo(mGrouppPick.getDiveNo());
            intent.putExtra(MyConstants.GROUPP, groupp);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_groupp_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mGrouppPick.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabGroupp.setVisibility(View.VISIBLE);
                    setVisibility(0,false);
                    return true;
                }
            } else {
                Intent intent = new Intent();
                // Get the GrouppPick from the Adapter
                mGrouppPick = mGrouppPickAdapter.getGroupPick();
                intent.putExtra(MyConstants.PICK_A_GROUPP, mGrouppPick);
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
        if (mGrouppPick.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabGroupp.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            // Can't select a valid row in Multi Edit Mode
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // GrouppPick
        Parcelable myGrouppPickState = mGrouppPick;
        outState.putParcelable(MyConstants.PICK_A_GROUPP, myGrouppPickState);

        // RecyclerView
        Serializable recyclerData = mGrouppPickAdapter.getGroupPickList();
        outState.putSerializable("RECYCLER_DATA", recyclerData);

        Parcelable mRecyclerState = Objects.requireNonNull(mBinding.recycler.getLayoutManager()).onSaveInstanceState();
        outState.putParcelable(MyConstants.LIST_STATE,mRecyclerState);

        // Save the state
        super.onSaveInstanceState(outState);
    }

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED Groupp from the Groupp activity
                    Groupp groupp;
                    groupp = data.getParcelableExtra(MyConstants.GROUPP);
                    // The Groupp has already been added to the Database
                    // Need to add it to the recyclerView
                    GrouppPick grouppPick = new GrouppPick();
                    assert groupp != null;
                    grouppPick.setGroupNo(groupp.getGroupNo());
                    grouppPick.setDiverNo(groupp.getDiverNo());
                    grouppPick.setDiveNo(groupp.getDiveNo());
                    grouppPick.setGroupType(groupp.getGroupType());
                    grouppPick.setGroupTypeDescription(groupp.getGroupTypeDescription());
                    grouppPick.setDescription(groupp.getDescription());
                    grouppPick.setCylinderType(groupp.getCylinderType());
                    grouppPick.setUsageType(groupp.getUsageType());
                    grouppPick.setHasDataChanged(groupp.getHasDataChanged());
                    mGrouppPickAdapter.addGroupp(grouppPick);
                    mGrouppPick = grouppPick;
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED Groupp from the Groupp activity
                    Groupp groupp;
                    groupp = data.getParcelableExtra(MyConstants.GROUPP);
                    // The Groupp has already been added to the Database
                    // Need to add it to the recyclerView
                    GrouppPick grouppPick = new GrouppPick();
                    assert groupp != null;
                    grouppPick.setGroupNo(groupp.getGroupNo());
                    grouppPick.setDiverNo(groupp.getDiverNo());
                    grouppPick.setDiveNo(groupp.getDiveNo());
                    grouppPick.setGroupType(groupp.getGroupType());
                    grouppPick.setGroupTypeDescription(groupp.getGroupTypeDescription());
                    grouppPick.setDescription(groupp.getDescription());
                    grouppPick.setCylinderType(groupp.getCylinderType());
                    grouppPick.setUsageType(groupp.getUsageType());
                    grouppPick.setHasDataChanged(groupp.getHasDataChanged());
                    mGrouppPickAdapter.modifyGroupp(grouppPick);
                    mGrouppPick = grouppPick;
                }
            });

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

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mGrouppPick.setInMultiEditMode(mInMultiEditMode);
        invalidateOptionsMenu();
        if (mGrouppPick.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            // Hiding the Add Diver floating button
            mBinding.fabGroupp.setVisibility(View.INVISIBLE);
            // Showing Checkboxes on all Diver items
            for (int i = 0; i < mGrouppPickList.size(); i++) {
                GrouppPick divePick = mGrouppPickList.get(i);
                divePick.setVisible(true);
                if (position - HEADER_OFFSET == i) {
                    divePick.setChecked(true);
                }
            }
            mGrouppPickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mGrouppsToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            // Showing the Add Diver floating button
            mBinding.fabGroupp.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all Diver items
            for (int i = 0; i < mGrouppPickList.size(); i++) {
                GrouppPick divePick = mGrouppPickList.get(i);
                divePick.setVisible(false);
                divePick.setChecked(false);
            }
            mGrouppPickAdapter.notifyDataSetChanged();
            mGrouppsToDelete = 1;
            mGrouppPickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mGrouppsToDelete = 0;
        } else {
            mGrouppsToDelete = mGrouppPickList.size();
        }
        for (int i = 0; i < mGrouppPickList.size(); i++) {
            GrouppPick mGrouppPick = mGrouppPickList.get(i);
            mGrouppPick.setChecked(checked);
            if (checked) {
                mGrouppsToDelete++;
            } else {
                mGrouppsToDelete--;
            }
        }
        mGrouppPickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mGrouppsToDelete);
        if (getSupportActionBar() != null) {
            if (mGrouppsToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countGroupps(Boolean checked) {
        if (checked) {
            mGrouppsToDelete++;
        } else {
            mGrouppsToDelete--;
        }
        mAppTitleCount = String.valueOf(mGrouppsToDelete);
        if (getSupportActionBar() != null) {
            if (mGrouppsToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mGrouppsToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mGrouppsToDelete + " " + getString(R.string.msg_equipment_groups_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mGrouppPickList.size() - 1;
                        String successTypes;
                        String failedTypes;
                        StringBuilder sbSuccessType = new StringBuilder();
                        StringBuilder sbFailedType = new StringBuilder();
                        mAirDa.openWithFKConstraintsEnabled();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            GrouppPick grouppPick = mGrouppPickList.get(position);
                            if (grouppPick.getChecked()) {
                                grouppPick = mGrouppPickAdapter.getGroupPick(position);
                                Integer rc = mAirDa.deleteGroupp(grouppPick.getGroupNo());
                                if (rc.equals(MyConstants.ZERO_I)) {
                                    // Delete was successful
                                    mGrouppPickAdapter.deleteGroupPick(position);
                                    mGrouppsToDelete--;
                                    sbSuccessType.insert(0, ", ");
                                    sbSuccessType.insert(0, grouppPick.getDescription());
                                    mGrouppPick.setHasDataChanged(true);
                                } else {
                                    // Delete failed
                                    sbFailedType.insert(0,", ");
                                    sbFailedType.insert(0, grouppPick.getDescription());
                                }
                            }
                        }
                        // Do not close the DB connection
                        // The DiveActivity controls the DataBase transaction
                        if (mGrouppPickList.size() > 0) {
                            // Set the current position in the Adapter
                            mGrouppPickAdapter.setSelectedPosition(0);
                            // Scroll to the Group, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mGrouppPickAdapter.notifyDataSetChanged();

                        successTypes = sbSuccessType.toString();
                        failedTypes = sbFailedType.toString();

                        if (successTypes.equals("")) {
                            successTypes = MyConstants.NONE;
                        } else {
                            successTypes = MyFunctions.removeLastString(successTypes, ", ");
                        }

                        if (failedTypes.equals("")) {
                            failedTypes = MyConstants.NONE;
                        } else {
                            failedTypes = MyFunctions.removeLastString(failedTypes,", ");
                        }

                        Resources res = getResources();
                        String message = String.format(res.getString(R.string.msg_delete_fk_constraint),res.getString(R.string.mn_groupp_type),successTypes,failedTypes);
                        showDeleteResults(message);

                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.button_cancel, null)
                    .show();
        }
    }

    public void showDeleteResults(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(mOriginalTitle);
                        mBinding.fabGroupp.setVisibility(View.VISIBLE);
                        setVisibility(0,false);
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added GrouppPick
        // The screen does not scroll if the newly added GrouppPick is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setGrouppPick(GrouppPick grouppPick) {
        mGrouppPick = grouppPick;
    }
}