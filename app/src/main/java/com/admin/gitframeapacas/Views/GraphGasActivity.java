package com.admin.gitframeapacas.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.admin.gitframeapacas.R;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

/*import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;*/

/**
 * Created by Admin on 17/11/2559.
 */

public class GraphGasActivity extends AppCompatActivity {

    private ValueLineChart mChartCO;
    private ValueLineChart mChartNO2;
    private ValueLineChart mChartO3;
    private ValueLineChart mChartSO2;
    private ValueLineChart mChartPM25;
    private ValueLineChart mChartRadio;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Graph");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mChartCO = (ValueLineChart) findViewById(R.id.chartCO);
        mChartNO2 = (ValueLineChart) findViewById(R.id.chartNO2);
        mChartO3 = (ValueLineChart) findViewById(R.id.chartO3);
        mChartSO2 = (ValueLineChart) findViewById(R.id.chartSO2);
        mChartPM25 = (ValueLineChart) findViewById(R.id.chartPM25);
        mChartRadio = (ValueLineChart) findViewById(R.id.chartRadio);


        loadData();
    }

    private void loadData() {


        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
        series.addPoint(new ValueLinePoint("00.00", 2.4f));
        series.addPoint(new ValueLinePoint("01.00", 3.4f));
        series.addPoint(new ValueLinePoint("02.00", 2.4f));
        series.addPoint(new ValueLinePoint("03.00", 1.2f));
        series.addPoint(new ValueLinePoint("04.00", 2.6f));
        series.addPoint(new ValueLinePoint("05.00", 1.0f));
        series.addPoint(new ValueLinePoint("06.00", 3.5f));
        series.addPoint(new ValueLinePoint("07.00", 2.4f));
        series.addPoint(new ValueLinePoint("08.00", 2.4f));
        series.addPoint(new ValueLinePoint("09.00", 3.4f));
        series.addPoint(new ValueLinePoint("10.00", 3.4f));
        series.addPoint(new ValueLinePoint("11.00", 5.2f));
        series.addPoint(new ValueLinePoint("12.00", 7.5f));
        series.addPoint(new ValueLinePoint("13.00", 6.5f));
        series.addPoint(new ValueLinePoint("14.00", 3.5f));
        series.addPoint(new ValueLinePoint("15.00", 4.8f));
        series.addPoint(new ValueLinePoint("16.00", 6.4f));
        series.addPoint(new ValueLinePoint("17.00", 2.3f));
        series.addPoint(new ValueLinePoint("18.00", 6.5f));
        series.addPoint(new ValueLinePoint("19.00", 7.5f));
        series.addPoint(new ValueLinePoint("20.00", 8.5f));
        series.addPoint(new ValueLinePoint("21.00", 9.9f));
        series.addPoint(new ValueLinePoint("22.00", 8.5f));
        series.addPoint(new ValueLinePoint("23.00", 4.2f));
        mChartCO.addSeries(series);
        mChartCO.startAnimation();
        mChartNO2.addSeries(series);
        mChartNO2.startAnimation();
        mChartO3.addSeries(series);
        mChartO3.startAnimation();
        mChartSO2.addSeries(series);
        mChartSO2.startAnimation();
        mChartPM25.addSeries(series);
        mChartPM25.startAnimation();
        mChartRadio.addSeries(series);
        mChartRadio.startAnimation();

    }
}
