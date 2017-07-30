package com.pdt.blissrecruitment.ui.list;

import android.text.TextUtils;
import android.util.Log;

import com.pdt.blissrecruitment.Util.Util;
import com.pdt.blissrecruitment.connector.Connector;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.entities.ListInfo;
import com.pdt.blissrecruitment.exception.ConnectorException;
import com.pdt.blissrecruitment.threadpools.RunningTaskTracker;
import com.pdt.blissrecruitment.ui.BaseContract;

import java.util.ArrayList;
import java.util.List;


public class ListPresenter implements ListContract.Presenter {
    private static final String TAG = "ListPresenter";

    private List<Question> cachedQuestionList = new ArrayList<>();
    private List<Question> cachedFilteredQuestionList = new ArrayList<>();

    private ListContract.View mView;

    private ListInfo mListInfo = null;
    private ListInfo mSearchListInfo = null;


    @Override
    public synchronized void questionsList() {
        if (mListInfo == null) {
            Log.d(TAG, "questionsList() new ListInfo");
            mListInfo = new ListInfo.ListInfoBuilder(ListFragment.ListMode.PLAIN).build();
        } else {
            Log.d(TAG, "questionsList() ListInfo: " + mListInfo.toString());
        }

        RunningTaskTracker.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                int offset = mListInfo.getOffset();
                List<Question> currentQuestionList;
                try {
                    currentQuestionList = Connector.getInstance().questionsList(mListInfo.getLimit(), offset);

                    int currentOffset = offset + mListInfo.getLimit();
                    mListInfo.setOffset(currentOffset);
                    cachedQuestionList.addAll(currentQuestionList);
                    Log.d(TAG, "questionsList() ListInfo after fetch: " + mListInfo.toString());
                    if (cachedQuestionList.isEmpty()) {
                        mView.onEmptyList();
                    } else if (currentQuestionList.isEmpty()) {
                        mView.endReached();
                    } else {
                        mView.onQuestionsListLoaded(currentQuestionList);
                    }

                } catch (ConnectorException e) {
                    mView.onError(e.getError());
                }

            }
        });

    }

    @Override
    public void filteredQuestionsList(final String filter) {
        if (TextUtils.isEmpty(filter)) {
            return;
        }

        if (mSearchListInfo == null || mSearchListInfo.isDifferentSearch(filter)) {
            cachedFilteredQuestionList.clear();
            mSearchListInfo = new ListInfo.ListInfoBuilder(ListFragment.ListMode.FILTERED)
                    .filter(filter)
                    .build();
        }


        RunningTaskTracker.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                int offset = mSearchListInfo.getOffset();
                List<Question> currentQuestionList;
                try {
                    currentQuestionList = Connector.getInstance().filteredQuestionsList(mSearchListInfo.getLimit(), offset, filter);

                    int currentOffset = offset + mSearchListInfo.getLimit();
                    mSearchListInfo.setOffset(currentOffset);
                    cachedFilteredQuestionList.addAll(currentQuestionList);
                    Log.d(TAG, "questionsList() ListInfo after fetch: " + mSearchListInfo.toString());
                    if (cachedFilteredQuestionList.isEmpty()) {
                        mView.onEmptyList();
                    } else if (currentQuestionList.isEmpty()) {
                        mView.endReached();
                    } else {
                        mView.onQuestionsListLoaded(currentQuestionList);
                    }

                } catch (ConnectorException e) {
                    mView.onError(e.getError());
                }

            }
        });


    }

    @Override
    public void restoreState(@ListFragment.ListMode int listMode) {
        switch (listMode) {
            case ListFragment.ListMode.FILTERED:
                if (cachedQuestionList.isEmpty()) {
                    mView.onEmptyList();
                } else {
                    mView.onQuestionsListLoaded(cachedQuestionList);
                }
                break;

            case ListFragment.ListMode.PLAIN:

                if (cachedFilteredQuestionList.isEmpty()) {
                    mView.onEmptyList();
                } else {
                    mView.onQuestionsListLoaded(cachedFilteredQuestionList);
                }
            default:

                break;
        }
    }

    @Override
    public void attachView(BaseContract.BaseView view) {
        mView = (ListContract.View) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void share(final String email, String filter) {
        final String url = Util.buildQuestionFilterUrl(filter);
        RunningTaskTracker.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {

                try {
                    Connector.getInstance().shareQuestion(email, url);
                } catch (ConnectorException e) {
                    mView.onError(e.getError());
                }

            }
        });

    }
}
