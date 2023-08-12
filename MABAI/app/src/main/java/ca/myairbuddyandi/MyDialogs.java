package ca.myairbuddyandi;

import android.app.Activity;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;


/**
 * Created by Michel on 2017-01-08.
 * Holds all of the logic for the MyDialogs class
 */

public class MyDialogs {

    // Static
    private static final String LOG_TAG = "MyDialogs";

    // Public

    // Protected

    // Private
    private Runnable answerTrue = null;
    private Runnable answerFalse = null;

    // End of variables

    // Public constructor
    public MyDialogs() {
    }

    public void confirm(Activity act, String Title, String ConfirmText, String yesBtn, String noBtn, Runnable yesProc, Runnable noProc) {

        answerTrue = yesProc;
        answerFalse= noProc;

        final AlertDialog alertDialog = new AlertDialog.Builder(act)
                .setTitle(Title)
                .setMessage(ConfirmText)
                .setCancelable(false)
                .setPositiveButton(yesBtn, null)
                .setNegativeButton(noBtn, null)
                .create();

        alertDialog.setOnShowListener(dialog -> {

            Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            // TODO: Must do more testing on all Activities
            // Closing the dialog no matter what!
            btnPositive.setOnClickListener(view -> {
                answerTrue.run();
                alertDialog.dismiss();
            });

            Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            btnNegative.setFocusable(true);
            btnNegative.setFocusableInTouchMode(true);
            btnNegative.requestFocus();

            btnNegative.setOnClickListener(view -> {
                answerFalse.run();
                alertDialog.dismiss();
            });
        });

        alertDialog.show();
    }
}
