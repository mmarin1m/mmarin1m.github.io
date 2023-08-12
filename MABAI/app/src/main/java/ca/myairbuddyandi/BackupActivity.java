package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import ca.myairbuddyandi.databinding.BackupActivityBinding;

public class BackupActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "BackupActivity";

    // Private
    private boolean isBackupOK= true;
    private String  eMessage;

    // Protected

    // Private

    private final String state = Environment.getExternalStorageState();
    private XmlBuilder xmlBuilder;
    private final AirDA mAirDa = new AirDA(this);
    private final String mExportFileName = MyConstants.AIRDA + " " + MyFunctions.getNow() + ".xml";
    private final Backup mBackup = new Backup();
    private BackupActivityBinding mBinding = null;
    private Uri mUri = null;

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.backup_activity);

        mBackup.mBinding = mBinding;

        mBackup.setContext(this);

        mBinding.setBackup(mBackup);

        // Set the listener
        mBinding.selectButton.setOnClickListener(view -> select());

        mBinding.backupButton.setOnClickListener(view -> backup());

        mBinding.stopButton.setOnClickListener(view -> {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    stop();
                }
            } catch (IOException e) {
                isBackupOK = false;
                eMessage = e.getMessage();
                e.printStackTrace();
            }
        });

        // Public
        boolean isWritable = false;
        boolean isReadable = false;
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            // Operation possible - Read and Write
            isWritable = true;
            isReadable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: Leave as is
        switch (requestCode) {
            case MyConstants.REQ_CODE_EXTERNAL_STORAGE_PERMISSION: {
                // NOTE: Leave as is
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Do here
                } else {
                    mBinding.note.setText(getString(R.string.msg_permission_not_granted));
                }
            }
        }
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_backup));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
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

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    @SuppressLint("WrongConstant")
    ActivityResultLauncher<Intent> createFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    mUri = data.getData();

                    // Persist permissions
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    // Check for the freshest data.
                    getContentResolver().takePersistableUriPermission(mUri, takeFlags);

                    mBinding.backupButton.setEnabled(true);
                    mBinding.backupButton.setAlpha(1.0f);
                    mBinding.stopButton.setEnabled(true);
                    mBinding.stopButton.setAlpha(1.0f);
                }
            });

    private void select() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "XYZ";
        Uri uri = Uri.parse(path);

        openDirectory(uri);
    }

    public void openDirectory(Uri uriToLoad) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/xml");
        intent.putExtra(Intent.EXTRA_TITLE, mExportFileName);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        createFileLauncher.launch(intent);
    }

    private void backup() {
        try {
            exportDb();
        } catch (IOException e) {
            isBackupOK = false;
            eMessage = e.getMessage();
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void stop() throws IOException {

        mBinding.backupButton.setEnabled(false);
        mBinding.backupButton.setAlpha(0.5f);

        // Close the database
        mAirDa.close();

        mBinding.backupButton.setEnabled(true);
        mBinding.backupButton.setAlpha(1.0f);
        mBinding.stopButton.setEnabled(false);
        mBinding.stopButton.setAlpha(0.5f);
    }

    private void exportDb() throws IOException {

        mBinding.selectButton.setEnabled(false);
        mBinding.selectButton.setAlpha(0.5f);
        mBinding.backupButton.setEnabled(false);
        mBinding.backupButton.setAlpha(0.5f);
        mBinding.stopButton.setEnabled(true);
        mBinding.stopButton.setAlpha(1.0f);

       String dbName = MyConstants.AIRDA;
        Log.d(LOG_TAG, "Exporting database - " + dbName + " exportFileName: " + mExportFileName);

        try {
            xmlBuilder = new XmlBuilder();
        } catch (IOException e) {
            isBackupOK = false;
            eMessage = e.getMessage();
            e.printStackTrace();
        }

        xmlBuilder.start(dbName);

        // Export the application settings
        exportSettings();

        mAirDa.open();

        // Export the database version
        exportDbVersion();

        // Export the tables
        // Get all the tables
        try (Cursor c = mAirDa.getDb().rawQuery(  "SELECT name "
                                                    + "FROM sqlite_master "
                                                    + "WHERE type='table'"
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
                                                    + "END ASC "
                                                  , null)) {

            String tableName;
            while (c.moveToNext()) {

                tableName = c.getString(0);

                try {
                    exportTable(tableName);
                } catch (IOException e) {
                    isBackupOK = false;
                    eMessage = e.getMessage();
                    e.printStackTrace();
                }
            }
        }
        String xmlString = null;
        try {
            xmlString = xmlBuilder.end();
        } catch (IOException e) {
            isBackupOK = false;
            eMessage = e.getMessage();
            e.printStackTrace();
        }

        try {
            writeToFile(xmlString);
        } catch (IOException e) {
            isBackupOK = false;
            eMessage = e.getMessage();
            e.printStackTrace();
        }
        mAirDa.close();
        Log.d(LOG_TAG, "Exporting database complete");

        String note;
        if (isBackupOK) {
            note = String.format(getResources().getString(R.string.msg_backup_success), mUri.getPath() + "/" + mExportFileName );
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
    }

    private void exportSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        MyCalc mMyCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            mMyCalc = new MyCalcImperial(this);
        } else {
            mMyCalc = new MyCalcMetric(this);
        }

        // In the same order as in dive_settings.xml
        int descentRate = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DESCENT_RATE, mMyCalc.getDescentRateDefault()))));
        int ascentRateToDs = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ASCENT_RATE_TO_DS, mMyCalc.getAscentRateToDsDefault()))));
        int ascentRateToSs = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ASCENT_RATE_TO_SS, mMyCalc.getAscentRateToSsDefault()))));
        int ascentRateToSu = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ASCENT_RATE_TO_SU, mMyCalc.getAscentRateToSuDefault()))));
        Double bubbleCheckDepth = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.BUBBLE_CHECK_DEPTH, mMyCalc.getBubbleCheckDepthDefault()))));
        int safetyStopDive = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.SAFETY_STOP_DIVE, mMyCalc.getSafetyStopDiveDefault()))));
        Double safetyStopDepth = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.SAFETY_STOP_DEPTH, mMyCalc.getSafetyStopDepthDefault()))));
        safetyStopDepth = mMyCalc.getMinimumSafetyStopDepth(safetyStopDepth);
        int deepStopPercent = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DEEP_STOP_PERCENT, "50"))));
        int deepStopDive = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DEEP_STOP_DIVE, mMyCalc.getDeepStopDiveDefault()))));
        Double end = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.END, getResources().getString(R.string.code_end_unit)))));
        int bubbleCheckTime = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.BUBBLE_CHECK_TIME, "1"))));
        int turnaroundTime = Integer.parseInt(MyFunctions.replaceEmptyByOne(Objects.requireNonNull(preferences.getString(MyConstants.TURNAROUND_TIME, mMyCalc.getTurnaroundTimeDefault()))));
        String subtractDeepStopTime = (preferences.getBoolean(MyConstants.SUBTRACT_DEEP_STOP_TIME,false)) ? MyConstants.YES : MyConstants.NO;
        int deepStopTime = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DEEP_STOP_TIME, "1"))));
        int safetyStopTime = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.SAFETY_STOP_TIME, "3"))));
        int ooaTurnaroundTime = Integer.parseInt(MyFunctions.replaceEmptyByOne(Objects.requireNonNull(preferences.getString(MyConstants.OOA_TURNAROUND_TIME, mMyCalc.getOoaTurnaroundTimeDefault()))));
        int myMinPressure = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.MY_MIN_PRESSURE, mMyCalc.getMyMinPressureDefault()))));
        int rockBottomMinPressure = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.ROCK_BOTTOM_MIN_PRESSURE, mMyCalc.getRockbottomMinPressureDefault()))));
        Double rockBottomSac = Double.parseDouble(Objects.requireNonNull(preferences.getString(MyConstants.ROCK_BOTTOM_SAC, mMyCalc.getRockbottomSacDefault())));
        Double rockBottomRmv = Double.parseDouble(Objects.requireNonNull(preferences.getString(MyConstants.ROCK_BOTTOM_RMV, mMyCalc.getRockbottomRmvDefault())));
        Double heliumMix = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.HELIUM_MIX, "100.0"))));
        Double oxygenMix = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.OXYGEN_MIX, "100.0"))));
        Double topOffMix = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.TOP_OFF_MIX, "20.9"))));
        Double lengthHose = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.LENGTH_HOSE, "100.0"))));
        Double diameterHose = Double.parseDouble(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DIAMETER_HOSE, "20.9"))));

        xmlBuilder.openTable(AirDBHelper.TABLE_SETTING);
        // Rates
        // 01
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.DESCENT_RATE, String.valueOf(descentRate));
        xmlBuilder.closeRow();
        // 02
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.ASCENT_RATE_TO_DS, String.valueOf(ascentRateToDs));
        xmlBuilder.closeRow();
        // 03
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.ASCENT_RATE_TO_SS, String.valueOf(ascentRateToSs));
        xmlBuilder.closeRow();
        // 04
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.ASCENT_RATE_TO_SU, String.valueOf(ascentRateToSu));
        xmlBuilder.closeRow();
        // Depths
        // 05
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.BUBBLE_CHECK_DEPTH, String.valueOf(bubbleCheckDepth));
        xmlBuilder.closeRow();
        // 06
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.SAFETY_STOP_DIVE, String.valueOf(safetyStopDive));
        xmlBuilder.closeRow();
        // 07
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.SAFETY_STOP_DEPTH, String.valueOf(safetyStopDepth));
        xmlBuilder.closeRow();
        // 08
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.DEEP_STOP_PERCENT, String.valueOf(deepStopPercent));
        xmlBuilder.closeRow();
        // 09
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.DEEP_STOP_DIVE, String.valueOf(deepStopDive));
        xmlBuilder.closeRow();
        // 10
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.END, String.valueOf(end));
        xmlBuilder.closeRow();
        // Times
        // 11
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.BUBBLE_CHECK_TIME, String.valueOf(bubbleCheckTime));
        xmlBuilder.closeRow();
        // 12
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.TURNAROUND_TIME, String.valueOf(turnaroundTime));
        xmlBuilder.closeRow();
        // 13
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.SUBTRACT_DEEP_STOP_TIME, subtractDeepStopTime);
        xmlBuilder.closeRow();
        // 14
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.DEEP_STOP_TIME, String.valueOf(deepStopTime));
        xmlBuilder.closeRow();
        // 15
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.SAFETY_STOP_TIME, String.valueOf(safetyStopTime));
        xmlBuilder.closeRow();
        // 16
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.OOA_TURNAROUND_TIME, String.valueOf(ooaTurnaroundTime));
        xmlBuilder.closeRow();
        // Pressures
        // 17
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.MY_MIN_PRESSURE, String.valueOf(myMinPressure));
        xmlBuilder.closeRow();
        // 18
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.ROCK_BOTTOM_MIN_PRESSURE, String.valueOf(rockBottomMinPressure));
        xmlBuilder.closeRow();
        // SACs and RMVs
        // 19
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.ROCK_BOTTOM_SAC, String.valueOf(rockBottomSac));
        xmlBuilder.closeRow();
        // 20
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.ROCK_BOTTOM_RMV, String.valueOf(rockBottomRmv));
        xmlBuilder.closeRow();
        // Blending
        // 21
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.HELIUM_MIX, String.valueOf(heliumMix));
        xmlBuilder.closeRow();
        // 22
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.OXYGEN_MIX, String.valueOf(oxygenMix));
        xmlBuilder.closeRow();
        // 23
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.TOP_OFF_MIX, String.valueOf(topOffMix));
        xmlBuilder.closeRow();
        // 24
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.LENGTH_HOSE, String.valueOf(lengthHose));
        xmlBuilder.closeRow();
        // 25
        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.DIAMETER_HOSE, String.valueOf(diameterHose));
        xmlBuilder.closeRow();

        xmlBuilder.closeTable();
    }

    private void exportDbVersion() {
        xmlBuilder.openTable(MyConstants.DB_VERSION);

        xmlBuilder.openRow();
        xmlBuilder.addColumn(MyConstants.VERSION_NO, String.valueOf(mAirDa.getDb().getVersion()));
        xmlBuilder.closeRow();

        xmlBuilder.closeTable();
    }

    private void exportTable(final String tableName) throws IOException {
        xmlBuilder.openTable(tableName);
        String sql = "select * from " + tableName + " ORDER BY ROWID";
        Cursor c = mAirDa.getDb().rawQuery(sql, new String[0]);
        if (c.moveToFirst()) {
            int cols = c.getColumnCount();
            do {
                xmlBuilder.openRow();
                for (int i = 0; i < cols; i++) {
                    xmlBuilder.addColumn(c.getColumnName(i), c.getString(i));
                }
                xmlBuilder.closeRow();
            } while (c.moveToNext());
        }
        c.close();
        xmlBuilder.closeTable();
    }

    private void writeToFile(final String xmlString)
            throws IOException {
        // Replace special characters
        String normalizedXml = MyFunctions.replaceXmlSpecialChars(xmlString);

        try {
            ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(mUri, "w");
            assert pfd != null;
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write(normalizedXml.getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            isBackupOK = false;
            eMessage = e.getMessage();
            e.printStackTrace();
        }
    }
}
