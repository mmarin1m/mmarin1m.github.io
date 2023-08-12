package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.ConstantBinding;
import ca.myairbuddyandi.databinding.ConstantHeaderBinding;

/**
 * Created by Michel on 2020-04-28.
 * Holds all of the logic for the ConstantAdapter class
 */

public class ConstantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    // Static
    private static final String LOG_TAG = "ConstantAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private ArrayList<Constant> mConstantList;
    private Boolean mDescendingDescription = false;
    private Boolean mDescendingValue = false;
    private Boolean mDescendingSystem = false;
    private Boolean mDescendingUnit = false;
    private Constant mConstant = new Constant();
    private ConstantFilter mFilter;
    private int mSelectedPosition = HEADER_OFFSET;

    // End of variables

    // Public constructor
    public ConstantAdapter(ArrayList<Constant> constantList) {
        mConstantList = constantList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstantBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindConstant(Constant constant) {
            binding.setConstant(constant);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            ConstantHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;

            binding.hdrSystem.setOnClickListener(view -> {
                // The user clicked on the Header System
                if (mConstantList.size() > 0) {
                    mConstantList.sort(new ConstantComparatorSystem(mDescendingSystem));
                    mDescendingSystem = !mDescendingSystem;
                    setConstantList(mConstantList);
                    notifyDataSetChanged();
                }

            });

            binding.hdrDescription.setOnClickListener(view -> {
                // The user clicked on the Header Description
                if (mConstantList.size() > 0) {
                    mConstantList.sort(new ConstantComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setConstantList(mConstantList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrValue.setOnClickListener(view -> {
                // The user clicked on the Header Value
                if (mConstantList.size() > 0) {
                    mConstantList.sort(new ConstantComparatorValue(mDescendingValue));
                    mDescendingValue = !mDescendingValue;
                    setConstantList(mConstantList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrUnit.setOnClickListener(view -> {
                // The user clicked on the Header Unit
                if (mConstantList.size() > 0) {
                    mConstantList.sort(new ConstantComparatorUnit(mDescendingUnit));
                    mDescendingUnit = !mDescendingUnit;
                    setConstantList(mConstantList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends ConstantAdapter.ViewHolder {

        public VHItem(View itemView) {

            super(itemView);

            ConstantBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View constantAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.constant_header, parent, false);
            return new ConstantAdapter.VHHeader(constantAdapter);
        } else if (viewType == TYPE_ITEM) {
            View constantAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.constant, parent, false);
            return new ConstantAdapter.VHItem(constantAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            Constant dataObject = mConstantList.get(position - HEADER_OFFSET);

            ConstantAdapter.ViewHolder itemView = (ConstantAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.constant, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindConstant(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mConstant = dataObject;
            }
        }
    }

    @Override
    public int getItemCount() {return mConstantList.size() + HEADER_OFFSET;}

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
            mFilter=new ConstantFilter(mConstantList,this);
        }
        return mFilter;
    }

    // My functions

    private boolean isPositionHeader(int position) { return position == MyConstants.ZERO_I; }

    public void setConstant (Constant constant) {mConstant = constant;}

    public void setConstantList(ArrayList<Constant> constantList) {mConstantList = constantList;}

    public ArrayList<Constant> getConstantList() {return mConstantList;}

    public Constant getConstant () { return mConstant; }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public int getConstantPosition(Constant constant) { return mConstantList.indexOf(constant); }
}
