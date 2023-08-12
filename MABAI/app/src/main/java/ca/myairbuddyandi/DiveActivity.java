package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Date;

import ca.myairbuddyandi.databinding.DiveComputerBinding;
import ca.myairbuddyandi.databinding.DiveGasBinding;
import ca.myairbuddyandi.databinding.DiveGearBinding;
import ca.myairbuddyandi.databinding.DiveEnvironmentBinding;
import ca.myairbuddyandi.databinding.DivePlanningBinding;
import ca.myairbuddyandi.databinding.DiveProblemBinding;
import ca.myairbuddyandi.databinding.DiveSummaryBinding;

/**
 * Created by Michel on 2017-01-04.
 * Holds all of the logic for the Dive Activity class
 */

public class DiveActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "DiveActivity";

    // Public

    // Protected

    // Private
    private int mPlanningPosition = 0;
    private int mSummaryPosition = 1;
    private int mEnvironmentPostion = 2;
    private int mGasPosition = 3;
    private int mGearPosition = 4;
    private int mProblemPosition = 5;
    private int mComputerPosition = 6;
    private int mGraphPosition = 7;
    private final AirDA mAirDa = new AirDA(this);
    private boolean mMyButton = true;
    private ArrayList<Dive> mDivePickList = new ArrayList<>();
    private Dive mDive = new Dive();
    private DiveComputerBinding mBindingComputer = null;
    private DiveGasBinding mBindingGas = null;
    private DiveGearBinding mBindingGear = null;
    private DiveEnvironmentBinding mBindingEnvironment = null;
    private DivePlanningBinding mBindingPlanning = null;
    private DiveProblemBinding mBindingProblem = null;
    private DiveSummaryBinding mBindingSummary = null;
    private Long mMyGroupNoOrig;
    private Long mMyBuddyDiverNoOrig;
    private Long mMyBuddyGroupNoOrig;
    private MyCalc mMyCalc;
    private final MyDialogs mDialogs = new MyDialogs();
    private DivePagerAdapter mDivePagerAdapter;
    private ViewPager2 mViewPager2;
    private State mState = new State();

    // End of variables

    @SuppressLint({"WrongViewCast", "NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        setContentView(R.layout.dive_pager_activity);

        // Get the views from dive_pager_activity since there is no DataBinding on this view
        Button cancelButton = findViewById(R.id.cancel_button);
        Button saveButton = findViewById(R.id.save_button);
        mViewPager2 = findViewById(R.id.dive_pager_activity);

        // Get the data from the Intent
        // TODO: Review all database logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mDive = getIntent().getParcelableExtra(MyConstants.DIVE,Dive.class);
            mState = getIntent().getParcelableExtra(MyConstants.STATE,State.class);
        } else {
            mDive = getIntent().getParcelableExtra(MyConstants.DIVE);
            mState = getIntent().getParcelableExtra(MyConstants.STATE);
        }
        assert mDive != null;
        mDive.setContext(this);

        if (mDive.getLogBookNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDive.getLogBookNo());
            }
        }

        mAirDa.openWithFKConstraintsEnabled();
        mAirDa.beginTransactionNonExclusive();

        // Set the data in the Spinner AnySymptom
        ArrayList<DynamicSpinner> anySymptomList = mAirDa.getDynamicSpinnerByType("AS");
        ArrayAdapter<DynamicSpinner> adapterAnySymptom = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, anySymptomList);
        adapterAnySymptom.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterAnySymptom(adapterAnySymptom);
        mDive.setItemsAnySymptom(anySymptomList);

        // Set the data in the Spinner Condition
        ArrayList<DynamicSpinner> conditionList = mAirDa.getDynamicSpinnerByType("CO");
        ArrayAdapter<DynamicSpinner> adapterCondition = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, conditionList);
        adapterCondition.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterCondition(adapterCondition);
        mDive.setItemsCondition(conditionList);

        // Set the data in the Spinner DiveType
        ArrayList<DiveType> diveTypeList = mAirDa.getAllDiveTypePickable();
        ArrayAdapter<DiveType> adapterDiveType = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, diveTypeList);
        adapterDiveType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterDiveType(adapterDiveType);
        mDive.setItemsDiveType(diveTypeList);

        // Set the data in the Spinner Environment
        ArrayList<DynamicSpinner> environmentList = mAirDa.getDynamicSpinnerByType("EN");
        ArrayAdapter<DynamicSpinner> adapterEnvironment = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, environmentList);
        adapterEnvironment.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterEnvironment(adapterEnvironment);
        mDive.setItemsEnvironment(environmentList);

        // Set the data in the Spinner ExposureAltitude
        ArrayList<DynamicSpinner> exposureAltitudeList = mAirDa.getDynamicSpinnerByType("EA");
        ArrayAdapter<DynamicSpinner> adapterExposureAltitude = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, exposureAltitudeList);
        adapterExposureAltitude.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterExposureAltitude(adapterExposureAltitude);
        mDive.setItemsExposureAltitude(exposureAltitudeList);

        // Set the data in the Spinner Malfunction
        ArrayList<DynamicSpinner> malfunctionList = mAirDa.getDynamicSpinnerByType("MA");
        ArrayAdapter<DynamicSpinner> adapterMalfunction = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, malfunctionList);
        adapterMalfunction.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterMalfunction(adapterMalfunction);
        mDive.setItemsMalfunction(malfunctionList);

        // Set the data in the Spinner Platform
        ArrayList<DynamicSpinner> platformList = mAirDa.getDynamicSpinnerByType("PL");
        ArrayAdapter<DynamicSpinner> adapterPlatform = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, platformList);
        adapterPlatform.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterPlatform(adapterPlatform);
        mDive.setItemsPlatform(platformList);

        // Set the data in the Spinner Problem
        ArrayList<DynamicSpinner> problemList = mAirDa.getDynamicSpinnerByType("PR");
        ArrayAdapter<DynamicSpinner> adapterProblem = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, problemList);
        adapterProblem.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterProblem(adapterProblem);
        mDive.setItemsProblem(problemList);

        // Set the data in the Spinner Status
        String[] itemsStatus = getResources().getStringArray(R.array.status_arrays);
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsStatus);
        adapterStatus.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterStatus(adapterStatus);
        mDive.setItemsStatus(itemsStatus);
        // NOTE: For future reference
        //spinnerStatus.setPaddingSafe(0, 0, 0, 0);

        // Set the data in the Spinner Suit
        ArrayList<DynamicSpinner> suitList = mAirDa.getDynamicSpinnerByType("SU");
        ArrayAdapter<DynamicSpinner> adapterSuit = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, suitList);
        adapterSuit.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterSuit(adapterSuit);
        mDive.setItemsSuit(suitList);

        // Set the data in the Spinner ThermalComfort
        ArrayList<DynamicSpinner> thermalComfortList = mAirDa.getDynamicSpinnerByType("TC");
        ArrayAdapter<DynamicSpinner> adapterThermalComfort = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, thermalComfortList);
        adapterThermalComfort.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterThermalComfort(adapterThermalComfort);
        mDive.setItemsThermalComfort(thermalComfortList);

        // Set the data in the Spinner Weather
        ArrayList<DynamicSpinner> weatherList = mAirDa.getDynamicSpinnerByType("WE");
        ArrayAdapter<DynamicSpinner> adapterWeather = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, weatherList);
        adapterWeather.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterWeather(adapterWeather);
        mDive.setItemsWeather(weatherList);

        // Set the data in the Spinner WorkLoad
        ArrayList<DynamicSpinner> workLoadList = mAirDa.getDynamicSpinnerByType("WL");
        ArrayAdapter<DynamicSpinner> adapterWorkLoad = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, workLoadList);
        adapterWorkLoad.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDive.setAdapterWorkLoad(adapterWorkLoad);
        mDive.setItemsWorkLoad(workLoadList);

        // Set the Dive Type
        if (mState.getDiveType().equals(MyConstants.LAST ) || mState.getDiveType().equals(MyConstants.AVERAGE) || mState.getDiveType().equals(MyConstants.LAST_10) ) {
            mDive.setDiveType(MyConstants.TYPICAL);
        } else {
            mDive.setDiveType(mState.getDiveType());
        }

        mDive.setMyBuddyDiverNo(mState.getBuddyDiverNo());
        mDive.setMyGroupNo(mState.getMyGroup());
        mDive.setMyBuddyGroupNo(mState.getMyBuddyGroup());

        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(this);
        } else {
            mMyCalc = new MyCalcMetric(this);
        }

        // Set my SAC and RMV
        mDive.setMySac((mState.getMySac().equals(MyConstants.ZERO_D)) ? mMyCalc.getSacDefault() : mState.getMySac());
        mDive.setMyRmv((mState.getMyRmv().equals(MyConstants.ZERO_D)) ? mMyCalc.getRmvDefault() : mState.getMyRmv());

        // Set my buddy's SAC and RMV
        mDive.setMyBuddySac((mState.getMyBuddySac().equals(MyConstants.ZERO_D)) ? mMyCalc.getSacDefault() : mState.getMyBuddySac());
        mDive.setMyBuddyRmv((mState.getMyBuddyRmv().equals(MyConstants.ZERO_D)) ? mMyCalc.getRmvDefault() : mState.getMyBuddyRmv());

        // Set the listeners
        cancelButton.setOnClickListener(view -> {
            if (mDive.getHasDataChanged()) {
                mDialogs.confirm(DiveActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
            } else {
                mAirDa.endTransaction();
                mAirDa.close();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        saveButton.setOnClickListener(view -> {
            // Validate data
            submitForm();
        });

        if (mDive.getDiveNo() == MyConstants.ZERO_L) {
            // Add mode (New Dive)
            // Set the default values
            mDive.setLogBookNo(mAirDa.getLastLogBookNo() + 1);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDive.getLogBookNo());
            }

            DiveLast diveLast = new DiveLast();
            mAirDa.getDiveLast(diveLast);

            if (diveLast.getLastDate().equals(MyConstants.ZERO_L)) {
                // Adding the very first dive
                // Use Today's date
                mDive.setDate(MyFunctions.getTodaysDate());
            } else {
                Date lastDate = MyFunctions.convertDateFromLongToDate(diveLast.getLastDate());
                lastDate = MyFunctions.formatDateDate(MyFunctions.getYear(lastDate),MyFunctions.getMonthOfYear(lastDate),MyFunctions.getDayOfMonth(lastDate));
                Date todaysDate = MyFunctions.getTodaysDate();
                todaysDate = MyFunctions.formatDateDate(MyFunctions.getYear(todaysDate),MyFunctions.getMonthOfYear(todaysDate),MyFunctions.getDayOfMonth(todaysDate));
                if (lastDate.equals(todaysDate)) {
                    // The last dive was on the same date as today
                    // Use the last date form the DataBase
                    // Transform the Dive Date from Integer/Long to a Date String
                    // Add last bottom time + 1:00 of surface interval
                    Date calculatedTime = MyFunctions.convertDateFromLongToDate(MyFunctions.addMinuteToDateTime(diveLast.getLastDate(), diveLast.getBottomTime() + 60.0));
                    Date now = MyFunctions.getNow();
                    if (calculatedTime.compareTo(now) > 0) {
                        // calculatedTime is in the future, use it
                        mDive.setDate(calculatedTime);
                    } else {
                        // calculatedTime is in the past, use current DateTime
                        mDive.setDate(now);
                    }
                } else {
                    // A new day, a new date
                    // Use today's date
                    mDive.setDate(MyFunctions.getTodaysDate());
                }
            }

            mDive.setHour(MyFunctions.getHour(mDive.getDate()));
            mDive.setMinute(MyFunctions.getMinute(mDive.getDate()));
            mDive.setTimeIn(MyFunctions.getTimeFromDate(getApplicationContext(),mDive.getDate())); // HH:MM or HH:MM AM/PM

            mDive.setStatus(getString(R.string.code_default_status)); // Plan
            mDive.setSalinity(true);
            mDive.setAverageDepth(MyConstants.ZERO_D);
            mDive.setAltitude(Integer.parseInt(getString(R.string.code_default_altitude)));

            mDive.setLocation(" ");
            mDive.setDiveSite(" ");
            mDive.setDiveBoat(" ");
            mDive.setPurpose(" ");

            mDive.setSuit("");
            mDive.setEnvironment("");
            mDive.setPlatform("");
            mDive.setWeather("");
            mDive.setCondition("");
            // 2023/08/2 DB_VERSION = 7 New columns
            mDive.setThermalComfort("");
            mDive.setWorkLoad("");
            mDive.setProblem("");
            mDive.setMalfunction("");
            mDive.setAnySymptom("");
            mDive.setExposureAltitude("");

            mDive.setMyDiverNo(MyConstants.ONE_L);

            mMyGroupNoOrig = MyConstants.ZERO_L;
            mMyBuddyDiverNoOrig = MyConstants.ZERO_L;
            mMyBuddyGroupNoOrig = MyConstants.ZERO_L;

            // DIVE
            // The dive will be committed upon the save
            // Adding the dive to be retrieve right away, within the same transaction
            addDive(mDive);

            // DIVER_DIVE
            // Me
            if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
                DiverDive diverDive = new DiverDive();
                diverDive.setDiverNo(MyConstants.ONE_L);
                diverDive.setDiveNo(mDive.getDiveNo());
                // Cannot calculate RMV with MyCalc because the consumption has not been added yet
                diverDive.setRmv(mDive.getMyRmv());
                diverDive.setIsPrimary("M");
                addDiverDive(diverDive);
            }

            // My Buddy
            if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
                DiverDive diverDive = new DiverDive();
                diverDive.setDiverNo(mDive.getMyBuddyDiverNo());
                diverDive.setDiveNo(mDive.getDiveNo());
                // Cannot calculate RMV with MyCalc because the consumption has not been added yet
                diverDive.setRmv(mDive.getMyBuddyRmv());
                diverDive.setIsPrimary("Y");
                addDiverDive(diverDive);
            }

            // DIVER_DIVE_GROUP
            // Me
            if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
                DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
                diverDiveGroup.setDiverNo(MyConstants.ONE_L);
                diverDiveGroup.setDiveNo(mDive.getDiveNo());
                diverDiveGroup.setGroupNo(mDive.getMyGroupNo());
                diverDiveGroup.setSac(mDive.getMySac());
                addDiverDiveGroup(diverDiveGroup);
            }

            // My Buddy
            if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
                DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
                diverDiveGroup.setDiverNo(mDive.getMyBuddyDiverNo());
                diverDiveGroup.setDiveNo(mDive.getDiveNo());
                diverDiveGroup.setGroupNo(mDive.getMyBuddyGroupNo());
                diverDiveGroup.setSac(mDive.getMyBuddySac());
                addDiverDiveGroup(diverDiveGroup);
            }

            // DIVER_DIVE_GROUP_CYL
            // Me
            if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
                ArrayList<GrouppCylinder> grouppCylinderList = mAirDa.getAllGroupCylinderByGroup(mDive.getMyGroupNo());
                createDiverDiveGroupCylinder(grouppCylinderList);
            }

            // My Buddy
            if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
                ArrayList<GrouppCylinder> grouppCylinderList = mAirDa.getAllGroupCylinderByGroup(mDive.getMyBuddyGroupNo());
                createDiverDiveGroupCylinder(grouppCylinderList);
            }
        }

        readDive();

        // Pager
        // The dive has not been retrieved yet!
        mDivePickList.add(mDive);   // 1- Planning
        mDivePickList.add(mDive);   // 2- Summary
        mDivePickList.add(mDive);   // 3- Environment
        mDivePickList.add(mDive);   // 4- Gas
        mDivePickList.add(mDive);   // 5- Gear
        mDivePickList.add(mDive);   // 6- Problem
        // TODO: Implement DiveComputer
