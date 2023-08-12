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

import ca.myairbuddyandi.databinding.UsageTypePickActivityBinding;

/**
 * Created by Michel on 2017-08-15.
 * Holds all the logic for the UsageTypePickActivity class
 */

public class UsageTypePickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "UsageTypePickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mPosition;
    private int mUsageTypesToDelete = 0;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<UsageType> mUsageTypePickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private String mOriginalTitle;
    private UsageType mUsageType = new UsageType();
    private UsageTypePickActivityBinding mBinding = null;
    private UsageTypePickAdapter mUsageTypePickAdapter;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.usage_type_pick_activity);

        //Get data for the UsageTypes
        mAirDa.open();
        mUsageTypePickList = mAirDa.getAllUsageTypes();

        if (mUsageTypePickList.size() > 0) {
            mUsageType = mUsageTypePickList.get(0);
        }

        // Create and load the data in the Recycler View Adapter
        if (mUsageTypePickAdapter == null) {
            mUsageTypePickAdapter = new UsageTypePickAdapter(this, mUsageTypePickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mUsageTypePickList.size() == MyConstants.ZERO_I) {
                mUsageTypePickAdapter.setUsageType(mUsageType);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mUsageTypePickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listener for the FAB
        mBinding.fabUsageType.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UsageTypeActivity.class);
            UsageType usageType = new UsageType();
            usageType.setUsageType("");
            intent.putExtra(MyConstants.USAGE_TYPE, usageType);
            addLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the Diver in the collection
        mPosition = mUsageTypePickList.indexOf(mUsageType);

        if (mPosition == -1 && mUsageTypePickList.size() >= 1) {
            // Can't find the Cylinder Type
            // Select first row
            mPosition = 0;
        }

        if (mUsageTypePickAdapter.getUsageTypePickList().size() >= 1) {
            // There is at least one Usage Type in the collection
            // Scroll to the Usage Type
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mUsageTypePickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        if (mUsageType.getInMultiEditMode()) {
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
            mUsageType = mUsageTypePickAdapter.getUsageType();
            Intent intent = new Intent(this, UsageTypeActivity.class);
            intent.putExtra(MyConstants.USAGE_TYPE, mUsageType);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_usage_type_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // Go back to Single Edit Mode
            if (mUsageType.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabUsageType.setVisibility(View.VISIBLE);
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
        if (mUsageType.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabUsageType.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
            finish();
        }
    }

    // My functions
    ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED UsageType from the UsageType activity
                    UsageType usageType = data.getParcelableExtra(MyConstants.USAGE_TYPE);
                    // The UsageType has already been added to the Database
                    // Need to add it to the recyclerView
                    mUsageTypePickAdapter.addUsageType(usageType);
                    mUsageType = usageType;
                }
            });
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED UsageType from the UsageType activity
                    UsageType usageType = data.getParcelableExtra(MyConstants.USAGE_TYPE);
                    // The UsageType has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    mUsageTypePickAdapter.modifyUsageType(usageType);
                    mUsageType = usageType;
                }
            });

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mUsageType.setInMultiEditMode(mInMultiEditMode);
        if (mUsageType.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add DiveType floating button
            mBinding.fabUsageType.setVisibility(View.INVISIBLE);
            // Only set the Multi Mode of there are rows to delete, other than the System Defined values
            // Showing Checkboxes on all usageType items
            for (int i = 0; i < mUsageTypePickList.size(); i++) {
                UsageType usageType = mUsageTypePickList.get(i);
                if (usageType.getSystemDefined().equals("N")) {
                    usageType.setVisible(View.VISIBLE);
                } else {
                    // The Checkbox is INVISIBLE, the user CANNOT delete it
                    usageType.setVisible(View.INVISIBLE);
                }
                if (position - HEADER_OFFSET == i && usageType.getSystemDefined().equals("N")) {
                    usageType.setChecked(true);
                    mUsageTypesToDelete++;
                }
            }
            mUsageTypePickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mUsageTypesToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add usageType floating button
            mBinding.fabUsageType.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all usageType items
            for (int i = 0; i < mUsageTypePickList.size(); i++) {
                UsageType usageType = mUsageTypePickList.get(i);
                usageType.setVisible(View.GONE);
                usageType.setChecked(false);
            }
            mUsageTypePickAdapter.notifyDataSetChanged();
            mUsageTypesToDelete = 0;
            mUsageTypePickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mUsageTypesToDelete = 0;
        } else {
            mUsageTypesToDelete = mUsageTypePickList.size();
        }
        for (int i = 0; i < mUsageTypePickList.size(); i++) {
            UsageType usageType = mUsageTypePickList.get(i);
            if (usageType.getSystemDefined().equals("N")) {
                usageType.setChecked(checked);
                if (checked) {
                    mUsageTypesToDelete++;
                } else {
                    mUsageTypesToDelete--;
                }
            } else {
                if (!checked) {
                    mUsageTypesToDelete--;
                }
            }
        }
        mUsageTypePickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mUsageTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mUsageTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countUsageTypes(Boolean checked) {
        if (checked) {
            mUsageTypesToDelete++;
        } else {
            mUsageTypesToDelete--;
        }
        mAppTitleCount = String.valueOf(mUsageTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mUsageTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mUsageTypesToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mUsageTypesToDelete + " " + getString(R.string.msg_groupp_type_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mUsageTypePickList.size() - 1;
                        String successTypes;
                        String failedTypes;
                        StringBuilder sbSuccessType = new StringBuilder();
                        StringBuilder sbFailedType = new StringBuilder();
                        mAirDa.openWithFKConstraintsEnabled();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            UsageType usageType = mUsageTypePickList.get(position);
                            if (usageType.getChecked()) {
                                usageType = mUsageTypePickAdapter.getUsageType(position);
                                Integer rc = mAirDa.deleteUsageType(usageType.getUsageType());
                                if (rc.equals(MyConstants.ZERO_I)) {
                                    // Delete was successful
                                    mUsageTypePickAdapter.deleteUsageType(position);
                                    mUsageTypesToDelete--;
                                    sbSuccessType.insert(0, ", ");
                                    sbSuccessType.insert(0, usageType.getUsageType());
                                    mUsageType.setHasDataChanged(true);
                                } else {
                                    // Delete failed
                                    sbFailedType.insert(0,", ");
                                    sbFailedType.insert(0, usageType.getUsageType());
                                }
                            }
                        }
                        mAirDa.close();
                        if (mUsageTypePickList.size() > 0) {
                            // Set the current position in the Adapter
                            mUsageTypePickAdapter.setSelectedPosition(0);
                            // Scroll to the usageType, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mUsageTypePickAdapter.notifyDataSetChanged();

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
                        mBinding.fabUsageType.setVisibility(View.VISIBLE);
                        setVisibility(0,false);
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added UsageType
        // The screen does not scroll if the newly added UsageType is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setUsageType(UsageType usageType) {mUsageType = usageType;}
}
