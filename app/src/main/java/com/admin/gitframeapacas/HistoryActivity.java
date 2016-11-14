package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Admin on 13/11/2559.
 */

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hisroty");
        setSupportActionBar(toolbar);

        recyclerview = (RecyclerView) findViewById(R.id.historyRecyclerView);
        //recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(new RecyclrViewAdapter());
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //recyclerview.setItemAnimator(new DefaultItemAnimator());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d("ben","1");
    }

    public class RecyclrViewAdapter extends  RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Log.d("ben","2");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_histoy,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.location.setText("ตลาดไทย");
            holder.status.setText("อากาศเยี่ยมยอด");
            Log.d("ben","3");
        }
        @Override
        public int getItemCount() {
            Log.d("ben","4");
            return 20;
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView location;
        TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("ben","5");
            location = (TextView) itemView.findViewById(R.id.location);
            status = (TextView) itemView.findViewById(R.id.status);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("click","Click: " + position);
        }
    }
}
