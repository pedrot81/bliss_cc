package com.pdt.blissrecruitment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkChangeReceiver";

    public interface NetworkEventsInterface {
        void onNetworkStateChanged(boolean connected);
    }

    private static List<NetworkEventsInterface> listeners;

    public NetworkChangeReceiver() {
        listeners = new ArrayList<>();
    }

    public static void addListener(NetworkEventsInterface networkEventsInterface) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        listeners.add(networkEventsInterface);
    }

    public static void removeListener(NetworkEventsInterface networkEventsInterface) {
        if (listeners == null) {
            return;
        }
        listeners.remove(networkEventsInterface);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
            Log.d(TAG, "activeNetwork is = [" + isConnected + "]");
            if (listeners != null) {
                for (NetworkEventsInterface eventsInterface : listeners) {
                    eventsInterface.onNetworkStateChanged(isConnected);
                }
            }

        }
    }
}
