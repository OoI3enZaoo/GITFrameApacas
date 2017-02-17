package com.admin.gitframeapacas.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.Data.LastDataResponse;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBCurrentLocation;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.admin.gitframeapacas.Service.RandomGas;
import com.admin.gitframeapacas.Views.RecommendActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.pawelkleczkowski.customgauge.CustomGauge;

import static java.lang.Float.parseFloat;


/**
 * Created by Admin on 12/11/2559.
 */

public class FeedHomeFragment extends Fragment {


    //xxxxxxx bluetooth le xxxxxxx

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
    private static TextView txtMode;
    ConstraintLayout view3;
    private FloatingActionButtonPlus mActionButtonPlus;


    public FeedHomeFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_home, container, false);
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
                    case 1:
                        Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
                        new DataNearby(getActivity()).execute();
                        break;

                    case 2:
                        DBUser dbUser = new DBUser(getActivity());
                        Toast.makeText(getActivity(), "Switch", Toast.LENGTH_SHORT).show();
                        if (dbUser.getHaveSensor() == 0) {
                            mBarChart.clearChart();
                            dbUser.updateHaveSensor(1);
                            txtMode.setText("โหมดเก็บค่าสภาพอากาศ");
                        } else if (dbUser.getHaveSensor() == 1) {
                            mBarChart.clearChart();
                            dbUser.updateHaveSensor(0);
                            txtMode.setText("โหมดอ่านค่าสภาพอากาศ");
                            new DataNearby(getActivity()).execute();
                        }
                        break;
                }
            }
        });
        gauge = (CustomGauge) v.findViewById(R.id.gaugeMaster);
        txtAQI = (TextView) v.findViewById(R.id.txtAQI);
        lastUpdate = (TextView) v.findViewById(R.id.txtlastUpdate);
        txtLocation = (TextView) v.findViewById(R.id.txtLocation);
        txtMode = (TextView) v.findViewById(R.id.txtMode);

        view3 = (ConstraintLayout) v.findViewById(R.id.contrant);
        loadData();
        return v;
    }

    public void loadData() {
        DBUser dbUser = new DBUser(getActivity());
        if (dbUser.getCheckSensor() == 1) {
            Log.i(TAG, "already have data");
            txtMode.setText("โหมดอ่านค่าสภาพอากาศ");
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
                Float fco = parseFloat(sCO);
                Float fno2 = parseFloat(sNO2);
                Float fo3 = parseFloat(sO3);
                Float fso2 = parseFloat(sSO2);
                Float fpm25 = parseFloat(sPM25);
                Float frad = parseFloat(sRAD);
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
            new DataNearby(getActivity()).execute();
        }
    }
    /*public void setLocation() {
        Log.i(TAG, "setData lat " + gps.getLatitude() + " lon: " + gps.getLongitude());
        new dataTask(getActivity()).execute(gps.getLatitude(), gps.getLongitude());
    }*/

    public void setNO2(Float no2, boolean status) {
        checkNo2 = status;
        sensor_no2 = no2 * 1000;
        Log.i(TAG, "no2: " + sensor_no2 + " true");
        setBar();
    }

    public void setCO(Float co, boolean status) {
        checkCO = status;
        sensor_co = co * 1000;
        Log.i(TAG, "co: " + sensor_co + " true");
        setBar();
    }

    public void setDust(Float dust, boolean status) {
        checkPM25 = status;
        sensor_pm25 = dust * 1000;
        Log.i(TAG, "pm25: " + sensor_pm25 + " true");
        setBar();
    }

    private void setBar() {
        Log.i(TAG, "checkno2: " + checkNo2 + " checkco: " + checkCO + " checkpm25: " + checkPM25);
        if (checkNo2 == true && checkCO == true && checkPM25 == true) {
            Log.i(TAG, "All true");
            mBarChart.clearChart();
            RandomGas gas = new RandomGas();
            float randO3 = gas.o3();
            float randSO2 = gas.so2();
            mBarChart.addBar(new BarModel("CO", sensor_co, Color.parseColor("#91a7ff")));
            mBarChart.addBar(new BarModel("NO2", sensor_no2, Color.parseColor("#42bd41")));
            mBarChart.addBar(new BarModel("O3", randO3, Color.parseColor("#fff176")));
            mBarChart.addBar(new BarModel("SO2", randSO2, Color.parseColor("#ffb74d")));
            mBarChart.addBar(new BarModel("PM2.5", sensor_pm25, Color.parseColor("#f36c60")));
            mBarChart.startAnimation();
            checkNo2 = false;
            checkCO = false;
            checkPM25 = false;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            lastUpdate.setText(String.format("เวลาล่าสุด: " + dateFormat.format(date)));
            txtMode.setText("โหมดเก็บค่าสภาพอากาศ");
        }
    }


    public class dataTask extends AsyncTask<Double, Object, String> {
        private Context mContext;

        public dataTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Double... location) {
            double lat = location[0];
            double lon = location[1];
            String data = "https://www.googleapis.com/fusiontables/v1/query?key=AIzaSyAWgbWYxPgHHo-PU4IuhjjZhjh1PXfhYkc&sql=SELECT SNAME FROM 1x40iw0b31K2vTT6ZiSUxBWUfBbznoppAtERA7S2G WHERE ST_INTERSECTS(geometry, CIRCLE(LATLNG(" + lat + "," + lon + "),1))";
            if (lat > 0 && lon > 0) {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url(data)
                        .post(formBody)
                        .build();
                String sname = "";

                String result = "";
                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().string();

                    result = result.replaceAll("\\s+", "");
                    result = result.replaceAll("\\W+", "");
                    result = result.replaceAll("[a-zA-Z]", "");
                    Log.i(TAG, "sname: " + result);



                    /*sname = result.replaceAll("\\D+", "");
                    if (sname.length() > 6) {
                        sname = sname.substring(0, 6);
                    }
*/

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
            return "";
        }
        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);
            if (!name.equals("")) {
                txtLocation.setText(name);
            }

        }
    }

    private class DataNearby extends AsyncTask<Object, Object, String> {

        private DBUser dbUser;
        private String message;
        private Context mContext;

        public DataNearby(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbUser = new DBUser(mContext);
        }
        @Override
        protected String doInBackground(Object... strings) {
            double lat = 0.0d;
            double lon = 0.0d;

            Log.i(TAG, "lat: " + lat + " lon: " + lon);
            if (lat > 0 && lon > 0) {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://www.googleapis.com/fusiontables/v1/query?key=AIzaSyAWgbWYxPgHHo-PU4IuhjjZhjh1PXfhYkc&sql=SELECT SCODE FROM 1x40iw0b31K2vTT6ZiSUxBWUfBbznoppAtERA7S2G WHERE ST_INTERSECTS(geometry, CIRCLE(LATLNG(" + lat + "," + lon + "),1))")
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    message = result.replaceAll("\\D+", "");
                    if (message.length() > 6) {
                        message = message.substring(0, 6);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                Request request2 = new Request.Builder()
                        .url("http://sysnet.utcc.ac.th/aparcas/api/LastDataInRecord.jsp?scode=" + message)
                        .build();
                try {
                    int rount2 = 0;
                    Response response;
                    response = client.newCall(request2).execute();
                    String result2 = response.body().string();
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<LastDataResponse>>() {
                    }.getType();
                    Collection<LastDataResponse> enums = gson.fromJson(result2, collectionType);
                    LastDataResponse[] result = enums.toArray(new LastDataResponse[enums.size()]);

                    aqi = result[0].getAqi();
                    co = result[0].getCo();
                    no2 = result[0].getNo2();
                    o3 = result[0].getO3();
                    so2 = result[0].getSo2();
                    pm25 = result[0].getPm25();
                    rad = result[0].getRad();
                    tstamp = result[0].getTstamp();
                    uid = result[0].getUser_id();
                    sname = result[0].getSname();
                    dname = result[0].getDname();
                    pname = result[0].getPname();
                    Log.i(TAG, "aqi: " + aqi + " co: " + co + " no2: " + no2 + " o3: " + o3 + " so2: " + so2 + " pm25: " + pm25 + " rad: " + rad + " tstamp: " + tstamp + " uid: " + uid);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "0";

            } else {
                return "3";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("0")) { //don't have sensor
                // Toast.makeText(getApplicationContext(), "ระบบทำการค้นหาสถานที่ใกล้เคียง", Toast.LENGTH_SHORT).show();
                DBCurrentLocation dbCL = new DBCurrentLocation(mContext);
                dbCL.drop();
                dbCL.insertData(aqi, co, no2, o3, so2, pm25, rad, tstamp, sname, dname, pname);
                Float fco = parseFloat(co);
                Float fno2 = parseFloat(no2);
                Float fo3 = parseFloat(o3);
                Float fso2 = parseFloat(so2);
                Float fpm25 = parseFloat(pm25);
                Float frad = parseFloat(rad);
                frad = frad * 1000;
                mBarChart.addBar(new BarModel("CO", fco, Color.parseColor("#91a7ff")));
                mBarChart.addBar(new BarModel("NO2", fno2, Color.parseColor("#42bd41")));
                mBarChart.addBar(new BarModel("O3", fo3, Color.parseColor("#fff176")));
                mBarChart.addBar(new BarModel("SO2", fso2, Color.parseColor("#ffb74d")));
                mBarChart.addBar(new BarModel("PM2.5", fpm25, Color.parseColor("#f36c60")));
                mBarChart.addBar(new BarModel("Radio", frad, Color.parseColor("#ba68c8")));
                mBarChart.startAnimation();
                Log.i(TAG, " co: " + fco + " no2: " + fno2 + " o3: " + fo3 + " so2: " + fso2 + " pm25: " + fpm25 + " rad: " + frad);

                txtLocation.setText(sname + " " + dname + " " + pname);
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
                DBUser dbUser = new DBUser(mContext);
                dbUser.updateCheckSensor(1);

            } else if (s.equals("1")) { // have sensor

                //Toast.makeText(getApplicationContext(), "ระบบทำการเชื่อมต่อไปยังเซ็นเซอร์", Toast.LENGTH_SHORT).show();

            } else if (s.equals("3")) {
                //Toast.makeText(getApplicationContext(), "กรุณาเปิด GPS ก่อน ระบบถึงสามารถหาข้อมูลที่ใกล้เคียงได้", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