//        mDivePickList.add(mDive);   // 7- Dive Computer
        // TODO: Implement Graph
//        mDivePickList.add(mDive);   // 8- Graph

        //Set the RecyclerView with a mDive with no data yet
        mDivePagerAdapter = new DivePagerAdapter(this,mDivePickList);

        // Set the RecyclerView to the ViewPager2
        mViewPager2.setAdapter(mDivePagerAdapter);

        mDive.setHasDataChanged(false);

        Log.d(LOG_TAG, "onCreate done");
    }

    // FIXME: Only used by commented code below (Stuck within 2 BeginTransaction())
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MyConstants.REQ_CODE_RMV:
//                // DIVE_PLAN_PICK always returns RESULT_OK
//                if (resultCode == RESULT_OK) {
//                    // Just need to Save the Dive info because the OnResume will re-retrieve it!
//                    updateDive(mDive);
//                    // The new Dive Type and RMV are in the state POJO
//                    mState = getIntent().getParcelableExtra(MyConstants.STATE);
//
//                    // Set the Dive Type
//                    if (mState.getDiveType().equals(MyConstants.LAST ) || mState.getDiveType().equals(MyConstants.AVERAGE ) || mState.getDiveType().equals(MyConstants.LAST_10 ) ) {
//                        mDive.setDiveType(MyConstants.TYPICAL);
//                    } else {
//                        mDive.setDiveType(mState.getDiveType());
//                    }
//
//                    // Set my SAC and RMV
//                    mDive.setMySac((mState.getMySac().equals(MyConstants.ZERO_D)) ? mMyCalc.getSacDefault() : mState.getMySac());
//                    mDive.setMyRmv((mState.getMyRmv().equals(MyConstants.ZERO_D)) ? mMyCalc.getRmvDefault() : mState.getMyRmv());
//
//                    // Set my buddy's SAC and RMV
//                    mDive.setMyBuddySac((mState.getMyBuddySac().equals(MyConstants.ZERO_D)) ? mMyCalc.getSacDefault() : mState.getMyBuddySac());
//                    mDive.setMyBuddyRmv((mState.getMyBuddyRmv().equals(MyConstants.ZERO_D)) ? mMyCalc.getRmvDefault() : mState.getMyBuddyRmv());
//
//                    // FIXME: Needs improvement
//                    // Actual data might not have change
//                    // Should have a hasDataChanged in SacRmv and/or State POJO
//                    mDive.setHasDataChanged(true);
//                }
                break;
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onResume() {
        super.onResume();

        // No binding occurred yet
        // The Views have not been created yet by the DivePagerAdapter
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_dive_edit));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mDive.getHasDataChanged()) {
                mDialogs.confirm(DiveActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
                return true;
            } else {
                mAirDa.endTransaction();
                mAirDa.close();
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
        if (mDive.getHasDataChanged()) {
            mDialogs.confirm(DiveActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
    }

    // ***** My functions *****

    public void bindAndProcessComputer(DiveComputerBinding bindingComputer) {
        mBindingComputer = bindingComputer;
        mBindingComputer.setDive(mDive);
        //Set all the listeners for this view
        refreshViewsComputer();
    }

    public void bindAndProcessEnvironment(DiveEnvironmentBinding bindingEnvironment) {
        mBindingEnvironment = bindingEnvironment;
        mBindingEnvironment.setDive(mDive);
        //Set all the listeners for this view
        refreshViewsEnvironment();
    }

    public void bindAndProcessGas(DiveGasBinding bindingGas) {
        mBindingGas = bindingGas;
        mBindingGas.setDive(mDive);
        //Set all the listeners for this view
        refreshViewsGas();
    }

    public void bindAndProcessGear(DiveGearBinding bindingGear) {
        mBindingGear = bindingGear;
        mBindingGear.setDive(mDive);
        //Set all the listeners for this view
        refreshViewsGas();
    }

    public void bindAndProcessPlanning(DivePlanningBinding bindingPlanning) {

        mBindingPlanning = bindingPlanning;
        mBindingPlanning.setDive(mDive);

        // Set the AutoTextView for Location
        ArrayList<DynamicSpinner> locationList = mAirDa.getDynamicSpinnerByType("LO");
        String[] locations = new String[locationList.size()];
        for (int i = 0; i < locationList.size(); i++) {
            DynamicSpinner dynamicSpinner = locationList.get(i);
            locations[i] = dynamicSpinner.getSpinnerText();
        }
        ArrayAdapter<String> adapterLocation = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, locations);
        mBindingPlanning.autoViewLO.setThreshold(1);
        mBindingPlanning.autoViewLO.setAdapter(adapterLocation);

        // Set the AutoTextView for Dive Site
        ArrayList<DynamicSpinner> diveSiteList = mAirDa.getDynamicSpinnerByType("DS");
        String[] diveSites = new String[diveSiteList.size()];
        for (int i = 0; i < diveSiteList.size(); i++) {
            DynamicSpinner dynamicSpinner = diveSiteList.get(i);
            diveSites[i] = dynamicSpinner.getSpinnerText();
        }
        ArrayAdapter<String> adapterDiveSite = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, diveSites);
        mBindingPlanning.autoViewDS.setThreshold(1);
        mBindingPlanning.autoViewDS.setAdapter(adapterDiveSite);

        // Set the AutoTextView for Dive Boat
        ArrayList<DynamicSpinner> diveBoatList = mAirDa.getDynamicSpinnerByType("DB");
        String[] diveBoats = new String[diveBoatList.size()];
        for (int i = 0; i < diveBoatList.size(); i++) {
            DynamicSpinner dynamicSpinner = diveBoatList.get(i);
            diveBoats[i] = dynamicSpinner.getSpinnerText();
        }
        ArrayAdapter<String> adapterBoatSite = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, diveBoats);
        mBindingPlanning.autoViewDB.setThreshold(1);
        mBindingPlanning.autoViewDB.setAdapter(adapterBoatSite);

        // Set the listeners

        DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, day) -> mBindingPlanning.editTextDA.setText(MyFunctions.formatDateString(getApplicationContext(), year, month, day));

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hourOfDay, minute) -> {
            // Hour is always in 24 hr format even if calendar is in 12 hr format
            mBindingPlanning.editTextTI.setText(MyFunctions.convertTimeToString(getApplicationContext(), mDive.getDate(), hourOfDay, minute));
            mDive.setHour(hourOfDay);
            mDive.setMinute(minute);
        };

        mBindingPlanning.myButton.setOnClickListener(view -> {
            // Remove or Add Me
            // To create an old dive history for My Buddy without affecting my numbers
            myButton();
        });

        mBindingPlanning.Calculate.setOnClickListener(view -> {
            // Calculate the SAC and RMV
            calculate();
        });

        mBindingPlanning.myBuddyButton.setOnClickListener(view -> {
            // Remove or Add My Buddy
            // To make a Me a Solo dive
            myBuddyButton();
        });

        // To Set the Date
        mBindingPlanning.editTextDA.setOnClickListener(view -> {
            Date dt = mDive.getDate();
            new DatePickerDialog(DiveActivity.this, onDateSetListener, MyFunctions.getYear(dt), MyFunctions.getMonthOfYear(dt), MyFunctions.getDayOfMonth(dt)).show();
        });

        // To set the Time In
        mBindingPlanning.editTextTI.setOnClickListener(view -> new TimePickerDialog(DiveActivity.this
                , onTimeSetListener
                , mDive.getHour()
                , mDive.getMinute()
                , DateFormat.is24HourFormat(getApplicationContext())
        ).show());

        // To edit Me
        mBindingPlanning.myLbl.setOnClickListener(view -> {
            // Save both the Me and My Buddy SAC
            updateMeMyBuddySac();
            // Save both the Me and My Buddy RMV
            updateMeMyBuddyRmv();
            // The Diver activity will be using this mAirDa transaction
            Intent intent = new Intent(getApplicationContext(), DiverActivity.class);
            Diver diver = new Diver();
            diver.setContext(getApplicationContext());
            diver.setDiverNo(mDive.getMyDiverNo());
            intent.putExtra(MyConstants.DIVER, diver);
            diverEditLauncher.launch(intent);
        });

        // To pick a Buddy
        mBindingPlanning.myBuddyLbl.setOnClickListener(view -> {
            // Save both the Me and My Buddy SAC
            updateMeMyBuddySac();
            // Save both the Me and My Buddy RMV
            updateMeMyBuddyRmv();
            // The Diver activities will be using this mAirDa transaction
            Intent intent = new Intent(getApplicationContext(), DiverPickActivity.class);
            Diver diver = new Diver();
            diver.setContext(getApplicationContext());
            diver.setDiverNo(mDive.getMyBuddyDiverNo());
            diver.setDiveNo(MyConstants.MINUS_ONE_L);
            diver.setLogBookNo(mDive.getLogBookNo());
            intent.putExtra(MyConstants.DIVER, diver);
            diverPickBuddyLauncher.launch(intent);
        });

        // To pick my Group
        mBindingPlanning.myGroup.setOnClickListener(view -> {
            // Save both the Me and My Buddy SAC
            updateMeMyBuddySac();
            // Save both the Me and My Buddy RMV
            updateMeMyBuddyRmv();
            // The Groupp activities will be using this mAirDa transaction
            Intent intent = new Intent(getApplicationContext(), GrouppPickActivity.class);
            GrouppPick grouppPick = new GrouppPick();
            grouppPick.setGroupNo(mDive.getMyGroupNo());
            grouppPick.setDiverNo(MyConstants.ONE_L);
            grouppPick.setDiveNo(mDive.getDiveNo());
            grouppPick.setLogBookNo(mDive.getLogBookNo());
            intent.putExtra(MyConstants.PICK_A_GROUPP, grouppPick);
            groupPickMeLauncher.launch(intent);
        });

        // To pick my Buddy Group
        mBindingPlanning.myBuddyGroup.setOnClickListener(view -> {
            // Save both the Me and My Buddy SAC
            updateMeMyBuddySac();
            // Save both the Me and My Buddy RMV
            updateMeMyBuddyRmv();
            // The Groupp activities will be using this mAirDa transaction
            Intent intent = new Intent(getApplicationContext(), GrouppPickActivity.class);
            GrouppPick grouppPick = new GrouppPick();
            grouppPick.setGroupNo(mDive.getMyBuddyGroupNo());
            grouppPick.setDiverNo(mDive.getMyBuddyDiverNo());
            grouppPick.setDiveNo(mDive.getDiveNo());
            grouppPick.setLogBookNo(mDive.getLogBookNo());
            intent.putExtra(MyConstants.PICK_A_GROUPP, grouppPick);
            groupPickMyBuddyLauncher.launch(intent);
        });

