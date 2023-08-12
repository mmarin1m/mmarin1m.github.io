package ca.myairbuddyandi;

import static java.lang.Long.parseLong;
import static java.lang.Long.valueOf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.ActivityMainBinding;

/**
        * Created by Michel on 2016-12-08.
        * Holds all of the logic for the MainActivity class
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Static
    private static final String LOG_TAG = "MainActivity";

    // Public

    // Protected
    // DIVER
    protected Boolean dropDiver = false;

    // DIVER DIVE
    protected Boolean dropState = false;

    // DIVE

    // TYPE
    protected Boolean dropCylinderType = false;
    protected Boolean dropDiveType = false;
    protected Boolean dropDynamicSpinner = false;
    protected Boolean dropGroupType = false;
    protected Boolean dropSegmentType = false;
    protected Boolean dropUsageType = false;

    // My history
    // Will drop common settings if set to true
    protected Boolean dropMyHistory = true;
    protected Boolean loadMyHistory = true;

    // Pre load common Equipment Group
    protected Boolean dropGroup = false;
    protected Boolean dropCylinder = false;
    protected Boolean dropGroupCylinder = false;
    // Will only load Common stuff once at install and if the Google account is different than mmarin1m@gmail.com
    protected Boolean loadCommon = true;

    // Private
    private ActivityMainBinding mBinding = null;
    private AirDA mAirDa = new AirDA(this);
    private final Diver mDiver = new Diver();
    private State mState = new State();
    private String mUnit;

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");

        if (BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        } else {
            // Firebase Crashlytics
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.setCustomKey("Country",UnitLocale.getCountryCode());
            crashlytics.setCustomKey("Language",UnitLocale.getLanguageCode());
        }

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        DrawerLayout drawer = findViewById(R.id.activity_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Default the Unit preference depending of the locale country
        mUnit = MyFunctions.getUnit();

        // Start initializing database tables from resource files
        // STATE and DIVER tables are the corner stone of this application, dot it first
        // In Foreground, NOT in the Background
        mAirDa.open();
        mAirDa.beginTransaction();

        GetOrCreateStateList();
        // Creating two divers
        // Me with diver_no = 1
        // My Buddy diver_no =2
        GetOrCreateDiverList();

        // Code tables
        GetOrCreateCylinderTypeList();
        GetOrCreateDiveTypeList();
        GetOrCreateDynamicSpinnerList();
        GetOrCreateGroupTypeList();
        GetOrCreateSegmentTypeList();
        GetOrCreateUsageTypeList();

        // Base tables
        // Drop and recreates tables that are not pre-loaded

        // Child tables
        // Drop and recreates tables that are not pre-loaded

        // Junction tables
        // Drop and recreates tables that are not pre-loaded

        // Get the State from the last main_activity (this)
        mAirDa.getState(mState);

        // Set the listeners
        // To edit Me
        mBinding.meLbl.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
        mBinding.meLbl.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DiverActivity.class);
            Diver diver = new Diver();
            diver.setContext(getApplicationContext());
            diver.setDiverNo(MyConstants.ONE_L);
            intent.putExtra(MyConstants.DIVER, diver);
            diverEditLauncher.launch(intent);

        });

        // To edit My Buddy
        if (mState.getBuddyDiverNo() > MyConstants.ONE_L) {
            mBinding.myBuddyLbl.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
            mBinding.myBuddyLbl.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), DiverActivity.class);
                Diver diver = new Diver();
                diver.setContext(getApplicationContext());
                diver.setDiverNo(mState.getBuddyDiverNo());
                intent.putExtra(MyConstants.DIVER, diver);
                diverEditLauncher.launch(intent);
            });
        }

        FloatingActionButton fab = findViewById(R.id.fabDive);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DivePickActivity.class);
            intent.putExtra(MyConstants.STATE, mState);
            DivePick mDivePick = new DivePick();
            mDivePick.setContext(getApplicationContext());
            intent.putExtra(MyConstants.DIVE_PICK, mDivePick);
            divePickLauncher.launch(intent);
        });

        // Set the State as a global variable
        MainApplication mainApplication = (MainApplication)getApplication();
        mainApplication.setState(mState);

        try {
            mAirDa.setTransactionSuccessful();
        } finally {
            // No transaction left behind
            mAirDa.endTransaction();
        }

        mAirDa.close();

        // End of initializing database tables

        Log.d(LOG_TAG, "onCreate done");
    }

    // DEBUG: Uncomment to test Fabric/Firebase
//    public void forceCrash(View view) {
//        throw new RuntimeException("This is a test crash");
//    }

    // Triggered after onCreate and after onActivityResult
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        // DEBUG: For debugging purposes
//        String ipaddress = MyFunctions.getIpAddress(this);

        // Set the Unit of Measure, only once at the install. Piggy back on the Waiver Logic!
        // Display the Waiver, only once at the install
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean waiverAccepted = preferences.getBoolean(getString(R.string.code_waiver_accepted), false);
        if(!waiverAccepted) {

            // Open the Waiver Activity
            Intent intent = new Intent(this, WaiverActivity.class);
            waiverLauncher.launch(intent);

        }

        // Get the State from the last main_activity (this)
        mAirDa.open();
        // Get data for layout
        Main main = new Main();
        main.setContext(this);
        // Passing the DataBinding object to be populated and display data on the activity
        mAirDa.getMain(mState.getDiveType(), mState.getBuddyDiverNo(), mState.getMyGroup(), mState.getMyBuddyGroup(), main);
        mAirDa.close();
        mBinding.setMain(main);

        waiverAccepted = preferences.getBoolean(getString(R.string.code_waiver_accepted), false);
        if(waiverAccepted) {
            //  Checks if Me has been edited
            // The most important field is the Max Depth Allowed
            // If it is 0 then the Diver needs to be edited
            mDiver.setContext(getApplicationContext());
            mAirDa.open();
            mAirDa.getDiver(MyConstants.ONE_L, mDiver);
            mAirDa.close();

            // Must set Me with Last Name & First Name otherwise the DiverAdapter might crash if left as is
            if (mDiver.getMaxDepthAllowed().equals(MyConstants.ZERO_D)) {
                Intent intent = new Intent(getApplicationContext(), DiverActivity.class);
                mDiver.setDiverNo(MyConstants.ONE_L);
                intent.putExtra(MyConstants.DIVER, mDiver);
                diverCreateMeLauncher.launch(intent);
            }

            if (BuildConfig.DEBUG) {
                // In development mode
                // Make Bluetooth and Bluetooth Le available for myself
                // Don't need to comment out the code before shipping a new version
                // Might have to manually to to App Setting to grant the permissions or uninstall, reinstall the app
                // TODO: Find a way to reactivate the permissions and not have to uninstall, reinstall the app
                boolean shouldShowRequestPermissionRationale = preferences.getBoolean(getString(R.string.code_bluetooth_permissions_granted), true);

                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);
                Menu menu = navigationView.getMenu();

                MenuItem target = menu.findItem(R.id.nav_bluetooth_le);
                if (shouldShowRequestPermissionRationale) {
                    // Permission has not been permanently denied yet. Can be set to true even if not yet denied once yet
                    // Show the Bluetooth menu items
                    target.setVisible(true);
                    target = menu.findItem(R.id.nav_bluetooth_le);
                    target.setVisible(true);
                } else {
                    // Permission has been permanently denied
                    // Hide the Bluetooth menu items
                    // They might have been showed before and the user decided to permanently denied the permissions
                    target.setVisible(false);
                    target = menu.findItem(R.id.nav_bluetooth_le);
                    target.setVisible(false);
                }
            }
        }

        Log.d(LOG_TAG, "onResume done");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        final MenuItem settingsItem = menu.findItem(R.id.action_settings);
        final MenuItem acknowledgementsItem = menu.findItem(R.id.action_acknowledgements);
        final MenuItem waiverItem = menu.findItem(R.id.action_waiver);
        final MenuItem privacyPolicyItem = menu.findItem(R.id.action_privacy_policy);

        settingsItem.setVisible(true);
        acknowledgementsItem.setVisible(true);
        waiverItem.setVisible(true);
        privacyPolicyItem.setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_contact_us) {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_main));
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_acknowledgements) {
            Intent intent = new Intent(this, AcknowledgementsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_waiver) {
            Intent intent = new Intent(this, WaiverActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_privacy_policy) {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "onNavigationItemSelected");

        // Handle navigation view item clicks here.

        DrawerLayout drawer = findViewById(R.id.activity_main);
        NavigationView nv= findViewById(R.id.nav_view);
        Menu m = nv.getMenu();
        int id = item.getItemId();

        if (id == R.id.nav_blending_tools) {
            boolean b = !m.findItem(R.id.nav_blending_nitrox).isVisible();
            // Set the Quick Calculators submenus visible state
            m.findItem(R.id.nav_blending_nitrox).setVisible(b);
            m.findItem(R.id.nav_blending_trimix).setVisible(b);
            // Set the Quick Calculators submenus invisible state
            m.findItem(R.id.nav_calculate_altitude).setVisible(false);
            m.findItem(R.id.nav_boyle_law).setVisible(false);
            m.findItem(R.id.nav_calculate_buoyancy).setVisible(false);
            m.findItem(R.id.nav_calculate_charles_law).setVisible(false);
            m.findItem(R.id.nav_calculate_current).setVisible(false);
            m.findItem(R.id.nav_calculate_current_deviation).setVisible(false);
            m.findItem(R.id.nav_calculate_cylinder).setVisible(false);
            m.findItem(R.id.nav_dalton_law).setVisible(false);
            m.findItem(R.id.nav_calculate_descent_ascent).setVisible(false);
            m.findItem(R.id.nav_calculate_ead).setVisible(false);
//            m.findItem(R.id.nav_calculate_end).setVisible(false);
            m.findItem(R.id.nav_gas_conversion).setVisible(false);
            m.findItem(R.id.nav_calculate_ata).setVisible(false);
            m.findItem(R.id.nav_calculate_rmv).setVisible(false);
            m.findItem(R.id.nav_swimming_distance).setVisible(false);
            m.findItem(R.id.nav_unit_conversion).setVisible(false);
            // Set the Maintenance submenus invisible state
            m.findItem(R.id.nav_me).setVisible(false);
            m.findItem(R.id.nav_divers).setVisible(false);
            m.findItem(R.id.nav_equipments).setVisible(false);
            m.findItem(R.id.nav_cylinder_type).setVisible(false);
            m.findItem(R.id.nav_dive_type).setVisible(false);
            m.findItem(R.id.nav_groupp_type).setVisible(false);
            m.findItem(R.id.nav_segment_type).setVisible(false);
            m.findItem(R.id.nav_usage_type).setVisible(false);
            m.findItem(R.id.nav_backup).setVisible(false);
            m.findItem(R.id.nav_restore).setVisible(false);
        } else if (id == R.id.nav_quick_calculators) {
            boolean b=!m.findItem(R.id.nav_calculate_altitude).isVisible();
            // Set the Quick Calculators submenus visible state
            m.findItem(R.id.nav_calculate_altitude).setVisible(b);
            m.findItem(R.id.nav_boyle_law).setVisible(b);
            m.findItem(R.id.nav_calculate_buoyancy).setVisible(b);
            m.findItem(R.id.nav_calculate_charles_law).setVisible(b);
            m.findItem(R.id.nav_calculate_current).setVisible(b);
            m.findItem(R.id.nav_calculate_current_deviation).setVisible(b);
            m.findItem(R.id.nav_calculate_cylinder).setVisible(b);
            m.findItem(R.id.nav_dalton_law).setVisible(b);
            m.findItem(R.id.nav_calculate_descent_ascent).setVisible(b);
            m.findItem(R.id.nav_calculate_ead).setVisible(b);
//            m.findItem(R.id.nav_calculate_end).setVisible(b);
            m.findItem(R.id.nav_gas_conversion).setVisible(b);
            m.findItem(R.id.nav_calculate_ata).setVisible(b);
            m.findItem(R.id.nav_calculate_rmv).setVisible(b);
            m.findItem(R.id.nav_swimming_distance).setVisible(b);
            m.findItem(R.id.nav_unit_conversion).setVisible(b);
            // Set the Blending Tools submenus invisible state
            m.findItem(R.id.nav_blending_nitrox).setVisible(false);
            m.findItem(R.id.nav_blending_trimix).setVisible(false);
            // Set the Maintenance submenus invisible state
            m.findItem(R.id.nav_me).setVisible(false);
            m.findItem(R.id.nav_divers).setVisible(false);
            m.findItem(R.id.nav_equipments).setVisible(false);
            m.findItem(R.id.nav_cylinder_type).setVisible(false);
            m.findItem(R.id.nav_dive_type).setVisible(false);
            m.findItem(R.id.nav_groupp_type).setVisible(false);
            m.findItem(R.id.nav_segment_type).setVisible(false);
            m.findItem(R.id.nav_usage_type).setVisible(false);
            m.findItem(R.id.nav_backup).setVisible(false);
            m.findItem(R.id.nav_restore).setVisible(false);
        } else if (id == R.id.nav_maintenance) {
            boolean b=!m.findItem(R.id.nav_me).isVisible();
            // Set the Maintenance submenus visible state
            m.findItem(R.id.nav_me).setVisible(b);
            m.findItem(R.id.nav_divers).setVisible(b);
            m.findItem(R.id.nav_equipments).setVisible(b);
            m.findItem(R.id.nav_cylinder_type).setVisible(b);
            m.findItem(R.id.nav_dive_type).setVisible(b);
            m.findItem(R.id.nav_groupp_type).setVisible(b);
            m.findItem(R.id.nav_segment_type).setVisible(b);
            m.findItem(R.id.nav_usage_type).setVisible(b);
            m.findItem(R.id.nav_backup).setVisible(b);
            m.findItem(R.id.nav_restore).setVisible(b);
            // Set the Blending Tools submenus invisible state
            m.findItem(R.id.nav_blending_nitrox).setVisible(false);
            m.findItem(R.id.nav_blending_trimix).setVisible(false);
            // Set the Quick Calculators submenus invisible state
            m.findItem(R.id.nav_calculate_altitude).setVisible(false);
            m.findItem(R.id.nav_boyle_law).setVisible(false);
            m.findItem(R.id.nav_calculate_buoyancy).setVisible(false);
            m.findItem(R.id.nav_calculate_charles_law).setVisible(false);
            m.findItem(R.id.nav_calculate_current).setVisible(false);
            m.findItem(R.id.nav_calculate_current_deviation).setVisible(false);
            m.findItem(R.id.nav_calculate_cylinder).setVisible(false);
            m.findItem(R.id.nav_dalton_law).setVisible(false);
            m.findItem(R.id.nav_calculate_descent_ascent).setVisible(false);
            m.findItem(R.id.nav_calculate_ead).setVisible(false);
//            m.findItem(R.id.nav_calculate_end).setVisible(false);
            m.findItem(R.id.nav_gas_conversion).setVisible(false);
            m.findItem(R.id.nav_calculate_ata).setVisible(false);
            m.findItem(R.id.nav_calculate_rmv).setVisible(false);
            m.findItem(R.id.nav_swimming_distance).setVisible(false);
            m.findItem(R.id.nav_unit_conversion).setVisible(false);
        } else if (id == R.id.nav_sac_rmv) {
            Intent intent = new Intent(this, SacRmvActivity.class);
            intent.putExtra(MyConstants.STATE, mState);
            rmvLauncher.launch(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_unit_conversion) {
            Intent intent = new Intent(this, CalculateUnitActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_gas_conversion) {
            Intent intent = new Intent(this, CalculateGasActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_altitude) {
            Intent intent = new Intent(this, CalculateAltitudeActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_ata) {
            Intent intent = new Intent(this, CalculatePressureActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_charles_law) {
            Intent intent = new Intent(this, CalculateCharlesLawActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_current) {
            Intent intent = new Intent(this, CalculateCurrentActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_current_deviation) {
            Intent intent = new Intent(this, CalculateCurrentDeviationActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_cylinder) {
            Intent intent = new Intent(this, CalculateCylinderActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_rmv) {
            Intent intent = new Intent(this, CalculateRmvActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_dalton_law) {
            Intent intent = new Intent(this, CalculateDaltonLawActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_boyle_law) {
            Intent intent = new Intent(this, CalculateBoyleLawActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_ead) {
            Intent intent = new Intent(this, CalculateEadActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_buoyancy) {
            Intent intent = new Intent(this, CalculateBuoyancyActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_blending_nitrox) {
            Intent intent = new Intent(this, CalculateNitroxActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_blending_trimix) {
            Intent intent = new Intent(this, CalculateTrimixActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
//        } else if (id == R.id.nav_calculate_end) {
//            Intent intent = new Intent(this, CalculateEndActivity.class);
//            startActivity(intent);
//            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_calculate_descent_ascent) {
            Intent intent = new Intent(this, CalculateDescentAscentActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_swimming_distance) {
            Intent intent = new Intent(this, CalculateSwimmingDistanceActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_cylinder_type) {
            Intent intent = new Intent(this, CylinderTypePickActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_me) {
            Intent intent = new Intent(this, DiverActivity.class);
            Diver diver = new Diver();
            diver.setContext(this);
            diver.setDiverNo(MyConstants.ONE_L);
            intent.putExtra(MyConstants.DIVER, diver);
            diverEditLauncher.launch(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_divers) {
            Intent intent = new Intent(getApplicationContext(), DiverPickActivity.class);
            Diver diver = new Diver();
            diver.setContext(getApplicationContext());
            // Set the diverNo to -1 to disable Pick
            diver.setDiverNo(MyConstants.MINUS_ONE_L);
            diver.setDiveNo(MyConstants.MINUS_ONE_L);
            diver.setLogBookNo(MyConstants.ZERO_INT);
            intent.putExtra(MyConstants.DIVER, diver);
            diverPickBuddyLauncher.launch(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_equipments) {
            Intent intent = new Intent(getApplicationContext(), GrouppPickActivity.class);
            GrouppPick grouppPick = new GrouppPick();
            // Set the groupNo to -1 to disable Pick
            grouppPick.setGroupNo(MyConstants.MINUS_ONE_L);
            // Only works for my equipments
            grouppPick.setDiverNo(MyConstants.ONE_L);
            grouppPick.setDiveNo(MyConstants.ZERO_L);
            intent.putExtra(MyConstants.PICK_A_GROUPP, grouppPick);
            groupPickMeLauncher.launch(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_dive_type) {
            Intent intent = new Intent(this, DiveTypePickActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_groupp_type) {
            Intent intent = new Intent(this, GrouppTypePickActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_segment_type) {
            Intent intent = new Intent(this, SegmentTypePickActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_usage_type) {
            Intent intent = new Intent(this, UsageTypePickActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_backup) {
            Intent intent = new Intent(this, BackupActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_restore) {
            Intent intent = new Intent(this, RestoreActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_acronym) {
            Intent intent = new Intent(this, AcronymActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_constant) {
            Intent intent = new Intent(this, ConstantActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_bluetooth_le) {
            Intent intent = new Intent(this, BluetoothLePickActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);

            // TODO: Implement Communicate
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed");

        // Hard button on Phone
        DrawerLayout drawer = findViewById(R.id.activity_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // My functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> divePickLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(LOG_TAG, "divePickLauncher");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    mState = data.getParcelableExtra(MyConstants.STATE);
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> diverCreateMeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(LOG_TAG, "diverCreateMeLauncher");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor edit = preferences.edit();
                        assert data != null;
                        Diver diver = data.getParcelableExtra(MyConstants.DIVER);
                        assert diver != null;
                        if (diver.getEmail().equals(MyConstants.MY_EMAIL)
                                // Date pattern must match the one in the Arrays Load My History
                                && MyFunctions.getDatePattern(getApplicationContext()).equals(MyConstants.MY_DATE_PATTERN)
                                && mUnit.equals(MyConstants.IMPERIAL)) {
                            // Start loading my history
                            // Load the history only once at the install
                            mAirDa.open();
                            mAirDa.beginTransaction();

                            boolean historyLoaded = preferences.getBoolean(getString(R.string.code_history_loaded), false);
                            if (!historyLoaded && loadMyHistory) {
                                edit.putBoolean(getString(R.string.code_history_loaded), Boolean.TRUE);
                                edit.apply();
                                // Drop the tables just in case
                                // In reverse order
                                mAirDa.dropDivePlan();
                                mAirDa.dropGroupCylinder();
                                mAirDa.dropGroupp();
                                mAirDa.dropDiverDiveGroupCylinder();
                                mAirDa.dropDiverDiveGroup();
                                mAirDa.dropDiveSegment();
                                mAirDa.dropDiverDive();
                                mAirDa.dropDive();
                                mAirDa.dropCylinder();
                                // Load my history
                                GetOrCreateMyCylinderList();
                                GetOrCreateMyDiveList();
                                GetOrCreateMyDiverDiveList();
                                GetOrCreateMyDiverDiveGroupList();
                                GetOrCreateMyDiverDiverGroupCylinderList();
                                GetOrCreateMyGroupList();
                                GetOrCreateMyGroupCylinderList();
                                GetOrCreateMyDivePlanList();
                            } else if (dropMyHistory) {
                                // In reverse order
                                mAirDa.dropDivePlan();
                                mAirDa.dropGroupCylinder();
                                mAirDa.dropGroupp();
                                mAirDa.dropDiverDiveGroupCylinder();
                                mAirDa.dropDiverDiveGroup();
                                mAirDa.dropDiveSegment();
                                mAirDa.dropDiverDive();
                                mAirDa.dropDive();
                                mAirDa.dropCylinder();
                            }
                            // Stop loading my history
                            try {
                                mAirDa.setTransactionSuccessful();
                            } finally {
                                // No transaction left behind
                                mAirDa.endTransaction();
                            }
                            mAirDa.close();
                        } else {
                            boolean commonLoaded = preferences.getBoolean(getString(R.string.code_common_loaded), false);
                            if (!commonLoaded && loadCommon) {
                                edit.putBoolean(getString(R.string.code_common_loaded), Boolean.TRUE);
                                edit.apply();
                                mAirDa.open();
                                mAirDa.beginTransaction();
                                // Pre load Equipment Group
                                GetOrCreateCommonGroupList();
                                GetOrCreateCommonCylinderList();
                                GetOrCreateCommonGroupCylinderList();
                                try {
                                    mAirDa.setTransactionSuccessful();
                                } finally {
                                    // No transaction left behind
                                    mAirDa.endTransaction();
                                }
                                mAirDa.close();
                            }
                        }
                    }
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> diverEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(LOG_TAG, "diverEditLauncher");
                // NOTE: Leave as is
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // do nothing
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> diverPickBuddyLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(LOG_TAG, "diverPickBuddyLauncher");
                // NOTE: Leave as is
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Do nothing
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> groupPickMeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(LOG_TAG, "groupPickMeLauncher");
                // NOTE: Leave as is
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Do nothing
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> rmvLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(LOG_TAG, "rmvLauncher");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    mState = data.getParcelableExtra(MyConstants.STATE);
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> waiverLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(LOG_TAG, "waiverLauncher");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = preferences.edit();

                    // The Waiver has not been Accepted yet
                    // The Waiver has been approved for the first time
                    edit.putBoolean(getString(R.string.code_waiver_accepted), Boolean.TRUE);
                    edit.apply();
                }
            });

    // Load database from resource files
    public void GetOrCreateCylinderTypeList() {
        Log.d(LOG_TAG, "GetOrCreateCylinderTypeList");

        ArrayList<CylinderType> cylinderTypeList;
        String[] cylinderTypes;
        String[] cylinderTypesDescription;
        String[] cylinderTypesVolume;
        String[] cylinderTypesRatedPressure;
        if (dropCylinderType) {
            mAirDa.dropCylinderType();
        }
        cylinderTypeList = mAirDa.getAllCylinderTypes();
        if (cylinderTypeList.size() == MyConstants.ZERO_I) {
            if ( mUnit.equals(MyConstants.IMPERIAL)) {
                cylinderTypes = getResources().getStringArray(R.array.cylinderType);
                cylinderTypesDescription = getResources().getStringArray(R.array.cylinderTypeDescription);
                cylinderTypesVolume = getResources().getStringArray(R.array.cylinderTypeVolume);
                cylinderTypesRatedPressure = getResources().getStringArray(R.array.cylinderTypeRatedPressure);
            } else {
                cylinderTypes = getResources().getStringArray(R.array.cylinderTypeMetric);
                cylinderTypesDescription = getResources().getStringArray(R.array.cylinderTypeDescriptionMetric);
                cylinderTypesVolume = getResources().getStringArray(R.array.cylinderTypeVolumeMetric);
                cylinderTypesRatedPressure = getResources().getStringArray(R.array.cylinderTypeRatedPressureMetric);
            }
            for (int i = 0; i < cylinderTypesDescription.length; i++) {
                CylinderType cylinderType = new CylinderType();
                cylinderType.setCylinderType(cylinderTypes[i]);
                cylinderType.setDescription(cylinderTypesDescription[i]);
                cylinderType.setVolume(Double.valueOf(cylinderTypesVolume[i]));
                cylinderType.setRatedPressure(Double.valueOf(cylinderTypesRatedPressure[i]));
                cylinderTypeList.add(cylinderType);
                mAirDa.createCylinderType(cylinderType);
                Log.d(LOG_TAG, "CylinderType created with CYLINDER_TYPE " + cylinderType.getCylinderType());
            }
        }
    }

    public void GetOrCreateDiveTypeList() {
        Log.d(LOG_TAG, "GetOrCreateDiveTypeList");

        ArrayList<DiveType> diveTypeList;
        String[] diveTypes;
        String[] diveTypesDescription;
        String[] diveTypesSortOrder;
        String[] diveTypesInPicker;
        if (dropDiveType) {
            mAirDa.dropDiveType();
        }
        diveTypeList = mAirDa.getAllDiveTypes();
        if (diveTypeList.size() == MyConstants.ZERO_I) {
            diveTypes = getResources().getStringArray(R.array.diveType);
            diveTypesDescription = getResources().getStringArray(R.array.diveTypeDescription);
            diveTypesSortOrder = getResources().getStringArray(R.array.diveTypeSortOrder);
            diveTypesInPicker = getResources().getStringArray(R.array.diveTypeInPicker);
            for (int i = 0; i < diveTypesDescription.length; i++) {
                DiveType diveType = new DiveType();
                diveType.setDiveType(diveTypes[i]);
                diveType.setDescription(diveTypesDescription[i]);
                diveType.setSortOrder(Integer.valueOf(diveTypesSortOrder[i]));
                diveType.setInPicker(diveTypesInPicker[i]);
                diveTypeList.add(diveType);
                mAirDa.createDiveType(diveType);
                Log.d(LOG_TAG, "DiveType created with DIVE_TYPE " + diveType.getDiveType());
            }
        }
    }

    public void GetOrCreateDynamicSpinnerList() {
        Log.d(LOG_TAG, "GetOrCreateDynamicSpinnerList");

        ArrayList<DynamicSpinner> dynamicSpinnerList;
        String[] dynamicSpinnerTypes;
        String[] dynamicSpinnerSystemDefined;
        String[] dynamicSpinnerText;
        if (dropDynamicSpinner) {
            mAirDa.dropDynamicSpinner();
        }
        dynamicSpinnerList = mAirDa.getAllDynamicSpinners();
        if (dynamicSpinnerList.size() == MyConstants.ZERO_I) {
            dynamicSpinnerTypes = getResources().getStringArray(R.array.dynamicSpinnerType);
            dynamicSpinnerSystemDefined = getResources().getStringArray(R.array.dynamicSpinnerSystemDelivered);
            dynamicSpinnerText = getResources().getStringArray(R.array.dynamicSpinnerText);
            for (int i = 0; i < dynamicSpinnerTypes.length; i++) {
                DynamicSpinner dynamicSpinner = new DynamicSpinner();
                dynamicSpinner.setSpinnerType(dynamicSpinnerTypes[i]);
                dynamicSpinner.setSystemDefined(dynamicSpinnerSystemDefined[i]);
                dynamicSpinner.setSpinnerText(dynamicSpinnerText[i]);
                dynamicSpinnerList.add(dynamicSpinner);
                mAirDa.createDynamicSpinner(dynamicSpinner);
                Log.d(LOG_TAG, "DynamicSpinner created with DYNAMIC_SPINNER " + dynamicSpinner.getSpinnerType() + " AND " + dynamicSpinner.getSpinnerText());
            }
        }
    }

    public void GetOrCreateGroupTypeList() {
        Log.d(LOG_TAG, "GetOrCreateGroupTypeList");

        ArrayList<GrouppType> grouppTypeList;
        String[] groupTypes;
        String[] groupTypesDescription;
        String[] groupTypesSystemDefined;
        if (dropGroupType) {
            mAirDa.dropGroupType();
        }
        grouppTypeList = mAirDa.getAllGroupTypes();
        if (grouppTypeList.size() == MyConstants.ZERO_I) {
            groupTypes = getResources().getStringArray(R.array.grouppType);
            groupTypesDescription = getResources().getStringArray(R.array.groupTypeDescription);
            groupTypesSystemDefined = getResources().getStringArray(R.array.groupTypeSystemDefined);
            for (int i = 0; i < groupTypesDescription.length; i++) {
                GrouppType grouppType = new GrouppType();
                grouppType.setGroupType(groupTypes[i]);
                grouppType.setDescription(groupTypesDescription[i]);
                grouppType.setSystemDefined(groupTypesSystemDefined[i]);
                grouppTypeList.add(grouppType);
                mAirDa.createGroupType(grouppType);
                Log.d(LOG_TAG, "GrouppType created with GROUP_TYPE " + grouppType.getGroupType());
            }
        }
    }

    public void GetOrCreateSegmentTypeList() {
        Log.d(LOG_TAG, "GetOrCreateSegmentTypeList");

        ArrayList<SegmentType> segmentTypeList;
        String[] segmentTypes;
        String[] segmentTypesDescription;
        String[] segmentTypesOrderNo;
        String[] segmentTypesDirection;
        String[] segmentTypesShowResult;
        String[] segmentTypesSystemDefined;
        String[] segmentTypesStatus;
        if (dropSegmentType) {
            mAirDa.dropSegmentType();
        }
        segmentTypeList = mAirDa.getAllSegmentTypes();
        if (segmentTypeList.size() == MyConstants.ZERO_I) {
            segmentTypes = getResources().getStringArray(R.array.segmentType);
            segmentTypesDescription = getResources().getStringArray(R.array.segmentTypeDescription);
            segmentTypesOrderNo = getResources().getStringArray(R.array.segmentTypeOrderNo);
            segmentTypesDirection = getResources().getStringArray(R.array.segmentTypeDirection);
            segmentTypesShowResult = getResources().getStringArray(R.array.segmentTypeShowResult);
            segmentTypesSystemDefined = getResources().getStringArray(R.array.segmentTypeSystemDefined);
            segmentTypesStatus = getResources().getStringArray(R.array.segmentTypeStatus);
            for (int i = 0; i < segmentTypesDescription.length; i++) {
                SegmentType segmentType = new SegmentType();
                segmentType.setSegmentType(segmentTypes[i]);
                segmentType.setDescription(segmentTypesDescription[i]);
                segmentType.setOrderNo(Integer.parseInt(segmentTypesOrderNo[i]));
                segmentType.setDirection(segmentTypesDirection[i]);
                segmentType.setShowResult(segmentTypesShowResult[i]);
                segmentType.setSystemDefined(segmentTypesSystemDefined[i]);
                segmentType.setStatus(segmentTypesStatus[i]);
                segmentTypeList.add(segmentType);
                mAirDa.createSegmentType(segmentType);
                Log.d(LOG_TAG, "SegmentType created with SEGMENT_TYPE " + segmentType.getSegmentType());
            }
        }
    }

    public void GetOrCreateUsageTypeList() {
        Log.d(LOG_TAG, "GetOrCreateUsageTypeList");

        ArrayList<UsageType> usageTypeList;
        String[] usageTypes;
        String[] usageTypesDescription;
        String[] usageTypesSystemDefined;
        if (dropUsageType) {
            mAirDa.dropUsageType();
        }
        usageTypeList = mAirDa.getAllUsageTypes();
        if (usageTypeList.size() == MyConstants.ZERO_I) {
            usageTypes = getResources().getStringArray(R.array.usageType);
            usageTypesDescription = getResources().getStringArray(R.array.usageTypeDescription);
            usageTypesSystemDefined = getResources().getStringArray(R.array.usageTypeSystemDefined);
            for (int i = 0; i < usageTypesDescription.length; i++) {
                UsageType usageType = new UsageType();
                usageType.setUsageType(usageTypes[i]);
                usageType.setDescription(usageTypesDescription[i]);
                usageType.setSystemDefined(usageTypesSystemDefined[i]);
                usageTypeList.add(usageType);
                mAirDa.createUsageType(usageType);
                Log.d(LOG_TAG, "UsageType created with USAGE_TYPE " + usageType.getUsageType());
            }
        }
    }

    public void GetOrCreateDiverList() {
        Log.d(LOG_TAG, "GetOrCreateDiverList");

        ArrayList<Diver> diverList;
        String[] diversNo;
        String[] diversLastName;
        String[] diversMiddleName;
        String[] diversFirstName;
        String[] diversGender;
        String[] diversPhone;
        String[] diversEmail;
        String[] diversCertificationBody;
        String[] diversCertificationLevel;
        String[] diversMaxDepthAllowed;
        if (dropDiver) {
            mAirDa.dropDiver();
        }

        diverList = mAirDa.getAllDivers(MyConstants.MINUS_ONE_L, MyConstants.MINUS_ONE_L, MyConstants.ZERO_INT);
        if (diverList.size() == MyConstants.ZERO_I) {
            diversNo = getResources().getStringArray(R.array.diverNo);
            diversLastName = getResources().getStringArray(R.array.diverLastName);
            diversMiddleName = getResources().getStringArray(R.array.diverMiddleName);
            diversFirstName = getResources().getStringArray(R.array.diverFirstName);
            diversGender = getResources().getStringArray(R.array.diverGender);
            diversPhone = getResources().getStringArray(R.array.diverPhone);
            diversEmail = getResources().getStringArray(R.array.diverEmail);
            diversCertificationBody = getResources().getStringArray(R.array.diverCertificationBody);
            diversCertificationLevel = getResources().getStringArray(R.array.diverCertificationLevel);
            diversMaxDepthAllowed = getResources().getStringArray(R.array.diverMaxDepthAllowed);
            for (int i = 0; i < diversLastName.length; i++) {
                Diver diver = new Diver();
                diver.setContext(this);
                diver.setDiverNo(valueOf(diversNo[i]));
                diver.setLastName(diversLastName[i]);
                diver.setMiddleName(diversMiddleName[i]);
                diver.setFirstName(diversFirstName[i]);
                // 1 = TRUE = Man; 0 = FALSE = Woman
                diver.setGender((Integer.parseInt(diversGender[i]) == MyConstants.ONE_I));
                diver.setBirthDate(MyFunctions.getBirthDate());
                diver.setPhone(diversPhone[i]);
                diver.setEmail(diversEmail[i]);
                diver.setCertificationBody(diversCertificationBody[i]);
                diver.setCertificationLevel(diversCertificationLevel[i]);
                diver.setMaxDepthAllowed(Double.valueOf(diversMaxDepthAllowed[i]));
                diverList.add(diver);
                mAirDa.createDiver(diver,false);
                Log.d(LOG_TAG, "Diver created with DIVER_NO " + diver.getDiverNo());
            }
        }
    }

    public void GetOrCreateStateList() {
        Log.d(LOG_TAG, "GetOrCreateStateList");

        ArrayList<State> stateList;
        String[] statesNo;
        String[] statesDiveType;
        String[] statesBuddyDiveNo;
        String[] statesMySac;
        String[] statesMyRmv;
        String[] statesMyGroup;
        String[] statesMyBuddySac;
        String[] statesMyBuddyRmv;
        String[] statesMyBuddyGroup;
        if (dropState) {
            mAirDa.dropState();
        }
        stateList = mAirDa.getAllStates();
        if (stateList.size() == MyConstants.ZERO_I) {
            statesNo = getResources().getStringArray(R.array.stateNo);
            statesDiveType = getResources().getStringArray(R.array.stateDiveType);
            statesBuddyDiveNo = getResources().getStringArray(R.array.stateBuddyDiverNo);
            statesMySac = getResources().getStringArray(R.array.stateMySac);
            statesMyRmv = getResources().getStringArray(R.array.stateMyRmv);
            statesMyGroup = getResources().getStringArray(R.array.stateMyGroup);
            statesMyBuddySac = getResources().getStringArray(R.array.stateMyBuddySac);
            statesMyBuddyRmv = getResources().getStringArray(R.array.stateMyBuddyRmv);
            statesMyBuddyGroup = getResources().getStringArray(R.array.stateMyBuddyGroup);
            for (int i = 0; i < statesNo.length; i++) {
                State state = new State();
                state.setStateNo(Integer.parseInt(statesNo[i]));
                state.setDiveType(statesDiveType[i]);
                state.setMySac(Double.valueOf(statesMySac[i]));
                state.setMyRmv(Double.valueOf(statesMyRmv[i]));
                state.setMyGroup(parseLong(statesMyGroup[i]));
                state.setMyBuddySac(Double.valueOf(statesMyBuddySac[i]));
                state.setMyBuddyRmv(Double.valueOf(statesMyBuddyRmv[i]));
                state.setBuddyDiverNo(Integer.parseInt(statesBuddyDiveNo[i]));
                state.setMyBuddyGroup(parseLong(statesMyBuddyGroup[i]));
                stateList.add(state);
                mAirDa.createState(state);
                Log.d(LOG_TAG, "State created with STATE_NO " + state.getStateNo());
            }
        }
    }

    // Load my history into the database from resource files
    public void GetOrCreateMyCylinderList() {
        Log.d(LOG_TAG, "GetOrCreateMyCylinderList");

        String[] cylinderDiverNos;
        String[] cylinderTypes;
        String[] cylinderVolumes;
        String[] cylinderRatedPressures;
        String[] cylinderBrands;
        String[] cylinderModels;
        String[] cylinderSerialNos;
        String[] cylinderLastVips;
        String[] cylinderLastHydros;
        String[] cylinderColors;
        String[] cylinderWeightFulls;
        String[] cylinderWeightEmpties;
        String[] cylinderBuoyancyFulls;
        String[] cylinderBuoyancyEmpties;
        cylinderDiverNos = getResources().getStringArray(R.array.cylinderDiverNo);
        cylinderTypes = getResources().getStringArray(R.array.cylinderCylinderType);
        cylinderVolumes = getResources().getStringArray(R.array.cylinderCylinderVolume);
        cylinderRatedPressures = getResources().getStringArray(R.array.cylinderRatedPressure);
        cylinderBrands = getResources().getStringArray(R.array.cylinderBrand);
        cylinderModels = getResources().getStringArray(R.array.cylinderModel);
        cylinderSerialNos = getResources().getStringArray(R.array.cylinderSerialNo);
        cylinderLastVips = getResources().getStringArray(R.array.cylinderLastVip);
        cylinderLastHydros = getResources().getStringArray(R.array.cylinderLastHydro);
        cylinderColors = getResources().getStringArray(R.array.cylinderColor);
        cylinderWeightFulls = getResources().getStringArray(R.array.cylinderWeightFull);
        cylinderWeightEmpties = getResources().getStringArray(R.array.cylinderWeightEmpty);
        cylinderBuoyancyFulls = getResources().getStringArray(R.array.cylinderBuoyancyFull);
        cylinderBuoyancyEmpties = getResources().getStringArray(R.array.cylinderBuoyancyEmpty);
        for (int i = 0; i < cylinderDiverNos.length; i++) {
            Cylinder cylinder = new Cylinder();
            cylinder.setDiverNo(valueOf(cylinderDiverNos[i]));
            cylinder.setCylinderTypeLoad(String.valueOf(cylinderTypes[i]));
            cylinder.setVolume(Double.valueOf(cylinderVolumes[i]));
            cylinder.setRatedPressure(Double.valueOf(cylinderRatedPressures[i]));
            cylinder.setBrand(String.valueOf(cylinderBrands[i]));
            cylinder.setModel(String.valueOf(cylinderModels[i]));
            cylinder.setSerialNo(String.valueOf(cylinderSerialNos[i]));
            cylinder.setLastVip(MyFunctions.convertDateFromStringToDate(getApplicationContext(),String.valueOf(cylinderLastVips[i])));
            cylinder.setLastHydro(MyFunctions.convertDateFromStringToDate(getApplicationContext(),String.valueOf(cylinderLastHydros[i])));
            cylinder.setTankColor(String.valueOf(cylinderColors[i]));
            cylinder.setWeightFull(Double.valueOf(cylinderWeightFulls[i]));
            cylinder.setWeightEmpty(Double.valueOf(cylinderWeightEmpties[i]));
            cylinder.setBuoyancyFull(Double.valueOf(cylinderBuoyancyFulls[i]));
            cylinder.setBuoyancyEmpty(Double.valueOf(cylinderBuoyancyEmpties[i]));
            mAirDa.createCylinder(cylinder, false);
            Log.d(LOG_TAG, "Cylinder created with CYLINDER_NO " + cylinder.getCylinderNo());
        }
    }

    public void GetOrCreateMyDiveList() {
        Log.d(LOG_TAG, "GetOrCreateMyDiveList");

        String[] diveTypes;
        String[] salinity;
        String[] dates;
        String[] timesIn;
        String[] bottomTimes;
        String[] averageDepths;
        String[] logBookNos;
        String[] status;
        String[] altitudes;
        String[] locations;
        String[] diveSites;
        String[] diveBoats;
        String[] purposes;
        String[] visibilities;
        String[] maximumDepths;
        String[] suits;
        String[] weights;
        String[] airTemps;
        String[] waterTempSurfaces;
        String[] waterTempBottoms;
        String[] waterTempAverages;
        String[] notes;
        String[] environments;
        String[] platforms;
        String[] weathers;
        String[] conditions;

        diveTypes = getResources().getStringArray(R.array.diveDiveType);
        salinity = getResources().getStringArray(R.array.diveDiveSalinity);
        dates = getResources().getStringArray(R.array.diveDiveDate);
        timesIn = getResources().getStringArray(R.array.diveDiveTimeIn);
        bottomTimes = getResources().getStringArray(R.array.diveDiveBottomTime);
        averageDepths = getResources().getStringArray(R.array.diveDiveAverageDepth);
        logBookNos = getResources().getStringArray(R.array.diveDiveLogBookNo);
        status = getResources().getStringArray(R.array.diveDiveStatus);
        altitudes = getResources().getStringArray(R.array.diveDiveAltitude);
        locations = getResources().getStringArray(R.array.diveDiveLocation);
        diveSites = getResources().getStringArray(R.array.diveDiveDiveSite);
        diveBoats = getResources().getStringArray(R.array.diveDiveDiveBoat);
        purposes = getResources().getStringArray(R.array.diveDivePurpose);
        visibilities = getResources().getStringArray(R.array.diveDiveVisibility);
        maximumDepths = getResources().getStringArray(R.array.diveDiveMaximumDepth);
        suits = getResources().getStringArray(R.array.diveDiveSuit);
        weights = getResources().getStringArray(R.array.diveDiveWeight);
        airTemps = getResources().getStringArray(R.array.diveDiveAirTemp);
        waterTempSurfaces = getResources().getStringArray(R.array.diveDiveWaterTempSurface);
        waterTempBottoms = getResources().getStringArray(R.array.diveDiveWaterTempBottom);
        waterTempAverages = getResources().getStringArray(R.array.diveDiveWaterTempAverage);
        notes = getResources().getStringArray(R.array.diveDiveNote);
        environments = getResources().getStringArray(R.array.diveDiveEnvironment);
        platforms = getResources().getStringArray(R.array.diveDivePlatform);
        weathers = getResources().getStringArray(R.array.diveDiveWeather);
        conditions = getResources().getStringArray(R.array.diveDiveCondition);

        for (int i = 0; i < diveTypes.length; i++) {
            Dive dive = new Dive();
            dive.setDiveTypeLoad(String.valueOf(diveTypes[i]));
            dive.setSalinity(Integer.valueOf(salinity[i]).equals(MyConstants.ONE_INT));
            dive.setDate(MyFunctions.convertDateFromStringToDate(getApplicationContext(),String.valueOf(dates[i])));
            // Format looks like 16:09
            dive.setTimeIn(String.valueOf(timesIn[i]));
            dive.setHour(MyFunctions.getHour(dive.getTimeIn()));
            dive.setMinute(MyFunctions.getMinute(dive.getTimeIn()));
            dive.setBottomTime(Double.valueOf(bottomTimes[i]));
            dive.setAverageDepth(Double.valueOf(averageDepths[i]));
            dive.setLogBookNo(Integer.parseInt(logBookNos[i]));
            dive.setStatusLoad(String.valueOf(status[i]));
            dive.setAltitude(Integer.parseInt(altitudes[i]));
            dive.setLocation(String.valueOf(locations[i]));
            dive.setDiveSite(String.valueOf(diveSites[i]));
            dive.setDiveBoat(String.valueOf(diveBoats[i]));
            dive.setPurpose(String.valueOf(purposes[i]));
            dive.setVisibility(String.valueOf(visibilities[i]));
            dive.setMaximumDepth(Double.valueOf(maximumDepths[i]));
            dive.setSuitLoad(String.valueOf(suits[i]));
            dive.setWeight(Double.valueOf(weights[i]));
            dive.setAirTemp(Double.valueOf(airTemps[i]));
            dive.setWaterTempSurface(Double.valueOf(waterTempSurfaces[i]));
            dive.setWaterTempBottom(Double.valueOf(waterTempBottoms[i]));
            dive.setWaterTempAverage(Double.valueOf(waterTempAverages[i]));
            dive.setNoteSummary(String.valueOf(notes[i]));
            dive.setEnvironmentLoad(String.valueOf(environments[i]));
            dive.setPlatformLoad(String.valueOf(platforms[i]));
            dive.setWeatherLoad(String.valueOf(weathers[i]));
            dive.setConditionLoad(String.valueOf(conditions[i]));
            mAirDa.createDive(dive, false);
            Log.d(LOG_TAG, "Dive created with DIVE_NO " + dive.getDiveNo());
        }
    }

    public void GetOrCreateMyDivePlanList() {
        Log.d(LOG_TAG, "GetOrCreateMyDivePlanList");

        String[] divePlanNos;
        String[] diveNos;
        String[] orderNos;
        String[] depths;
        String[] minutes;
        divePlanNos = getResources().getStringArray(R.array.divePlanDivePlanNo);
        diveNos = getResources().getStringArray(R.array.divePlanDiveNo);
        orderNos = getResources().getStringArray(R.array.divePlanOrderNo);
        depths = getResources().getStringArray(R.array.divePlanDepth);
        minutes = getResources().getStringArray(R.array.divePlanMinute);
        for (int i = 0; i < divePlanNos.length; i++) {
            DivePlan divePlan = new DivePlan();
            divePlan.setDivePlanNo(parseLong(divePlanNos[i]));
            divePlan.setDiveNo(parseLong(diveNos[i]));
            divePlan.setOrderNo(parseLong(orderNos[i]));
            divePlan.setDepth(Double.valueOf(depths[i]));
            divePlan.setMinute(Integer.parseInt(minutes[i]));
            mAirDa.createDivePlan(divePlan, false);
            Log.d(LOG_TAG, "DivePlan created with DIVE_PLAN_NO " + divePlan.getDivePlanNo());
        }
    }

    public void GetOrCreateMyDiverDiveList() {
        Log.d(LOG_TAG, "GetOrCreateMyDiverDiveList");

        String[] diverNos;
        String[] diveNos;
        String[] rmvs;
        diverNos = getResources().getStringArray(R.array.diverDiveDiverNo);
        diveNos = getResources().getStringArray(R.array.diverDiveDiveNo);
        rmvs = getResources().getStringArray(R.array.diverDiveRmv);
        for (int i = 0; i < diverNos.length; i++) {
            DiverDive diverDive = new DiverDive();
            diverDive.setDiverNo(parseLong(diverNos[i]));
            diverDive.setDiveNo(parseLong(diveNos[i]));
            diverDive.setRmv(Double.valueOf(rmvs[i]));
            mAirDa.createDiverDive(diverDive);
            Log.d(LOG_TAG, "DiverDive created with DIVER_NO " + diverDive.getDiverNo() + " DIVE_NO " + diverDive.getDiveNo());
        }
    }

    public void GetOrCreateMyDiverDiveGroupList() {
        Log.d(LOG_TAG, "GetOrCreateMyDiverDiveGroupList");

        String[] diverNos;
        String[] diveNos;
        String[] groupNos;
        String[] sacs;
        diverNos = getResources().getStringArray(R.array.diverDiveGroupDiverNo);
        groupNos = getResources().getStringArray(R.array.diverDiveGroupGroupNo);
        diveNos = getResources().getStringArray(R.array.diverDiveGroupDiveNo);
        sacs = getResources().getStringArray(R.array.diverDiveGroupSac);
        for (int i = 0; i < diverNos.length; i++) {
            DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
            diverDiveGroup.setDiverNo(parseLong(diverNos[i]));
            diverDiveGroup.setDiveNo(parseLong(diveNos[i]));
            diverDiveGroup.setGroupNo(parseLong(groupNos[i]));
            diverDiveGroup.setSac(Double.valueOf(sacs[i]));
            mAirDa.createDiverDiveGroup(diverDiveGroup);
            Log.d(LOG_TAG, "DiverDiveGroup created with DIVER_NO " + diverDiveGroup.getDiverNo() + " DIVE_NO " + diverDiveGroup.getDiveNo() + " GROUP_NO " + diverDiveGroup.getGroupNo());
        }
    }

    public void GetOrCreateMyDiverDiverGroupCylinderList() {
        Log.d(LOG_TAG, "GetOrCreateMyDiverDiverGroupCylinderList");

        String[] diverNos;
        String[] diveNos;
        String[] groupNos;
        String[] cylinderNos;
        String[] beginningPressures;
        String[] endingPressures;
        String[] o2s;
        String[] ns;
        String[] hes;
        diverNos = getResources().getStringArray(R.array.diverDiveGroupCylinderDiverNo);
        groupNos = getResources().getStringArray(R.array.diverDiveGroupCylinderGroupNo);
        diveNos = getResources().getStringArray(R.array.diverDiveGroupCylinderDiveNo);
        cylinderNos = getResources().getStringArray(R.array.diverDiveGroupCylinderCylinderNo);
        beginningPressures = getResources().getStringArray(R.array.diverDiveGroupCylinderBeginningPressure);
        endingPressures = getResources().getStringArray(R.array.diverDiveGroupCylinderEndingPressure);
        o2s = getResources().getStringArray(R.array.diverDiveGroupCylinderO2);
        ns = getResources().getStringArray(R.array.diverDiveGroupCylinderN);
        hes = getResources().getStringArray(R.array.diverDiveGroupCylinderHe);
        for (int i = 0; i < diverNos.length; i++) {
            DiverDiveGroupCyl diverDiveGroupCyl = new DiverDiveGroupCyl();
            diverDiveGroupCyl.setDiverNo(parseLong(diverNos[i]));
            diverDiveGroupCyl.setDiveNo(parseLong(diveNos[i]));
            diverDiveGroupCyl.setGroupNo(parseLong(groupNos[i]));
            diverDiveGroupCyl.setCylinderNo(parseLong(cylinderNos[i]));
            diverDiveGroupCyl.setBeginningPressure(Double.valueOf(beginningPressures[i]));
            diverDiveGroupCyl.setEndingPressure(Double.valueOf(endingPressures[i]));
            diverDiveGroupCyl.setO2(Integer.parseInt(o2s[i]));
            diverDiveGroupCyl.setN(Integer.parseInt(ns[i]));
            diverDiveGroupCyl.setHe(Integer.parseInt(hes[i]));
            diverDiveGroupCyl.setUsageType(getString(R.string.cd_blank));
            mAirDa.createDiverDiveGroupCylinder(diverDiveGroupCyl);
            Log.d(LOG_TAG, "DiverDiveGroupCylinder created with DIVER_NO " + diverDiveGroupCyl.getDiverNo() + " DIVE_NO " + diverDiveGroupCyl.getDiveNo() + " GROUP_NO " + diverDiveGroupCyl.getGroupNo() + " CYLINDER_NO " + diverDiveGroupCyl.getCylinderNo());
        }
    }

    public void GetOrCreateMyGroupList() {
        Log.d(LOG_TAG, "GetOrCreateMyGroupList");

        String[] groupNos;
        String[] diverNos;
        String[] groupTypes;
        String[] descriptions;
        groupNos = getResources().getStringArray(R.array.groupGroupNo);
        diverNos = getResources().getStringArray(R.array.groupDiverNo);
        groupTypes = getResources().getStringArray(R.array.groupGroupType);
        descriptions = getResources().getStringArray(R.array.groupGroupDescription);
        for (int i = 0; i < diverNos.length; i++) {
            Groupp groupp = new Groupp();
            groupp.setGroupNo(valueOf(groupNos[i]));
            groupp.setDiverNo(valueOf(diverNos[i]));
            groupp.setGroupTypeLoad(String.valueOf(groupTypes[i]));
            groupp.setDescription(String.valueOf(descriptions[i]));
            mAirDa.createGroupp(groupp, false);
            Log.d(LOG_TAG, "Groupp created with GROUP_NO " + groupp.getGroupNo());
        }
    }

    public void GetOrCreateMyGroupCylinderList() {
        Log.d(LOG_TAG, "GetOrCreateMyGroupCylinderList");

        String[] groupNos;
        String[] cylinderNos;
        String[] usageTypes;
        groupNos = getResources().getStringArray(R.array.groupCylinderGroupNo);
        cylinderNos = getResources().getStringArray(R.array.groupCylinderCylinderNo);
        usageTypes = getResources().getStringArray(R.array.groupCylinderUsageType);
        for (int i = 0; i < groupNos.length; i++) {
            GrouppCylinder grouppCylinder = new GrouppCylinder();
            grouppCylinder.setGroupNo(valueOf(groupNos[i]));
            grouppCylinder.setCylinderNo(valueOf(cylinderNos[i]));
            grouppCylinder.setUsageTypeLoad(String.valueOf(usageTypes[i]));
            mAirDa.createGroupCylinder(grouppCylinder);
            Log.d(LOG_TAG, "GrouppCylinder created with GROUP_NO " + grouppCylinder.getGroupNo() + " CYLINDER_NO " + grouppCylinder.getCylinderNo());
        }
    }
    // End of loading My History

    // Pre load common Equipment Group
    public void GetOrCreateCommonGroupList() {
        Log.d(LOG_TAG, "GetOrCreateCommonGroupList");

        ArrayList<Groupp> grouppList;
        String[] groupNos;
        String[] diverNos;
        String[] groupTypes;
        String[] descriptions;

        if (dropGroup) {
            mAirDa.dropGroup();
        }

        grouppList = mAirDa.getAllGroups();
        if (grouppList.size() == MyConstants.ZERO_I) {
            if ( mUnit.equals(MyConstants.IMPERIAL)) {
                groupNos = getResources().getStringArray(R.array.commonGroupGroupNo);
                diverNos = getResources().getStringArray(R.array.commonGroupDiverNo);
                groupTypes = getResources().getStringArray(R.array.commonGroupGroupType);
                descriptions = getResources().getStringArray(R.array.commonGroupGroupDescription);
            } else {
                groupNos = getResources().getStringArray(R.array.commonGroupGroupNoMetric);
                diverNos = getResources().getStringArray(R.array.commonGroupDiverNoMetric);
                groupTypes = getResources().getStringArray(R.array.commonGroupGroupTypeMetric);
                descriptions = getResources().getStringArray(R.array.commonGroupGroupDescriptionMetric);
            }
            for (int i = 0; i < diverNos.length; i++) {
                Groupp groupp = new Groupp();
                groupp.setGroupNo(valueOf(groupNos[i]));
                groupp.setDiverNo(valueOf(diverNos[i]));
                groupp.setGroupTypeLoad(String.valueOf(groupTypes[i]));
                groupp.setDescription(String.valueOf(descriptions[i]));
                mAirDa.createGroupp(groupp, false);
                Log.d(LOG_TAG, "Groupp created with GROUP_NO " + groupp.getGroupNo());
            }
        }
    }

    public void GetOrCreateCommonCylinderList() {
        Log.d(LOG_TAG, "GetOrCreateCommonCylinderList");

        ArrayList<Cylinder> cylinderList;
        String[] diverNos;
        String[] cylinderTypes;
        String[] cylinderVolumes;
        String[] cylinderRatedPressures;

        String[] cylinderBrands;
        String[] cylinderModels;
        String[] cylinderSerialNos;
        String[] cylinderLastVips;
        String[] cylinderLastHydros;
        String[] cylinderTankColors;
        String[] cylinderWeightFulls;
        String[] cylinderWeightEmpties;
        String[] cylinderBuoyancyFulls;
        String[] cylinderBuoyancyEmpties;

        if (dropCylinder) {
            mAirDa.dropCylinder();
        }

        cylinderList = mAirDa.getAllCylinders();
        if (cylinderList.size() == MyConstants.ZERO_I) {
            if ( mUnit.equals(MyConstants.IMPERIAL)) {
                diverNos = getResources().getStringArray(R.array.commonCylinderDiverNo);
                cylinderTypes = getResources().getStringArray(R.array.commonCylinderCylinderType);
                cylinderVolumes = getResources().getStringArray(R.array.commonCylinderCylinderVolume);
                cylinderRatedPressures = getResources().getStringArray(R.array.commonCylinderRatedPressure);
                cylinderBrands = getResources().getStringArray(R.array.commonCylinderBrand);
                cylinderModels = getResources().getStringArray(R.array.commonCylinderModel);
                cylinderSerialNos = getResources().getStringArray(R.array.commonCylinderSerialNumber);
                cylinderLastVips = getResources().getStringArray(R.array.commonCylinderLastVip);
                cylinderLastHydros = getResources().getStringArray(R.array.commonCylinderLastHydro);
                cylinderTankColors = getResources().getStringArray(R.array.commonCylinderTankColor);
                cylinderWeightFulls = getResources().getStringArray(R.array.commonCylinderWeightFull);
                cylinderWeightEmpties = getResources().getStringArray(R.array.commonCylinderWeightEmpty);
                cylinderBuoyancyFulls = getResources().getStringArray(R.array.commonCylinderBuoyancyFull);
                cylinderBuoyancyEmpties = getResources().getStringArray(R.array.commonCylinderBuoyancyEmpty);
            } else {
                diverNos = getResources().getStringArray(R.array.commonCylinderDiverNoMetric);
                cylinderTypes = getResources().getStringArray(R.array.commonCylinderCylinderTypeMetric);
                cylinderVolumes = getResources().getStringArray(R.array.commonCylinderCylinderVolumeMetric);
                cylinderRatedPressures = getResources().getStringArray(R.array.commonCylinderRatedPressureMetric);
                cylinderBrands = getResources().getStringArray(R.array.commonCylinderBrandMetric);
                cylinderModels = getResources().getStringArray(R.array.commonCylinderModelMetric);
                cylinderSerialNos = getResources().getStringArray(R.array.commonCylinderSerialNumberMetric);
                cylinderLastVips = getResources().getStringArray(R.array.commonCylinderLastVipMetric);
                cylinderLastHydros = getResources().getStringArray(R.array.commonCylinderLastHydroMetric);
                cylinderTankColors = getResources().getStringArray(R.array.commonCylinderTankColorMetric);
                cylinderWeightFulls = getResources().getStringArray(R.array.commonCylinderWeightFullMetric);
                cylinderWeightEmpties = getResources().getStringArray(R.array.commonCylinderWeightEmptyMetric);
                cylinderBuoyancyFulls = getResources().getStringArray(R.array.commonCylinderBuoyancyFullMetric);
                cylinderBuoyancyEmpties = getResources().getStringArray(R.array.commonCylinderBuoyancyEmptyMetric);
            }
            for (int i = 0; i < diverNos.length; i++) {
                Cylinder cylinder = new Cylinder();
                cylinder.setDiverNo(valueOf(diverNos[i]));
                cylinder.setCylinderTypeLoad(String.valueOf(cylinderTypes[i]));
                cylinder.setVolume(Double.valueOf(cylinderVolumes[i]));
                cylinder.setRatedPressure(Double.valueOf(cylinderRatedPressures[i]));
                cylinder.setBrand(String.valueOf(cylinderBrands[i]));
                cylinder.setModel(String.valueOf(cylinderModels[i]));
                cylinder.setSerialNo(String.valueOf(cylinderSerialNos[i]));
                cylinder.setLastVip(MyFunctions.convertDateFromStringToDate(getApplicationContext(),String.valueOf(cylinderLastVips[i])));
                cylinder.setLastHydro(MyFunctions.convertDateFromStringToDate(getApplicationContext(),String.valueOf(cylinderLastHydros[i])));
                cylinder.setTankColor(String.valueOf(cylinderTankColors[i]));
                cylinder.setWeightFull(Double.valueOf(cylinderWeightFulls[i]));
                cylinder.setWeightEmpty(Double.valueOf(cylinderWeightEmpties[i]));
                cylinder.setBuoyancyFull(Double.valueOf(cylinderBuoyancyFulls[i]));
                cylinder.setBuoyancyEmpty(Double.valueOf(cylinderBuoyancyEmpties[i]));

                mAirDa.createCylinder(cylinder, false);
                Log.d(LOG_TAG, "CylinderType created with CYLINDER_NO " + cylinder.getCylinderNo());
            }
        }
    }

    public void GetOrCreateCommonGroupCylinderList() {
        Log.d(LOG_TAG, "GetOrCreateCommonGroupCylinderList");

        ArrayList<GrouppCylinder> groupCylinderList;
        String[] groupNos;
        String[] cylinderNos;
        String[] usageTypes;

        if (dropGroupCylinder) {
            mAirDa.dropGroupCylinder();
        }

        groupCylinderList = mAirDa.getAllGroupCylinders();
        if (groupCylinderList.size() == MyConstants.ZERO_I) {
            if ( mUnit.equals(MyConstants.IMPERIAL)) {
                groupNos = getResources().getStringArray(R.array.commonGroupCylinderGroupNo);
                cylinderNos = getResources().getStringArray(R.array.commonGroupCylinderCylinderNo);
                usageTypes = getResources().getStringArray(R.array.commonGroupCylinderUsageType);
            } else {
                groupNos = getResources().getStringArray(R.array.commonGroupCylinderGroupNoMetric);
                cylinderNos = getResources().getStringArray(R.array.commonGroupCylinderCylinderNoMetric);
                usageTypes = getResources().getStringArray(R.array.commonGroupCylinderUsageTypeMetric);
            }
            for (int i = 0; i < groupNos.length; i++) {
                GrouppCylinder grouppCylinder = new GrouppCylinder();
                grouppCylinder.setGroupNo(valueOf(groupNos[i]));
                grouppCylinder.setCylinderNo(valueOf(cylinderNos[i]));
                grouppCylinder.setUsageTypeLoad(String.valueOf(usageTypes[i]));
                mAirDa.createGroupCylinder(grouppCylinder);
                Log.d(LOG_TAG, "GrouppCylinder created with GROUP_NO " + grouppCylinder.getGroupNo() + " CYLINDER_NO " + grouppCylinder.getCylinderNo());
            }
        }
    }
    // End of Pre loading common Equipment Group
}