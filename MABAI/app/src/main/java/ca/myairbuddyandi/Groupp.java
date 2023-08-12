package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.ArrayList;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the Groupp class
 */

public class Groupp extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "Groupp";
    private static final int HINT_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mGroupTypePosition;
    private int mLogBookNo;
    private Boolean mHasDataChanged = false;
    private Long mGrouppNo;
    private Long mDiverNo;
    private Long mDiveNo;
    private String mGrouppType;
    private String mGrouppTypeDescription;
    private String mDescription;
    private String mCylinderType;
    private String mUsageType;

    // Excluded from Parcelable
    private boolean mChecked = false;
    private boolean mVisible = false;
    private ArrayAdapter<GrouppType> mAdapterGroupType;
    private ArrayList<GrouppType> mItemsGrouppType;
    private Boolean mInMultiEditMode = false;
    private String mGrouppTypeOriginal;

    // End of variables

    // Public constructor
    Groupp() {
    }

    // Getters and setters

    public Long getGroupNo() {return mGrouppNo; }

    void setGroupNo(Long groupNo) {
        mGrouppNo = groupNo;
    }

    //

    public Long getDiverNo() {return mDiverNo; }

    public void setDiverNo(Long diverNo) {
        mDiverNo = diverNo;
    }

    //

    public Long getDiveNo() {return mDiveNo; }

    public void setDiveNo(Long diveNo) {
        mDiveNo = diveNo;
    }

    //

    public int getLogBookNo() {return mLogBookNo; }

    public void setLogBookNo(int logBookNo) {mLogBookNo = logBookNo;}

    //

    public String getDescription() {return mDescription; }

    public void setDescription(String description) { mDescription = description; }

    //

    public String getCylinderType() {return mCylinderType; }

    public void setCylinderType(String cylinderType) {
        mCylinderType = cylinderType;
    }

    //

    public String getUsageType() {return mUsageType; }

    public void setUsageType(String usageType) {
        mUsageType = usageType;
    }

    //

    public String getGroupTypeOriginal() {return mGrouppTypeOriginal; }

    public void setGroupTypeOriginal(String groupTypeOriginal) { mGrouppTypeOriginal = groupTypeOriginal; }

    //

    // GrouppType Spinner

    public String getGroupType() {return mGrouppType; }

    public void setGroupType(String groupType) {
        mGrouppType = groupType;
        //Set the position to select and show the data on the activity
        setGroupTypePosition(getGroupTypeIndex(mGrouppType));
    }

    public void setGroupTypeCommon(String groupType) {
        mGrouppType = groupType;
    }

    public void setGroupTypeLoad(String groupType) {
        mGrouppType = groupType;
    }

    public String getGroupTypeDescription() {return mGrouppTypeDescription; }

    public int getGroupTypePosition() {return mGroupTypePosition; }

    public void setGroupTypePosition(int groupTypePosition) {mGroupTypePosition = groupTypePosition;}

    public void setItemsGroupType(ArrayList<GrouppType> itemsGrouppType) {
        mItemsGrouppType = itemsGrouppType;}

    public void setAdapterGroupType(ArrayAdapter<GrouppType> adapterGroupType) {
        mAdapterGroupType = adapterGroupType;
    }

    public ArrayAdapter<GrouppType> getAdapterGroupType () {
        return mAdapterGroupType;
    }

    private int getGroupTypeIndex(String myGroupType)
    {
        int position = 0;

        for (int i = 0; i < mItemsGrouppType.size(); i++) {
            GrouppType grouppType = mItemsGrouppType.get(i);
            if(grouppType.getGroupType().equals(myGroupType)) {
                position = i;
                mGrouppTypeDescription = grouppType.getDescription();
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + 1;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedGroupType() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    GrouppType grouppType = (GrouppType) parent.getAdapter().getItem(position + 1);
                    mGrouppType = grouppType.getGroupType();
                    mGrouppTypeDescription = grouppType.getDescription();
                    // The Hint is in the list at item 0
                    if (position + HINT_OFFSET != mGroupTypePosition) {
                        mHasDataChanged = true;
                    }
                }
            }
        };
    }

    // End of Spinner

    // My functions

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {mVisible = visible;}

    //

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    //

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    // Data Binding

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {mChecked = checked;}

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

        Groupp groupp = (Groupp) o;

        return mGrouppNo.equals(groupp.mGrouppNo) && mDiverNo.equals(groupp.mDiverNo);

    }

    @Override
    public int hashCode() {
        int result = mGrouppNo.hashCode();
        result = 31 * result + mDiverNo.hashCode();
        return result;
    }

    // Starts of parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mGrouppNo);
        dest.writeValue(this.mDiverNo);
        dest.writeValue(this.mDiveNo);
        dest.writeString(this.mGrouppType);
        dest.writeString(this.mGrouppTypeDescription);
        dest.writeInt(this.mGroupTypePosition);
        dest.writeInt(this.mLogBookNo);
        dest.writeString(this.mDescription);
        dest.writeString(this.mCylinderType);
        dest.writeString(this.mUsageType);
        dest.writeValue(this.mHasDataChanged);
    }

    protected Groupp(Parcel in) {
        this.mGrouppNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDiverNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDiveNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mGrouppType = in.readString();
        this.mGrouppTypeDescription = in.readString();
        this.mGroupTypePosition = in.readInt();
        this.mLogBookNo = in.readInt();
        this.mDescription = in.readString();
        this.mCylinderType = in.readString();
        this.mUsageType = in.readString();
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Groupp> CREATOR = new Creator<Groupp>() {
        @Override
        public Groupp createFromParcel(Parcel source) {
            return new Groupp(source);
        }

        @Override
        public Groupp[] newArray(int size) {
            return new Groupp[size];
        }
    };
}
