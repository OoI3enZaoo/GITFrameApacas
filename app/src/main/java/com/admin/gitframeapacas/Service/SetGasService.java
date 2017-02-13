package com.admin.gitframeapacas.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
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


        Toast.makeText(getApplication(), "Start Service!: ", Toast.LENGTH_LONG).show();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (MQTTRunning) {
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

    public class RandomGas {
        Random rand = new Random();
        int ranPosition;

        int id() {

            return rand.nextInt(999999999) + 1111;
        }

        float lat(int option) {

            float latmin;
            float latmax;
            Log.i("ben", "randomPosition(lat): " + option);
            switch (option) {
                case 1:
                    latmin = 13.705845f;
                    latmax = 13.927882f;

                    break;
                case 2:
                    latmin = 13.665243f;
                    latmax = 13.713277f;

                    break;
                case 3:
                    latmin = 13.781308f;
                    latmax = 13.668292f;
                    break;
                default:
                    latmin = 13.623085f;
                    latmax = 13.507395f;
                    break;
            }
            return rand.nextFloat() * (latmax - latmin) + latmin;

        }

        float lon(int option) {
            float lonmin;
            float lonmax;
            Log.i("ben", "randomPosition(lon): " + option);
            switch (option) {
                case 1:
                    lonmin = 100.563011f;
                    lonmax = 100.909767f;
                    break;
                case 2:
                    lonmin = 100.587902f;
                    lonmax = 100.737247f;
                    break;
                case 3:
                    lonmin = 100.344143f;
                    lonmax = 100.514345f;
                    break;
                default:
                    lonmin = 100.423708f;
                    lonmax = 100.442076f;
                    break;
            }
            return rand.nextFloat() * (lonmax - lonmin) + lonmin;

        }

        int co() {
            return rand.nextInt(80) + 5;
        }

        int no2() {
            return rand.nextInt(120) + 15;

        }

        int o3() {
            return rand.nextInt(20) + 1;

        }

        int so2() {
            return rand.nextInt(18) + 5;

        }

        int pm25() {
            return rand.nextInt(350) + 50;
        }

        float rad() {
            float radmin = 0.010f;
            float radmax = 1.100f;
            return rand.nextFloat() * (radmax - radmin) + radmin;
        }

        int tstamp() {
            Date dNow = new Date();
            /*SimpleDateFormat ft =
                    new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
            ft.format(dNow);*/
            long unixTime = System.currentTimeMillis() / 1000;


            return (int) unixTime;
        }

    }


}
