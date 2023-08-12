package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the DiveType class
 */

public class DiveType extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "DiveType";

    // Public

    // Protected

    // Private
    private String mDescription;
    private String mDiveType;
    private String mInPicker;
    private Integer mSortOrder;

    // Excluded from parcelable
    private boolean mVisible = false;
    private boolean mChecked = false;
    private int mDives;
    private Boolean mHasDataChanged = false;
    private Boolean mInMultiEditMode = false;

    // End of variables

    // Public constructor
    public DiveType() {
    }

    // Getters and setters

    public String getDiveType() {return mDiveType; }

    public void setDiveType(String diveType) {mDiveType = diveType;}

    //

    public String getDescription() {return mDescription; }

    public void setDescription(String description) {
        mDescription = description;
    }

    //

    public int getDives() {return mDives; }

    public void setDives(int dives) {
        mDives = dives;
    }

    //

    public Integer getSortOrder() {return mSortOrder; }

    public void setSortOrder(Integer inSortOrder) {mSortOrder = inSortOrder;}

    //

    public String getInPicker() {return mInPicker; }

    public void setInPicker(String inPicker) {mInPicker = inPicker;}

    // My functions

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    //

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {this.mVisible = visible;}

    //

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    //

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {this.mChecked = checked;}

    //

    // NOTE: Do Not annotate at @NonNull
    @Override
    public String toString() {
        // What to display in the Spinner list
        return this.mDescription;
    }

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

        DiveType diveType = (DiveType) o;

        return mDiveType.equals(diveType.mDiveType);

    }

    @Override
    public int hashCode() {
        return mDiveType.hashCode();
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDiveType);
        dest.writeString(this.mDescription);
        dest.writeValue(this.mSortOrder);
        dest.writeString(this.mInPicker);
    }

    protected DiveType(Parcel in) {
        this.mDiveType = in.readString();
        this.mDescription = in.readString();
        this.mSortOrder = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mInPicker = in.readString();
    }

    public static final Parcelable.Creator<DiveType> CREATOR = new Parcelable.Creator<DiveType>() {
        @Override
        public DiveType createFromParcel(Parcel source) {
            return new DiveType(source);
        }

        @Override
        public DiveType[] newArray(int size) {
            return new DiveType[size];
        }
    };
}
