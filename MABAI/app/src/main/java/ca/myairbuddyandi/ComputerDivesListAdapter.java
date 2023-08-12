package ca.myairbuddyandi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.ComputerDivesListActivityBinding;

/**
 * Created by Michel on 2023-03-22
 * Holds all of the logic for the ComputerDivesListAdapter class
 *
 * This adapter holds the computer dives list from the the dive computer, as is
 */

public class ComputerDivesListAdapter extends RecyclerView.Adapter<ComputerDivesListAdapter.ViewHolder> {

    // Static
    private static final String LOG_TAG = "ComputerDivesListAdapter";

    // Public

    // Protected

    // Private
    private ArrayList<ComputerDivesList> mComputerDivesListing;
    private final ComputerDivesList mComputerDivesList = new ComputerDivesList();

    // End of variables

    // Public constructor
    public ComputerDivesListAdapter(Context context, ArrayList<ComputerDivesList> computerDiveList) {
        mComputerDivesListing = computerDiveList;}

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ComputerDivesListActivityBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        private void bindComputerDive(ComputerDivesList computerDivesList) {
            binding.setComputerDivesList(computerDivesList);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    @NonNull
    @Override
    public ComputerDivesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View computerDiveAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.computer_dives_list_activity, parent, false);
        return new ViewHolder(computerDiveAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComputerDivesList dataObject = mComputerDivesListing.get(position);

        ComputerDivesListActivityBinding binding;

        binding = DataBindingUtil.bind(holder.itemView);

        holder.getBinding().setVariable(BR.computerDivesList, dataObject);
        holder.getBinding().executePendingBindings();

        holder.bindComputerDive(dataObject);
    }

    @Override
    public int getItemCount() {return mComputerDivesListing.size();}

    // My functions

    // NOTE: Reserved for future use
    ComputerDivesList getComputerDivesList() {return mComputerDivesList;}

    // NOTE: Reserved for future use
    void setComputerDiveListing(ArrayList<ComputerDivesList> computerDivesListing) {mComputerDivesListing = computerDivesListing;}
}