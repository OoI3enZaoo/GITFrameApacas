package com.admin.gitframeapacas.Service;

import android.util.Log;

import java.util.Date;
import java.util.Random;

/**
 * Created by Admin on 16/2/2560.
 */

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

    public int o3() {
        return (rand.nextInt(20) + 1) * 1000;

    }

    public int so2() {
        return (rand.nextInt(18) + 5) * 1000;

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


