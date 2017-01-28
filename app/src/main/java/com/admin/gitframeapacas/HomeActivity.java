package com.admin.gitframeapacas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import com.arlib.floatingsearchview.FloatingSearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import devlight.io.library.ntb.NavigationTabBar;

//import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static int aaa;
    public static int a = 0;
    public static boolean MQTTRunning = true;
    static FloatingSearchView searchDistrict;
    static FloatingSearchView searchNavigate;
    private static DrawerLayout mDrawerLayout;
    String mID;
    //private FirebaseAuth mAuth;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private BlockingQueue<JSONObject> messageQueue = new LinkedBlockingQueue<JSONObject>();
    private MqttThread mqttThread = null;
    private String mqttBrokerURL = "tcp://sysnet.utcc.ac.th:1883";
    private String mqttUser = "admin";
    private String mqttPwd = "admin";
    private String sssn = "aparcas_raw";

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

        Intent intent = getIntent();
        mID = intent.getStringExtra("ID").toString();

        //mAuth = FirebaseAuth.getInstance();

       /* Intent intent = getIntent();
        type = intent.getStringExtra("TYPE").toString();
        UID = intent.getStringExtra("ID").toString();

        if (type.equals("User")) {
            //new SkipTask().execute();
        }
        if (type.equals("Member")) {
            Toast.makeText(getApplicationContext(), "Welcome Member", Toast.LENGTH_SHORT).show();

        }*/

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (MQTTRunning) {
                    MQTTSender();
                }

            }
        }, 0, 3000);
    }

    /*public static void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
            onAttachSearchViewToDrawer(searchDistrict);

    }*/


    private void initUI() {

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
                        break;

                    case 1:
                        if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 2:
                        if (searchDistrict.getVisibility() == View.VISIBLE) {
                            searchDistrict.setVisibility(View.GONE);
                            searchNavigate.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 3:
                        if (searchNavigate.getVisibility() == View.VISIBLE) {
                            searchNavigate.setVisibility(View.GONE);
                        }
                        if (searchDistrict.getVisibility() == View.GONE) {
                            searchDistrict.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent);
            Toast.makeText(this, "HistoryActivity", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_notification) {
            Toast.makeText(this, "NotificationActivity", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_reward) {
            Toast.makeText(this, "LocationActivity", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_setting) {

            Toast.makeText(this, "SettingActivity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {

            Toast.makeText(this, "HelpActivity", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent2);
        } else if (id == R.id.nav_logout) {

            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            DBHelper db = new DBHelper(getApplicationContext());
            db.deleteContact(1);
            Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
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

    /* public void signOut() {
          AlertDialog.Builder alert = new AlertDialog.Builder(this);
          alert.setMessage(R.string.logout);
          alert.setCancelable(false);
          alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  mAuth.signOut();
                  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                  startActivity(intent);
              }
          });
          alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.dismiss();
              }
          });
          alert.show();
      }
  */

    public void MQTTSender() {
        JSONObject obj = new JSONObject();
        try {
            Random rand = new Random();
            int option = rand.nextInt(4) + 1;
            obj.put("id", new RandomGas().id());
            obj.put("lat", new RandomGas().lat(option));
            obj.put("lon", new RandomGas().lon(option));
            obj.put("co", new RandomGas().co());
            obj.put("no2", new RandomGas().no2());
            obj.put("o3", new RandomGas().o3());
            obj.put("so2", new RandomGas().so2());
            obj.put("pm25", new RandomGas().pm25());
            obj.put("rad", new RandomGas().rad());
            obj.put("tstamp", new RandomGas().tstamp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            messageQueue.put(obj);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mqttThread = new MqttThread(sssn, messageQueue, mqttBrokerURL, mqttUser, mqttPwd);
        mqttThread.start();

    }// end of Random

    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Log.i("ben", "Id: " + mID.toString());
                    return new FeedHomeFragment();

                case 1:
                    Log.i("ben", "Id: " + mID.toString());
                    return new FeedFavoriteFragment();
                case 2:
                    Log.i("ben", "Id: " + mID.toString());
                    return new FeedMapFragment();
                case 3:
                    Log.i("ben", "Id: " + mID.toString());
                    return new FeedAboutAQIFragment();


            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

    }

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

        int o3() {
            return rand.nextInt(20) + 1;

        }

        int so2() {
            return rand.nextInt(18) + 5;

        }

        int pm25() {
            return rand.nextInt(350) + 50;
        }

        float rad() {
            float radmin = 0.020f;
            float radmax = 0.200f;
            return rand.nextFloat() * (radmax - radmin) + radmin;
        }

        String tstamp() {
            Date dNow = new Date();
            SimpleDateFormat ft =
                    new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
            return ft.format(dNow);
        }

    }


}
