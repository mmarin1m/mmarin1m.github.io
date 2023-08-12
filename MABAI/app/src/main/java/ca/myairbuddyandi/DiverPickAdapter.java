package ca.myairbuddyandi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.DiverPickBinding;
import ca.myairbuddyandi.databinding.DiverPickHeaderBinding;

/**
 * Created by Michel on 2016-12-15.
 * Holds all of the logic for the DiverPickAdapter class
 */

public class DiverPickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    // Static
    private static final String LOG_TAG = "DiverPickAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int HEADER_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mExpandedPosition = -1;
    private int mSelectedPosition = HEADER_OFFSET;
    private ArrayList<Diver> mDiverPickList;
    private Boolean mDescendingFullName = false;
    private Boolean mDescendingDives = false;
    private Boolean mInMultiEditMode = false;
    private CheckBox mCheckBoxHdr;
    private final ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private final Context mContext;
    private Diver mDiver = new Diver();
    private DiverPickFilter mFilter;

    // End of variables

    // Public constructor
    public DiverPickAdapter(Context context, ArrayList<Diver> diverPickList) {
        mContext = context;
        mDiverPickList = diverPickList;
        mDiver.setContext(mContext);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final DiverPickBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindDiver(Diver diver) {
            binding.setDiver(diver);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder {

        @SuppressLint("NotifyDataSetChanged")
        public VHHeader(View itemView) {

            super(itemView);

            DiverPickHeaderBinding binding = DataBindingUtil.bind(itemView);

            assert binding != null;
            mCheckBoxHdr = binding.checkBoxHD;

            binding.hdrFullName.setOnClickListener(view -> {
                // The user clicked on the Header Last, First Name
                if (mDiverPickList.size() > 0) {
                    mDiverPickList.sort(new DiverPickComparatorFullName(mDescendingFullName));
                    mDescendingFullName = !mDescendingFullName;
                    setDiverPickList(mDiverPickList);
                    notifyDataSetChanged();
                }

            });

            binding.hdrDives.setOnClickListener(view -> {
                // The user clicked on the Header Date
                if (mDiverPickList.size() > 0) {
                    mDiverPickList.sort(new DiverPickComparatorDives(mDescendingDives));
                    mDescendingDives = !mDescendingDives;
                    setDiverPickList(mDiverPickList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class VHItem extends DiverPickAdapter.ViewHolder {

        private final CheckBox checkBox;
        private final ImageView letterCircle;
        private final TableLayout expandedArea;

        public VHItem(View itemView) {

            super(itemView);

            DiverPickBinding binding = DataBindingUtil.bind(itemView);

            itemView.setClickable(true);

            assert binding != null;
            checkBox = binding.checkBox;
            letterCircle = binding.gmailItemLetter;
            expandedArea = binding.expandArea;

            binding.detailIcon.setOnClickListener(view -> {
                // Enter Single Edit Mode
                Intent intent = new Intent(mContext, DiverActivity.class);
                intent.putExtra(MyConstants.DIVER, mDiver);
                ((DiverPickActivity) mContext).setDiver(mDiver);
                ((DiverPickActivity) mContext).editLauncher.launch(intent);
            });

            binding.grouppIcon.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, GrouppPickActivity.class);
                GrouppPick grouppPick = new GrouppPick();
                // Trying to position the list one the first equipment
                grouppPick.setGroupNo(MyConstants.ONE_L);
                // Only works for my equipments
                grouppPick.setDiverNo(mDiver.getDiverNo());
                grouppPick.setDiveNo(MyConstants.ZERO_L);
                // 04/14/2023 Added to make the PICK button invisible
                //            when showing Equipment Groups for a Diver coming from Maintenance menu
                grouppPick.setGroupNo(MyConstants.MINUS_ONE_L);
                intent.putExtra(MyConstants.PICK_A_GROUPP, grouppPick);
                ((DiverPickActivity) mContext).setDiver(mDiver);
                mContext.startActivity(intent);
            });

            itemView.setOnClickListener(view -> {
                if (mInMultiEditMode) {
                    // Select the checkBox and increase count
                    checkBox.setChecked(!checkBox.isChecked());
                    ((DiverPickActivity) mContext).countDivers(checkBox.isChecked());
                } else {
                    // Select a new Diver by changing item (row)
                    int position = getBindingAdapterPosition() - HEADER_OFFSET;
                    if (position >= MyConstants.ZERO_I) {
                        mDiver = mDiverPickList.get(position);
                        // Checks for expanded view, collapse if you find one
                        if (mExpandedPosition >= MyConstants.ZERO_I) {
                            notifyItemChanged(mExpandedPosition);
                        }
                        // Set the current position to expanded
                        mExpandedPosition = getBindingAdapterPosition();
                        notifyItemChanged(mExpandedPosition);
                        // Updating old as well as new positions
                        // Deselect the old position
                        notifyItemChanged(mSelectedPosition);
                        // Select the new position
                        mSelectedPosition = position + HEADER_OFFSET;
                        notifyItemChanged(mSelectedPosition);
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                // Enter Multi Edit Mode
                mInMultiEditMode = !mInMultiEditMode;
                mCheckBoxHdr.setVisibility((mInMultiEditMode) ? View.INVISIBLE : View.GONE);
                int position = getBindingAdapterPosition();
                if (position >= MyConstants.ZERO_I) {
                    mExpandedPosition = -1;
                    ((DiverPickActivity) mContext).setVisibility(position, mInMultiEditMode);
                }
                return true;
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View pickDiverAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.diver_pick_header, parent, false);
            return new DiverPickAdapter.VHHeader(pickDiverAdapter);
        } else if (viewType == TYPE_ITEM) {
            View pickDiverAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.diver_pick, parent, false);
            return new DiverPickAdapter.VHItem(pickDiverAdapter);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DiverPickAdapter.VHItem) {
            Diver dataObject = mDiverPickList.get(position - HEADER_OFFSET);

            DiverPickAdapter.ViewHolder itemView = (DiverPickAdapter.ViewHolder)  holder;

            itemView.getBinding().setVariable(BR.diver, dataObject);
            itemView.getBinding().executePendingBindings();
            itemView.bindDiver(dataObject);

            // Get the current dataObject
            if(mSelectedPosition == position){
                mDiver = dataObject;
            }

            // Set the expanded area
            if (position == mExpandedPosition) {
                ((DiverPickAdapter.VHItem) holder).expandedArea.setVisibility(View.VISIBLE);
                if (position == mDiverPickList.size()) {
                    // Last row
                    // Scroll to the bottom to show the icon bar
                    ((DiverPickActivity) mContext).doSmoothScroll(mDiverPickList.size());
                }
            } else {
                ((DiverPickAdapter.VHItem) holder).expandedArea.setVisibility(View.GONE);
            }

            // Get the first letter of list item
            String letter;
            if (dataObject.getLastName().length() > MyConstants.ZERO_I ) {
                letter = String.valueOf(dataObject.getLastOrFirstName().charAt(0));
            } else {
                letter = " ";
            }
            TextDrawable circle = TextDrawable.builder().buildRound(letter, mGenerator.getColor(dataObject.getDiverNo()));
            ((DiverPickAdapter.VHItem) holder).letterCircle.setImageDrawable(circle);

            ((DiverPickAdapter.VHItem) holder).checkBox.setOnClickListener(v -> {
                CheckBox checkbox = (CheckBox) v;
                ((DiverPickActivity) mContext).countDivers(checkbox.isChecked());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDiverPickList.size() + HEADER_OFFSET;
    }

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
            mFilter=new DiverPickFilter(mDiverPickList,this);
        }
        return mFilter;
    }

    // My functions

    public void addDiver(Diver diver) {
        mDiver = diver;
        // Add the new Diver to the Array list
        mDiverPickList.add(diver);
        // Via the Activity, tell the RecyclerView to scroll to the newly added Diver
        ((DiverPickActivity) mContext).doSmoothScroll(getItemCount());
        // Tell the Adapter that a new item has been added
        // This will trigger a onBindViewHolder()
        notifyItemInserted(getItemCount());
    }

    public void modifyDiver(Diver modifiedDiver) {
        mDiver = modifiedDiver;
        // Find the position of the modified Diver in the collection
        int position = mDiverPickList.indexOf(modifiedDiver);
        // Get the modified Diver
        if (position >= MyConstants.ZERO_I) {
            mDiver = mDiverPickList.get(position);
            // Replace the old Diver with the modified Diver
            mDiverPickList.set(position, modifiedDiver);
            // Tell the Adapter that an item has been modified
            notifyItemChanged(position);
        }
    }

    public void deleteDiver(int position) {
        mDiverPickList.remove(position);
        notifyItemRemoved(position);
    }

    private boolean isPositionHeader(int position) { return position == MyConstants.ZERO_I; }

    public void setDiver (Diver diver) {mDiver = diver;}

    public Diver getDiver () {
        return mDiver;
    }

    public void setDiverPickList(ArrayList<Diver> diverPickList) {
        mDiverPickList = diverPickList;
    }

    public ArrayList<Diver> getDiverPickList() {return mDiverPickList;}

    public Diver getDiver(int position) {
        return mDiverPickList.get(position);
    }

    public void setSelectedPosition (int selectedPosition) {mSelectedPosition = selectedPosition;}

    public void setMultiEditMode (Boolean inMultiEditMode) {
        mInMultiEditMode = inMultiEditMode;
    }

    public int getDiverPickPosition(Diver diver) { return mDiverPickList.indexOf(diver); }
}