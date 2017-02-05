package com.admin.gitframeapacas.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.admin.gitframeapacas.R;

/**
 * Created by User on 4/2/2560.
 */

public class FeedMapNavigateFragment extends Fragment {
    /*MapView mMapView;
    private GoogleMap googleMap;*/

    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigate, container, false);
        mWebView = (WebView) v.findViewById(R.id.webview_navigate);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.loadUrl("http://sysnet.utcc.ac.th/aparcas/directions.html");
        return v;
    }



   /* @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }*/

}
