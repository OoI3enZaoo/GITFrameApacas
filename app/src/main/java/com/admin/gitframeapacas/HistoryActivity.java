package com.admin.gitframeapacas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pl.pawelkleczkowski.customgauge.CustomGauge;


/**
 * Created by Admin on 13/11/2559.
 */

public class HistoryActivity extends AppCompatActivity {


    Toolbar toolbar;
    ImageButton fabButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("History");
        setSupportActionBar(toolbar);


        //recyclerview.setHasFixedSize(true);
        new HistoryTask().execute();

        //recyclerview.setItemAnimator(new DefaultItemAnimator());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("ben", "1");
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
                new HomeActivity().signOut();
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

    public class HistoryTask extends AsyncTask<String, Void, String> {

        RecyclerView recyclerview;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerview = (RecyclerView) findViewById(R.id.historyRecyclerView);
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            recyclerview.setAdapter(new RecyclrViewAdapter());
            recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

    public class RecyclrViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Log.d("ben", "2");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_histoy, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            //holder.location.setText("สนามหลวง");
            // holder.status.setText("อากาศเยี่ยมยอด");
            Log.d("ben", "3");
        }

        @Override
        public int getItemCount() {
            Log.d("ben", "4");
            return 10;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder//implements View.OnClickListener
    {
        TextView location;
        TextView status;
        ImageView image;
        CustomGauge gauge;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("ben", "5");
            location = (TextView) itemView.findViewById(R.id.location);
            status = (TextView) itemView.findViewById(R.id.status);
            gauge = (CustomGauge) itemView.findViewById(R.id.gauge);
        }


      /*  @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("click","Click: " + position);
            Toast.makeText(getApplicationContext(), "click: " + position, Toast.LENGTH_SHORT).show();
        }*/
    }


}
