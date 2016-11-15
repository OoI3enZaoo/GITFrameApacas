package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import pl.pawelkleczkowski.customgauge.CustomGauge;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Admin on 12/11/2559.
 */

public class FeedHomeFragment extends Fragment {


    public FeedHomeFragment(){



    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerview = (RecyclerView) v.findViewById(R.id.recyclerView_recom);
        //recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(new RecyclrViewAdapter());
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        TextView txt1= (TextView) v.findViewById(R.id.txt1);
        TextView txt2 = (TextView) v.findViewById(R.id.txt2);
        TextView txt3 = (TextView) v.findViewById(R.id.txt3);
        CustomGauge gauge = (CustomGauge)v.findViewById(R.id.gauge);

        return v;
    }

    public class RecyclrViewAdapter extends  RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Log.d("ben","2");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //   holder.location.setText("ตลาดไทย");
            //holder.status.setText("อากาศเยี่ยมยอด");
        }

        @Override
        public int getItemCount() {
            Log.d("ben","4");
            return 4;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtRecom;
        ImageView imgRecom;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("ben","5");
            txtRecom = (TextView) itemView.findViewById(R.id.txt_recom);
            imgRecom = (ImageView) itemView.findViewById(R.id.img_recom);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("click","Click: " + position);
            Toast.makeText(getApplicationContext(), "click: " + position, Toast.LENGTH_SHORT).show();
        }
    }
}
