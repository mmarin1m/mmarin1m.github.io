package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CylinderType class
 */

public class CylinderType extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "CylinderType";

    // Public

    // Protected

    // Private
    private String mCylinderType;
    private String mDescription;
    private Double mVolume;
    private Double mRatedPressure;

    // Excluded from parcelable
    private Boolean mHasDataChanged = false;
    private Boolean mInMultiEditMode = false;
    private boolean mVisible = false;
    private boolean mChecked = false;
    private int mDives;

    // End of variables

    // Public constructor
    public CylinderType() {
    }

    // Getters and setters

    public String getCylinderType() {return mCylinderType; }

    public void setCylinderType(String cylinderType) {mCylinderType = cylinderType;}

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

    public Double getVolume() {return mVolume; }

    public void setVolume(Double volume) {
        mVolume = volume;
    }

    //

    public Double getRatedPressure() {return mRatedPressure; }

    public void setRatedPressure(Double ratedPressure) {
        mRatedPressure = ratedPressure;
    }

    // My functions

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {this.mVisible = visible;}

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    // Data Binding

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {this.mChecked = checked;}

    // NOTE: Do not annotate @NonNull
    @Override
    public String toString() {
        // What to display in the Spinner list
        return mDescription;
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

        CylinderType that = (CylinderType) o;

        return mCylinderType.equals(that.mCylinderType);

    }

    @Override
    public int hashCode() {
        return mCylinderType.hashCode();
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCylinderType);
        dest.writeString(this.mDescription);
        dest.writeValue(this.mVolume);
        dest.writeValue(this.mRatedPressure);
        dest.writeValue(this.mHasDataChanged);
        dest.writeByte(this.mVisible ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mChecked ? (byte) 1 : (byte) 0);
    }

    protected CylinderType(Parcel in) {
        this.mCylinderType = in.readString();
        this.mDescription = in.readString();
        this.mVolume = (Double) in.readValue(Double.class.getClassLoader());
        this.mRatedPressure = (Double) in.readValue(Double.class.getClassLoader());
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mVisible = in.readByte() != 0;
        this.mChecked = in.readByte() != 0;
    }

    public static final Creator<CylinderType> CREATOR = new Creator<CylinderType>() {
        @Override
        public CylinderType createFromParcel(Parcel source) {
            return new CylinderType(source);
        }

        @Override
        public CylinderType[] newArray(int size) {
            return new CylinderType[size];
        }
    };
}
