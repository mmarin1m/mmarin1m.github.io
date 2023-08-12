package ca.myairbuddyandi;

/**
 * Created by Michel on 2020-04-28.
 * Holds all of the logic for the Constant class
 */

public class Constant {

    // Static
    private static final String LOG_TAG = "Constant";

    // Public

    // Protected

    // Private
    private Double mValue;
    private String mDescription;
    private String mSystem;
    private String mUnit;

    // End of variables

    // Public constructor
    public Constant() {
    }

    // Getters and setters

    public String getDescription() {return mDescription; }

    public void setDescription(String description) {mDescription = description;}

    //

    public Double getValue() {return mValue; }

    public void setValue(Double value) {mValue = value;}

    //

    public String getSystem() {return mSystem; }

    public void setSystem(String system) {mSystem = system;}

    //

    public String getUnit() {return mUnit; }

    public void setUnit(String unit) {mUnit = unit;}
}
