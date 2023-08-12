package ca.myairbuddyandi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Objects;

import ca.myairbuddyandi.databinding.AcronymActivityBinding;

/**
 * Created by Michel on 2020-04-28.
 * Holds all of the logic for the AcronymActivity class
 *
 */

public class AcronymActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "AcronymActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private Acronym mAcronym = new Acronym();
    private AcronymActivityBinding mBinding = null;
    private AcronymAdapter mAcronymAdapter;
    private int mPosition;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.acronym_activity);

        //Get data for the Acronyms
        // Private
        ArrayList<Acronym> mAcronymList = getAllAcronyms();

        // Create and load the data in the Recycler View Adapter
        if (mAcronymAdapter == null) {
            mAcronymAdapter = new AcronymAdapter(mAcronymList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mAcronymList.size() == MyConstants.ZERO_I) {
                mAcronymAdapter.setAcronym(mAcronym);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mAcronymAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listener for the Search
        mBinding.searchView.setOnSearchClickListener(v -> {
        });

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                //Filter as typing
                mAcronymAdapter.getFilter().filter(query);
                return false;
            }
        });

        mBinding.searchView.setOnCloseListener(() -> false);

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Work with the list in the Adapter as it might be Filtered Out
        // Try to find the Acronym in the collection
        mPosition = mAcronymAdapter.getAcronymPosition(mAcronym);

        if (mPosition == -1 && mAcronymAdapter.getAcronymList().size() >= 1) {
            // Can't find the Acronym
            // Select first row
            mPosition = 0;
        }

        if (mAcronymAdapter.getAcronymList().size() >= 1) {
            // There is at least one Acronym in the collection
            // Scroll to the Acronym
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mAcronymAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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

        // TODO: Implement Share
//        final MenuItem shareItem = menu.findItem(R.id.action_share);

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

        if (id == R.id.action_contact_us) {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_acronyms));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            super.onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
        finish();
    }

    // // My functions

    ArrayList<Acronym> getAllAcronyms() {
        ArrayList<Acronym> acronymList = new ArrayList<>();
        String[] acronymDescriptions;
        String[] acronymAcronyms;

        acronymDescriptions = getResources().getStringArray(R.array.acronymDescription);
        acronymAcronyms = getResources().getStringArray(R.array.acronymAcronym);

        for (int i = 0; i < acronymDescriptions.length; i++) {
            Acronym acronym = new Acronym();
            acronym.setAcronym(acronymAcronyms[i]);
            acronym.setDescription(acronymDescriptions[i]);
            acronymList.add(acronym);
        }
        return acronymList;
    }

    public void setAcronym(Acronym acronym) {
        mAcronym = acronym;
    }
}