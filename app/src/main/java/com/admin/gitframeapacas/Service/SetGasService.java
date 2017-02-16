package com.admin.gitframeapacas.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Admin on 29/1/2560.
 */

public class SetGasService extends Service {
    public static boolean MQTTRunning = true;
    private static GPSTracker gps;
    private BlockingQueue<JSONObject> messageQueue = new LinkedBlockingQueue<JSONObject>();
    private SetMqttThread setMqttThread = null;
    private String mqttBrokerURL = "tcp://sysnet.utcc.ac.th:1883";
    private String mqttUser = "admin";
    private String mqttPwd = "admin";
    private String sssn = "aparcas_raw";
    private String TAG = "BENSetGasService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {

        gps = new GPSTracker(getApplicationContext());

        Toast.makeText(getApplication(), "Start Service!: " + gps.getLatitude() + " lon: " + gps.getLongitude(), Toast.LENGTH_LONG).show();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (MQTTRunning) {

                    Log.i(TAG, "lat: " + gps.getLatitude());
                    Log.i(TAG, "long: " + gps.getLongitude());
                    MQTTSender();

                }
            }
        }, 0, 3000);
    }
    public void onDestroy() {
        Toast.makeText(getApplication(), "Stop Service!", Toast.LENGTH_LONG).show();
    }

    public void MQTTSender() {
        JSONObject obj = new JSONObject();
        try {
            Random rand = new Random();
            int option = rand.nextInt(4) + 1;
            obj.put("uid", new RandomGas().id());
            obj.put("lat", new RandomGas().lat(option));
            obj.put("lon", new RandomGas().lon(option));
            obj.put("co", new RandomGas().co());
            obj.put("no2", new RandomGas().no2());
            obj.put("o3", new RandomGas().o3());
            obj.put("so2", new RandomGas().so2());
            obj.put("pm25", new RandomGas().pm25());
            obj.put("rad", new RandomGas().rad());
            obj.put("ts", new RandomGas().tstamp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            messageQueue.put(obj);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setMqttThread = new SetMqttThread(sssn, messageQueue, mqttBrokerURL, mqttUser, mqttPwd) {
            @Override
            public void createListener() {

            }

            @Override
            public void createClient() {

            }
        };
        setMqttThread.start();

    }// end of Random


}
