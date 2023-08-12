package ca.myairbuddyandi;

/**
 * Created by Michel on 2020-04-28.
 * Holds all of the logic for the Acronym class
 */

public class Acronym {

    // Static
    private static final String LOG_TAG = "Acronym";

    // Public

    // Protected

    // Private
    private String mAcronym;
    private String mDescription;

    // End of variables

    // Public constructor
    public Acronym() {
    }

    // Setters and getters

    public String getAcronym() {return mAcronym; }

    public void setAcronym(String acronym) {mAcronym = acronym;}

    public String getDescription() {return mDescription; }

    public void setDescription(String description) {mDescription = description;}
}
