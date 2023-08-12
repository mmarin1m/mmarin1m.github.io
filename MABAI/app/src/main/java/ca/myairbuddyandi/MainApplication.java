package ca.myairbuddyandi;

import android.app.Application;
import android.content.Context;

/**
 * Created by Michel on 2016-12-08.
 * Holds all of the logic for the MainApplication class
 */

public class MainApplication extends Application {

    // Static
    private static Context mContext;

    // Public

    // Protected

    // Private
    private State mState;

    // End of variables

    // Public constructor
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    // Getters and setters

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

    // Allows to perform MainApplication.getContext().getResources().getString(R.string.button_depth) in a POJO
    public static Context getContext(){
        return mContext;
    }
}