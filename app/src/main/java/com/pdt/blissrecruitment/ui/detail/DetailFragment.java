package com.pdt.blissrecruitment.ui.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdt.blissrecruitment.R;
import com.pdt.blissrecruitment.Util.Constants;
import com.pdt.blissrecruitment.connector.entities.Choice;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.ui.adapters.ChoicesAdapter;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment implements
        DetailFragmentInterface.Subordinate,
        ChoicesAdapter.Callback {
    public static final String TAG = "DetailFragment";

    private int questionId;

    private DetailFragmentInterface.Controller mController;

    private ChoicesAdapter mChoicesAdapter;

    private RecyclerView mRecyclerView;
    private LinearLayout loadingScreen;
    private LinearLayout retryScreen;
    private View rootView;

    private ImageView image;
    private TextView title;

    private LinearLayoutManager mLinearLayoutManager;


    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(int questionId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_QUESTION_ID, questionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            questionId = getArguments().getInt(Constants.BUNDLE_KEY_QUESTION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setupUI(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onSearchViewReady() called with: view = [" + view + "], savedInstanceState = [" + savedInstanceState + "]");
        super.onViewCreated(view, savedInstanceState);
        mController.getQuestion(questionId);
    }

    private void setupUI(View rootView) {
        Log.d(TAG, "setupUI() called with: rootView = [" + rootView + "]");
        image = (ImageView) rootView.findViewById(R.id.image);
        title = (TextView) rootView.findViewById(R.id.title);
        loadingScreen = (LinearLayout) rootView.findViewById(R.id.loading_screen);
        retryScreen = (LinearLayout) rootView.findViewById(R.id.retry_screen);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mChoicesAdapter = new ChoicesAdapter(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mChoicesAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu() called with: menu = [" + menu + "], inflater = [" + inflater + "]");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.action_share_detail:
                buildShareDialog();
                break;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailFragmentInterface.Controller) {
            mController = (DetailFragmentInterface.Controller) context;
            mController.registerSubordinate(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DetailFragmentInterface.Controller");
        }
    }

    @Override
    public void onDetach() {
        if (mController != null) {
            mController.unregisterSubordinate();
        }
        super.onDetach();
    }

    /* DetailFragmentInterface.Subordinate */

    @Override
    public void onQuestionLoaded(final Question question) {
        Log.d(TAG, "onQuestionLoaded() called with: question = [" + question + "]");
        rootView.post(new Runnable() {
            @Override
            public void run() {

                mChoicesAdapter.clear();
                mChoicesAdapter.addItems(question.getChoices());
                title.setText(question.getQuestion());
                Picasso.with(getContext())
                        .load(question.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(image);

                mController.setToolbarTitle(question.getQuestion());

            }
        });

    }

     /* ChoicesAdapter.Callback */

    @Override
    public void vote(Choice choice) {
        mController.vote(choice);
    }

    private void buildShareDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final EditText edittext = new EditText(getContext());
        alert.setTitle(getString(R.string.share_detail_title));
        alert.setView(edittext);

        alert.setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String mail = edittext.getText().toString();
                mController.share(mail, questionId);
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
