package com.admin.gitframeapacas;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Admin on 17/11/2559.
 */

public class GraphGasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        initGraph(graph);
    }

    public void initGraph(final GraphView graph) {

        Button btn = (Button) findViewById(R.id.btnAddSeries);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph.addSeries(new LineGraphSeries(generateData()));



            }

        });
        btn = (Button) findViewById(R.id.btnClearSeries);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph.removeAllSeries();
            }
        });
    }

    private DataPoint[] generateData() {
        Random rand = new Random();
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = rand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + rand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
}
