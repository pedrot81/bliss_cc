package com.pdt.blissrecruitment.ui.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdt.blissrecruitment.R;
import com.pdt.blissrecruitment.connector.entities.Choice;

import java.util.ArrayList;
import java.util.List;

public class ChoicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "ChoicesAdapter";
    private List<Choice> mDataset;

    private Callback mCallback;

    public ChoicesAdapter(List<Choice> dataset, Callback callback) {
        mDataset = dataset != null ? dataset : new ArrayList<Choice>();
        mCallback = callback;
    }

    public ChoicesAdapter(Callback callback) {
        mDataset = new ArrayList<Choice>();
        mCallback = callback;
    }

    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public synchronized void addItems(List<Choice> list) {
        int start = mDataset.size();
        mDataset.addAll(list);
        notifyItemRangeChanged(start, start + list.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choice_row, parent, false);
        return new ChoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Choice choice = mDataset.get(position);
        ChoiceViewHolder choiceViewHolder = (ChoiceViewHolder) holder;
        choiceViewHolder.bind(choice);
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    private class ChoiceViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView choiceLabel;
        private TextView votesLabel;
        private android.support.v7.widget.CardView cardView;

        public ChoiceViewHolder(View view) {
            super(view);
            choiceLabel = (TextView) view.findViewById(R.id.choice);
            votesLabel = (TextView) view.findViewById(R.id.votes);
            cardView = (CardView) view.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
        }

        public void bind(Choice choice) {
            choiceLabel.setText(choice.getChoice());
            String votes = String.valueOf(choice.getVotes());
            votesLabel.setText(votes);
        }

        @Override
        public void onClick(View v) {
            if (mCallback == null) {
                return;
            }

            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            mCallback.vote(mDataset.get(position));
        }
    }

    public interface Callback {
        void vote(Choice choice);
    }
}
