package ca.myairbuddyandi;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Michel on 2018-03-11.
 * Holds all of the logic for the DiveLinearLayoutManager class
 */

public class DiveLinearLayoutManager extends LinearLayoutManager {

    // Public constructor
    public DiveLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state,
                                       int position) {
        RecyclerView.SmoothScroller smoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return DiveLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START; // override base class behavior
                    }
                };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
