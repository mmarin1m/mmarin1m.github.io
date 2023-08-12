package ca.myairbuddyandi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.ComputerDivesListActivityBinding;

/**
 * Created by Michel on 2023-03-22.
 * Holds all of the logic for the ComputerDivesListActivity class
 *
 * To display the dives from the dive computer as is
 *
 * No edit
 * No search
 * No sort
 * No select
 * No pick
 * No expand area
 *
 * Main POJO:   ComputerDivesList
 * Passes:      None
 * Receives:    None
 * Passes back: None
 */

public class ComputerDivesListActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "ComputerDivesListActivity";

    // Public

    // Protected

    // Private
    private ComputerDivesListAdapter mComputerDivesListAdapter;
    private ComputerDivesListActivityBinding mBinding = null;
//    private final AirDA mAirDa = new AirDA(this);
    private ArrayList<ComputerDivesList> mComputerDivesListing = new ArrayList<>();

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.computer_dives_list_activity);

//        mAirDa.open();

//        mAirDa.close();

        // Create and load the data in the Recycler View Adapter
        if (mComputerDivesListAdapter == null) {
            mComputerDivesListAdapter = new ComputerDivesListAdapter(this, mComputerDivesListing);
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mComputerDivesListAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Set the listeners
        // ??


        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
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
            intent.putExtra(getString(R.string.app_help_topic), getString(R.string.act_sac_and_rmv));
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
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
    }

//    // This function is called when the user accepts or decline the permission.
//    // Request Code is used to check which permission called this function.
//    // This request code is provided when the user is prompt for permission.
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//    }

    // My functions

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}