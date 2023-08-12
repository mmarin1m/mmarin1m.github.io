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

import ca.myairbuddyandi.databinding.CylinderTypePickActivityBinding;

/**
 * Created by Michel on 2017-08-15.
 * Holds all of the logic for the CylinderTypePickActivity class
 */

public class CylinderTypePickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CylinderTypePickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private ArrayList<CylinderType> mCylinderTypePickList = new ArrayList<>();
    private CylinderType mCylinderType = new CylinderType();
    private final AirDA mAirDa = new AirDA(this);
    private CylinderTypePickActivityBinding mBinding = null;
    private CylinderTypePickAdapter mCylinderTypePickAdapter;
    private CharSequence mAppTitleCount;
    private int mCylinderTypesToDelete = 1;
    private int mPosition;
    private String mOriginalTitle;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.cylinder_type_pick_activity);

        //Get data for the CylinderTypes
        mAirDa.open();
        mCylinderTypePickList = mAirDa.getAllCylinderTypes();

        if (mCylinderTypePickList.size() > 0) {
            mCylinderType = mCylinderTypePickList.get(0);
        }

        // Create and load the data in the Recycler View Adapter
        if (mCylinderTypePickAdapter == null) {
            mCylinderTypePickAdapter = new CylinderTypePickAdapter(this, mCylinderTypePickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mCylinderTypePickList.size() == MyConstants.ZERO_I) {
                mCylinderTypePickAdapter.setCylinderType(mCylinderType);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mCylinderTypePickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listener for the FAB
        mBinding.fabCylinderType.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CylinderTypeActivity.class);
            CylinderType cylinderType = new CylinderType();
            cylinderType.setCylinderType("");
            intent.putExtra(MyConstants.CYLINDER_TYPE, cylinderType);
            addLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the Diver in the collection
        mPosition = mCylinderTypePickList.indexOf(mCylinderType);

        if (mPosition == -1 && mCylinderTypePickList.size() >= 1) {
            // Can't find the Cylinder Type
            // Select first row
            mPosition = 0;
        }

        if (mCylinderTypePickAdapter.getCylinderTypePickList().size() >= 1) {
            // There is at least one Cylinder Type in the collection
            // Scroll to the Cylinder Type
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mCylinderTypePickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        if (mCylinderType.getInMultiEditMode()) {
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
            mCylinderType = mCylinderTypePickAdapter.getCylinderType();
            Intent intent = new Intent(this, CylinderTypeActivity.class);
            intent.putExtra(MyConstants.CYLINDER_TYPE, mCylinderType);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_cylinder_type_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mCylinderType.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabCylinderType.setVisibility(View.VISIBLE);
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
        if (mCylinderType.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabCylinderType.setVisibility(View.VISIBLE);
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
    @SuppressLint("NewApi")
    ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED CylinderType from the CylinderType activity
                    CylinderType cylinderType;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        cylinderType = data.getParcelableExtra(MyConstants.CYLINDER_TYPE,CylinderType.class);
                    } else {
                        cylinderType = data.getParcelableExtra(MyConstants.CYLINDER_TYPE);
                    }
                    // The CylinderType has already been added to the Database
                    // Need to add it to the recyclerView
                    mCylinderTypePickAdapter.addCylinderType(cylinderType);
                    mCylinderType = cylinderType;
                    // Closes the database for CylinderType activity
                    // Because it can be also called from Cylinder activity
                    mAirDa.close();
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    @SuppressLint("NewApi")
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED CylinderType from the CylinderType activity
                    CylinderType cylinderType;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        cylinderType = data.getParcelableExtra(MyConstants.CYLINDER_TYPE,CylinderType.class);
                    } else {
                        cylinderType = data.getParcelableExtra(MyConstants.CYLINDER_TYPE);
                    }
                    // The CylinderType has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    mCylinderTypePickAdapter.modifyCylinderType(cylinderType);
                    mCylinderType = cylinderType;
                    // Closes the database for CylinderType activity
                    // Because it can be also called from Cylinder activity
                    mAirDa.close();
                }
            });

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mCylinderType.setInMultiEditMode(mInMultiEditMode);
        if (mCylinderType.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add CylinderType floating button
            mBinding.fabCylinderType.setVisibility(View.INVISIBLE);
            // Showing Checkboxes on all CylinderType items
            for (int i = 0; i < mCylinderTypePickList.size(); i++) {
                CylinderType cylinderType = mCylinderTypePickList.get(i);
                cylinderType.setVisible(true);
                if (position - HEADER_OFFSET == i) {
                    cylinderType.setChecked(true);
                }
            }
            mCylinderTypePickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mCylinderTypesToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add CylinderType floating button
            mBinding.fabCylinderType.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all CylinderType items
            for (int i = 0; i < mCylinderTypePickList.size(); i++) {
                CylinderType cylinderType = mCylinderTypePickList.get(i);
                cylinderType.setVisible(false);
                cylinderType.setChecked(false);
            }
            mCylinderTypePickAdapter.notifyDataSetChanged();
            mCylinderTypesToDelete = 1;
            mCylinderTypePickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mCylinderTypesToDelete = 0;
        } else {
            mCylinderTypesToDelete = mCylinderTypePickList.size();
        }
        for (int i = 0; i < mCylinderTypePickList.size(); i++) {
            CylinderType mCylinderType = mCylinderTypePickList.get(i);
            mCylinderType.setChecked(checked);
            if (checked) {
                mCylinderTypesToDelete++;
            } else {
                mCylinderTypesToDelete--;
            }
        }
        mCylinderTypePickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mCylinderTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mCylinderTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countCylinderTypes(Boolean checked) {
        if (checked) {
            mCylinderTypesToDelete++;
        } else {
            mCylinderTypesToDelete--;
        }
        mAppTitleCount = String.valueOf(mCylinderTypesToDelete);
        if (getSupportActionBar() != null) {
            if (mCylinderTypesToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public
    void deleteMultiMode() {
        if (mCylinderTypesToDelete > 0) {
            if (mCylinderTypesToDelete < mCylinderTypePickList.size()) {
                new AlertDialog.Builder(this)
                        .setMessage(mCylinderTypesToDelete + " " + getString(R.string.msg_cylinder_type_will_be_deleted))
                        .setCancelable(false)
                        .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                            // Delete logic
                            int itemCount = mCylinderTypePickList.size() - 1;
                            String successTypes;
                            String failedTypes;
                            StringBuilder sbSuccessType = new StringBuilder();
                            StringBuilder sbFailedType = new StringBuilder();
                            mAirDa.openWithFKConstraintsEnabled();
                            for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                                CylinderType cylinderType = mCylinderTypePickList.get(position);
                                if (cylinderType.getChecked()) {
                                    cylinderType = mCylinderTypePickAdapter.getCylinderType(position);
                                    Integer rc = mAirDa.deleteCylinderType(cylinderType.getCylinderType());
                                    if (rc.equals(MyConstants.ZERO_I)) {
                                        // Delete was successful
                                        mCylinderTypePickAdapter.deleteCylinderType(position);
                                        mCylinderTypesToDelete--;
                                        sbSuccessType.insert(0, ", ");
                                        sbSuccessType.insert(0, cylinderType.getCylinderType());
                                        mCylinderType.setHasDataChanged(true);
                                    } else {
                                        // Delete failed
                                        sbFailedType.insert(0,", ");
                                        sbFailedType.insert(0, cylinderType.getCylinderType());
                                    }
                                }
                            }
                            mAirDa.close();
                            if (mCylinderTypePickList.size() > 0) {
                                // Set the current position in the Adapter
                                mCylinderTypePickAdapter.setSelectedPosition(0);
                                // Scroll to the CylinderType, it might be far down the screen
                                mBinding.recycler.smoothScrollToPosition(0);
                            }
                            mCylinderTypePickAdapter.notifyDataSetChanged();

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
                            String message = String.format(res.getString(R.string.msg_delete_fk_constraint),res.getString(R.string.mn_cylinder_type),successTypes,failedTypes);
                            showDeleteResults(message);

                            dialog.dismiss();
                        })
                        .setNegativeButton(R.string.button_cancel, null)
                        .show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.msg_cylinder_type_must_keep_one)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void showDeleteResults(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(mOriginalTitle);
                        mBinding.fabCylinderType.setVisibility(View.VISIBLE);
                        setVisibility(0,false);
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added CylinderType
        // The screen does not scroll if the newly added CylinderType is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setCylinderType(CylinderType cylinderType) {
        mCylinderType = cylinderType;
    }
}
