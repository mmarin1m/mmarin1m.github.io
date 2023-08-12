package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by Michel on 2017-03-12.
 * Holds all of the logic for CylinderPickFilter class
 */

public class CylinderPickFilter extends Filter {

    // Static
    private static final String LOG_TAG = "CylinderPickFilter";

    // Public

    // Protected

    // Private
    private final CylinderPickAdapter adapter;
    private final ArrayList<CylinderPick> filterList;

    // End of variables

    // Public constructor
    public CylinderPickFilter(ArrayList<CylinderPick> filterList, CylinderPickAdapter adapter)
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
            //Store our filtered Cylinders
            ArrayList<CylinderPick> filteredCylinderPicks = new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {
                //Compare
                if(filterList.get(i).getCylinderType().toUpperCase().contains(constraint)
                        || String.valueOf(filterList.get(i).getVolume()).toUpperCase().contains(constraint)
                        || String.valueOf(filterList.get(i).getRatedPressure()).toUpperCase().contains(constraint))
                {
                    //Add CylinderPick to the filtered Cylinders
                    filteredCylinderPicks.add(filterList.get(i));
                }
            }
            results.count= filteredCylinderPicks.size();
            results.values= filteredCylinderPicks;
        } else {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    @SuppressWarnings("unchecked")
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setCylinderPickList((ArrayList<CylinderPick>) results.values);
        //Refresh
        adapter.notifyDataSetChanged();
    }
}
