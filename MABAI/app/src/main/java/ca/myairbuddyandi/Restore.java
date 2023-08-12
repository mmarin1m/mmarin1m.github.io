package ca.myairbuddyandi;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.RestoreActivityBinding;

/**
 * Created by Michel on 2022-02-25.
 * Holds all of the logic for the CalculateAltitude class
 */

public class Restore extends BaseObservable {

    // Static
    private static final String LOG_TAG = "Restore";

    // Public
    public RestoreActivityBinding mBinding = null;

    // Protected

    // Private
    private String mNote = " ";
    private String mError = " ";
    // NOTE: Leave as is
    private String mHowToRestore = " ";
    private Context mContext;

    // End of variables

    // Public constructor
    public Restore() {
    }

    // Getters and setters

    public void setContext(Context context) {
        mContext = context;
    }

    //

    @Bindable
    public String getNote() {return mNote; }

    @Bindable
    public void setNote(String note) {
        mNote = note;
    }

    //

    @Bindable
    public String getError() {return mError; }

    @Bindable
    public void setError(String error) {
        mError = error;
    }

    //

    @Bindable
    public String getHowToRestore() {
        return MyFunctions.fromHtml(mContext.getString(R.string.help_restore_how_to)).toString();
    }

    // NOTE: Leave as is
    //       To use Data Binding on the How To with CDATA
    @Bindable
    public void setHowToRestore(String howToRestore) {
        mHowToRestore = howToRestore;
    }
}