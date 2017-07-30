package com.pdt.blissrecruitment.ui.detail;

import com.pdt.blissrecruitment.entities.Error;
import com.pdt.blissrecruitment.connector.entities.Choice;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.ui.BaseContract;

public interface DetailContract extends BaseContract {

    interface View extends BaseView {

        void onQuestionLoaded(Question question);

        void onError(Error error);
    }

    interface Presenter extends BasePresenter {

        /**
         * request for question
         */
        void getQuestion(int questionId);

        void share(String email, int questionId);

        void restoreState(int listMode);

        void vote(Choice choice);
    }
}