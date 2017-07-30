package com.pdt.blissrecruitment.ui.list;

import com.pdt.blissrecruitment.connector.entities.Question;

import java.util.List;

public interface ListFragmentInterface {

    interface Controller {
        void registerSubordinate(Subordinate subordinate);

        void unregisterSubordinate();

        void setToolbarTitle(String title);

        void listQuestions();

        void listFilteredQuestions(String filter);

        void restoreState(int listMode);

        void openDetail(Question question);

        void onSearchViewReady();

        void share(String email, String url);
    }

    interface Subordinate {

        void onQuestionsListLoaded(List<Question> questions);

        void endReached();

        void restoreState(boolean restoringState, int listMode);

        void handleDeepLinking(String filter);
    }
}
