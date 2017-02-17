/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.admin.gitframeapacas.Bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.admin.gitframeapacas.SQLite.DBUser;
import com.admin.gitframeapacas.Service.RandomGas;
import com.admin.gitframeapacas.Service.SetMqttThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.admin.gitframeapacas.Bluetooth.SampleGattAttributes.MANAFACTURER_NAME;
import static com.admin.gitframeapacas.Bluetooth.SampleGattAttributes.NO2_NAME;


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String SENSOR_PM25 = "pm25";
    public final static String SENSOR_CO = "co";
    public final static String SENSOR_NO2 = "no2";
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID UUID_MANAFACTURER_NAME =
            UUID.fromString(MANAFACTURER_NAME);
    public final static UUID UUID_NO2 =
            UUID.fromString(NO2_NAME);
    private final static String TAG = "BENBluetoothLeService";
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public static boolean MQTTRunning = true;
    private static float CO = 0.0f;
    private static float NO2 = 0.0f;
    private static float PM25 = 0.0f;
    private static boolean CO_Status = false;
    private static boolean NO2_Status = false;
    private static boolean PM25_Status = false;
    private final IBinder mBinder = new LocalBinder();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BlockingQueue<JSONObject> messageQueue = new LinkedBlockingQueue<JSONObject>();
    private SetMqttThread setMqttThread = null;
    private String mqttBrokerURL = "tcp://sysnet.utcc.ac.th:1883";
    private String mqttUser = "admin";
    private String mqttPwd = "admin";
    private String sssn = "aparcas_raw";


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private int mConnectionState = STATE_DISCONNECTED;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override


        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        /*if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(SENSOR_PM25, String.valueOf(heartRate));
        }*/
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            /*int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }*/
            final byte[] bytes = characteristic.getValue();
            String number = "";
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 46) {
                    number = number + ".";
                } else {
                    int num = bytes[i] - 48;
                    number = number + Integer.toString(num);
                }

            }
            String test = "";
            Log.d(TAG, "Received length data: " + bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 46) {
                    Log.d(TAG, "Received heart rate " + i + " = " + ".");
                    test = test + ".";
                } else {
                    Log.d(TAG, "Received heart rate " + i + " = " + Integer.toString(bytes[i] - 48));
                    test = test + Integer.toString(bytes[i] - 48);
                }

            }

            Log.d(TAG, "Received heart rate test: " + test);

            intent.putExtra(SENSOR_PM25, number);

            setPM25(number);
        } else if (UUID_MANAFACTURER_NAME.equals(characteristic.getUuid())) {
            final byte[] bytes = characteristic.getValue();
            String mCo = "";
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 46) {
                    mCo = mCo + ".";
                } else {
                    int num = bytes[i] - 48;
                    mCo = mCo + Integer.toString(num);
                }

            }
            String test = "";
            Log.d(TAG, "Received length data: " + bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 46) {
                    Log.d(TAG, "Received CO " + i + " = " + ".");
                    test = test + ".";
                } else {
                    Log.d(TAG, "Received CO " + i + " = " + Integer.toString(bytes[i] - 48));
                    test = test + Integer.toString(bytes[i] - 48);
                }

            }

            Log.d(TAG, "Received CO test: " + test);
            setCO(test);
            intent.putExtra(SENSOR_CO, mCo);


        } else if (UUID_NO2.equals(characteristic.getUuid())) {
            final byte[] bytes = characteristic.getValue();
            String mNo2 = "";
            for (int i = 0; i < bytes.length; i++) {

                if (bytes[i] == 46) {
                    mNo2 = mNo2 + ".";
                } else {
                    int num = bytes[i] - 48;
                    mNo2 = mNo2 + Integer.toString(num);
                }

            }
            String test = "";
            Log.d(TAG, "Received length data: " + bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 46) {
                    Log.d(TAG, "Received NO2 " + i + " = " + ".");
                    test = test + ".";
                } else {
                    Log.d(TAG, "Received NO2 " + i + " = " + Integer.toString(bytes[i] - 48));
                    test = test + Integer.toString(bytes[i] - 48);
                }

            }

            Log.d(TAG, "Received NO2 test: " + test);
            setNo2(test);
            intent.putExtra(SENSOR_NO2, mNo2);

        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));


                String mPm25 = new String(data) + "\n" + stringBuilder.toString();


                intent.putExtra(SENSOR_PM25, mPm25);


            }
        }

        sendBroadcast(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        Log.i(TAG, "mBluetoothAdapter: " + mBluetoothAdapter + " address: " + address);
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        if (UUID_MANAFACTURER_NAME.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        if (UUID_NO2.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public void onDestroy() {
        Toast.makeText(getApplication(), "Stop Service!", Toast.LENGTH_LONG).show();
    }

    private void setPM25(String mPm25) {
        Log.i(TAG, "setPm25: " + mPm25);
        PM25 = Float.parseFloat(mPm25.toString().trim());
        PM25_Status = true;
        combineData();
    }

    private void setCO(String mCo) {
        Log.i(TAG, "setCO: " + mCo);
        CO = Float.parseFloat(mCo.toString().trim());
        CO_Status = true;
        combineData();
    }

    private void setNo2(String mNo2) {
        Log.i(TAG, "setNO2: " + mNo2);
        NO2 = Float.parseFloat(mNo2.toString().trim());
        NO2_Status = true;
        combineData();
    }

    private void combineData() {

        Log.i(TAG, "PM25_STATUS: " + PM25_Status + " CO_STATUS: " + CO_Status + " NO2_STATUS: " + NO2_Status);
        if (PM25_Status == true && CO_Status == true && NO2_Status == true) {
            DBUser dbUser = new DBUser(getApplicationContext());
            long uid = dbUser.getUserID();
            float lat = 0.0f;
            float lon = 0.0f;
            MQTTSender(uid, lat, lon, CO, NO2, PM25);

            PM25_Status = false;
            CO_Status = false;
            NO2_Status = false;
        }
    }

    public void MQTTSender(long Luid, float mlat, float mlon, float mCO, float SNO2, float Spm25) {

        JSONObject obj = new JSONObject();
        try {

            float fo3 = new RandomGas().o3();
            float fso2 = new RandomGas().so2();
            float frad = new RandomGas().rad();
            float mmlat = new RandomGas().lat(1);
            float mmlon = new RandomGas().lon(1);

            String uid = String.valueOf(Luid);
            String o3 = String.valueOf(fo3);
            String so2 = String.valueOf(fso2);
            String rad = String.valueOf(frad);
            String pm25 = String.valueOf(Spm25);
            String NO2 = String.valueOf(SNO2);
            String lat = String.valueOf(mmlat);
            String lon = String.valueOf(mmlon);
            String CO = String.valueOf(mCO);
            int tstamp = new RandomGas().tstamp();
            obj.put("uid", uid);
            obj.put("lat", lat);
            obj.put("lon", lon);
            obj.put("co", CO);
            obj.put("no2", NO2);
            obj.put("o3", o3);
            obj.put("so2", so2);
            obj.put("pm25", pm25);
            obj.put("rad", rad);
            obj.put("ts", tstamp);
            Log.i(TAG, "MQTTSender: uid: " + uid + " lat: " + lat + " lon: " + lon + " co: " + CO + " no2: " + NO2 + " o3: " + o3 + " so2: " + so2 + " pm25: " + pm25 + " rad: " + rad + " ts: " + tstamp);
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

    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
