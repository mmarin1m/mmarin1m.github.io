package ca.myairbuddyandi;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Michel on 2017-02-16.
 * To find out if MyText has been changed
 */

public abstract class MyTextWatcher implements TextWatcher {

    // Static
    private static final String LOG_TAG = "MyTextWatcher";

    // Public

    // Protected

    // Private

    // End of variables

    // Public constructor
    public MyTextWatcher() {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO: Auto-generated method stub
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO: Auto-generated method stub
    }

    @Override
    public void afterTextChanged(Editable s) {
        onTextChanged(s.toString());
    }

    // My functions
    public abstract void onTextChanged(String newValue);

}
