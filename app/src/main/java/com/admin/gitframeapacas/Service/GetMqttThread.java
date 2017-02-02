package com.admin.gitframeapacas.Service;

import android.util.Log;

import net.sf.xenqtt.client.AsyncClientListener;
import net.sf.xenqtt.client.MqttClient;

import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;


public abstract class GetMqttThread implements Runnable {

    public AsyncClientListener mqttListener = null;
    public MqttClient mqttClient = null;
    public int mqttHandlerThreadPoolSize = 5;
    private BlockingQueue<JSONObject> messageQueue;
    private Thread innerThread;
    private boolean stopInnerThread = false;

    public GetMqttThread() {
    }

    public void start() {
        try {
            createListener();
            createClient();
            innerThread = new Thread(this);
            innerThread.start();
        } catch (Exception e) {

            Log.i("MQT2", "start exception (GetMqttThread): " + e);
        }
    }

    public void stop() {
        try {
            stopInnerThread = true;
            destroyPublisher();
        } catch (Exception e) {
            Log.i("MQT2", "stop exception (GetMqttThread): " + e);
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (!stopInnerThread) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {

                Log.i("MQT2", "run exception (GetMqttThread): " + e);
            }//end try
        }//end while
    }


    public boolean isStopInnerThread() {
        return stopInnerThread;
    }

    public void setStopInnerThread(boolean stopInnerThread) {
        this.stopInnerThread = stopInnerThread;
    }

    public abstract void createListener();

    public abstract void createClient();

    public void destroyPublisher() {
        if (!mqttClient.isClosed()) {
            mqttClient.disconnect();
        }
    }

}
