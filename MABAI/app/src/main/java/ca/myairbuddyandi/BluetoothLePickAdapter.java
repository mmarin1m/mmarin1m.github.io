package ca.myairbuddyandi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.BluetoothLePickBinding;
import ca.myairbuddyandi.databinding.BluetoothLePickHeaderBinding;

/**
 * Created by Michel on 2023-04-26.
 * Holds all of the logic for the BluetoothLePickAdapter class
 *
 * This class is for exploring and debugging Bluetooth classic and Bluetooth LE connectivity
 */

class BluetoothLePickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "BluetoothLePickAdapter";
    private static final int HEADER_OFFSET = 1;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    // Public

    // Protected

    // Private
    private ArrayList<Bluetooth> mBluetoothPickList;
    private Bluetooth mBluetooth;
    private int mSelectedPosition = HEADER_OFFSET;

    private final Context mContext;

    // End of variables

    // Public constructor
    public BluetoothLePickAdapter(Context context, ArrayList<Bluetooth> bluetoothList) {
        mBluetoothPickList = bluetoothList;
        mContext = context;
        mBluetooth = new Bluetooth(mContext);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final BluetoothLePickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindBluetooth(Bluetooth bluetooth) {
            binding.setBluetooth(bluetooth);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        public VHHeader(View itemView) {

            super(itemView);

            BluetoothLePickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
        }
    }

    private class VHItem extends BluetoothLePickAdapter.ViewHolder {
        public VHItem(View itemView) {
            super(itemView);

            BluetoothLePickBinding binding  = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;

            itemView.setOnClickListener(view -> {

                // Select a new Bluetooth by changing item (row)
                int position = getBindingAdapterPosition() - HEADER_OFFSET;
                Activity activity = (Activity) mContext;
                String className = activity.getClass().getSimpleName();
                if (position >= MyConstants.ZERO_I) {
                    mBluetooth = mBluetoothPickList.get(position);
                    ((BluetoothLePickActivity) mContext).setBluetooth(mBluetooth);
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
            View pickBluetoothAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_le_pick_header, parent, false);
            return new BluetoothLePickAdapter.VHHeader(pickBluetoothAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickBluetoothAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_le_pick, parent, false);
            return new BluetoothLePickAdapter.VHItem(pickBluetoothAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            Bluetooth dataObject = mBluetoothPickList.get(position - HEADER_OFFSET);

            BluetoothLePickAdapter.ViewHolder itemView = (BluetoothLePickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.bluetooth, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindBluetooth(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mBluetooth = dataObject;
            }

            if(mSelectedPosition == position)
                holder.itemView.setBackgroundColor(Color.parseColor(MyConstants.BLUE));
            else
                holder.itemView.setBackgroundColor(Color.parseColor(MyConstants.WHITE));
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

    public void setBluetoothPick (Bluetooth bluetoothPick) {mBluetooth = bluetoothPick;}

    public void setBluetoothList(ArrayList<Bluetooth> bluetoothPickList) {mBluetoothPickList = bluetoothPickList;}

    public ArrayList<Bluetooth> getBluetoothList() {return mBluetoothPickList;}

    public Bluetooth getBluetooth () {return mBluetooth;}

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}
}
