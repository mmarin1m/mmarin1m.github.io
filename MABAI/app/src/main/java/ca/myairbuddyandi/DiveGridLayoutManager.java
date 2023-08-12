package ca.myairbuddyandi;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Michel on 2020-10-15.
 * Holds all of the logic for the DiveGridLayoutManager class
 */

// NOTE: Reserved for future use
public class DiveGridLayoutManager extends GridLayoutManager {

    // Static
    private static final String LOG_TAG = "DiveGridLayoutManager";

    // Public

    // Protected

    // Private
    private int columnWidth;

    // End of variables

    // Public constructor
    // NOTE: Reserved for future use
    public DiveGridLayoutManager(Context context, int columnWidth) {
        super(context, 1);

        setColumnWidth(columnWidth);
    }

    public void setColumnWidth(int newColumnWidth) {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth;
        }
    }


//    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state,
                                       int position) {
        RecyclerView.SmoothScroller smoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
//                    @Override
//                    public PointF computeScrollVectorForPosition(int targetPosition) {
//                        return DiveGridLayoutManager.this
//                                .computeScrollVectorForPosition(targetPosition);
//                                .computeHorizontalScrollRange();
//                    }

                    @Override
                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START; // override base class behavior
                    }
                };
        smoothScroller.setTargetPosition(position);
    }
}
