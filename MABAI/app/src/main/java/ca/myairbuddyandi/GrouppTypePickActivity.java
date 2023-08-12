package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
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

import ca.myairbuddyandi.databinding.GrouppTypePickActivityBinding;

/**
 * Created by Michel on 2017-08-15.
 * Holds all the logic for the GrouppTypePickActivity class
 */

public class GrouppTypePickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "GrouppTypePickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mGrouppTypesToDelete = 0;
    private int mPosition;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<GrouppType> mGrouppTypePickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private GrouppType mGrouppType = new GrouppType();
    private GrouppTypePickActivityBinding mBinding = null;
    private GrouppTypePickAdapter mGrouppTypePickAdapter;
    private String mOriginalTitle;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.groupp_type_pick_activity);

        //Get data for the GrouppTypes
        mAirDa.open();
        mGrouppTypePickList = mAirDa.getAllGroupTypes();

        if (mGrouppTypePickList.size() > 0) {
            mGrouppType = mGrouppTypePickList.get(0);
        }

        // Create and load the data in the Recycler View Adapter
        if (mGrouppTypePickAdapter == null) {
            mGrouppTypePickAdapter = new GrouppTypePickAdapter(this, mGrouppTypePickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mGrouppTypePickList.size() == MyConstants.ZERO_I) {
                mGrouppTypePickAdapter.setGroupType(mGrouppType);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mGrouppTypePickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listener for the FAB
        mBinding.fabGroupType.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GrouppTypeActivity.class);
            GrouppType grouppType = new GrouppType();
            grouppType.setGroupType("");
            intent.putExtra(MyConstants.GROUPP_TYPE, grouppType);
            addLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the Diver in the collection
        mPosition = mGrouppTypePickList.indexOf(mGrouppType);

        if (mPosition == -1 && mGrouppTypePickList.size() >= 1) {
            // Can't find the Cylinder Type
            // Select first row
            mPosition = 0;
        }

        if (mGrouppTypePickAdapter.getGroupTypePickList().size() >= 1) {
            // There is at least one Group Type in the collection
            // Scroll to the Group Type
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mGrouppTypePickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        if (mGrouppType.getInMultiEditMode()) {
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
            mGrouppType = mGrouppTypePickAdapter.getGroupType();
            Intent intent = new Intent(this, GrouppTypeActivity.class);
            intent.putExtra(MyConstants.GROUPP_TYPE, mGrouppType);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_groupp_type_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mGrouppType.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabGroupType.setVisibility(View.VISIBLE);
                    setVisibility(0,false);
                    return true;
                }
            } else {
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
        if (mGrouppType.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabGroupType.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            Intent intent = new Intent();
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
                    // Get the newly ADDED GrouppType from the GrouppType activity
                    GrouppType grouppType = data.getParcelableExtra(MyConstants.GROUPP_TYPE);
                    // The GrouppType has already been added to the Database
                    // Need to add it to the recyclerView
                    mGrouppTypePickAdapter.addGroupType(grouppType);
                    mGrouppType = grouppType;
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED GrouppType from the GrouppType activity
                    GrouppType grouppType = data.getParcelableExtra(MyConstants.GROUPP_TYPE);
                    // The GrouppType has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    mGrouppTypePickAdapter.modifyGroupType(grouppType);
                    mGrouppType = grouppType;
                }
            });

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mGrouppType.setInMultiEditMode(mInMultiEditMode);
        if (mGrouppType.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add DiveType floating button
            mBinding.fabGroupType.setVisibility(View.INVISIBLE);
            // Only set the Multi Mode of there are rows to delete, other than the System Defined values
            // Showing Checkboxes on all GrouppType items
            for (int i = 0; i < mGrouppTypePickList.size(); i++) {
                GrouppType grouppType = mGrouppTypePickList.get(i);
                if (grouppType.getSystemDefined().equals("N")) {
                    // The Checkbox is VISIBLE, the user CAN delete it
                    grouppType.setVisible(View.VISIBLE);
                } else {
                    // The Checkbox is INVISIBLE, the user CANNOT delete it
                    grouppType.setVisible(View.INVISIBLE);
                }
                if (position - HEADER_OFFSET == i && grouppType.getSystemDefined().equals("N")) {
                    grouppType.setChecked(true);
                    mGrouppTypesToDelete++;
                }
            }
            mGrouppTypePickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mGrouppTypesToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add GrouppType floating button
            mBinding.fabGroupType.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all GrouppType items
            // System Defined or not
            for (int i = 0; i < mGrouppTypePickList.size(); i++) {
                GrouppType grouppType = mGrouppTypePickList.get(i);
                grouppType.setVisible(View.GONE);
                grouppType.setChecked(false);
            }
            mGrouppTypePickAdapter.notifyDataSetChanged();
            mGrouppTypesToDelete = 0;
            mGrouppTypePickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mGrouppTypesToDelete = 0;
        } else {
            mGrouppTypesToDelete = mGrouppTypePickList.size();
        }
        for (int i = 0; i < mGrouppTypePickList.size(); i++) {
            GrouppType grouppType = mGrouppTypePickList.get(i);
            if (grouppType.getSystemDefined().equals("N")) {
                grouppType.setChecked(checked);
                if (checked) {
                    mGrouppTypesToDelete++;
                } else {
                    mGrouppTypesToDelete--;
                }
            } else {
                if (!checked) {
                    mGrouppTypesToDelete--;
                }
            }
        }
        mGrouppTypePickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mGrouppTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mGrouppTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countGrouppTypes(Boolean checked) {
        if (checked) {
            mGrouppTypesToDelete++;
        } else {
            mGrouppTypesToDelete--;
        }
        mAppTitleCount = String.valueOf(mGrouppTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mGrouppTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mGrouppTypesToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mGrouppTypesToDelete + " " + getString(R.string.msg_groupp_type_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mGrouppTypePickList.size() - 1;
                        mAirDa.openWithFKConstraintsEnabled();
                        String successTypes;
                        String failedTypes;
                        StringBuilder sbSuccessType = new StringBuilder();
                        StringBuilder sbFailedType = new StringBuilder();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            GrouppType grouppType = mGrouppTypePickList.get(position);
                            if (grouppType.getChecked()) {
                                grouppType = mGrouppTypePickAdapter.getGroupType(position);
                                Integer rc = mAirDa.deleteGroupType(grouppType.getGroupType());
                                if (rc.equals(MyConstants.ZERO_I)) {
                                    // Delete was successful
                                    mGrouppTypePickAdapter.deleteGroupType(position);
                                    mGrouppTypesToDelete--;
                                    sbSuccessType.insert(0, ", ");
                                    sbSuccessType.insert(0, grouppType.getGroupType());
                                    mGrouppType.setHasDataChanged(true);

                                } else {
                                    // Delete failed
                                    sbFailedType.insert(0,", ");
                                    sbFailedType.insert(0, grouppType.getGroupType());
                                }
                            }
                        }
                        mAirDa.close();
                        if (mGrouppTypePickList.size() > 0) {
                            // Set the current position in the Adapter
                            mGrouppTypePickAdapter.setSelectedPosition(0);
                            // Scroll to the GrouppType, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mGrouppTypePickAdapter.notifyDataSetChanged();

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
                        mBinding.fabGroupType.setVisibility(View.VISIBLE);
                        setVisibility(0,false);
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added GrouppType
        // The screen does not scroll if the newly added GrouppType is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setGrouppType(GrouppType grouppType) {mGrouppType = grouppType;}
}
