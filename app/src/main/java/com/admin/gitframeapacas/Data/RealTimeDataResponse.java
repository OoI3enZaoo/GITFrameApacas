package com.admin.gitframeapacas.Data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 2/2/2560.
 */

public class RealTimeDataResponse {

    public RealTimeDataResponse() {
    }

    public String getID(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return mainObject.getString("id");
    }

    public double getLat(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return Double.parseDouble(mainObject.getString("lat"));
    }

    public double getLon(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return Double.parseDouble(mainObject.getString("lon"));
    }

    public String getCO(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return mainObject.getString("co");
    }

    public String getNO2(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return mainObject.getString("no2");
    }

    public String getO3(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return mainObject.getString("o3");
    }

    public String getSO2(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return mainObject.getString("so2");
    }

    public String getPM25(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return mainObject.getString("pm25");
    }

    public String getRad(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        double a = Double.parseDouble(mainObject.getString("rad"));
        return String.format("%.2f uSv/h", a);
    }

    public String getTstamp(String text) throws JSONException {
        JSONObject mainObject = new JSONObject(text);
        return mainObject.getString("tstamp");
    }


}



