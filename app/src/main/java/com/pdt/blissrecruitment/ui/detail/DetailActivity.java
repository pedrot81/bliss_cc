package com.pdt.blissrecruitment.ui.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.pdt.blissrecruitment.R;
import com.pdt.blissrecruitment.Util.Constants;
import com.pdt.blissrecruitment.connector.entities.Choice;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.entities.Error;
import com.pdt.blissrecruitment.ui.BaseActivity;
import com.pdt.blissrecruitment.ui.list.ListFragment;

public class DetailActivity extends BaseActivity implements
        DetailContract.View,
        DetailFragmentInterface.Controller {

    private DetailPresenter mPresenter;
    private DetailFragmentInterface.Subordinate mSubordinateDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int questionId = getIntent().getExtras().getInt(Constants.BUNDLE_KEY_QUESTION_ID);
        attachPresenter();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ListFragment.TAG);
        fragment = fragment == null ? DetailFragment.newInstance(questionId) : fragment;
        loadFragment(fragment, DetailFragment.TAG);
    }

    private void attachPresenter() {
        mPresenter = (DetailPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null) {
            Log.d(TAG, "attachPresenter() create ListPresenter");
            mPresenter = new DetailPresenter();
        }
        mPresenter.attachView(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }


    /* BaseActivity */

    @Override
    public void onNetworkStateChanged(boolean connected) {

    }

    /* DetailContract.View */

    @Override
    public void onQuestionLoaded(Question question) {
        if (mSubordinateDetailFragment != null) {
            mSubordinateDetailFragment.onQuestionLoaded(question);
        }

    }

    @Override
    public void onError(Error error) {
        // todo handle error
    }
/* DetailFragmentInterface.Controller */

    @Override
    public void getQuestion(int questionId) {
        mPresenter.getQuestion(questionId);

    }

    @Override
    public void registerSubordinate(DetailFragmentInterface.Subordinate subordinate) {
        mSubordinateDetailFragment = subordinate;
    }

    @Override
    public void unregisterSubordinate() {
        mSubordinateDetailFragment = null;
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void share(String email, int questionId) {
        mPresenter.share(email, questionId);
    }

    @Override
    public void vote(Choice choice) {
        mPresenter.vote(choice);
    }
}
