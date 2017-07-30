package com.pdt.blissrecruitment.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pdt.blissrecruitment.R;
import com.pdt.blissrecruitment.receiver.NetworkChangeReceiver;

public abstract class BaseActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkEventsInterface {
    public static final String TAG = "BaseActivity";
    public boolean networkAvailable;

    /**
     * Convenience method for replacing fragments
     *
     * @param fragment the fragment instance
     * @param tag      fragment tag
     */
    protected void loadFragment(Fragment fragment, String tag) {
        Log.d(TAG, "loadFragment() called with: fragment = [" + fragment + "], tag = [" + tag + "]");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        fragmentTransaction.replace(R.id.container, fragment, tag);

        fragmentTransaction.commitAllowingStateLoss();
        fm.executePendingTransactions();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkChangeReceiver.addListener(this);
    }

    @Override
    protected void onDestroy() {
        NetworkChangeReceiver.removeListener(this);
        super.onDestroy();
    }

    @Override
    public abstract void onNetworkStateChanged(boolean connected);
}
