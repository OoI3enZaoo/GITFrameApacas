package com.admin.gitframeapacas.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBFavorite;
import com.admin.gitframeapacas.Views.DistrictActivity;

import java.util.ArrayList;
import java.util.List;


public class FeedFavoriteFragment extends Fragment {
    public static ArrayList<String> snameArray = new ArrayList<String>();
    public static ArrayList<String> timeArray = new ArrayList<String>();
    public static ArrayList<String> scodeArray = new ArrayList<String>();
    public static ArrayList<String> COArray = new ArrayList<String>();
    public static ArrayList<String> NO2Array = new ArrayList<String>();
    public static ArrayList<String> O3Array = new ArrayList<String>();
    public static ArrayList<String> SO2Array = new ArrayList<String>();
    public static ArrayList<String> PM25Array = new ArrayList<String>();
    public static ArrayList<String> radArray = new ArrayList<String>();
    public static ArrayList<String> aqiArray = new ArrayList<String>();
    public static ArrayList<String> aqiColorArray = new ArrayList<String>();
    public static ArrayList<String> messageArray = new ArrayList<String>();

    public static CardViewAdapter mAdapter;
    int count = 0;
    /*    private List<String> scodeList;
        private List<String> snameList;
        private List<String> messageList;
        private List<String> timeList;
        private List<String> COList;
        private List<String> NO2List;
        private List<String> O3List;
        private List<String> SO2List;
        private List<String> PM25List;
        private List<String> radList;
        private List<String> aqiList;
        private List<String> aqiColorList;*/
    private String TAG = "BENFeedFavoriteFragment";
    private int myposition = 0;

    /*public void clearData() {
        snameArray.clear(); //clear list
        mAdapter.notifyDataSetChanged(); //let your adapter know about the changes and reload view.

    }*/
    public static String getAQItoMessage(String strAQI) {

        String message = "";
        int aqi = Integer.parseInt(strAQI);
        if (aqi > -1 && aqi < 51) {
            message = "สภาพอากาศคุณภาพดี";
        } else if (aqi > 50 && aqi < 101) {
            message = "สภาพอากาศปานกลาง";
        } else if (aqi > 99 && aqi < 201) {
            message = "สภาพอากาศมีผลกระทบต่อสุขภาพ";
        } else if (aqi > 200 && aqi < 300) {
            message = "สภาพอากาศมีผลกระทบต่อสุขภาพมาก";
        } else { // more 300
            message = "สภาพอากาศอันตราย";
        }
        return message;
    }

