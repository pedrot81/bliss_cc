package com.pdt.blissrecruitment.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdt.blissrecruitment.R;

public class NoNetworkFragment extends Fragment {
    public static final String TAG = "NoNetworkFragment";

    public NoNetworkFragment() {
        // Required empty public constructor
    }


    public static NoNetworkFragment newInstance() {
        NoNetworkFragment fragment = new NoNetworkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_network, container, false);
    }

}
