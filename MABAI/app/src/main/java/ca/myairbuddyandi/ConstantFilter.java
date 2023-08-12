package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by Michel on 2020-04-28
 * Holds all of the logic for the ConstantFilter class
 */

public class ConstantFilter extends Filter {

    // Static
    private static final String LOG_TAG = "ConstantFilter";

    // Public

    // Protected

    // Private
    private final ConstantAdapter adapter;
    private final ArrayList<Constant> filterList;

    // End of variables

    // Public constructor
    public ConstantFilter(ArrayList<Constant> filterList, ConstantAdapter adapter)
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
            //Store our filtered Constants
            ArrayList<Constant> filteredConstants = new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {
                //Compare
                if(filterList.get(i).getDescription().toUpperCase().contains(constraint)
                        || filterList.get(i).getValue().toString().contains(constraint)
                        || filterList.get(i).getUnit().toUpperCase().contains(constraint))
                {
                    //Add Constant to the filtered Constants
                    filteredConstants.add(filterList.get(i));
                }
            }
            results.count=filteredConstants.size();
            results.values=filteredConstants;
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
        adapter.setConstantList((ArrayList<Constant>) results.values);
        //Refresh
        adapter.notifyDataSetChanged();
    }
}
