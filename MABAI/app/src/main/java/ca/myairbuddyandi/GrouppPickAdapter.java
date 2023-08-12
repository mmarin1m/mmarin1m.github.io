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

import ca.myairbuddyandi.databinding.GrouppPickBinding;
import ca.myairbuddyandi.databinding.GrouppPickHeaderBinding;

/**
 * Created by Michel on 2017-06-03.
 * Holds all the logic for the GrouppPickAdapter class
 */

public class GrouppPickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    // Static
    private static final String LOG_TAG = "GrouppPickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<GrouppPick> mGrouppPickList;
    private Boolean mDescendingType = false;
    private Boolean mDescendingDescription = false;
    private Boolean mDescendingDives = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private final ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private GrouppPick mGrouppPick = new GrouppPick();
    private GrouppPickFilter mFilter;

    // End of variables

    // Public constructor
    public GrouppPickAdapter(Context context, ArrayList<GrouppPick> grouppPickList) {
        mContext = context;
        mGrouppPickList = grouppPickList;

        // Set the data for the GrouppType adapter
        AirDA mAirDa = new AirDA(context);
        mAirDa.openWithFKConstraintsEnabled();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final GrouppPickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindGroupp(GrouppPick grouppPick) {
            binding.setGrouppPick(grouppPick);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            GrouppPickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrType.setOnClickListener(view -> {
                // The user clicked on the Header Type
                if (mGrouppPickList.size() > 0) {
                    mGrouppPickList.sort(new GrouppPickComparatorType(mDescendingType));
                    mDescendingType = !mDescendingType;
                    setGrouppPickList(mGrouppPickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrDesc.setOnClickListener(view -> {
                // The user clicked on the Header Description
                if (mGrouppPickList.size() > 0) {
                    mGrouppPickList.sort(new GrouppPickComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setGrouppPickList(mGrouppPickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrDives.setOnClickListener(view -> {
                // The user clicked on the Header Dives
                if (mGrouppPickList.size() > 0) {
                    mGrouppPickList.sort(new GrouppPickComparatorDives(mDescendingDives));
                    mDescendingDives = !mDescendingDives;
                    setGrouppPickList(mGrouppPickList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends GrouppPickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final ImageView letterCircle;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            GrouppPickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            letterCircle = binding.gmailItemLetter;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, GrouppActivity.class);
                Groupp groupp = new Groupp();
                groupp.setGroupNo(mGrouppPick.getGroupNo());
                groupp.setDiveNo(mGrouppPick.getDiveNo());
                groupp.setLogBookNo((mGrouppPick.getLogBookNo()));
                intent.putExtra(MyConstants.GROUPP, groupp);
                ((GrouppPickActivity) mContext).setGrouppPick(mGrouppPick);
                ((GrouppPickActivity) mContext).editLauncher.launch(intent);
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((GrouppPickActivity) mContext).countGroupps(checkBox.isChecked());
                } else {
                    // Select a new GrouppPick by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mGrouppPick = mGrouppPickList.get(position);
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
                        ((GrouppPickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickGrouppAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupp_pick_header, parent, false);
            return new GrouppPickAdapter.VHHeader(pickGrouppAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickGrouppAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupp_pick, parent, false);
            return new GrouppPickAdapter.VHItem(pickGrouppAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {

            GrouppPick dataObject = mGrouppPickList.get(position - HEADER_OFFSET);

            GrouppPickAdapter.ViewHolder itemView = (GrouppPickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.grouppPick, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindGroupp(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mGrouppPick = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((GrouppPickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mGrouppPickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((GrouppPickActivity) mContext).doSmoothScroll(mGrouppPickList.size());
                }
            } else {
                ((GrouppPickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            // Get the first letter of list item
            String letter = String.valueOf(dataObject.getDescription().charAt(0));
            TextDrawable circle = TextDrawable.builder().buildRound(letter, mGenerator.getColor(dataObject.getGroupNo()));
            ((VHItem) holder).letterCircle.setImageDrawable(circle);

            ((GrouppPickAdapter.VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((GrouppPickActivity) mContext).countGroupps(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {return mGrouppPickList.size() + HEADER_OFFSET;}

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
            mFilter = new GrouppPickFilter(mGrouppPickList,this);
        }
        return mFilter;
    }

    // My functions

    public void addGroupp(GrouppPick grouppPick) {
        mGrouppPick = grouppPick;
        // Add the new GrouppPick to the Array list
        mGrouppPickList.add(grouppPick);
        // Via the Activity, tell the RecyclerView to scroll to the newly added GrouppPick
        ((GrouppPickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
    }

    public void modifyGroupp(GrouppPick modifiedGrouppPick) {
        mGrouppPick = modifiedGrouppPick;
        // Find the position of the modified GrouppPick in the collection
        int position = mGrouppPickList.indexOf(modifiedGrouppPick);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old GrouppPick with the modified GrouppPick
            mGrouppPickList.set(position, modifiedGrouppPick);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteGroupPick(int position) {
        mGrouppPickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {return position == MyConstants.ZERO_I;}

    public void setGrouppPick (GrouppPick grouppPick) {mGrouppPick = grouppPick;}

    public void setGrouppPickList(ArrayList<GrouppPick> grouppPickList) {mGrouppPickList = grouppPickList;}

    public ArrayList<GrouppPick> getGroupPickList() {return mGrouppPickList;}

    public GrouppPick getGroupPick() {return mGrouppPick;}

    public GrouppPick getGroupPick(int position) {return mGrouppPickList.get(position);}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean mInMultiEditMode) {this.mInMultiEditMode = mInMultiEditMode;}

    public Boolean getInMultiEditMode () {return this.mInMultiEditMode;}

    public int getGroupPickPosition(GrouppPick grouppPick) { return mGrouppPickList.indexOf(grouppPick); }
}
