package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import ca.myairbuddyandi.databinding.SegmentTypePickBinding;
import ca.myairbuddyandi.databinding.SegmentTypePickHeaderBinding;

/**
 * Created by Michel on 2016-12-15.
 * Holds all of the logic for the SegmentTypePickAdapter class
 */

public class SegmentTypePickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "SegmentTypePickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<SegmentType> mSegmentTypePickList;
    private Boolean mDescendingType = false;
    private Boolean mDescendingDescription = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private SegmentType mSegmentType = new SegmentType();

    // End of variables

    // Public constructor
    public SegmentTypePickAdapter(Context context, ArrayList<SegmentType> segmentTypePickList) {
        mContext = context;
        mSegmentTypePickList = segmentTypePickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final SegmentTypePickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindSegmentType(SegmentType segmentType) {
            binding.setSegmentType(segmentType);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            SegmentTypePickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrType.setOnClickListener(view -> {
                // The user clicked on the Header Log Book No
                if (mSegmentTypePickList.size() > 0) {
                    SegmentType segmentType;
                    segmentType = mSegmentTypePickList.get(mSelectedPosition - HEADER_OFFSET);
                    mSegmentTypePickList.sort(new SegmentTypeComparatorType(mDescendingType));
                    mDescendingType = !mDescendingType;
                    setSegmentTypeList(mSegmentTypePickList);
                    notifyDataSetChanged();
                    // Save the current selectedPosition
                    // Needed to remove the backgroundColor
                    int previousPosition = mSelectedPosition;
                    // Set the mSelectedPosition based on the indexOf the previously selected SegmentType
                    mSelectedPosition = mSegmentTypePickList.indexOf(segmentType) + HEADER_OFFSET;
                    // Remove the backgroundColor
                    notifyItemChanged(previousPosition);
                }

            });

            binding.hdrDescription.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mSegmentTypePickList.size() > 0) {
                    SegmentType segmentType;
                    segmentType = mSegmentTypePickList.get(mSelectedPosition - HEADER_OFFSET);
                    mSegmentTypePickList.sort(new SegmentTypeComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setSegmentTypeList(mSegmentTypePickList);
                    notifyDataSetChanged();
                    // Save the current selectedPosition
                    // Needed to remove the backgroundColor
                    int previousPosition = mSelectedPosition;
                    // Set the mSelectedPosition based on the indexOf the previously selected SegmentType
                    mSelectedPosition = mSegmentTypePickList.indexOf(segmentType) + HEADER_OFFSET;
                    // Remove the backgroundColor
                    notifyItemChanged(previousPosition);
                }
            });
        }
    }

    private class VHItem extends SegmentTypePickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            SegmentTypePickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, SegmentTypeActivity.class);
                SegmentType segmentType;
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                if (position >= MyConstants.ZERO_I) {
                    segmentType = mSegmentTypePickList.get(position);
                    intent.putExtra(MyConstants.SEGMENT_TYPE, segmentType);
                    ((SegmentTypePickActivity) mContext).setSegmentType(segmentType);
                    ((SegmentTypePickActivity) mContext).editLauncher.launch(intent);
                }
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((SegmentTypePickActivity) mContext).countSegmentTypes(checkBox.isChecked());
                } else {
                    // Select a new SegmentType by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mSegmentType = mSegmentTypePickList.get(position);
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
                // The Checkbox in the Header never shows up
                // It's either INVISIBLE to move the header to the right
                // Or GONE to move the header to the left
                if (mCheckBoxHdr != null) {
                    mCheckBoxHdr.setVisibility((mInMultiEditMode) ? View.INVISIBLE : View.GONE);
                    int position = getBindingAdapterPosition();
                    if (position >= MyConstants.ZERO_I) {
                        mExpandedPosition = -1;
                        ((SegmentTypePickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickSegmentTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.segment_type_pick_header, parent, false);
            return new SegmentTypePickAdapter.VHHeader(pickSegmentTypeAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickSegmentTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.segment_type_pick, parent, false);
            return new SegmentTypePickAdapter.VHItem(pickSegmentTypeAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SegmentTypePickAdapter.VHItem) {
            SegmentType dataObject = mSegmentTypePickList.get(position - HEADER_OFFSET);

            SegmentTypePickAdapter.ViewHolder itemView = (SegmentTypePickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(ca.myairbuddyandi.BR.segmentType, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindSegmentType(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mSegmentType = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((SegmentTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                // Deselect the Data Row
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                ((SegmentTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            ((SegmentTypePickAdapter.VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((SegmentTypePickActivity) mContext).countSegmentTypes(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSegmentTypePickList.size() + HEADER_OFFSET;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My Functions

    public void addSegmentType(SegmentType segmentType) {
        mSegmentType = segmentType;
        // Add the new SegmentType to the Array list
        mSegmentTypePickList.add(segmentType);
        // Via the Activity, tell the RecyclerView to scroll to the newly added SegmentType
        ((SegmentTypePickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
        // Save the current selectedPosition
        // Needed to remove the backgroundColor
        int previousPosition = mSelectedPosition;
        // Set the mSelectedPosition based on the indexOf the newly added SegmentType
        mSelectedPosition = mSegmentTypePickList.indexOf(segmentType) + HEADER_OFFSET;
        // Remove the backgroundColor
        mExpandedPosition = -1;
        notifyItemChanged(previousPosition);
    }

    public void modifySegmentType(SegmentType modifiedSegmentType) {
        mSegmentType = modifiedSegmentType;
        // Find the position of the modified SegmentType in the collection
        int position = mSegmentTypePickList.indexOf(modifiedSegmentType);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old SegmentType with the modified SegmentType
            mSegmentTypePickList.set(position, modifiedSegmentType);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteSegmentType(int position) {
        mSegmentTypePickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {return position == MyConstants.ZERO_I;}

    public void setSegmentType(SegmentType segmentType) {mSegmentType = segmentType;}

    private void setSegmentTypeList(ArrayList<SegmentType> segmentTypePickList) {mSegmentTypePickList = segmentTypePickList;}

    public ArrayList<SegmentType> getSegmentTypePickList() {return mSegmentTypePickList;}

    public SegmentType getSegmentType() {return mSegmentType;}

    public SegmentType getSegmentType(int position) {return mSegmentTypePickList.get(position);}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}
}