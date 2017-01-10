package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

    private ValueLineChart mCubicValueLineChart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Graph");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        loadData();
    }

    private void loadData() {


        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
        series.addPoint(new ValueLinePoint("00.00", 2.4f));
        series.addPoint(new ValueLinePoint("01.00", 3.4f));
        series.addPoint(new ValueLinePoint("02.00", .4f));
        series.addPoint(new ValueLinePoint("03.00", 1.2f));
        series.addPoint(new ValueLinePoint("04.00", 2.6f));
        series.addPoint(new ValueLinePoint("05.00", 1.0f));
        series.addPoint(new ValueLinePoint("06.00", 3.5f));
        series.addPoint(new ValueLinePoint("07.00", 2.4f));
        series.addPoint(new ValueLinePoint("08.00", 2.4f));
        series.addPoint(new ValueLinePoint("09.00", 3.4f));
        series.addPoint(new ValueLinePoint("10.00", .4f));
        series.addPoint(new ValueLinePoint("11.00", 1.3f));

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();

    }
}
