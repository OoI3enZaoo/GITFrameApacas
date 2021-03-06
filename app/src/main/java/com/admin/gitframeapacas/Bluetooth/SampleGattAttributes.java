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

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String MANAFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";
    public static String NO2_NAME = "00002b29-0000-1000-8000-00805f9b34fb";
    public static String RAD_NAME = "00002c29-0000-1000-8000-00805f9b34fb";

    private static HashMap<String, String> attributes = new HashMap();

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Dust Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "CO Service");
        attributes.put("0000180b-0000-1000-8000-00805f9b34fb", "NO2 Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Dust Measurement");
        attributes.put(MANAFACTURER_NAME, "CO Measurement");
        attributes.put(NO2_NAME, "NO2 Measurement");
        attributes.put(RAD_NAME, "Radioactive Measurement");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
