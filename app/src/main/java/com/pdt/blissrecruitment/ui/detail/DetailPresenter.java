package com.pdt.blissrecruitment.ui.detail;

import com.google.gson.Gson;
import com.pdt.blissrecruitment.Util.Util;
import com.pdt.blissrecruitment.connector.Connector;
import com.pdt.blissrecruitment.connector.entities.Choice;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.exception.ConnectorException;
import com.pdt.blissrecruitment.threadpools.RunningTaskTracker;

import java.util.ArrayList;

public class DetailPresenter implements DetailContract.Presenter {

    private DetailContract.View mView;
    private Question cachedQuestion;

    @Override
    public void attachView(DetailContract.BaseView view) {
        mView = (DetailContract.View) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void getQuestion(final int questionId) {
        RunningTaskTracker.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                String id = String.valueOf(questionId);
                try {
                    cachedQuestion = Connector.getInstance().retrieveQuestion(id);
                    mView.onQuestionLoaded(cachedQuestion);
                } catch (ConnectorException e) {
                    mView.onError(e.getError());
                }

            }
        });

    }

    @Override
    public void share(final String email, int questionId) {
        final String url = Util.buildQuestionUrl(questionId);
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

    @Override
    public void restoreState(int listMode) {

    }

    @Override
    public void vote(final Choice choice) {


        // update
        RunningTaskTracker.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                /*
                * Not a good way to update votes, its error prone on
                 * prod environments. backend should have the responsibility
                  * to increase number of votes
                * */
                Question updatedQuestion = cachedQuestion;
                ArrayList<Choice> choices = (ArrayList<Choice>) updatedQuestion.getChoices();
                for (Choice currentChoice : choices) {
                    if (choice.getChoice().equalsIgnoreCase(currentChoice.getChoice())) {
                        int addVote = currentChoice.getVotes() + 1;
                        currentChoice.setVotes(addVote);
                    }
                }

                Gson gson = new Gson();
                String json = gson.toJson(updatedQuestion);
                String id = String.valueOf(updatedQuestion.getId());
                try {
                    Question question = Connector.getInstance().updateQuestion(id, json);
                    // use updated object as response as server response is a dummy
                    mView.onQuestionLoaded(updatedQuestion);
                } catch (ConnectorException e) {
                    mView.onError(e.getError());
                }

            }
        });
    }
}
