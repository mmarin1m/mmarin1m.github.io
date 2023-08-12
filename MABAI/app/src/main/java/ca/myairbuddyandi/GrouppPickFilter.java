package ca.myairbuddyandi;

import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by Michel on 2017-03-12.
 * Holds all the logic for the GrouppPickFilter class
 */

public class GrouppPickFilter extends Filter {

    // Static
    private static final String LOG_TAG = "GrouppPickFilter";

    // Public

    // Protected

    // Private
    private final ArrayList<GrouppPick> filterList;
    private final GrouppPickAdapter adapter;

    // End of variables

    // Public constructor
    public GrouppPickFilter(ArrayList<GrouppPick> filterList, GrouppPickAdapter adapter)
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
            //Store our filtered Groupps
            ArrayList<GrouppPick> filteredGrouppPicks = new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {
                //Compare
                if(filterList.get(i).getGroupType().toUpperCase().contains(constraint)
                        || filterList.get(i).getDescription().toUpperCase().contains(constraint))
                {
                    //Add GrouppPick to the filtered Groupps
                    filteredGrouppPicks.add(filterList.get(i));
                }
            }
            results.count= filteredGrouppPicks.size();
            results.values= filteredGrouppPicks;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @Override
    @SuppressWarnings("unchecked")
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setGrouppPickList((ArrayList<GrouppPick>) results.values);
        //Refresh
        adapter.notifyDataSetChanged();
    }
}
