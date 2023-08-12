package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.GrouppCylinderDataBinding;
import ca.myairbuddyandi.databinding.GrouppCylinderHeaderBinding;

/**
 * Created by Michel on 2017-06-03.
 * Holds all of the logic for the GrouppCylinderAdapter class
 * AKA Adapter on the Edit an Equipment Group
 */

public class GrouppCylinderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "GrouppCylinderAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private final ArrayAdapter<UsageType> mAdapterUsageType;
    private ArrayList<GrouppCylinder> mGrouppCylinderList;
    private Boolean mDescendingNo = false;
    private Boolean mDescendingType = false;
    private Boolean mDescendingVolume = false;
    private Boolean mDescendingRatedPressure = false;
    private Boolean mDescendingUsage = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private final ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private GrouppCylinder mGrouppCylinder = new GrouppCylinder();
    private Spinner mSpinner;

    // End of variables

    // Public constructor
    public GrouppCylinderAdapter(Context context, ArrayList<GrouppCylinder> grouppCylinderList) {
        mContext = context;
        mGrouppCylinderList = grouppCylinderList;

        // Set the data for the UsageType adapter
        AirDA airDa = new AirDA(context);
        airDa.open();
        ArrayList<UsageType> usageTypeList = airDa.getAllUsageTypes();
        mAdapterUsageType = new ArrayAdapter<>(mContext,R.layout.support_simple_spinner_dropdown_item, usageTypeList);
        mAdapterUsageType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final GrouppCylinderDataBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, CylinderActivity.class);
                Cylinder cylinder = new Cylinder();
                cylinder.setCylinderNo(mGrouppCylinder.getCylinderNo());
                cylinder.setUsageType(mGrouppCylinder.getUsageType());
                cylinder.setIsNew(mGrouppCylinder.getIsNew());
                intent.putExtra(MyConstants.CYLINDER, cylinder);
                ((GrouppActivity) mContext).setGrouppCylinder(mGrouppCylinder);
                ((GrouppActivity) mContext).editLauncher.launch(intent);
            });
        }

        void bindGrouppCylinder(GrouppCylinder grouppCylinder) {binding.setGrouppCylinder(grouppCylinder);}

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            GrouppCylinderHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrNo.setOnClickListener(view -> {
                // The user clicked on the Header No
                if (mGrouppCylinderList.size() > 0) {
                    mGrouppCylinderList.sort(new GrouppCylinderComparatorNo(mDescendingNo));
                    mDescendingNo = !mDescendingNo;
                    setGrouppCylinderList(mGrouppCylinderList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrType.setOnClickListener(view -> {
                // The user clicked on the Header Type
                if (mGrouppCylinderList.size() > 0) {
                    mGrouppCylinderList.sort(new GrouppCylinderComparatorType(mDescendingType));
                    mDescendingType = !mDescendingType;
                    setGrouppCylinderList(mGrouppCylinderList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrVolume.setOnClickListener(view -> {
                // The user clicked on the Header Volume
                if (mGrouppCylinderList.size() > 0) {
                    mGrouppCylinderList.sort(new GrouppCylinderComparatorVolume(mDescendingVolume));
                    mDescendingVolume = !mDescendingVolume;
                    setGrouppCylinderList(mGrouppCylinderList);
                    notifyDataSetChanged();
                }
            });

            // Only in Landscape mode
            if (!MyFunctions.getRotation(mContext).equals("portrait")) {
                assert binding.hdrRatedPressure != null;
                binding.hdrRatedPressure.setOnClickListener(view -> {
                    // The user clicked on the Header Rated Pressure (Landscape)
                    if (mGrouppCylinderList.size() > 0) {
                        mGrouppCylinderList.sort(new GrouppCylinderComparatorRatedPressure(mDescendingRatedPressure));
                        mDescendingRatedPressure = !mDescendingRatedPressure;
                        setGrouppCylinderList(mGrouppCylinderList);
                        notifyDataSetChanged();
                    }
                });
            }

            binding.hdrUsage.setOnClickListener(view -> {
                // The user clicked on the Header Usage
                if (mGrouppCylinderList.size() > 0) {
                    mGrouppCylinderList.sort(new GrouppCylinderComparatorUsage(mDescendingUsage));
                    mDescendingUsage = !mDescendingUsage;
                    setGrouppCylinderList(mGrouppCylinderList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends GrouppCylinderAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final ImageView letterCircle;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {
            super(itemView);

            GrouppCylinderDataBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            letterCircle = binding.gmailItemLetter;
            mSpinner = binding.spinnerUsage;
            expandedArea = binding.expandArea;

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((GrouppActivity) mContext).countGrouppCylinder(checkBox.isChecked());
                } else {
                    // Select a new Cylinder by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mGrouppCylinder = mGrouppCylinderList.get(position);
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
                        ((GrouppActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View grouppCylinderAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupp_cylinder_header, parent, false);
            return new GrouppCylinderAdapter.VHHeader(grouppCylinderAdapter);
        } else if (viewType == TYPE_ITEM) {
            View grouppCylinderAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupp_cylinder_data, parent, false);
            return new GrouppCylinderAdapter.VHItem(grouppCylinderAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            GrouppCylinder dataObject = mGrouppCylinderList.get(position - HEADER_OFFSET);

            GrouppCylinderAdapter.ViewHolder itemView = (GrouppCylinderAdapter.ViewHolder)  holder;

            mSpinner.setAdapter(mAdapterUsageType);

            itemView.getBinding().setVariable(BR.grouppCylinder, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindGrouppCylinder(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mGrouppCylinder = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((GrouppCylinderAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mGrouppCylinderList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((GrouppActivity) mContext).doSmoothScroll(mGrouppCylinderList.size());
                }
            } else {
                ((GrouppCylinderAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            // Get the first letter of list item
            String letter = String.valueOf(dataObject.getCylinderType().charAt(0));
            TextDrawable circle = TextDrawable.builder().buildRound(letter, mGenerator.getColor(dataObject.getCylinderNo()));
            ((VHItem) holder).letterCircle.setImageDrawable(circle);
        }
    }

    @Override
    public int getItemCount() {return mGrouppCylinderList.size() + HEADER_OFFSET;}

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    public void addGrouppCylinder(GrouppCylinder grouppCylinder) {
        mGrouppCylinder = grouppCylinder;
        // Add the new GrouppCylinder to the Array list
        mGrouppCylinderList.add(grouppCylinder);
        // Via the Activity, tell the RecyclerView to scroll to the newly added GrouppCylinder
        ((GrouppActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
    }

    public void modifyGrouppCylinder(GrouppCylinder modifiedGrouppCylinder) {
        mGrouppCylinder = modifiedGrouppCylinder;
        // Find the position of the modified GrouppCylinder in the collection
        int position = mGrouppCylinderList.indexOf(modifiedGrouppCylinder);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old GrouppCylinder with the modified GrouppCylinder
            mGrouppCylinderList.set(position, modifiedGrouppCylinder);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteGroupCylinder(int position) {
        mGrouppCylinderList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {return position == MyConstants.ZERO_I;}

    public void setGrouppCylinder(GrouppCylinder grouppCylinder) {mGrouppCylinder = grouppCylinder;}

    private void setGrouppCylinderList(ArrayList<GrouppCylinder> grouppCylinderPickList) {mGrouppCylinderList = grouppCylinderPickList;}

    public ArrayList<GrouppCylinder> getGrouppCylinderList() {return mGrouppCylinderList;}

    public void setGrouppCylinderListPosition(ArrayList<Integer> grouppCylinderListPosition) {
        for (int i=0;i<mGrouppCylinderList.size();i++) {
            GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
            grouppCylinder.setUsageTypePosition(grouppCylinderListPosition.get(i));
        }
    }

    public GrouppCylinder getGrouppCylinder() {return mGrouppCylinder;}

    public GrouppCylinder getGrouppCylinder(int position) {return mGrouppCylinderList.get(position);}

    public ArrayList<Integer> getGrouppCylinderListPosition() {
        ArrayList<Integer> grouppCylinderListPosition = new ArrayList<>();
        for (int i=0;i<mGrouppCylinderList.size();i++)
        {
            GrouppCylinder grouppCylinder = mGrouppCylinderList.get(i);
            grouppCylinderListPosition.add(grouppCylinder.getUsageTypeIndex(grouppCylinder.getUsageType()));
        }
        return grouppCylinderListPosition;
    }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}
}
