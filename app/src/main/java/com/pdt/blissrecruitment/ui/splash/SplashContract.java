package com.pdt.blissrecruitment.ui.splash;

import com.pdt.blissrecruitment.ui.BaseContract;

/**
 * Created by pdt on 27/07/2017.
 */

public interface SplashContract extends BaseContract {

    interface View extends BaseView {

        void onStatusOk();

        void showLoadingScreen();

        void showRetryScreen();
    }


    interface Presenter extends BasePresenter {

        void requestServerHealthStatus();
    }
}
