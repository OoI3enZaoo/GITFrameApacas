package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 12/11/2559.
 */

public class FeedMapFragment extends Fragment {


    private FragmentActivity myContext;
    public FeedMapFragment(){


    }


    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);


        return v;

    }
}
