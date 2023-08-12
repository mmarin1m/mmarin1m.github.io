package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2023-03-21.
 * Holds all of the logic for the ComputerPick class
 *
 * This POJO is used to list all supported dive computer in MABAI
 */

public class ComputerPick extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "ComputerPick";

    // Public

    // Protected

    // Private
    private long mComputerNo;
    private String mVendor;
    private String mDescription;
    private String mProduct;

    // Excluded from Parcelable
    private Boolean mInMultiEditMode = false;
    private boolean mVisible = false;
    private boolean mChecked = false;

    // End of variables

    // Public constructor
    public ComputerPick() {
    }

    // Getters and setters

    public void setVendor(String vendor) { mVendor = vendor;}

    public String getVendor() {return mVendor;}

    //

    public long getComputerNo() {return mComputerNo; }

    public void setComputerNo(long computerNo) {
        mComputerNo = computerNo;
    }

    //

    public void setDescription(String description) { mDescription = description;}

    public String getDescription() {return mDescription;}

    //

    public void setProduct(String product) { mProduct = product;}

    public String getProduct() {return mProduct;}

    // My functions

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {this.mVisible = visible;}

    //

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    //

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {this.mChecked = checked;}

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComputerPick computer = (ComputerPick) o;

        return mComputerNo == computer.mComputerNo;

    }

    @Override
    public int hashCode() {
        return (int) (mComputerNo ^ (mComputerNo >>> 32));
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mComputerNo);
        dest.writeString(this.mVendor);
        dest.writeString(this.mDescription);
        dest.writeString(this.mProduct);
    }

    protected ComputerPick(Parcel in) {
        this.mComputerNo = in.readLong();
        this.mVendor = in.readString();
        this.mDescription = in.readString();
        this.mProduct = in.readString();
    }

    public static final Creator<ComputerPick> CREATOR = new Creator<ComputerPick>() {
        @Override
        public ComputerPick createFromParcel(Parcel source) {
            return new ComputerPick(source);
        }

        @Override
        public ComputerPick[] newArray(int size) {
            return new ComputerPick[size];
        }
    };
}
