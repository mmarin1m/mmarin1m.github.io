package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.DiverDiveGroupCylPickBinding;
import ca.myairbuddyandi.databinding.DiverDiveGroupCylPickHeaderBinding;

/**
 * Created by Michel on 2020-03/15.
 * Holds all of the logic for the DiverDiveGroupCylPickAdapter class
 */

public class DiverDiveGroupCylPickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "DiverDiveGroupCylPickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<DiverDiveGroupCyl> mDiverDiveGroupCylPickList;
    private Boolean mDescendingCylinderType = false;
    private Boolean mDescendingUsageType = false;
    private Boolean mDescendingBeginningPressure = false;
    private Boolean mDescendingEndingPressure = false;
    private final Context mContext;
    private DiverDiveGroupCyl mDiverDiveGroupCyl = new DiverDiveGroupCyl();

    // End of variables

    // Public constructor
    public DiverDiveGroupCylPickAdapter(Context context, ArrayList<DiverDiveGroupCyl> diverDiveGroupCylPickList) {
        mContext = context;
        mDiverDiveGroupCylPickList = diverDiveGroupCylPickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final DiverDiveGroupCylPickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindDiverDiveGroupCyl(DiverDiveGroupCyl diverDiveGroupCyl) { binding.setDiverDiveGroupCyl(diverDiveGroupCyl); }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            DiverDiveGroupCylPickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;

            binding.hdrCylinderType.setOnClickListener(view -> {
                // The user clicked on the Header Cylinder Type
                if (mDiverDiveGroupCylPickList.size() > 0) {
                    mDiverDiveGroupCylPickList.sort(new DiverDiveGroupCylPickComparatorCylinderType(mDescendingCylinderType));
                    mDescendingCylinderType = !mDescendingCylinderType;
                    setDiverDiveGroupCylPickList(mDiverDiveGroupCylPickList);
                    notifyDataSetChanged();
                }

            });

            binding.hdrUsageType.setOnClickListener(view -> {
                // The user clicked on the Header Usage Type
                if (mDiverDiveGroupCylPickList.size() > 0) {
                    mDiverDiveGroupCylPickList.sort(new DiverDiveGroupCylPickComparatorUsageType(mDescendingUsageType));
                    mDescendingUsageType = !mDescendingUsageType;
                    setDiverDiveGroupCylPickList(mDiverDiveGroupCylPickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrBeginningPressure.setOnClickListener(view -> {
                // The user clicked on the Header Beginning Pressure
                if (mDiverDiveGroupCylPickList.size() > 0) {
                    mDiverDiveGroupCylPickList.sort(new DiverDiveGroupCylPickComparatorBeginningPressure(mDescendingBeginningPressure));
                    mDescendingBeginningPressure = !mDescendingBeginningPressure;
                    setDiverDiveGroupCylPickList(mDiverDiveGroupCylPickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrEndingPressure.setOnClickListener(view -> {
                // The user clicked on the Header Ending Pressure
                if (mDiverDiveGroupCylPickList.size() > 0) {
                    mDiverDiveGroupCylPickList.sort(new DiverDiveGroupCylPickComparatorEndingPressure(mDescendingEndingPressure));
                    mDescendingEndingPressure = !mDescendingEndingPressure;
                    setDiverDiveGroupCylPickList(mDiverDiveGroupCylPickList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends DiverDiveGroupCylPickAdapter.ViewHolder {

        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            DiverDiveGroupCylPickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, DiverDiveGroupCylActivity.class);
                DiverDiveGroupCyl diverDiveGroupCyl = new DiverDiveGroupCyl();
                diverDiveGroupCyl.setDiverNo(mDiverDiveGroupCyl.getDiverNo());
                diverDiveGroupCyl.setDiveNo(mDiverDiveGroupCyl.getDiveNo());
                diverDiveGroupCyl.setUsageType(mDiverDiveGroupCyl.getUsageType());
                diverDiveGroupCyl.setGroupNo(mDiverDiveGroupCyl.getGroupNo());
                diverDiveGroupCyl.setLogBookNo(mDiverDiveGroupCyl.getLogBookNo());
                intent.putExtra(MyConstants.DIVER_DIVE_GROUP_CYLINDER, diverDiveGroupCyl);
                ((DiverDiveGroupCylPickActivity) mContext).editLauncher.launch(intent);
            });

            itemView.setOnClickListener(view -> {
                // Select a new Diver by changing item (row)
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                if (position >= MyConstants.ZERO_I) {
                    mDiverDiveGroupCyl = mDiverDiveGroupCylPickList.get(position);
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
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View pickDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.diver_dive_group_cyl_pick_header, parent, false);
            return new DiverDiveGroupCylPickAdapter.VHHeader(pickDiveAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.diver_dive_group_cyl_pick, parent, false);
            return new DiverDiveGroupCylPickAdapter.VHItem(pickDiveAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            DiverDiveGroupCyl dataObject = mDiverDiveGroupCylPickList.get(position - HEADER_OFFSET);

            DiverDiveGroupCylPickAdapter.ViewHolder itemView = (DiverDiveGroupCylPickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.diverDiveGroupCyl, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindDiverDiveGroupCyl(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mDiverDiveGroupCyl = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((DiverDiveGroupCylPickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mDiverDiveGroupCylPickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((DiverDiveGroupCylPickActivity) mContext).doSmoothScroll(mDiverDiveGroupCylPickList.size());
                }
            } else {
                ((DiverDiveGroupCylPickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDiverDiveGroupCylPickList.size() + HEADER_OFFSET;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    @SuppressLint("NotifyDataSetChanged")
    public void modifyDiverDiveGroupCyl(DiverDiveGroupCyl modifiedDiverDiveGroupCyl) {
        mDiverDiveGroupCyl = modifiedDiverDiveGroupCyl;
        // Find the position of the modified DiverDiveGroupCyl in the collection
        int position = mDiverDiveGroupCylPickList.indexOf(modifiedDiverDiveGroupCyl);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old DiverDiveGroupCyl with the modified DiverDiveGroupCyl
            mDiverDiveGroupCylPickList.set(position, modifiedDiverDiveGroupCyl);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
            notifyDataSetChanged();
            // Must sort to always show the Plans in the right order
            mExpandedPosition = -1;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == MyConstants.ZERO_I;
    }

    public void setDiverDiveGroupCyl (DiverDiveGroupCyl diverDiveGroupCyl) {mDiverDiveGroupCyl = diverDiveGroupCyl;}

    private void setDiverDiveGroupCylPickList(ArrayList<DiverDiveGroupCyl> diverDiveGroupCylPickList) {mDiverDiveGroupCylPickList = diverDiveGroupCylPickList;}

    public ArrayList<DiverDiveGroupCyl> getDiverDiveGroupCylPickList() {return mDiverDiveGroupCylPickList;}

    public DiverDiveGroupCyl getDiverDiveGroupCyl () {
        return mDiverDiveGroupCyl;
    }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}
}
