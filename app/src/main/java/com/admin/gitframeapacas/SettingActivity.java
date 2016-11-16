package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Admin on 16/11/2559.
 */

public class SettingActivity extends AppCompatActivity {
    String[] Setting = new String[]{"ทั่วไป", "ความเป็นส่วนตัว", "การแจ้งเตือน", "เกี่ยวกับ"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("การตั้งค่า");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Setting);
        ListView mList = (ListView) findViewById(R.id.setting_list);
        mList.setDivider(null);
        mList.setAdapter(itemsAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position)
                {
                    case 0:
                        Toast.makeText(SettingActivity.this, "Get: " +Setting[position], Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(SettingActivity.this, "Get: " +Setting[position], Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(SettingActivity.this, "Get: " +Setting[position], Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(SettingActivity.this, "Get: " +Setting[position], Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
        });

    }

}
