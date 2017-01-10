package com.admin.gitframeapacas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

public class DistrictActivity extends AppCompatActivity {
    Toolbar toolbar;
    String DistrictName;
    private FloatingActionButtonPlus mActionButtonPlus;
    private BarChart mBarChart;
    private TextView txtLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        DistrictName = intent.getStringExtra("district").toString();
        toolbar.setTitle(DistrictName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtLocation = (TextView) findViewById(R.id.txtLocation);


        mActionButtonPlus = (FloatingActionButtonPlus) findViewById(R.id.ActionButtonPlus);
        mBarChart = (BarChart) findViewById(R.id.barchart);
        loadData();
    }

    private void loadData() {

        txtLocation.setText(DistrictName);
        mActionButtonPlus.setPosition(FloatingActionButtonPlus.POS_RIGHT_TOP);
        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Tips", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(DistrictActivity.this, RecommendActivity.class);
                        startActivity(intent2);
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Chart", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DistrictActivity.this, GraphGasActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Add to favorite", Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        });


        mBarChart.addBar(new BarModel("CO", 2.3f, Color.parseColor("#91a7ff")));
        mBarChart.addBar(new BarModel("NO2", 2.f, Color.parseColor("#42bd41")));
        mBarChart.addBar(new BarModel("O3", 3.3f, Color.parseColor("#fff176")));
        mBarChart.addBar(new BarModel("SO2", 1.1f, Color.parseColor("#ffb74d")));
        mBarChart.addBar(new BarModel("PM2.5", 20.7f, Color.parseColor("#f36c60")));
        mBarChart.addBar(new BarModel("Radio", 5.7f, Color.parseColor("#ba68c8")));
        mBarChart.startAnimation();


    }

}
