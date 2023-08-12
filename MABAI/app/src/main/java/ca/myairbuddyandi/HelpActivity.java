package ca.myairbuddyandi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Michel on 2016-11-24.
 * Holds all of the logic for the HelpActivity class
 */

public class HelpActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "HelpActivity";

    // Public

    // Protected

    // Private

    // End of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        setContentView(R.layout.help_activity);

        Bundle extras = getIntent().getExtras();

        String helpTopic = null;

        if (extras != null) {
            helpTopic = extras.getString(getString(R.string.app_help_topic));
        }

        TextView textview = findViewById(R.id.textViewHelp);

        if (helpTopic != null) {
            if (helpTopic.equals(getString(R.string.act_main))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_main)));

            } else if (helpTopic.equals(getString(R.string.act_sac_and_rmv))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_rmv)));

            } else if (helpTopic.equals(getString(R.string.act_diver_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_diver_pick)));
            } else if (helpTopic.equals(getString(R.string.act_diver_extra_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_diver_extra_pick)));

            } else if (helpTopic.equals(getString(R.string.act_diver_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_diver_edit)));

            } else if (helpTopic.equals(getString(R.string.act_dive_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_dive_pick)));
            } else if (helpTopic.equals(getString(R.string.act_dive_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_dive_edit)));

            } else if (helpTopic.equals(getString(R.string.act_consumption_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_diver_dive_group_pick)));
            } else if (helpTopic.equals(getString(R.string.act_consumption_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_diver_dive_group_edit)));

            } else if (helpTopic.equals(getString(R.string.act_dive_plan_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_dive_plan_pick)));
            } else if (helpTopic.equals(getString(R.string.act_dive_plan_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_dive_plan_edit)));

            } else if (helpTopic.equals(getString(R.string.act_groupp_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_groupp_pick)));
            } else if (helpTopic.equals(getString(R.string.act_groupp_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_groupp_edit)));

            } else if (helpTopic.equals(getString(R.string.act_cylinder_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_cylinder_pick)));
            } else if (helpTopic.equals(getString(R.string.act_cylinder_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_cylinder_edit)));

            } else if (helpTopic.equals(getString(R.string.act_cylinder_type_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_cylinder_type_pick)));
            } else if (helpTopic.equals(getString(R.string.act_cylinder_type_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_cylinder_type_edit)));

            } else if (helpTopic.equals(getString(R.string.act_dive_type_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_dive_type_pick)));
            } else if (helpTopic.equals(getString(R.string.act_dive_type_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_dive_type_edit)));

            } else if (helpTopic.equals(getString(R.string.act_groupp_type_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_groupp_type_pick)));
            } else if (helpTopic.equals(getString(R.string.act_groupp_type_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_groupp_type_edit)));

            } else if (helpTopic.equals(getString(R.string.act_segment_type_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_segment_type_pick)));
            } else if (helpTopic.equals(getString(R.string.act_segment_type_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_segment_type_edit)));

            } else if (helpTopic.equals(getString(R.string.act_usage_type_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_usage_type_pick)));
            } else if (helpTopic.equals(getString(R.string.act_usage_type_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_usage_type_edit)));

            } else if (helpTopic.equals(getString(R.string.act_turnaround))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_turnaround)));
            } else if (helpTopic.equals(getString(R.string.act_rockbottom))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_rockbottom)));
            } else if (helpTopic.equals(getString(R.string.act_rule_of_thirds))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_rule_of_third)));
            } else if (helpTopic.equals(getString(R.string.act_emergency))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_emergency)));
            } else if (helpTopic.equals(getString(R.string.act_drift))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_drift)));

            } else if (helpTopic.equals(getString(R.string.act_backup))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_backup)));
            } else if (helpTopic.equals(getString(R.string.act_restore))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_restore)));

            } else if (helpTopic.equals(getString(R.string.act_calculate_altitude))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_altitude)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_boyle_law))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_boyle_law)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_charles_law))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_charles_law)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_dalton_law))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_dalton_law)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_buoyancy))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_buoyancy)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_current))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_current)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_current_deviation))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_current_deviation)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_cylinder))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_cylinder)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_descent_ascent))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_descent_ascent)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_ead))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_ead)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_end))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_end)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_gas))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_gas)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_nitrox))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_nitrox)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_pressure))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_pressure)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_rmv))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_rmv)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_swimming_distance))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_swimming_distance)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_trimix))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_trimix)));
            } else if (helpTopic.equals(getString(R.string.act_calculate_unit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_calculate_unit)));

            } else if (helpTopic.equals(getString(R.string.act_acronyms))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_acronyms)));
            } else if (helpTopic.equals(getString(R.string.act_constants))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_constants)));


            } else if (helpTopic.equals(getString(R.string.act_computer_edit))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_computer_edit)));
            } else if (helpTopic.equals(getString(R.string.act_computer_dive))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_computer_dive)));
            } else if (helpTopic.equals(getString(R.string.act_computer_dive_troubleshooting))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_computer_dive_troubleshooting)));
            } else if (helpTopic.equals(getString(R.string.act_computer_dives_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_computer_dives_pick)));
            } else if (helpTopic.equals(getString(R.string.act_computer_dive_download_progress_bar))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_computer_dive_download_progress_bar)));
            } else if (helpTopic.equals(getString(R.string.act_computer_pick))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_computer_pick)));
            } else if (helpTopic.equals(getString(R.string.act_libdivecomputer_pick_scan))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_libdivecomputer_pick_scan)));
            } else if (helpTopic.equals(getString(R.string.act_libdivecomputer_pick_scan_troubleshooting))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_libdivecomputer_pick_scan_troubleshooting)));

            } else if (helpTopic.equals(getString(R.string.act_compare_turnaround_dives))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_compare_dives)));
            } else if (helpTopic.equals(getString(R.string.act_compare_rule_of_thirds_dives))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_compare_dives)));
            } else if (helpTopic.equals(getString(R.string.act_compare_drift_dives))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_compare_dives)));
            } else if (helpTopic.equals(getString(R.string.act_compare_rockbottom_dives))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_compare_dives)));
            } else if (helpTopic.equals(getString(R.string.act_compare_emergency_dives))) {
                textview.setText(MyFunctions.fromHtml(getString(R.string.help_compare_dives)));

            }  else {
                textview.setText(R.string.code_unknown_case);
            }
        }
        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if(id==android.R.id.home)
        {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
