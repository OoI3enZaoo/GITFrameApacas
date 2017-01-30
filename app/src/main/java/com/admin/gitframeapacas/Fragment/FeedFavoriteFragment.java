package com.admin.gitframeapacas.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admin.gitframeapacas.R;

/**
 * Created by Admin on 2/1/2560.
 */

public class FeedFavoriteFragment extends Fragment {
    public FeedFavoriteFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_favorite, container, false);


        return v;

    }
}
