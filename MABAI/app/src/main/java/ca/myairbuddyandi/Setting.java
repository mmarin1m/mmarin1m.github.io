package ca.myairbuddyandi;

/**
 * Created by Michel on 2022-03-04.
 * Holds all of the logic for the Setting class
 */

public class Setting {

    // Static
    private static final String LOG_TAG = "Setting";

    // Public

    // Protected

    // Private
    private String mColumnName = " ";
    private String mColumnValue = " ";

    // End of variables

    // Public constructor
    public Setting() {
    }

    // Getters and Setters

    public String getColumnName() {return mColumnName; }

    public void setColumnName(String columnName) {
        mColumnName = columnName;
    }

    //

    public String getColumnValue() {return mColumnValue; }

    public void setColumnValue(String columnValue) {
        mColumnValue = columnValue;
    }
}