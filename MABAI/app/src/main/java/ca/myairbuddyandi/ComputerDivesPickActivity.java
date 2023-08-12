package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.ComputerDivesPickActivityBinding;

/**
 * Created by Michel on 2023-07-14.
 * Holds all of the logic for the ComputerDivesPickActivity class
 *
 * To select/unselect new dives to download from the dive computer
 * The selected dives will be eventually saved to MABAI
 *
 * Main POJO:   ComputerDivesPick
 * Passes:      None
 * Receives:    None
 * Passes back: ComputerDivesPick
 */

public class ComputerDivesPickActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "ComputerDivesPickActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mDivesToDownload = 0;
    private String mOriginalTitle;
    private ArrayList<ComputerDives> mComputerDivesPickList;
    private CharSequence mAppTitleCount;
    private ComputerDives mComputerDives = new ComputerDives();
    private ComputerDivesPickActivityBinding mBinding = null;
    private ComputerDivesPickAdapter mComputerDivesPickAdapter;
    private ComputerDivesPick mComputerDivesPick;

    // End of variables

    @SuppressLint("NotifyDataSetChanged")
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        mOriginalTitle = this.getTitle().toString();

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.computer_dives_pick_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mComputerDivesPick = getIntent().getParcelableExtra(MyConstants.COMPUTER_DIVES_PICK,ComputerDivesPick.class);
        } else {
            mComputerDivesPick = getIntent().getParcelableExtra(MyConstants.COMPUTER_DIVES_PICK);
        }

        // Set the listeners
        mBinding.downloadButton.setOnClickListener(view -> {
            download();
        });

        // Get the computer dives data to be selected/unselected
        mComputerDivesPickList = mComputerDivesPick.getComputerDivesPickList();

        // Create and load the data in the Recycler View Adapter
        if (mComputerDivesPickAdapter == null) {
            mComputerDivesPickAdapter = new ComputerDivesPickAdapter(this, mComputerDivesPickList);
        } else {
            mComputerDivesPickAdapter.setComputerDivesPickList(mComputerDivesPickList);
        }

        // If the list is empty, make sure there is a valid POJO in the adapter
        if (mComputerDivesPickList.size() == MyConstants.ZERO_I) {
            mComputerDivesPickAdapter.setComputerDives(mComputerDives);
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mComputerDivesPickAdapter);
        mBinding.recycler.setLayoutManager(new DiveLinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);

        // Do not scroll to the first dive because we do not want
        // the first checkbox to be unchecked and the count to go down!

        // Count the Computer Dives
        countComputerDives();

        // Set the title with the number of selected computer dive
        if (getSupportActionBar() != null) {
            if (mDivesToDownload > 0) {
                getSupportActionBar().setTitle(mOriginalTitle + " " + String.valueOf(mDivesToDownload));
            }
        }

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        if (mComputerDivesPick.getInMultiSelectionMode()) {
            checkAll.setVisible(true);
            checkAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            checkAll.setVisible(false);
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
            selectAll(!item.isChecked());
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_computer_dives_pick));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            if (mComputerDivesPick.getInMultiSelectionMode()) {
                // Go back to Single Selection Mode
                if (getSupportActionBar() != null) {
                    if (mDivesToDownload > 0) {
                        getSupportActionBar().setTitle(mOriginalTitle + " " + String.valueOf(mDivesToDownload));
                    } else {
                        getSupportActionBar().setTitle(mOriginalTitle);
                    }
                    setVisibility(0,false);
                    return true;
                }
            } else {
                Intent intent = new Intent();
                // Get the ComputerDivesPick from the Adapter
                intent.putExtra(MyConstants.COMPUTER_DIVES_PICK, mComputerDivesPick);
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
        if (mComputerDivesPick.getInMultiSelectionMode()) {
            // Go back to Single Selection Mode
            if (getSupportActionBar() != null) {
                if (mDivesToDownload > 0) {
                    getSupportActionBar().setTitle(mOriginalTitle + " " + String.valueOf(mDivesToDownload));
                    setVisibility(0, false);
                } else {
                    getSupportActionBar().setTitle(mOriginalTitle);
                }
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(MyConstants.COMPUTER_DIVES_PICK, mComputerDivesPick);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
            finish();
        }
    }

    public void onPause() {
        super.onPause();
    }

    // My functions

    private void download() {
        if (mComputerDivesPick.getInMultiSelectionMode()) {
            // Go back to Single Selection Mode
            if (getSupportActionBar() != null) {
                if (mDivesToDownload > 0) {
                    getSupportActionBar().setTitle(mOriginalTitle + " " + String.valueOf(mDivesToDownload));
                } else {
                    getSupportActionBar().setTitle(mOriginalTitle);
                }
                setVisibility(0,false);
            }
        } else {
            Intent intent = new Intent();
            // Get the ComputerDivesPick from the Adapter
            intent.putExtra(MyConstants.COMPUTER_DIVES_PICK, mComputerDivesPick);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
            finish();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setVisibility(int position, Boolean mInMultiSelectionMode) {
        mComputerDivesPick.setInMultiSelectionMode(mInMultiSelectionMode);
        if (mComputerDivesPick.getInMultiSelectionMode()) {
            // TRUE = In Multi Selectioin Mode
            // Show the Delete
            invalidateOptionsMenu();
            mComputerDivesPickAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                if (mDivesToDownload > 0) {
                    // SetTitle() need to be a String
                    mAppTitleCount = String.valueOf(mDivesToDownload);
                    getSupportActionBar().setTitle(mAppTitleCount);
                } else {
                    getSupportActionBar().setTitle(mOriginalTitle);
                }
            }
        } else {
            // FALSE = NOT in Multi Selection Mode
            // Hide the Delete
            invalidateOptionsMenu();
            mComputerDivesPickAdapter.notifyDataSetChanged();
            mComputerDivesPickAdapter.setMultiSelectionMode(false);
            if (getSupportActionBar() != null) {
                if (mDivesToDownload > 0) {
                    getSupportActionBar().setTitle(mOriginalTitle + " " + String.valueOf(mDivesToDownload));
                } else {
                    getSupportActionBar().setTitle(mOriginalTitle);
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(Boolean checked) {
        if (checked) {
            mDivesToDownload = 0;
        } else {
            mDivesToDownload = mComputerDivesPickList.size();
        }

        for (int i = 0; i < mComputerDivesPickList.size(); i++) {
            ComputerDives mComputerDives = mComputerDivesPickList.get(i);
            mComputerDives.setChecked(checked);
            if (checked) {
                mDivesToDownload++;
            } else {
                mDivesToDownload--;
            }
        }

        mComputerDivesPickAdapter.notifyDataSetChanged();
        mAppTitleCount = String.valueOf(mDivesToDownload);
        if (getSupportActionBar() != null) {
            if (mDivesToDownload > 0) {
                getSupportActionBar().setTitle(mAppTitleCount);
            } else {
                getSupportActionBar().setTitle(R.string.code_select_more);
            }
        }
    }

    public void countComputerDives() {
        for (int i = 0; i < mComputerDivesPickList.size(); i++) {
            ComputerDives computerDives = mComputerDivesPickList.get(i);
            if (computerDives.getChecked()) {
                mDivesToDownload++;
            }
        }
    }

    public void countComputerDives(Boolean checked) {
        if (checked) {
            mDivesToDownload++;
        } else {
            mDivesToDownload--;
        }

        mAppTitleCount = String.valueOf(mDivesToDownload);
        if (mComputerDivesPick.getInMultiSelectionMode()) {
            if (getSupportActionBar() != null) {
                if (mDivesToDownload > 0) {
                    getSupportActionBar().setTitle(mAppTitleCount);
                } else {
                    getSupportActionBar().setTitle(R.string.code_select_more);
                }
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mOriginalTitle + " " + String.valueOf(mDivesToDownload));
            }
        }
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added Dive
        // The screen does not scroll if the newly added Dive is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    public void setComputerDives(ComputerDives computerDives) {
        mComputerDives = computerDives;
    }
}