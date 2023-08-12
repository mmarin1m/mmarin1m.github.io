package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import ca.myairbuddyandi.databinding.ComputerPickActivityBinding;

/**
 * Created by Michel on 2023-03-21.
 * Holds all of the logic for the ComputerPickActivity class
 *
 * To select and edit dive computer already saved
 * To select and download dives from a dive computer already saved
 * To start adding a new dive computer
 * To delete a dive computer already saved
 *
 * Main POJO:   ComputerPick
 * Passes:      Computer
 * Receives:    Computer
 * Passes back: None
 *
 */

public class ComputerPickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "ComputerPickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<ComputerPick> mComputerPickList = new ArrayList<>();
    private CharSequence mAppTitleCount;
    private ComputerPick mComputerPick = new ComputerPick();
    private ComputerPickActivityBinding mBinding = null;
    private ComputerPickAdapter mComputerPickAdapter;
    private int mComputersToDelete = 1;
    private int mPosition;
    private String mOriginalTitle;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.computer_pick_activity);

        //Get data for the Computers
        mAirDa.open();
        mComputerPickList = mAirDa.getAllComputers();

        // Create and load the data in the Recycler View Adapter
        if (mComputerPickAdapter == null) {
            mComputerPickAdapter = new ComputerPickAdapter(this, mComputerPickList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mComputerPickList.size() == MyConstants.ZERO_I) {
                mComputerPickAdapter.setComputerPick(mComputerPick);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mComputerPickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);

        // Set the listener for the FAB
        mBinding.fabComputer.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ComputerActivity.class);
            Computer computer = new Computer();
            computer.setComputerNo(MyConstants.ZERO_L);
            intent.putExtra(MyConstants.COMPUTER, computer);
            addLauncher.launch(intent);
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Try to find the Computer in the collection
        mPosition = mComputerPickList.indexOf(mComputerPick);

        if (mPosition == -1 && mComputerPickList.size() >= 1) {
            // Can't find the Computer
            // Select first row
            mPosition = 0;
        }

        if (mComputerPickAdapter.getComputerPickList().size() >= 1) {
            // There is at least one Computer in the collection
            // Scroll to the Computer
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mComputerPickAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        if (mComputerPick.getInMultiEditMode()) {
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
            Intent intent = new Intent(this, ComputerActivity.class);
            mComputerPick = mComputerPickAdapter.getComputer();
            Computer computer = new Computer();
            computer.setComputerNo(mComputerPick.getComputerNo());
            intent.putExtra(MyConstants.COMPUTER, computer);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_computer_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mComputerPick.getInMultiEditMode()) {
                // Go back to Single Edit Mode
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mOriginalTitle);
                    mBinding.fabComputer.setVisibility(View.VISIBLE);
                    setVisibility(0,false);
                    return true;
                }
            } else {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        if (mComputerPick.getInMultiEditMode()) {
            // Go back to Single Edit Mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
                mBinding.fabComputer.setVisibility(View.VISIBLE);
                setVisibility(0,false);
            }
        } else {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }
    }

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    @SuppressWarnings("DEPRECATION")
    ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly ADDED Computer from the Computer activity
                    Computer computer;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        computer = data.getParcelableExtra(MyConstants.COMPUTER,Computer.class);
                    } else {
                        computer = data.getParcelableExtra(MyConstants.COMPUTER);
                    }
                    // The Computer has already been added to the Database
                    // Need to add it to the recyclerView
                    ComputerPick computerPick = new ComputerPick();
                    assert computer != null;
                    computerPick.setComputerNo(computer.getComputerNo());
                    computerPick.setVendor(computer.getVendor());
                    computerPick.setProduct(computer.getProduct());
                    computerPick.setDescription(computer.getDescription());
                    mComputerPickAdapter.addComputerPick(computerPick);
                    mComputerPick = computerPick;
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly MODIFIED Computer from the Computer activity
                    Computer computer;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        computer = data.getParcelableExtra(MyConstants.COMPUTER,Computer.class);
                    } else {
                        computer = data.getParcelableExtra(MyConstants.COMPUTER);
                    }
                    // The ComputerPick has already been saved to the Database
                    // Need to reflect the changes in the recyclerView
                    ComputerPick computerPick = new ComputerPick();
                    assert computer != null;
                    computerPick.setComputerNo(computer.getComputerNo());
                    computerPick.setVendor(computer.getVendor());
                    computerPick.setProduct(computer.getProduct());
                    computerPick.setDescription(computer.getDescription());
                    mComputerPickAdapter.modifyComputerPick(computerPick);
                    mComputerPick = computerPick;
                }
            });

    // Adapter functions
    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiEditMode) {
        mComputerPick.setInMultiEditMode(mInMultiEditMode);
        if (mComputerPick.getInMultiEditMode()) {
            // TRUE = In Multi Mode
            // Show the Delete
            invalidateOptionsMenu();
            // Hiding the Add Computer floating button
            mBinding.fabComputer.setVisibility(View.INVISIBLE);
            // Showing Checkboxes on all Computer items
            for (int i = 0; i < mComputerPickList.size(); i++) {
                ComputerPick computerPick = mComputerPickList.get(i);
                computerPick.setVisible(true);
                if (position - HEADER_OFFSET == i) {
                    computerPick.setChecked(true);
                }
            }
            mComputerPickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(" " + mComputersToDelete);
            }
        } else {
            // FALSE = NOT in Multi Mode
            // Hide the Delete
            invalidateOptionsMenu();
            // Showing the Add Computer floating button
            mBinding.fabComputer.setVisibility(View.VISIBLE);
            // Hiding Checkboxes on all Computer items
            for (int i = 0; i < mComputerPickList.size(); i++) {
                ComputerPick computerPick = mComputerPickList.get(i);
                computerPick.setVisible(false);
                computerPick.setChecked(false);
            }
            mComputerPickAdapter.notifyDataSetChanged();
            mComputersToDelete = 1;
            mComputerPickAdapter.setMultiEditMode(false);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mComputersToDelete = 0;
        } else {
            mComputersToDelete = mComputerPickList.size();
        }
        for (int i = 0; i < mComputerPickList.size(); i++) {
            ComputerPick mComputerPick = mComputerPickList.get(i);
            mComputerPick.setChecked(checked);
            if (checked) {
                mComputersToDelete++;
            } else {
                mComputersToDelete--;
            }
        }
        mComputerPickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mComputersToDelete);
        if (getSupportActionBar() != null) {
            if (mComputersToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countComputers(Boolean checked) {
        if (checked) {
            mComputersToDelete++;
        } else {
            mComputersToDelete--;
        }
        mAppTitleCount = String.valueOf(mComputersToDelete);
        if (getSupportActionBar() != null) {
            if (mComputersToDelete > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteMultiMode() {
        if (mComputersToDelete > 0) {
            new AlertDialog.Builder(this)
                    .setMessage(mComputersToDelete + " " + getString(R.string.msg_computer_will_be_deleted))
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_delete, (dialog, id) -> {
                        // Delete logic
                        int itemCount = mComputerPickList.size() - 1;
                        for (int position = itemCount; position >= MyConstants.ZERO_I; position--) {
                            ComputerPick mComputerPick = mComputerPickList.get(position);
                            if (mComputerPick.getChecked()) {
                                mComputerPick = mComputerPickAdapter.getComputerPick(position);
                                mComputerPickAdapter.deleteComputerPick(position);
                                mAirDa.deleteComputer(mComputerPick.getComputerNo());
                                mComputersToDelete--;
                            }
                        }
                        if (mComputerPickList.size() > 0) {
                            // Set the current position in the Adapter
                            mComputerPickAdapter.setSelectedPosition(0);
                            // Scroll to the Computer, it might be far down the screen
                            mBinding.recycler.smoothScrollToPosition(0);
                        }
                        mComputerPickAdapter.notifyDataSetChanged();

                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(mOriginalTitle);
                            mBinding.fabComputer.setVisibility(View.VISIBLE);
                            setVisibility(0,false);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, null)
                    .show();
        }
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added Computer
        // The screen does not scroll if the newly added Computer is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }
}