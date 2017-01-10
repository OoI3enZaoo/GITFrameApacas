package com.admin.gitframeapacas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;




/**
 * Created by Admin on 12/11/2559.
 */

public class FeedHomeFragment extends Fragment {

    private static final String TAG = "AnonymousAuth";

    private PieChart mPieChart;
    private BarChart mBarChart;
    private FloatingActionButtonPlus mActionButtonPlus;
    public FeedHomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mPieChart = (PieChart) v.findViewById(R.id.piechart);
        mBarChart = (BarChart) v.findViewById(R.id.barchart);
        mActionButtonPlus = (FloatingActionButtonPlus) v.findViewById(R.id.ActionButtonPlus);

        loadData();
        return v;

    }

    private void loadData() {

        mActionButtonPlus.setPosition(FloatingActionButtonPlus.POS_RIGHT_TOP);
        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(getActivity(), "Tips", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "Chart", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), GraphGasActivity.class);
                        startActivity(intent);
                        break;

                }

            }
        });


        mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#91a7ff")));
        mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#42bd41")));
        mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#fff176")));
        mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#ffb74d")));
        mPieChart.addPieSlice(new PieModel("Eae", 9, Color.parseColor("#f36c60")));
        mPieChart.setInnerValueString("AQI");
        mPieChart.setEmptyDataText("Fuck");
        mPieChart.startAnimation();


        /*mPieChart.setInnerValueSize(40);
        mPieChart.setUseInnerValue(true);*/

        mBarChart.addBar(new BarModel("CO", 2.3f, Color.parseColor("#91a7ff")));
        mBarChart.addBar(new BarModel("NO2", 2.f, Color.parseColor("#42bd41")));
        mBarChart.addBar(new BarModel("O3", 3.3f, Color.parseColor("#fff176")));
        mBarChart.addBar(new BarModel("SO2", 1.1f, Color.parseColor("#ffb74d")));
        mBarChart.addBar(new BarModel("PM2.5", 20.7f, Color.parseColor("#f36c60")));
        mBarChart.addBar(new BarModel("Radio", 5.7f, Color.parseColor("#ba68c8")));
        mBarChart.startAnimation();

    }



}
