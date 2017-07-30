package com.pdt.blissrecruitment.ui.list;

import com.pdt.blissrecruitment.entities.Error;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.ui.BaseContract;

import java.util.List;

/**
 * Created by pdt on 27/07/2017.
 */

public interface ListContract extends BaseContract {

    interface View extends BaseView {

        void onQuestionsListLoaded(List<Question> questions);

        void endReached();

        void onEmptyList();

        void onError(Error error);
    }

    interface Presenter extends BasePresenter {

        void questionsList();

        void filteredQuestionsList(String filter);

        void restoreState(int listMode);

        void share(String email, String url);
    }
}
