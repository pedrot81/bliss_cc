package com.pdt.blissrecruitment.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdt.blissrecruitment.BlissRecruitment;
import com.pdt.blissrecruitment.R;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.ui.list.ListFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pdt on 28/07/2017.
 */

public class QuestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "QuestionsAdapter";
    private List<Question> mDataset;

    private
    @ListFragment.ListMode
    int mListMode;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Callback mCallback;

    private boolean mLoading;

    public QuestionsAdapter(List<Question> dataset, Callback callback) {
        mDataset = dataset != null ? dataset : new ArrayList<Question>();
        mCallback = callback;
    }

    public QuestionsAdapter(Callback callback) {
        this(null, callback);
    }

    public void setListMode(@ListFragment.ListMode int listMode) {
        this.mListMode = listMode;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_row, parent, false);
            return new QuestionViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                Question question = mDataset.get(position);
                QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
                questionViewHolder.bind(question, mListMode);
                break;

            case VIEW_TYPE_LOADING:
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public synchronized void addItems(List<Question> list) {
        hideLoading();
        int start = mDataset.size();
        mDataset.addAll(list);
        notifyItemRangeChanged(start, start + list.size());
        mLoading = false;
    }

    public synchronized boolean isLoading() {
        return mLoading;
    }

    public int setIsLoading() {
        mLoading = true;
        mDataset.add(null);
        return mDataset.size() - 1;
    }


    public synchronized void hideLoading() {
        int lastPosition = mDataset.size() - 1;
        if (lastPosition != RecyclerView.NO_POSITION && mDataset.get(lastPosition) == null) {
            mDataset.remove(lastPosition);
            notifyItemRemoved(lastPosition);
        }
    }

    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }


    private class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView id;
        private TextView datePublished;
        private ImageView thumb;
        private android.support.v7.widget.CardView cardView;

        QuestionViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            datePublished = (TextView) view.findViewById(R.id.date_published);
            id = (TextView) view.findViewById(R.id.question_id);
            thumb = (ImageView) view.findViewById(R.id.thumb);
            cardView = (CardView) view.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

        }

        void bind(Question question, @ListFragment.ListMode int listMode) {
            title.setText(question.getQuestion());
            datePublished.setText(question.getPublishedAt()); // format date
            String questionID = String.valueOf(question.getId());
            id.setText(questionID);
            loadImage(thumb, question.getThumbUrl());
            setCardBackground(cardView, listMode);
        }

        private void setCardBackground(CardView cardView, @ListFragment.ListMode int listMode) {
            int color;
            switch (listMode) {
                case ListFragment.ListMode.FILTERED:
                    color = ContextCompat.getColor(BlissRecruitment.getInstance(), R.color.accent);
                    break;

                case ListFragment.ListMode.PLAIN:
                    color = ContextCompat.getColor(BlissRecruitment.getInstance(), R.color.primary_dark);
                    break;

                default:
                    color = ContextCompat.getColor(BlissRecruitment.getInstance(), R.color.divider);
                    break;
            }

            cardView.setCardBackgroundColor(color);
        }

        private void loadImage(ImageView imageView, String url) {
            Picasso.with(BlissRecruitment.getInstance())
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
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

            mCallback.onClick(mDataset.get(position));
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    public interface Callback {
        void onClick(Question question);
    }

}
