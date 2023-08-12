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

import ca.myairbuddyandi.databinding.CylinderTypePickBinding;
import ca.myairbuddyandi.databinding.CylinderTypePickHeaderBinding;

/**
 * Created by Michel on 2016-12-15.
 * Holds all of the logic for the CylinderTypePickAdapter class
 */

public class CylinderTypePickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "CylinderTypePickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private ArrayList<CylinderType> mCylinderTypePickList;
    private Boolean mDescendingType = false;
    private Boolean mDescendingDescription = false;
    private Boolean mDescendingDives = false;
    private Boolean mDescendingVolume = false;
    private Boolean mDescendingRatedPressure = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private CylinderType mCylinderType = new CylinderType();
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;

    // End of variables

    // Public constructor
    public CylinderTypePickAdapter(Context context, ArrayList<CylinderType> cylinderTypePickList) {
        mContext = context;
        mCylinderTypePickList = cylinderTypePickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final CylinderTypePickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindCylinderType(CylinderType cylinderType) {
            binding.setCylinderType(cylinderType);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            CylinderTypePickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrType.setOnClickListener(view -> {
                // The user clicked on the Header Log Book No
                if (mCylinderTypePickList.size() > 0) {
                    mCylinderTypePickList.sort(new CylinderTypeComparatorType(mDescendingType));
                    mDescendingType = !mDescendingType;
                    setCylinderTypeList(mCylinderTypePickList);
                    notifyDataSetChanged();
                }

            });

            binding.hdrDescription.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mCylinderTypePickList.size() > 0) {
                    mCylinderTypePickList.sort(new CylinderTypeComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setCylinderTypeList(mCylinderTypePickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrVolume.setOnClickListener(view -> {
                // The user clicked on the Header Status
                if (mCylinderTypePickList.size() > 0) {
                    mCylinderTypePickList.sort(new CylinderTypeComparatorVolume(mDescendingVolume));
                    mDescendingVolume = !mDescendingVolume;
                    setCylinderTypeList(mCylinderTypePickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrRatedPressure.setOnClickListener(view -> {
                // The user clicked on the Header My Buddy
                if (mCylinderTypePickList.size() > 0) {
                    mCylinderTypePickList.sort(new CylinderTypeComparatorRatedPressure(mDescendingRatedPressure));
                    mDescendingRatedPressure = !mDescendingRatedPressure;
                    setCylinderTypeList(mCylinderTypePickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrDives.setOnClickListener(view -> {
                // The user clicked on the Header My Buddy
                if (mCylinderTypePickList.size() > 0) {
                    mCylinderTypePickList.sort(new CylinderTypeComparatorDives(mDescendingDives));
                    mDescendingDives = !mDescendingDives;
                    setCylinderTypeList(mCylinderTypePickList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends CylinderTypePickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            CylinderTypePickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, CylinderTypeActivity.class);
                CylinderType cylinderType;
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                if (position >= MyConstants.ZERO_I) {
                    cylinderType = mCylinderTypePickList.get(position);
                    intent.putExtra(MyConstants.CYLINDER_TYPE, cylinderType);
                    ((CylinderTypePickActivity) mContext).setCylinderType(cylinderType);
                    ((CylinderTypePickActivity) mContext).editLauncher.launch(intent);
                }
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((CylinderTypePickActivity) mContext).countCylinderTypes(checkBox.isChecked());
                } else {
                    // Select a new CylinderType by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mCylinderType = mCylinderTypePickList.get(position);
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
                        ((CylinderTypePickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickCylinderTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.cylinder_type_pick_header, parent, false);
            return new CylinderTypePickAdapter.VHHeader(pickCylinderTypeAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickCylinderTypeAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.cylinder_type_pick, parent, false);
            return new CylinderTypePickAdapter.VHItem(pickCylinderTypeAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CylinderTypePickAdapter.VHItem) {
            CylinderType dataObject = mCylinderTypePickList.get(position - HEADER_OFFSET);

            CylinderTypePickAdapter.ViewHolder itemView = (CylinderTypePickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.cylinderType, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindCylinderType(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mCylinderType = dataObject;
            }

            // Set the expanded area
            if (position >= MyConstants.ZERO_I && position == mExpandedPosition) {
                ((CylinderTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mCylinderTypePickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((CylinderTypePickActivity) mContext).doSmoothScroll(mCylinderTypePickList.size());
                }
            } else {
                ((CylinderTypePickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            ((CylinderTypePickAdapter.VHItem) holder).checkBox.setOnClickListener((View v) -> {
                CheckBox checkbox = (CheckBox) v;
                ((CylinderTypePickActivity) mContext).countCylinderTypes(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCylinderTypePickList.size() + HEADER_OFFSET;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    public void addCylinderType(CylinderType cylinderType) {
        mCylinderType = cylinderType;
        // Add the new CylinderType to the Array list
        mCylinderTypePickList.add(cylinderType);
        // Via the Activity, tell the RecyclerView to scroll to the newly added CylinderType
        ((CylinderTypePickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
    }

    public void modifyCylinderType(CylinderType modifiedCylinderType) {
        mCylinderType = modifiedCylinderType;
        // Find the position of the modified CylinderType in the collection
        int position = mCylinderTypePickList.indexOf(modifiedCylinderType);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old CylinderType with the modified CylinderType
            mCylinderTypePickList.set(position, modifiedCylinderType);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteCylinderType(int position) {
        mCylinderTypePickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {
        return position == MyConstants.ZERO_I;
    }

    public void setCylinderType (CylinderType cylinderType) {mCylinderType = cylinderType;}

    private void setCylinderTypeList(ArrayList<CylinderType> cylinderTypePickList) {mCylinderTypePickList = cylinderTypePickList;}

    public ArrayList<CylinderType> getCylinderTypePickList() {return mCylinderTypePickList;}

    public CylinderType getCylinderType () {
        return mCylinderType;
    }

    public CylinderType getCylinderType (int position) {return mCylinderTypePickList.get(position);}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {
        mInMultiEditMode = inMultiEditMode;
    }
}