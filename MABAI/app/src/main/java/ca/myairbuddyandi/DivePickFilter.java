package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by Michel on 2017-03-12.
 * Holds all of the logic for the DivePickFilter class
 */

public class DivePickFilter extends Filter {

    // Static
    private static final String LOG_TAG = "DivePickFilter";

    // Public

    // Protected

    // Private
    private final DivePickAdapter adapter;
    private final ArrayList<DivePick> filterList;

    // End of variables

    // Public constructor
    public DivePickFilter(ArrayList<DivePick> filterList, DivePickAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;
    }

    //Filtering occurs
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //Check constraint validity
        if(constraint != null && constraint.length() > 0)
        {
            //Change to upper
            constraint = constraint.toString().toUpperCase();
            //Store our filtered Dives
            ArrayList<DivePick> filteredDives = new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {
                //Compare
                if(filterList.get(i).getMyBuddyFullName().toUpperCase().contains(constraint)
                        || filterList.get(i).getDateString().toUpperCase().contains(constraint)
                        || filterList.get(i).getStatus().toUpperCase().contains(constraint)
                        || String.valueOf(filterList.get(i).getLogBookNo()).toUpperCase().contains(constraint)
                        || filterList.get(i).getLocation().toUpperCase().contains(constraint)
                        || filterList.get(i).getDiveSite().toUpperCase().contains(constraint)
                        )
                {
                    //Add Dive to the filtered Dives
                    filteredDives.add(filterList.get(i));
                }
            }
            results.count=filteredDives.size();
            results.values=filteredDives;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    @SuppressWarnings("unchecked")
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setDivePickList((ArrayList<DivePick>) results.values);
        //Refresh
        adapter.notifyDataSetChanged();
    }
}
