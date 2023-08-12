package ca.myairbuddyandi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by Michel on 2027-07-17.
 * Holds all of the logic for the ComputerDiveDownloadProgressBarActivity class
 *
 * ??
 *
 * Main POJO:   ??
 * Passes:      ??
 * Receives:    ??
 * Passes back: ??
 */

public class ComputerDiveDownloadProgressBarActivity extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "ComputerDiveDownloadProgressBarActivity";

    // Public

    // Protected

    // Private
    // Number of dives selected to download
    private int mDivesToDownload;
    private int mI;
    private ArrayList<ComputerDives> mComputerDivesPickList;
    private Button mButtonCancel;
    private Button mButtonClose;
    private Button mButtonStart;
    private ProgressBar mProgressBar;
    private TextView mHelp;
    private TextView mTextViewCount;
    private TextView mTextViewDiveNos;
    private TextView mTextViewInfo;
    private final Handler handler = new Handler();

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        setContentView(R.layout.computer_dive_download_progress_bar);

        // Get the data from the Intent
        ComputerDivesPick mComputerDivesPick;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mComputerDivesPick = getIntent().getParcelableExtra(MyConstants.COMPUTER_DIVES_PICK,ComputerDivesPick.class);
        } else {
            mComputerDivesPick = getIntent().getParcelableExtra(MyConstants.COMPUTER_DIVES_PICK);
        }

        assert mComputerDivesPick != null;
        mComputerDivesPickList = mComputerDivesPick.getComputerDivesPickList();

        mProgressBar = this.findViewById(R.id.progressBar);
        mHelp = this.findViewById(R.id.textHelp);
        mTextViewInfo = this.findViewById(R.id.textViewInfo);
        mTextViewCount = this.findViewById(R.id.textViewCount);
        mTextViewDiveNos = this.findViewById(R.id.textViewDiveNos);
        mButtonStart = this.findViewById(R.id.button_start);
        mButtonClose = this.findViewById(R.id.button_close);
        mButtonCancel = this.findViewById(R.id.button_cancel);

        countComputerDives();

        if (mDivesToDownload > 0) {
            mButtonStart.setEnabled(true);
            mButtonStart.setAlpha(1.0f);
        }

        mProgressBar.setIndeterminate(false);

        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help();
            }
        });

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStartProgressBar();
            }
        });

        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        Log.d(LOG_TAG, "onCreate done");
    }

    // My functions

    private void cancel() {

    }

    private void close() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void countComputerDives() {
        for (int mI = 0; mI < mComputerDivesPickList.size(); mI++) {
            ComputerDives computerDives = mComputerDivesPickList.get(mI);
            if (computerDives.getChecked()) {
                mDivesToDownload++;
            }
        }
    }

    private void doStartProgressBar()  {
        mProgressBar.setMax(mDivesToDownload);
        mButtonClose.setEnabled(false);
        mButtonClose.setAlpha(0.5f);
        mButtonCancel.setEnabled(true);
        mButtonCancel.setAlpha(1.0f);

        Thread thread = new Thread(new Runnable()  {

            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        mButtonStart.setEnabled(false);
                    }
                });
                for( mI =0; mI < mDivesToDownload; mI++) {
                    ComputerDives computerDives = mComputerDivesPickList.get(mI);
                    if (computerDives.getChecked()) {
                        final int progress = mI + 1;
                        // Do something (Download, Upload, Update database,..)
                        SystemClock.sleep(1000); // Sleep 1 second

                        // Update interface.
                        handler.post(new Runnable() {
                            public void run() {
                                mProgressBar.setProgress(progress);
                                int percent = (progress * 100) / mDivesToDownload;

                                mTextViewInfo.setText(String.format(getResources().getString(R.string.msg_download_dives_percent),percent));
                                mTextViewCount.setText(String.format(getResources().getString(R.string.msg_download_dives_count),mI,mDivesToDownload));
                                mTextViewDiveNos.setText(String.format(getResources().getString(R.string.msg_download_dives_nos),computerDives.getDiveNo(),mDivesToDownload));
                                if (progress == mDivesToDownload) {
                                    mTextViewInfo.setText(String.format(getResources().getString(R.string.msg_download_completed),mDivesToDownload));
                                    mButtonStart.setEnabled(false);
                                    mButtonStart.setAlpha(0.5f);
                                    mButtonClose.setEnabled(true);
                                    mButtonClose.setAlpha(1.0f);
                                    mButtonCancel.setEnabled(false);
                                    mButtonCancel.setAlpha(0.5f);
                                }
                            }
                        });
                    }
                }
            }
        });
        thread.start();
    }

    private void help() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_computer_dive_download_progress_bar));
        startActivity(intent);
    }
}