    public static String getAQIColor(String strAQI) {

        String color = "";
        int aqi = Integer.parseInt(strAQI);
        if (aqi > -1 && aqi < 51) {
            color = "#b3e0ff";
        } else if (aqi > 50 && aqi < 101) {
            color = "#c2f0c2";
        } else if (aqi > 99 && aqi < 201) {
            color = "#ffffb3";
        } else if (aqi > 200 && aqi < 300) {
            color = "#ffd9b3";
        } else { // more 300
            color = "#ffcccc";
        }
        return color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_favorite, container, false);

        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                Toast.makeText(getActivity(), "Tapped " + snameArray.get(position) + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DistrictActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("district", snameArray.get(position));
                bundle.putString("scode", scodeArray.get(position));
                bundle.putString("co", COArray.get(position));
                bundle.putString("no2", NO2Array.get(position));
                bundle.putString("o3", O3Array.get(position));
                bundle.putString("so2", SO2Array.get(position));
                bundle.putString("pm25", PM25Array.get(position));
                bundle.putString("rad", radArray.get(position));
                bundle.putString("tstamp", timeArray.get(position));
                bundle.putString("aqi", aqiArray.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onButton1Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Button1 in " + snameArray.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton2Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Button2 in " + snameArray.get(position), Toast.LENGTH_SHORT).show();
            }
        };

        mAdapter = new CardViewAdapter(snameArray, itemTouchListener);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        timeArray.clear();
        snameArray.clear();
        messageArray.clear();
        scodeArray.clear();
        COArray.clear();
        NO2Array.clear();
        O3Array.clear();
        SO2Array.clear();
        PM25Array.clear();
        radArray.clear();
        aqiArray.clear();
        aqiColorArray.clear();
        mAdapter.notifyDataSetChanged();

        DBFavorite dbFavorite = new DBFavorite(getActivity());
        Cursor res = dbFavorite.getAllData();

        if (res.getCount() == 0) {
            Log.i(TAG, "Nothing found");
        } else {
            while (res.moveToNext()) {
                String sName = res.getString(0);
                String sCode = res.getString(1);
                String sAQI = res.getString(2);
                String sTime = res.getString(9);
                Log.i(TAG, "sname: " + sName + " sAQI: " + sAQI + " stime: " + sTime + " scode: " + sCode);
                String message = getAQItoMessage(sAQI);
                timeArray.add(count, sTime.toString());
                snameArray.add(count, sName.toString());
                messageArray.add(count, message);
                scodeArray.add(count, sCode);
                COArray.add(count, res.getString(3));
                NO2Array.add(count, res.getString(4));
                O3Array.add(count, res.getString(5));
                SO2Array.add(count, res.getString(6));
                PM25Array.add(count, res.getString(7));
                radArray.add(count, res.getString(8));
                aqiArray.add(count, sAQI);
                aqiColorArray.add(count, getAQIColor(sAQI));

            }
        }
        return v;
    }

    /**
     * Interface for the touch events in each item
     */
    public interface OnItemTouchListener {
        /**
         * Callback invoked when the user Taps one of the RecyclerView items
         *
         * @param view     the CardView touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onCardViewTap(View view, int position);

        /**
         * Callback invoked when the Button1 of an item is touched
         *
         * @param view     the Button touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onButton1Click(View view, int position);

        /**
         * Callback invoked when the Button2 of an item is touched
         *
         * @param view     the Button touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onButton2Click(View view, int position);
    }

    /**
     * A simple adapter that loads a CardView layout with one TextView and two Buttons, and
     * listens to clicks on the Buttons or on the CardView
     */
    public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
        private List<String> cards;
        private OnItemTouchListener onItemTouchListener;

        public CardViewAdapter(List<String> cards, OnItemTouchListener onItemTouchListener) {
            this.cards = cards;
            this.onItemTouchListener = onItemTouchListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            //  viewHolder.sname.setText(cards.get(i));
            String Time = timeArray.get(i);
            Time = Time.substring(0, 16);
            viewHolder.txtstatusOfS.setText(messageArray.get(i));
            viewHolder.txtsname.setText(snameArray.get(i));
            viewHolder.txtTime.setText(Time);
            viewHolder.constraintLayout.setBackgroundColor(Color.parseColor(aqiColorArray.get(i)));
            myposition = i;


        }

        @Override
        public int getItemCount() {
            return cards == null ? 0 : cards.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            //   private TextView sname;
            private TextView txtstatusOfS;
            private TextView txtsname;
            private TextView txtTime;
            private Button btnRemove;
            private ConstraintLayout constraintLayout;

            public ViewHolder(View itemView) {
                super(itemView);


                txtstatusOfS = (TextView) itemView.findViewById(R.id.txtrecommend);
                txtsname = (TextView) itemView.findViewById(R.id.txtsname);
                txtTime = (TextView) itemView.findViewById(R.id.txtTime);
                btnRemove = (Button) itemView.findViewById(R.id.btnBin);
                constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintBackground);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                    }
                });
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        snameArray.remove(getLayoutPosition());
                        mAdapter.notifyItemRemoved(getLayoutPosition());
                        mAdapter.notifyDataSetChanged();
                        DBFavorite dbFavorite = new DBFavorite(getActivity());
                        dbFavorite.deleteData(scodeArray.get(getLayoutPosition()));
                        Log.i(TAG, "Scode: " + scodeArray.get(getLayoutPosition()));
                        //Toast.makeText(getActivity(), "คุณได้ลบ " + snameArray.get(getLayoutPosition()) + "ออกจากรายการโปรดแล้ว", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

}





