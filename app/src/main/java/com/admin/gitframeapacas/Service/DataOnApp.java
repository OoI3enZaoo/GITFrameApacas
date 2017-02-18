package com.admin.gitframeapacas.Service;


import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.admin.gitframeapacas.Data.LastDataResponse;
import com.admin.gitframeapacas.Fragment.FeedHomeFragment;
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
    Timer timer;
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
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                new DataNearby().execute();
                Log.i(TAG, "lat: " + lat + " lon: " + lon);
            }
        }, 0, 5000);


    }

    @Override
    public void onDestroy() {
        stopUpdates();
        super.onDestroy();

    }

    private void stopUpdates() {

        timer.cancel();
        timer.purge();
        timer = null;

    }
    private class DataNearby extends AsyncTask<Object, Object, String> {

        private DBUser dbUser;
        private String message;


        public DataNearby() {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbUser = new DBUser(getApplicationContext());
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
                    aqi = result[0].getAqi().toString();
                    co = result[0].getCo().toString();
                    no2 = result[0].getNo2().toString();
                    o3 = result[0].getO3().toString();
                    so2 = result[0].getSo2().toString();
                    pm25 = result[0].getPm25().toString();
                    rad = result[0].getRad().toString();
                    tstamp = result[0].getTstamp().toString();
                    uid = result[0].getUser_id().toString();
                    sname = result[0].getSname().toString();
                    dname = result[0].getDname().toString();
                    pname = result[0].getPname().toString();

                    Log.i(TAG, "message: aqi: " + message + "aqi: " + aqi + " co: " + co + " no2: " + no2 + " o3: " + o3 + " so2: " + so2 + " pm25: " + pm25 + " rad: " + rad + " tstamp: " + tstamp + " uid: " + uid);

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
            boolean CheckSame = false;
            if (s.equals("0")) { //don'timer have sensor
                // Toast.makeText(getApplicationContext(), "ระบบทำการค้นหาสถานที่ใกล้เคียง", Toast.LENGTH_SHORT).show();
                DBCurrentLocation dbCL = new DBCurrentLocation(getApplicationContext());


                String res_tstamp = "";

                Cursor res = dbCL.getAllData();
                while (res.moveToNext()) {
                    res_tstamp = res.getString(7).toString();
                }
                Log.i(TAG, "CheckSame tstamp: " + tstamp + " reststamp: " + res_tstamp);
                if (tstamp.equals(res_tstamp)) {
                    CheckSame = true;
                } else if (dbUser.getCheckSensor() == 0 || !tstamp.equals(res_tstamp)) {
                    CheckSame = false;
                    dbUser.updateCheckSensor(1);
                    DBCurrentLocation dbcl = new DBCurrentLocation(getApplicationContext());
                    dbcl.insertData(aqi, co, no2, o3, so2, pm25, rad, tstamp, sname, dname, pname);
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(FeedHomeFragment.mBroadcastStringAction);
                    broadcastIntent.putExtra("aqi", aqi);
                    broadcastIntent.putExtra("co", co);
                    broadcastIntent.putExtra("no2", no2);
                    broadcastIntent.putExtra("o3", o3);
                    broadcastIntent.putExtra("so2", so2);
                    broadcastIntent.putExtra("pm25", pm25);
                    broadcastIntent.putExtra("rad", rad);
                    broadcastIntent.putExtra("tstamp", tstamp);
                    broadcastIntent.putExtra("sname", sname);
                    broadcastIntent.putExtra("dname", dname);
                    broadcastIntent.putExtra("pname", pname);
                    sendBroadcast(broadcastIntent);


                }
                Log.i(TAG, "CheckSame : " + CheckSame);





            }
        }
    }

}

