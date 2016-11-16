package com.admin.gitframeapacas;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Admin on 16/11/2559.
 */

public class HelpActivity extends AppCompatActivity {
    String[] Setting = new String[]{"การเพิ่มรายการโปรด", "วิธีดูพื้นที่อื่นๆ", "สมัครสมาชิกแล้วได้อะไร", "ขั้นตอนการใช้งาน"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ความช่วยเหลือ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_while_24dp);

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Setting);
        ListView mList = (ListView) findViewById(R.id.helpList);
        //mList.setDivider(null);
        mList.setAdapter(itemsAdapter);



    }
}
