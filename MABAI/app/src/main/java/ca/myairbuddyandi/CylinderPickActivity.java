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

import ca.myairbuddyandi.databinding.CylinderPickActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the CylinderPickActivity class
 */

public class CylinderPickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CylinderPickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<CylinderPick> mCylinderPickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private CylinderPick mCylinderPick = new CylinderPick();
    private CylinderPickActivityBinding mBinding = null;
    private CylinderPickAdapter mCylinderPickAdapter;
    private int mCylindersToDelete = 1;
    private int mPosition;
    private final MyDialogs mDialogs = new MyDialogs();
    private String mOriginalTitle;

    // End of variables

    @SuppressLint("NewApi")
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.cylinder_pick_activity);

        if (savedInstanceState != null) {
            // 2nd time in
            // CylinderPick
            mCylinderPick = savedInstanceState.getParcelable(MyConstants.PICK_A_CYLINDER);

            // RecyclerView
            mCylinderPickList = (ArrayList<CylinderPick>) savedInstanceState.getSerializable("RECYCLER_DATA");

            Parcelable mRecyclerState = savedInstanceState.getParcelable(MyConstants.LIST_STATE);
            Objects.requireNonNull(mBinding.recycler.getLayoutManager()).onRestoreInstanceState(mRecyclerState);

            if (mCylinderPickAdapter == null) {
                mCylinderPickAdapter = new CylinderPickAdapter(this, mCylinderPickList);
                // If the list is empty, make sure there is a valid POJO in the adapter
                if (mCylinderPickList.size() == MyConstants.ZERO_I) {
                    mCylinderPickAdapter.setCylinderPick(mCylinderPick);
                }
            }

            mBinding.recycler.setAdapter(mCylinderPickAdapter);
            mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
            mBinding.recycler.setHasFixedSize(true);

        } else {
            // 1st time in
            // Get the data from the Intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mCylinderPick = getIntent().getParcelableExtra(MyConstants.PICK_A_CYLINDER,CylinderPick.class);
            } else {
                mCylinderPick = getIntent().getParcelableExtra(MyConstants.PICK_A_CYLINDER);
            }

            //Get data for the Cylinders
            mAirDa.open();
            mCylinderPickList = mAirDa.getAllCylindersByDiver(mCylinderPick.getDiverNo());
        }

        // Create and load the data in the Recycler View Adapter
        if (mCylinderPickAdapter == null) {
            mCylinderPickAdapter = new CylinderPickAdapter(this, mCylinderPickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mCylinderPickList.size() == MyConstants.ZERO_I) {
                mCylinderPickAdapter.setCylinderPick(mCylinderPick);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mCylinderPickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Try to find the CylinderPick in the collection
        mPosition = mCylinderPickList.indexOf(mCylinderPick);

        if (mPosition == -1 && mCylinderPickList.size() >= 1) {
            // Can't find the Cylinder
            // Select first row
            mPosition = 0;
        }

        if (mCylinderPickAdapter.getCylinderPickList().size() >= 1) {
            // There is at least one Cylinder in the collection
            // Scroll to the Cylinder, it might be far down the screen
            mBinding.recycler.smoothScrollToPosition(mPosition + 1);
            // Set the current position in the Adapter
            mCylinderPickAdapter.setSelectedPosition(mPosition + 1);

            // Wait and perform a Click
            mBinding.recycler.postDelayed(() -> {
                try {
                    Objects.requireNonNull(mBinding.recycler.findViewHolderForAdapterPosition(mPosition + HEADER_OFFSET)).itemView.performClick();
                } catch (Exception e) {
                    // Do nothing
                }
            }, MyConstants.DELAY_MILLI_SECONDS);
        }

        // Set the listener for the Cancel button
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mCylinderPick.getHasDataChanged()) {
                mDialogs.confirm(CylinderPickActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        // Set the listener for the Pick button
        mBinding.pickButton.setOnClickListener(view -> {
            // Going back to GrouppActivity
            if (mCylinderPickList.size() > 0) {
                Intent intent = new Intent();
                mCylinderPick = mCylinderPickAdapter.getCylinderPick();
                intent.putExtra(MyConstants.PICK_A_CYLINDER, mCylinderPick);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        // Set the listener for the FAB button
        mBinding.fabCylinder.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CylinderActivity.class);
            Cylinder cylinder = new Cylinder();
            cylinder.setCylinderNo(MyConstants.ZERO_L);
            cylinder.setDiverNo(mCylinderPick.getDiverNo());
            cylinder.setVolume(MyConstants.ZERO_L.doubleValue());
            cylinder.setRatedPressure(MyConstants.ZERO_D);
            intent.putExtra(MyConstants.CYLINDER, cylinder);
            addLauncher.launch(intent);
        });

        // Set the listener for the Search
        mBinding.searchView.setOnSearchClickListener(v -> mBinding.fabCylinder.setVisibility(View.INVISIBLE));

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                //Filter as typing
                mCylinderPickAdapter.getFilter().filter(query);
                return false;
            }
        });

        mBinding.searchView.setOnCloseListener(() -> {
            mBinding.fabCylinder.setVisibility(View.VISIBLE);
            return false;
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Work with the list in the Adapter as it might be Filtered Out
        // Try to find the Cylinder in the collection
        mPosition = mCylinderPickAdapter.getCylinderPickPosition(mCylinderPick);

        if (mPosition == -1 && mCylinderPickAdapter.getCylinderPickList().size() >= 1) {
            // Can't find the Diver
            // Select first row
            mPosition = 0;
        }

        if (mCylinderPickAdapter.getCylinderPickList().size() >= 1) {
            // There is at least one Cylinder in the collection
            // Scroll to the Cylinder
            mBinding.recycler.smoothScrollToPosition(mPosition + 1);
            // Set the current position in the Adapter
            mCylinderPickAdapter.setSelectedPosition(mPosition + 1);
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

        if (mCylinderPick.getInMultiEditMode()) {
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
            mCylinderPick = mCylinderPickAdapter.getCylinderPick();
            Intent intent = new Intent(this, CylinderActivity.class);
            Cylinder cylinder = new Cylinder();
            cylinder.setCylinderNo(mCylinderPick.getCylinderNo());
            intent.putExtra(MyConstants.CYLINDER, cylinder);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_cylinder_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mCylinderPick.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabCylinder.setVisibility(View.VISIBLE);
                    setVisibility(0,false);
                    return true;
                }
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
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
        if (mCylinderPick.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabCylinder.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            // Going back to GrouppActivity
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // GrouppPick
        Parcelable myCylinderPickState = mCylinderPick;
        outState.putParcelable(MyConstants.PICK_A_CYLINDER, myCylinderPickState);

        // RecyclerView
        Serializable recyclerData = mCylinderPickAdapter.getCylinderPickList();
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
                    // Get the newly ADDED Cylinder from the Cylinder activity
                    Cylinder cylinder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        cylinder = data.getParcelableExtra(MyConstants.CYLINDER,Cylinder.class);
                    } else {
                        cylinder = data.getParcelableExtra(MyConstants.CYLINDER);
                    }
                    // The Cylinder has already been added to the Database
                    // Need to add it to the recyclerView
                    CylinderPick cylinderPick = new CylinderPick();
                    assert cylinder != null;
                    cylinderPick.setDiverNo(cylinder.getDiverNo());
                    cylinderPick.setCylinderNo(cylinder.getCylinderNo());
                    cylinderPick.setCylinderType(cylinder.getCylinderType());
                    cylinderPick.setVolume(cylinder.getVolume());
                    cylinderPick.setUsageType(cylinder.getUsageType());
                    cylinderPick.setRatedPressure(cylinder.getRatedPressure());
                    cylinderPick.setGroupDescription(getString(R.string.code_orphan));
                    mCylinderPickAdapter.addCylinder(cylinderPick);
                    mCylinderPick = cylinderPick;
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED Cylinder from the Cylinder activity
                    Cylinder cylinder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        cylinder = data.getParcelableExtra(MyConstants.CYLINDER,Cylinder.class);
                    } else {
                        cylinder = data.getParcelableExtra(MyConstants.CYLINDER);
                    }
                    // The Cylinder has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    CylinderPick cylinderPick = new CylinderPick();
                    assert cylinder != null;
                    cylinderPick.setDiverNo(cylinder.getDiverNo());
                    cylinderPick.setCylinderNo(cylinder.getCylinderNo());
                    cylinderPick.setCylinderType(cylinder.getCylinderType());
                    cylinderPick.setVolume(cylinder.getVolume());
                    cylinderPick.setRatedPressure(cylinder.getRatedPressure());
                    cylinderPick.setUsageType(cylinder.getUsageType());
                    mCylinderPickAdapter.modifyCylinder(cylinderPick);
                    mCylinderPick = cylinderPick;
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
        mCylinderPick.setInMultiEditMode(mInMultiEditMode);
        if (mCylinderPick.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add Cylinder floating button
            mBinding.fabCylinder.setVisibility(View.INVISIBLE);
            // Showing Checkboxes on all Cylinder items
            for (int i = 0; i < mCylinderPickList.size(); i++) {
                CylinderPick cylinderPick = mCylinderPickList.get(i);
                cylinderPick.setVisible(true);
                if (position - HEADER_OFFSET == i) {
                    cylinderPick.setChecked(true);
                }
            }
            mCylinderPickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mCylindersToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add Cylinder floating button
            mBinding.fabCylinder.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all Cylinder items
            for (int i = 0; i < mCylinderPickList.size(); i++) {
                CylinderPick cylinderPick = mCylinderPickList.get(i);
                cylinderPick.setVisible(false);
                cylinderPick.setChecked(false);
            }
            mCylinderPickAdapter.notifyDataSetChanged();
            mCylindersToDelete = 1;
            mCylinderPickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mCylindersToDelete = 0;
        } else {
            mCylindersToDelete = mCylinderPickList.size();
        }
        for (int i = 0; i < mCylinderPickList.size(); i++) {
            CylinderPick mCylinderPick = mCylinderPickList.get(i);
            mCylinderPick.setChecked(checked);
            if (checked) {
                mCylindersToDelete++;
            } else {
                mCylindersToDelete--;
            }
        }
        mCylinderPickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mCylindersToDelete);
        if (getSupportActionBar() != null) {
            if (mCylindersToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countCylinders(Boolean checked) {
        if (checked) {
            mCylindersToDelete++;
        } else {
            mCylindersToDelete--;
        }
        mAppTitleCount = String.valueOf(mCylindersToDelete);
        if (getSupportActionBar() != null) {
            if (mCylindersToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mCylindersToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mCylindersToDelete + " " + getString(R.string.msg_cylinders_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mCylinderPickList.size() - 1;
                        String successTypes;
                        String failedTypes;
                        StringBuilder sbSuccessType = new StringBuilder();
                        StringBuilder sbFailedType = new StringBuilder();
                        mAirDa.openWithFKConstraintsEnabled();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            CylinderPick cylinderPick = mCylinderPickList.get(position);
                            if (cylinderPick.getChecked()) {
                                cylinderPick = mCylinderPickAdapter.getCylinderPick(position);
                                Integer rc = mAirDa.deleteCylinder(cylinderPick.getCylinderNo());
                                if (rc.equals(MyConstants.ZERO_I)) {
                                    // Delete was successful
                                    mCylinderPickAdapter.deleteCylinder(position);
                                    mCylindersToDelete--;
                                    sbSuccessType.insert(0, ", ");
                                    sbSuccessType.insert(0, cylinderPick.getCylinderType());
                                    mCylinderPick.setHasDataChanged(true);
                                } else {
                                    // Delete failed
                                    sbFailedType.insert(0,", ");
                                    sbFailedType.insert(0, cylinderPick.getCylinderType());
                                }
                            }
                        }
                        // Do not close the DB connection
                        // The DiveActivity controls the DataBase transaction
                        if (mCylinderPickList.size() > 0) {
                            // Set the current position in the Adapter
                            mCylinderPickAdapter.setSelectedPosition(0);
                            // Scroll to the SegmentType, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mCylinderPickAdapter.notifyDataSetChanged();

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
                        mBinding.fabCylinder.setVisibility(View.VISIBLE);
                        setVisibility(0,false);
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added CylinderPick
        // The screen does not scroll if the newly added CylinderPick is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setCylinderPick(CylinderPick cylinderPick) {
        mCylinderPick = cylinderPick;
    }
}