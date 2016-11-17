package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 17/11/2559.
 */

class FeedNearbyFragment extends Fragment {

    FeedNearbyFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nearby, container, false);;
        return v;

    }
}

