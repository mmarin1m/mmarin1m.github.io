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

import ca.myairbuddyandi.databinding.UsageTypePickBinding;
import ca.myairbuddyandi.databinding.UsageTypePickHeaderBinding;

/**
 * Created by Michel on 2016-12-15.
 * Holds all the logic for the UsageTypePickAdapter class
 */

public class UsageTypePickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "UsageTypePickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<UsageType> mUsageTypePickList;
    private Boolean mDescendingDescription = false;
    private Boolean mDescendingType = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private UsageType mUsageType = new UsageType();

    // End of variables

    // Public constructor
    public UsageTypePickAdapter(Context context, ArrayList<UsageType> usageTypePickList) {
        mContext = context;
        mUsageTypePickList = usageTypePickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final UsageTypePickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindUsageType(UsageType usageType) {
            binding.setUsageType(usageType);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            UsageTypePickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrType.setOnClickListener(view -> {
                // The user clicked on the Header Log Book No
                if (mUsageTypePickList.size() > 0) {
                    mUsageTypePickList.sort(new UsageTypeComparatorType(mDescendingType));
                    mDescendingType = !mDescendingType;
                    setUsageTypeList(mUsageTypePickList);
                    notifyDataSetChanged();
                }

            });

            binding.hdrDescription.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mUsageTypePickList.size() > 0) {
                    mUsageTypePickList.sort(new UsageTypeComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setUsageTypeList(mUsageTypePickList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends UsageTypePickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            UsageTypePickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, UsageTypeActivity.class);
                UsageType usageType;
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                if (position >= MyConstants.ZERO_I) {
                    usageType = mUsageTypePickList.get(position);
                    intent.putExtra(MyConstants.USAGE_TYPE, usageType);
                    ((UsageTypePickActivity) mContext).setUsageType(usageType);
                    ((UsageTypePickActivity) mContext).editLauncher.launch(intent);
                }
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((UsageTypePickActivity) mContext).countUsageTypes(checkBox.isChecked());
                } else {
                    // Select a new UsageType by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mUsageType = mUsageTypePickList.get(position);
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
                        ((UsageTypePickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickUsageTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.usage_type_pick_header, parent, false);
            return new UsageTypePickAdapter.VHHeader(pickUsageTypeAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickUsageTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.usage_type_pick, parent, false);
            return new UsageTypePickAdapter.VHItem(pickUsageTypeAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof UsageTypePickAdapter.VHItem) {
            UsageType dataObject = mUsageTypePickList.get(position - HEADER_OFFSET);

            UsageTypePickAdapter.ViewHolder itemView = (UsageTypePickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.usageType, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindUsageType(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mUsageType = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((UsageTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mUsageTypePickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((UsageTypePickActivity) mContext).doSmoothScroll(mUsageTypePickList.size());
                }
            } else {
                ((UsageTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            ((UsageTypePickAdapter.VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((UsageTypePickActivity) mContext).countUsageTypes(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUsageTypePickList.size() + HEADER_OFFSET;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    public void addUsageType(UsageType usageType) {
        mUsageType = usageType;
        // Add the new UsageType to the Array list
        mUsageTypePickList.add(usageType);
        // Via the Activity, tell the RecyclerView to scroll to the newly added UsageType
        ((UsageTypePickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
    }

    public void modifyUsageType(UsageType modifiedUsageType) {
        mUsageType = modifiedUsageType;
        // Find the position of the modified UsageType in the collection
        int position = mUsageTypePickList.indexOf(modifiedUsageType);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old UsageType with the modified UsageType
            mUsageTypePickList.set(position, modifiedUsageType);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteUsageType(int position) {
        mUsageTypePickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {return position == MyConstants.ZERO_I;}

    public void setUsageType (UsageType usageType) {mUsageType = usageType;}

    private void setUsageTypeList(ArrayList<UsageType> usageTypePickList) {mUsageTypePickList = usageTypePickList;}

    public ArrayList<UsageType> getUsageTypePickList() {return mUsageTypePickList;}

    public UsageType getUsageType () {return mUsageType;}

    public UsageType getUsageType (int position) {return mUsageTypePickList.get(position);}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}
}