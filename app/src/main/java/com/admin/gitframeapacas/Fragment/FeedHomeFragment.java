package com.admin.gitframeapacas.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBCurrentLocation;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.admin.gitframeapacas.Service.GPSTracker;
import com.admin.gitframeapacas.Views.RecommendActivity;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import pl.pawelkleczkowski.customgauge.CustomGauge;

import static java.lang.Float.parseFloat;


/**
 * Created by Admin on 12/11/2559.
 */

public class FeedHomeFragment extends Fragment {


    //xxxxxxx bluetooth le xxxxxxx

    public static final String mBroadcastStringAction = "com.truiton.broadcast.string";
    public static BarChart mBarChart;
    public static Float sensor_no2 = 0.0f;
    public static Float sensor_co = 0.0f;
    public static Float sensor_pm25 = 0.0f;
    public static boolean checkNo2;
    public static boolean checkCO;
    public static boolean checkPM25;
    private static String TAG = "BENFeedHomeFragment";
    private static CustomGauge gauge;
    private static TextView txtAQI;
    private static String aqi;
    private static String co;
    private static String no2;
    private static String o3;
    private static String so2;
    private static String pm25;
    private static String rad;
    private static String tstamp;
    private static String uid;
    private static String sname;
    private static String dname;
    private static String pname;
    private static TextView lastUpdate;
    private static TextView txtLocation;
    private static GPSTracker gps;
    ConstraintLayout view3;
    private double lat = 0.0d;
    private double lon = 0.0d;
    private FloatingActionButtonPlus mActionButtonPlus;
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastStringAction)) {
                mBarChart.clearChart();
                String co = intent.getStringExtra("co");
                String aqi = intent.getStringExtra("aqi");
                String no2 = intent.getStringExtra("no2");
                String o3 = intent.getStringExtra("o3");
                String so2 = intent.getStringExtra("so2");
                String pm25 = intent.getStringExtra("pm25");
                String rad = intent.getStringExtra("rad");
                String tstamp = intent.getStringExtra("tstamp");
                String sname = intent.getStringExtra("sname");
                String dname = intent.getStringExtra("dname");
                String pname = intent.getStringExtra("pname");
                Float fco = parseFloat(co) * 1000;
                Float fno2 = parseFloat(no2) * 1000;
                Float fo3 = parseFloat(o3) * 1000;
                Float fso2 = parseFloat(so2) * 1000;
                Float fpm25 = parseFloat(pm25);
                Float frad = parseFloat(rad) * 1000;
                mBarChart.addBar(new BarModel("CO", fco, Color.parseColor("#91a7ff")));
                mBarChart.addBar(new BarModel("NO2", fno2, Color.parseColor("#42bd41")));
                mBarChart.addBar(new BarModel("O3", fo3, Color.parseColor("#fff176")));
                mBarChart.addBar(new BarModel("SO2", fso2, Color.parseColor("#ffb74d")));
                mBarChart.addBar(new BarModel("PM2.5", fpm25, Color.parseColor("#f36c60")));
                mBarChart.addBar(new BarModel("Radio", frad, Color.parseColor("#ba68c8")));
                mBarChart.startAnimation();
                Log.i(TAG, "BroadcastReceiver");
                txtLocation.setText(dname + " " + sname + " " + pname);
                lastUpdate.setText(tstamp.toString() + "");
                gauge.setValue(Integer.parseInt(aqi));
                txtAQI.setText("AQI: " + aqi);
                if (gauge.getValue() > 0) {
                    gauge.setPointStartColor(Color.parseColor("#91a7ff"));

                }
                if (gauge.getValue() > 50) {
                    gauge.setPointStartColor(Color.parseColor("#42bd41"));
                }
                if (gauge.getValue() > 100) {
                    gauge.setPointStartColor(Color.parseColor("#fff176"));

                }
                if (gauge.getValue() > 200) {
                    gauge.setPointStartColor(Color.parseColor("#ffb74d"));
                }
                if (gauge.getValue() > 300) {
                    gauge.setPointStartColor(Color.parseColor("#f36c60"));
                }

            }
        }
    };

    public FeedHomeFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_home, container, false);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        gps = new GPSTracker(getActivity());
        lat = gps.getLatitude();
        lon = gps.getLongitude();

        mBarChart = (BarChart) v.findViewById(R.id.barchart);
        mActionButtonPlus = (FloatingActionButtonPlus) v.findViewById(R.id.ActionButtonPlus);
        mActionButtonPlus.setPosition(FloatingActionButtonPlus.POS_RIGHT_TOP);

        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {

            @Override
            public void onItemClick(FabTagLayout tagView, int position) {

                switch (position) {
                    case 0:
                        Toast.makeText(getActivity(), "Tips", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(getActivity(), RecommendActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        });
        gauge = (CustomGauge) v.findViewById(R.id.gaugeMaster);
        txtAQI = (TextView) v.findViewById(R.id.txtAQI);
        lastUpdate = (TextView) v.findViewById(R.id.txtlastUpdate);
        txtLocation = (TextView) v.findViewById(R.id.txtLocation);


        view3 = (ConstraintLayout) v.findViewById(R.id.contrant);
        loadData();
        return v;
    }

    public void loadData() {
        DBUser dbUser = new DBUser(getActivity());
        if (dbUser.getCheckSensor() == 1) {
            Log.i(TAG, "already have data");
            DBCurrentLocation dbcl = new DBCurrentLocation(getActivity());
            Cursor res = dbcl.getAllData();
            while (res.moveToNext()) {
                String sAQI = res.getString(0);
                String sCO = res.getString(1);
                String sNO2 = res.getString(2);
                String sO3 = res.getString(3);
                String sSO2 = res.getString(4);
                String sPM25 = res.getString(5);
                String sRAD = res.getString(6);
                String sTstamp = res.getString(7);
                String sSname = res.getString(8);
                String sDname = res.getString(9);
                String sPname = res.getString(10);
                Float fco = parseFloat(sCO) * 1000;
                Float fno2 = parseFloat(sNO2) * 1000;
                Float fo3 = parseFloat(sO3) * 1000;
                Float fso2 = parseFloat(sSO2) * 1000;
                Float fpm25 = parseFloat(sPM25);
                Float frad = parseFloat(sRAD) * 1000;
                mBarChart.addBar(new BarModel("CO", fco, Color.parseColor("#91a7ff")));
                mBarChart.addBar(new BarModel("NO2", fno2, Color.parseColor("#42bd41")));
                mBarChart.addBar(new BarModel("O3", fo3, Color.parseColor("#fff176")));
                mBarChart.addBar(new BarModel("SO2", fso2, Color.parseColor("#ffb74d")));
                mBarChart.addBar(new BarModel("PM2.5", fpm25, Color.parseColor("#f36c60")));
                mBarChart.addBar(new BarModel("Radio", frad, Color.parseColor("#ba68c8")));
                mBarChart.startAnimation();
                Log.i(TAG, "already: " + " co: " + fco + " no2: " + fno2 + " o3: " + fo3 + " so2: " + fso2 + " pm25: " + fpm25 + " rad: " + frad);

                txtLocation.setText(sDname + " " + sSname + " " + sPname);
                lastUpdate.setText(sTstamp.toString() + "");
                gauge.setValue(Integer.parseInt(sAQI));
                txtAQI.setText("AQI: " + sAQI);
                if (gauge.getValue() > 0) {
                    gauge.setPointStartColor(Color.parseColor("#91a7ff"));

                }
                if (gauge.getValue() > 50) {
                    gauge.setPointStartColor(Color.parseColor("#42bd41"));
                }
                if (gauge.getValue() > 100) {
                    gauge.setPointStartColor(Color.parseColor("#fff176"));

                }
                if (gauge.getValue() > 200) {
                    gauge.setPointStartColor(Color.parseColor("#ffb74d"));
                }
                if (gauge.getValue() > 300) {
                    gauge.setPointStartColor(Color.parseColor("#f36c60"));
                }
            }
        } else if (dbUser.getCheckSensor() == 0) {


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }
}
