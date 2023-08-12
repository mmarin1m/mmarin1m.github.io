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

import ca.myairbuddyandi.databinding.ComputerPickBinding;
import ca.myairbuddyandi.databinding.ComputerPickHeaderBinding;

/**
 * Created by Michel on 2023-03-21.
 * Holds all of the logic for the ComputerPickAdapter class
 *
 * To hold the computer saved computer dives to be edited or download dives from
 */

public class ComputerPickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "ComputerPickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<ComputerPick> mComputerPickList;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private ComputerPick mComputerPick = new ComputerPick();

    // End of variables

    // Public constructor
    public ComputerPickAdapter(Context context, ArrayList<ComputerPick> computerPickList) {
        mContext = context;
        mComputerPickList = computerPickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final ComputerPickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindComputerPick(ComputerPick computerPick) {
            binding.setComputerPick(computerPick);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            ComputerPickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;
        }
    }

    private class VHItem extends ComputerPickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {
            super(itemView);

            ComputerPickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, ComputerActivity.class);
                Computer computer = new Computer();
                computer.setComputerNo(mComputerPick.getComputerNo());
                intent.putExtra(MyConstants.COMPUTER, computer);
                ((ComputerPickActivity) mContext).editLauncher.launch(intent);
            });

            binding.diveIcon.setOnClickListener(view -> {
                // To view and download dives
                Intent intent = new Intent(mContext, ComputerDiveActivity.class);
                Computer computer = new Computer();
                computer.setComputerNo(mComputerPick.getComputerNo());
                intent.putExtra(MyConstants.COMPUTER, computer);
                ((ComputerPickActivity) mContext).editLauncher.launch(intent);
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((ComputerPickActivity) mContext).countComputers(checkBox.isChecked());
                } else {
                    // Select a new Computer by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mComputerPick = mComputerPickList.get(position);
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
                        ((ComputerPickActivity) mContext).setVisibility(position, mInMultiEditMode);
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
            View pickComputerAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.computer_pick_header, parent, false);
            return new ComputerPickAdapter.VHHeader(pickComputerAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickComputerAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.computer_pick, parent, false);
            return new ComputerPickAdapter.VHItem(pickComputerAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            ComputerPick dataObject = mComputerPickList.get(position - HEADER_OFFSET);

            ComputerPickAdapter.ViewHolder itemView = (ComputerPickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.computer, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindComputerPick(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mComputerPick = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((ComputerPickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mComputerPickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((ComputerPickActivity) mContext).doSmoothScroll(mComputerPickList.size());
                }
            } else {
                ((ComputerPickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            ((VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((ComputerPickActivity) mContext).countComputers(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {return mComputerPickList.size() + HEADER_OFFSET;}

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    public void addComputerPick(ComputerPick computerPick) {
        mComputerPick = computerPick;
        // Add the new Computer to the Array list
        mComputerPickList.add(computerPick);
        // Via the Activity, tell the RecyclerView to scroll to the newly added Computer
        ((ComputerPickActivity) mContext).doSmoothScroll(getItemCount() + HEADER_OFFSET);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount() + HEADER_OFFSET);
        // Must sort to always show the Plans in the right order
        sortByDescription(false);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void modifyComputerPick(ComputerPick modifiedComputer) {
        mComputerPick = modifiedComputer;
        // Find the position of the modified Computer in the collection
        int position = mComputerPickList.indexOf(modifiedComputer);
        if (position >= MyConstants.ZERO_I) {
            // Replace the old Computer with the modified Computer
            mComputerPickList.set(position, modifiedComputer);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position + HEADER_OFFSET);
            notifyDataSetChanged();
            // Must sort to always show the Plans in the right order
            mExpandedPosition = -1;
            sortByDescription(false);
        }
    }

    public void deleteComputerPick(int position) {
        mComputerPickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) {return position == MyConstants.ZERO_I;}

    public void setComputerPick (ComputerPick computerPick) {mComputerPick = computerPick;}

    private void setComputerPickList(ArrayList<ComputerPick> computerPickList) {mComputerPickList = computerPickList;}

    public ArrayList<ComputerPick> getComputerPickList() {return mComputerPickList;}

    public ComputerPick getComputer () {return mComputerPick;}

    public ComputerPick getComputerPick(int position) {return mComputerPickList.get(position);}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    @SuppressLint("NotifyDataSetChanged")
    private void sortByDescription(Boolean descendingDescription) {
        mComputerPickList.sort(new ComputerPickComparatorDescription(descendingDescription));
        Boolean mDescendingDescription = !descendingDescription;
        setComputerPickList(mComputerPickList);
        notifyDataSetChanged();
    }
}