//        // FIXME: Stuck within 2 BeginTransaction()
////        // To select my RMV
////        mBindingPlanning.myLblRmv.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                // Save both the Me and My Buddy SAC
////                updateMeMyBuddySac();
////                // Save both the Me and My Buddy RMV
////                updateMeMyBuddyRmv();
////                // The Groupp activities will be using this mAirDa transaction
////                Intent intent = new Intent(getApplicationContext(), SacRmvActivity.class);
////                startActivityForResult(intent,MyConstants.REQ_CODE_RMV);
////            }
////        });

        // To edit the Planning
        mBindingPlanning.planning.setOnClickListener(view -> {
            // Save both the Me and My Buddy SAC
            updateMeMyBuddySac();
            // Save both the Me and My Buddy RMV
            updateMeMyBuddyRmv();
            // The Planning activities will be using this mAirDa transaction
            Intent intent = new Intent(getApplicationContext(), DivePlanPickActivity.class);
            DivePlan divePlan = new DivePlan();
            divePlan.setDiveNo(mDive.getDiveNo());
            divePlan.setLogBookNo((mDive.getLogBookNo()));
            intent.putExtra(MyConstants.DIVE_PLAN, divePlan);
            divePlanPickLauncher.launch(intent);
        });

        // To edit my consumption
        mBindingPlanning.myConsumption.setOnClickListener(view -> {
            // Save both the Me and My Buddy SAC
            updateMeMyBuddySac();
            // Save both the Me and My Buddy RMV
            updateMeMyBuddyRmv();
            // The Consumption activities will be using this mAirDa transaction
            Intent intent = new Intent(getApplicationContext(), DiverDiveGroupCylPickActivity.class);
            DiverDiveGroupCyl diverDiveGroupCyl = new DiverDiveGroupCyl();
            diverDiveGroupCyl.setDiverNo(1);
            diverDiveGroupCyl.setDiveNo(mDive.getDiveNo());
            diverDiveGroupCyl.setLogBookNo(mDive.getLogBookNo());
            diverDiveGroupCyl.setGroupNo(mDive.getMyGroupNo());
            intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, diverDiveGroupCyl);
            diverDiveGroupCylinderLauncher.launch(intent);
        });

        // To edit my buddy's consumption
        mBindingPlanning.myBuddyConsumption.setOnClickListener(view -> {
            // Save both the Me and My Buddy SAC
            updateMeMyBuddySac();
            // Save both the Me and My Buddy RMV
            updateMeMyBuddyRmv();
            // The Consumption activities will be using this mAirDa transaction
            Intent intent = new Intent(getApplicationContext(), DiverDiveGroupCylActivity.class);
            DiverDiveGroupCyl diverDiveGroupCyl = new DiverDiveGroupCyl();
            diverDiveGroupCyl.setDiverNo(mDive.getMyBuddyDiverNo());
            diverDiveGroupCyl.setDiveNo(mDive.getDiveNo());
            diverDiveGroupCyl.setLogBookNo(mDive.getLogBookNo());
            diverDiveGroupCyl.setGroupNo(mDive.getMyBuddyGroupNo());
            intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, diverDiveGroupCyl);
            diverDiveGroupCylinderLauncher.launch(intent);
        });

        // To edit the Extra Divers
        mBindingPlanning.extraDivers.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DiverExtraPickActivity.class);
            Diver Diver = new Diver();
            Diver.setDiverNo(mDive.getMyDiverNo());
            Diver.setDiveNo(mDive.getDiveNo());
            Diver.setLogBookNo(mDive.getLogBookNo());
            intent.putExtra(MyConstants.PICK_A_DIVER_EXTRA, Diver);
            // No need for a launcher or to return results
            // The extra divers are saved in DiverExtraPickActivity
            startActivity(intent);
        });

        mBindingPlanning.spinnerStatus.setFocusable(true);
        mBindingPlanning.spinnerStatus.setFocusableInTouchMode(true);
        mBindingPlanning.spinnerStatus.clearFocus();
        mBindingPlanning.spinnerStatus.requestFocus();

        if (mDive.getDiveNo() == MyConstants.ZERO_L) {
            // Add mode (New Dive)
            // Set the default values
            mDive.setLogBookNo(mAirDa.getLastLogBookNo() + 1);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " #" + mDive.getLogBookNo());
            }

            DiveLast diveLast = new DiveLast();
            mAirDa.getDiveLast(diveLast);

            if (diveLast.getLastDate().equals(MyConstants.ZERO_L)) {
                // Adding the very first dive
                // Use Today's date
                mDive.setDate(MyFunctions.getTodaysDate());
            } else {
                Date lastDate = MyFunctions.convertDateFromLongToDate(diveLast.getLastDate());
                lastDate = MyFunctions.formatDateDate(MyFunctions.getYear(lastDate),MyFunctions.getMonthOfYear(lastDate),MyFunctions.getDayOfMonth(lastDate));
                Date todaysDate = MyFunctions.getTodaysDate();
                todaysDate = MyFunctions.formatDateDate(MyFunctions.getYear(todaysDate),MyFunctions.getMonthOfYear(todaysDate),MyFunctions.getDayOfMonth(todaysDate));
                if (lastDate.equals(todaysDate)) {
                    // The last dive was on the same date as today
                    // Use the last date form the DataBase
                    // Transform the Dive Date from Integer/Long to a Date String
                    // Add last bottom time + 1:00 of surface interval
                    Date calculatedTime = MyFunctions.convertDateFromLongToDate(MyFunctions.addMinuteToDateTime(diveLast.getLastDate(), diveLast.getBottomTime() + 60.0));
                    Date now = MyFunctions.getNow();
                    if (calculatedTime.compareTo(now) > 0) {
                        // calculatedTime is in the future, use it
                        mDive.setDate(calculatedTime);
                    } else {
                        // calculatedTime is in the past, use current DateTime
                        mDive.setDate(now);
                    }
                } else {
                    // A new day, a new date
                    // Use today's date
                    mDive.setDate(MyFunctions.getTodaysDate());
                }
            }

            mDive.setHour(MyFunctions.getHour(mDive.getDate()));
            mDive.setMinute(MyFunctions.getMinute(mDive.getDate()));
            mDive.setTimeIn(MyFunctions.getTimeFromDate(getApplicationContext(),mDive.getDate())); // HH:MM or HH:MM AM/PM

            mDive.setStatus(getString(R.string.code_default_status)); // Plan
            mDive.setSalinity(true);
            mDive.setAverageDepth(MyConstants.ZERO_D);
            mDive.setAltitude(Integer.parseInt(getString(R.string.code_default_altitude)));

            mDive.setLocation(" ");
            mDive.setDiveSite(" ");
            mDive.setDiveBoat(" ");
            mDive.setPurpose(" ");

            mDive.setSuit("");
            mDive.setEnvironment("");
            mDive.setPlatform("");
            mDive.setWeather("");
            mDive.setCondition("");

            mDive.setMyDiverNo(MyConstants.ONE_L);

            mMyGroupNoOrig = MyConstants.ZERO_L;
            mMyBuddyDiverNoOrig = MyConstants.ZERO_L;
            mMyBuddyGroupNoOrig = MyConstants.ZERO_L;

            mBindingPlanning.myGroup.setEnabled(true);
            mBindingPlanning.myGroup.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));

            // DIVE
            // The dive will be committed upon the save
            addDive(mDive);

            // DIVER_DIVE
            // Me
            if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
                DiverDive diverDive = new DiverDive();
                diverDive.setDiverNo(MyConstants.ONE_L);
                diverDive.setDiveNo(mDive.getDiveNo());
                // Cannot calculate RMV with MyCalc because the consumption has not been added yet
                diverDive.setRmv(mDive.getMyRmv());
                diverDive.setIsPrimary("M");
                addDiverDive(diverDive);
            }

            // My Buddy
            if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
                DiverDive diverDive = new DiverDive();
                diverDive.setDiverNo(mDive.getMyBuddyDiverNo());
                diverDive.setDiveNo(mDive.getDiveNo());
                // Cannot calculate RMV with MyCalc because the consumption has not been added yet
                diverDive.setRmv(mDive.getMyBuddyRmv());
                diverDive.setIsPrimary("Y");
                addDiverDive(diverDive);
            }

            // DIVER_DIVE_GROUP
            // Me
            if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
                DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
                diverDiveGroup.setDiverNo(MyConstants.ONE_L);
                diverDiveGroup.setDiveNo(mDive.getDiveNo());
                diverDiveGroup.setGroupNo(mDive.getMyGroupNo());
                diverDiveGroup.setSac(mDive.getMySac());
                addDiverDiveGroup(diverDiveGroup);
            }

            // My Buddy
            if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
                DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
                diverDiveGroup.setDiverNo(mDive.getMyBuddyDiverNo());
                diverDiveGroup.setDiveNo(mDive.getDiveNo());
                diverDiveGroup.setGroupNo(mDive.getMyBuddyGroupNo());
                diverDiveGroup.setSac(mDive.getMyBuddySac());
                addDiverDiveGroup(diverDiveGroup);
            }

            // DIVER_DIVE_GROUP_CYL
            // Me
            if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
                ArrayList<GrouppCylinder> grouppCylinderList = mAirDa.getAllGroupCylinderByGroup(mDive.getMyGroupNo());
                createDiverDiveGroupCylinder(grouppCylinderList);
            }

            // My Buddy
            if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
                ArrayList<GrouppCylinder> grouppCylinderList = mAirDa.getAllGroupCylinderByGroup(mDive.getMyBuddyGroupNo());
                createDiverDiveGroupCylinder(grouppCylinderList);
            }
        }

        refreshViewsPlanning();
    }

    public void bindAndProcessProblem(DiveProblemBinding bindingProblem) {
        mBindingProblem = bindingProblem;
        mBindingProblem.setDive(mDive);
        //Set all the listeners for this view
        refreshViewsProblem();
    }

    public void bindAndProcessSummary(DiveSummaryBinding bindingSummary) {
        mBindingSummary = bindingSummary;
        mBindingSummary.setDive(mDive);
        //Set all the listeners for this view
        refreshViewsSummary();
    }

    private void myBuddyButton() {
        // Delete the DIVER_DIVE
        removeDiverDive(mDive.getMyBuddyDiverNo(),mDive.getDiveNo());
        // Reset "No Buddy"
        mDive.setMyBuddyDiverNo(MyConstants.ZERO_L);
        mDive.setMyBuddyGroupNo(MyConstants.ZERO_L);
        mDive.setMyBuddySac(MyConstants.ZERO_D);
        mDive.setMyBuddyRmv(MyConstants.ZERO_D);
        mDive.setMyBuddyFullName(this.getResources().getString(R.string.sql_no_buddy));
        mBindingPlanning.myBuddyLbl.setText(this.getResources().getString(R.string.sql_no_buddy));
        mBindingPlanning.myBuddyGroup.setText(this.getResources().getString(R.string.act_groupp_pick));
        mBindingPlanning.myBuddyGroup.setTextColor(Color.BLACK);
        mBindingPlanning.myBuddyGroup.setEnabled(false);
        mBindingPlanning.myBuddySac.setText("0.0");
        mBindingPlanning.myBuddyRmv.setText("0.0");
        mBindingPlanning.myBuddySac.setEnabled(false);
        mBindingPlanning.myBuddyRmv.setEnabled(false);
        requestFocus(mBindingPlanning.myBuddyLbl);
        // Cannot removed Me
        mBindingPlanning.myButton.setEnabled(false);
        mBindingPlanning.myButton.setVisibility(View.GONE);
        // No more Buddy to remove
        mBindingPlanning.myBuddyButton.setEnabled(false);
        mBindingPlanning.myBuddyButton.setVisibility(View.GONE);
        // No more Consumption to pick
        mBindingPlanning.myBuddyConsumption.setEnabled(false);
        mBindingPlanning.myBuddyConsumption.setTextColor(Color.BLACK);
    }

    private boolean invertMyButton() {
        return !mMyButton;
    }

    private void myButton() {
        if (mMyButton) {
            // NOTE: Leave as is
            mMyButton = invertMyButton();
            mBindingPlanning.myButton.setImageResource(R.drawable.ic_plus);
            mBindingPlanning.myButton.setColorFilter(Color.GREEN);
            mBindingPlanning.myGroup.setTextColor(Color.BLACK);
            mBindingPlanning.myGroup.setEnabled(false);
            mBindingPlanning.myGroup.setText(this.getResources().getString(R.string.act_groupp_pick));
            mBindingPlanning.myConsumption.setTextColor(Color.BLACK);
            mBindingPlanning.myConsumption.setEnabled(false);
            // Delete Me from the DIVER_DIVE
            removeDiverDive(MyConstants.ONE_L,mDive.getDiveNo());
            mDive.setMyDiverNo(MyConstants.ZERO_L);
            mDive.setMyGroupNo(MyConstants.ZERO_L);
            mDive.setMySac(MyConstants.ZERO_D);
            mDive.setMyRmv(MyConstants.ZERO_D);
            mBindingPlanning.mySac.setText("0.0");
            mBindingPlanning.myRmv.setText("0.0");
            mBindingPlanning.mySac.setEnabled(false);
            mBindingPlanning.myRmv.setEnabled(false);
            // Delete Me from the DIVER-DIVE_GROUP
            removeDiverDiveGroup(MyConstants.ONE_L,mDive.getDiveNo());
            // No more Buddy to remove
            mBindingPlanning.myBuddyButton.setEnabled(false);
            mBindingPlanning.myBuddyButton.setVisibility(View.GONE);
        } else {
            // NOTE: Leave as is
            mMyButton = invertMyButton();
            mBindingPlanning.myButton.setImageResource(R.drawable.ic_minus);
            mBindingPlanning.myButton.setColorFilter(Color.RED);
            mBindingPlanning.myGroup.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
            mBindingPlanning.myGroup.setEnabled(true);
            mBindingPlanning.mySac.setEnabled(true);
            mBindingPlanning.myRmv.setEnabled(true);
            // Add Me back to the DIVER_DIVE
            DiverDive diverDive = new DiverDive();
            diverDive.setDiverNo(MyConstants.ONE_L);
            diverDive.setDiveNo(mDive.getDiveNo());
            diverDive.setRmv(mDive.getMyRmv());
            diverDive.setIsPrimary("M");
            addDiverDive(diverDive);
            mDive.setMyDiverNo(MyConstants.ONE_L);
            // Add Me back to the DIVER_DIVE_GROUP
            DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
            diverDiveGroup.setDiverNo(MyConstants.ONE_L);
            diverDiveGroup.setDiveNo(mDive.getDiveNo());
            diverDiveGroup.setGroupNo(mDive.getMyGroupNo());
            diverDiveGroup.setSac(mDive.getMySac());
            addDiverDiveGroup(diverDiveGroup);
            // Buddy is ready to be removed
            mBindingPlanning.myBuddyButton.setEnabled(true);
            mBindingPlanning.myBuddyButton.setVisibility(View.VISIBLE);
        }
    }

    public Runnable noProc(){
        return () -> {
            //Do nothing, stay on this activity
        };
    }

    private void readDive() {
        // Edit mode
        // Retrieve the dive from the Pick list
        // Or retrieve the the new dive that we just added
        // Or retrieve the dive that we just modify in order to get the latest data and be able to refresh the view
        mAirDa.getDive(mDive.getDiveNo(),mDive);

        mDive.setTimeIn(MyFunctions.getTimeFromDate(getApplicationContext(),mDive.getDate())); // HH:MM or HH:MM AM/PM
        mDive.setHour(MyFunctions.getHour(mDive.getDate()));
        mDive.setMinute(MyFunctions.getMinute(mDive.getDate()));

        if (mDive.getMyDiverNo() == MyConstants.ZERO_L) {
            mMyButton = invertMyButton();
        }
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

    public Runnable yesProc(){
        return () -> {
            mAirDa.endTransaction();
            mAirDa.close();
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        };
    }

    // Launcher functions

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> divePlanPickLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Just need to Save the Dive info because the OnResume will re-retrieve it!
                    updateDive(mDive);
                    DivePlan divePlan;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        divePlan = data.getParcelableExtra(MyConstants.DIVE_PLAN, DivePlan.class);
                    } else {
                        divePlan = data.getParcelableExtra(MyConstants.DIVE_PLAN);
                    }
                    assert divePlan != null;
                    mDive.setHasDataChanged(divePlan.getHasDataChanged());

                    // Read the dive to get the dive plan count
                    readDive();
                    refreshViewsPlanning();
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> diverDiveGroupCylinderLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Same as Consumption
                    // Same code for Me and My Buddy. Just the dove_no changes
                    // Save the dive no matter the result coming from DIVER_DIVE_GROUP_CYLINDER
                    // Just need to Save the Dive info because the OnResume will re-retrieve it!
                    // The Consumption is saved in the DiverDiveGroupCylActivity
                    updateDive(mDive);
                    DiverDiveGroupCyl diverDiveGroupCyl;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        diverDiveGroupCyl = data.getParcelableExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER,DiverDiveGroupCyl.class);
                    } else {
                        diverDiveGroupCyl = data.getParcelableExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER);
                    }
                    assert diverDiveGroupCyl != null;
                    mDive.setHasDataChanged(diverDiveGroupCyl.getHasDataChanged());

                    refreshViewsPlanning();
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> diverEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the data from Edit a Diver
                    Diver diver;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        diver = data.getParcelableExtra(MyConstants.DIVER,Diver.class);
                    } else {
                        diver = data.getParcelableExtra(MyConstants.DIVER);
                    }
                    assert diver != null;
                    mDive.setHasDataChanged(diver.getHasDataChanged());

                    refreshViewsPlanning();
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> diverPickBuddyLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the data from Pick a Diver
                    Diver diver;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        diver = data.getParcelableExtra(MyConstants.PICK_A_DIVER,Diver.class);
                    } else {
                        diver = data.getParcelableExtra(MyConstants.PICK_A_DIVER);
                    }
                    assert diver != null;
                    mDive.setHasDataChanged(diver.getHasDataChanged());

                    // Set the data in the Spinner Status
                    String[] itemsStatus = getResources().getStringArray(R.array.status_arrays);
                    ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemsStatus);
                    adapterStatus.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    // Set the data in the Spinner DiveType
                    ArrayList<DiveType> diveTypeList = mAirDa.getAllDiveTypePickable();
                    ArrayAdapter<DiveType> adapterDiveType = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, diveTypeList);
                    adapterDiveType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    // Set the data in the Condition Condition
                    ArrayList<DynamicSpinner> conditionList = mAirDa.getDynamicSpinnerByType("SU");
                    ArrayAdapter<DynamicSpinner> adapterCondition = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, conditionList);
                    adapterCondition.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    // Set the data in the Spinner Environment
                    ArrayList<DynamicSpinner> environmentList = mAirDa.getDynamicSpinnerByType("EN");
                    ArrayAdapter<DynamicSpinner> adapterEnvironment = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, environmentList);
                    adapterEnvironment.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    // Set the data in the Spinner Platform
                    ArrayList<DynamicSpinner> platformList = mAirDa.getDynamicSpinnerByType("PL");
                    ArrayAdapter<DynamicSpinner> adapterPlatform = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, platformList);
                    adapterPlatform.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    // Set the data in the Spinner Suit
                    ArrayList<DynamicSpinner> suitList = mAirDa.getDynamicSpinnerByType("SU");
                    ArrayAdapter<DynamicSpinner> adapterSuit = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, suitList);
                    adapterSuit.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    // Set the data in the Spinner Weather
                    ArrayList<DynamicSpinner> weatherList = mAirDa.getDynamicSpinnerByType("WE");
                    ArrayAdapter<DynamicSpinner> adapterWeather = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, weatherList);
                    adapterWeather.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    if (!diver.getDiverNo().equals(MyConstants.ZERO_L) && !diver.getDiverNo().equals(mDive.getMyBuddyDiverNo())) {
                        // Update DIVE
                        mDive.setMyBuddyGroupNo(MyConstants.ZERO_L);
                        updateDive(mDive);
                        mDive.setHasDataChanged(true);
                        // Update DIVER_DIVE
                        // My Buddy has not been necessarily removed
                        // Might just come in from Diver Pick with a different Diver No
                        mAirDa.deleteDiverDive(mDive.getMyBuddyDiverNo(), mDive.getDiveNo());
                        // Add the Buddy
                        DiverDive diverDive = new DiverDive();
                        diverDive.setDiverNo(diver.getDiverNo());
                        diverDive.setDiveNo(mDive.getDiveNo());
                        // Cannot calculate RMV with MyCalc because the consumption has not been added yet
                        diverDive.setRmv(mDive.getMyBuddyRmv());
                        diverDive.setIsPrimary("Y");
                        addDiverDive(diverDive);
                    }
                    refreshViewsPlanning();
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> groupPickMeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the data from Pick a Group
                    GrouppPick grouppPick;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        grouppPick = data.getParcelableExtra(MyConstants.PICK_A_GROUPP,GrouppPick.class);
                    } else {
                        grouppPick = data.getParcelableExtra(MyConstants.PICK_A_GROUPP);
                    }
                    // It is possible that that the definition of the same Equipment Group has changed
                    assert grouppPick != null;
                    mDive.setHasDataChanged(grouppPick.getHasDataChanged());
                    //Set My Group on the activity
                    if (!grouppPick.getGroupNo().equals(MyConstants.ZERO_L)) {
                        // Since My Buddy and Group could be different, the best approach is to always delete all of the DIVER_DIVE_GROUP (via DELETE CASCADE)
                        // for the current dive_no. If I change my Group, it will not be the same cylinders, therefore the consumption data e.g. beginning pressure,
                        // O2 etc need to be reset anyway
                        // Me
                        if (!grouppPick.getGroupNo().equals(mMyGroupNoOrig)) {
                            // My Group has changed
                            // Update DIVE
                            mDive.setMyGroupNo(grouppPick.getGroupNo());
                            updateDive(mDive);
                            mDive.setHasDataChanged(true);
                            // Update DIVER_DIVE_GROUP
                            removeDiverDiveGroup(mDive.getMyDiverNo(), mDive.getDiveNo());
                            DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
                            diverDiveGroup.setDiverNo(MyConstants.ONE_L);
                            diverDiveGroup.setDiveNo(mDive.getDiveNo());
                            diverDiveGroup.setGroupNo(grouppPick.getGroupNo());
                            diverDiveGroup.setSac(mMyCalc.getSacDefault());
                            addDiverDiveGroup(diverDiveGroup);
                            // Update DIVER_DIVE_GROUP_CYLINDER
                            removeDiverDiveGroupCyl(mDive.getMyDiverNo(), mDive.getDiveNo());
                            ArrayList<GrouppCylinder> grouppCylinderList = mAirDa.getAllGroupCylinderByGroup(mDive.getMyGroupNo());
                            createDiverDiveGroupCylinder(grouppCylinderList);
                        }
                    }
                    refreshViewsPlanning();
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> groupPickMyBuddyLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the data from Pick a Group
                    GrouppPick grouppPick;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        grouppPick = data.getParcelableExtra(MyConstants.PICK_A_GROUPP,GrouppPick.class);
                    } else {
                        grouppPick = data.getParcelableExtra(MyConstants.PICK_A_GROUPP);
                    }
                    // It is possible that that the definition of the ame Equipment Group has changed
                    assert grouppPick != null;
                    mDive.setHasDataChanged(grouppPick.getHasDataChanged());
                    //Set My Buddy Group on the activity
                    if (!grouppPick.getGroupNo().equals(MyConstants.ZERO_L)) {
                        // Since My Buddy and Group could be different, the best approach is to always delete all of the DIVER_DIVE_GROUP (via DELETE CASCADE)
                        // for the current dive_no. If I change my Group, it will not be the same cylinders, therefore the consumption data e.g. beginning pressure,
                        // O2 etc need to be reset anyway
                        // My Buddy
                        // If I change Buddy or if he changes his Group, it will not be the same cylinders, therefore the consumption data e.g. beginning pressure, O2 etc need to be reset anyway
                        if ((mDive.getMyBuddyDiverNo() != mMyBuddyDiverNoOrig) || (!grouppPick.getGroupNo().equals(mMyBuddyGroupNoOrig))) {
                            // My Buddy or his Group changed
                            // Update DIVE
                            mDive.setMyBuddyGroupNo(grouppPick.getGroupNo());
                            updateDive(mDive);
                            mDive.setHasDataChanged(true);
                            // Update DIVER_DIVE_GROUP
                            removeDiverDiveGroup(mDive.getMyBuddyDiverNo(), mDive.getDiveNo());
                            DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
                            diverDiveGroup.setDiverNo(mDive.getMyBuddyDiverNo());
                            diverDiveGroup.setDiveNo(mDive.getDiveNo());
                            diverDiveGroup.setGroupNo(grouppPick.getGroupNo());
                            diverDiveGroup.setSac(mMyCalc.getSacDefault());
                            addDiverDiveGroup(diverDiveGroup);
                            // Update DIVER_DIVE_GROUP_CYLINDER
                            removeDiverDiveGroupCyl(mDive.getMyBuddyDiverNo(), mDive.getDiveNo());
                            ArrayList<GrouppCylinder> grouppCylinderList = mAirDa.getAllGroupCylinderByGroup(mDive.getMyBuddyGroupNo());
                            createDiverDiveGroupCylinder(grouppCylinderList);
                        }
                    }
                    refreshViewsPlanning();
                }
            });

    // ***** Validating and Saving functions *****

