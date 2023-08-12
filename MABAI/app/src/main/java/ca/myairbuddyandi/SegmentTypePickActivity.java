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

import ca.myairbuddyandi.databinding.SegmentTypePickActivityBinding;

/**
 * Created by Michel on 2017-08-15.
 * Holds all of the logic for the SegmentTypePickActivity class
 */

public class SegmentTypePickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "SegmentTypePickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mPosition;
    private int mSegmentTypesToDelete = 0;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<SegmentType> mSegmentTypePickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private SegmentType mSegmentType = new SegmentType();
    private SegmentTypePickAdapter mSegmentTypePickAdapter;
    private SegmentTypePickActivityBinding mBinding = null;
    private String mOriginalTitle;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.segment_type_pick_activity);

        //Get data for the SegmentTypes
        mAirDa.open();
        mSegmentTypePickList = mAirDa.getAllSegmentTypes();

        if (mSegmentTypePickList.size() > 0) {
            mSegmentType = mSegmentTypePickList.get(0);
        }

        // Create and load the data in the Recycler View Adapter
        if (mSegmentTypePickAdapter == null) {
            mSegmentTypePickAdapter = new SegmentTypePickAdapter(this, mSegmentTypePickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mSegmentTypePickList.size() == MyConstants.ZERO_I) {
                mSegmentTypePickAdapter.setSegmentType(mSegmentType);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mSegmentTypePickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listener for the FAB
        mBinding.fabSegmentType.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SegmentTypeActivity.class);
            SegmentType segmentType = new SegmentType();
            segmentType.setSegmentType("");
            intent.putExtra(MyConstants.SEGMENT_TYPE, segmentType);
            addLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the Diver in the collection
        mPosition = mSegmentTypePickList.indexOf(mSegmentType);

        if (mPosition == -1 && mSegmentTypePickList.size() >= 1) {
            // Can't find the Cylinder Type
            // Select first row
            mPosition = 0;
        }

        if (mSegmentTypePickAdapter.getSegmentTypePickList().size() >= 1) {
            // There is at least one Segment Type in the collection
            // Scroll to the Segment Type
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mSegmentTypePickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        if (mSegmentType.getInMultiEditMode()) {
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
            mSegmentType = mSegmentTypePickAdapter.getSegmentType();
            Intent intent = new Intent(this, SegmentTypeActivity.class);
            intent.putExtra(MyConstants.SEGMENT_TYPE, mSegmentType);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_segment_type_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // Going back to SacRmvActivity
            if (mSegmentType.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabSegmentType.setVisibility(View.VISIBLE);
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
        if (mSegmentType.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabSegmentType.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
            finish();
        }
    }

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mSegmentType.setInMultiEditMode(mInMultiEditMode);
        if (mSegmentType.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add DiveType floating button
            mBinding.fabSegmentType.setVisibility(View.INVISIBLE);
            // Only set the Multi Mode of there are rows to delete, other than the System Defined values
            // Showing Checkboxes on all SegmentType items
            for (int i = 0; i < mSegmentTypePickList.size(); i++) {
                SegmentType segmentType = mSegmentTypePickList.get(i);
                if (segmentType.getSystemDefined().equals("N")) {
                    segmentType.setVisible(View.VISIBLE);
                } else {
                    // The Checkbox is INVISIBLE, the user CANNOT delete it
                    segmentType.setVisible(View.INVISIBLE);
                }
                if (position - HEADER_OFFSET == i && segmentType.getSystemDefined().equals("N")) {
                    segmentType.setChecked(true);
                    mSegmentTypesToDelete++;
                }
            }
            mSegmentTypePickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mSegmentTypesToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add SegmentType floating button
            mBinding.fabSegmentType.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all SegmentType items
            for (int i = 0; i < mSegmentTypePickList.size(); i++) {
                SegmentType segmentType = mSegmentTypePickList.get(i);
                segmentType.setVisible(View.GONE);
                segmentType.setChecked(false);
            }
            mSegmentTypePickAdapter.notifyDataSetChanged();
            mSegmentTypesToDelete = 0;
            mSegmentTypePickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
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
                    // Get the newly ADDED SegmentType from the SegmentType activity
                    SegmentType segmentType = data.getParcelableExtra(MyConstants.SEGMENT_TYPE);
                    // The SegmentType has already been added to the Database
                    // Need to add it to the recyclerView
                    mSegmentTypePickAdapter.addSegmentType(segmentType);
                    mSegmentType = segmentType;
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED SegmentType from the SegmentType activity
                    SegmentType segmentType = data.getParcelableExtra(MyConstants.SEGMENT_TYPE);
                    // The SegmentType has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    mSegmentTypePickAdapter.modifySegmentType(segmentType);
                    mSegmentType = segmentType;
                }
            });

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mSegmentTypesToDelete = 0;
        } else {
            mSegmentTypesToDelete = mSegmentTypePickList.size();
        }
        for (int i = 0; i < mSegmentTypePickList.size(); i++) {
            SegmentType segmentType = mSegmentTypePickList.get(i);
            if (segmentType.getSystemDefined().equals("N")) {
                segmentType.setChecked(checked);
                if (checked) {
                    mSegmentTypesToDelete++;
                } else {
                    mSegmentTypesToDelete--;
                }
            } else {
                if (!checked) {
                    mSegmentTypesToDelete--;
                }
            }
        }
        mSegmentTypePickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mSegmentTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mSegmentTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countSegmentTypes(Boolean checked) {
        if (checked) {
            mSegmentTypesToDelete++;
        } else {
            mSegmentTypesToDelete--;
        }
        mAppTitleCount = String.valueOf(mSegmentTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mSegmentTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mSegmentTypesToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mSegmentTypesToDelete + " " + getString(R.string.msg_segment_type_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mSegmentTypePickList.size() - 1;
                        String successTypes;
                        String failedTypes;
                        StringBuilder sbSuccessType = new StringBuilder();
                        StringBuilder sbFailedType = new StringBuilder();
                        mAirDa.openWithFKConstraintsEnabled();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            SegmentType segmentType = mSegmentTypePickList.get(position);
                            if (segmentType.getChecked()) {
                                segmentType = mSegmentTypePickAdapter.getSegmentType(position);
                                Integer rc = mAirDa.deleteSegmentType(segmentType.getSegmentType());
                                if (rc.equals(MyConstants.ZERO_I)) {
                                    // Delete was successful
                                    mSegmentTypePickAdapter.deleteSegmentType(position);
                                    mSegmentTypesToDelete--;
                                    sbSuccessType.insert(0, ", ");
                                    sbSuccessType.insert(0, segmentType.getSegmentType());
                                    mSegmentType.setHasDataChanged(true);
                                } else {
                                    // Delete failed
                                    sbFailedType.insert(0,", ");
                                    sbFailedType.insert(0, segmentType.getSegmentType());
                                }
                            }
                        }
                        mAirDa.close();
                        if (mSegmentTypePickList.size() > 0) {
                            // Set the current position in the Adapter
                            mSegmentTypePickAdapter.setSelectedPosition(0);
                            // Scroll to the SegmentType, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mSegmentTypePickAdapter.notifyDataSetChanged();

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
                        String message = String.format(res.getString(R.string.msg_delete_fk_constraint),res.getString(R.string.mn_segment_type),successTypes,failedTypes);
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
                        mBinding.fabSegmentType.setVisibility(View.VISIBLE);
                        setVisibility(0,false);
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added SegmentType
        // The screen does not scroll if the newly added SegmentType is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setSegmentType(SegmentType segmentType) {mSegmentType = segmentType;}
}
