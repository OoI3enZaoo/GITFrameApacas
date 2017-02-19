package com.admin.gitframeapacas.Views;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
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
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import com.admin.gitframeapacas.Fragment.FeedMapRealtimeFragment;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBCurrentLocation;
import com.admin.gitframeapacas.SQLite.DBFavorite;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.admin.gitframeapacas.Service.DataOnApp;
import com.admin.gitframeapacas.Service.GetGasService;
import com.admin.gitframeapacas.Service.SetGasService;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import devlight.io.library.ntb.NavigationTabBar;

import static com.admin.gitframeapacas.Fragment.FeedHomeFragment.aqi;
import static com.admin.gitframeapacas.Fragment.FeedHomeFragment.rad;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String mBroadcastStringAction = "com.truiton.broadcast.string";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final long SCAN_PERIOD = 1000;
    private static final int REQUEST_ENABLE_BT = 1;
    public static boolean MQTTRunning = false;
    public static String dust = null;
    public static String CO = null;
    public static String NO2 = null;
    public static String RAD = null;
    public static FloatingActionButtonPlus mActionButtonPlus;
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
    private List<String> arrayDust = new ArrayList<String>();
    private List<String> arrayCO = new ArrayList<String>();
    private List<String> arrayNO2 = new ArrayList<String>();
    private List<String> arrayRAD = new ArrayList<String>();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Snackbar snackbar;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private String mDeviceName = "TestFloatNumber";
    private String mDeviceAddress = "98:4F:EE:0D:1D:2E";
    private BluetoothLeService mBluetoothLeService;
    //private TextView txtName;
    //private IntentFilter mIntentFilter;
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
    private BroadcastReceiver broadcastReceiver;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Device: " + device.getName().toString() + " id: " + device.getAddress().toString());
                            if (device.getName().equals("TestFloatNumber")) {
                                connectBluetooth(device.getAddress().toString(), device.getName().toString());
                            }
                        }
                    });
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

                //displayData(intent.getStringExtra(BluetoothLeService.SENSOR_PM25));
                //displayDataCO(intent.getStringExtra(BluetoothLeService.SENSOR_CO));
                //displayDataNO2(intent.getStringExtra(BluetoothLeService.SENSOR_NO2));

                dust = intent.getStringExtra(BluetoothLeService.SENSOR_PM25);
                CO = intent.getStringExtra(BluetoothLeService.SENSOR_CO);
                NO2 = intent.getStringExtra(BluetoothLeService.SENSOR_NO2);
                RAD = intent.getStringExtra(BluetoothLeService.SENSOR_RAD);


                //homefragment.setLocation();
                if (dust != null && !dust.equals("0.00") && !dust.equals("")) {
                    dust = dust.toString().trim();
                    //  homefragment.setDust(Float.parseFloat(dust), true);

                }
                if (CO != null && !CO.equals("0.00") && !CO.equals("")) {
                    CO = CO.toString().trim();
                    //  homefragment.setCO(Float.parseFloat(CO), true);

                }
                if (NO2 != null && !NO2.equals("0.00") && !NO2.equals("")) {
                    NO2 = NO2.toString().trim();
//                    homefragment.setNO2(Float.parseFloat(NO2), true);
                }
                Log.d(TAG, "test value dust =" + dust + "," + "CO =" + CO + "," + "NO2 =" + NO2);

                if (intent.getStringExtra(BluetoothLeService.SENSOR_PM25) != null) {
                    arrayDust.add(intent.getStringExtra(BluetoothLeService.SENSOR_PM25));
                    Log.d(TAG, "Dust List = " + String.valueOf(arrayDust));
                }
                if (intent.getStringExtra(BluetoothLeService.SENSOR_CO) != null) {
                    arrayCO.add(intent.getStringExtra(BluetoothLeService.SENSOR_CO));
                    Log.d(TAG, "CO List = " + String.valueOf(arrayCO));
                }
                if (intent.getStringExtra(BluetoothLeService.SENSOR_NO2) != null) {
                    arrayNO2.add(intent.getStringExtra(BluetoothLeService.SENSOR_NO2));
                    Log.d(TAG, "NO2 List = " + String.valueOf(arrayNO2));
                }
                if (intent.getStringExtra(BluetoothLeService.SENSOR_RAD) != null) {
                    arrayRAD.add(intent.getStringExtra(BluetoothLeService.SENSOR_RAD));
                    Log.d(TAG, "RAD List = " + String.valueOf(arrayRAD));
                }
                Log.d(TAG, "Dust = " + intent.getStringExtra(BluetoothLeService.SENSOR_PM25));
                Log.d(TAG, "CO = " + intent.getStringExtra(BluetoothLeService.SENSOR_CO));
                Log.d(TAG, "NO2 = " + intent.getStringExtra(BluetoothLeService.SENSOR_NO2));
                Log.d(TAG, "RAD = " + intent.getStringExtra(BluetoothLeService.SENSOR_RAD));




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
        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "my Profile", Toast.LENGTH_SHORT).show();
            }
        });*/

        Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.searchfragment);
        searchDistrict = (FloatingSearchView) myFragment.getView().findViewById(R.id.search_district);
        //searchDistrict.attachNavigationDrawerToMenuButton(mDrawerLayout);

        searchNavigate = (FloatingSearchView) myFragment.getView().findViewById(R.id.search_navigate);
        //searchNavigate.attachNavigationDrawerToMenuButton(mDrawerLayout);


        view2 = (ConstraintLayout) findViewById(R.id.constaint_home);

        //txtName = (TextView)findViewById(R.id.txtName);
        /*Fragment myFragment2 = getSupportFragmentManager().findFragmentById(R.id.chartfragment);
        mText = (TextView) myFragment2.getView().findViewById(R.id.txtAQI);*/

        //startService(new Intent(getApplicationContext(), SetGasService.class));
        startService(new Intent(getApplicationContext(), DataOnApp.class));

        //fn_permission();

        mHandler = new Handler();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


        mActionButtonPlus = (FloatingActionButtonPlus) findViewById(R.id.ActionButtonPlus);
        mActionButtonPlus.setPosition(FloatingActionButtonPlus.POS_RIGHT_TOP);


        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {

            @Override
            public void onItemClick(FabTagLayout tagView, int position) {

                switch (position) {
                    case 0:
                        Intent intent2 = new Intent(getApplicationContext(), RecommendActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("aqi", aqi);
                        bundle.putString("rad", rad);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        break;
                }
            }
        });


    }

    private void connectBluetooth(String address, String name) {


        Log.i(TAG, "Device: equals: " + address + " name: " + name);
        DBUser dbUser = new DBUser(getApplicationContext());

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(address);
            Log.d(TAG, "Connect request result=" + result);
        }

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.i(TAG, "Blutooth adapterr: " + mBluetoothAdapter);

        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }

    }



