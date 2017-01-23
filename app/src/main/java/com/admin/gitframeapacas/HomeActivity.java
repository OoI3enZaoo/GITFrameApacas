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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

//import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static int aaa;
    public static int a = 0;
    static FloatingSearchView searchDistrict;
    static FloatingSearchView searchNavigate;
    private static DrawerLayout mDrawerLayout;
    //private FirebaseAuth mAuth;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String type;
    private String UID = "";

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

            Toast.makeText(this, "LocationActivity", Toast.LENGTH_SHORT).show();
            //signOut();
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

                    return new FeedHomeFragment();
                case 1:

                    return new FeedFavoriteFragment();
                case 2:

                    return new FeedMapFragment();
                case 3:

                    return new FeedAboutAQIFragment();


            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
 /*       public boolean isViewFromObject(final View view, final Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(final View container, final int position, final Object object) {
            ((ViewPager) container).removeView((View) object);
        }*/


    }

/*    public class SkipTask extends AsyncTask<String, Void, String> {

        String message = "";
        String status = "";

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();


            RequestBody formBody1 = new FormBody.Builder()
                    .add("id", UID)
                    .build();
            Request request1 = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/SelectUser.jsp")
                    .post(formBody1)
                    .build();

            try {
                Response response = client.newCall(request1).execute();
                status = response.body().string();
                status = status.trim().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (status.equals("NonSuccess")) {

                RequestBody formBody = new FormBody.Builder()
                        .add("id", UID)
                        .add("district", type)
                        .build();
                Request request = new Request.Builder()
                        .url("http://sysnet.utcc.ac.th/aparcas/InsertUser.jsp")
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    message = response.body().string();
                    // Do something with the response.
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("ben", "status: " + status);
            Log.i("ben", "Message: " + message);
        }
    }*/
}
