package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.Objects;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the DynamicSpinner class
 */

public class DynamicSpinner extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "DynamicSpinner";

    // Public

    // Protected

    // Private
    private String mSpinnerText;
    private String mSpinnerType;
    private String mSystemDefined;

    // Excluded from parcelable
    private boolean mChecked = false;
    private boolean mVisible = false;
    private Boolean mHasDataChanged = false;
    private Boolean mInMultiEditMode = false;

    // End of variables

    // Public constructor
    public DynamicSpinner() {
    }

    // Getters and setters

    public String getSpinnerType() {return mSpinnerType; }

    public void setSpinnerType(String spinnerType) {mSpinnerType = spinnerType;}

    //

    public String getSpinnerText() {return mSpinnerText; }

    public void setSpinnerText(String spinnerText) {mSpinnerText = spinnerText;}

    //

    public String getSystemDefined() {return mSystemDefined; }

    public void setSystemDefined(String systemDefined) {
        mSystemDefined = systemDefined;
    }

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

    // NOTE: Do not annotate @NonNUll
    @Override
    public String toString() {
        // What to display in the Spinner list
        return this.mSpinnerText;
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
        DynamicSpinner that = (DynamicSpinner) o;
        return mSpinnerType.equals(that.mSpinnerType) &&
                mSpinnerText.equals(that.mSpinnerText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSpinnerType, mSpinnerText);
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSpinnerType);
        dest.writeString(this.mSpinnerText);
        dest.writeValue(this.mSystemDefined);
    }

    protected DynamicSpinner(Parcel in) {
        this.mSpinnerType = in.readString();
        this.mSpinnerText = in.readString();
        this.mSystemDefined = in.readString();
    }

    public static final Creator<DynamicSpinner> CREATOR = new Creator<DynamicSpinner>() {
        @Override
        public DynamicSpinner createFromParcel(Parcel source) {
            return new DynamicSpinner(source);
        }

        @Override
        public DynamicSpinner[] newArray(int size) {
            return new DynamicSpinner[size];
        }
    };
}