/*



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
*/


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
                        .title("หน้าหลัก")
                        .badgeTitle("เช็คสภาพอากาศ")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_favorite),
                        Color.parseColor(colors[1]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("รายการโปรด")
                        .badgeTitle("มาเพิ่มกันเลย")
                        .build()
        );
    /*    models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_navigate),
                        Color.parseColor(colors[2]))
                        // .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("Navigate")
                        .badgeTitle("go somewhere?")
                        .build()
        );*/
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_realtime),
                        Color.parseColor(colors[2]))
                        // .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("ระบบเรียลไทม์")
                        .badgeTitle("สภาพอากาศแบบเรียลไทม์")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_about),
                        Color.parseColor(colors[3]))
                        // .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("เกี่ยวกับ")
                        .badgeTitle("ดัชนีต่างๆ")
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

                        if (mActionButtonPlus.getVisibility() == View.GONE) {
                            mActionButtonPlus.setVisibility(View.VISIBLE);
                        }

                       /* if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }
                        if (isMyServiceRunning(GetGasService.class) == true) {
                            stopService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }*/
                        break;

                    case 1:
                        if (mActionButtonPlus.getVisibility() == View.VISIBLE) {
                            mActionButtonPlus.setVisibility(View.GONE);
                        }
                        /*if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }
                        if (isMyServiceRunning(GetGasService.class) == true) {
                            stopService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }*/
                        break;

                    case 2:
                        if (mActionButtonPlus.getVisibility() == View.VISIBLE) {
                            mActionButtonPlus.setVisibility(View.GONE);
                        }
