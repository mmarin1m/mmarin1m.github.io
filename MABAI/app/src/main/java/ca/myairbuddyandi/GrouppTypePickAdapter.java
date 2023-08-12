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

import ca.myairbuddyandi.databinding.GrouppTypePickBinding;
import ca.myairbuddyandi.databinding.GrouppTypePickHeaderBinding;

/**
 * Created by Michel on 2016-12-15.
 * Holds all the logic for the GrouppTypePickAdapter class
 */

public class GrouppTypePickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "GrouppTypePickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<GrouppType> mGrouppTypePickList;
    private Boolean mDescendingType = false;
    private Boolean mDescendingDescription = false;
    private Boolean mInMultiEditMode = false;
    private final Context mContext;
    private CheckBox mCheckBoxHdr;
    private GrouppType mGrouppType = new GrouppType();

    // End of variables

    // Public constructor
    public GrouppTypePickAdapter(Context context, ArrayList<GrouppType> grouppTypePickList) {
        mContext = context;
        mGrouppTypePickList = grouppTypePickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final GrouppTypePickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindGroupType(GrouppType grouppType) {
            binding.setGrouppType(grouppType);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            GrouppTypePickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrType.setOnClickListener(view -> {
                // The user clicked on the Header Log Book No
                if (mGrouppTypePickList.size() > 0) {
                    mGrouppTypePickList.sort(new GrouppTypeComparatorType(mDescendingType));
                    mDescendingType = !mDescendingType;
                    setGroupTypeList(mGrouppTypePickList);
                    notifyDataSetChanged();
                }

            });

            binding.hdrDescription.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mGrouppTypePickList.size() > 0) {
                    mGrouppTypePickList.sort(new GrouppTypeComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setGroupTypeList(mGrouppTypePickList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends GrouppTypePickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            GrouppTypePickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, GrouppTypeActivity.class);
                GrouppType grouppType;
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                if (position >= MyConstants.ZERO_I) {
                    grouppType = mGrouppTypePickList.get(position);
                    intent.putExtra(MyConstants.GROUPP_TYPE, grouppType);
                    ((GrouppTypePickActivity) mContext).setGrouppType(grouppType);
                    ((GrouppTypePickActivity) mContext).editLauncher.launch(intent);
                }
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((GrouppTypePickActivity) mContext).countGrouppTypes(checkBox.isChecked());
                } else {
                    // Select a new GrouppType by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mGrouppType = mGrouppTypePickList.get(position);
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
                        ((GrouppTypePickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickGroupTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupp_type_pick_header, parent, false);
            return new GrouppTypePickAdapter.VHHeader(pickGroupTypeAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickGroupTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupp_type_pick, parent, false);
            return new GrouppTypePickAdapter.VHItem(pickGroupTypeAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof GrouppTypePickAdapter.VHItem) {
            GrouppType dataObject = mGrouppTypePickList.get(position - HEADER_OFFSET);

            GrouppTypePickAdapter.ViewHolder itemView = (GrouppTypePickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.grouppType, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindGroupType(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mGrouppType = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((GrouppTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mGrouppTypePickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((GrouppTypePickActivity) mContext).doSmoothScroll(mGrouppTypePickList.size());
                }
            } else {
                ((GrouppTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            ((GrouppTypePickAdapter.VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((GrouppTypePickActivity) mContext).countGrouppTypes(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {return mGrouppTypePickList.size() + HEADER_OFFSET;}

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    public void addGroupType(GrouppType grouppType) {
        mGrouppType = grouppType;
        // Add the new GrouppType to the Array list
        mGrouppTypePickList.add(grouppType);
        // Via the Activity, tell the RecyclerView to scroll to the newly added GrouppType
        ((GrouppTypePickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
    }

    public void modifyGroupType(GrouppType modifiedGrouppType) {
        mGrouppType = modifiedGrouppType;
        // Find the position of the modified GrouppType in the collection
        int position = mGrouppTypePickList.indexOf(modifiedGrouppType);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old GrouppType with the modified GrouppType
            mGrouppTypePickList.set(position, modifiedGrouppType);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteGroupType(int position) {
        mGrouppTypePickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {return position == MyConstants.ZERO_I;}

    public void setGroupType(GrouppType grouppType) {mGrouppType = grouppType;}

    private void setGroupTypeList(ArrayList<GrouppType> grouppTypePickList) {mGrouppTypePickList = grouppTypePickList;}

    public ArrayList<GrouppType> getGroupTypePickList() {return mGrouppTypePickList;}

    public GrouppType getGroupType() {return mGrouppType;}

    public GrouppType getGroupType(int position) {return mGrouppTypePickList.get(position);}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}
}