//    DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, day) -> mBindingPlanning.editTextDA.setText(MyFunctions.formatDateString(getApplicationContext(), year, month, day));
//
//    TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hourOfDay, minute) -> {
//        // Hour is always in 24 hr format even if calendar is in 12 hr format
//        mBindingPlanning.editTextTI.setText(MyFunctions.convertTimeToString(getApplicationContext(), mDive.getDate(), hourOfDay, minute));
//        mDive.setHour(hourOfDay);
//        mDive.setMinute(minute);
//    };

    private void submitForm() {

        // Validate 1- Planning
        if (validateDiverDepth()) {
            return;
        }

        if (validatePlanning()) {
            return;
        }

        if (!validateMeRmv()) {
            return;
        }

        if (!validateMyBuddyRmv()) {
            return;
        }

        if (validateEquipmentGroup()) {
            return;
        }

        // Validate 2- Summary
        if (!validateLogBookNo()) {
            return;
        }

        if (validateBottomTime()) {
            return;
        }

        if (validateAverageDepth()) {
            return;
        }

        if (validateMaximumDepth()) {
            return;
        }

        // Validate 3- Environment
        if (!validateAltitude()) {
            return;
        }

        // Validate 4- Gas
        if (validateConsumption()) {
            return;
        }

        // Validate 5- Gear
        //             None so far

        // Validate 6- Problem
        //             None so far

        // Validate 7- Computer
        //             None so far

        // Validate 8- Graph
        //             None so far


        // 2020/03/26 Move up the RMV validation when changing the bottomTime
        // Must calculate and validate RMV here, before starting the DB updates
        // Using real calculation for both SAC & RMV, using MyCalc because:
        // 1) It is a real Dive and therefore the Beginning and Ending Pressures are mandatory
        // 2) It is a Plan dive and all Bottom Time, Beginning and Ending Pressures have been entered
        // Me
        DiverDive diverDive = new DiverDive();
        if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
            diverDive.setDiverNo(1);
            diverDive.setDiveNo(mDive.getDiveNo());
            Double beginningPressure = mAirDa.getSumBeginningPressure(mDive.getMyDiverNo(),mDive.getDiveNo());
            Double endingPressure = mAirDa.getSumEndingPressure(mDive.getMyDiverNo(),mDive.getDiveNo());
            // NOTE: Leave as is
            if (        mDive.getStatus().equals(getString(R.string.code_dive_status_real))
                    || (mDive.getStatus().equals(MyConstants.PLAN) && mDive.getAverageDepth() != 0.0 && mDive.getBottomTime() != 0 && beginningPressure != 0.0 && endingPressure != 0.0)
            ) {

                if (!mDive.getMySac().equals(MyConstants.ZERO_D)) {
                    mDive.setMySac(MyFunctions.roundUp(mMyCalc.getSac(beginningPressure, endingPressure, mDive.getBottomTime(), mDive.getAverageDepth(), mDive.getSalinity()),2));
                }

                if (!mDive.getMyRmv().equals(MyConstants.ZERO_D)) {
                    mDive.setMyRmv(MyFunctions.roundUp(mMyCalc.getRmv(mDive.getMySac(),mDive.getMyRatedVolume(),mDive.getMyRatedPressure()),2));
                }

                diverDive.setRmv(mDive.getMyRmv());
            } else {
                diverDive.setRmv(mDive.getMyRmv());
            }

            if (mDive.getMyRmv() < MyConstants.ZERO_D || mDive.getMyRmv() > mMyCalc.getMaxRmv()) {
                String message = String.format(getResources().getString(R.string.msg_rmv), mMyCalc.getMaxRmv().toString(),mMyCalc.getRmvUnit());
                mBindingPlanning.mySac.setText(String.valueOf(mDive.getMySac()));
                mBindingPlanning.myRmv.setText(String.valueOf(mDive.getMyRmv()));
                mBindingPlanning.myRmv.setError(message);
                requestFocus(mBindingPlanning.myRmv);
                return;
            }
        }

        // 2020/03/26 Move up the RMV validation when changing the bottomTime
        // Using real calculation for both SAC & RMV, using MyCalc because:
        // 1) It is a real Dive and therefore the Beginning and Ending Pressures are mandatory
        // 2) It is a Plan dive and all Bottom Time, Beginning and Ending Pressures have been entered
        // My Buddy
        if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            // Add the Buddy
            diverDive.setDiverNo(mDive.getMyBuddyDiverNo());
            diverDive.setDiveNo(mDive.getDiveNo());
            Double beginningPressure = mAirDa.getSumBeginningPressure(mDive.getMyBuddyDiverNo(),mDive.getDiveNo());
            Double endingPressure = mAirDa.getSumEndingPressure(mDive.getMyBuddyDiverNo(),mDive.getDiveNo());
            // NOTE: Leave as is
            if (        mDive.getStatus().equals(getString(R.string.code_dive_status_real))
                    || (mDive.getStatus().equals(MyConstants.PLAN) && mDive.getAverageDepth() != 0.0 && mDive.getBottomTime() != 0 && beginningPressure != 0.0 && endingPressure != 0.0)
            ) {

                if (!mDive.getMyBuddySac().equals(MyConstants.ZERO_D)) {
                    mDive.setMyBuddySac(MyFunctions.roundUp(mMyCalc.getSac(beginningPressure, endingPressure, mDive.getBottomTime(), mDive.getAverageDepth(), mDive.getSalinity()),2));
                }

                if (!mDive.getMyBuddyRmv().equals(MyConstants.ZERO_D)) {
                    mDive.setMyBuddyRmv(MyFunctions.roundUp(mMyCalc.getRmv(mDive.getMyBuddySac(), mDive.getMyBuddyRatedVolume(), mDive.getMyBuddyRatedPressure()),2));
                }

                diverDive.setRmv(mDive.getMyBuddyRmv());
            } else {
                diverDive.setRmv(mDive.getMyBuddyRmv());
            }
            diverDive.setIsPrimary("Y");

            if (mDive.getMyBuddyRmv() < MyConstants.ZERO_D || mDive.getMyBuddyRmv() > mMyCalc.getMaxRmv()) {
                String message = String.format(getResources().getString(R.string.msg_rmv), mMyCalc.getMaxRmv().toString(),mMyCalc.getRmvUnit());
                mBindingPlanning.myBuddySac.setText(String.valueOf(mDive.getMySac()));
                mBindingPlanning.myBuddyRmv.setText(String.valueOf(mDive.getMyRmv()));
                mBindingPlanning.myBuddyRmv.setError(message);
                requestFocus(mBindingPlanning.myBuddyRmv);
                return;
            }
        }

        // Save DIVE, DIVER_DIVE, DIVER_DIVE_GROUP, DIVER_DIVE_GROUP_CYLINDER, DIVE_SEGMENT and STATE data
        // Edit mode
        try {
            // Update DIVE
            updateDive(mDive);
            // Update the Dynamic Spinner
            mAirDa.updateDynamicSpinnerByType("LO",mDive.getLocationOld(),mDive.getLocation());
            mAirDa.updateDynamicSpinnerByType("DS",mDive.getDiveSiteOld(),mDive.getDiveSite());
            mAirDa.updateDynamicSpinnerByType("DB",mDive.getDiveBoatOld(),mDive.getDiveBoat());

            // Update DIVER_DIVE
            //Need to redo all of the calculation, because of the previous validation above
            // Use real calculation for both SAC & RMV, using MyCalc because:
            // 1) It is a real Dive and therefore the Beginning and Ending Pressures are mandatory
            // 2) It is a Plan dive and all Bottom Time, Beginning and Ending Pressures have been entered
            // Me
            if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
                diverDive.setDiverNo(1);
                diverDive.setDiveNo(mDive.getDiveNo());
                Double beginningPressure = mAirDa.getSumBeginningPressure(mDive.getMyDiverNo(),mDive.getDiveNo());
                Double endingPressure = mAirDa.getSumEndingPressure(mDive.getMyDiverNo(),mDive.getDiveNo());
                if (        mDive.getStatus().equals(getString(R.string.code_dive_status_real))
                        || (mDive.getStatus().equals(MyConstants.PLAN) && mDive.getAverageDepth() != 0.0 && mDive.getBottomTime() != 0 && beginningPressure != 0.0 && endingPressure != 0.0)
                   ) {

                    if (!mDive.getMySac().equals(MyConstants.ZERO_D)) {
                        mDive.setMySac(MyFunctions.roundUp(mMyCalc.getSac(beginningPressure, endingPressure, mDive.getBottomTime(), mDive.getAverageDepth(), mDive.getSalinity()),2));
                    }

                    if (!mDive.getMyRmv().equals(MyConstants.ZERO_D)) {
                        mDive.setMyRmv(MyFunctions.roundUp(mMyCalc.getRmv(mDive.getMySac(),mDive.getMyRatedVolume(),mDive.getMyRatedPressure()),2));
                    }

                    diverDive.setRmv(mDive.getMyRmv());
                } else {
                    diverDive.setRmv(mDive.getMyRmv());
                }

                if (mDive.getMyRmv() > mMyCalc.getMaxRmv()) {
                    String message = String.format(getResources().getString(R.string.msg_rmv), mMyCalc.getMaxRmv().toString(),mMyCalc.getRmvUnit());
                    mBindingPlanning.myRmv.setError(message);
                    requestFocus(mBindingPlanning.myRmv);
                    return;
                }

                diverDive.setIsPrimary("M");
                updateDiverDive(diverDive);
            }

            // Update DIVER_DIVE
            // Use real calculation for both SAC & RMV, using MyCalc because:
            // 1) It is a real Dive and therefore the Beginning and Ending Pressures are mandatory
            // 2) It is a Plan dive and all Bottom Time, Beginning and Ending Pressures have been entered
            // My Buddy
            if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
                // Add the Buddy
                diverDive.setDiverNo(mDive.getMyBuddyDiverNo());
                diverDive.setDiveNo(mDive.getDiveNo());
                Double beginningPressure = mAirDa.getSumBeginningPressure(mDive.getMyBuddyDiverNo(),mDive.getDiveNo());
                Double endingPressure = mAirDa.getSumEndingPressure(mDive.getMyBuddyDiverNo(),mDive.getDiveNo());
                if (        mDive.getStatus().equals(getString(R.string.code_dive_status_real))
                        || (mDive.getStatus().equals(MyConstants.PLAN) && mDive.getAverageDepth() != 0.0 && mDive.getBottomTime() != 0 && beginningPressure != 0.0 && endingPressure != 0.0)
                   ) {

                    if (!mDive.getMyBuddySac().equals(MyConstants.ZERO_D)) {
                        mDive.setMyBuddySac(MyFunctions.roundUp(mMyCalc.getSac(beginningPressure, endingPressure, mDive.getBottomTime(), mDive.getAverageDepth(), mDive.getSalinity()),2));
                    }

                    if (!mDive.getMyBuddyRmv().equals(MyConstants.ZERO_D)) {
                        mDive.setMyBuddyRmv(MyFunctions.roundUp(mMyCalc.getRmv(mDive.getMyBuddySac(), mDive.getMyBuddyRatedVolume(), mDive.getMyBuddyRatedPressure()),2));
                    }

                    diverDive.setRmv(mDive.getMyBuddyRmv());
                } else {
                    diverDive.setRmv(mDive.getMyBuddyRmv());
                }

                diverDive.setIsPrimary("Y");
                updateDiverDive(diverDive);
            }

            // Update DIVER_DIVE_GROUP
            // The DIVER_DIVE_GROUP has been added when selecting the mandatory Equipment Group
            // Just need to update the SAC
            // Me
            DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
            if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
                diverDiveGroup.setDiverNo(MyConstants.ONE_L);
                diverDiveGroup.setDiveNo(mDive.getDiveNo());
                diverDiveGroup.setGroupNo(mDive.getMyGroupNo());
                diverDiveGroup.setSac(mDive.getMySac());
                updateDiverDiveGroup(diverDiveGroup);
            }
            // My Buddy
            if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
                diverDiveGroup.setDiverNo(mDive.getMyBuddyDiverNo());
                diverDiveGroup.setDiveNo(mDive.getDiveNo());
                diverDiveGroup.setGroupNo(mDive.getMyBuddyGroupNo());
                diverDiveGroup.setSac(mDive.getMyBuddySac());
                updateDiverDiveGroup(diverDiveGroup);
            }

            // Save STATE data
            saveState();
            // End transaction with success
            // Including the Diver updates
            mAirDa.setTransactionSuccessful();
        } finally {
            // No transaction left behind
            mAirDa.endTransaction();
        }
        mAirDa.close();

        // Return the parcel object with the newly saved data
        Intent intent = new Intent();
        intent.putExtra(MyConstants.DIVE,mDive);
        intent.putExtra(MyConstants.STATE, mState);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateLogBookNo() {
        // Not required
        // A Diver does not have to enter a number referring back to his log book, paper or electronic
        // But must be between 0 and 9,9999

        if (mBindingPlanning.editTextLBN.getText().toString().trim().isEmpty() || !isValidLogBookNo(mDive.getLogBookNo())) {
            mBindingPlanning.editTextLBN.setError(getString(R.string.msg_log_book_no));
            requestFocus(mBindingPlanning.editTextLBN);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateBottomTime() {
        // 2020/03/25 Optimized to support bottomTime mm:ss
        if ((mDive.getStatus().equals(getString(R.string.code_dive_status_real))) && ((mBindingSummary.editTextBT.getText().toString().trim().isEmpty()) || (!mBindingSummary.editTextBT.getText().toString().trim().isEmpty() && isInvalidBottomTimeReal(mDive.getBottomTimeStringX())))) {
            mBindingSummary.editTextBT.setError(getString(R.string.msg_bottom_time_real));
            requestFocus(mBindingSummary.editTextBT);
            mViewPager2.setCurrentItem(mSummaryPosition);
            return true;
        } else if (mDive.getStatus().equals(MyConstants.PLAN) && (!mBindingSummary.editTextBT.getText().toString().trim().isEmpty() && isInvalidBottomTimePlan(mDive.getBottomTimeStringX()))) {
            mBindingSummary.editTextBT.setError(getString(R.string.msg_bottom_time_plan));
            requestFocus(mBindingSummary.editTextBT);
            mViewPager2.setCurrentItem(mSummaryPosition);
            return true;
        } else {
            // Convert mm:ss into a Double
            mDive.setBottomTime(MyFunctions.convertMmSs(mDive.getBottomTimeStringX()));
            return false;
        }
    }

    private boolean validateAverageDepth() {
        // Required for a Status of Real
        // Must be valid for a status of Plan
        if (mDive.getStatus().equals(getString(R.string.code_dive_status_real)) && ((mBindingPlanning.editTextAD.getText().toString().trim().isEmpty())  || (!mBindingPlanning.editTextAD.getText().toString().trim().isEmpty() && isInvalidAverageDepthReal(mDive.getAverageDepth())))) {
            String message = String.format(getResources().getString(R.string.msg_depth_average_real), mMyCalc.getMaxAverageDepth().toString(),mMyCalc.getDepthUnit());
            mBindingPlanning.editTextAD.setError(message);
            requestFocus(mBindingPlanning.editTextAD);
            return true;
        } else if (mDive.getStatus().equals(MyConstants.PLAN) && (!mBindingPlanning.editTextAD.getText().toString().trim().isEmpty() && isInvalidAverageDepthPlan(mDive.getAverageDepth()))) {
            String message = String.format(getResources().getString(R.string.msg_depth_average_plan), mMyCalc.getMaxAverageDepth().toString(),mMyCalc.getDepthUnit());
            mBindingPlanning.editTextAD.setError(message);
            requestFocus(mBindingPlanning.editTextAD);
            return true;
        } else {
            return false;
        }
    }

    private boolean validateMaximumDepth() {
        // Must be valid for a status of Plan or Real
        if (((mBindingPlanning.editTextMD.getText().toString().trim().isEmpty())  || (!mBindingPlanning.editTextMD.getText().toString().trim().isEmpty() && isInvalidMaximumDepth(mDive.getMaximumDepth())))) {
            String message = String.format(getResources().getString(R.string.msg_depth_maximum),mMyCalc.getMaxAverageDepth().toString(),mMyCalc.getDepthUnit());
            mBindingPlanning.editTextMD.setError(message);
            requestFocus(mBindingPlanning.editTextMD);
            return true;
        } else {
            return false;
        }
    }

    private boolean validateAltitude() {
        // Required
        if (mBindingPlanning.editTextAL.getText().toString().trim().isEmpty() || !isValidAltitude(mDive.getAltitude())) {
            String message = String.format(getResources().getString(R.string.msg_altitude),mMyCalc.getMinAltitude().toString(),mMyCalc.getMaxAltitude().toString(),mMyCalc.getDepthUnit());
            mBindingPlanning.editTextAL.setError(message);
            requestFocus(mBindingPlanning.editTextAL);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateMeRmv() {
        // Required
        // Check the RMV that has been entered because:
        // 1) It is a Plan Dive
        // 2) And either Beginning and/or Ending Pressures and/or Bottom Time are missing
        Double beginningPressure = mAirDa.getSumBeginningPressure(mDive.getMyDiverNo(),mDive.getDiveNo());
        Double endingPressure = mAirDa.getSumEndingPressure(mDive.getMyDiverNo(),mDive.getDiveNo());
        if ( mDive.getStatus().equals(MyConstants.PLAN) && (mDive.getAverageDepth() == 0.0 || mDive.getBottomTime() == 0 || beginningPressure == 0.0 || endingPressure == 0.0)) {
            if ((mDive.getMyDiverNo() != 0) && (mBindingPlanning.myRmv.getText().toString().trim().isEmpty() || isInvalidRmv(mDive.getMyRmv()))) {
                String message = String.format(getResources().getString(R.string.msg_rmv), mMyCalc.getMaxRmv().toString(),mMyCalc.getRmvUnit());
                mBindingPlanning.myRmv.setError(message);
                requestFocus(mBindingPlanning.myRmv);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private boolean validateMyBuddyRmv() {
        // Required
        // Check the RMV that has been entered because:
        // 1) It is a Plan Dive
        // 2) And either Beginning and/or Ending Pressures and/or Bottom Time are missing
        Double beginningPressure = mAirDa.getSumBeginningPressure(mDive.getMyBuddyDiverNo(),mDive.getDiveNo());
        Double endingPressure = mAirDa.getSumEndingPressure(mDive.getMyBuddyDiverNo(),mDive.getDiveNo());
        if ( mDive.getStatus().equals(MyConstants.PLAN) && (mDive.getAverageDepth() == 0.0 || mDive.getBottomTime() == 0 || beginningPressure == 0.0 || endingPressure == 0.0)) {
            if ((mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) && (mBindingPlanning.myBuddyRmv.getText().toString().trim().isEmpty() || isInvalidRmv(mDive.getMyBuddyRmv()))) {
                mBindingPlanning.myBuddyRmv.setError(getString(R.string.msg_rmv));
                requestFocus(mBindingPlanning.myBuddyRmv);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private boolean validateEquipmentGroup() {
        // Required when the Status is Real
        if (mDive.getMyDiverNo() != MyConstants.ZERO_L && mDive.getMyGroupNo() == MyConstants.ZERO_L) {
            showError(getString(R.string.msg_my_group_required));
            requestFocus(mBindingPlanning.myGroup);
            return true;
        } else if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L && mDive.getMyBuddyGroupNo() == MyConstants.ZERO_L)   {
            showError(getString(R.string.msg_my_buddy_group_required));
            requestFocus(mBindingPlanning.myBuddyGroup);
            return true;
        } else {
            return false;
        }
    }

    private boolean validateConsumption() {
        // Required when the Status is Real
        if (mDive.getStatus().equals(getString(R.string.code_dive_status_real)) && (mDive.getMyDiverNo() != MyConstants.ZERO_L && mAirDa.getSumEndingPressure(mDive.getMyDiverNo(), mDive.getDiveNo()).equals(MyConstants.ZERO_D))) {
            showError(getString(R.string.msg_my_consumption_required));
            requestFocus(mBindingPlanning.myConsumption);
            return true;
        } else if (mDive.getStatus().equals(getString(R.string.code_dive_status_real)) && (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L && mAirDa.getSumEndingPressure(mDive.getMyBuddyDiverNo(), mDive.getDiveNo()).equals(MyConstants.ZERO_D))) {
            showError(getString(R.string.msg_my_buddy_consumption_required));
            requestFocus(mBindingPlanning.myBuddyConsumption);
            return true;
        } else {
            return false;
        }
    }

    private boolean validatePlanning() {
        // Required when the Status is Plan
        if (mDive.getDiveNo() != MyConstants.ZERO_L && mDive.getStatus().equals(MyConstants.PLAN) && mDive.getDivePlanCount() == MyConstants.ZERO_I) {
            showError(getString(R.string.msg_planning_required));
            mViewPager2.setCurrentItem(mPlanningPosition);
            requestFocus(mBindingPlanning.planning);
            return true;
        } else {
            return false;
        }
    }

    private boolean validateDiverDepth() {
        // Required - Not responsible for divers going past their limits!
        // Must be under the Max Depth Allowed for both Divers
        Double maxDepth = mAirDa.getDivePlanMaxDepth(mDive.getDiveNo());
        if (    mDive.getStatus().equals(MyConstants.PLAN)
             && (   (mDive.getMyDiverNo() != MyConstants.ZERO_L && maxDepth > mAirDa.getDiverMaxDepthAllowed(mDive.getMyDiverNo()))
                 || (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L && maxDepth > mAirDa.getDiverMaxDepthAllowed(mDive.getMyBuddyDiverNo()))
                )
           ) {
            showError(getString(R.string.msg_diver_depth));
            requestFocus(mBindingPlanning.planning);
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidLogBookNo(Integer logBookNo) {
        // Allow duplicate Log Book No
        // Useful when calculating different RMV during the same dive e.g. Swam, Work or Resting
        return (logBookNo >= MyConstants.ZERO_L && logBookNo <= MyConstants.MAX_LOG_BOOK_NO);
    }

    private boolean isInvalidAverageDepthReal(Double averageDepth) {
        return (averageDepth < MyConstants.ONE_D || averageDepth > mMyCalc.getMaxAverageDepth());
    }

    private boolean isInvalidAverageDepthPlan(Double averageDepth) {
        return (averageDepth < MyConstants.ZERO_D || averageDepth > mMyCalc.getMaxAverageDepth());
    }

    private boolean isInvalidMaximumDepth(Double maximumDepth) {
        return (maximumDepth < MyConstants.ZERO_D || maximumDepth > mMyCalc.getMaxAverageDepth());
    }

    private boolean isInvalidBottomTimeReal(String bottomTime) {
        // 2020/03/25 Optimized to support bottomTime mm:ss
        // Required for a Status of Real. >= 0:01
        // Return true if invalid
        // Return false if valid
        String formattedBottomTime = MyFunctions.formatBottomTime(bottomTime);

        if (formattedBottomTime.isEmpty()) {
            return true;
        } else return formattedBottomTime.equals("00:00") || formattedBottomTime.equals("0:00");
    }

    private boolean isInvalidBottomTimePlan(String bottomTime) {
        // 2020/03/25 Optimized to support bottomTime mm:ss
        // Required for a Status of Real. >= 0:00
        // Return true if invalid
        // Return false if valid
        String formattedBottomTime = MyFunctions.formatBottomTime(bottomTime);

        return formattedBottomTime.isEmpty();
    }

    private boolean isValidAltitude(int altitude) {
        return (altitude >=  mMyCalc.getMinAltitude() && altitude <= mMyCalc.getMaxAltitude());
    }

    private boolean isInvalidRmv(Double rmv) {
        return (rmv < MyConstants.MIN_RMV || rmv > mMyCalc.getMaxRmv());
    }

    private void addDive(Dive dive) {mAirDa.createDive(dive, false);}

    private void updateDive(Dive dive) {mAirDa.updateDive(dive);}

    private void addDiverDive(DiverDive diverDive) {mAirDa.createDiverDive(diverDive);}

    private void updateMeMyBuddySac() {
        // Update DIVER_DIVE_GROUP Me
        DiverDiveGroup diverDiveGroup = new DiverDiveGroup();
        diverDiveGroup.setDiverNo(MyConstants.ONE_L);
        diverDiveGroup.setDiveNo(mDive.getDiveNo());
        diverDiveGroup.setGroupNo(mDive.getMyGroupNo());
        diverDiveGroup.setSac(mDive.getMySac());
        updateDiverDiveGroup(diverDiveGroup);
        // Update DIVER_DIVE_GROUP My Buddy
        diverDiveGroup.setDiverNo(mDive.getMyBuddyDiverNo());
        diverDiveGroup.setDiveNo(mDive.getDiveNo());
        diverDiveGroup.setGroupNo(mDive.getMyBuddyGroupNo());
        diverDiveGroup.setSac(mDive.getMyBuddySac());
        updateDiverDiveGroup(diverDiveGroup);
    }

    private void updateMeMyBuddyRmv() {
        // Update DIVER_DIVE Me
        DiverDive diverDive = new DiverDive();
        diverDive.setDiverNo(MyConstants.ONE_L);
        diverDive.setDiveNo(mDive.getDiveNo());
        // Cannot calculate RMV with MyCalc because the consumption has not been added yet
        diverDive.setRmv(mDive.getMyRmv());
        diverDive.setIsPrimary("M");
        updateDiverDive(diverDive);
        // Update DIVER_DIVE My Buddy
        diverDive.setDiverNo(mDive.getMyBuddyDiverNo());
        diverDive.setDiveNo(mDive.getDiveNo());
        // Cannot calculate RMV with MyCalc because the consumption has not been added yet
        diverDive.setRmv(mDive.getMyBuddyRmv());
        diverDive.setIsPrimary("Y");
        updateDiverDive(diverDive);
    }

    private void updateDiverDive(DiverDive diverDive) {mAirDa.updateDiverDive(diverDive);}

    private void removeDiverDive(Long diverNo, Long diveNo) {mAirDa.deleteDiverDive(diverNo, diveNo);}

    private void removeDiverDiveGroup(Long diverNo, Long diveNo) {mAirDa.deleteDiverDiveGroupByDiverNoDiveNo(diverNo, diveNo);}

    private void addDiverDiveGroup(DiverDiveGroup diverDiveGroup) {mAirDa.createDiverDiveGroup(diverDiveGroup);}

    private void updateDiverDiveGroup(DiverDiveGroup diverDiveGroup) {mAirDa.updateDiverDiveGroup(diverDiveGroup);}

    private void createDiverDiveGroupCylinder(ArrayList<GrouppCylinder> grouppCylinderList) {
        for (int i = 0; i < grouppCylinderList.size(); i++) {
            GrouppCylinder grouppCylinder = grouppCylinderList.get(i);
            DiverDiveGroupCyl diverDiveGroupCyl = new DiverDiveGroupCyl();
            diverDiveGroupCyl.setDiverNo(grouppCylinder.getDiverNo());
            diverDiveGroupCyl.setDiveNo(mDive.getDiveNo());
            diverDiveGroupCyl.setGroupNo(grouppCylinder.getGroupNo());
            diverDiveGroupCyl.setCylinderNo(grouppCylinder.getCylinderNo());
            diverDiveGroupCyl.setBeginningPressure(grouppCylinder.getRatedPressure());
            diverDiveGroupCyl.setEndingPressure(MyConstants.ZERO_D);
            diverDiveGroupCyl.setO2(Integer.parseInt(getString(R.string.code_default_o2)));
            diverDiveGroupCyl.setN(Integer.parseInt(getString(R.string.code_default_n)));
            diverDiveGroupCyl.setHe(Integer.parseInt(getString(R.string.code_default_he)));
            diverDiveGroupCyl.setUsageType(getString(R.string.cd_blank));
            addDiverDiveGroupCylinder(diverDiveGroupCyl);
        }
    }

    private void addDiverDiveGroupCylinder(DiverDiveGroupCyl diverDiveGroupCyl) {mAirDa.createDiverDiveGroupCylinder(diverDiveGroupCyl);}

    private void removeDiverDiveGroupCyl(Long diverNo, Long diveNo) {mAirDa.deleteDiverDiveGroupCylinderByDiverNoDiveNo(diverNo, diveNo);}

    private void refreshViewsComputer() {

    }

    private void refreshViewsEnvironment() {

    }

    private void refreshViewsGas() {

    }

    private void refreshViewsGear() {

    }

    private void refreshViewsGraph() {

    }

    private void refreshViewsPlanning() {
        // Edit mode
        // Save original values
        mMyGroupNoOrig = mDive.getMyGroupNo();
        mMyBuddyDiverNoOrig = mDive.getMyBuddyDiverNo();
        mMyBuddyGroupNoOrig = mDive.getMyBuddyGroupNo();

        // Me
        if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
            mBindingPlanning.myGroup.setEnabled(true);
            if (mDive.getMyGroupNo() != MyConstants.ZERO_L) {
                mBindingPlanning.myGroup.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
            } else {
                mBindingPlanning.myGroup.setTextColor(ContextCompat.getColor(this, R.color.purple));
            }
        }

        // My Buddy
        if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            mBindingPlanning.myBuddyGroup.setEnabled(true);
            if (mDive.getMyBuddyGroupNo() != MyConstants.ZERO_L) {
                mBindingPlanning.myBuddyGroup.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
            } else {
                mBindingPlanning.myBuddyGroup.setTextColor(ContextCompat.getColor(this, R.color.purple));
            }
        }

        if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            mBindingPlanning.myButton.setEnabled(true);
            mBindingPlanning.myButton.setVisibility(View.VISIBLE);
            // The Buddy can be removed
            mBindingPlanning.myBuddyButton.setEnabled(true);
            mBindingPlanning.myBuddyButton.setVisibility(View.VISIBLE);
        } else {
            // Me cannot be removed yet
            mBindingPlanning.myButton.setEnabled(false);
            mBindingPlanning.myButton.setVisibility(View.GONE);
            // The Buddy cannot be removed yet
            mBindingPlanning.myBuddyButton.setEnabled(false);
            mBindingPlanning.myBuddyButton.setVisibility(View.GONE);
        }

        if(mDive.getMyDiverNo() == MyConstants.ZERO_L && mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            // Me can be added back
            mBindingPlanning.myButton.setEnabled(true);
            mBindingPlanning.myButton.setVisibility(View.VISIBLE);
            mBindingPlanning.myButton.setImageResource(R.drawable.ic_plus);
            mBindingPlanning.myButton.setColorFilter(Color.RED);
        }

        // SAC & RMV
        // Me
        if (mDive.getMyDiverNo() != 0) {
            mBindingPlanning.mySac.setEnabled(true);
            mBindingPlanning.myRmv.setEnabled(true);
        } else {
            mBindingPlanning.mySac.setEnabled(false);
            mBindingPlanning.myRmv.setEnabled(false);
        }

        // My Buddy
        if (mDive.getMyBuddyDiverNo() != 0) {
            mBindingPlanning.myBuddySac.setEnabled(true);
            mBindingPlanning.myBuddyRmv.setEnabled(true);
        } else {
            mBindingPlanning.myBuddySac.setEnabled(false);
            mBindingPlanning.myBuddyRmv.setEnabled(false);
        }

        // Planning
        mBindingPlanning.planning.setEnabled(true);
        if (mDive.getDivePlanCount() == MyConstants.ZERO_I) {
            mBindingPlanning.planning.setTextColor(ContextCompat.getColor(this, R.color.purple));
        } else {
            mBindingPlanning.planning.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
        }

        // Consumption
        // Me
        if (mDive.getMyGroupNo() != MyConstants.ZERO_L) {
            mBindingPlanning.myConsumption.setEnabled(true);
            if (mAirDa.getSumEndingPressure(mDive.getMyDiverNo(), mDive.getDiveNo()).equals(MyConstants.ZERO_D)) {
                mBindingPlanning.myConsumption.setTextColor(ContextCompat.getColor(this, R.color.purple));
            } else {
                mBindingPlanning.myConsumption.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
            }
        } else {
            mBindingPlanning.myConsumption.setEnabled(false);
            mBindingPlanning.myConsumption.setTextColor(Color.BLACK);
        }

        // My Buddy
        if (mDive.getMyBuddyGroupNo() != MyConstants.ZERO_L) {
            mBindingPlanning.myBuddyConsumption.setEnabled(true);
            if (mAirDa.getSumEndingPressure(mDive.getMyBuddyDiverNo(), mDive.getDiveNo()).equals(MyConstants.ZERO_D)) {
                mBindingPlanning.myBuddyConsumption.setTextColor(ContextCompat.getColor(this, R.color.purple));
            } else {
                mBindingPlanning.myBuddyConsumption.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
            }
        } else {
            mBindingPlanning.myBuddyConsumption.setEnabled(false);
            mBindingPlanning.myBuddyConsumption.setTextColor(Color.BLACK);
        }

        // Extra Diver(s)
        mBindingPlanning.extraDivers.setEnabled(true);
        if (mDive.getExtraDiversCount() == MyConstants.ZERO_I) {
            mBindingPlanning.extraDivers.setTextColor(ContextCompat.getColor(this, R.color.purple));
        } else {
            mBindingPlanning.extraDivers.setTextColor(ContextCompat.getColor(this, R.color.theme_myapp_action_bar));
        }

//        // Must reset the mDive data in the RecyclerView??
//        mDivePagerAdapter.setDive(mDive);
    }

    private void refreshViewsProblem() {

    }

    private void refreshViewsSummary() {

    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveState() {
        mState.setDiveType(mDive.getDiveType());
        mState.setMySac(mDive.getMySac());
        mState.setMyRmv(mDive.getMyRmv());
        mState.setMyGroup(mDive.getMyGroupNo());
        mState.setBuddyDiverNo(mDive.getMyBuddyDiverNo());
        mState.setMyBuddySac(mDive.getMyBuddySac());
        mState.setMyBuddyRmv(mDive.getMyBuddyRmv());
        mState.setMyBuddyGroup(mDive.getMyBuddyGroupNo());
        mAirDa.updateState(mState);
    }

    private void calculate() {

        if (validateBottomTime()) {
            return;
        }

        if (validateAverageDepth()) {
            return;
        }

        if (validateEquipmentGroup()) {
            return;
        }

        if (validateConsumption()) {
            return;
        }

        if (validatePlanning()) {
            return;
        }

        if (validateDiverDepth()) {
            return;
        }

        if (mDive.getMyDiverNo() != MyConstants.ZERO_L) {
            // Calculates the SAC & RMV for Me
            Double beginningPressure = mAirDa.getSumBeginningPressure(mDive.getMyDiverNo(), mDive.getDiveNo());
            Double endingPressure = mAirDa.getSumEndingPressure(mDive.getMyDiverNo(), mDive.getDiveNo());
            if (mDive.getStatus().equals(getString(R.string.code_dive_status_real))
                    || (mDive.getStatus().equals(MyConstants.PLAN) && mDive.getAverageDepth() != 0.0 && mDive.getBottomTime() != 0 && beginningPressure != 0.0 && endingPressure != 0.0)
                    ) {

                mDive.setMySac(MyFunctions.roundUp(mMyCalc.getSac(beginningPressure, endingPressure, mDive.getBottomTime(), mDive.getAverageDepth(), mDive.getSalinity()),2));
                mBindingPlanning.mySac.setText(String.valueOf(mDive.getMySac()));

                mDive.setMyRmv(MyFunctions.roundUp(mMyCalc.getRmv(mDive.getMySac(), mDive.getMyRatedVolume(), mDive.getMyRatedPressure()),2));
                mBindingPlanning.myRmv.setText(String.valueOf(mDive.getMyRmv()));
            }
        }

        if (mDive.getMyBuddyDiverNo() != MyConstants.ZERO_L) {
            // Calculates the SAC & RMV for My Buddy
            Double beginningPressure = mAirDa.getSumBeginningPressure(mDive.getMyBuddyDiverNo(), mDive.getDiveNo());
            Double endingPressure = mAirDa.getSumEndingPressure(mDive.getMyBuddyDiverNo(), mDive.getDiveNo());
            if (mDive.getStatus().equals(getString(R.string.code_dive_status_real))
                    || (mDive.getStatus().equals(MyConstants.PLAN) && mDive.getAverageDepth() != 0.0 && mDive.getBottomTime() != 0 && beginningPressure != 0.0 && endingPressure != 0.0)
                    ) {

                mDive.setMyBuddySac(MyFunctions.roundUp(mMyCalc.getSac(beginningPressure, endingPressure, mDive.getBottomTime(), mDive.getAverageDepth(), mDive.getSalinity()),2));
                mBindingPlanning.myBuddySac.setText(String.valueOf(mDive.getMyBuddySac()));

                mDive.setMyBuddyRmv(MyFunctions.roundUp(mMyCalc.getRmv(mDive.getMyBuddySac(), mDive.getMyBuddyRatedVolume(), mDive.getMyBuddyRatedPressure()),2));
                mBindingPlanning.myBuddyRmv.setText(String.valueOf(mDive.getMyBuddyRmv()));
            }
        }
    }
}