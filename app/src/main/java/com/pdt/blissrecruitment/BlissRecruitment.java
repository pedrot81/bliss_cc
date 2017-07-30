package com.pdt.blissrecruitment;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.pdt.blissrecruitment.receiver.NetworkChangeReceiver;

public class BlissRecruitment extends Application {
    @SuppressLint("StaticFieldLeak")
    private static BlissRecruitment sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        registerNetworkBroadcastReceivers();
    }

    public static BlissRecruitment getInstance() {
        return sInstance;
    }

    private void registerNetworkBroadcastReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChangeReceiver(), intentFilter);
    }
}
