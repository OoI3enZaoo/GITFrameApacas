package com.admin.gitframeapacas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import pl.pawelkleczkowski.customgauge.CustomGauge;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Admin on 12/11/2559.
 */

public class FeedHomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "AnonymousAuth";
    private String uid;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mAQI = mRootRef.child("aqi");
    DatabaseReference mPM25 = mRootRef.child("pm25");
    DatabaseReference mH2O = mRootRef.child("h2o");
    DatabaseReference mCO = mRootRef.child("co");
    DatabaseReference mSO2 = mRootRef.child("so2");
    private CustomGauge gaugeAQI;
    private TextView txtAQI;
    private TextView txtCO;
    private TextView txtPM25;
    private TextView txtSO2;
    private TextView txtH2O;

    //-- ตัวแปรสำหรับเกจวัดก๊าซต่างๆ
    CustomGauge gaguePM25;
    CustomGauge gagueCO;
    CustomGauge gagueHO2;
    CustomGauge gagueSO2;

    //--ปุ่มสำหรับ Random ค่าก๊าซ
    Button btnRandom;

    //--ตัวแปร Random ก๊าซ
    private int rPM25;
    private int rCO;
    private int rHO2;
    private int rSO2;

    //--นำไปรับค่าของแต่ละก๊าซ สำหรับมาคำนวณหาค่า AQI
    int calPM25;
    int calCO;
    int calHO2;
    int calSO2;

    public FeedHomeFragment(String name) {
        this.uid = name;

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
        recyclerview.setAdapter(new RecyclrViewAdapter());
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "ID: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
        TextView testID = (TextView) v.findViewById(R.id.txtID);
        testID.setText("UserId: " + uid);

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
        btnRandom = (Button) v.findViewById(R.id.btnRandom);
        //gaguePM25.setValue(150);
        Random rand = new Random();
        rPM25 = rand.nextInt(100) + 1;
        rHO2 = rand.nextInt(100) + 1;
        rCO = rand.nextInt(100) + 1;
        rSO2 = rand.nextInt(100) + 1;
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();


        mPM25.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


               /* mPM25.setValue((int) rPM25);
                gaguePM25.setValue(rPM25);
                txtPM25.setText("PM2.5: " + rPM25);
                calPM25 = dataSnapshot.getValue(Integer.class);*/

                calPM25 = dataSnapshot.getValue(Integer.class);
                mPM25.setValue((int) calPM25);
                gaguePM25.setValue(calPM25);
                txtPM25.setText("PM2.5: " + calPM25);
                onAQIChange();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mH2O.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


             /*   mH2O.setValue((int) rHO2);
                gagueHO2.setValue(rHO2);
                txtH2O.setText("HO2: " + rHO2);
                calHO2 = dataSnapshot.getValue(Integer.class);*/

                calHO2 = dataSnapshot.getValue(Integer.class);
                mH2O.setValue((int) calHO2);
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


               /* mCO.setValue((int) rCO);
                gagueCO.setValue(rCO);
                txtCO.setText("CO: " + rCO);
                calCO = dataSnapshot.getValue(Integer.class);*/

                calCO = dataSnapshot.getValue(Integer.class);
                mCO.setValue((int) calCO);
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


               /* mSO2.setValue((int) rSO2);
                gagueSO2.setValue(rSO2);
                txtSO2.setText("SO2: " + rSO2);
                 calSO2 = dataSnapshot.getValue(Integer.class);
                */

                calSO2 = dataSnapshot.getValue(Integer.class);
                mSO2.setValue((int) calSO2);
                gagueSO2.setValue(calSO2);
                txtSO2.setText("SO2: " + calSO2);
                onAQIChange();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
      /*  btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random rand = new Random();
                rPM25 = rand.nextInt(100) + 1;
                rCO = rand.nextInt(100) + 1;
                rHO2 = rand.nextInt(100) + 1;
                rSO2 = rand.nextInt(100) + 1;

                mPM25.setValue((int) rPM25);
                mCO.setValue((int) rCO);
                mH2O.setValue((int) rHO2);
                mSO2.setValue((int) rSO2);

                int result = (calPM25 + calCO + calHO2 + calSO2) / 4;
                mAQI.setValue((int) result);
                gaugeAQI.setValue((int) result);
                txtAQI.setText("AQI: " + result);

            }
        });
*/
       /* mAQI.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int result = (calPM25 + calCO + calHO2 + calSO2) / 4;
                mAQI.setValue((int) result);
                gaugeAQI.setValue((int) result);
                txtAQI.setText("AQI: " + result +" 2");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });*/


    }
    public void onAQIChange(){

        int result = (calPM25 + calCO + calHO2 + calSO2) / 4;
        mAQI.setValue((int) result);
        gaugeAQI.setValue((int) result);
        txtAQI.setText("AQI: " + result +" 2");
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

            return 3;
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