/*
                        if (searchDistrict.getVisibility() == View.VISIBLE) {
                            searchDistrict.setVisibility(View.GONE);
                            searchNavigate.setVisibility(View.VISIBLE);
                        }

                        if (isMyServiceRunning(GetGasService.class) == true) {
                            stopService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }*/
                        break;

                    case 3://map
                        if (mActionButtonPlus.getVisibility() == View.VISIBLE) {
                            mActionButtonPlus.setVisibility(View.GONE);
                        }
                       /* if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }

                        if (isMyServiceRunning(GetGasService.class) == false) {
                            startService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }*/

                        break;
                    case 4:
                        if (mActionButtonPlus.getVisibility() == View.VISIBLE) {
                            mActionButtonPlus.setVisibility(View.GONE);
                        }
                       /* if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }
                        if (isMyServiceRunning(GetGasService.class) == true) {
                            stopService(new Intent(getApplicationContext(), GetGasService.class));
                            Log.i(TAG, "is service running: " + isMyServiceRunning(GetGasService.class));
                        }*/
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

      /*  if (id == R.id.nav_notification) {
            Toast.makeText(this, "NotificationActivity", Toast.LENGTH_SHORT).show();



        } *//*else if (id == R.id.nav_reward) {
            Toast.makeText(this, "LocationActivity", Toast.LENGTH_SHORT).show();
        }  if (id == R.id.nav_setting) {

            Toast.makeText(this, "SettingActivity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        }*/
        if (id == R.id.nav_help) {

            Toast.makeText(this, "HelpActivity", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent2);
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            if (isMyServiceRunning(SetGasService.class) == true) {
                stopService(new Intent(getApplicationContext(), SetGasService.class));
            }
            if (isMyServiceRunning(GetGasService.class) == true) {
                stopService(new Intent(getApplicationContext(), GetGasService.class));
            }
            if (isMyServiceRunning(DataOnApp.class) == true) {
                stopService(new Intent(getApplicationContext(), DataOnApp.class));
            }

            DBUser db = new DBUser(getApplicationContext());
            db.updateStatus(0);
            db.updateName("");
            db.updateCheckSensor(0);
            db.updateUserID("");
            db.updateHaveSensor(3);
            DBFavorite dbFavorite = new DBFavorite(getApplicationContext());
            dbFavorite.drop();
            DBCurrentLocation dbCur = new DBCurrentLocation(getApplicationContext());
            dbCur.drop();
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
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG, "onReceive: " + "\n" + intent.getExtras().get("coordinates"));

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

        if (!mBluetoothAdapter.isEnabled()) {
            DBUser dbUser = new DBUser(getApplicationContext());
            // if (dbUser.getHaveSensor() == 1) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //  }
        }
        scanLeDevice(true);

    }


    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
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
                        if (NO2 != null) {
                            Log.d(TAG, "loop3 !=null");
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                            statusCurrent++;
                        } else {
                            Log.d(TAG, "loop3 ==null");
                        }
                    }
                    if (statusCurrent == 4) {
                        counter = 3;
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);

                    }
                } else if (mConnected == false) {// if bluetooth disable
                    try {
                        // thread to sleep for 1000 milliseconds

                        Random r = new Random();
                        dust = arrayDust.get(r.nextInt(arrayDust.size()));
                        CO = arrayCO.get(r.nextInt(arrayCO.size()));
                        NO2 = arrayNO2.get(r.nextInt(arrayNO2.size()));
                        RAD = arrayRAD.get(r.nextInt(arrayRAD.size()));

                        Log.d(TAG, "BLE is dead");


                        Log.d(TAG, "Dust random = = " + dust);
                        Log.d(TAG, "CO random = = " + CO);
                        Log.d(TAG, "NO2 random = = " + NO2);
                        Log.d(TAG, "RAD random = = " + RAD);
                    } catch (Exception e) {

                    }


                }


            }
        }, 0, 1000);

    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                    Log.i(TAG, "FeedMapRealtimeFragment");
                    return new FeedMapRealtimeFragment();
                case 3:
                    Log.i(TAG, "FeedAboutAQIFragment");
                    return new FeedAboutAQIFragment();


            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }


}