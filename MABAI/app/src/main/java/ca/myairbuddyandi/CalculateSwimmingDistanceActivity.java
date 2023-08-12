package ca.myairbuddyandi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import ca.myairbuddyandi.databinding.CalculateSwimmingDistanceActivityBinding;

/**
 * Created by Michel on 2017-01-04.
 * Hold all of the logic for the CalculateSwimmingDistanceActivity class
 */

public class CalculateSwimmingDistanceActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "CalculateSwimmingDistanceActivity";

    // Public

    // Protected

    // Private
    private String mUnit;
    private final CalculateSwimmingDistance mCalculateSwimmingDistance = new CalculateSwimmingDistance();
    private CalculateSwimmingDistanceActivityBinding mBinding = null;
    private final MyCalcImperial mMyCalcImperial = new MyCalcImperial(this);
    private final MyCalcMetric mMyCalcMetric = new MyCalcMetric(this);

    // Default
    private Double mDefaultDistance;
    private Double mDefaultDistancePerKick;
    private Double mDefaultKicks;
    private Double mDefaultSpeedMin;
    private Double mDefaultSpeedSec;
    private Double mDefaultTime;
    private String mDefaultUnit;

    // Other
    private Double mOtherDistance;
    private Double mOtherDistancePerKick;
    private Double mOtherKicks;
    private Double mOtherSpeedMin;
    private Double mOtherSpeedSec;
    private Double mOtherTime;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.calculate_swimming_distance_activity);

        mCalculateSwimmingDistance.mBinding = mBinding;

        mBinding.setCalculateSwimmingDistance(mCalculateSwimmingDistance);

        // Set the listeners
        mBinding.switchUnit.setOnClickListener(view -> {
            switchSide();
            switchToOtherUnit();
        });

        //

        mBinding.calculateDistanceButton.setOnClickListener(view -> calculateDistance());

        mBinding.calculateDistanceButton2.setOnClickListener(view -> calculateDistance());

        //

        mBinding.calculateDistanceKickButton.setOnClickListener(view -> calculateDistanceKick());

        mBinding.calculateDistanceKickButton2.setOnClickListener(view -> calculateDistanceKick());

        //

        mBinding.calculateKicksButton.setOnClickListener(view -> calculateKicks());

        mBinding.calculateKicksButton2.setOnClickListener(view -> calculateKicks());

        //

        mBinding.calculateSpeedButton.setOnClickListener(view -> calculateSpeed());

        mBinding.calculateSpeedButton2.setOnClickListener(view -> calculateSpeed());

        //

        mBinding.calculateTimeButton.setOnClickListener(view -> calculateTime());

        mBinding.calculateTimeButton2.setOnClickListener(view -> calculateTime());

        //

        mBinding.clearButton.setOnClickListener(view -> clear());

        // Get the phone unit as stored in MainActivity.java
        mUnit = MyFunctions.getUnit();

        // Read the previously saved Default Values
        readDefaultValues();

        mBinding.calculateSwimmingDistanceActivity.requestFocus();

        Log.d(LOG_TAG, "onCreate done");
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_calculate_swimming_distance));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            saveDefaultValues();
            Intent intent = new Intent();
            intent.putExtra(MyConstants.SWIMMING_SPEED,mDefaultSpeedSec);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        saveDefaultValues();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void calculateDistance() {
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialDistance();
            // Calculate Metric values
            convertToMetricDistance();
        } else {
            calculateMetricDistance();
            // Calculate Imperial values
            convertToImperialDistance();
        }

        mBinding.defaultDistance.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistance()));
        mBinding.defaultDistancePerKick.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistancePerKick()));
        mBinding.defaultKicks.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultKicks()));
        mBinding.defaultSpeedMin.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedMin()));
        mBinding.defaultSpeedSec.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedSec()));
        mBinding.defaultTime.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultTime()));

        requestFocus(mBinding.defaultDistance,false);
    }

    private void calculateDistanceKick() {
        // Distance Per Kick =  Distance / Kicks
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialDistanceKick();
            // Calculate Metric values
            convertToMetricDistanceKick();
        } else {
            calculateMetricDistanceKick();
            // Calculate Imperial values
            convertToImperialDistanceKick();
        }

        // Swim Speed and Time are not part of the equation
        mCalculateSwimmingDistance.setDefaultSpeedMin(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultSpeedSec(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultTime(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherSpeedMin(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherSpeedSec(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherTime(MyConstants.ZERO_D);

        mBinding.defaultDistance.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistance()));
        mBinding.defaultDistancePerKick.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistancePerKick()));
        mBinding.defaultKicks.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultKicks()));
        mBinding.defaultSpeedMin.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedMin()));
        mBinding.defaultSpeedSec.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedSec()));
        mBinding.defaultTime.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultTime()));

        requestFocus(mBinding.defaultDistance,false);
    }

    private void calculateKicks() {
        // Kicks = Distance / Distance Per Kick
        // Kicks = (Speed * Time) / Distance Per Kick
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialKicks();
            // Calculate Metric values
            convertToMetricKicks();
        } else {
            calculateMetricKicks();
            // Calculate Imperial values
            convertToImperialKicks();
        }

        // Swim Speed and Time are not part of the equation
        mCalculateSwimmingDistance.setDefaultSpeedMin(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultSpeedSec(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultTime(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherSpeedMin(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherSpeedSec(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherTime(MyConstants.ZERO_D);

        mBinding.defaultDistance.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistance()));
        mBinding.defaultDistancePerKick.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistancePerKick()));
        mBinding.defaultKicks.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultKicks()));
        mBinding.defaultSpeedMin.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedMin()));
        mBinding.defaultSpeedSec.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedSec()));
        mBinding.defaultTime.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultTime()));

        requestFocus(mBinding.defaultKicks, false);
    }

    private void calculateSpeed() {
        // Speed = Distance / Time
        // Speed = ((Distance/Kick) * Kicks) / Time
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialSpeed();
            // Calculate Metric values
            convertToMetricSpeed();
        } else {
            calculateMetricSpeed();
            // Calculate Imperial values
            convertToImperialSpeed();
        }

        // Distance/Kick and Kicks are not part of the equation
        mCalculateSwimmingDistance.setDefaultDistancePerKick(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultKicks(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherKicks(MyConstants.ZERO_D);

        mBinding.defaultDistance.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistance()));
        mBinding.defaultDistancePerKick.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistancePerKick()));
        mBinding.defaultKicks.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultKicks()));
        mBinding.defaultSpeedMin.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedMin()));
        mBinding.defaultSpeedSec.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedSec()));
        mBinding.defaultTime.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultTime()));

        requestFocus(mBinding.defaultDistance, false);
    }

    private void calculateTime() {
        // Time = Distance / Speed
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            calculateImperialTime();
            // Calculate Metric values
            convertToMetricTime();
        } else {
            calculateMetricTime();
            // Calculate Imperial values
            convertToImperialTime();
        }

        // Distance/Kick and Kicks are not part of the equation
        mCalculateSwimmingDistance.setDefaultDistancePerKick(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultKicks(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherKicks(MyConstants.ZERO_D);

        mBinding.defaultDistance.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistance()));
        mBinding.defaultDistancePerKick.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultDistancePerKick()));
        mBinding.defaultKicks.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultKicks()));
        mBinding.defaultSpeedMin.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedMin()));
        mBinding.defaultSpeedSec.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultSpeedSec()));
        mBinding.defaultTime.setText(String.valueOf(mCalculateSwimmingDistance.getDefaultTime()));

        requestFocus(mBinding.defaultTime, false);
    }

    private void calculateImperialDistance() {
        if (mCalculateSwimmingDistance.getSource().equals("distanceperkick") || mCalculateSwimmingDistance.getSource().equals("kicks") ) {
            // Distance = Distance Per Kick * Kicks
            // Get all entered values first
            mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
            mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
            mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
            mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

            // Calculate Distance
            mDefaultDistance = mDefaultDistancePerKick * mDefaultKicks;

            // Set the calculated values in the POJO
            mCalculateSwimmingDistance.setDefaultDistance(MyFunctions.roundUp(mDefaultDistance,1));

            // Swim Speed and Time are not part of the equation
            mCalculateSwimmingDistance.setDefaultSpeedMin(MyConstants.ZERO_D);
            mCalculateSwimmingDistance.setDefaultSpeedSec(MyConstants.ZERO_D);
            mCalculateSwimmingDistance.setDefaultTime(MyConstants.ZERO_D);
        } else {
            // Distance = Swim Speed * Time
            // Get all entered values first
            mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
            mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

            // Calculate Distance
            mDefaultDistance = mDefaultSpeedMin * mDefaultTime;
            // Convert the feet/min to feet/sec
            mDefaultSpeedSec = mDefaultSpeedMin / 60;

            // Set the calculated values in the POJO
            mCalculateSwimmingDistance.setDefaultDistance(MyFunctions.roundUp(mDefaultDistance,1));
            mCalculateSwimmingDistance.setDefaultSpeedSec(MyFunctions.roundUp(mDefaultSpeedSec,1));

            // Distance/Kick and Kicks are not part of the equation
            mCalculateSwimmingDistance.setDefaultDistancePerKick(MyConstants.ZERO_D);
            mCalculateSwimmingDistance.setDefaultKicks(MyConstants.ZERO_D);
        }
    }

    private void calculateImperialDistanceKick() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Calculate Distance Per Kick
        if (mDefaultKicks.equals(MyConstants.ZERO_D)) {
            mDefaultDistancePerKick = MyConstants.ZERO_D;
        } else {
            mDefaultDistancePerKick = mDefaultDistance / mDefaultKicks;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setDefaultDistancePerKick(MyFunctions.roundUp(mDefaultDistancePerKick,1));
    }

    private void calculateImperialKicks() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultSpeedSec = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Calculate Kicks
        if (mDefaultDistancePerKick.equals(MyConstants.ZERO_D)) {
            mDefaultKicks = MyConstants.ZERO_D;
        } else if (!mDefaultDistance.equals(MyConstants.ZERO_D)) {
            mDefaultKicks = mDefaultDistance / mDefaultDistancePerKick;
        } else if (!mDefaultSpeedMin.equals(MyConstants.ZERO_D) || !mDefaultTime.equals(MyConstants.ZERO_D)) {
                mDefaultKicks = (mDefaultSpeedMin * mDefaultTime) / mDefaultDistancePerKick;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setDefaultKicks(MyFunctions.roundUp(mDefaultKicks,1));
    }

    private void calculateImperialSpeed() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Calculate Speed
        if (mDefaultTime.equals(MyConstants.ZERO_D)) {
            mDefaultSpeedMin = MyConstants.ZERO_D;
        } else if (!mDefaultDistance.equals(MyConstants.ZERO_D)) {
            mDefaultSpeedMin = mDefaultDistance / mDefaultTime;
        } else if (!mDefaultDistancePerKick.equals(MyConstants.ZERO_D)) {
            mDefaultSpeedMin = (mDefaultDistancePerKick * mDefaultKicks) / mDefaultTime;
        }
        mDefaultSpeedSec = mDefaultSpeedMin / 60.0;

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setDefaultSpeedMin(MyFunctions.roundUp(mDefaultSpeedMin,1));
        mCalculateSwimmingDistance.setDefaultSpeedSec(MyFunctions.roundUp(mDefaultSpeedSec,1));
    }

    private void calculateImperialTime() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultSpeedSec = mDefaultSpeedMin / 60;

        // Calculate Time
        if (mDefaultSpeedMin.equals(MyConstants.ZERO_D)) {
            mDefaultTime = MyConstants.ZERO_D;
        } else {
            mDefaultTime = mDefaultDistance / mDefaultSpeedMin;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setDefaultTime(MyFunctions.roundUp(mDefaultTime,1));
        mCalculateSwimmingDistance.setDefaultSpeedSec(MyFunctions.roundUp(mDefaultSpeedSec,1));
    }

    private void calculateMetricDistance() {
        if (mCalculateSwimmingDistance.getSource().equals("distanceperkick") || mCalculateSwimmingDistance.getSource().equals("kicks") ) {
            // Distance = Distance Per Kick * Kicks
            // Get all entered values first
            mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
            mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
            mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
            mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

            // Calculate Distance
            mDefaultDistance = mDefaultDistancePerKick * mDefaultKicks;

            // Set the calculated values in the POJO
            mCalculateSwimmingDistance.setDefaultDistance(MyFunctions.roundUp(mDefaultDistance,1));

            // Swim Speed and Time are not part of the equation
            mCalculateSwimmingDistance.setDefaultSpeedMin(MyConstants.ZERO_D);
            mCalculateSwimmingDistance.setDefaultSpeedSec(MyConstants.ZERO_D);
            mCalculateSwimmingDistance.setDefaultTime(MyConstants.ZERO_D);
        } else {
            // Distance = Swim Speed * Time
            // Get all entered values first
            mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
            mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

            // Calculate Distance
            mDefaultDistance = mDefaultSpeedMin * mDefaultTime;
            // Convert the feet/min to feet/sec
            mDefaultSpeedSec = mDefaultSpeedMin / 60;

            // Set the calculated values in the POJO
            mCalculateSwimmingDistance.setDefaultDistance(MyFunctions.roundUp(mDefaultDistance,1));
            mCalculateSwimmingDistance.setDefaultSpeedSec(MyFunctions.roundUp(mDefaultSpeedSec,1));

            // Distance/Kick and Kicks are not part of the equation
            mCalculateSwimmingDistance.setDefaultDistancePerKick(MyConstants.ZERO_D);
            mCalculateSwimmingDistance.setDefaultKicks(MyConstants.ZERO_D);
        }
    }

    private void calculateMetricDistanceKick() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Calculate Distance Per Kick
        if (mDefaultKicks.equals(MyConstants.ZERO_D)) {
            mDefaultDistancePerKick = MyConstants.ZERO_D;
        } else {
            mDefaultDistancePerKick = mDefaultDistance / mDefaultKicks;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setDefaultDistancePerKick(MyFunctions.roundUp(mDefaultDistancePerKick,1));
    }

    private void calculateMetricKicks() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Calculate Kicks
        if (mDefaultDistancePerKick.equals(MyConstants.ZERO_D)) {
            mDefaultKicks = MyConstants.ZERO_D;
        } else if (!mDefaultDistance.equals(MyConstants.ZERO_D)) {
            mDefaultKicks = mDefaultDistance / mDefaultDistancePerKick;
        } else if (!mDefaultSpeedMin.equals(MyConstants.ZERO_D) || !mDefaultTime.equals(MyConstants.ZERO_D)) {
            mDefaultKicks = (mDefaultSpeedMin * mDefaultTime) / mDefaultDistancePerKick;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setDefaultKicks(MyFunctions.roundUp(mDefaultKicks,1));
    }

    private void calculateMetricSpeed() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Calculate Speed
        if (mDefaultTime.equals(MyConstants.ZERO_D)) {
            mDefaultSpeedMin = MyConstants.ZERO_D;
        } else if (!mDefaultDistance.equals(MyConstants.ZERO_D)) {
            mDefaultSpeedMin = mDefaultDistance / mDefaultTime;
        } else if (!mDefaultDistancePerKick.equals(MyConstants.ZERO_D)) {
            mDefaultSpeedMin = (mDefaultDistancePerKick * mDefaultKicks) / mDefaultTime;
        }

        mDefaultSpeedSec = mDefaultSpeedMin / 60.0;

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setDefaultSpeedMin(MyFunctions.roundUp(mDefaultSpeedMin,1));
        mCalculateSwimmingDistance.setDefaultSpeedSec(MyFunctions.roundUp(mDefaultSpeedSec,1));
    }

    private void calculateMetricTime() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultSpeedSec = mDefaultSpeedMin / 60;

        // Calculate Time
        if (mDefaultSpeedMin.equals(MyConstants.ZERO_D)) {
            mDefaultTime = MyConstants.ZERO_D;
        } else {
            mDefaultTime = mDefaultDistance / mDefaultSpeedMin;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setDefaultTime(MyFunctions.roundUp(mDefaultTime,1));
        mCalculateSwimmingDistance.setDefaultSpeedSec(MyFunctions.roundUp(mDefaultSpeedSec,1));
    }

    private void convertToImperialDistance() {
        // Get all entered values first
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        if (mCalculateSwimmingDistance.getSource().equals("distanceperkick") || mCalculateSwimmingDistance.getSource().equals("kicks") ) {
            // Convert Meter to Feet
            mOtherDistancePerKick = mMyCalcImperial.convertMeterToFeet(mDefaultDistancePerKick);

            // Calculate Distance
            mOtherKicks = mDefaultKicks;
            mCalculateSwimmingDistance.setOtherKicks(mDefaultKicks);
            mOtherDistance = mOtherDistancePerKick * mOtherKicks;

            // Swim Speed and Time are not part of the equation
            mOtherSpeedMin = MyConstants.ZERO_D;
            mOtherSpeedSec = MyConstants.ZERO_D;
            mOtherTime = MyConstants.ZERO_D;
        } else {
            // Convert Meter to Feet
            mOtherDistance = mMyCalcImperial.convertMeterToFeet(mDefaultDistance);
            mOtherSpeedMin = mMyCalcImperial.convertMeterToFeet(mDefaultSpeedMin);
            mOtherSpeedSec = mOtherSpeedMin / 60.0;

            // Distance/Kick and Kicks are not part of the equation
            mOtherDistancePerKick = MyConstants.ZERO_D;
            mOtherKicks = MyConstants.ZERO_D;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyFunctions.roundUp(mOtherDistancePerKick,1));
        mCalculateSwimmingDistance.setOtherKicks(mOtherKicks);
        mCalculateSwimmingDistance.setOtherSpeedMin(MyFunctions.roundUp(mOtherSpeedMin,1));
        mCalculateSwimmingDistance.setOtherSpeedSec(MyFunctions.roundUp(mOtherSpeedSec,1));
        mCalculateSwimmingDistance.setOtherTime(mDefaultTime);
    }

    private void convertToImperialDistanceKick() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Convert Meter to Feet
        mOtherDistance = mMyCalcMetric.convertMeterToFeet(mDefaultDistance);
        mOtherKicks = mDefaultKicks;

        if (mOtherKicks.equals(MyConstants.ZERO_D)) {
            mOtherDistancePerKick = MyConstants.ZERO_D;
        } else {
            mOtherDistancePerKick = mOtherDistance / mOtherKicks;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherKicks(mDefaultKicks);
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyFunctions.roundUp(mOtherDistancePerKick,1));
    }

    private void convertToImperialKicks() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Convert Meter to Feet
        mOtherDistance = mMyCalcMetric.convertMeterToFeet(mDefaultDistance);
        mOtherSpeedMin = mMyCalcImperial.convertMeterToFeet(mDefaultSpeedMin);
        mOtherSpeedSec = mMyCalcImperial.convertMeterToFeet(mDefaultSpeedSec);
        mOtherDistancePerKick = mMyCalcMetric.convertMeterToFeet(mDefaultDistancePerKick);
        mOtherTime = mDefaultTime;

        // Calculate Kicks
        if (mOtherDistancePerKick.equals(MyConstants.ZERO_D)) {
            mOtherKicks = MyConstants.ZERO_D;
        } else if (!mOtherDistance.equals(MyConstants.ZERO_D)) {
            mOtherKicks = mOtherDistance / mOtherDistancePerKick;
        } else if (!mOtherSpeedMin.equals(MyConstants.ZERO_D) || !mOtherTime.equals(MyConstants.ZERO_D)) {
            mOtherKicks = (mOtherSpeedMin * mOtherTime) / mOtherDistancePerKick;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyFunctions.roundUp(mOtherDistancePerKick,1));
        mCalculateSwimmingDistance.setOtherKicks(MyFunctions.roundUp(mOtherKicks,1));
        mCalculateSwimmingDistance.setOtherSpeedMin(MyFunctions.roundUp(mOtherSpeedMin,1));
        mCalculateSwimmingDistance.setOtherSpeedSec(MyFunctions.roundUp(mOtherSpeedSec,1));
        mCalculateSwimmingDistance.setOtherTime(MyFunctions.roundUp(mOtherTime,1));
    }

    private void convertToImperialSpeed() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Convert Meter to Feet
        mOtherDistance = mMyCalcImperial.convertMeterToFeet(mDefaultDistance);
        mOtherDistancePerKick = mMyCalcImperial.convertMeterToFeet(mDefaultDistancePerKick);
        mOtherKicks = mDefaultKicks;
        mOtherTime = mDefaultTime;

        // Calculate Speed
        if (mOtherTime.equals(MyConstants.ZERO_D)) {
            mOtherSpeedMin = MyConstants.ZERO_D;
        } else if (!mOtherDistance.equals(MyConstants.ZERO_D)) {
            mOtherSpeedMin = mOtherDistance / mOtherTime;
        } else if (!mOtherDistancePerKick.equals(MyConstants.ZERO_D)) {
            mOtherSpeedMin = (mOtherDistancePerKick * mOtherKicks) / mOtherTime;
        }

        mOtherSpeedSec = mOtherSpeedMin / 60;

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyFunctions.roundUp(mOtherDistancePerKick,1));
        mCalculateSwimmingDistance.setOtherKicks(MyFunctions.roundUp(mOtherKicks,1));
        mCalculateSwimmingDistance.setOtherSpeedMin(MyFunctions.roundUp(mOtherSpeedMin,1));
        mCalculateSwimmingDistance.setOtherSpeedSec(MyFunctions.roundUp(mOtherSpeedSec,1));
        mCalculateSwimmingDistance.setOtherTime(MyFunctions.roundUp(mOtherTime,1));
    }

    private void convertToImperialTime() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultSpeedSec = mDefaultSpeedMin / 60;

        // Convert Feet to Meter
        mOtherDistance = mMyCalcImperial.convertMeterToFeet(mDefaultDistance);
        mOtherSpeedMin = mMyCalcImperial.convertMeterToFeet(mDefaultSpeedMin);
        mOtherSpeedSec = mMyCalcImperial.convertMeterToFeet(mDefaultSpeedSec);

        // Calculate Time
        if (mOtherSpeedMin.equals(MyConstants.ZERO_D)) {
            mOtherTime = MyConstants.ZERO_D;
        } else {
            mOtherTime = mOtherDistance / mOtherSpeedMin;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherSpeedMin(MyFunctions.roundUp(mOtherSpeedMin,1));
        mCalculateSwimmingDistance.setOtherSpeedSec(MyFunctions.roundUp(mOtherSpeedSec,1));
        mCalculateSwimmingDistance.setOtherTime(MyFunctions.roundUp(mOtherTime,1));
    }

    private void convertToMetricDistance() {
        // Get all entered values first
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        if (mCalculateSwimmingDistance.getSource().equals("distanceperkick") || mCalculateSwimmingDistance.getSource().equals("kicks") ) {
            // Convert Feet to Meter
            mOtherDistancePerKick = mMyCalcMetric.convertFeetToMeter(mDefaultDistancePerKick);

            // Calculate Distance
            mOtherKicks = mDefaultKicks;
            mCalculateSwimmingDistance.setOtherKicks(mDefaultKicks);
            mOtherDistance = mOtherDistancePerKick * mOtherKicks;

            // Swim Speed and Time are not part of the equation
            mOtherSpeedMin = MyConstants.ZERO_D;
            mOtherSpeedSec = MyConstants.ZERO_D;
            mOtherTime = MyConstants.ZERO_D;
        } else {
            // Convert Feet to Meter
            mOtherDistance = mMyCalcMetric.convertFeetToMeter(mDefaultDistance);
            mOtherSpeedMin = mMyCalcMetric.convertFeetToMeter(mDefaultSpeedMin);
            mOtherSpeedSec = mOtherSpeedMin / 60.0;

            // Distance/Kick and Kicks are not part of the equation
            mOtherDistancePerKick = MyConstants.ZERO_D;
            mOtherKicks = MyConstants.ZERO_D;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyFunctions.roundUp(mOtherDistancePerKick,1));
        mCalculateSwimmingDistance.setOtherKicks(mOtherKicks);
        mCalculateSwimmingDistance.setOtherSpeedMin(MyFunctions.roundUp(mOtherSpeedMin,1));
        mCalculateSwimmingDistance.setOtherSpeedSec(MyFunctions.roundUp(mOtherSpeedSec,1));
        mCalculateSwimmingDistance.setOtherTime(mDefaultTime);
    }

    private void convertToMetricDistanceKick() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Convert Feet to Meter
        mOtherDistance = mMyCalcMetric.convertFeetToMeter(mDefaultDistance);
        mOtherKicks = mDefaultKicks;

        if (mOtherKicks.equals(MyConstants.ZERO_D)) {
            mOtherDistancePerKick = MyConstants.ZERO_D;
        } else {
            mOtherDistancePerKick = mOtherDistance / mOtherKicks;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherKicks(mDefaultKicks);
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyFunctions.roundUp(mOtherDistancePerKick,1));
    }

    private void convertToMetricKicks() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Convert Feet to Meter
        mOtherDistance = mMyCalcMetric.convertFeetToMeter(mDefaultDistance);
        mOtherSpeedMin = mMyCalcMetric.convertFeetToMeter(mDefaultSpeedMin);
        mOtherSpeedSec = mMyCalcMetric.convertFeetToMeter(mDefaultSpeedSec);
        mOtherDistancePerKick = mMyCalcMetric.convertFeetToMeter(mDefaultDistancePerKick);
        mOtherTime = mDefaultTime;

        // Calculate Kicks
        if (mOtherDistancePerKick.equals(MyConstants.ZERO_D)) {
            mOtherKicks = MyConstants.ZERO_D;
        } else if (!mOtherDistance.equals(MyConstants.ZERO_D)) {
            mOtherKicks = mOtherDistance / mOtherDistancePerKick;
        } else if (!mOtherSpeedMin.equals(MyConstants.ZERO_D) || !mOtherTime.equals(MyConstants.ZERO_D)) {
            mOtherKicks = (mOtherSpeedMin * mOtherTime) / mOtherDistancePerKick;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyFunctions.roundUp(mOtherDistancePerKick,1));
        mCalculateSwimmingDistance.setOtherKicks(MyFunctions.roundUp(mOtherKicks,1));
        mCalculateSwimmingDistance.setOtherSpeedMin(MyFunctions.roundUp(mOtherSpeedMin,1));
        mCalculateSwimmingDistance.setOtherSpeedSec(MyFunctions.roundUp(mOtherSpeedSec,1));
        mCalculateSwimmingDistance.setOtherTime(MyFunctions.roundUp(mOtherTime,1));
    }

    private void convertToMetricSpeed() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultTime = mCalculateSwimmingDistance.getDefaultTime();

        // Convert Feet to Meter
        mOtherDistance = mMyCalcMetric.convertFeetToMeter(mDefaultDistance);
        mOtherDistancePerKick = mMyCalcMetric.convertFeetToMeter(mDefaultDistancePerKick);
        mOtherKicks = mDefaultKicks;
        mOtherTime = mDefaultTime;

        // Calculate Speed
        if (mOtherTime.equals(MyConstants.ZERO_D)) {
            mOtherSpeedMin = MyConstants.ZERO_D;
        } else if (!mOtherDistance.equals(MyConstants.ZERO_D)) {
            mOtherSpeedMin = mOtherDistance / mOtherTime;
        } else if (!mOtherDistancePerKick.equals(MyConstants.ZERO_D)) {
            mOtherSpeedMin = (mOtherDistancePerKick * mOtherKicks) / mOtherTime;
        }

        mOtherSpeedSec = mOtherSpeedMin / 60;

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyFunctions.roundUp(mOtherDistancePerKick,1));
        mCalculateSwimmingDistance.setOtherKicks(MyFunctions.roundUp(mOtherKicks,1));
        mCalculateSwimmingDistance.setOtherSpeedMin(MyFunctions.roundUp(mOtherSpeedMin,1));
        mCalculateSwimmingDistance.setOtherSpeedSec(MyFunctions.roundUp(mOtherSpeedSec,1));
        mCalculateSwimmingDistance.setOtherTime(MyFunctions.roundUp(mOtherTime,1));
    }

    private void convertToMetricTime() {
        // Get all entered values first
        mDefaultDistance = mCalculateSwimmingDistance.getDefaultDistance();
        mDefaultDistancePerKick = mCalculateSwimmingDistance.getDefaultDistancePerKick();
        mDefaultKicks = mCalculateSwimmingDistance.getDefaultKicks();
        mDefaultSpeedMin = mCalculateSwimmingDistance.getDefaultSpeedMin();
        mDefaultSpeedSec = mDefaultSpeedMin / 60;

        // Convert Feet to Meter
        mOtherDistance = mMyCalcMetric.convertFeetToMeter(mDefaultDistance);
        mOtherSpeedMin = mMyCalcMetric.convertFeetToMeter(mDefaultSpeedMin);
        mOtherSpeedSec = mMyCalcMetric.convertFeetToMeter(mDefaultSpeedSec);

        // Calculate Time
        if (mOtherSpeedMin.equals(MyConstants.ZERO_D)) {
            mOtherTime = MyConstants.ZERO_D;
        } else {
            mOtherTime = mOtherDistance / mOtherSpeedMin;
        }

        // Set the calculated values in the POJO
        mCalculateSwimmingDistance.setOtherDistance(MyFunctions.roundUp(mOtherDistance,1));
        mCalculateSwimmingDistance.setOtherSpeedMin(MyFunctions.roundUp(mOtherSpeedMin,1));
        mCalculateSwimmingDistance.setOtherSpeedSec(MyFunctions.roundUp(mOtherSpeedSec,1));
        mCalculateSwimmingDistance.setOtherTime(MyFunctions.roundUp(mOtherTime,1));
    }

    private void clear() {
        // Reset the Default
        mCalculateSwimmingDistance.setDefaultDistance(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultDistancePerKick(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultKicks(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultSpeedMin(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultSpeedSec(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setDefaultTime(MyConstants.ZERO_D);

        mBinding.defaultDistance.setText("0.0");
        mBinding.defaultDistancePerKick.setText("0.0");
        mBinding.defaultKicks.setText("0.0");
        mBinding.defaultSpeedMin.setText("0.0");
        mBinding.defaultSpeedSec.setText("0.0");
        mBinding.defaultTime.setText("0.0");

        // Reset the Other
        mCalculateSwimmingDistance.setOtherDistance(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherDistancePerKick(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherKicks(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherSpeedMin(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherSpeedSec(MyConstants.ZERO_D);
        mCalculateSwimmingDistance.setOtherTime(MyConstants.ZERO_D);

        requestFocus(mBinding.defaultDistance, false);
    }

    private void requestFocus(View view, boolean showKeyboard) {
        if (view instanceof EditText) {
            // Only works for EditText
            view.clearFocus();
            view.requestFocus();
            ((EditText) view).selectAll();
            if (showKeyboard) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            view.clearFocus();
            view.requestFocus();
        }
    }

    public void switchSide() {
        // Save the Other side
        String otherUnit = mBinding.otherUnit.getText().toString();

        String otherDistance = mBinding.otherDistance.getText().toString();
        String otherDistanceLbl = mBinding.otherDistanceLbl.getText().toString();
        String otherDistancePerKick = mBinding.otherDistancePerKick.getText().toString();
        String otherDistancePerKickLbl = mBinding.otherDistancePerKickLbl.getText().toString();
        String otherKicks = mBinding.otherKicks.getText().toString();
        String otherDistancePerMin = mBinding.otherSpeedMin.getText().toString();
        String otherDistancePerMinLbl = mBinding.otherSpeedMinLbl.getText().toString();
        String otherDistancePerSec = mBinding.otherSpeedSec.getText().toString();
        String otherDistancePerSecLbl = mBinding.otherSpeedSecLbl.getText().toString();
        String otherTime = mBinding.otherTime.getText().toString();

        // Switch Default side to Other side
        mBinding.otherUnit.setText(mBinding.defaultUnit.getText().toString());

        mBinding.otherDistance.setText(mBinding.defaultDistance.getText().toString());
        mBinding.otherDistanceLbl.setText(mBinding.defaultDistanceLbl.getText().toString());
        mBinding.otherDistancePerKick.setText(mBinding.defaultDistancePerKick.getText().toString());
        mBinding.otherDistancePerKickLbl.setText(mBinding.defaultDistancePerKickLbl.getText().toString());
        mBinding.otherKicks.setText(mBinding.defaultKicks.getText().toString());
        mBinding.otherSpeedMin.setText(mBinding.defaultSpeedMin.getText().toString());
        mBinding.otherSpeedMinLbl.setText(mBinding.defaultSpeedMinLbl.getText().toString());
        mBinding.otherSpeedSec.setText(mBinding.defaultSpeedSec.getText().toString());
        mBinding.otherSpeedSecLbl.setText(mBinding.defaultSpeedSecLbl.getText().toString());
        mBinding.otherTime.setText(mBinding.defaultTime.getText().toString());

        // Switch Other Side (saved) to Default side
        mBinding.defaultUnit.setText(otherUnit);

        mBinding.defaultDistance.setText(otherDistance);
        mBinding.defaultDistanceLbl.setText(otherDistanceLbl);
        mBinding.defaultDistancePerKick.setText(otherDistancePerKick);
        mBinding.defaultDistancePerKickLbl.setText(otherDistancePerKickLbl);
        mBinding.defaultKicks.setText(otherKicks);
        mBinding.defaultSpeedMin.setText(otherDistancePerMin);
        mBinding.defaultSpeedMinLbl.setText(otherDistancePerMinLbl);
        mBinding.defaultSpeedSec.setText(otherDistancePerSec);
        mBinding.defaultSpeedSecLbl.setText(otherDistancePerSecLbl);
        mBinding.defaultTime.setText(otherTime);

        View view = getCurrentFocus();

        if (view != null && view != mBinding.calculateSwimmingDistanceActivity) {
            requestFocus(view, true);
        }
    }

    private void switchToOtherUnit() {
        // Set the new Default Unit
        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            mDefaultUnit = MyConstants.METRIC;
        } else {
            mDefaultUnit = MyConstants.IMPERIAL;
        }
    }

    private void readDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // If no default unit (Last Used unit) exists e.g. first time ever, use the Phone unit
        mDefaultUnit = (preferences.getString(getString(R.string.code_default_unit), mUnit));

        // The preferred unit is always displayed on the left
        // The layout defaults to Imperial unit on the left
        // Might have to switch if the user last exit the activity on using the other unit

        assert mDefaultUnit != null;
        if (mUnit.equals(MyConstants.IMPERIAL)) {
            // The phone is in IMPERIAL
            // And it is also the App default
            if (!mDefaultUnit.equals(mUnit)) {
                // Switch to METRIC
                switchSide();
            }
        } else {
            if (mDefaultUnit.equals(MyConstants.METRIC)) {
                // The phone is in METRIC
                // Switch to METRIC
                switchSide();
            }
        }
    }

    private void saveDefaultValues() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.code_default_unit),mDefaultUnit);
        edit.apply();
    }
}
