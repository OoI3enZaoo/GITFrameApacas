package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 17/11/2559.
 */

class FeedAboutAQIFragment extends Fragment {


    FeedAboutAQIFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_aboutaqi, container, false);;
        return v;

    }
}
