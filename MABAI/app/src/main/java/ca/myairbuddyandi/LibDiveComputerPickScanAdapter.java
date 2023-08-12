package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.LibdivecomputerPickScanBinding;
import ca.myairbuddyandi.databinding.LibdivecomputerPickScanHeaderBinding;

/**
 * Created by Michel on 2023-07-10.
 * Holds all of the logic for the LibDiveComputerPickScanAdapter class
 */

public class LibDiveComputerPickScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "LibDiveComputerPickScanAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<Bluetooth> mBluetoothPickList;
    private Bluetooth mBluetooth;
    private final Context mContext;

    // End of variables

    // Public constructor
    public LibDiveComputerPickScanAdapter(Context context, ArrayList<Bluetooth> bluetoothList) {
        mContext = context;
        mBluetoothPickList = bluetoothList;
        mBluetooth = new Bluetooth(context);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final LibdivecomputerPickScanBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindLibDiveComputer(Bluetooth bluetooth) {
            binding.setBluetooth(bluetooth);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private static class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            LibdivecomputerPickScanHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
        }
    }

    private class VHItem extends LibDiveComputerPickScanAdapter.ViewHolder {

        private final TableLayout expandedArea;
        public VHItem(View itemView) {
            super(itemView);

            LibdivecomputerPickScanBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            expandedArea = binding.expandArea;

            binding.connectIcon.setOnClickListener(view -> {
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                if (position >= MyConstants.ZERO_I) {
                    ((LibDiveComputerPickScanActivity) mContext).connect(position);
                }
            });

            itemView.setOnClickListener(view -> {
                // Select a new Bluetooth by changing item (row)
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                if (position >= MyConstants.ZERO_I) {
                    mBluetooth = mBluetoothPickList.get(position);
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
            View pickLibDiveComputerAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.libdivecomputer_pick_scan_header, parent, false);
            return new VHHeader(pickLibDiveComputerAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickLibDiveComputerAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.libdivecomputer_pick_scan, parent, false);
            return new LibDiveComputerPickScanAdapter.VHItem(pickLibDiveComputerAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            Bluetooth dataObject = mBluetoothPickList.get(position - HEADER_OFFSET);

            LibDiveComputerPickScanAdapter.ViewHolder itemView = (LibDiveComputerPickScanAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.bluetooth, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindLibDiveComputer(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mBluetooth = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((LibDiveComputerPickScanAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mBluetoothPickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((LibDiveComputerPickScanActivity) mContext).doSmoothScroll(mBluetoothPickList.size());
                }
            } else {
                ((LibDiveComputerPickScanAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {return mBluetoothPickList.size() + HEADER_OFFSET;}

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    // My functions

    private boolean isPositionHeader(int position) { return position == MyConstants.ZERO_I; }

    public void setBluetooth (Bluetooth bluetooth) {mBluetooth = bluetooth;}

    public void setBluetoothPickList(ArrayList<Bluetooth> bluetoothPickList) {mBluetoothPickList = bluetoothPickList;}

    public ArrayList<Bluetooth> getBluetoothPickList() {return mBluetoothPickList;}

    public Bluetooth getBluetooth () {return mBluetooth;}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}
}
