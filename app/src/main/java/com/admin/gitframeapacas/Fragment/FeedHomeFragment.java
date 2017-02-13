package com.admin.gitframeapacas.Fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.Bluetooth.BluetoothLeService;
import com.admin.gitframeapacas.Bluetooth.DeviceScanActivity;
import com.admin.gitframeapacas.Bluetooth.SampleGattAttributes;
import com.admin.gitframeapacas.Data.LastDataResponse;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBCurrentLocation;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.admin.gitframeapacas.Service.GPSTracker;
import com.admin.gitframeapacas.Views.RecommendActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static BarChart mBarChart;
    private static String TAG = "BENFeedHomeFragment";
    private static CustomGauge gauge;
    private static TextView txtAQI;
    private static GPSTracker gps;
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
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    ConstraintLayout view2;
    String dust = null;
    String CO = null;
    String NO2 = null;
    int statusCurrent = 1;
    int counter = 0;
    List<String> arrayDust = new ArrayList<String>();
    List<String> arrayCO = new ArrayList<String>();
    List<String> arrayNO2 = new ArrayList<String>();
    private FloatingActionButtonPlus mActionButtonPlus;
    private Snackbar snackbar;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                getActivity().finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private String hrValue;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                getActivity().invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                getActivity().invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //displayDataCO(intent.getStringExtra(BluetoothLeService.EXTRA_DATA1));
                //displayDataNO2(intent.getStringExtra(BluetoothLeService.EXTRA_DATA2));

                dust = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                CO = intent.getStringExtra(BluetoothLeService.EXTRA_DATA1);
                NO2 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA2);

                Log.d(TAG, "test value dust =" + dust + "," + "CO =" + CO + "," + "NO2 =" + NO2);

                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA) != null) {
                    arrayDust.add(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    Log.d(TAG, "Dust List = " + String.valueOf(arrayDust));
                }
                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA1) != null) {
                    arrayCO.add(intent.getStringExtra(BluetoothLeService.EXTRA_DATA1));
                    Log.d(TAG, "CO List = " + String.valueOf(arrayCO));
                }
                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA2) != null) {
                    arrayNO2.add(intent.getStringExtra(BluetoothLeService.EXTRA_DATA2));
                    Log.d(TAG, "NO2 List = " + String.valueOf(arrayNO2));
                }

                Log.d(TAG, "Dust = " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Log.d(TAG, "CO = " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA1));
                Log.d(TAG, "NO2 = " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA2));
            }
        }
    };

    public FeedHomeFragment() {

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_home, container, false);
        mBarChart = (BarChart) v.findViewById(R.id.barchart);
        mActionButtonPlus = (FloatingActionButtonPlus) v.findViewById(R.id.ActionButtonPlus);
        gauge = (CustomGauge) v.findViewById(R.id.gaugeMaster);
        txtAQI = (TextView) v.findViewById(R.id.txtAQI);
        gps = new GPSTracker(getContext());
        lastUpdate = (TextView) v.findViewById(R.id.txtlastUpdate);
        txtLocation = (TextView) v.findViewById(R.id.txtLocation);

        view2 = (ConstraintLayout) v.findViewById(R.id.fragment_home);


        DBUser db = new DBUser(getActivity());
        String user_type = db.getUserType();
        if (user_type.equals("member")) {
            Log.i(TAG, "You are member");


        } else {//user
            Log.i(TAG, "You are user");


        }
        DBCurrentLocation dbcl = new DBCurrentLocation(getContext());

        if (dbcl.numberOfRows() == 1) {
            Log.i(TAG, "already have data");
            Cursor res = dbcl.getAllData();
            if (res.getCount() == 0) {

            } else {

                while (res.moveToNext()) {
                    //String sName = res.getString(0);


                }
            }
        } else {

            loadData();
            Log.i(TAG, "LoadData()");
        }
        return v;
    }

    private void loadData() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final DBUser db = new DBUser(getActivity());
        if (db.getCheckSensor() == 0) {
            alertDialogBuilder.setTitle("APARCAS System");
            alertDialogBuilder
                    .setMessage("คุณมีอุปกรณ์เซ็นเซอร์ตรวจจับสภาพอากาศหรือไม่")
                    .setCancelable(false)
                    .setPositiveButton("มี", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Log.i(TAG, "มีเซ็นเซอร์");
                            //gauge.setVisibility(View.GONE);
                            //txtAQI.setVisibility(View.GONE);
                            dialog.cancel();
                            //db.updateCheckSensor(1);
                            db.updateHaveSensor(1);
                            Intent intent = new Intent(getActivity(), DeviceScanActivity.class);
                            startActivity(intent);


                            Intent intent2 = getActivity().getIntent();
                            mDeviceName = intent2.getStringExtra(EXTRAS_DEVICE_NAME);
                            mDeviceAddress = intent2.getStringExtra(EXTRAS_DEVICE_ADDRESS);


                        }
                    })
                    .setNegativeButton("ไม่มี", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i(TAG, "ไม่มีเซ็เนซอร์");
                            dialog.cancel();
                            snackbar = Snackbar.make(view2, "หากคุณมีเซ็นเซอร์ คุณสามารถเข้าไปเปิดการใช้งานที่ตั้งค่าได้ในภายหลัง", Snackbar.LENGTH_LONG)
                                    .setAction("ปิด", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    });
                            db.updateHaveSensor(0);
                            snackbar.show();
                            DBUser db = new DBUser(getActivity());
                            //db.updateCheckSensor(1);
                            new DataNearby(getActivity()).execute();


                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }


        mActionButtonPlus.setPosition(FloatingActionButtonPlus.POS_RIGHT_TOP);
        //mActionButtonPlus.setPadding(50,100,5,0);


        //mActionButtonPlus.clearAnimation();

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

                }

            }
        });


    }

    private void clearUI() {
        hrValue = "";
    }


    //xxxxxxxxxxxxxxxxxxxxxxxxx bluetoothLE method xxxxxxxxxxxxxxxxxxxxxxxxx
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void updateConnectionState(final int resourceId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    private void displayData(String data) {//write textview for Dust
        if (data != null) {

        }
    }

    private void displayDataCO(String data) {// write textview for CO
        if (data != null) {

        }
    }

    private void displayDataNO2(String data) {// write textview for NO2
        if (data != null) {

        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);


        }
        Timer myTimer;
        myTimer = new Timer();

        myTimer.schedule(new TimerTask() {
            public void run() {

                if (mConnected == true) {//if bluttooth is enable
                    final BluetoothGattCharacteristic characteristic =
                            mGattCharacteristics.get(2).get(counter);
                    if (statusCurrent == 1) {
                        counter = 0;
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);

                        if (dust != null) {
                            Log.d(TAG, "loop1 !=null");
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                            statusCurrent++;
                        } else {
                            Log.d(TAG, "loop1 ==null");
                        }
                    }
                    if (statusCurrent == 2) {
                        counter = 1;
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);
                        if (CO != null) {
                            Log.d(TAG, "loop2 !=null");
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                            statusCurrent++;
                        } else {
                            Log.d(TAG, "loop2 ==null");
                        }
                    }
                    if (statusCurrent == 3) {
                        counter = 2;
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);

                    }
                } else if (mConnected == false) {// if bluetooth disable
                    try {
                        // thread to sleep for 1000 milliseconds
                        Thread.sleep(3000);
                        Log.d(TAG, "BLE is dead");

                        Random r = new Random();
                        dust = arrayDust.get(r.nextInt(arrayDust.size()));
                        CO = arrayCO.get(r.nextInt(arrayCO.size()));
                        NO2 = arrayNO2.get(r.nextInt(arrayNO2.size()));
                        Log.d(TAG, "Dust random = = " + dust);
                        Log.d(TAG, "CO random = = " + CO);
                        Log.d(TAG, "NO2 random = = " + NO2);
                    } catch (Exception e) {

                    }


                }


            }
        }, 0, 1000);
    }

    public static class DataNearby extends AsyncTask<Object, Object, String> {

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

            double lat = gps.getLatitude();
            double lon = gps.getLongitude();

            Log.i(TAG, "lat: " + lat + " lon: " + lon);
            if (lat > 0 && lon > 0) {

                if (dbUser.getHaveSensor() == 0) {// don't have sensor

                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .build();
                    Request request = new Request.Builder()
                            .url("https://www.googleapis.com/fusiontables/v1/query?key=AIzaSyAWgbWYxPgHHo-PU4IuhjjZhjh1PXfhYkc&sql=SELECT SCODE FROM 1x40iw0b31K2vTT6ZiSUxBWUfBbznoppAtERA7S2G WHERE ST_INTERSECTS(geometry, CIRCLE(LATLNG(13.8048817,100.5870658),100))")
                            .post(formBody)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        String result = response.body().string();
                        message = result.replaceAll("\\D+", "");

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
                } else if (dbUser.getHaveSensor() == 1) {// have sensor


                    return "1";
                }
            } else {
                return "3";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("0")) { //don't have sensor
                // Toast.makeText(getApplicationContext(), "ระบบทำการค้นหาสถานที่ใกล้เคียง", Toast.LENGTH_SHORT).show();


                DBCurrentLocation dbCL = new DBCurrentLocation(mContext);
                dbCL.insertData(aqi, co, no2, o3, so2, pm25, rad, tstamp, sname, dname, pname);
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

            } else if (s.equals("1")) { // have sensor

                //Toast.makeText(getApplicationContext(), "ระบบทำการเชื่อมต่อไปยังเซ็นเซอร์", Toast.LENGTH_SHORT).show();

            } else if (s.equals("3")) {
                //Toast.makeText(getApplicationContext(), "กรุณาเปิด GPS ก่อน ระบบถึงสามารถหาข้อมูลที่ใกล้เคียงได้", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
