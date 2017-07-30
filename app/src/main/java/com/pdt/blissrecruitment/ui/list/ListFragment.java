package com.pdt.blissrecruitment.ui.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pdt.blissrecruitment.R;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.ui.adapters.QuestionsAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static com.pdt.blissrecruitment.Util.Constants.BUNDLE_KEY_LIST_MODE;
import static com.pdt.blissrecruitment.Util.Constants.BUNDLE_KEY_RESTORING_STATE;
import static com.pdt.blissrecruitment.ui.list.ListFragment.ListMode.FILTERED;
import static com.pdt.blissrecruitment.ui.list.ListFragment.ListMode.PLAIN;

public class ListFragment extends Fragment implements
        ListFragmentInterface.Subordinate,
        QuestionsAdapter.Callback {
    public static final String TAG = "ListFragment";
    private static final int visibleThreshold = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PLAIN, FILTERED})
    public @interface ListMode {
        int PLAIN = 1;
        int FILTERED = 2;
    }

    private
    @ListMode
    int mListMode;
    private boolean mRestoringState;
    private String mFilter;

    private ListFragmentInterface.Controller mController;

    private QuestionsAdapter mQuestionsAdapter;

    private RecyclerView mRecyclerView;
    private LinearLayout loadingScreen;
    private LinearLayout retryScreen;
    private LinearLayoutManager mLinearLayoutManager;
    private SearchView searchView;

    private MenuItem shareMenuItem;

    private boolean searchViewHasText;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(boolean restoringState, @ListFragment.ListMode int listMode) {
        ListFragment fragment = new ListFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(BUNDLE_KEY_RESTORING_STATE, restoringState);
        arguments.putInt(BUNDLE_KEY_LIST_MODE, listMode);
        fragment.setArguments(arguments);

        return fragment;
    }

    @SuppressWarnings("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mRestoringState = getArguments().getBoolean(BUNDLE_KEY_RESTORING_STATE);
            mListMode = getArguments().getInt(BUNDLE_KEY_LIST_MODE);
        }

        if (savedInstanceState != null) {
            mRestoringState = savedInstanceState.getBoolean(BUNDLE_KEY_RESTORING_STATE);
            mListMode = savedInstanceState.getInt(BUNDLE_KEY_LIST_MODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_RESTORING_STATE, true);
        outState.putInt(BUNDLE_KEY_LIST_MODE, mListMode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        setupUI(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onSearchViewReady() called with: view = [" + view + "], savedInstanceState = [" + savedInstanceState + "]");
        super.onViewCreated(view, savedInstanceState);
        loadUI();

    }

    private void loadUI() {
        mQuestionsAdapter.setListMode(mListMode);
        Log.d(TAG, "loadUI() called with: mRestoringState = [" + mRestoringState + "]");
        if (this.mRestoringState) {
            mController.restoreState(mListMode);
        } else {
            switch (mListMode) {
                case FILTERED:
                    mController.listFilteredQuestions(mFilter);
                    break;
                case PLAIN:
                    mController.listQuestions();
                default:

                    break;
            }
        }
    }

    private void setupUI(View rootView) {
        loadingScreen = (LinearLayout) rootView.findViewById(R.id.loading_screen);
        retryScreen = (LinearLayout) rootView.findViewById(R.id.retry_screen);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mQuestionsAdapter = new QuestionsAdapter(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mQuestionsAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = mQuestionsAdapter.getItemCount();
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                if (!mQuestionsAdapter.isLoading() && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            int lastPosition = mQuestionsAdapter.setIsLoading();
                            mQuestionsAdapter.notifyItemInserted(lastPosition);

                            switch (mListMode) {
                                case ListFragment.ListMode.FILTERED:
                                    mController.listFilteredQuestions(mFilter);
                                    break;

                                case ListFragment.ListMode.PLAIN:
                                    mController.listQuestions();
                                    break;

                                default:
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListFragmentInterface.Controller) {
            mController = (ListFragmentInterface.Controller) context;
            mController.registerSubordinate(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListFragmentInterface.Controller");
        }
    }

    @Override
    public void onDetach() {
        if (mController != null) {
            mController.unregisterSubordinate();
        }
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu() called with: menu = [" + menu + "], inflater = [" + inflater + "]");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);

        makeSearchView(menu);
        mController.onSearchViewReady();
        shareMenuItem = menu.findItem(R.id.action_share_list);
        toggleShareAvailability(mListMode == ListMode.FILTERED);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.action_share_list:
                buildShareDialog();
                break;
        }
        return true;
    }


    private void makeSearchView(final Menu menu) {
        searchViewHasText = false;
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    return false;
                }

                mListMode = FILTERED;
                mFilter = query;
                mQuestionsAdapter.clear();
                mQuestionsAdapter.setListMode(mListMode);
                searchView.clearFocus();
                toggleShareAvailability(mListMode == ListMode.FILTERED);

                mController.listFilteredQuestions(mFilter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchViewHasText = s.length() > 0;
                if (!searchViewHasText) {
                    closeSearch();
                }
                return true;
            }


        });
    }

    private void toggleShareAvailability(boolean visible) {
        shareMenuItem.setVisible(visible);
    }

    private void closeSearch() {
        if (mListMode == FILTERED) {
            mListMode = PLAIN;
            mQuestionsAdapter.clear();
            mQuestionsAdapter.setListMode(mListMode);
            mRestoringState = true;
            loadUI();
            toggleShareAvailability(mListMode == ListMode.FILTERED);
        }
    }

    /* ListFragmentInterface.Subordinate */


    @Override
    public void onQuestionsListLoaded(final List<Question> questions) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mQuestionsAdapter.addItems(questions);
            }
        });
    }

    @Override
    public void endReached() {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mQuestionsAdapter.hideLoading();
            }
        });
    }

    @Override
    public void restoreState(boolean restoringState, int listMode) {
        mRestoringState = restoringState;
        loadUI();
    }

    @Override
    public void handleDeepLinking(String filter) {
        searchView.setIconified(false);
        searchView.setQuery(filter, true);
        searchView.clearFocus();
    }

    /* QuestionsAdapter.Callback */
    @Override
    public void onClick(Question question) {
        mController.openDetail(question);
    }


    private void buildShareDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final EditText edittext = new EditText(getContext());
        alert.setTitle(getString(R.string.share_title));
        alert.setView(edittext);

        alert.setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String mail = edittext.getText().toString();
                mController.share(mail, mFilter);
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
    }
}
