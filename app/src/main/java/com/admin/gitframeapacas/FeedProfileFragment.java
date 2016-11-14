package com.admin.gitframeapacas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
        String[] textString = {"History", "Notification", "Reward"};
        int[] drawableIds = {R.drawable.ic_tap_history, R.drawable.ic_tap_notification, R.drawable.ic_tap_giftcad};
        CustomAdapter adapter = new CustomAdapter(v.getContext(), textString, drawableIds);
        listView1 = (ListView) v.findViewById(R.id.menu_setting);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position){
                    case 0:
                        Intent intent = new Intent(getActivity(),HistoryActivity.class);
                        startActivity(intent);

                        break;
                    case 1:
                        Toast.makeText(getActivity().getBaseContext(), country[position], Toast.LENGTH_SHORT ).show();
                        Log.i("benben",country[position]);
                        break;
                    default:
                        Toast.makeText(getActivity().getBaseContext(), country[position], Toast.LENGTH_SHORT ).show();
                        Log.i("benben",country[position]);
                        break;
                }
            }
        });
        return v;
    }
}

