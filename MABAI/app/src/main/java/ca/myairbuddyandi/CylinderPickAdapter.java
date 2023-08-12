package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.CylinderPickBinding;
import ca.myairbuddyandi.databinding.CylinderPickHeaderBinding;

/**
 * Created by Michel on 2017-06-03.
 * * Holds all of the logic for the CylinderPickAdapter class
 */

public class CylinderPickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    // Static
    private static final String LOG_TAG = "CylinderPickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private ArrayList<CylinderPick> mCylinderPickList;
    private Boolean mDescendingNo = false;
    private Boolean mDescendingType = false;
    private Boolean mDescendingVolume = false;
    private Boolean mDescendingRatedPressure = false;
    private Boolean mDescendingDescription = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private final Context mContext;
    private CylinderPick mCylinderPick = new CylinderPick();
    private CylinderPickFilter mFilter;
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;

    // End of variables

    // Public constructor
    public CylinderPickAdapter(Context context, ArrayList<CylinderPick> cylinderPickList) {
        mContext = context;
        mCylinderPickList = cylinderPickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final CylinderPickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindCylinder(CylinderPick cylinderPick) {binding.setCylinderPick(cylinderPick);}

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            CylinderPickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrNo.setOnClickListener(view -> {
                // The user clicked on the Header No
                if (mCylinderPickList.size() > 0) {
                    mCylinderPickList.sort(new CylinderPickComparatorNo(mDescendingNo));
                    mDescendingNo = !mDescendingNo;
                    setCylinderPickList(mCylinderPickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrType.setOnClickListener(this::onClick);

            binding.hdrVolume.setOnClickListener(view -> {
                // The user clicked on the Header Volume
                if (mCylinderPickList.size() > 0) {
                    mCylinderPickList.sort(new CylinderPickComparatorVolume(mDescendingVolume));
                    mDescendingVolume = !mDescendingVolume;
                    setCylinderPickList(mCylinderPickList);
                    notifyDataSetChanged();
                }
            });

            // Only in Landscape mode
            if (!MyFunctions.getRotation(mContext).equals("portrait")) {
                assert binding.hdrRatedPressure != null;
                binding.hdrRatedPressure.setOnClickListener(view -> {
                    // The user clicked on the Header Rated Pressure (Landscape)
                    if (mCylinderPickList.size() > 0) {
                        mCylinderPickList.sort(new CylinderPickComparatorRatedPressure(mDescendingRatedPressure));
                        mDescendingRatedPressure = !mDescendingRatedPressure;
                        setCylinderPickList(mCylinderPickList);
                        notifyDataSetChanged();
                    }
                });
            }

            binding.hdrDescription.setOnClickListener(view -> {
                // The user clicked on the Header Description
                if (mCylinderPickList.size() > 0) {
                    mCylinderPickList.sort(new CylinderPickComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setCylinderPickList(mCylinderPickList);
                    notifyDataSetChanged();
                }
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        private void onClick(View view) {
            // The user clicked on the Header Type
            if (mCylinderPickList.size() > 0) {
                mCylinderPickList.sort(new CylinderPickComparatorType(mDescendingType));
                mDescendingType = !mDescendingType;
                setCylinderPickList(mCylinderPickList);
                notifyDataSetChanged();
            }
        }
    }

    private class VHItem extends CylinderPickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final ImageView letterCircle;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            CylinderPickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            letterCircle = binding.gmailItemLetter;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, CylinderActivity.class);
                Cylinder cylinder = new Cylinder();
                cylinder.setCylinderNo(mCylinderPick.getCylinderNo());
                intent.putExtra(MyConstants.CYLINDER, cylinder);
                ((CylinderPickActivity) mContext).setCylinderPick(mCylinderPick);
                ((CylinderPickActivity) mContext).editLauncher.launch(intent);
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((CylinderPickActivity) mContext).countCylinders(checkBox.isChecked());
                } else {
                    // Select a new Cylinder by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mCylinderPick = mCylinderPickList.get(position);
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
                        ((CylinderPickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickCylinderAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.cylinder_pick_header, parent, false);
            return new CylinderPickAdapter.VHHeader(pickCylinderAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickCylinderAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.cylinder_pick, parent, false);
            return new CylinderPickAdapter.VHItem(pickCylinderAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            CylinderPick dataObject = mCylinderPickList.get(position - HEADER_OFFSET);

            CylinderPickAdapter.ViewHolder itemView = (CylinderPickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.cylinderPick, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindCylinder(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mCylinderPick = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((CylinderPickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mCylinderPickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((CylinderPickActivity) mContext).doSmoothScroll(mCylinderPickList.size());
                }
            } else {
                ((CylinderPickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            // Get the first letter of list item
            String letter = String.valueOf(dataObject.getCylinderType().charAt(0));
            TextDrawable circle = TextDrawable.builder().buildRound(letter, mGenerator.getColor(dataObject.getCylinderNo()));
            ((VHItem) holder).letterCircle.setImageDrawable(circle);

            ((CylinderPickAdapter.VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((CylinderPickActivity) mContext).countCylinders(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {return mCylinderPickList.size() + HEADER_OFFSET;}

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public Filter getFilter() {
        if(mFilter==null)
        {
            mFilter = new CylinderPickFilter(mCylinderPickList,this);
        }
        return mFilter;
    }

    // My functions

    public void addCylinder(CylinderPick cylinderPick) {
        mCylinderPick = cylinderPick;
        // Add the new CylinderPick to the Array list
        mCylinderPickList.add(cylinderPick);
        // Via the Activity, tell the RecyclerView to scroll to the newly added CylinderPick
        ((CylinderPickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
    }

    public void modifyCylinder(CylinderPick modifiedCylinderPick) {
        mCylinderPick = modifiedCylinderPick;
        // Find the position of the modified CylinderPick in the collection
        int position = mCylinderPickList.indexOf(modifiedCylinderPick);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old CylinderPick with the modified CylinderPick
            mCylinderPickList.set(position, modifiedCylinderPick);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteCylinder(int position) {
        mCylinderPickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {
        return position == MyConstants.ZERO_I;
    }

    public void setCylinderPick(CylinderPick cylinderPick) {mCylinderPick = cylinderPick;}

    public void setCylinderPickList(ArrayList<CylinderPick> cylinderPickList) {mCylinderPickList = cylinderPickList;}

    public ArrayList<CylinderPick> getCylinderPickList() {return mCylinderPickList;}

    public CylinderPick getCylinderPick() {return mCylinderPick;}

    public CylinderPick getCylinderPick(int position) {
        return mCylinderPickList.get(position);
    }

    public void setSelectedPosition(int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode(Boolean inMultiEditMode) {
        mInMultiEditMode = inMultiEditMode;
    }

    public int getCylinderPickPosition(CylinderPick cylinderPick) { return mCylinderPickList.indexOf(cylinderPick); }
}
