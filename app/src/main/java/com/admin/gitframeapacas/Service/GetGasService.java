package com.admin.gitframeapacas.Service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.sf.xenqtt.client.AsyncClientListener;
import net.sf.xenqtt.client.AsyncMqttClient;
import net.sf.xenqtt.client.MqttClient;
import net.sf.xenqtt.client.PublishMessage;
import net.sf.xenqtt.client.Subscription;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Admin on 2/2/2560.
 */

public class GetGasService extends Service {
    private String TAG = "BENGetGasService";
    private String mqttBrokerURL = "tcp://sysnet.utcc.ac.th:1883";
    private String mqttUser = "admin";
    private String mqttPwd = "admin";
    private Hashtable<String, GetMqttThread> mqttThreadHT = new Hashtable<String, GetMqttThread>();
    private String sssn = "admin";
    private String topic = "aparcas_raw";

    @Nullable


    @Override
    public void onCreate() {
        super.onCreate();


        GetMqttThread mqttThread = createMQTTThread(sssn, topic);
        mqttThread.start();
        mqttThreadHT.put(sssn, mqttThread);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i(TAG, "Level memory: " + level);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "In onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private GetMqttThread createMQTTThread(final String sssn, final String topic) {
        return new GetMqttThread() {
            @Override
            public void createListener() {
                Log.i(TAG, "createListener");

                // TODO Auto-generated method stub
                final CountDownLatch connectLatch = new CountDownLatch(1);
                final AtomicReference<ConnectReturnCode> connectReturnCode = new AtomicReference<ConnectReturnCode>();
                mqttListener = new AsyncClientListener() {
                    @Override
                    public void publishReceived(MqttClient client, final PublishMessage message) {

                        final PublishMessage msg = message;
                        String mMsg = msg.getPayloadString().toString();
                        Log.i(TAG, "publishReceived: " + mMsg);


                        /*Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(FeedMapRealtimeFragment.mBroadcastStringAction);
                        broadcastIntent.putExtra("Data", mMsg);
                        sendBroadcast(broadcastIntent);
                        */
                        message.ack();
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
                        Log.i(TAG, "connected");
                        connectReturnCode.set(returnCode);
                        connectLatch.countDown();
                    }

                    @Override
                    public void published(MqttClient arg0, PublishMessage arg1) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "published");

                    }

                    @Override
                    public void subscribed(MqttClient arg0,
                                           Subscription[] arg1, Subscription[] arg2,
                                           boolean arg3) {
                        // TODO Auto-generated method stub
                        Log.i("ben", "subscribed");

                    }

                    @Override
                    public void unsubscribed(MqttClient arg0, String[] arg1) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "unsubscribed");

                    }
                };
            }//end createListener

            public void createClient() {
                // TODO Auto-generated method stub
                Log.i(TAG, "createClient");
                mqttClient = new AsyncMqttClient(mqttBrokerURL, mqttListener, mqttHandlerThreadPoolSize);
                try {
                    Log.i(TAG, "createClient in try");
                    mqttClient.connect(topic, true, mqttUser, mqttPwd);
                    List<Subscription> subscriptions = new ArrayList<Subscription>();
                    subscriptions.add(new Subscription(topic, QoS.AT_MOST_ONCE));
                    mqttClient.subscribe(subscriptions);

                } catch (Exception e) {
                    Log.i(TAG, "An exception prevented the publishing of the full catalog." + e);
                }
            }//end createClient

        };//end new SetMqttThread

    }//end createMQTTThread()


}
