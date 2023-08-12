package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the UsageType class
 */

public class UsageType extends BaseObservable implements Parcelable {

    //Static
    private static final String LOG_TAG = "UsageType";

    // Public

    // Protected

    // Private
    private String mDescription;
    private String mSystemDefined;
    private String mUsageType;

    // Excluded from parcelable
    private boolean mChecked = false;
    private int mVisible = View.GONE;
    private Boolean mHasDataChanged = false;
    private Boolean mInMultiEditMode = false;

    // End of variables

    // Public constructor
    public UsageType() {
    }

    // Getters and setters

    public String getUsageType() {return mUsageType; }

    public void setUsageType(String usageType) {
        mUsageType = usageType;
    }

    //

    public String getDescription() {return mDescription; }

    public void setDescription(String description) {
        mDescription = description;
    }

    //

    public String getSystemDefined() {return mSystemDefined; }

    public void setSystemDefined(String systemDefined) {
        mSystemDefined = systemDefined;
    }

    // My functions

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    //

    public int getVisible() {return mVisible;}

    public void setVisible(int visible) {mVisible = visible;}

    //

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    //

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {mChecked = checked;}

    //

    // NOTE: Do not annotate @NonNull
    @Override
    public String toString() {
        // What to display in the Spinner list
        return mDescription;
    }

    //

    @Bindable
    public TextWatcher getOnTextChanged() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mHasDataChanged = true;
            }
        };
    }

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsageType usageType = (UsageType) o;

        return mUsageType.equals(usageType.mUsageType);

    }

    @Override
    public int hashCode() {
        return mUsageType.hashCode();
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUsageType);
        dest.writeString(this.mDescription);
        dest.writeString(this.mSystemDefined);
    }

    protected UsageType(Parcel in) {
        this.mUsageType = in.readString();
        this.mDescription = in.readString();
        this.mSystemDefined = in.readString();
    }

    public static final Parcelable.Creator<UsageType> CREATOR = new Parcelable.Creator<UsageType>() {
        @Override
        public UsageType createFromParcel(Parcel source) {
            return new UsageType(source);
        }

        @Override
        public UsageType[] newArray(int size) {
            return new UsageType[size];
        }
    };
}
