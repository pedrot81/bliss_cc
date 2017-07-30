package com.pdt.blissrecruitment.ui.splash;

import com.pdt.blissrecruitment.connector.Connector;
import com.pdt.blissrecruitment.exception.ConnectorException;
import com.pdt.blissrecruitment.threadpools.RunningTaskTracker;
import com.pdt.blissrecruitment.ui.BaseContract;

/**
 * Created by pdt on 27/07/2017.
 */

public class SplashPresenter implements SplashContract.Presenter {

    private SplashContract.View mView;

    @Override
    public void attachView(BaseContract.BaseView view) {
        mView = (SplashContract.View) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }


    @Override
    public void requestServerHealthStatus() {
        mView.showLoadingScreen();

        RunningTaskTracker.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isStatusOk =isStatusOk = Connector.getInstance().checkServer();
                    if (isStatusOk) {
                        mView.onStatusOk();
                    } else {
                        mView.showRetryScreen();
                    }
                } catch (ConnectorException e) {
                    mView.showRetryScreen();
                }

            }
        });

    }
}
