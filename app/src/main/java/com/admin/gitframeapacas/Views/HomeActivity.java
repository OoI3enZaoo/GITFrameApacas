package com.admin.gitframeapacas.Views;

import android.app.ActivityManager;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.admin.gitframeapacas.Bluetooth.BluetoothLeService;
import com.admin.gitframeapacas.Bluetooth.SampleGattAttributes;
import com.admin.gitframeapacas.Fragment.FeedAboutAQIFragment;
import com.admin.gitframeapacas.Fragment.FeedFavoriteFragment;
import com.admin.gitframeapacas.Fragment.FeedHomeFragment;
import com.admin.gitframeapacas.Fragment.FeedMapNavigateFragment;
import com.admin.gitframeapacas.Fragment.FeedMapRealtimeFragment;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBCurrentLocation;
import com.admin.gitframeapacas.SQLite.DBFavorite;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.admin.gitframeapacas.Service.GetGasService;
import com.admin.gitframeapacas.Service.SetGasService;
import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import devlight.io.library.ntb.NavigationTabBar;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private static final int REQUEST_PERMISSIONS = 100;
    public static boolean MQTTRunning = false;
    public static String dust = null;
    public static String CO = null;
    public static String NO2 = null;
    static FloatingSearchView searchDistrict;
    static FloatingSearchView searchNavigate;
    //static TextView mText;
    private static DrawerLayout mDrawerLayout;
    private static String TAG = "BENHomeActivity";
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    Double latitude, longitude;
    Geocoder geocoder;
    boolean boolean_permission;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    ConstraintLayout view2;
    int statusCurrent = 1;
    int counter = 0;
    List<String> arrayDust = new ArrayList<String>();
    List<String> arrayCO = new ArrayList<String>();
    List<String> arrayNO2 = new ArrayList<String>();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Snackbar snackbar;


    /* private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {

             latitude = Double.valueOf(intent.getStringExtra("latutide"));
             longitude = Double.valueOf(intent.getStringExtra("longitude"));

             List<Address> addresses = null;

             try {
                 addresses = geocoder.getFromLocation(latitude, longitude, 1);
                 String cityName = addresses.get(0).getAddressLine(0);
                 String stateName = addresses.get(0).getAddressLine(1);
                 String countryName = addresses.get(0).getAddressLine(2);


                 //Log.i(TAG,"area: "+ addresses.get(0).getAdminArea());
                 Log.i(TAG, "locality: " + stateName);
                 Log.i(TAG, "address: " + countryName);

                 snackbar = Snackbar.make(view2, "Hello", Snackbar.LENGTH_LONG)
                         .setAction("ปิด", new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 snackbar.dismiss();
                             }
                         });
                 snackbar.show();


             } catch (IOException e1) {
                 e1.printStackTrace();
             }


           *//*  tv_latitude.setText(latitude+"");
            tv_longitude.setText(longitude+"");
            tv_address.getText();*//*


        }
    };*/
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    public final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private String hrValue;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //displayDataCO(intent.getStringExtra(BluetoothLeService.EXTRA_DATA1));
                //displayDataNO2(intent.getStringExtra(BluetoothLeService.EXTRA_DATA2));

                dust = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                CO = intent.getStringExtra(BluetoothLeService.EXTRA_DATA1);
                NO2 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA2);

                FeedHomeFragment homefragment = new FeedHomeFragment();

                if (dust != null && !dust.equals("0.00") && !dust.equals("")) {
                    dust = dust.toString().trim();
                    homefragment.setDust(Float.parseFloat(dust));


                }
                if (CO != null && !CO.equals("0.00") && !CO.equals("")) {
                    CO = CO.toString().trim();
                    homefragment.setCO(Float.parseFloat(CO));
                }
                if (NO2 != null && !NO2.equals("0.00") && !NO2.equals("")) {
                    NO2 = NO2.toString().trim();
                    homefragment.setNO2(Float.parseFloat(NO2));
                }
                Log.d(TAG, "test value dust =" + dust + "," + "CO =" + CO + "," + "NO2 =" + NO2);

                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA) != null) {
                    arrayDust.add(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    Log.d(TAG, "Dust List = " + String.valueOf(arrayDust));
                }
                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA1) != null) {
                    arrayCO.add(intent.getStringExtra(BluetoothLeService.EXTRA_DATA1));
                    Log.d(TAG, "CO List = " + String.valueOf(arrayCO));
                }
                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA2) != null) {
                    arrayNO2.add(intent.getStringExtra(BluetoothLeService.EXTRA_DATA2));
                    Log.d(TAG, "NO2 List = " + String.valueOf(arrayNO2));
                }

                Log.d(TAG, "Dust = " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Log.d(TAG, "CO = " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA1));
                Log.d(TAG, "NO2 = " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA2));




              /*  Bundle bundle = new Bundle();
                bundle.putString("CO", CO);
                bundle.putString("NO2", NO2);
                bundle.putString("DUST", dust);
                FeedHomeFragment fragobj = new FeedHomeFragment();
                fragobj.setArguments(bundle);*/

                /*barChart.addBar(new BarModel("CO", Integer.parseInt(CO), Color.parseColor("#91a7ff")));
                barChart.addBar(new BarModel("NO2", Integer.parseInt(NO2), Color.parseColor("#42bd41")));
                barChart.addBar(new BarModel("PM2.5", Integer.parseInt(dust), Color.parseColor("#f36c60")));*/
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "my Profile", Toast.LENGTH_SHORT).show();
            }
        });

        Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.searchfragment);
        searchDistrict = (FloatingSearchView) myFragment.getView().findViewById(R.id.search_district);
        searchDistrict.attachNavigationDrawerToMenuButton(mDrawerLayout);

        searchNavigate = (FloatingSearchView) myFragment.getView().findViewById(R.id.search_navigate);
        searchNavigate.attachNavigationDrawerToMenuButton(mDrawerLayout);


        view2 = (ConstraintLayout) findViewById(R.id.constaint_home);
        //gps = new GPSTracker(getApplicationContext());


        /*Fragment myFragment2 = getSupportFragmentManager().findFragmentById(R.id.chartfragment);
        mText = (TextView) myFragment2.getView().findViewById(R.id.txtAQI);*/

        startService(new Intent(getApplicationContext(), SetGasService.class));

        fn_permission();

        Intent intent = getIntent();
        if (intent.getStringExtra("checkblt") != null) {
            String checkbluetooth = intent.getStringExtra("checkblt");
            Log.i(TAG, "checkbluetooth: " + checkbluetooth);
            if (checkbluetooth.equals("1")) {
                DBUser dbUser = new DBUser(getApplicationContext());
                dbUser.updateHaveSensor(1);
                Intent intent2 = getIntent();
                mDeviceName = intent2.getStringExtra(EXTRAS_DEVICE_NAME);
                mDeviceAddress = intent2.getStringExtra(EXTRAS_DEVICE_ADDRESS);
                Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                Log.i(TAG, "devicename: " + mDeviceName + " device Address: " + mDeviceAddress);

            }

        }


    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

    private void initUI() {

        Log.i(TAG, "initUI");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);
        final String[] colors = getResources().getStringArray(R.array.default_preview);
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_home),
                        Color.parseColor(colors[0]))
                        //.selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("Home")
                        .badgeTitle("Home Sweet Home")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_favorite),
                        Color.parseColor(colors[1]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("Favorite")
                        .badgeTitle("My Favorite :D")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_navigate),
                        Color.parseColor(colors[2]))
                        // .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("Navigate")
                        .badgeTitle("go somewhere?")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_realtime),
                        Color.parseColor(colors[2]))
                        // .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("Real time")
                        .badgeTitle("Real time !!")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_about),
                        Color.parseColor(colors[3]))
                        // .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("About")
                        .badgeTitle("AQI")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
                switch (position) {
                    case 0:

                        if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }
                        if (isMyServiceRunning(GetGasService.class) == true) {
                            stopService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }
                        break;

                    case 1:
                        if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }
                        if (isMyServiceRunning(GetGasService.class) == true) {
                            stopService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }
                        break;

                    case 2:

                        if (searchDistrict.getVisibility() == View.VISIBLE) {
                            searchDistrict.setVisibility(View.GONE);
                            searchNavigate.setVisibility(View.VISIBLE);
                        }

                        if (isMyServiceRunning(GetGasService.class) == true) {
                            stopService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }
                        break;

                    case 3://map
                        if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }

                        if (isMyServiceRunning(GetGasService.class) == false) {
                            startService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }

                        break;
                    case 4:
                        if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }
                        if (isMyServiceRunning(GetGasService.class) == true) {
                            stopService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notification) {
            Toast.makeText(this, "NotificationActivity", Toast.LENGTH_SHORT).show();

        } /*else if (id == R.id.nav_reward) {
            Toast.makeText(this, "LocationActivity", Toast.LENGTH_SHORT).show();
        }*/ else if (id == R.id.nav_setting) {

            Toast.makeText(this, "SettingActivity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {

            Toast.makeText(this, "HelpActivity", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent2);
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            DBUser db = new DBUser(getApplicationContext());
            db.updateStatus(0);
            db.updateName("");
            db.updateCheckSensor(0);
            db.updateUserID((long) 0);
            db.updateHaveSensor(3);
            DBFavorite dbFavorite = new DBFavorite(getApplicationContext());
            dbFavorite.drop();
            DBCurrentLocation dbCur = new DBCurrentLocation(getApplicationContext());
            dbCur.drop();
            if (isMyServiceRunning(SetGasService.class) == true) {
                stopService(new Intent(getApplicationContext(), SetGasService.class));
            }
            if (isMyServiceRunning(GetGasService.class) == true) {
                stopService(new Intent(getApplicationContext(), GetGasService.class));
            }
            Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_help:
                Intent intent2 = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent2);
                return true;
            case R.id.action_logout:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void clearUI() {
        hrValue = "";
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxx bluetoothLE method xxxxxxxxxxxxxxxxxxxxxxxxx
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    private void displayData(String data) {//write textview for Dust
        if (data != null) {

        }
    }

    private void displayDataCO(String data) {// write textview for CO
        if (data != null) {

        }
    }

    private void displayDataNO2(String data) {// write textview for NO2
        if (data != null) {

        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);


        }
        Timer myTimer;
        myTimer = new Timer();

        myTimer.schedule(new TimerTask() {
            public void run() {

                if (mConnected == true) {//if bluttooth is enable
                    final BluetoothGattCharacteristic characteristic =
                            mGattCharacteristics.get(2).get(counter);
                    if (statusCurrent == 1) {
                        counter = 0;
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);

                        if (dust != null) {
                            Log.d(TAG, "loop1 !=null");
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                            statusCurrent++;
                        } else {
                            Log.d(TAG, "loop1 ==null");
                        }
                    }
                    if (statusCurrent == 2) {
                        counter = 1;
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);
                        if (CO != null) {
                            Log.d(TAG, "loop2 !=null");
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                            statusCurrent++;
                        } else {
                            Log.d(TAG, "loop2 ==null");
                        }
                    }
                    if (statusCurrent == 3) {
                        counter = 2;
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);

                    }
                } else if (mConnected == false) {// if bluetooth disable
                    try {
                        // thread to sleep for 1000 milliseconds
                        Thread.sleep(3000);
                        Log.d(TAG, "BLE is dead");

                        Random r = new Random();
                        dust = arrayDust.get(r.nextInt(arrayDust.size()));
                        CO = arrayCO.get(r.nextInt(arrayCO.size()));
                        NO2 = arrayNO2.get(r.nextInt(arrayNO2.size()));
                        Log.d(TAG, "Dust random = = " + dust);
                        Log.d(TAG, "CO random = = " + CO);
                        Log.d(TAG, "NO2 random = = " + NO2);
                    } catch (Exception e) {

                    }


                }


            }
        }, 0, 1000);


    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {


        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Log.i(TAG, "FeedHomeFragment");
                    return new FeedHomeFragment();
                case 1:
                    Log.i(TAG, "FeedFavoriteFragment");
                    return new FeedFavoriteFragment();
                case 2:
                    Log.i(TAG, "FeedFavoriteFragment");
                    return new FeedMapNavigateFragment();
                case 3:
                    Log.i(TAG, "FeedMapRealtimeFragment");
                    return new FeedMapRealtimeFragment();
                case 4:
                    Log.i(TAG, "FeedAboutAQIFragment");
                    return new FeedAboutAQIFragment();


            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

    }
}