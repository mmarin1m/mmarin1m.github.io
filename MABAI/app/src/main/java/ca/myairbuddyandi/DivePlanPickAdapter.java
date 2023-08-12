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

import ca.myairbuddyandi.databinding.DivePlanPickBinding;
import ca.myairbuddyandi.databinding.DivePlanPickHeaderBinding;

/**
 * Created by Michel on 2017-06-03.
 * Holds all of the logic for the DivePlanPickAdapter class
 */

public class DivePlanPickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "DivePlanPickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<DivePlan> mDivePlanPickList;
    private Boolean mDescendingOrderNo = false;
    private Boolean mDescendingDepth = false;
    private Boolean mDescendingBottomTime = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private DivePlan mDivePlan = new DivePlan();

    // End of variables

    // Public constructor
    public DivePlanPickAdapter(Context context, ArrayList<DivePlan> divePlanPickList) {
        mContext = context;
        mDivePlanPickList = divePlanPickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final DivePlanPickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindDivePlan(DivePlan divePlan) {
            binding.setDivePlan(divePlan);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            DivePlanPickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrOrderNo.setOnClickListener(view -> {
                // The user clicked on the Header Log Book No
                if (mDivePlanPickList.size() > 0) {
                    sortByOrderNo(mDescendingOrderNo);
                }

            });

            binding.hdrDepth.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mDivePlanPickList.size() > 0) {
                    mDivePlanPickList.sort(new DivePlanComparatorDepth(mDescendingDepth));
                    mDescendingDepth = !mDescendingDepth;
                    setDivePlanPickList(mDivePlanPickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrBottomMinute.setOnClickListener(this::onClick);
        }

        @SuppressLint("NotifyDataSetChanged")
        private void onClick(View view) {
            // The user clicked on the Header Status
            if (mDivePlanPickList.size() > 0) {
                mDivePlanPickList.sort(new DivePlanComparatorBottomTime(mDescendingBottomTime));
                mDescendingBottomTime = !mDescendingBottomTime;
                setDivePlanPickList(mDivePlanPickList);
                notifyDataSetChanged();
            }
        }
    }

    private class VHItem extends DivePlanPickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            DivePlanPickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, DivePlanActivity.class);
                DivePlan divePlan = new DivePlan();
                divePlan.setDivePlanNo(mDivePlan.getDivePlanNo());
                divePlan.setLogBookNo(mDivePlan.getLogBookNo());
                intent.putExtra(MyConstants.DIVE_PLAN, divePlan);
                ((DivePlanPickActivity) mContext).editLauncher.launch(intent);
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((DivePlanPickActivity) mContext).countDivePlans(checkBox.isChecked());
                } else {
                    // Select a new Dive Plan by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mDivePlan = mDivePlanPickList.get(position);
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
                        ((DivePlanPickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_plan_pick_header, parent, false);
            return new DivePlanPickAdapter.VHHeader(pickDiveAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_plan_pick, parent, false);
            return new DivePlanPickAdapter.VHItem(pickDiveAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            DivePlan dataObject = mDivePlanPickList.get(position - HEADER_OFFSET);

            DivePlanPickAdapter.ViewHolder itemView = (DivePlanPickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.divePlan, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindDivePlan(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mDivePlan = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((DivePlanPickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mDivePlanPickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((DivePlanPickActivity) mContext).doSmoothScroll(mDivePlanPickList.size());
                }
            } else {
                ((DivePlanPickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            ((VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((DivePlanPickActivity) mContext).countDivePlans(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDivePlanPickList.size() + HEADER_OFFSET;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    public void addDivePlan(DivePlan divePlan) {
        mDivePlan = divePlan;
        // Add the new DivePlan to the Array list
        mDivePlanPickList.add(divePlan);
        // Via the Activity, tell the RecyclerView to scroll to the newly added DivePlan
        ((DivePlanPickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
        // Must sort to always show the Plans in the right order
        sortByOrderNo(false);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void modifyDivePlan(DivePlan modifiedDivePlan) {
        mDivePlan = modifiedDivePlan;
        // Find the position of the modified DivePlan in the collection
        int position = mDivePlanPickList.indexOf(modifiedDivePlan);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old DivePlan with the modified DivePlan
            mDivePlanPickList.set(position, modifiedDivePlan);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
            notifyDataSetChanged();
            // Must sort to always show the Plans in the right order
            mExpandedPosition = -1;
            sortByOrderNo(false);
        }
    }

    public void deleteDivePlan(int position) {
        mDivePlanPickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {
        return position == MyConstants.ZERO_I;
    }

    public void setDivePlan (DivePlan divePlan) {mDivePlan = divePlan;}

    private void setDivePlanPickList(ArrayList<DivePlan> divePlanPickList) {mDivePlanPickList = divePlanPickList;}

    public ArrayList<DivePlan> getDivePlanPickList() {return mDivePlanPickList;}

    public DivePlan getDivePlan () {
        return mDivePlan;
    }

    public DivePlan getDivePlan(int position) {
        return mDivePlanPickList.get(position);
    }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {
        mInMultiEditMode = inMultiEditMode;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortByOrderNo(Boolean descendingOrderNo) {
        mDivePlanPickList.sort(new DivePlanComparatorOrderNo(descendingOrderNo));
        mDescendingOrderNo = !descendingOrderNo;
        setDivePlanPickList(mDivePlanPickList);
        notifyDataSetChanged();
    }
}
