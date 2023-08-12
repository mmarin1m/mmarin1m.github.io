package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.DiveTypePickBinding;
import ca.myairbuddyandi.databinding.DiveTypePickHeaderBinding;

/**
 * Created by Michel on 2016-12-15.
 * Holds all of the logic for the DiveTypePickAdapter class
 */

public class DiveTypePickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "DiveTypePickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<DiveType> mDiveTypePickList;
    private Boolean mDescendingType = false;
    private Boolean mDescendingDescription = false;
    private Boolean mDescendingDives = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private DiveType mDiveType = new DiveType();

    // End of variables

    // Public constructor
    public DiveTypePickAdapter(Context context, ArrayList<DiveType> diveTypePickList) {
        mContext = context;
        mDiveTypePickList = diveTypePickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final DiveTypePickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindDiveType(DiveType diveType) {
            binding.setDiveType(diveType);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            DiveTypePickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrType.setOnClickListener(view -> {
                // The user clicked on the Header Log Book No
                if (mDiveTypePickList.size() > 0) {
                    mDiveTypePickList.sort(new DiveTypeComparatorType(mDescendingType));
                    mDescendingType = !mDescendingType;
                    setDiveTypeList(mDiveTypePickList);
                    notifyDataSetChanged();
                }

            });

            binding.hdrDescription.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mDiveTypePickList.size() > 0) {
                    mDiveTypePickList.sort(new DiveTypeComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setDiveTypeList(mDiveTypePickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrDives.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mDiveTypePickList.size() > 0) {
                    mDiveTypePickList.sort(new DiveTypeComparatorDives(mDescendingDives));
                    mDescendingDives = !mDescendingDives;
                    setDiveTypeList(mDiveTypePickList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends DiveTypePickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            DiveTypePickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, DiveTypeActivity.class);
                DiveType diveType;
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                if (position >= MyConstants.ZERO_I) {
                    diveType = mDiveTypePickList.get(position);
                    intent.putExtra(MyConstants.DIVE_TYPE, diveType);
                    ((DiveTypePickActivity) mContext).setDiveType(diveType);
                    ((DiveTypePickActivity) mContext).editLauncher.launch(intent);
                }
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((DiveTypePickActivity) mContext).countDiveTypes(checkBox.isChecked());
                } else {
                    // Select a new DiveType by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mDiveType = mDiveTypePickList.get(position);
                        // Checks for expanded view, collapse if you find one
                        if (mExpandedPosition >= MyConstants.ZERO_I) {
                            notifyItemChanged(mExpandedPosition);
                        }
                        // Set the current position to expanded
                        mExpandedPosition = getBindingAdapterPosition();
                        notifyItemChanged(mExpandedPosition);
                        // Updating old as well as new positions
                        // Deselect the old position
                        notifyItemChanged(mSelectedPosition);
                        // Select the new position
                        mSelectedPosition = position + HEADER_OFFSET;
                        notifyItemChanged(mSelectedPosition);
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                // Enter Multi Edit Mode
                mInMultiEditMode = !mInMultiEditMode;
                if (mCheckBoxHdr != null) {
                    mCheckBoxHdr.setVisibility((mInMultiEditMode) ? View.INVISIBLE : View.GONE);
                    int position = getBindingAdapterPosition();
                    if (position >= MyConstants.ZERO_I) {
                        mExpandedPosition = -1;
                        ((DiveTypePickActivity) mContext).setVisibility(position, mInMultiEditMode);
                    }
                }
                return true;
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View pickDiveTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_type_pick_header, parent, false);
            return new DiveTypePickAdapter.VHHeader(pickDiveTypeAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickDiveTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_type_pick, parent, false);
            return new DiveTypePickAdapter.VHItem(pickDiveTypeAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DiveTypePickAdapter.VHItem) {
            DiveType dataObject = mDiveTypePickList.get(position - HEADER_OFFSET);

            DiveTypePickAdapter.ViewHolder itemView = (DiveTypePickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.diveType, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindDiveType(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mDiveType = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((DiveTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mDiveTypePickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((DiveTypePickActivity) mContext).doSmoothScroll(mDiveTypePickList.size());
                }
            } else {
                ((DiveTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            ((DiveTypePickAdapter.VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((DiveTypePickActivity) mContext).countDiveTypes(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDiveTypePickList.size() + HEADER_OFFSET;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    public void addDiveType(DiveType diveType) {
        mDiveType = diveType;
        // Add the new DiveType to the Array list
        mDiveTypePickList.add(diveType);
        // Via the Activity, tell the RecyclerView to scroll to the newly added DiveType
        ((DiveTypePickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
    }

    public void modifyDiveType(DiveType modifiedDiveType) {
        mDiveType = modifiedDiveType;
        // Find the position of the modified DiveType in the collection
        int position = mDiveTypePickList.indexOf(modifiedDiveType);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old DiveType with the modified DiveType
            mDiveTypePickList.set(position, modifiedDiveType);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteDiveType(int position) {
        mDiveTypePickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {
        return position == MyConstants.ZERO_I;
    }

    public void setDiveType (DiveType diveType) {mDiveType = diveType;}

    private void setDiveTypeList(ArrayList<DiveType> diveTypePickList) {mDiveTypePickList = diveTypePickList;}

    public ArrayList<DiveType> getDiveTypePickList() {return mDiveTypePickList;}

    public DiveType getDiveType () {
        return mDiveType;
    }

    public DiveType getDiveType (int position) {
        return mDiveTypePickList.get(position);
    }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {
        mInMultiEditMode = inMultiEditMode;
    }
}