package com.admin.gitframeapacas.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.Data.RealTimeDataResponse;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.Service.GetGasService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

/**
 * Created by Admin on 12/11/2559.
 */

public class FeedMapRealtimeFragment extends Fragment {


    public static final String mBroadcastStringAction = "com.truiton.broadcast.string";
    public static final String mBroadcastIntegerAction = "com.truiton.broadcast.integer";
    public static final String mBroadcastArrayListAction = "com.truiton.broadcast.arraylist";
    MapView mMapView;
    private GoogleMap googleMap;
    private String TAG = "BENFeedMapFragment";
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*mTextView.setText(mTextView.getText()
                    + "Broadcast From Service: \n");*/
            if (intent.getAction().equals(mBroadcastStringAction)) {
                final String mData = intent.getStringExtra("Data");
                Log.i(TAG, mData + "\n\n");
                final RealTimeDataResponse data = new RealTimeDataResponse();


                //  Log.i(TAG,"lon: " + lon);
                try {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(data.getLat(mData), data.getLon(mData)))
                            .title("การแจ้งเตือนคุณภาพอากาศ")
                            //.snippet("Marker \n ete")
                            .snippet("วันที่: " + data.getTstamp(mData) + "\n" +
                                    "ID: " + data.getID(mData) + "\n" +
                                    "CO: " + data.getCO(mData) + "\n" +
                                    "NO2: " + data.getNO2(mData) + "\n" +
                                    "O3: " + data.getO3(mData) + "\n" +
                                    "SO2: " + data.getSO2(mData) + "\n" +
                                    "PM2.5: " + data.getPM25(mData) + "\n" +
                                    "RadioActive: " + data.getRad(mData))

                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))


                    );

                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            LinearLayout info = new LinearLayout(getActivity());
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(getActivity());
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(getActivity());
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });


                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            try {
                                Toast.makeText(getActivity(), "CO: " + data.getCO(mData), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent stopIntent = new Intent(getActivity(), GetGasService.class);
                getActivity().stopService(stopIntent);

            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_realtime, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                /*// For showing a move to my location button
                googleMap.setMyLocationEnabled(true);*/

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(13.756331, 100.501765);
              /*  googleMap.addMarker(new MarkerOptions().position(new LatLng(13.756331,100.501765))
                        .title("Marker Title")
                        .snippet("Marker Description")
                );*/


                //Log.i(TAG,"lat: " + data.getLat() + "\nlon: " + data.getLon());

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        mIntentFilter.addAction(mBroadcastIntegerAction);
        mIntentFilter.addAction(mBroadcastArrayListAction);
        return v;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();

    }


}
