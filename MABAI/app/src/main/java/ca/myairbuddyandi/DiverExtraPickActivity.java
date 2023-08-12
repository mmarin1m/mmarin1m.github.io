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
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Objects;

import ca.myairbuddyandi.databinding.DiverPickActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the DiverExtraPickActivity class
 *
 */

public class DiverExtraPickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DiverExtraPickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mDiversToDelete = 0;
    private int mPosition;
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<Diver> mDiverPickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private Diver mDiver = new Diver();
    private DiverPickActivityBinding mBinding = null;
    private DiverExtraPickAdapter mDiverExtraPickAdapter;
    private String mOriginalTitle;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.diver_pick_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mDiver = getIntent().getParcelableExtra(MyConstants.PICK_A_DIVER_EXTRA,Diver.class);
        } else {
            mDiver = getIntent().getParcelableExtra(MyConstants.PICK_A_DIVER_EXTRA);
        }
        assert mDiver != null;
        mDiver.setContext(this);

        if (mDiver.getLogBookNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDiver.getLogBookNo());
            }
        }

        //Get data for the Divers
        mAirDa.openWithFKConstraintsEnabled();

        mDiverPickList = mAirDa.getAllDiversExtra(mDiver.getLogBookNo(), mDiver.getDiveNo());

        // Create and load the data in the Recycler View Adapter
        if (mDiverExtraPickAdapter == null) {
            mDiverExtraPickAdapter = new DiverExtraPickAdapter(this, mDiverPickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mDiverPickList.size() == MyConstants.ZERO_I) {
                mDiverExtraPickAdapter.setDiver(mDiver);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mDiverExtraPickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listener for the Search
        mBinding.searchView.setOnSearchClickListener(v -> mBinding.fabDiver.setVisibility(View.INVISIBLE));

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                //Filter as typing
                mDiverExtraPickAdapter.getFilter().filter(query);
                return false;
            }
        });

        mBinding.searchView.setOnCloseListener(() -> {
            mBinding.fabDiver.setVisibility(View.VISIBLE);
            return false;
        });

        // Set the listener for the FAB
        mBinding.fabDiver.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DiverPickActivity.class);
            Diver diver = new Diver();
            diver.setContext(getApplicationContext());
            diver.setDiverNo(MyConstants.ZERO_L);
            diver.setDiveNo(mDiver.getDiveNo());
            diver.setLogBookNo(mDiver.getLogBookNo());
            intent.putExtra(MyConstants.DIVER, diver);
            addLauncher.launch(intent);
        });

        // No need for a Pick button listener
        // All the extra divers are saved in this Activity
        // And nothing is brought back to the Dive Activity

        mBinding.buttonRow.setVisibility(View.GONE);

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Work with the list in the Adapter as it might be Filtered Out
        // Try to find the Diver in the collection
        mPosition = mDiverExtraPickAdapter.getDiverPickPosition(mDiver);

        if (mPosition == -1 && mDiverExtraPickAdapter.getDiverPickList().size() >= 1) {
            // Can't find the Diver
            // Select first row
            mPosition = 0;
        }

        if (mDiverExtraPickAdapter.getDiverPickList().size() >= 1) {
            // There is at least one Diver in the collection
            // Scroll to the Diver
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mDiverExtraPickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        if (mDiver.getInMultiEditMode()) {
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
            mDiver = mDiverExtraPickAdapter.getDiver();
            Intent intent = new Intent(this, DiverActivity.class);
            intent.putExtra(MyConstants.DIVER, mDiver);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_diver_extra_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // Going back to SacRmvActivity
            if (mDiver.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabDiver.setVisibility(View.VISIBLE);
                    setVisibility(0,false);
                    return true;
                }
            } else {
                mDiver = mDiverExtraPickAdapter.getDiver();
                Intent intent = new Intent();
                intent.putExtra(MyConstants.PICK_A_DIVER, mDiver);
                setResult(RESULT_OK,intent);
            }
            super.onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        if (mDiver.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabDiver.setVisibility(View.VISIBLE);
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

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED Diver from the Diver Pick activity
                    Diver diver;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        diver = data.getParcelableExtra(MyConstants.PICK_A_DIVER,Diver.class);
                    } else {
                        diver = data.getParcelableExtra(MyConstants.PICK_A_DIVER);
                    }
                    // The Diver has already been added to the Database
                    // Need to add it to the recyclerView
                    mDiverExtraPickAdapter.addDiver(diver);
                    mDiver = diver;
                    // Need to add it to DIVER_DIVE table
                    DiverDive diverDive = new DiverDive();
                    diverDive.setDiverNo(mDiver.getDiverNo());
                    diverDive.setDiveNo(mDiver.getDiveNo());
                    diverDive.setIsPrimary("N");
                    diverDive.setRmv(MyConstants.ZERO_D);
                    mAirDa.createDiverDive(diverDive);
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED Diver from the Diver activity
                    Diver diver = data.getParcelableExtra(MyConstants.DIVER);
                    // The Diver has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    mDiverExtraPickAdapter.modifyDiver(diver);
                    mDiver = diver;
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
        mDiver.setInMultiEditMode(mInMultiEditMode);
        if (mDiver.getInMultiEditMode()) {
            // New
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add DiveType floating button
            mBinding.fabDiver.setVisibility(View.INVISIBLE);
            // Only set the Multi Mode of there are rows to delete, other than the Me value (1)
            // Showing Checkboxes on all Diver items
            for (int i = 0; i < mDiverPickList.size(); i++) {
                Diver diver = mDiverPickList.get(i);
                if (diver.getDiverNo() > MyConstants.ONE_L) {
                    diver.setVisible(View.VISIBLE);
                } else {
                    // The Checkbox is INVISIBLE, the user CANNOT delete it
                    diver.setVisible(View.INVISIBLE);
                }
                if (position - HEADER_OFFSET  == i && diver.getDiverNo() > MyConstants.ONE_L) {
                    diver.setChecked(true);
                    mDiversToDelete++;
                }
            }
            mDiverExtraPickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mDiversToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add Diver floating button
            mBinding.fabDiver.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all Diver items
            for (int i = 0; i < mDiverPickList.size(); i++) {
                Diver diver = mDiverPickList.get(i);
                diver.setVisible(View.GONE);
                diver.setChecked(false);
            }
            mDiverExtraPickAdapter.notifyDataSetChanged();
            mDiversToDelete = 0;
            mDiverExtraPickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mDiversToDelete = 0;
        } else {
            mDiversToDelete = mDiverPickList.size();
        }
        for (int i = 0; i < mDiverPickList.size(); i++) {
            Diver diver = mDiverPickList.get(i);
            if (diver.getDiverNo() > MyConstants.ONE_L) {
                diver.setChecked(checked);
                if (checked) {
                    mDiversToDelete++;
                } else {
                    mDiversToDelete--;
                }
            } else {
                if (!checked) {
                    mDiversToDelete--;
                }
            }
        }
        mDiverExtraPickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mDiversToDelete);
        if (getSupportActionBar() != null) {
            if (mDiversToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countDivers(Boolean checked) {
        if (checked) {
            mDiversToDelete++;
        } else {
            mDiversToDelete--;
        }
        mAppTitleCount = String.valueOf(mDiversToDelete);
        if (getSupportActionBar() != null) {
            if (mDiversToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mDiversToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mDiversToDelete + " " + getString(R.string.msg_divers_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mDiverPickList.size() - 1;
                        String successDivers;
                        String failedDivers;
                        StringBuilder sbSuccessDiver = new StringBuilder();
                        StringBuilder sbFailedDiver = new StringBuilder();
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            Diver diver = mDiverPickList.get(position);
                            State state = new State();
                            mAirDa.getState(state);
                            if (diver.getChecked()) {
                                diver = mDiverExtraPickAdapter.getDiver(position);
                                Integer rc = mAirDa.deleteDiverDive(diver.getDiverNo(),diver.getDiveNo());
                                // No RI on the Diver table since we are deleting a diver with DELETE CASCADE
                                // All entries for a Diver will be deleted
                                // Still displaying the same message for consistency
                                if (rc.equals(MyConstants.ZERO_I)) {
                                    // Delete was successful
                                    mDiverExtraPickAdapter.deleteDiver(position);
                                    mDiversToDelete--;
                                    sbSuccessDiver.insert(0, ", ");
                                    sbSuccessDiver.insert(0, diver.getLastOrFirstName());
                                    mDiver.setHasDataChanged(true);
                                    // State
                                    // Reset the buddy to 0 if being deleted
                                    if (state.getBuddyDiverNo() == diver.getDiverNo()) {
                                        state.setBuddyDiverNo(0);
                                        state.setMyBuddyRmv(MyConstants.ZERO_D);
                                        mAirDa.updateState(state);
                                    }
                                } else {
                                    // Delete failed
                                    sbFailedDiver.insert(0,", ");
                                    sbFailedDiver.insert(0, diver.getLastOrFirstName());
                                }
                            }
                        }
                        if (mDiverPickList.size() > 0) {
                            // Set the current position in the Adapter
                            mDiverExtraPickAdapter.setSelectedPosition(0);
                            // Scroll to the Diver, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mDiverExtraPickAdapter.notifyDataSetChanged();

                        successDivers = sbSuccessDiver.toString();
                        failedDivers = sbFailedDiver.toString();

                        if (successDivers.equals("")) {
                            successDivers = MyConstants.NONE;
                        } else {
                            successDivers = MyFunctions.removeLastString(successDivers, ", ");
                        }

                        if (failedDivers.equals("")) {
                            failedDivers = MyConstants.NONE;
                        } else {
                            failedDivers = MyFunctions.removeLastString(failedDivers,", ");
                        }

                        Resources res = getResources();
                        String message = String.format(res.getString(R.string.msg_delete_fk_constraint),res.getString(R.string.mn_dive_type),successDivers,failedDivers);
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
                        mBinding.fabDiver.setVisibility(View.VISIBLE);
                        setVisibility(0,false);
                    }

                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added Diver
        // The screen does not scroll if the newly added Diver is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    // My functions

    public void setDiver(Diver diver) {
        mDiver = diver;
    }
}