package com.admin.gitframeapacas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

public class DistrictActivity extends AppCompatActivity {
    Toolbar toolbar;
    String DistrictName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        DistrictName = intent.getStringExtra("district").toString();
        toolbar.setTitle(DistrictName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtLocation.setText(DistrictName);

        FloatingActionButtonPlus mActionButtonPlus = (FloatingActionButtonPlus) findViewById(R.id.ActionButtonPlus);
        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Add to favorite", Toast.LENGTH_SHORT).show();
                        break;// first menu(add to favorite)
                    case 1:
                        Toast.makeText(getApplicationContext(), "Chart", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DistrictActivity.this, GraphGasActivity.class);
                        startActivity(intent);
                        break;// second menu(Chart)

                }

            }
        });

    }

}
