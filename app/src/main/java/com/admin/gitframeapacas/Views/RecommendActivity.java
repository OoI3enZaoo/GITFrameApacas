package com.admin.gitframeapacas.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.DividerItemDecoration;
import com.admin.gitframeapacas.R;

/**
 * Created by Admin on 19/11/2559.
 */

public class RecommendActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("การปฏิบัติตัว");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerView_recom);
//recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerview.setAdapter(new RecyclrViewAdapter());
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    public class RecyclrViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //   holder.location.setText("ตลาดไทย");
            //holder.status.setText("อากาศเยี่ยมยอด");
        }

        @Override
        public int getItemCount() {

            return 8;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtRecom;
        ImageView imgRecom;


        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("ben", "5");
            txtRecom = (TextView) itemView.findViewById(R.id.txt_recom);
            imgRecom = (ImageView) itemView.findViewById(R.id.img_recom);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("click", "Click: " + position);
            Toast.makeText(getApplicationContext(), "click: " + position, Toast.LENGTH_SHORT).show();
        }
    }


}
