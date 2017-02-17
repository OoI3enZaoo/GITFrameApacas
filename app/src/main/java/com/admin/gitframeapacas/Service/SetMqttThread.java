package com.admin.gitframeapacas.Service;


import android.util.Log;

import net.sf.xenqtt.client.AsyncClientListener;
import net.sf.xenqtt.client.AsyncMqttClient;
import net.sf.xenqtt.client.MqttClient;
import net.sf.xenqtt.client.PublishMessage;
import net.sf.xenqtt.client.Subscription;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SetMqttThread implements Runnable {

    public AsyncClientListener mqttListener = null;
    public MqttClient mqttClient = null;
    public int mqttHandlerThreadPoolSize = 5;
    private BlockingQueue<JSONObject> messageQueue;
    private Thread innerThread;
    private boolean stopInnerThread = false;
    private String mqttBrokerURL;
    private String mqttClientId;
    private boolean mqttCleanSession = false;
    private String mqttUser;
    private String mqttPwd;
    private String topicName;
    private String sssn;
    private int counter = 0;
    private String TAG = "BENSetMqttThread";

    public SetMqttThread(String sssn, BlockingQueue<JSONObject> messageQueue, String mqttBrokerURL, String mqttUser, String mqttPwd) {
        super();
        this.messageQueue = messageQueue;
        this.mqttBrokerURL = mqttBrokerURL;
        this.mqttClientId = sssn + this.hashCode();
        this.sssn = sssn;
        this.topicName = sssn;
        this.mqttUser = mqttUser;
        this.mqttPwd = mqttPwd;
    }

    public SetMqttThread() {

    }

    public void start() {
        try {
            createPublisher();
            innerThread = new Thread(this);
            innerThread.start();
        } catch (Exception e) {

            Log.i(TAG, "start exception (SetMqttThread): " + e);
        }
    }

    public void stop() {
        try {
            stopInnerThread = true;
            destroyPublisher();
        } catch (Exception e) {
            Log.i(TAG, "stop exception (SetMqttThread): " + e);
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (!stopInnerThread) {
            try {
                Thread.sleep(100);
                JSONObject event = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                if (event != null) sendMessage(event);
            } catch (Exception e) {

                Log.i(TAG, "run exception (SetMqttThread): " + e);
            }//end try
        }//end while
    }

    public void sendMessage(JSONObject obj) throws JSONException {
        //counter++;

        mqttClient.publish(new PublishMessage(topicName, QoS.AT_MOST_ONCE, String.valueOf(obj)));
    }


    public void createPublisher() {
        final CountDownLatch connectLatch = new CountDownLatch(1);
        final AtomicReference<ConnectReturnCode> connectReturnCode = new AtomicReference<ConnectReturnCode>();
        mqttListener = new AsyncClientListener() {
            @Override
            public void publishReceived(MqttClient client, PublishMessage message) {
                Log.i(TAG, "Received a message when no subscriptions were active. Check your broker ");
            }

            @Override
            public void disconnected(MqttClient client, Throwable cause, boolean reconnecting) {
                if (cause != null) {
                    Log.i(TAG, "Disconnected from the broker due to an exception - " + cause);
                } else {
                    Log.i(TAG, "Disconnected from the broker.");
                }

                if (reconnecting) {
                    Log.i(TAG, "Attempting to reconnect to the broker.");
                }
            }

            @Override
            public void connected(MqttClient client, ConnectReturnCode returnCode) {
                connectReturnCode.set(returnCode);
                connectLatch.countDown();
            }

            @Override
            public void subscribed(MqttClient client, Subscription[] requestedSubscriptions, Subscription[] grantedSubscriptions, boolean requestsGranted) {
            }

            @Override
            public void unsubscribed(MqttClient client, String[] topics) {
            }

            @Override
            public void published(MqttClient client, PublishMessage message) {
            }

        };

        mqttClient = new AsyncMqttClient(mqttBrokerURL, mqttListener, mqttHandlerThreadPoolSize);
        try {
            // Connect to the broker. We will await the return code so that we know whether or not we can even begin publishing.
            mqttClient.connect(mqttClientId, mqttCleanSession, mqttUser, mqttPwd);
            connectLatch.await();

            ConnectReturnCode returnCode = connectReturnCode.get();
            if (returnCode == null || returnCode != ConnectReturnCode.ACCEPTED) {
                Log.i(TAG, "The broker rejected our attempt to connect - Reason: " + returnCode);
                //return;
            }

        } catch (Exception e) {
            Log.i(TAG, "An exception prevented the publishing of the full catalog." + e);
        }
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
