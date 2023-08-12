package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by Michel on 2020-04-28
 * Holds all of the logic for the AcronymFilter class
 */

public class AcronymFilter extends Filter {

    // Static
    private static final String LOG_TAG = "AcronymFilter";

    // Public

    // Protected

    // Private
    private final AcronymAdapter adapter;
    private final ArrayList<Acronym> filterList;

    // End of variables

    // Public constructor
    public AcronymFilter(ArrayList<Acronym> filterList, AcronymAdapter adapter)
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
            constraint=constraint.toString().toUpperCase();
            //Store our filtered Acronyms
            ArrayList<Acronym> filteredAcronyms = new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {
                //Compare
                if(filterList.get(i).getAcronym().toUpperCase().contains(constraint)
                        || filterList.get(i).getDescription().toUpperCase().contains(constraint))
                {
                    //Add Acronym to the filtered Acronyms
                    filteredAcronyms.add(filterList.get(i));
                }
            }
            results.count=filteredAcronyms.size();
            results.values=filteredAcronyms;
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
        adapter.setAcronymList((ArrayList<Acronym>) results.values);
        //Refresh
        adapter.notifyDataSetChanged();
    }
}
