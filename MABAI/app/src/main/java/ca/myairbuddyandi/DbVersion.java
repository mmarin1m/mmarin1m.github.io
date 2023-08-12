package ca.myairbuddyandi;

/**
 * Created by Michel on 2022-03-21.
 * Holds all of the logic for the DbVersion class
 */

public class DbVersion {

    // Static
    private static final String LOG_TAG = "DbVersion";

    // Public

    // Protected

    // Private
    private String mColumnName = " ";
    private String mColumnValue = " ";

    // End of variables

    // Public constructor
    public DbVersion() {
    }

    // Getters and setters

    // NOTE: Reserved for future use
    public String getColumnName() {return mColumnName; }

    public void setColumnName(String columnName) {
        mColumnName = columnName;
    }

    //

    // NOTE: Reserved for future use
    public String getColumnValue() {return mColumnValue; }

    public void setColumnValue(String columnValue) {
        mColumnValue = columnValue;
    }
}