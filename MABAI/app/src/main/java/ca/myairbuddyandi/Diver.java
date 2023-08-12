package ca.myairbuddyandi;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.Date;

/**
 * Created by Michel on 2016-11-28.
 * This POJO is very stand alone. It does not require any other PK e.g. Dive
 * This is why this same POJO is used in Pick a Diver ad Edit a Diver
 */

public class Diver extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "Diver";

    // Public

    // Protected

    // Private
    private boolean mGender;
    private int mLogBookNo;
    private Boolean mHasDataChanged = false;
    private Date mBirthDate;
    private Double mMaxDepthAllowed;
    private Double mMaxDepthAllowedOld;
    private Long mDiveNo;
    private Long mDiverNo;
    private String mCertificationBody;
    private String mCertificationLevel;
    private String mEmail;
    private String mFirstName;
    private String mFirstNameOld;
    private String mLastName;
    private String mLastNameOld;
    private String mMiddleName;
    private String mPhone;

    // Excluded from Parcelable

    private boolean mChecked = false;
    private int mDives;
    private int mVisible = View.GONE;
    private Boolean mInMultiEditMode = false;
    private transient Context mContext;

    // Emd ov variables

    // Public constructor
    public Diver() {
    }

    // Getters and setters

    public void setContext(Context context) {
        mContext = context;
    }

    //

    Date getBirthDate() {return mBirthDate; }

    void setBirthDate(Date birthDate) {mBirthDate = birthDate;}

    public String getBirthDateString() {return MyFunctions.convertDateFromDateToString(mContext, getBirthDate()); }

    public void setBirthDateString(String birthDateString) {setBirthDate(MyFunctions.convertDateFromStringToDate(mContext, birthDateString));}

    //

    public Long getDiveNo() {return mDiveNo; }

    public void setDiveNo(Long diveNo) {
        mDiveNo = diveNo;
    }

    //

    public Long getDiverNo() {return mDiverNo; }

    public void setDiverNo(Long diverNo) { mDiverNo = diverNo; }

    //

    public int getDives() {return mDives; }

    public void setDives(int dives) {
        mDives = dives;
    }

    //

    public String getCertificationBody() {return mCertificationBody; }

    public void setCertificationBody (String certificationBody) {mCertificationBody = certificationBody;}

    //

    public String getCertificationLevel() {return mCertificationLevel; }

    public void setCertificationLevel (String certificationLevel) {mCertificationLevel = certificationLevel;}

    //

    public String getEmail() {return mEmail; }

    public void setEmail(String email) {mEmail = email;}

    //

    public String getFirstName() {return mFirstName; }

    public void setFirstName(String firstName) {mFirstName = firstName;}

    //

    public String getFirstNameOld() {return mFirstNameOld; }

    public void setFirstNameOld(String firstNameOld) {mFirstNameOld = firstNameOld;}

    //

    public String getFullName() {
        if (mDiverNo.equals(MyConstants.ZERO_L)) {
            return mLastName;
        } else if (!mLastName.trim().isEmpty() && !mFirstName.trim().isEmpty()){
            return mLastName + ", " + mFirstName;
        } else if (!mLastName.trim().isEmpty()) {
            return mLastName;
        } else {
            return mFirstName;
        }
    }
    //
    public boolean getGender() {return mGender;}

    public void setGender(boolean gender) {mGender = gender;}

    //

    public String getLastName() {return mLastName; }

    public void setLastName(String lastName) {mLastName = lastName;}

    //

    public String getLastNameOld() {return mLastNameOld; }

    public void setLastNameOld(String lastNameOld) {mLastNameOld = lastNameOld;}

    //

    String getLastOrFirstName() {
        if (mLastName.trim().isEmpty()) {
            return mFirstName;
        } else {
            return mLastName;
        }
    }

    //

    public int getLogBookNo() {return mLogBookNo; }

    public void setLogBookNo(int logBookNo) {mLogBookNo = logBookNo;}

    //

    public Double getMaxDepthAllowed() {return mMaxDepthAllowed; }

    public void setMaxDepthAllowed(Double maxDepthAllowed) {mMaxDepthAllowed = maxDepthAllowed;}

    //

    public Double getMaxDepthAllowedOld() {return mMaxDepthAllowedOld; }

    void setMaxDepthAllowedOld(Double maxDepthAllowedOld) {mMaxDepthAllowedOld = maxDepthAllowedOld;}

    //

    public String getMiddleName() {return mMiddleName; }

    public void setMiddleName(String middleName) {mMiddleName = middleName;}

    //

    public String getPhone() {return mPhone;}

    public void setPhone(String phone) {mPhone = phone;}

    //

    public  boolean getWoman() {
        return !mGender;
    }

    // My functions

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {
        mHasDataChanged = hasDataChanged;}

    //

    public int getVisible() {return mVisible;}

    public void setVisible(int visible) {mVisible = visible;}

    //

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    // Data Binding

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {mChecked = checked;}

    //

    @Bindable
    public TextWatcher getOnTextChanged() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                setHasDataChanged(true);
            }
        };
    }

    @Bindable
    public TextWatcher getOnTextChangedMDA() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO: Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!getMaxDepthAllowedOld().toString().equals(s.toString())) {
                    setHasDataChanged(true);
                }
                onTextChanged(s.toString());

            }
        };
    }

    //

    @Bindable
    public RadioGroup.OnCheckedChangeListener getOnRadioGroupChanged() {
        return new MyRadioGroupWatcher() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                super.onCheckedChanged(group, checkedId);
                setHasDataChanged(true);
            }
        };
    }

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Diver diver = (Diver) o;

        return mDiverNo.equals(diver.mDiverNo);

    }

    @Override
    public int hashCode() {
        return mDiverNo.hashCode();
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mBirthDate != null ? this.mBirthDate.getTime() : -1);
        dest.writeValue(this.mDiverNo);
        dest.writeValue(this.mDiveNo);
        dest.writeInt(this.mLogBookNo);
        dest.writeString(this.mFirstName);
        dest.writeString(this.mMiddleName);
        dest.writeString(this.mLastName);
        dest.writeByte(this.mGender ? (byte) 1 : (byte) 0);
        dest.writeString(this.mPhone);
        dest.writeString(this.mEmail);
        dest.writeString(this.mCertificationBody);
        dest.writeString(this.mCertificationLevel);
        dest.writeValue(this.mMaxDepthAllowed);
        dest.writeValue(this.mHasDataChanged);
    }

    protected Diver(Parcel in) {
        long tmpMBirthDate = in.readLong();
        this.mBirthDate = tmpMBirthDate == -1 ? null : new Date(tmpMBirthDate);
        this.mDiverNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDiveNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mLogBookNo = in.readInt();
        this.mFirstName = in.readString();
        this.mMiddleName = in.readString();
        this.mLastName = in.readString();
        this.mGender = in.readByte() != 0;
        this.mPhone = in.readString();
        this.mEmail = in.readString();
        this.mCertificationBody = in.readString();
        this.mCertificationLevel = in.readString();
        this.mMaxDepthAllowed = (Double) in.readValue(Double.class.getClassLoader());
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Diver> CREATOR = new Creator<Diver>() {
        @Override
        public Diver createFromParcel(Parcel source) {
            return new Diver(source);
        }

        @Override
        public Diver[] newArray(int size) {
            return new Diver[size];
        }
    };
}
