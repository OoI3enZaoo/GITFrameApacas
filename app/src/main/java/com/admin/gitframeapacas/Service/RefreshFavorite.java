package com.admin.gitframeapacas.Service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.admin.gitframeapacas.Data.LastDataResponse;
import com.admin.gitframeapacas.SQLite.DBFavorite;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RefreshFavorite extends Service {
    private static String aqi;
    private static String co;
    private static String no2;
    private static String o3;
    private static String so2;
    private static String pm25;
    private static String rad;
    private static String tstamp;
    private static String sname;
    private String TAG = "BENRefreshFavorite";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {


        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Refresh();
            }
        }, 0, 60000);
    }

    private void Refresh() {
        DBFavorite dbFavorite = new DBFavorite(getApplicationContext());
        Cursor res = dbFavorite.getAllData();
        if (res.getCount() == 0) {
            Log.i(TAG, "Nothing found");
        } else {
            while (res.moveToNext()) {
                String scode = res.getString(1);
                new RefreshTask().execute(scode);
            }
        }
    }

    class RefreshTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String scode = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request request2 = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/api/LastDataInRecord.jsp?scode=" + scode)
                    .build();
            try {
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

                sname = result[0].getSname();
                //Log.i(TAG, "aqi: " + aqi + " co: " + co + " no2: " + no2 + " o3: " + o3 + " so2: " + so2 + " pm25: " + pm25 + " rad: " + rad + " tstamp: " + tstamp + " uid: " + uid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return scode;
        }

        @Override
        protected void onPostExecute(String scode) {
            super.onPostExecute(scode);
            DBFavorite dbFavorite = new DBFavorite(getApplicationContext());
            Log.i(TAG, "Update:" + dbFavorite.UpdateData(scode, sname, aqi, co, no2, o3, so2, pm25, rad, tstamp));
        }
    }
}



