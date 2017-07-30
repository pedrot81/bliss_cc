package com.pdt.blissrecruitment.ui;

/**
 * Created by pdt on 27/07/2017.
 */

public interface BaseContract {

    interface BaseView {

    }

    interface BasePresenter {

        void attachView(BaseView view);

        void detachView();
    }
}
