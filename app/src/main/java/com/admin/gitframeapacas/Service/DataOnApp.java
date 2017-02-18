package com.admin.gitframeapacas.Service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.admin.gitframeapacas.Data.LastDataResponse;
import com.admin.gitframeapacas.SQLite.DBCurrentLocation;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataOnApp extends Service {

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
    private static GPSTracker gps;
    private String TAG = "BENDateOnApp";
    private double lat = 0.0d;
    private double lon = 0.0d;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new GPSTracker(getApplicationContext());
        lat = gps.getLatitude();
        lon = gps.getLongitude();


        Toast.makeText(getApplicationContext(), "start Dataonapp lat: " + lat + " lon: " + lon, Toast.LENGTH_SHORT).show();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                new DataNearby().execute();
                Log.i(TAG, "lat: " + lat + " lon: " + lon);
            }
        }, 0, 2000);

    }

    private class DataNearby extends AsyncTask<Object, Object, String> {

        private DBUser dbUser;
        private String message;
        private Context mContext;

        public DataNearby(Context context) {
            mContext = context;
        }

        public DataNearby() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbUser = new DBUser(mContext);

        }

        @Override
        protected String doInBackground(Object... strings) {

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
                DBCurrentLocation dbCL = new DBCurrentLocation(getApplicationContext());
                dbCL.drop();
                dbCL.insertData(aqi, co, no2, o3, so2, pm25, rad, tstamp, sname, dname, pname);
                /*Float fco = parseFloat(co);
                Float fno2 = parseFloat(no2);
                Float fo3 = parseFloat(o3);
                Float fso2 = parseFloat(so2);
                Float fpm25 = parseFloat(pm25);
                Float frad = parseFloat(rad);
                frad = frad * 1000;
                homeFragment.setDataFromService(fco, fno2, fo3, fso2, fpm25, frad, sname, dname, pname);*/

            }
        }
    }

}

