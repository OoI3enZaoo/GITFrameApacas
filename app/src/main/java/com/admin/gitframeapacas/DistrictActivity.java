package com.admin.gitframeapacas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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


    }

}
