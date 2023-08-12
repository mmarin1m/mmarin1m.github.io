package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.ComputerDivesPickBinding;
import ca.myairbuddyandi.databinding.ComputerDivesPickHeaderBinding;

/**
 * Created by Michel on 2017-06-03.
 * Holds all of the logic for the ComputerDivesPickAdapter class
 *
 * To hold the new dives to download from the dive computer to be selected/unselected
 */

class ComputerDivesPickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "ComputerDivesPickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mSelectedPosition = HEADER_OFFSET;
    private Boolean mInMultiSelectionMode = false;
    private ArrayList<ComputerDives> mComputerDivesPickList;
    private CheckBox mCheckBoxHdr;
    private final Context mContext;
    private ComputerDives mComputerDives = new ComputerDives();

    // End of variables

    // Public constructor
    public ComputerDivesPickAdapter(Context context, ArrayList<ComputerDives> computerDivesPickList) {
        mContext = context;
        mComputerDivesPickList = computerDivesPickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final ComputerDivesPickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindComputerDives(ComputerDives computerDives) {
            binding.setComputerDives(computerDives);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            ComputerDivesPickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.hdrCheckBox;
        }
    }

    private class VHItem extends ComputerDivesPickAdapter.ViewHolder {

        private final CheckBox checkBox;

        public VHItem(View itemView) {
            super(itemView);

            ComputerDivesPickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;

            itemView.setOnClickListener(view -> {
                if (mInMultiSelectionMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((ComputerDivesPickActivity) mContext).countComputerDives(checkBox.isChecked());
                } else {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((ComputerDivesPickActivity) mContext).countComputerDives(checkBox.isChecked());
                    // Select a new Dive by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mComputerDives = mComputerDivesPickList.get(position);
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
                // Enter Multi Selection Mode
                mInMultiSelectionMode = !mInMultiSelectionMode;
                if (mCheckBoxHdr != null) {
                    int position = getBindingAdapterPosition();
                    if (position >= MyConstants.ZERO_I) {
                        ((ComputerDivesPickActivity) mContext).setVisibility(position, mInMultiSelectionMode);
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
            View pickDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.computer_dives_pick_header, parent, false);
            return new ComputerDivesPickAdapter.VHHeader(pickDiveAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.computer_dives_pick, parent, false);
            return new ComputerDivesPickAdapter.VHItem(pickDiveAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            ComputerDives dataObject = mComputerDivesPickList.get(position - HEADER_OFFSET);

            ComputerDivesPickAdapter.ViewHolder itemView = (ComputerDivesPickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.computerDives, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindComputerDives(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mComputerDives = dataObject;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mComputerDivesPickList.size() + HEADER_OFFSET;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions
    
    private boolean isPositionHeader(int position) { return position == MyConstants.ZERO_I; }

    public void setComputerDives (ComputerDives computerDives) {mComputerDives = computerDives;}

    public void setComputerDivesPickList(ArrayList<ComputerDives> computerDivesPickList) {mComputerDivesPickList = computerDivesPickList;}

    public ArrayList<ComputerDives> getComputerDivesPickList() {return mComputerDivesPickList;}

    public ComputerDives getComputerDives () {
        return mComputerDives;
    }

    public ComputerDives getComputerDives(int position) {
        return mComputerDivesPickList.get(position);
    }

    public void setMultiSelectionMode (Boolean inMultiSelectionMode) {mInMultiSelectionMode = inMultiSelectionMode;}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public int getComputerDivesPosition(ComputerDives computerDives) { return mComputerDivesPickList.indexOf(computerDives); }

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
