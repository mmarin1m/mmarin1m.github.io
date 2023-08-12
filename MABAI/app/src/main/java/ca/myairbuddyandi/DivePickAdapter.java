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
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.DivePickBinding;
import ca.myairbuddyandi.databinding.DivePickHeaderBinding;

/**
 * Created by Michel on 2017-06-03.
 * Holds all of the logic for the DivePickAdapter class
 */

class DivePickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    // Static
    private static final String LOG_TAG = "DivePickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<DivePick> mDivePickList;
    private Boolean mDescendingDate = false;
    private Boolean mDescendingDiveSite = false;
    private Boolean mDescendingGroup = false;
    private Boolean mDescendingLocation = false;
    private Boolean mDescendingLogBookNo = false;
    private Boolean mDescendingMyBuddy = false;
    private Boolean mDescendingStatus = false;
    private Boolean mDescendingType = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private final Context mContext;
    private DivePick mDivePick = new DivePick();
    private DivePickFilter mFilter;
    private State mState = null;

    // End of variables

    // Public constructor
    public DivePickAdapter(Context context, ArrayList<DivePick> divePickList) {
        mContext = context;
        mDivePickList = divePickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final DivePickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindDivePick(DivePick divePick) {
            binding.setDivePick(divePick);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            DivePickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrLogBookNo.setOnClickListener(view -> {
                // The user clicked on the Header Log Book No (LBN)
                if (mDivePickList.size() > 0) {
                    mDivePickList.sort(new DivePickComparatorLogBookNo(mDescendingLogBookNo));
                    mDescendingLogBookNo = !mDescendingLogBookNo;
                    setDivePickList(mDivePickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrDate.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mDivePickList.size() > 0) {
                    mDivePickList.sort(new DivePickComparatorDate(mDescendingDate));
                    mDescendingDate = !mDescendingDate;
                    setDivePickList(mDivePickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrStatus.setOnClickListener(view -> {
                // The user clicked on the Header Status
                if (mDivePickList.size() > 0) {
                    mDivePickList.sort(new DivePickComparatorStatus(mDescendingStatus));
                    mDescendingStatus = !mDescendingStatus;
                    setDivePickList(mDivePickList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrMyBuddy.setOnClickListener(view -> {
                // The user clicked on the Header My Buddy
                if (mDivePickList.size() > 0) {
                    mDivePickList.sort(new DivePickComparatorMyBuddy(mDescendingMyBuddy));
                    mDescendingMyBuddy = !mDescendingMyBuddy;
                    setDivePickList(mDivePickList);
                    notifyDataSetChanged();
                }
            });

            // NOTE: Leave as is
            assert binding.hdrLocation != null;
            binding.hdrLocation.setOnClickListener(view -> {
                // The user clicked on Location
                if (mDivePickList.size() > 0) {
                    mDivePickList.sort(new DivePickComparatorLocation(mDescendingLocation));
                    mDescendingLocation = !mDescendingLocation;
                    setDivePickList(mDivePickList);
                    notifyDataSetChanged();
                }
            });

            // NOTE: Leave as is
            assert binding.hdrDiveSite != null;
            binding.hdrDiveSite.setOnClickListener(view -> {
                // The user clicked on Dive Site
                if (mDivePickList.size() > 0) {
                    mDivePickList.sort(new DivePickComparatorDiveSite(mDescendingDiveSite));
                    mDescendingDiveSite = !mDescendingDiveSite;
                    setDivePickList(mDivePickList);
                    notifyDataSetChanged();
                }
            });

            // Only in Landscape mode
            if (MyFunctions.getRotation(mContext).equals("landscape")) {
                assert binding.hdrTypeDesc != null;
                binding.hdrTypeDesc.setOnClickListener(view -> {
                    // The user clicked on the Header Type
                    if (mDivePickList.size() > 0) {
                        mDivePickList.sort(new DivePickComparatorTypeDesc(mDescendingType));
                        mDescendingType = !mDescendingType;
                        setDivePickList(mDivePickList);
                        notifyDataSetChanged();
                    }
                });
            }

            // Only in Landscape mode
            if (MyFunctions.getRotation(mContext).equals("landscape")) {
                assert binding.hdrGroupDesc != null;
                binding.hdrGroupDesc.setOnClickListener(view -> {
                    // The user clicked on the Header Group
                    if (mDivePickList.size() > 0) {
                        mDivePickList.sort(new DivePickComparatorGroupDesc(mDescendingGroup));
                        mDescendingGroup = !mDescendingGroup;
                        setDivePickList(mDivePickList);
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }

    private class VHItem extends DivePickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final ImageView letterCircle;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {
            super(itemView);

            DivePickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            letterCircle = binding.gmailItemLetter;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, DiveActivity.class);
                Dive dive = new Dive();
                dive.setContext(mContext);
                dive.setDiveNo(mDivePick.getDiveNo());
                dive.setLogBookNo(mDivePick.getLogBookNo());
                intent.putExtra(MyConstants.DIVE, dive);
                intent.putExtra(MyConstants.STATE, mState);
                ((DivePickActivity) mContext).editLauncher.launch(intent);
            });

            binding.turnaroundIcon.setOnClickListener(view -> {
                // Call the Turnaround Activity
                // Check if the Dive has at least one Dive Plan
                AirDA airDa = new AirDA(mContext);
                airDa.open();
                int planCount = airDa.getPlanCount(mDivePick.getDiveNo());
                airDa.close();
                // Checks if any dives are checked to be compare
                // Use the 3 first one
                int compareCount = 0;
                long diveNo1 = MyConstants.ZERO_L;
                long diveNo2 = MyConstants.ZERO_L;
                long diveNo3 = MyConstants.ZERO_L;
                // Need to scroll through the RecyclerView and select first 3 selected dives
                for (int i=0;i<mDivePickList.size();i++) {
                    DivePick divePick = mDivePickList.get(i);
                    if (divePick.getCheckedCompare()) {
                        compareCount++;
                        if (compareCount == 1) {
                            diveNo1 = divePick.getDiveNo();
                        } else if (compareCount == 2) {
                            diveNo2 = divePick.getDiveNo();
                        } else if (compareCount == 3) {
                            diveNo3 = divePick.getDiveNo();
                        }
                    }
                }
                //
                if (planCount == MyConstants.ZERO_L) {
                    showError(mContext.getResources().getString(R.string.dlg_missing_planning), mContext.getResources().getString(R.string.msg_planning_required_graphic));
                } else if (compareCount == 0) {
                    Intent intent = new Intent(mContext, TurnaroundActivity.class);
                    intent.putExtra(MyConstants.DIVE_PICK, mDivePick);
                    ((DivePickActivity) mContext).setDivePick(mDivePick);
                    mContext.startActivity(intent);
                } else {
                    DivesSelected diveSelected = new DivesSelected();
                    diveSelected.setDiveNo1(diveNo1);
                    diveSelected.setDiveNo2(diveNo2);
                    diveSelected.setDiveNo3(diveNo3);
                    Intent intent = new Intent(mContext, TurnaroundCompareActivity.class);
                    intent.putExtra(MyConstants.DIVES_SELECTED, diveSelected);
                    mContext.startActivity(intent);
                }
            });

            binding.ruleOfThirdsIcon.setOnClickListener(view -> {
                // Call the RuleOfThirds Activity
                // Check if the Dive has at least one Dive Plan
                AirDA airDa = new AirDA(mContext);
                airDa.open();
                int planCount = airDa.getPlanCount(mDivePick.getDiveNo());
                airDa.close();
                // Checks if any dives are checked to be compare
                // Use the 3 first one
                int compareCount = 0;
                long diveNo1 = MyConstants.ZERO_L;
                long diveNo2 = MyConstants.ZERO_L;
                long diveNo3 = MyConstants.ZERO_L;
                // Need to scroll through the RecyclerView and select first 3 selected dives
                for (int i=0;i<mDivePickList.size();i++) {
                    DivePick divePick = mDivePickList.get(i);
                    if (divePick.getCheckedCompare()) {
                        compareCount++;
                        if (compareCount == 1) {
                            diveNo1 = divePick.getDiveNo();
                        } else if (compareCount == 2) {
                            diveNo2 = divePick.getDiveNo();
                        } else if (compareCount == 3) {
                            diveNo3 = divePick.getDiveNo();
                        }
                    }
                }
                if (planCount == MyConstants.ZERO_L) {
                    showError(mContext.getResources().getString(R.string.dlg_missing_planning), mContext.getResources().getString(R.string.msg_planning_required_graphic));
                } else if (compareCount == 0) {
                    Intent intent = new Intent(mContext, RuleOfThirdsActivity.class);
                    intent.putExtra(MyConstants.DIVE_PICK, mDivePick);
                    ((DivePickActivity) mContext).setDivePick(mDivePick);
                    mContext.startActivity(intent);
                } else {
                    DivesSelected diveSelected = new DivesSelected();
                    diveSelected.setDiveNo1(diveNo1);
                    diveSelected.setDiveNo2(diveNo2);
                    diveSelected.setDiveNo3(diveNo3);
                    Intent intent = new Intent(mContext, RuleOfThirdsCompareActivity.class);
                    intent.putExtra(MyConstants.DIVES_SELECTED, diveSelected);
                    mContext.startActivity(intent);
                }
            });

            binding.driftIcon.setOnClickListener(view -> {
                // Call the Drift Activity
                // Check if the Dive has at least one Dive Plan
                AirDA airDa = new AirDA(mContext);
                airDa.open();
                int planCount = airDa.getPlanCount(mDivePick.getDiveNo());
                airDa.close();
                // Checks if any dives are checked to be compare
                // Use the 3 first one
                int compareCount = 0;
                long diveNo1 = MyConstants.ZERO_L;
                long diveNo2 = MyConstants.ZERO_L;
                long diveNo3 = MyConstants.ZERO_L;
                // Need to scroll through the RecyclerView and select first 3 selected dives
                for (int i=0;i<mDivePickList.size();i++) {
                    DivePick divePick = mDivePickList.get(i);
                    if (divePick.getCheckedCompare()) {
                        compareCount++;
                        if (compareCount == 1) {
                            diveNo1 = divePick.getDiveNo();
                        } else if (compareCount == 2) {
                            diveNo2 = divePick.getDiveNo();
                        } else if (compareCount == 3) {
                            diveNo3 = divePick.getDiveNo();
                        }
                    }
                }
                //
                if (planCount == MyConstants.ZERO_L) {
                    showError(mContext.getResources().getString(R.string.dlg_missing_planning), mContext.getResources().getString(R.string.msg_planning_required_graphic));
                } else if (compareCount == 0) {
                    Intent intent = new Intent(mContext, DriftActivity.class);
                    intent.putExtra(MyConstants.DIVE_PICK, mDivePick);
                    ((DivePickActivity) mContext).setDivePick(mDivePick);
                    mContext.startActivity(intent);
                } else {
                    DivesSelected diveSelected = new DivesSelected();
                    diveSelected.setDiveNo1(diveNo1);
                    diveSelected.setDiveNo2(diveNo2);
                    diveSelected.setDiveNo3(diveNo3);
                    Intent intent = new Intent(mContext, DriftCompareActivity.class);
                    intent.putExtra(MyConstants.DIVES_SELECTED, diveSelected);
                    mContext.startActivity(intent);
                }
            });

            binding.rockBottomIcon.setOnClickListener(view -> {
                // Call the Rockbottom Activity
                // Check if the Dive has at least one Dive Plan
                AirDA airDa = new AirDA(mContext);
                airDa.open();
                int planCount = airDa.getPlanCount(mDivePick.getDiveNo());
                airDa.close();
                // Checks if any dives are checked to be compare
                // Use the 3 first one
                int compareCount = 0;
                long diveNo1 = MyConstants.ZERO_L;
                long diveNo2 = MyConstants.ZERO_L;
                long diveNo3 = MyConstants.ZERO_L;
                // Need to scroll through the RecyclerView and select first 3 selected dives
                for (int i=0;i<mDivePickList.size();i++) {
                    DivePick divePick = mDivePickList.get(i);
                    if (divePick.getCheckedCompare()) {
                        compareCount++;
                        if (compareCount == 1) {
                            diveNo1 = divePick.getDiveNo();
                        } else if (compareCount == 2) {
                            diveNo2 = divePick.getDiveNo();
                        } else if (compareCount == 3) {
                            diveNo3 = divePick.getDiveNo();
                        }
                    }
                }
                if (planCount == MyConstants.ZERO_L) {
                    showError(mContext.getResources().getString(R.string.dlg_missing_planning), mContext.getResources().getString(R.string.msg_planning_required_graphic));
                } else if (compareCount == 0) {
                    Intent intent = new Intent(mContext, RockbottomActivity.class);
                    intent.putExtra(MyConstants.DIVE_PICK, mDivePick);
                    ((DivePickActivity) mContext).setDivePick(mDivePick);
                    mContext.startActivity(intent);
                } else {
                    DivesSelected diveSelected = new DivesSelected();
                    diveSelected.setDiveNo1(diveNo1);
                    diveSelected.setDiveNo2(diveNo2);
                    diveSelected.setDiveNo3(diveNo3);
                    Intent intent = new Intent(mContext, RockbottomCompareActivity.class);
                    intent.putExtra(MyConstants.DIVES_SELECTED, diveSelected);
                    mContext.startActivity(intent);
                }
            });

            binding.emergencyIcon.setOnClickListener(view -> {
                // Call the Emergency Activity
                // Check if the Dive has at least one Dive Plan
                AirDA airDa = new AirDA(mContext);
                airDa.open();
                int planCount = airDa.getPlanCount(mDivePick.getDiveNo());
                airDa.close();
                // Checks if any dives are checked to be compare
                // Use the 3 first one
                int compareCount = 0;
                long diveNo1 = MyConstants.ZERO_L;
                long diveNo2 = MyConstants.ZERO_L;
                long diveNo3 = MyConstants.ZERO_L;
                // Need to scroll through the RecyclerView and select first 3 selected dives
                for (int i=0;i<mDivePickList.size();i++) {
                    DivePick divePick = mDivePickList.get(i);
                    if (divePick.getCheckedCompare()) {
                        compareCount++;
                        if (compareCount == 1) {
                            diveNo1 = divePick.getDiveNo();
                        } else if (compareCount == 2) {
                            diveNo2 = divePick.getDiveNo();
                        } else if (compareCount == 3) {
                            diveNo3 = divePick.getDiveNo();
                        }
                    }
                }
                if (planCount == MyConstants.ZERO_L) {
                    showError(mContext.getResources().getString(R.string.dlg_missing_planning), mContext.getResources().getString(R.string.msg_planning_required_graphic));
                } else if (compareCount == 0) {
                    Intent intent = new Intent(mContext, EmergencyActivity.class);
                    intent.putExtra(MyConstants.DIVE_PICK, mDivePick);
                    ((DivePickActivity) mContext).setDivePick(mDivePick);
                    mContext.startActivity(intent);
                } else {
                    DivesSelected diveSelected = new DivesSelected();
                    diveSelected.setDiveNo1(diveNo1);
                    diveSelected.setDiveNo2(diveNo2);
                    diveSelected.setDiveNo3(diveNo3);
                    Intent intent = new Intent(mContext, EmergencyCompareActivity.class);
                    intent.putExtra(MyConstants.DIVES_SELECTED, diveSelected);
                    mContext.startActivity(intent);
                }
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((DivePickActivity) mContext).countDives(checkBox.isChecked());
                } else {
                    // Select a new Dive by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mDivePick = mDivePickList.get(position);
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
                        ((DivePickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_pick_header, parent, false);
            return new DivePickAdapter.VHHeader(pickDiveAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_pick, parent, false);
            return new DivePickAdapter.VHItem(pickDiveAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            DivePick dataObject = mDivePickList.get(position - HEADER_OFFSET);

            DivePickAdapter.ViewHolder itemView = (DivePickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.divePick, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindDivePick(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mDivePick = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((DivePickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mDivePickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((DivePickActivity) mContext).doSmoothScroll(mDivePickList.size());
                }
            } else {
                ((DivePickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            // Get the first letter of list item
            String letter = String.valueOf(dataObject.getMyBuddyFullName().charAt(0));
            TextDrawable circle = TextDrawable.builder().buildRound(letter, mGenerator.getColor(dataObject.getDiveNo()));
            ((VHItem) holder).letterCircle.setImageDrawable(circle);

            ((VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((DivePickActivity) mContext).countDives(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDivePickList.size() + HEADER_OFFSET;
    }

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
            mFilter=new DivePickFilter(mDivePickList,this);
        }
        return mFilter;
    }

    // My functions

    public void addDive(DivePick divePick) {
        mDivePick = divePick;
        // Add the new DivePick at the beginning of the Array list
        mDivePickList.add(0,divePick);
        // Expanded position is always at the beginning of the RecyclerView
        mExpandedPosition = HEADER_OFFSET;
        // Tell the Adapter that the previous Expanded position is not expanded anymore
        notifyItemChanged(mSelectedPosition);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(HEADER_OFFSET);
        notifyItemChanged(mSelectedPosition);
    }

    public void modifyDive(DivePick modifiedDivePick) {
        mDivePick = modifiedDivePick;
        // Find the position of the modified DivePick in the collection
        int position = mDivePickList.indexOf(modifiedDivePick);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old DivePick with the modified DivePick
            mDivePickList.set(position, modifiedDivePick);
            notifyItemChanged(position + HEADER_OFFSET);
        }
    }

    public void deleteDive(int position) {
        mDivePickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) { return position == MyConstants.ZERO_I; }

    public void setDivePick (DivePick divePick) {mDivePick = divePick;}

    public void setDivePickList(ArrayList<DivePick> divePickList) {mDivePickList = divePickList;}

    public ArrayList<DivePick> getDivePickList() {return mDivePickList;}

    public DivePick getDivePick () {
        return mDivePick;
    }

    public DivePick getDivePick(int position) {
        return mDivePickList.get(position);
    }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {
        mInMultiEditMode = inMultiEditMode;
    }

    public void setState(State state) {
        mState = state;
    }

    public int getDivePickPosition(DivePick divePick) { return mDivePickList.indexOf(divePick); }

    private void showError(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setIcon(R.drawable.ic_alert)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
