package com.pdt.blissrecruitment.ui.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pdt.blissrecruitment.R;
import com.pdt.blissrecruitment.Util.Constants;
import com.pdt.blissrecruitment.ui.BaseActivity;
import com.pdt.blissrecruitment.ui.detail.DetailActivity;
import com.pdt.blissrecruitment.ui.list.ListActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import static com.pdt.blissrecruitment.ui.splash.SplashActivity.OpenMode.DEEP_LINK_FILTER;
import static com.pdt.blissrecruitment.ui.splash.SplashActivity.OpenMode.DEEP_LINK_QUESTION;
import static com.pdt.blissrecruitment.ui.splash.SplashActivity.OpenMode.NORMAL;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends BaseActivity implements SplashContract.View {
    private static final String TAG = "SplashActivity";


    private LinearLayout loadingScreen;
    private LinearLayout retryScreen;
    private Button retryButton;
    private SplashPresenter presenter;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NORMAL, DEEP_LINK_FILTER, DEEP_LINK_QUESTION})
    public @interface OpenMode {
        int NORMAL = 1;
        int DEEP_LINK_FILTER = 2;
        int DEEP_LINK_QUESTION = 3;
    }

    private
    @OpenMode
    int mOpenMode;
    private String deepLinkValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hide();
        mOpenMode = OpenMode.NORMAL;
        // check deeplinking
        Uri data = getIntent().getData();
        String scheme = getIntent().getScheme();

        if (!TextUtils.isEmpty(scheme) &&
                Constants.SCHEME.equals(scheme)) {

            Set<String> params = data.getQueryParameterNames();
            if (params.contains(Constants.PARAM_QUESTION_ID)) {
                mOpenMode = OpenMode.DEEP_LINK_QUESTION;
                deepLinkValue = data.getQueryParameter(Constants.PARAM_QUESTION_ID);

            } else if (params.contains(Constants.PARAM_QUESTION_FILTER)) {
                mOpenMode = OpenMode.DEEP_LINK_FILTER;
                deepLinkValue = data.getQueryParameter(Constants.PARAM_QUESTION_FILTER);
            }
        }

        setupUI();
        attachPresenter();
        presenter.requestServerHealthStatus();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setupUI() {
        loadingScreen = (LinearLayout) findViewById(R.id.loading_screen);
        retryScreen = (LinearLayout) findViewById(R.id.retry_screen);
        retryButton = (Button) findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.requestServerHealthStatus();
            }
        });
    }


    private void attachPresenter() {
        presenter = (SplashPresenter) getLastCustomNonConfigurationInstance();
        if (presenter == null) {
            presenter = new SplashPresenter();
        }
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    @Override
    public void onNetworkStateChanged(boolean connected) {

    }

    @Override
    public void onStatusOk() {
        Log.d(TAG, "onStatusOk() called");
        // move on!
        Intent intent = null;
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK;
        switch (mOpenMode) {
            case OpenMode.NORMAL:
                intent = new Intent(this, ListActivity.class);
                break;

            case OpenMode.DEEP_LINK_FILTER:
                intent = new Intent(this, ListActivity.class);
                intent.putExtra(Constants.PARAM_QUESTION_FILTER, deepLinkValue);
                intent.addFlags(flags);
                break;

            case OpenMode.DEEP_LINK_QUESTION:

                intent = new Intent(this, DetailActivity.class);
                intent.putExtra(Constants.PARAM_QUESTION_ID, deepLinkValue);
                intent.addFlags(flags);
                break;

            default:
        }
        Log.d(TAG, "onStatusOk() called with: mOpenMode = [" + mOpenMode + "], deepLinkValue = [" + deepLinkValue + "]");
        startActivity(intent);
    }

    @Override
    public void showLoadingScreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingScreen.setVisibility(View.VISIBLE);
                retryScreen.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showRetryScreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingScreen.setVisibility(View.GONE);
                retryScreen.setVisibility(View.VISIBLE);
            }
        });

    }
}
