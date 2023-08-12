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
import java.util.Comparator;
import java.util.Objects;

import ca.myairbuddyandi.databinding.ConstantActivityBinding;

/**
 * Created by Michel on 2020-04-28.
 * Holds all of the logic for the ConstantActivity class
 *
 */

public class ConstantActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "ConstantActivity";
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private Constant mConstant = new Constant();
    private ConstantActivityBinding mBinding = null;
    private ConstantAdapter mConstantAdapter;
    private int mPosition;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.constant_activity);

        //Get data for the Constants
        // Private
        ArrayList<Constant> mConstantList = getAllConstants();

        // Create and load the data in the Recycler View Adapter
        if (mConstantAdapter == null) {
            mConstantAdapter = new ConstantAdapter(mConstantList);
            // If the list is empty, make sure there is a valid POJO in the adapter
            if (mConstantList.size() == MyConstants.ZERO_I) {
                mConstantAdapter.setConstant(mConstant);
            }
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mConstantAdapter);
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
                mConstantAdapter.getFilter().filter(query);
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
        // Try to find the Constant in the collection
        mPosition = mConstantAdapter.getConstantPosition(mConstant);

        if (mPosition == -1 && mConstantAdapter.getConstantList().size() >= 1) {
            // Can't find the Constant
            // Select first row
            mPosition = 0;
        }

        if (mConstantAdapter.getConstantList().size() >= 1) {
            // There is at least one Constant in the collection
            // Scroll to the Constant
            mBinding.recycler.smoothScrollToPosition(mPosition + HEADER_OFFSET);
            // Set the current position in the Adapter
            mConstantAdapter.setSelectedPosition(mPosition + HEADER_OFFSET);
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_constants));
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

    ArrayList<Constant> getAllConstants() {
        ArrayList<Constant> constantList = new ArrayList<>();
        String[] constantSystems;
        String[] constantDescriptions;
        String[] constantValues;
        String[] constantUnits;

        constantSystems = getResources().getStringArray(R.array.constantSystem);
        constantDescriptions = getResources().getStringArray(R.array.constantDescription);
        constantValues = getResources().getStringArray(R.array.constantValue);
        constantUnits = getResources().getStringArray(R.array.constantUnit);

        for (int i = 0; i < constantSystems.length; i++) {
            Constant constant = new Constant();
            constant.setSystem(constantSystems[i]);
            constant.setDescription(constantDescriptions[i]);
            constant.setValue(Double.valueOf(constantValues[i]));
            constant.setUnit(constantUnits[i]);
            constantList.add(constant);
        }

        constantList.sort(Comparator.comparing(Constant::getSystem)
                .thenComparing(Constant::getDescription));

        return constantList;
    }

    public void setConstant(Constant constant) {
        mConstant = constant;
    }

}