package com.pdt.blissrecruitment.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import com.pdt.blissrecruitment.R;
import com.pdt.blissrecruitment.Util.Constants;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.entities.Error;
import com.pdt.blissrecruitment.ui.BaseActivity;
import com.pdt.blissrecruitment.ui.NoNetworkFragment;
import com.pdt.blissrecruitment.ui.detail.DetailActivity;

import java.util.List;

import static com.pdt.blissrecruitment.Util.Constants.BUNDLE_KEY_LIST_MODE;
import static com.pdt.blissrecruitment.Util.Constants.BUNDLE_KEY_RESTORING_STATE;

public class ListActivity extends BaseActivity implements
        ListContract.View,
        ListFragmentInterface.Controller {

    private static final String TAG = "ListActivity";

    private
    @ListFragment.ListMode
    int listMode;

    private ListPresenter mPresenter;

    private boolean mRestoringState;
    private String mPendingDeepLink = null;

    private ListFragmentInterface.Subordinate mSubordinateListFragment;

    @SuppressWarnings("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        attachPresenter();

        boolean restoringState = false;
        listMode = ListFragment.ListMode.PLAIN;
        if (savedInstanceState != null) {
            restoringState = savedInstanceState.getBoolean(BUNDLE_KEY_RESTORING_STATE);
            listMode = savedInstanceState.getInt(BUNDLE_KEY_LIST_MODE);
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ListFragment.TAG);
        fragment = fragment == null ? ListFragment.newInstance(restoringState, listMode) : fragment;
        loadFragment(fragment, ListFragment.TAG);

        if (getIntent().hasExtra(Constants.PARAM_QUESTION_FILTER)) {
            mPendingDeepLink = getIntent().getExtras().getString(Constants.PARAM_QUESTION_FILTER);
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_RESTORING_STATE, true);
        outState.putInt(BUNDLE_KEY_LIST_MODE, listMode);
    }

    private void attachPresenter() {
        mPresenter = (ListPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null) {
            Log.d(TAG, "attachPresenter() create ListPresenter");
            mPresenter = new ListPresenter();
        }
        mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    /* ListContract.View */
    @Override
    public void onQuestionsListLoaded(final List<Question> questions) {
        Log.d(TAG, "onQuestionsListLoaded() called with: questions = [" + questions.size() + "]");
        if (mSubordinateListFragment != null) {
            mSubordinateListFragment.onQuestionsListLoaded(questions);
        } else {
            Log.d(TAG, "onQuestionsListLoaded() subordinate if null");
        }

    }

    @Override
    public void endReached() {
        mSubordinateListFragment.endReached();

    }

    @Override
    public void onError(Error error) {
        // todo handle error
    }

    @Override
    public void onEmptyList() {
        // todo handle empty list
    }

    /* BaseActivity */
    @Override
    public void onNetworkStateChanged(boolean connected) {
        networkAvailable = connected;

        if (connected) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ListFragment.TAG);
            fragment = fragment == null ? ListFragment.newInstance(false, listMode) : fragment;
            loadFragment(fragment, ListFragment.TAG);

        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(NoNetworkFragment.TAG);
            fragment = fragment == null ? NoNetworkFragment.newInstance() : fragment;
            loadFragment(fragment, NoNetworkFragment.TAG);
        }
    }


    /*ListFragmentInterface.Controller */

    @Override
    public void registerSubordinate(ListFragmentInterface.Subordinate subordinate) {
        Log.d(TAG, "registerSubordinate() called with: subordinate = [" + subordinate + "]");
        mSubordinateListFragment = subordinate;

        if (mRestoringState) {
            mRestoringState = false;
            mSubordinateListFragment.restoreState(mRestoringState, listMode);
        }
    }

    @Override
    public void unregisterSubordinate() {
        Log.d(TAG, "unregisterSubordinate() called");
        mSubordinateListFragment = null;
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void listFilteredQuestions(String filter) {
        mPresenter.filteredQuestionsList(filter);
    }

    @Override
    public void listQuestions() {
        Log.d(TAG, "listQuestions() called");
        mPresenter.questionsList();

    }

    @Override
    public void restoreState(int listMode) {
        Log.d(TAG, "restoreState() called with: listMode = [" + listMode + "]");
        mPresenter.restoreState(listMode);
    }

    @Override
    public void openDetail(Question question) {
        Intent intent = new Intent(this, DetailActivity.class);
        /* question could be bundled on intent, but for this challenge
        purpose we will always request it remotely */
        intent.putExtra(Constants.BUNDLE_KEY_QUESTION_ID, question.getId());
        startActivity(intent);
    }

    @Override
    public void onSearchViewReady() {
        if (!TextUtils.isEmpty(mPendingDeepLink)) {
            mSubordinateListFragment.handleDeepLinking(mPendingDeepLink);
            mPendingDeepLink = null;
        }
    }

    @Override
    public void share(String email, String filter) {
        mPresenter.share(email, filter);
    }
}
