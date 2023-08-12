package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by Michel on 2017-03-12.
 * Holds all of the logic for DiverExtraPickFilter class
 */

public class DiverExtraPickFilter extends Filter {

    // Static
    private static final String LOG_TAG = "DiverExtraPickFilter";

    // Public

    // Protected

    // Private
    private final ArrayList<Diver> mFilterList;
    private final DiverExtraPickAdapter mAdapter;

    // End of variables

    // Public constructor
    public DiverExtraPickFilter(ArrayList<Diver> filterList, DiverExtraPickAdapter adapter)
    {
        this.mAdapter=adapter;
        this.mFilterList=filterList;
    }

    //Filtering occurs
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //Check constraint validity
        if(constraint != null && constraint.length() > 0)
        {
            //Change to upper
            constraint=constraint.toString().toUpperCase();
            //Store our filtered divers
            ArrayList<Diver> filteredDivers=new ArrayList<>();
            for (int i=0;i<mFilterList.size();i++)
            {
                //Compare
                if(mFilterList.get(i).getFullName().toUpperCase().contains(constraint))
                {
                    //Add diver to the filtered divers
                    filteredDivers.add(mFilterList.get(i));
                }
            }
            results.count=filteredDivers.size();
            results.values=filteredDivers;
        }else
        {
            results.count=mFilterList.size();
            results.values=mFilterList;
        }
        return results;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    @SuppressWarnings("unchecked")
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mAdapter.setDiverPickList((ArrayList<Diver>) results.values);
        //Refresh
        mAdapter.notifyDataSetChanged();
    }
}
