package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
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

import ca.myairbuddyandi.databinding.DiveTypePickActivityBinding;

/**
 * Created by Michel on 2017-08-15.
 * Holds all of the logic for the DiveTypePickActivity class
 */

public class DiveTypePickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DiveTypePickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mDiveTypesToDelete = 1;
    private int mPosition;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<DiveType> mDiveTypePickList = new ArrayList<>();
    private DiveType mDiveType = new DiveType();
    private DiveTypePickActivityBinding mBinding = null;
    private DiveTypePickAdapter mDiveTypePickAdapter;
    private CharSequence mAppTitleCount;
    private String mOriginalTitle;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.dive_type_pick_activity);

        //Get data for the DiveTypes
        mAirDa.open();
        mDiveTypePickList = mAirDa.getAllDiveTypePickable();

        if (mDiveTypePickList.size() > 0) {
            mDiveType = mDiveTypePickList.get(0);
        }

        // Create and load the data in the Recycler View Adapter
        if (mDiveTypePickAdapter == null) {
            mDiveTypePickAdapter = new DiveTypePickAdapter(this, mDiveTypePickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mDiveTypePickList.size() == MyConstants.ZERO_I) {
                mDiveTypePickAdapter.setDiveType(mDiveType);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mDiveTypePickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listener for the FAB
        mBinding.fabDiveType.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DiveTypeActivity.class);
            DiveType diveType = new DiveType();
            diveType.setDiveType("");
            intent.putExtra(MyConstants.DIVE_TYPE, diveType);
            addLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the Diver in the collection
        mPosition = mDiveTypePickList.indexOf(mDiveType);

        if (mPosition == -1 && mDiveTypePickList.size() >= 1) {
            // Can't find the Cylinder Type
            // Select first row
            mPosition = 0;
        }

        if (mDiveTypePickAdapter.getDiveTypePickList().size() >= 1) {
            // There is at least one Dive Type in the collection
            // Scroll to the Dive Type
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mDiveTypePickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        if (mDiveType.getInMultiEditMode()) {
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
            mDiveType = mDiveTypePickAdapter.getDiveType();
            Intent intent = new Intent(this, DiveTypeActivity.class);
            intent.putExtra(MyConstants.DIVE_TYPE, mDiveType);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_dive_type_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // Going back to SacRmvActivity
            if (mDiveType.getInMultiEditMode()) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabDiveType.setVisibility(View.VISIBLE);
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
        if (mDiveType.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabDiveType.setVisibility(View.VISIBLE);
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
                    // Get the newly ADDED DiveType from the DiveType activity
                    DiveType diveType;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        diveType = data.getParcelableExtra(MyConstants.DIVE_TYPE,DiveType.class);
                    } else {
                        diveType = data.getParcelableExtra(MyConstants.DIVE_TYPE);
                    }
                    // The DiveType has already been added to the Database
                    // Need to add it to the recyclerView
                    mDiveTypePickAdapter.addDiveType(diveType);
                    mDiveType = diveType;
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED DiveType from the DiveType activity
                    DiveType diveType;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        diveType = data.getParcelableExtra(MyConstants.DIVE_TYPE,DiveType.class);
                    } else {
                        diveType = data.getParcelableExtra(MyConstants.DIVE_TYPE);
                    }
                    // The DiveType has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    mDiveTypePickAdapter.modifyDiveType(diveType);
                    mDiveType = diveType;
                }
            });

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mDiveType.setInMultiEditMode(mInMultiEditMode);
        if (mDiveType.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add DiveType floating button
            mBinding.fabDiveType.setVisibility(View.INVISIBLE);
            // Showing Checkboxes on all DiveType items
            for (int i = 0; i < mDiveTypePickList.size(); i++) {
                DiveType diveType = mDiveTypePickList.get(i);
                diveType.setVisible(true);
                if (position - HEADER_OFFSET == i) {
                    diveType.setChecked(true);
                }
            }
            mDiveTypePickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mDiveTypesToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add DiveType floating button
            mBinding.fabDiveType.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all DiveType items
            for (int i = 0; i < mDiveTypePickList.size(); i++) {
                DiveType diveType = mDiveTypePickList.get(i);
                diveType.setVisible(false);
                diveType.setChecked(false);
            }
            mDiveTypePickAdapter.notifyDataSetChanged();
            mDiveTypesToDelete = 1;
            mDiveTypePickAdapter.setMultiEditMode(false);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mDiveTypesToDelete = 0;
        } else {
            mDiveTypesToDelete = mDiveTypePickList.size();
        }
        for (int i = 0; i < mDiveTypePickList.size(); i++) {
            DiveType mDiveType = mDiveTypePickList.get(i);
            mDiveType.setChecked(checked);
            if (checked) {
                mDiveTypesToDelete++;
            } else {
                mDiveTypesToDelete--;
            }
        }
        mDiveTypePickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mDiveTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mDiveTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countDiveTypes(Boolean checked) {
        if (checked) {
            mDiveTypesToDelete++;
        } else {
            mDiveTypesToDelete--;
        }
        mAppTitleCount = String.valueOf(mDiveTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mDiveTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mDiveTypesToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mDiveTypesToDelete + " " + getString(R.string.msg_dive_type_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mDiveTypePickList.size() - 1;
                        String successTypes;
                        String failedTypes;
                        StringBuilder sbSuccessType = new StringBuilder();
                        StringBuilder sbFailedType = new StringBuilder();
                        mAirDa.openWithFKConstraintsEnabled();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            DiveType diveType = mDiveTypePickList.get(position);
                            if (diveType.getChecked()) {
                                diveType = mDiveTypePickAdapter.getDiveType(position);
                                Integer rc = mAirDa.deleteDiveType(diveType.getDiveType());
                                if (rc.equals(MyConstants.ZERO_I)) {
                                    // Delete was successful
                                    mDiveTypePickAdapter.deleteDiveType(position);
                                    mDiveTypesToDelete--;
                                    sbSuccessType.insert(0, ", ");
                                    sbSuccessType.insert(0, diveType.getDiveType());
                                    mDiveType.setHasDataChanged(true);
                                } else {
                                    // Delete failed
                                    sbFailedType.insert(0,", ");
                                    sbFailedType.insert(0, diveType.getDiveType());
                                }
                            }
                        }
                        mAirDa.close();
                        if (mDiveTypePickList.size() > 0) {
                            // Set the current position in the Adapter
                            mDiveTypePickAdapter.setSelectedPosition(0);
                            // Scroll to the DiveType, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mDiveTypePickAdapter.notifyDataSetChanged();

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
                        String message = String.format(res.getString(R.string.msg_delete_fk_constraint),res.getString(R.string.mn_dive_type),successTypes,failedTypes);
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
                        mBinding.fabDiveType.setVisibility(View.VISIBLE);
                        setVisibility(0,false);
                    }

                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added DiveType
        // The screen does not scroll if the newly added DiveType is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setDiveType(DiveType diveType) {mDiveType = diveType;}
}
