package ca.myairbuddyandi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.RmvBinding;

/**
 * Created by Michel on 2016-12-15.
 * Holds all of the logic for the SacRmvAdapter class
 */

public class SacRmvAdapter extends RecyclerView.Adapter<SacRmvAdapter.ViewHolder> {

    // Static
    private static final String LOG_TAG = "SacRmvAdapter";

    // Public

    // Protected

    // Private
    private ArrayList<SacRmv> mSacRmvList;
    private final Context mContext;
    private RadioButton mLastCheckedRb = null;
    private SacRmv mSacRmv = new SacRmv();
    private State mState = null;

    // End of variables

    // Public constructor
    public SacRmvAdapter(Context context, ArrayList<SacRmv> sacRmvList) {
        mContext = context;
        mSacRmvList = sacRmvList;}

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RmvBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            RadioButton radioButton = null;
            if (binding != null) {
                radioButton = binding.RadioButton;
            }
            if (radioButton != null) {
                radioButton.setOnClickListener(view -> {
                    if (mLastCheckedRb != null) {
                        mLastCheckedRb.setChecked(false);
                    }
                    mLastCheckedRb = (RadioButton) view;
                    int position = getBindingAdapterPosition();
                    if (position >= MyConstants.ZERO_I) {
                        mSacRmv = mSacRmvList.get(position);
                    }
                });
            }

            if (binding != null) {
                binding.diveType.setOnClickListener(view -> {
                    int position = getBindingAdapterPosition();
                    if (position >= MyConstants.ZERO_I) {
                        mSacRmv = mSacRmvList.get(position);
                        if (mSacRmv.getDiveNo() > 0
                                && (mSacRmv.getDiveType().equals("L")
                                || mSacRmv.getDiveType().equals("MI")
                                || mSacRmv.getDiveType().equals("MA"))) {
                            Intent intent = new Intent(mContext, DiveActivity.class);
                            Dive dive = new Dive();
                            dive.setContext(mContext);
                            dive.setDiveNo(mSacRmv.getDiveNo());
                            dive.setLogBookNo(mSacRmv.getLogBookNo());
                            intent.putExtra(MyConstants.DIVE, dive);
                            intent.putExtra(MyConstants.STATE, mState);
                            ((SacRmvActivity) mContext).divePickLauncher.launch(intent);
                        }
                    }
                });
            }
        }

        private void bindSacRmv(SacRmv sacRmv) {
            binding.setSacRmv(sacRmv);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }

    @NonNull
    @Override
    public SacRmvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View sacRmvAdapter = LayoutInflater.from(parent.getContext()).inflate(R.layout.rmv, parent, false);
        return new SacRmvAdapter.ViewHolder(sacRmvAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SacRmv dataObject = mSacRmvList.get(position);
        RmvBinding binding;

        binding = DataBindingUtil.bind(holder.itemView);

        holder.getBinding().setVariable(BR.sacRmv, dataObject);
        holder.getBinding().executePendingBindings();

        // Checks the initial RadioButton
        RadioButton checkedRb = null;
        TextView diveType = null;
        if (binding != null) {
            checkedRb = binding.RadioButton;
            diveType = binding.diveType;
        }
        // NOTE: Leave as is
        if (dataObject.getDiveTypeSelected().equals("Y")) {
            assert checkedRb != null;
            checkedRb.setChecked(true);
            mLastCheckedRb = checkedRb;
            mSacRmv = dataObject;
        } else {
            assert checkedRb != null;
            checkedRb.setChecked(false);
        }

        if (        dataObject.getDiveNo() > 0
                && (dataObject.getDiveType().equals("L")
                || dataObject.getDiveType().equals("MI")
                || dataObject.getDiveType().equals("MA"))) {
            diveType.setTextColor(ContextCompat.getColor(mContext, R.color.theme_myapp_action_bar));
        }

        holder.bindSacRmv(dataObject);
    }

    @Override
    public int getItemCount() {
        return mSacRmvList.size();
    }

    // My functions

    public SacRmv getSacRmv() {return mSacRmv;}

    public void setSacRmvList(ArrayList<SacRmv> sacRmvList) {mSacRmvList = sacRmvList;}

    public void setState(State state) {mState = state;}
}