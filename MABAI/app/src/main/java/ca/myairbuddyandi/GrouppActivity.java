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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import ca.myairbuddyandi.databinding.GrouppActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the GrouppActivity class
 * AKA Edit an Equipment Group
 */

public class GrouppActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "GrouppActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected
    protected ArrayList<Integer> mGrouppCylinderListPosition = new ArrayList<>();
    protected ArrayList<GrouppCylinder> mGrouppCylinderList = new ArrayList<>();

    // Private
    private int mFirstBGCylinder;
    private int mGrouppCylinderToDelete = 1;
    private int mPosition;
    private final AirDA mAirDa = new AirDA(this);
    private CharSequence mAppTitleCount;
    private final DiverDiveGroupCyl mDiverDiveGroupCyl = new DiverDiveGroupCyl();
    private Groupp mGroupp = null;
    private GrouppActivityBinding mBinding = null;
    private GrouppCylinder mGrouppCylinder = new GrouppCylinder();
    private GrouppCylinderAdapter mGrouppCylinderAdapter;
    private MyCalc mMyCalc;
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.groupp_activity);

        mAirDa.open();

        if (savedInstanceState != null) {
            // 2nd time in
            // Groupp
            mGroupp = savedInstanceState.getParcelable(MyConstants.GROUPP);

            if ((mGroupp != null ? mGroupp.getLogBookNo() : 0) != MyConstants.ZERO_I) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mGroupp.getLogBookNo());
                }
            }

            // Spinner
            // Set the data in the Spinner GrouppType
            ArrayList<GrouppType> grouppTypeList = mAirDa.getAllGroupTypes();
            ArrayAdapter<GrouppType> adapterGroupType = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, grouppTypeList);
            adapterGroupType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            mGroupp.setAdapterGroupType(adapterGroupType);
            mGroupp.setItemsGroupType(grouppTypeList);

            mBinding.spinnerType.setFocusable(true);
            mBinding.spinnerType.setFocusableInTouchMode(true);

            int spinnerPosition = savedInstanceState.getInt("SPINNER_POSITION");
            mGroupp.setGroupTypePosition(spinnerPosition);

            // Description
            String groupDescription = savedInstanceState.getString("GROUP_DESCRIPTION");
            mBinding.editTextDE.setText(groupDescription);

            // RecyclerView
            Serializable recyclerData = savedInstanceState.getSerializable("RECYCLER_DATA");
            mGrouppCylinderList = (ArrayList<GrouppCylinder>) recyclerData;

            Serializable recyclerPosition = savedInstanceState.getSerializable("RECYCLER_SPINNER_POSITION");
            mGrouppCylinderListPosition = (ArrayList<Integer>) recyclerPosition;

            Parcelable recyclerState = savedInstanceState.getParcelable(MyConstants.LIST_STATE);
            Objects.requireNonNull(mBinding.recycler.getLayoutManager()).onRestoreInstanceState(recyclerState);

            if (mGrouppCylinderAdapter == null) {
                mGrouppCylinderAdapter = new GrouppCylinderAdapter(this, mGrouppCylinderList);
                mGrouppCylinderAdapter.setGrouppCylinderListPosition(mGrouppCylinderListPosition);
                // If the list is empty, make sure there is a valid POJO in the adapter
                if (mGrouppCylinderList.size() == MyConstants.ZERO_I) {
                    mGrouppCylinderAdapter.setGrouppCylinder(mGrouppCylinder);
                }
            }

            // Set the Recycler View
            mBinding.recycler.setAdapter(mGrouppCylinderAdapter);
            mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
            mBinding.recycler.setHasFixedSize(true);
            mBinding.recycler.setNestedScrollingEnabled(false);

            // Binding
            mBinding.setGroupp(mGroupp);

        } else {
            // 1st time in
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mGroupp = getIntent().getParcelableExtra(MyConstants.GROUPP,Groupp.class);
            } else {
                mGroupp = getIntent().getParcelableExtra(MyConstants.GROUPP);
            }

            assert mGroupp != null;
            if (mGroupp.getLogBookNo() != MyConstants.ZERO_I) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mGroupp.getLogBookNo());
                }
            }

            // Spinner
            // Set the data in the Spinner GrouppType
            ArrayList<GrouppType> grouppTypeList = mAirDa.getAllGroupTypes();
            ArrayAdapter<GrouppType> adapterGroupType = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, grouppTypeList);
            adapterGroupType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            mGroupp.setAdapterGroupType(adapterGroupType);
            mGroupp.setItemsGroupType(grouppTypeList);

            if (mGroupp.getGroupNo().equals(MyConstants.ZERO_L)) {
                // Add mode (New Groupp)
                // Data must be in the Model first in order to bind
                // All data initialization must be done to the Model and not the View to avoid Data Changed Event
                // Set the default values
                // Group Type
                mGroupp.setGroupType(getString(R.string.code_default_groupp_type));
            }  else {
                // Edit mode
                // Get the data for the Group (single edit)
                mAirDa.getGroup(mGroupp.getGroupNo(),mGroupp);

                // Get the data for the Groupp Cylinder (List edit)
                mGrouppCylinderList = mAirDa.getAllGroupCylinderWUsage(mGroupp.getDiverNo(),mGroupp.getGroupNo());
            }

            mGroupp.setGroupTypeOriginal(mGroupp.getGroupType());

            // Binding
            mBinding.setGroupp(mGroupp);

            // Create and load the data in the Recycler View Adapter
            if (mGrouppCylinderAdapter == null) {
                mGrouppCylinderAdapter = new GrouppCylinderAdapter(this, mGrouppCylinderList);
                // If the list is empty, make sure there is a valid POJO in the adapter
                if (mGrouppCylinderList.size() == MyConstants.ZERO_I) {
                    mGrouppCylinderAdapter.setGrouppCylinder(mGrouppCylinder);
                }
            }

            // Set the Recycler View
            mBinding.recycler.setAdapter(mGrouppCylinderAdapter);
            mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
            mBinding.recycler.setHasFixedSize(true);
            mBinding.recycler.setNestedScrollingEnabled(false);

            mBinding.spinnerType.setFocusable(true);
            mBinding.spinnerType.setFocusableInTouchMode(true);

            requestFocus(mBinding.spinnerType);
            mBinding.spinnerType.requestFocus();
        }

        // Set the listeners
        mBinding.cancelButton.setOnClickListener(view -> {
            if (mGroupp.getHasDataChanged() || checkHasCylinderChanged()) {
                mDialogs.confirm(GrouppActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        mBinding.saveButton.setOnClickListener(view -> {
            // Validate data
            submitForm();
        });

        // Set the listener for the FAB
        mBinding.fabCylinder.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CylinderPickActivity.class);
            CylinderPick cylinderPick = new CylinderPick();
            cylinderPick.setCylinderNo(MyConstants.ZERO_L);
            cylinderPick.setRatedPressure(MyConstants.ZERO_D);
            cylinderPick.setDiverNo(mGroupp.getDiverNo());
            intent.putExtra(MyConstants.PICK_A_CYLINDER, cylinderPick);
            addLauncher.launch(intent);
        });

        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(this);
        } else {
            mMyCalc = new MyCalcMetric(this);
        }

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the GrouppPick in the collection
        mPosition = mGrouppCylinderList.indexOf(mGrouppCylinder);

        if (mPosition == -1 && mGrouppCylinderList.size() >= 1) {
            // Can't find the Groupp
            // Select first row
            mPosition = 0;
        }

        if (mGrouppCylinderAdapter.getGrouppCylinderList().size() >= 1) {
            // There is at least one Groupp Cylinder in the collection
            // Scroll to the Groupp Cylinder
            mBinding.recycler.smoothScrollToPosition(mPosition + 1);
            // Set the current position in the Adapter
            mGrouppCylinderAdapter.setSelectedPosition(mPosition + 1);

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

        if (mGroupp.getInMultiEditMode()) {
            deleteItem.setVisible(true);
            checkAll.setVisible(true);
            deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            checkAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            mBinding.saveButton.setText(R.string.lbl_blank);
            mBinding.saveButton.setEnabled(false);
            mBinding.saveButton.setAlpha(0.5f);
        } else {
            deleteItem.setVisible(false);
            checkAll.setVisible(false);
            deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            checkAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            mBinding.saveButton.setText(R.string.button_save);
            mBinding.saveButton.setEnabled(true);
            mBinding.saveButton.setAlpha(1.0f);
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
            mGrouppCylinder = mGrouppCylinderAdapter.getGrouppCylinder();
            Intent intent = new Intent(this, CylinderActivity.class);
            Cylinder cylinder = new Cylinder();
            cylinder.setCylinderNo(mGrouppCylinder.getCylinderNo());
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_groupp_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mGroupp.getHasDataChanged() || checkHasCylinderChanged()) {
                mDialogs.confirm(GrouppActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
                return true;
            } else if (mGroupp.getInMultiEditMode()) {
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
                finish();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        if (mGroupp.getHasDataChanged() || checkHasCylinderChanged()) {
            mDialogs.confirm(GrouppActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
        } else if (mGroupp.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabCylinder.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Groupp
        Parcelable myGrouppState = mGroupp;
        outState.putParcelable(MyConstants.GROUPP, myGrouppState);

        // Spinner
        outState.putInt("SPINNER_POSITION",mBinding.spinnerType.getSelectedItemPosition());

        outState.putString("SPINNER_GROUP_TYPE","SI");

        // Description
        outState.putString("GROUP_DESCRIPTION",mBinding.editTextDE.getText().toString().trim());

        // RecyclerView
        Serializable recyclerData = mGrouppCylinderAdapter.getGrouppCylinderList();
        outState.putSerializable("RECYCLER_DATA", recyclerData);

        Parcelable mRecyclerState = Objects.requireNonNull(mBinding.recycler.getLayoutManager()).onSaveInstanceState();
        outState.putParcelable(MyConstants.LIST_STATE,mRecyclerState);

        // Must save selected position of each Spinner in the list
        Serializable recyclerPosition = mGrouppCylinderAdapter.getGrouppCylinderListPosition();
        outState.putSerializable("RECYCLER_SPINNER_POSITION", recyclerPosition);

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
                    // Get the newly ADDED Cylinder from the Pick Cylinder activity
                    CylinderPick cylinderPick;
                    cylinderPick = data.getParcelableExtra(MyConstants.PICK_A_CYLINDER);
                    // Make sure the Cylinder is not already in the Group
                    assert cylinderPick != null;
                    if (isCylinderInGroup(cylinderPick.getCylinderNo())) {
                        showError(getString(R.string.msg_cylinder_already_in_group));
                    }
                    // The Cylinder has already been added to the Database
                    // Need to add it to the recyclerView
                    // Group is not yet created
                    GrouppCylinder grouppCylinder = new GrouppCylinder();

                    // Set the data in the Spinner UsageType
                    ArrayList<UsageType> usageTypeList = mAirDa.getAllUsageTypes();
                    ArrayAdapter<UsageType> adapterUsageType = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, usageTypeList);
                    adapterUsageType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    grouppCylinder.setItemsUsageType(usageTypeList);
                    grouppCylinder.setGroupNo(MyConstants.ZERO_L);
                    grouppCylinder.setDiverNo(mGroupp.getDiverNo());
                    grouppCylinder.setCylinderNo(cylinderPick.getCylinderNo());
                    grouppCylinder.setCylinderType(cylinderPick.getCylinderType());
                    grouppCylinder.setVolume(cylinderPick.getVolume());
                    grouppCylinder.setRatedPressure(cylinderPick.getRatedPressure());
                    grouppCylinder.setUsageType(getString(R.string.code_default_usage));
                    grouppCylinder.setUsageTypeOld(getString(R.string.code_default_usage));
                    grouppCylinder.setIsNew(MyConstants.YES);
                    grouppCylinder.setHasDataChanged(true);
                    mGrouppCylinderAdapter.addGrouppCylinder(grouppCylinder);
                    mGroupp.setHasDataChanged(true);
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
                    cylinder = data.getParcelableExtra(MyConstants.CYLINDER);
                    // The Cylinder has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    GrouppCylinder grouppCylinder = new GrouppCylinder();

                    // Set the data in the Spinner UsageType
                    ArrayList<UsageType> usageTypeList = mAirDa.getAllUsageTypes();
                    ArrayAdapter<UsageType> adapterUsageType = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, usageTypeList);
                    adapterUsageType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    grouppCylinder.setItemsUsageType(usageTypeList);
                    grouppCylinder.setGroupNo(mGroupp.getGroupNo());
                    grouppCylinder.setDiverNo(mGroupp.getDiverNo());
                    assert cylinder != null;
                    grouppCylinder.setCylinderNo(cylinder.getCylinderNo());
                    grouppCylinder.setCylinderType(cylinder.getCylinderType());
                    grouppCylinder.setVolume(cylinder.getVolume());
                    grouppCylinder.setRatedPressure(cylinder.getRatedPressure());
                    grouppCylinder.setUsageType(cylinder.getUsageType());
                    grouppCylinder.setUsageTypeOld(cylinder.getUsageType());
                    grouppCylinder.setIsNew(cylinder.getIsNew());
                    mGrouppCylinderAdapter.modifyGrouppCylinder(grouppCylinder);
                    mGroupp.setHasDataChanged(true);
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

    private void submitForm() {

        if (!validateDescription()) {
            return;
        }

        // Make sure there is at least 1 Bottom Gas cylinder
        if (!validateBottomGasCylinderPresence()) {
            return;
        }

        // Validate ratedPressure and volume to make sure all of the Bottom Gas are of the save Pressure and volume
        if (!validateSameVolumePressure()) {
            return;
        }

        // Make sure Group Type has the valid number of cylinders e.g. 1 Bottom Gas for Single (SI) and 2 Bottom Gas for Double or Sidemount (DO or SM)
        if (!validateNoCylinder()) {
            return;
        }

        // Make sure the new cylinder is of usageType of Deco Gas ("DG") or Emergency Gas ("EG")
        if (!validateUsageType()) {
            return;
        }

        // Save data
        if (mGroupp.getGroupNo().equals(MyConstants.ZERO_L)) {
            // Add mode
            // GROUPP
            mAirDa.createGroupp(mGroupp, false);
            // DIVER_DIVE_GROUP
            DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
            diverDiveGroup.setDiverNo(mGroupp.getDiverNo());
            diverDiveGroup.setDiveNo(mGroupp.getDiveNo());
            diverDiveGroup.setGroupNo(mGroupp.getGroupNo());
            diverDiveGroup.setSac(mMyCalc.getSacDefault());
            mAirDa.createDiverDiveGroup(diverDiveGroup);
            // GROUP_CYLINDER & DIVER_DIVE_GROUP_CYLINDER
            for (int i=0;i<mGrouppCylinderList.size();i++) {
                // GROUP_CYLINDER
                GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
                // CylinderNo is already present in grouppCylinder
                // Just need to set the GroupNo
                grouppCylinder.setGroupNo(mGroupp.getGroupNo());
                mAirDa.createGroupCylinder(grouppCylinder);
                // DIVER_DIVE_GROUP_CYLINDER
                mDiverDiveGroupCyl.setDiverNo(mGroupp.getDiverNo());
                mDiverDiveGroupCyl.setDiveNo(mGroupp.getDiveNo());
                mDiverDiveGroupCyl.setGroupNo(mGroupp.getGroupNo());
                mDiverDiveGroupCyl.setCylinderNo(grouppCylinder.getCylinderNo());
                // Set the Beginning Pressure to the Rated Pressure
                mDiverDiveGroupCyl.setBeginningPressure(grouppCylinder.getRatedPressure());
                // Set the Ending Pressure to 0
                // Ending Pressure must be read from the SPG
                mDiverDiveGroupCyl.setEndingPressure(MyConstants.ZERO_D);
                mDiverDiveGroupCyl.setO2(Integer.parseInt(getString(R.string.code_default_o2)));
                mDiverDiveGroupCyl.setN(Integer.parseInt(getString(R.string.code_default_n)));
                mDiverDiveGroupCyl.setHe(Integer.parseInt(getString(R.string.code_default_he)));
                mDiverDiveGroupCyl.setUsageType(getString(R.string.cd_blank));
                mAirDa.createDiverDiveGroupCylinder(mDiverDiveGroupCyl);
            }
        }  else {
            // Edit mode
            // GROUPP
            mAirDa.updateGroupp(mGroupp);
            // Three (3) things can change:
            // - Description
            // - Group Type
            // - Cylinder(s)
            // If only the Description changed then do not perform the rest of the deletes and updates
            // In other words, if the Group Type changed (e.g. Single to Double) or any of the cylinders changed
            // e.g. Changing rated pressure or volume, adding or removing a cylinder
            // e.g. Adding or removing a Pony bottles should not count
            if (!mGroupp.getGroupType().equals(mGroupp.getGroupTypeOriginal()) || checkHasCylinderChanged()) {
                mGroupp.setHasDataChanged(true);
                // Delete all of the GROUP_CYLINDER
                mAirDa.deleteGroupCylinderByGroupNo(mGroupp.getGroupNo());
                // Delete all of the DIVER_DIVE_GROUP for a given group_no
                // Deleting the DIVER_DIVE_GROUP also deletes the DIVER_DIVE_GROUP_CYLINDER by "ON DELETE CASCADE"
                mAirDa.deleteDiverDiveGroupByGroupNo(mGroupp.getGroupNo());
                // Recreate the information
                // DIVER_DIVE_GROUP
                DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
                diverDiveGroup.setDiverNo(mGroupp.getDiverNo());
                diverDiveGroup.setDiveNo(mGroupp.getDiveNo());
                diverDiveGroup.setGroupNo(mGroupp.getGroupNo());
                diverDiveGroup.setSac(mMyCalc.getSacDefault());
                mAirDa.createDiverDiveGroup(diverDiveGroup);
                // GROUP_CYLINDER & DIVER_DIVE_GROUP_CYLINDER
                for (int i = 0; i < mGrouppCylinderList.size(); i++) {
                    // GROUP_CYLINDER
                    GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
                    grouppCylinder.setGroupNo(mGroupp.getGroupNo());
                    mAirDa.createGroupCylinder(grouppCylinder);
                    // DIVER_DIVE_GROUP_CYLINDER
                    mDiverDiveGroupCyl.setDiverNo(mGroupp.getDiverNo());
                    mDiverDiveGroupCyl.setDiveNo(mGroupp.getDiveNo());
                    mDiverDiveGroupCyl.setGroupNo(mGroupp.getGroupNo());
                    mDiverDiveGroupCyl.setCylinderNo(grouppCylinder.getCylinderNo());
                    // Set the Beginning Pressure to the Rated Pressure
                    mDiverDiveGroupCyl.setBeginningPressure(grouppCylinder.getRatedPressure());
                    // Set the Ending Pressure to 0
                    // Ending Pressure must be read from the SPG
                    mDiverDiveGroupCyl.setEndingPressure(MyConstants.ZERO_D);
                    mDiverDiveGroupCyl.setO2(Integer.parseInt(getString(R.string.code_default_o2)));
                    mDiverDiveGroupCyl.setN(Integer.parseInt(getString(R.string.code_default_n)));
                    mDiverDiveGroupCyl.setHe(Integer.parseInt(getString(R.string.code_default_he)));
                    mDiverDiveGroupCyl.setUsageType(getString(R.string.cd_blank));
                    mAirDa.createDiverDiveGroupCylinder(mDiverDiveGroupCyl);
                }
            } else {
                // The specs have not changed for BOTTOM GAS and TRAVEL GAS
                // No BOTTOM GAS and TRAVEL GAS have been added
                // Maybe an EMERGENCY GAS (Pony) has been added or removed
                // Maybe an EMERGENCY GAS (Pony) has been modified
                // The easier way is to delete all EMERGENCY GAS and add it back
                // It is possible that there is no row to delete

                // Delete all of the GROUP_CYLINDER for EMERGENCY GAS
                mAirDa.deleteGroupCylinderByUsageType(mGroupp.getGroupNo(), MyConstants.EMERGENCY_GAS);

                // Delete all of the DIVER_DIVE_GROUP_CYLINDER for EMERGENCY GAS
                mAirDa.deleteDiverDiveGroupCylinderByDiverNoDiveNoGroupNoUsageType(mGroupp.getDiverNo(), mGroupp.getDiveNo(), mGroupp.getGroupNo(), MyConstants.EMERGENCY_GAS);

                for (int i = 0; i < mGrouppCylinderList.size(); i++) {
                    GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
                    if (grouppCylinder.getUsageType().equals(MyConstants.EMERGENCY_GAS)) {
                        // GROUP_CYLINDER
                        grouppCylinder.setGroupNo(mGroupp.getGroupNo());
                        mAirDa.createGroupCylinder(grouppCylinder);


                        // DIVER_DIVE_GROUP_CYLINDER
                        mDiverDiveGroupCyl.setDiverNo(mGroupp.getDiverNo());
                        mDiverDiveGroupCyl.setDiveNo(mGroupp.getDiveNo());
                        mDiverDiveGroupCyl.setGroupNo(mGroupp.getGroupNo());
                        mDiverDiveGroupCyl.setCylinderNo(grouppCylinder.getCylinderNo());
                        // Set the Beginning Pressure to the Rated Pressure
                        mDiverDiveGroupCyl.setBeginningPressure(grouppCylinder.getRatedPressure());
                        // Set the Ending Pressure to 0
                        // Ending Pressure must be read from the SPG
                        mDiverDiveGroupCyl.setEndingPressure(MyConstants.ZERO_D);
                        mDiverDiveGroupCyl.setO2(Integer.parseInt(getString(R.string.code_default_o2)));
                        mDiverDiveGroupCyl.setN(Integer.parseInt(getString(R.string.code_default_n)));
                        mDiverDiveGroupCyl.setHe(Integer.parseInt(getString(R.string.code_default_he)));
                        mDiverDiveGroupCyl.setUsageType(getString(R.string.cd_blank));
                        mAirDa.createDiverDiveGroupCylinder(mDiverDiveGroupCyl);
                    }
                }
            }
        }

        // Return the parcel object with the newly saved data
        Intent intent = new Intent();
        intent.putExtra(MyConstants.GROUPP,mGroupp);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateDescription() {
        // Required
        if (mBinding.editTextDE.getText().toString().trim().isEmpty()) {
            mBinding.editTextDE.setError(getString(R.string.msg_description));
            requestFocus(mBinding.editTextDE);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateBottomGasCylinderPresence() {
        // Required
        mFirstBGCylinder = findFirstBottomGasCylinder();
        if (mFirstBGCylinder == MyConstants.MINUS_ONE_I)  {
            showError(getString(R.string.msg_groupp_cylinder_presence));
            return false;
        } else {
            return true;
        }
    }

    private boolean validateSameVolumePressure() {
        // Required
        GrouppCylinder grouppCylinder = mGrouppCylinderList.get(mFirstBGCylinder);
        Double volume = grouppCylinder.getVolume();
        Double ratedPressure = grouppCylinder.getRatedPressure();
        boolean rc = true;

        for (int ii = mFirstBGCylinder; ii < mGrouppCylinderList.size(); ii++) {
            grouppCylinder = mGrouppCylinderList.get(ii);
            if (grouppCylinder.getUsageType().equals(MyConstants.BOTTOM_GAS)) {
                if (!volume.equals(grouppCylinder.getVolume()) || !ratedPressure.equals(grouppCylinder.getRatedPressure())) {
                    showError(getString(R.string.msg_groupp_cylinder_diff));
                    rc = false;
                    break;
                }
            }
        }
        return rc;
    }

    private boolean validateNoCylinder() {
        // Required
        GrouppCylinder grouppCylinder;
        int noCylinder = MyConstants.ZERO_I;
        boolean rc = true;

        for (int ii = mFirstBGCylinder; ii < mGrouppCylinderList.size(); ii++) {
            grouppCylinder = mGrouppCylinderList.get(ii);
            if (grouppCylinder.getUsageType().equals(MyConstants.BOTTOM_GAS)) {
                noCylinder += 1;
                }
        }

        if (       (mGroupp.getGroupType().equals("SI") && noCylinder != 1)
                || (mGroupp.getGroupType().equals("DO") && noCylinder != 2)
                || (mGroupp.getGroupType().equals("SM") && noCylinder != 2)
                ) {
            showError(getString(R.string.msg_groupp_cylinder_no_match));
            rc = false;
        }

        return rc;
    }

    private boolean validateUsageType() {
        boolean rc = true;

        for (int i=0;i<mGrouppCylinderList.size();i++) {
            GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);

            if (grouppCylinder.getIsNew().equals(MyConstants.YES)) {
                // It is a new cylinder
                // It can be of type of PONY or DECO
                // Type of BOTTOM_GAS or TRAVEL_GAS are not allowed
                if (mAirDa.groupUsedByRealDive(grouppCylinder.getGroupNo()) > 0
                        && (grouppCylinder.getUsageType().equals(MyConstants.BOTTOM_GAS)
                        || grouppCylinder.getUsageType().equals(MyConstants.TRAVEL_GAS))) {
                    showError(getString(R.string.msg_groupp_used_real_dive));
                    rc = false;
                    break;
                }
            } else if (grouppCylinder.getHasSpecChanged()) {
                showError(getString(R.string.msg_groupp_used_real_dive));
                rc = false;
                break;
            }
        }

        return rc;
    }

    private Boolean isCylinderInGroup(Long cylinderNo) {
        boolean found = false;
        for (int i = 0; i < mGrouppCylinderList.size(); i++) {
            GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
            if (grouppCylinder.getCylinderNo().equals(cylinderNo)) {
                found = true;
                break;
            }
        }
        return found;
    }

    private int findFirstBottomGasCylinder() {
        int ii = MyConstants.MINUS_ONE_I;
        for (int i = 0; i < mGrouppCylinderList.size(); i++) {
            GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
            if (grouppCylinder.getUsageType().equals(MyConstants.BOTTOM_GAS)) {
                ii = i;
                break;
            }
        }
        return ii;
    }

    private Boolean checkHasCylinderChanged() {
        boolean hasCylinderChanged = false;
        for (int i = 0; i < mGrouppCylinderList.size(); i++) {
            GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
            // Adding a Pony/Deco Gas doe not affect the Group Definition
            if (grouppCylinder.getHasDataChanged()
                    && (grouppCylinder.getUsageType().equals(MyConstants.BOTTOM_GAS)
                    || grouppCylinder.getUsageType().equals(MyConstants.TRAVEL_GAS))) {
                hasCylinderChanged = true;
                break;
            }
        }

        return hasCylinderChanged;
    }

    private void requestFocus(View view) {
        if (view instanceof EditText) {
            // Only works for EditText
            view.clearFocus();
            view.requestFocus();
            ((EditText) view).selectAll();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            view.clearFocus();
            view.requestFocus();
        }
    }

    // Adapter functions

    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mGroupp.setInMultiEditMode(mInMultiEditMode);
        if (mGroupp.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add Cylinder floating button
            mBinding.fabCylinder.setVisibility(View.INVISIBLE);
            // Showing Checkboxes on all Cylinders items
            for (int i = 0; i < mGrouppCylinderList.size(); i++) {
                GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
                grouppCylinder.setVisible(true);
                if (position - HEADER_OFFSET == i) {
                    grouppCylinder.setChecked(true);
                }
            }
            mGrouppCylinderAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mGrouppCylinderToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add Cylinder floating button
            mBinding.fabCylinder.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all Cylinder items
            for (int i = 0; i < mGrouppCylinderList.size(); i++) {
                GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
                grouppCylinder.setVisible(false);
                grouppCylinder.setChecked(false);
            }
            mGrouppCylinderAdapter.notifyDataSetChanged();
            mGrouppCylinderToDelete = 1;
            mGrouppCylinderAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mGrouppCylinderToDelete = 0;
        } else {
            mGrouppCylinderToDelete = mBinding.recycler.getChildCount() - HEADER_OFFSET;
        }
        for (int i = 0; i < mGrouppCylinderList.size(); i++) {
            GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
            grouppCylinder.setChecked(checked);
            if (checked) {
                mGrouppCylinderToDelete++;
            } else {
                mGrouppCylinderToDelete--;
            }
        }
        mGrouppCylinderAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mGrouppCylinderToDelete);
        if (getSupportActionBar() != null) {
            if (mGrouppCylinderToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countGrouppCylinder(Boolean checked) {
        if (checked) {
            mGrouppCylinderToDelete++;
        } else {
            mGrouppCylinderToDelete--;
        }
        mAppTitleCount = String.valueOf(mGrouppCylinderToDelete);
        if (getSupportActionBar() != null) {
            if (mGrouppCylinderToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mGrouppCylinderToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mGrouppCylinderToDelete + " " + getString(R.string.msg_cylinders_will_be_removed))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_remove, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mGrouppCylinderList.size() - 1;
                        String successTypes;
                        String failedTypes;
                        StringBuilder sbSuccessType = new StringBuilder();
                        StringBuilder sbFailedType = new StringBuilder();
                        mAirDa.openWithFKConstraintsEnabled();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            GrouppCylinder grouppCylinder = mGrouppCylinderList.get(position);
                            if (grouppCylinder.getChecked()) {
                                grouppCylinder = mGrouppCylinderAdapter.getGrouppCylinder(position);
                                if (       grouppCylinder.getUsageType().equals(MyConstants.DECO_GAS)
                                        || grouppCylinder.getUsageType().equals(MyConstants.EMERGENCY_GAS)
                                        || grouppCylinder.getIsNew().equals(MyConstants.YES)
                                        || grouppCylinder.getUsageCount() == MyConstants.ZERO_I){
                                    Integer rc = mAirDa.deleteGroupCylinder(grouppCylinder.getGroupNo(), grouppCylinder.getCylinderNo());
                                    if (rc.equals(MyConstants.ZERO_I)) {
                                        // Delete was successful
                                        mGrouppCylinderAdapter.deleteGroupCylinder(position);
                                        mGrouppCylinderToDelete--;
                                        sbSuccessType.insert(0, ", ");
                                        sbSuccessType.insert(0, grouppCylinder.getCylinderType());
                                        mGroupp.setHasDataChanged(true);
                                    } else {
                                        // Delete failed
                                        sbFailedType.insert(0, ", ");
                                        sbFailedType.insert(0, grouppCylinder.getCylinderType());
                                    }
                                } else {
                                    showError(getString(R.string.msg_groupp_used_real_dive));
                                    break;
                                }
                            }
                        }
                        // Do not close the DB connection
                        // The DiveActivity controls the DataBase transaction
                        if (mGrouppCylinderList.size() > 0) {
                            // Set the current position in the Adapter
                            mGrouppCylinderAdapter.setSelectedPosition(0);
                            // Scroll to the Cylinder, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mGrouppCylinderAdapter.notifyDataSetChanged();

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
                        String message = String.format(res.getString(R.string.msg_delete_fk_constraint),res.getString(R.string.mn_cylinder),successTypes,failedTypes);
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
                        mBinding.saveButton.setText(R.string.button_save);
                        mBinding.saveButton.setEnabled(true);
                        mBinding.saveButton.setAlpha(1.0f);
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added Dive
        // The screen does not scroll if the newly added Dive is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setGrouppCylinder(GrouppCylinder grouppCylinder) {
        mGrouppCylinder = grouppCylinder;
    }
}