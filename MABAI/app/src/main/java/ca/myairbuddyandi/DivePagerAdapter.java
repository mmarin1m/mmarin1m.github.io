package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.DiveComputerBinding;
import ca.myairbuddyandi.databinding.DiveGasBinding;
import ca.myairbuddyandi.databinding.DiveGearBinding;
import ca.myairbuddyandi.databinding.DiveEnvironmentBinding;
import ca.myairbuddyandi.databinding.DivePlanningBinding;
import ca.myairbuddyandi.databinding.DiveProblemBinding;
import ca.myairbuddyandi.databinding.DiveSummaryBinding;

/**
 * ??
 */

public class DivePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static
    private static final String LOG_TAG = "DivePagerAdapter";
    private static final int TYPE_PLANNING = 0;
    private static final int TYPE_SUMMARY = 1;
    private static final int TYPE_ENVIRONMENT = 2;
    private static final int TYPE_GAS = 3;
    private static final int TYPE_GEAR = 4;
    private static final int TYPE_PROBLEM = 5;
    private static final int TYPE_COMPUTER = 6;
    private static final int TYPE_GRAPH = 7;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mSelectedPosition = HEADER_OFFSET;
    private final Context mContext;
    private Dive mDive = new Dive();
    private ArrayList<Dive> mDivePickList;

    // End of variables

    // Public constructor
    public DivePagerAdapter(Context context, ArrayList<Dive> divePickList) {
        mContext = context;
        mDivePickList = divePickList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class viewPlanning extends RecyclerView.ViewHolder {

        private final DivePlanningBinding binding;

        public viewPlanning(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            ((DiveActivity) mContext).bindAndProcessPlanning(binding);

        }

        void bindDive(Dive dive) {
            binding.setDive(dive);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class viewSummary extends DivePagerAdapter.ViewHolder {

        private final DiveSummaryBinding binding;

        public viewSummary(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            ((DiveActivity) mContext).bindAndProcessSummary(binding);
        }

        void bindDive(Dive dive) {
            binding.setDive(dive);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class viewEnvironment extends RecyclerView.ViewHolder {

        private final DiveEnvironmentBinding binding;

        @SuppressLint("NotifyDataSetChanged")
        public viewEnvironment(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            ((DiveActivity) mContext).bindAndProcessEnvironment(binding);
        }

        void bindDive(Dive dive) {
            binding.setDive(dive);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class viewGas extends RecyclerView.ViewHolder {

        private final DiveGasBinding binding;

        @SuppressLint("NotifyDataSetChanged")
        public viewGas(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            ((DiveActivity) mContext).bindAndProcessGas(binding);
        }

        void bindDive(Dive dive) {
            binding.setDive(dive);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class viewGear extends RecyclerView.ViewHolder {

        private final DiveGearBinding binding;

        @SuppressLint("NotifyDataSetChanged")
        public viewGear(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            ((DiveActivity) mContext).bindAndProcessGear(binding);
        }

        void bindDive(Dive dive) {
            binding.setDive(dive);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class viewProblem extends RecyclerView.ViewHolder {

        private final DiveProblemBinding binding;

        @SuppressLint("NotifyDataSetChanged")
        public viewProblem(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            ((DiveActivity) mContext).bindAndProcessProblem(binding);
        }

        void bindDive(Dive dive) {
            binding.setDive(dive);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class viewComputer extends RecyclerView.ViewHolder {

        private final DiveComputerBinding binding;

        @SuppressLint("NotifyDataSetChanged")
        public viewComputer(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);

            ((DiveActivity) mContext).bindAndProcessComputer(binding);
        }

        void bindDive(Dive dive) {
            binding.setDive(dive);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PLANNING) {
            View viewPlanning = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_planning, parent, false);
            return new DivePagerAdapter.viewPlanning(viewPlanning);
        } else if (viewType == TYPE_SUMMARY) {
            View viewSummary = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_summary, parent, false);
            return new DivePagerAdapter.viewSummary(viewSummary);
        } else if (viewType == TYPE_ENVIRONMENT) {
            View viewEnvironment = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_environment, parent, false);
            return new DivePagerAdapter.viewEnvironment(viewEnvironment);
        } else if (viewType == TYPE_GAS) {
            View viewGas = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_gas, parent, false);
            return new DivePagerAdapter.viewGas(viewGas);
        } else if (viewType == TYPE_GEAR) {
            View viewGear = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_gear, parent, false);
            return new DivePagerAdapter.viewGear(viewGear);
        } else if (viewType == TYPE_PROBLEM) {
            View viewProblem = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_problem, parent, false);
            return new DivePagerAdapter.viewProblem(viewProblem);
        } else if (viewType == TYPE_COMPUTER) {
            View viewComputer = LayoutInflater.from(parent.getContext()).inflate(R.layout.dive_computer, parent, false);
            return new DivePagerAdapter.viewComputer(viewComputer);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DivePagerAdapter.viewPlanning) {

            Dive dataObject = mDivePickList.get(position);

            DivePagerAdapter.viewPlanning itemView = (DivePagerAdapter.viewPlanning) holder;

            itemView.getBinding().setVariable(BR.dive, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindDive(dataObject);

        } else if (holder instanceof DivePagerAdapter.viewSummary) {

            Dive dataObject = mDivePickList.get(position);

            DivePagerAdapter.viewSummary itemView = (DivePagerAdapter.viewSummary) holder;

//            itemView.getBinding().setVariable(BR.dive, dataObject);??
//            itemView.getBinding().executePendingBindings();
//            itemView.bindDive(dataObject);

        } else if (holder instanceof DivePagerAdapter.viewEnvironment) {

            Dive dataObject = mDivePickList.get(position);

            DivePagerAdapter.viewEnvironment itemView = (DivePagerAdapter.viewEnvironment) holder;

//            itemView.getBinding().setVariable(BR.dive, dataObject);??
//            itemView.getBinding().executePendingBindings();
//            itemView.bindDive(dataObject);

        } else if (holder instanceof DivePagerAdapter.viewGas) {

            Dive dataObject = mDivePickList.get(position);

            DivePagerAdapter.viewGas itemView = (DivePagerAdapter.viewGas) holder;

//            itemView.getBinding().setVariable(BR.dive, dataObject);??
//            itemView.getBinding().executePendingBindings();
//            itemView.bindDive(dataObject);

        } else if (holder instanceof DivePagerAdapter.viewGear) {

            Dive dataObject = mDivePickList.get(position);

            DivePagerAdapter.viewGear itemView = (DivePagerAdapter.viewGear) holder;

//            itemView.getBinding().setVariable(BR.dive, dataObject);??
//            itemView.getBinding().executePendingBindings();
//            itemView.bindDive(dataObject);

        } else if (holder instanceof DivePagerAdapter.viewProblem) {

            Dive dataObject = mDivePickList.get(position);

            DivePagerAdapter.viewProblem itemView = (DivePagerAdapter.viewProblem) holder;

//            itemView.getBinding().setVariable(BR.dive, dataObject);??
//            itemView.getBinding().executePendingBindings();
//            itemView.bindDive(dataObject);

        } else if (holder instanceof DivePagerAdapter.viewComputer) {

            Dive dataObject = mDivePickList.get(position);

            DivePagerAdapter.viewComputer itemView = (DivePagerAdapter.viewComputer) holder;

//            itemView.getBinding().setVariable(BR.dive, dataObject);??
//            itemView.getBinding().executePendingBindings();
//            itemView.bindDive(dataObject);

        }
    }

    @Override
    public int getItemCount() {
//        return arrayList.size() + HEADER_OFFSET;??
        return mDivePickList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch(position) {
            case 1:
                return TYPE_SUMMARY;
            case 2:
                return TYPE_ENVIRONMENT;
            case 3:
                return TYPE_GAS;
            case 4:
                return TYPE_GEAR;
            case 5:
                return TYPE_PROBLEM;
            case 6:
                return TYPE_COMPUTER;
            case 0:
            default:
                return TYPE_PLANNING;
        }
    }

    // My functions

//    public void addDiver(Diver diver) {
//        mDiver = diver;
//        // Add the new Diver to the Array list
//        mDiverPickList.add(diver);
//        // Via the Activity, tell the RecyclerView to scroll to the newly added Diver
//        ((DiverPickActivity) mContext).doSmoothScroll(getItemCount());
//        // Tell the Adapter that a new item has been added
//        // This will trigger a onBindViewHolder()
//        notifyItemInserted(getItemCount());
//    }

//    public void modifyDiver(Diver modifiedDiver) {
//        mDiver = modifiedDiver;
//        // Find the position of the modified Diver in the collection
//        int position = mDiverPickList.indexOf(modifiedDiver);
//        // Get the modified Diver
//        if (position >= MyConstants.ZERO_I) {
//            mDiver = mDiverPickList.get(position);
//            // Replace the old Diver with the modified Diver
//            mDiverPickList.set(position, modifiedDiver);
//            // Tell the Adapter that an item has been modified
//            notifyItemChanged(position);
//        }
//    }

//    public void deleteDiver(int position) {
//        mDiverPickList.remove(position);
//        notifyItemRemoved(position);
//    }

//    private boolean isPositionHeader(int position) { return position == MyConstants.ZERO_I; }

//    public void setDiver (Diver diver) {mDiver = diver;}

//    public Diver getDiver () {
//        return mDiver;
//    }

    public void setDive(Dive dive) {
        mDive = dive;
        // Replace the Dive at the beginning of the array
        // TODO: Get position
        mDivePickList.set(0,dive);
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
//        notifyItemInserted(HEADER_OFFSET);
        notifyItemChanged(0);
    }

    public void setDivePickList(ArrayList<Dive> divePickList) {
        mDivePickList = divePickList;
    }

//    public ArrayList<Diver> getDiverPickList() {return mDiverPickList;}
//
//    public Diver getDiver(int position) {
//        return mDiverPickList.get(position);
//    }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

//    public void setMultiEditMode (Boolean inMultiEditMode) {
//        mInMultiEditMode = inMultiEditMode;
//    }

//    public int getDiverPickPosition(Diver diver) { return mDiverPickList.indexOf(diver); }
}