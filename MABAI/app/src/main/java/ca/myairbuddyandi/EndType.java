package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;

/**
 * Created by Michel on 2021-05-04.
 * Holds all the logic for the EndType class
 */

public class EndType extends BaseObservable {

    // Static
    private static final String LOG_TAG = "EndType";

    // Public

    // Protected

    // Private
    private String mEndType;
    private String mEndTypeDescription;

    // Public constructor
    public EndType() {
    }

    // Getters and setters

    public String getEndType() {return mEndType; }

    public void setEndType(String endType) {
        mEndType = endType;
    }

    //

    // NOTE: Reserved for future use
    public String getEndTypeDescription() {return mEndTypeDescription; }

    public void setEndTypeDescription(String endTypeDescription) { mEndTypeDescription = endTypeDescription; }

    // My functions

//    public Boolean getHasDataChanged() {return mHasDataChanged;}
//
//    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}
//
//    public int getVisible() {return mVisible; }
//
//    public void setVisible(int visible) {mVisible = visible; }
//
//    public Boolean getInMultiEditMode() {return mInMultiEditMode;}
//
//    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}
//
//    @Bindable
//    public boolean getChecked() {return mChecked;}
//
//    public void setChecked(boolean checked) {this.mChecked = checked;}

    // NOTE: Do not annotate @NonNull
    @Override
    public String toString() {
        // What to display in the Spinner list
        return mEndTypeDescription;
    }

//    @Bindable
//    public TextWatcher getOnTextChanged() {
//        return new MyTextWatcher() {
//            @Override
//            public void onTextChanged(String newValue) {
//                mHasDataChanged = true;
//            }
//        };
//    }

//    // Equals
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        EndType grouppType = (EndType) o;
//
//        return mGrouppType.equals(grouppType.mGrouppType);
//
//    }
//
//    @Override
//    public int hashCode() {
//        return mGrouppType.hashCode();
//    }
//
//    // Starts of parcelable
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.mGrouppType);
//        dest.writeString(this.mDescription);
//        dest.writeString(this.mSystemDefined);
//    }
//
//    public EndType() {
//    }
//
//    protected EndType(Parcel in) {
//        this.mGrouppType = in.readString();
//        this.mDescription = in.readString();
//        this.mSystemDefined = in.readString();
//    }
//
//    public static final Creator<EndType> CREATOR = new Creator<EndType>() {
//        @Override
//        public EndType createFromParcel(Parcel source) {
//            return new EndType(source);
//        }
//
//        @Override
//        public EndType[] newArray(int size) {
//            return new EndType[size];
//        }
//    };
}
