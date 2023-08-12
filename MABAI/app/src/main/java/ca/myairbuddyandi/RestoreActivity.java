package ca.myairbuddyandi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import ca.myairbuddyandi.databinding.RestoreActivityBinding;

public class RestoreActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "RestoreActivity";

    // Public

    // Protected

    private boolean isRestoreOK= true;
    private final AirDA mAirDa = new AirDA(this);
    private final MyDialogs mDialogs = new MyDialogs();
    private final Restore mRestore = new Restore();
    private RestoreActivityBinding mBinding = null;
    private String  eMessage;
    private final String mState = Environment.getExternalStorageState();
    // NOTE: Used for debugging and analysis purposes
    private String mXmlData;
    private Uri mUri = null;
    private XmlPullParserHandler mParser = new XmlPullParserHandler();

    // End of variables

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.restore_activity);

        mRestore.mBinding = mBinding;

        mRestore.setContext(this);

        mBinding.setRestore(mRestore);

        // Set the listener
        mBinding.selectButton.setOnClickListener(view -> select());

        mBinding.restoreButton.setOnClickListener(view -> {
            try {
                mDialogs.confirm(RestoreActivity.this, getString(R.string.dlg_confirm_restore), getString(R.string.dlg_restore), getString(R.string.dlg_positive), getString(R.string.dlg_negative), yesProc(), noProc());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        mBinding.stopButton.setOnClickListener(view -> {
            try {
                stop();
            } catch (IOException e) {
                isRestoreOK = false;
                eMessage = e.getMessage();
                e.printStackTrace();
            }
        });

        // Private
        boolean isWritable = false;
        boolean isReadable = false;
        if (Environment.MEDIA_MOUNTED.equals(mState)) {
            // Operation possible - Read and Write
            isWritable = true;
            isReadable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(mState)) {
            // Operation possible - Read Only
            isWritable = false;
            isReadable = true;
        } else {
            // SD card not available
            isWritable = false;
            isReadable = false;
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
            intent.putExtra(getString(R.string.app_help_topic), getString(R.string.act_restore));
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            // Action Bar Up button
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
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

    // My functions

    static String getFileNameByUri(final Context context, Uri uri, String defaultName) {
        String fileName = defaultName;
        Uri filePathUri = uri;
        try {//from  w  w w.java  2s .co  m
            if (Objects.requireNonNull(uri.getScheme()).compareTo("content") == 0) {
                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    assert cursor != null;
                    if (cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                        filePathUri = Uri.parse(cursor.getString(column_index));
                        fileName = filePathUri.getLastPathSegment();
                    }
                }
            } else if (uri.getScheme().compareTo("file") == 0) {
                fileName = filePathUri.getLastPathSegment();
            } else {
                fileName = fileName + "_" + filePathUri.getLastPathSegment();
            }
        } catch (Exception e) {
            //do nothing, only return default file name;
            Log.d(LOG_TAG, Objects.requireNonNull(e.getLocalizedMessage()));
        }
        return fileName;
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> pickFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the file's content URI from the incoming Intent
                    mUri = data.getData();

                    String note;
                    if (isRestoreOK) {
                        note = String.format(getResources().getString(R.string.msg_restore_file_selected), mUri.getPath() + "/" + getFileNameByUri(getApplicationContext(), mUri, getResources().getString(R.string.msg_unknown)));
                        // eMessage should be blank because all processes terminated normally
                        eMessage = " ";
                        mBinding.restoreButton.setEnabled(true);
                        mBinding.restoreButton.setAlpha(1.0f);
                    } else {
                        note = getResources().getString(R.string.msg_process_failed);
                        mBinding.lblError.setVisibility(View.VISIBLE);
                        // eMessage should have been set in the catch exception
                        if (eMessage.isEmpty()) {
                            eMessage = getResources().getString(R.string.msg_unknown);
                        }
                    }
                    mBinding.note.setText(note);
                    mBinding.error.setText(eMessage);
                }
            });

    private void select() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, getResources().getString(R.string.dlg_choose_file));
        pickFileLauncher.launch(chooseFile);
    }

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

    private void restore() throws IOException {
        mBinding.selectButton.setEnabled(false);
        mBinding.selectButton.setAlpha(0.5f);
        mBinding.restoreButton.setEnabled(false);
        mBinding.restoreButton.setAlpha(0.5f);
        mBinding.stopButton.setEnabled(true);
        mBinding.stopButton.setAlpha(1.0f);

        mXmlData = readTextFromUri(mUri);

        // Perform the XML restore

        // Load the tables
        // Loading order is:
        // 1	STATE
        // 2	DIVER
        // 3	CYLINDER_TYPE
        // 4	DIVE_TYPE
        // 5	DYNAMIC_SPINNER
        // 6	GROUP_TYPE
        // 7	SEGMENT_TYPE
        // 8	USAGE_TYPE
        // 9	CYLINDER
        //10	DIVE
        //11	DIVER_DIVE
        //12	DIVE_SEGMENT
        //13	DIVER_DIVE_GROUP
        //14	DIVER_DIVE_GROUP_CYLINDER
        //15	GROUPP
        //16	GROUP_CYLINDER
        //17	DIVE_PLAN

        mAirDa.open();
        mAirDa.beginTransaction();

        if (getDatabaseName()) {
            if (restoreSettings()) {
                if (getDatabaseVersion()) {
                    if (deleteAllTables()) {
                        if (restoreState()) {
                            if (restoreDiver()) {
                                if (restoreCylinderType()) {
                                    if (restoreDiveType()) {
                                        if (restoreDynamicSpinner()) {
                                            if (restoreGroupType()) {
                                                if (restoreSegmentType()) {
                                                    if (restoreUsageType()) {
                                                        if (restoreCylinder()) {
                                                            if (restoreDive()) {
                                                                if (restoreDiverDive()) {
                                                                    if (restoreDiveSegment()) {
                                                                        if (restoreDiverDiveGroup()) {
                                                                            if (restoreDiverDiveGroupCylinder()) {
                                                                                if (restoreGroupp()) {
                                                                                    if (restoreGroupCylinder()) {
                                                                                        if (restoreDivePlan()) {
                                                                                            // The restore has been fully executed
                                                                                            // COMMIT
                                                                                            try {
                                                                                                mAirDa.setTransactionSuccessful();
                                                                                            } finally {
                                                                                                // No transaction left behind
                                                                                                mAirDa.endTransaction();
                                                                                            }
                                                                                            mAirDa.close();

                                                                                            String note;
                                                                                            if (isRestoreOK) {
                                                                                                note = getString(R.string.msg_restore_success);
                                                                                                // eMessage should be blank because all processes terminated normally
                                                                                                eMessage = " ";
                                                                                            } else {
                                                                                                note = getResources().getString(R.string.msg_process_failed);
                                                                                                mBinding.lblError.setVisibility(View.VISIBLE);
                                                                                                // eMessage should have been set in the catch exception
                                                                                                if (eMessage.isEmpty()) {
                                                                                                    eMessage = getResources().getString(R.string.msg_unknown);
                                                                                                }
                                                                                            }

                                                                                            mBinding.note.setText(note);
                                                                                            mBinding.error.setText(eMessage);

                                                                                            mBinding.selectButton.setEnabled(true);
                                                                                            mBinding.selectButton.setAlpha(1.0f);
                                                                                            mBinding.restoreButton.setEnabled(false);
                                                                                            mBinding.restoreButton.setAlpha(0.5f);
                                                                                            mBinding.stopButton.setEnabled(false);
                                                                                            mBinding.stopButton.setAlpha(0.5f);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean getDatabaseName() throws FileNotFoundException {
        InputStream fis = getContentResolver().openInputStream(mUri);
        // Get the database name from the backup file
        String databaseName = mParser.parseDatabaseName(fis);

        if (databaseName == null) {
            showErrorAndFinish(getResources().getString(R.string.dlg_invalid_backup_file), getResources().getString(R.string.msg_invalid_backup_file));
            return false;
        } else  if(!databaseName.equalsIgnoreCase(MyConstants.AIRDA)) {
        // If it is airDa then we have a possible good backup
            showErrorAndFinish(getResources().getString(R.string.dlg_invalid_backup_file), getResources().getString(R.string.msg_invalid_backup_file));
            return false;
        } else {
            return true;
        }
    }

    private boolean restoreSettings() throws FileNotFoundException {
        List<Setting> settings;
        // Get the settings from the backup file
        InputStream fis = getContentResolver().openInputStream(mUri);
        settings = mParser.parseSettings(fis);

        if (settings == null || settings.size() == 0) {
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_SETTING));
            return false;
        }
        // Loop through the list and update all the settings one by one
        String columnName;
        String columnValue;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();

        for (int i = 0; i < settings.size(); i++) {
            Setting setting = settings.get(i);
            columnName = setting.getColumnName();
            columnValue = setting.getColumnValue();

            // It's OK if the backup file does not have an entry for any of the settings
            // The app always reads the setting with a default value

            // All of the Integer and Double (Float) settings from the Application Setting Editor are stored in String
            // Except for subtractDeepStopTime who it is stored as a Boolean

            if (MyConstants.DESCENT_RATE.equals(columnName)
                    || MyConstants.ASCENT_RATE_TO_DS.equals(columnName)
                    || MyConstants.ASCENT_RATE_TO_SS.equals(columnName)
                    || MyConstants.ASCENT_RATE_TO_SU.equals(columnName)
                    || MyConstants.BUBBLE_CHECK_DEPTH.equals(columnName)
                    || MyConstants.SAFETY_STOP_DIVE.equals(columnName)
                    || MyConstants.SAFETY_STOP_DEPTH.equals(columnName)
                    || MyConstants.DEEP_STOP_PERCENT.equals(columnName)
                    || MyConstants.DEEP_STOP_DIVE.equals(columnName)
                    || MyConstants.END.equals(columnName)
                    || MyConstants.BUBBLE_CHECK_TIME.equals(columnName)
                    || MyConstants.TURNAROUND_TIME.equals(columnName)
                    || MyConstants.DEEP_STOP_TIME.equals(columnName)
                    || MyConstants.SAFETY_STOP_TIME.equals(columnName)
                    || MyConstants.OOA_TURNAROUND_TIME.equals(columnName)
                    || MyConstants.MY_MIN_PRESSURE.equals(columnName)
                    || MyConstants.ROCK_BOTTOM_MIN_PRESSURE.equals(columnName)
                    || MyConstants.ROCK_BOTTOM_SAC.equals(columnName)
                    || MyConstants.ROCK_BOTTOM_RMV.equals(columnName)
                    || MyConstants.HELIUM_MIX.equals(columnName)
                    || MyConstants.OXYGEN_MIX.equals(columnName)
                    || MyConstants.TOP_OFF_MIX.equals(columnName)
                    || MyConstants.LENGTH_HOSE.equals(columnName)
                    || MyConstants.DIAMETER_HOSE.equals(columnName)) {
                editor.putString(columnName, columnValue);
            } else if (MyConstants.SUBTRACT_DEEP_STOP_TIME.equals(columnName)) {
                editor.putBoolean(columnName, (columnValue.equals(MyConstants.YES)));
            }

            editor.apply();

            Log.d(LOG_TAG, "Setting: " + columnName + " value: " + columnValue + " has been created.");
        }

        return true;
    }

    private boolean getDatabaseVersion() throws FileNotFoundException {
        // Get the DB Version from the backup file
        List<DbVersion> dbVersions;
        InputStream fis = getContentResolver().openInputStream(mUri);
        dbVersions = mParser.parseDbVersions(fis);
        // NOTE: Reserved for future use
        DbVersion dbVersion = dbVersions.get(0);
        return true;
    }

    private boolean deleteAllTables() {
        // Delete all the database tables
        // In reverse order of the load sequence

        // Get all the tables
        try (Cursor c = mAirDa.getDb().rawQuery("SELECT name "
                        + "FROM sqlite_master "
                        + "WHERE type='table' "
                        + "AND   UPPER(name) != 'ANDROID_METADATA' "
                        + "AND   UPPER(name) != 'SQLITE_SEQUENCE' "
                        + "AND   UPPER(name) NOT LIKE ('UIDX%') "
                        + "ORDER BY CASE WHEN UPPER(name) = 'STATE' THEN 10 "
                        + "WHEN UPPER(name) = 'DIVER' THEN 20 "
                        + "WHEN UPPER(name) = 'CYLINDER_TYPE' THEN 30 "
                        + "WHEN UPPER(name) = 'DIVE_TYPE' THEN 40 "
                        + "WHEN UPPER(name) = 'DYNAMIC_SPINNER' THEN 50 "
                        + "WHEN UPPER(name) = 'GROUP_TYPE' THEN 60 "
                        + "WHEN UPPER(name) = 'SEGMENT_TYPE' THEN 70 "
                        + "WHEN UPPER(name) = 'USAGE_TYPE' THEN 80 "
                        + "WHEN UPPER(name) = 'CYLINDER' THEN 90 "
                        + "WHEN UPPER(name) = 'DIVE' THEN 100 "
                        + "WHEN UPPER(name) = 'DIVER_DIVE' THEN 110 "
                        + "WHEN UPPER(name) = 'DIVE_SEGMENT' THEN 120 "
                        + "WHEN UPPER(name) = 'DIVER_DIVE_GROUP' THEN 130 "
                        + "WHEN UPPER(name) = 'DIVER_DIVE_GROUP_CYLINDER' THEN 140 "
                        + "WHEN UPPER(name) = 'GROUPP' THEN 150 "
                        + "WHEN UPPER(name) = 'GROUP_CYLINDER' THEN 160 "
                        + "WHEN UPPER(name) = 'DIVE_PLAN' THEN 170 "
                        + "ELSE 180 "
                        + "END DESC "
                , null)) {

            if (c.moveToFirst()) {

                String tableName;
                while (c.moveToNext()) {
                    tableName = c.getString(0);
                    deleteTable(tableName);
                }
            }
        }
        return true;
    }

    private boolean restoreState() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 1- Get the table STATE from the backup file
        List<State> states;
        InputStream fis = getContentResolver().openInputStream(mUri);
        states = mParser.parseStates(fis);
        if (states != null && states.size() > 0) {
            for (int i = 0; i < states.size(); i++) {
                State state = states.get(i);
                mAirDa.createState(state);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_STATE));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_STATE));
            return false;
        }
        return true;
    }

    private boolean restoreDiver() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 2- Get the table DIVER from the backup file
        // With autoincrement
        List<Diver> divers;
        InputStream fis = getContentResolver().openInputStream(mUri);
        divers = mParser.parseDivers(fis);
        if (divers != null && divers.size() > 0) {
            for (int i = 0; i < divers.size(); i++) {
                Diver diver = divers.get(i);
                mAirDa.createDiver(diver, true);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVER));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVER));
            return false;
        }
        return true;
    }

    private boolean restoreCylinderType() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 3- Get the table CYLINDER_TYPE from the backup file
        List<CylinderType> cylinderTypes;
        InputStream fis = getContentResolver().openInputStream(mUri);
        cylinderTypes = mParser.parseCylinderTypes(fis);
        if (cylinderTypes != null && cylinderTypes.size() > 0) {
            for (int i = 0; i < cylinderTypes.size(); i++) {
                CylinderType cylinderType = cylinderTypes.get(i);
                mAirDa.createCylinderType(cylinderType);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_CYLINDER_TYPE));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_CYLINDER_TYPE));
            return false;
        }
        return true;
    }

    private boolean restoreDiveType() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 4- Get the table DIVE_TYPE from the backup file
        List<DiveType> diveTypes;
        InputStream fis = getContentResolver().openInputStream(mUri);
        diveTypes = mParser.parseDiveTypes(fis);
        if (diveTypes != null && diveTypes.size() > 0) {
            for (int i = 0; i < diveTypes.size(); i++) {
                DiveType diveType = diveTypes.get(i);
                mAirDa.createDiveType(diveType);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVE_TYPE));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVE_TYPE));
            return false;
        }
        return true;
    }

    private boolean restoreDynamicSpinner() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 5- Get the table DYNAMIC_SPINNER from the backup file
        List<DynamicSpinner> dynamicSpinners;
        InputStream fis = getContentResolver().openInputStream(mUri);
        dynamicSpinners = mParser.parseDynamicSpinners(fis);
        if (dynamicSpinners != null && dynamicSpinners.size() > 0) {
            for (int i = 0; i < dynamicSpinners.size(); i++) {
                DynamicSpinner dynamicSpinner = dynamicSpinners.get(i);
                mAirDa.createDynamicSpinner(dynamicSpinner);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DYNAMIC_SPINNER));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DYNAMIC_SPINNER));
            return false;
        }
        return true;
    }

    private boolean restoreGroupType() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 6- Get the table GROUP_TYPE from the backup file
        List<GrouppType> grouppTypes;
        InputStream fis = getContentResolver().openInputStream(mUri);
        grouppTypes = mParser.parseGrouppTypes(fis);
        if (grouppTypes != null && grouppTypes.size() > 0) {
            for (int i = 0; i < grouppTypes.size(); i++) {
                GrouppType grouppType = grouppTypes.get(i);
                mAirDa.createGroupType(grouppType);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_GROUP_TYPE));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_GROUP_TYPE));
            return false;
        }
        return true;
    }

    private boolean restoreSegmentType() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 7- Get the table SEGMENT_TYPE from the backup file
        List<SegmentType> segmentTypes;
        InputStream fis = getContentResolver().openInputStream(mUri);
        segmentTypes = mParser.parseSegmentTypes(fis);
        if (segmentTypes != null && segmentTypes.size() > 0) {
            for (int i = 0; i < segmentTypes.size(); i++) {
                SegmentType segmentType = segmentTypes.get(i);
                mAirDa.createSegmentType(segmentType);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_SEGMENT_TYPE));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_SEGMENT_TYPE));
            return false;
        }
        return true;
    }

    private boolean restoreUsageType() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 8- Get the table USAGE_TYPE from the backup file
        List<UsageType> usageTypes;
        InputStream fis = getContentResolver().openInputStream(mUri);
        usageTypes = mParser.parseUsageTypes(fis);
        if (usageTypes != null && usageTypes.size() > 0) {
            for (int i = 0; i < usageTypes.size(); i++) {
                UsageType usageType = usageTypes.get(i);
                mAirDa.createUsageType(usageType);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_USAGE_TYPE));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_USAGE_TYPE));
            return false;
        }
        return true;
    }

    private boolean restoreCylinder() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 9- Get the table CYLINDER from the backup file
        // With autoincrement
        List<Cylinder> cylinders;
        InputStream fis = getContentResolver().openInputStream(mUri);
        cylinders =mParser.parseCylinders(fis);
        if (cylinders != null && cylinders.size() > 0) {
            for (int i = 0; i < cylinders.size(); i++) {
                Cylinder cylinder = cylinders.get(i);
                mAirDa.createCylinder(cylinder, true);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_CYLINDER));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_CYLINDER));
            return false;
        }
        return true;
    }

    private boolean restoreDive() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 10- Get the table DIVE from the backup file
        // With autoincrement
        List<Dive> dives;
        InputStream fis = getContentResolver().openInputStream(mUri);
        dives = mParser.parseDives(fis);
        if (dives != null && dives.size() > 0) {
            for (int i = 0; i < dives.size(); i++) {
                Dive dive = dives.get(i);
                mAirDa.createDive(dive,true);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVE));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVE));
            return false;
        }
        return true;
    }

    private boolean restoreDiverDive() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 11- Get the table DIVER_DIVE from the backup file
        List<DiverDive> diverDives;
        InputStream fis = getContentResolver().openInputStream(mUri);
        diverDives = mParser.parseDiverDives(fis);
        if (diverDives != null && diverDives.size() > 0) {
            for (int i = 0; i < diverDives.size(); i++) {
                DiverDive diverDive = diverDives.get(i);
                mAirDa.createDiverDive(diverDive);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVER_DIVE));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVER_DIVE));
            return false;
        }
        return true;
    }

    private boolean restoreDiveSegment() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 12- Get the table DIVE_SEGMENT from the backup file
        List<DiveSegment> diveSegments;
        InputStream fis = getContentResolver().openInputStream(mUri);
        diveSegments = mParser.parseDiveSegments(fis);
        // Dive Segment might be empty
        if (diveSegments != null) {
            for (int i = 0; i < diveSegments.size(); i++) {
                DiveSegment diveSegment = diveSegments.get(i);
                mAirDa.createDiveSegment(diveSegment);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVE_SEGMENT));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVE_SEGMENT));
            return false;
        }
        return true;
    }

    private boolean restoreDiverDiveGroup() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 13- Get the table DIVER_DIVE_GROUP from the backup file
        List<DiverDiveGroup> diverDiveGroups;
        InputStream fis = getContentResolver().openInputStream(mUri);
        diverDiveGroups = mParser.parseDiverDiveGroups(fis);
        if (diverDiveGroups != null && diverDiveGroups.size() > 0) {
            for (int i = 0; i < diverDiveGroups.size(); i++) {
                DiverDiveGroup diverDiveGroup = diverDiveGroups.get(i);
                mAirDa.createDiverDiveGroup(diverDiveGroup);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVER_DIVE_GROUP));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVER_DIVE_GROUP));
            return false;
        }
        return true;
    }

    private boolean restoreDiverDiveGroupCylinder() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 14- Get the table DIVER_DIVE_GROUP_CYLINDER from the backup file
        List<DiverDiveGroupCyl> diverDiveGroupCyls;
        InputStream fis = getContentResolver().openInputStream(mUri);
        diverDiveGroupCyls = mParser.parseDiverDiveGroupCyls(fis);
        // Diver Dive Group Cylinder might be empty
        if (diverDiveGroupCyls != null) {
            for (int i = 0; i < diverDiveGroupCyls.size(); i++) {
                DiverDiveGroupCyl diverDiveGroupCyl = diverDiveGroupCyls.get(i);
                mAirDa.createDiverDiveGroupCylinder(diverDiveGroupCyl);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER));
            return false;
        }
        return true;
    }

    private boolean restoreGroupp() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 15- Get the table GROUPP from the backup file
        // With autoincrement
        List<Groupp> groupps;
        InputStream fis = getContentResolver().openInputStream(mUri);
        groupps = mParser.parseGroupps(fis);
        if (groupps != null && groupps.size() > 0) {
            for (int i = 0; i < groupps.size(); i++) {
                Groupp groupp = groupps.get(i);
                mAirDa.createGroupp(groupp, true);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_GROUPP));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_GROUPP));
            return false;
        }
        return true;
    }

    private boolean restoreGroupCylinder() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 16- Get the table GROUP_CYLINDER from the backup file
        List<GrouppCylinder> grouppCylinders;
        InputStream fis = getContentResolver().openInputStream(mUri);
        grouppCylinders = mParser.parseGrouppCylinders(fis);
        if (grouppCylinders != null && grouppCylinders.size() > 0) {
            for (int i = 0; i < grouppCylinders.size(); i++) {
                GrouppCylinder grouppCylinder = grouppCylinders.get(i);
                mAirDa.createGroupCylinder(grouppCylinder);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_GROUP_CYLINDER));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_GROUP_CYLINDER));
            return false;
        }
        return true;
    }

    private boolean restoreDivePlan() throws FileNotFoundException {
        // TODO: In a future release, check the DB Version to restore to the right DB Version e.g. 2, 3, 4, or 5
        // 17- Get the table DIVE_PLAN from the backup file
        // With autoincrement
        List<DivePlan> divePlans;
        InputStream fis = getContentResolver().openInputStream(mUri);
        divePlans = mParser.parseDivePlans(fis);
        if (divePlans != null && divePlans.size() > 0) {
            for (int i = 0; i < divePlans.size(); i++) {
                DivePlan divePlan = divePlans.get(i);
                mAirDa.createDivePlan(divePlan, true);
                if (!mAirDa.getSuccess()) {
                    mAirDa.endTransaction();
                    mAirDa.close();
                    showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVE_PLAN));
                    return false;
                }
            }
        } else {
            mAirDa.endTransaction();
            mAirDa.close();
            showErrorAndFinish(getResources().getString(R.string.dlg_restore_failed), String.format(getResources().getString(R.string.msg_restore_failed), AirDBHelper.TABLE_DIVE_PLAN));
            return false;
        }
        return true;
    }

    private void deleteTable(String tableName) {
        try {
            mAirDa.getDb().execSQL("DELETE FROM " + tableName);
        } catch (SQLException e) {
            isRestoreOK = false;
            eMessage = e.getMessage();
            throw new RuntimeException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void stop() throws IOException {
        mBinding.selectButton.setEnabled(false);
        mBinding.selectButton.setAlpha(0.5f);
        mBinding.restoreButton.setEnabled(false);
        mBinding.restoreButton.setAlpha(0.5f);

        // Close the file
        // Not needed

        // Rollback
        mAirDa.endTransaction();

        // Close the database
        mAirDa.close();

        mBinding.selectButton.setEnabled(true);
        mBinding.selectButton.setAlpha(1.0f);
        mBinding.restoreButton.setEnabled(false);
        mBinding.restoreButton.setAlpha(0.5f);
        mBinding.stopButton.setEnabled(false);
        mBinding.stopButton.setAlpha(0.5f);
    }

    public Runnable yesProc() throws IOException {
        return () -> {
            try {
                restore();
            } catch (IOException e) {
                isRestoreOK = false;
                eMessage = e.getMessage();
                e.printStackTrace();
            }
        };
    }

    public Runnable noProc(){
        return () -> {
            //Do nothing
            //Do not perform the restore
        };
    }

    private void showErrorAndFinish(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setIcon(R.drawable.ic_alert)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> {
                    //dialog.dismiss();
                    finish();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}