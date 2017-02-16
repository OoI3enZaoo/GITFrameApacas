package com.admin.gitframeapacas.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.Data.LastDataResponse;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBFavorite;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.pawelkleczkowski.customgauge.CustomGauge;

import static java.lang.Float.parseFloat;

public class DistrictActivity extends AppCompatActivity {
    private static String DistrictName;
    private static String DistrictCode;
    private static BarChart mBarChart;
    private static String TAG = "BENDistrictActivity";
    private static String aqi;
    private static String co;
    private static String no2;
    private static String o3;
    private static String so2;
    private static String pm25;
    private static String rad;
    private static String tstamp;
    private static String uid;
    private static TextView lastUpdate;
    private static CustomGauge gauge;
    private static TextView txtAQI;
    Toolbar toolbar;
    private FloatingActionButtonPlus mActionButtonPlus;
    private TextView txtLocation;
    private ConstraintLayout view2;
    private Snackbar snackbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        DistrictName = intent.getStringExtra("district").toString();
        DistrictCode = intent.getStringExtra("scode").toString();
        toolbar.setTitle(DistrictName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        mActionButtonPlus = (FloatingActionButtonPlus) findViewById(R.id.ActionButtonPlus);
        mBarChart = (BarChart) findViewById(R.id.barchart);
        lastUpdate = (TextView) findViewById(R.id.txtlastUpdate);
        view2 = (ConstraintLayout) findViewById(R.id.contraint_district);
        gauge = (CustomGauge) findViewById(R.id.gaugeMaster);
        txtAQI = (TextView) findViewById(R.id.txtAQI);
        loadData();
        if (intent.getStringExtra("co") == null) {
            new getLastDataTask(getApplicationContext()).execute(DistrictCode);
        } else {
            new getDataInSQLiteTask().execute(intent.getStringExtra("co").toString(), intent.getStringExtra("no2").toString(), intent.getStringExtra("o3").toString(), intent.getStringExtra("so2").toString(),
                    intent.getStringExtra("pm25").toString(), intent.getStringExtra("rad").toString(), intent.getStringExtra("district").toString(), intent.getStringExtra("scode").toString(),
                    intent.getStringExtra("tstamp").toString(), intent.getStringExtra("aqi").toString());


            Log.i(TAG, "NOT NULL");
        }
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
                        Toast.makeText(getApplicationContext(), "Navigate to", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DistrictActivity.this, GraphGasActivity.class);
                        startActivity(intent);
                        break;
                    case 2:


                        new AddFavorite().execute();


                        break;

                }

            }
        });

    }

    private static class getLastDataTask extends AsyncTask<String, Void, String> {

        private Context mContext;

        public getLastDataTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "JSONFeed");
        }

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "doinbackground");


            Request request = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/api/LastDataInRecord.jsp?scode=" + strings[0])
                    .build();
            try {
                int rount2 = 0;
                Response response;
                response = client.newCall(request).execute();
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
                //setData(aqi,co,no2,o3,so2,pm25,rad,tstamp,uid);

                Log.i(TAG, "aqi: " + aqi + " co: " + co + " no2: " + no2 + " o3: " + o3 + " so2: " + so2 + " pm25: " + pm25 + " rad: " + rad + " tstamp: " + tstamp + " uid: " + uid);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Float fco = parseFloat(co);
            Float fno2 = parseFloat(no2);
            Float fo3 = parseFloat(o3);
            Float fso2 = parseFloat(so2);
            Float fpm25 = parseFloat(pm25);
            Float frad = parseFloat(rad);

            mBarChart.addBar(new BarModel("CO", fco, Color.parseColor("#91a7ff")));
            mBarChart.addBar(new BarModel("NO2", fno2, Color.parseColor("#42bd41")));
            mBarChart.addBar(new BarModel("O3", fo3, Color.parseColor("#fff176")));
            mBarChart.addBar(new BarModel("SO2", fso2, Color.parseColor("#ffb74d")));
            mBarChart.addBar(new BarModel("PM2.5", fpm25, Color.parseColor("#f36c60")));
            mBarChart.addBar(new BarModel("Radio", frad, Color.parseColor("#ba68c8")));
            mBarChart.startAnimation();
            Log.i(TAG, " co: " + fco + " no2: " + fno2 + " o3: " + fo3 + " so2: " + fso2 + " pm25: " + fpm25 + " rad: " + frad);

            lastUpdate.setText("เวลาล่าสุด: " + tstamp);
            txtAQI.setText("AQI: " + aqi);
            gauge.setValue(Integer.parseInt(aqi));
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

    private class AddFavorite extends AsyncTask<String, Void, String> {

        private String emailStatus = "";

        @Override
        protected String doInBackground(String... strings) {

            DBUser dbuser = new DBUser(getApplicationContext());
            long uid = dbuser.getUserID();
            Log.i(TAG, "doInBackground");
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("uid", String.valueOf(uid))
                    .add("scode", DistrictCode)
                    .build();
            Request request = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/api/addFavorite.jsp")
                    .post(formBody)
                    .build();
            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "1";


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(TAG, "onPostExcute");
            if (s.equals("1")) {
                DBFavorite dbFavorite = new DBFavorite(getApplicationContext());
                Log.i(TAG, "dianame: " + DistrictName + " dcode: " + DistrictCode + " aqi:  " + aqi + " co: " + co + " no2: " + no2 + " o3: " + o3 + " so2: " + so2 + " pm25: " + pm25 + " rad: " + rad + " tstamp: " + tstamp);
                Log.i(TAG, "insert: " + dbFavorite.insertData(DistrictName, DistrictCode, aqi, co, no2, o3, so2, pm25, rad, tstamp));
                Log.i(TAG, "count: " + dbFavorite.numberOfRows());
                snackbar = Snackbar.make(view2, "คุณได้เพิ่ม " + DistrictName + " เป็นรายการโปรดแล้ว", Snackbar.LENGTH_LONG)
                        .setAction("ปิด", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                snackbar.show();


            } else {


            }


        }
    }


    private class getDataInSQLiteTask extends AsyncTask<String, Void, String> {

        String mCO;
        String mNO2;
        String mO3;
        String mSO2;
        String mPM25;
        String mRad;
        String mSname;
        String mScode;
        String mTime;
        String mAQI;

        @Override
        protected String doInBackground(String... strings) {

            mCO = strings[0];
            mNO2 = strings[1];
            mO3 = strings[2];
            mSO2 = strings[3];
            mPM25 = strings[4];
            mRad = strings[5];
            mSname = strings[6];
            mScode = strings[7];
            mTime = strings[8];
            mAQI = strings[9];

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Float fco = parseFloat(mCO);
            Float fno2 = parseFloat(mNO2);
            Float fo3 = parseFloat(mO3);
            Float fso2 = parseFloat(mSO2);
            Float fpm25 = parseFloat(mPM25);
            Float frad = parseFloat(mRad);

            mBarChart.addBar(new BarModel("CO", fco, Color.parseColor("#91a7ff")));
            mBarChart.addBar(new BarModel("NO2", fno2, Color.parseColor("#42bd41")));
            mBarChart.addBar(new BarModel("O3", fo3, Color.parseColor("#fff176")));
            mBarChart.addBar(new BarModel("SO2", fso2, Color.parseColor("#ffb74d")));
            mBarChart.addBar(new BarModel("PM2.5", fpm25, Color.parseColor("#f36c60")));
            mBarChart.addBar(new BarModel("Radio", frad, Color.parseColor("#ba68c8")));
            mBarChart.startAnimation();
            Log.i(TAG, " co: " + fco + " no2: " + fno2 + " o3: " + fo3 + " so2: " + fso2 + " pm25: " + fpm25 + " rad: " + frad);

            lastUpdate.setText("เวลาล่าสุด: " + mTime);
            txtAQI.setText("AQI: " + mAQI);
            gauge.setValue(Integer.parseInt(mAQI));
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
}
