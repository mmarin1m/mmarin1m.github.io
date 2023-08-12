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

import ca.myairbuddyandi.databinding.AcronymBinding;
import ca.myairbuddyandi.databinding.AcronymHeaderBinding;

/**
 * Created by Michel on 2020-04-28.
 * Holds all of the logic for the AcronymAdapter class
 */

public class AcronymAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    // Static
    private static final String LOG_TAG = "AcronymAdapterAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;
    // Public

    // Protected

    // Private
    private ArrayList<Acronym> mAcronymList;
    private Boolean mDescendingAcronym = false;
    private Boolean mDescendingDescription = false;
    private Acronym mAcronym = new Acronym();
    private AcronymFilter mFilter;
    private int mSelectedPosition = HEADER_OFFSET;

    // End of variables

    // Public constructor
    public AcronymAdapter(ArrayList<Acronym> acronymList) {
        mAcronymList = acronymList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final AcronymBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindAcronym(Acronym acronym) {
            binding.setAcronymm(acronym);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            AcronymHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;

            binding.hdrAcronym.setOnClickListener(view -> {
                // The user clicked on the Header Unit
                if (mAcronymList.size() > 0) {
                    mAcronymList.sort(new AcronymComparatorAcronym(mDescendingAcronym));
                    mDescendingAcronym = !mDescendingAcronym;
                    setAcronymList(mAcronymList);
                    notifyDataSetChanged();
                }
            });

            binding.hdrDescription.setOnClickListener(view -> {
                // The user clicked on the Header Description
                if (mAcronymList.size() > 0) {
                    mAcronymList.sort(new AcronymComparatorDescription(mDescendingDescription));
                    mDescendingDescription = !mDescendingDescription;
                    setAcronymList(mAcronymList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends AcronymAdapter.ViewHolder {

        public VHItem(View itemView) {

            super(itemView);

            AcronymBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View acronymAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.acronym_header, parent, false);
            return new AcronymAdapter.VHHeader(acronymAdapter);
        } else if (viewType == TYPE_ITEM) {
            View acronymAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.acronym, parent, false);
            return new AcronymAdapter.VHItem(acronymAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            Acronym dataObject = mAcronymList.get(position - HEADER_OFFSET);

            AcronymAdapter.ViewHolder itemView = (AcronymAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.acronymm, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindAcronym(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mAcronym = dataObject;
            }
        }
    }

    @Override
    public int getItemCount() {return mAcronymList.size() + HEADER_OFFSET;}

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
           mFilter=new AcronymFilter(mAcronymList,this);
        }
        return mFilter;
    }

    // My functions

    private boolean isPositionHeader(int position) { return position == MyConstants.ZERO_I; }

    public void setAcronym (Acronym acronym) {mAcronym = acronym;}

    void setAcronymList(ArrayList<Acronym> acronymList) {mAcronymList = acronymList;}

    public ArrayList<Acronym> getAcronymList() {return mAcronymList;}

    public Acronym getAcronym () { return mAcronym; }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public int getAcronymPosition(Acronym acronym) { return mAcronymList.indexOf(acronym); }
}
