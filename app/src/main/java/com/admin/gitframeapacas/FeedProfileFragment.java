package com.admin.gitframeapacas;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


/**
 * Created by Admin on 13/11/2559.
 */

public class FeedProfileFragment extends Fragment {

    ListView listView1;
    String [] country = new String[] {
            "India",
            "Japan",
            "Pakistan"
    };
    public FeedProfileFragment(){


    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        String[] textString = {"ประวัติการเข้าชม", "การแจ้งเตือน", "รางวัล"};
        int[] drawableIds = {R.drawable.ic_tap_history, R.drawable.ic_tap_notification, R.drawable.ic_tap_giftcad};
        CustomAdapter adapter = new CustomAdapter(v.getContext(), textString, drawableIds);
        listView1 = (ListView) v.findViewById(R.id.menu_setting);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity().getBaseContext(), country[i], Toast.LENGTH_SHORT ).show();
                Log.i("benben",country[i]);
            }
        });

        return v;
    }
}

