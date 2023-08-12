package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all the logic for the GrouppType class
 */

public class GrouppType extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "GrouppType";

    // Public

    // Protected

    // Private
    private String mDescription;
    private String mGrouppType;
    private String mSystemDefined;

    // Excluded from parcelable
    private Boolean mHasDataChanged = false;
    private Boolean mInMultiEditMode = false;
    private boolean mChecked = false;
    private int mVisible = View.GONE;

    // End of variables

    // Public constructor
    public GrouppType() {
    }

    // Getters and setters

    public String getGroupType() {return mGrouppType; }

    public void setGroupType(String groupType) {
        mGrouppType = groupType;
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

    public int getVisible() {return mVisible; }

    public void setVisible(int visible) {mVisible = visible; }

    //

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    //

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

        GrouppType grouppType = (GrouppType) o;

        return mGrouppType.equals(grouppType.mGrouppType);

    }

    @Override
    public int hashCode() {
        return mGrouppType.hashCode();
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mGrouppType);
        dest.writeString(this.mDescription);
        dest.writeString(this.mSystemDefined);
    }

    protected GrouppType(Parcel in) {
        this.mGrouppType = in.readString();
        this.mDescription = in.readString();
        this.mSystemDefined = in.readString();
    }

    public static final Creator<GrouppType> CREATOR = new Creator<GrouppType>() {
        @Override
        public GrouppType createFromParcel(Parcel source) {
            return new GrouppType(source);
        }

        @Override
        public GrouppType[] newArray(int size) {
            return new GrouppType[size];
        }
    };
}
