package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pl.pawelkleczkowski.customgauge.CustomGauge;


/**
 * Created by Admin on 12/11/2559.
 */

public class FeedHomeFragment extends Fragment {

    private static final String TAG = "AnonymousAuth";
    //-- ตัวแปรสำหรับเกจวัดก๊าซต่างๆ
    CustomGauge gaguePM25;
    CustomGauge gagueCO;
    CustomGauge gagueHO2;
    CustomGauge gagueSO2;
    //--ปุ่มสำหรับ Random ค่าก๊าซ
    Button btnRandom;
    /*private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid;*/
    /*DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mAQI = mRootRef.child("aqi");
    DatabaseReference mPM25 = mRootRef.child("pm25");
    DatabaseReference mHO2 = mRootRef.child("ho2");
    DatabaseReference mCO = mRootRef.child("co");
    DatabaseReference mSO2 = mRootRef.child("so2");*/
    private CustomGauge gaugeAQI;
    private TextView txtAQI;
    private TextView txtCO;
    private TextView txtPM25;
    private TextView txtSO2;
    private TextView txtH2O;



    public FeedHomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerview = (RecyclerView) v.findViewById(R.id.recyclerView_recom);
        TextView text = (TextView) v.findViewById(R.id.txtLocation);
        text.setFocusable(true);
        text.setFocusableInTouchMode(true);
        text.requestFocus();
        //recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new DividerItemDecoration(getActivity()));
        recyclerview.setAdapter(new RecyclrViewAdapter());
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));


        txtAQI = (TextView) v.findViewById(R.id.txtAQI);
        txtCO = (TextView) v.findViewById(R.id.txtCO);
        txtPM25 = (TextView) v.findViewById(R.id.txtPM25);
        txtSO2 = (TextView) v.findViewById(R.id.txtSO2);
        txtH2O = (TextView) v.findViewById(R.id.txtH2O);

        gaugeAQI = (CustomGauge) v.findViewById(R.id.gaugeMaster);
        gaguePM25 = (CustomGauge) v.findViewById(R.id.gPM25);
        gagueCO = (CustomGauge) v.findViewById(R.id.gCO);
        gagueHO2 = (CustomGauge) v.findViewById(R.id.gHO2);
        gagueSO2 = (CustomGauge) v.findViewById(R.id.gSO2);

     /*   btnRandom =(Button)v.findViewById(R.id.btnRandom);
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                int[] index = new int[4];
                for (int i = 0; i < 4; i++) {
                    int mrandom = rand.nextInt(100) + 1;
                    index[i] = mrandom;
                }
                mSO2.setValue(index[0]);
                mPM25.setValue(index[1]);
                mHO2.setValue(index[2]);
                mCO.setValue(index[3]);
            }
        });*/
        return v;
    }




   /* @Override
    public void onStart() {
        super.onStart();
        mPM25.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                calPM25 = dataSnapshot.getValue(Integer.class);
                mPM25.setValue( calPM25);
                gaguePM25.setValue(calPM25);
                txtPM25.setText("PM2.5: " + calPM25);
                onAQIChange();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mHO2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                calHO2 = dataSnapshot.getValue(Integer.class);
                mHO2.setValue( calHO2);
                gagueHO2.setValue(calHO2);
                txtH2O.setText("HO2: " + calHO2);
                onAQIChange();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mCO.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                calCO = dataSnapshot.getValue(Integer.class);

                mCO.setValue( calCO);
                gagueCO.setValue(calCO);
                txtCO.setText("CO: " + calCO);
                onAQIChange();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSO2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                calSO2 = dataSnapshot.getValue(Integer.class);
                mSO2.setValue( calSO2);
                gagueSO2.setValue(calSO2);
                txtSO2.setText("SO2: " + calSO2);
                onAQIChange();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void onAQIChange(){

        int result = (calPM25 + calCO + calHO2 + calSO2) / 4;
        mAQI.setValue( result);
        gaugeAQI.setValue( result);
        txtAQI.setText("AQI: " + result);
    }

*/

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

            return 5;
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
            Toast.makeText(getContext(), "click: " + position, Toast.LENGTH_SHORT).show();
        }
    }







}
