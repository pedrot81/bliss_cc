package com.pdt.blissrecruitment.ui.detail;

import com.pdt.blissrecruitment.connector.entities.Choice;
import com.pdt.blissrecruitment.connector.entities.Question;

public interface DetailFragmentInterface {

    interface Controller {
        void registerSubordinate(Subordinate subordinate);

        void unregisterSubordinate();

        void setToolbarTitle(String title);

        void getQuestion(int questionId);

        void share(String email, int questionId);

        void vote(Choice choice);
    }

    interface Subordinate {

        void onQuestionLoaded(Question question);

    }
}
