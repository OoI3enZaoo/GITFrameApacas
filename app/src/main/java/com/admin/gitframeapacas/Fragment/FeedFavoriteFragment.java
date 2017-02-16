package com.admin.gitframeapacas.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import java.util.Arrays;
import java.util.List;


public class FeedFavoriteFragment extends Fragment {
    String[] snameArray;
    String[] messageArray;
    String[] timeArray;
    String[] scodeArray;
    String[] COArray;
    String[] NO2Array;
    String[] O3Array;
    String[] SO2Array;
    String[] PM25Array;
    String[] radArray;
    String[] aqiArray;
    private CardViewAdapter mAdapter;
    private List<String> scodeList;
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
    private int count = 0;
    private String TAG = "BENFeedFavoriteFragment";
    private int myposition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_favorite, container, false);

        DBFavorite dbFavorite = new DBFavorite(getActivity());
        Cursor res = dbFavorite.getAllData();
        timeArray = new String[dbFavorite.numberOfRows()];
        snameArray = new String[dbFavorite.numberOfRows()];
        messageArray = new String[dbFavorite.numberOfRows()];
        scodeArray = new String[dbFavorite.numberOfRows()];
        COArray = new String[dbFavorite.numberOfRows()];
        NO2Array = new String[dbFavorite.numberOfRows()];
        O3Array = new String[dbFavorite.numberOfRows()];
        SO2Array = new String[dbFavorite.numberOfRows()];
        PM25Array = new String[dbFavorite.numberOfRows()];
        radArray = new String[dbFavorite.numberOfRows()];
        aqiArray = new String[dbFavorite.numberOfRows()];

        if (res.getCount() == 0) {
            Log.i(TAG, "Nothing found");
        } else {
            while (res.moveToNext()) {
                String sName = res.getString(0);
                String sCode = res.getString(1);
                String sAQI = res.getString(2);
                String sTime = res.getString(9);
                Log.i(TAG, "sname: " + sName + " sAQI: " + sAQI + " stime: " + sTime + " scode: " + sCode);
                String message = setAQItoMessage(sAQI);
                timeArray[count] = sTime.toString();
                snameArray[count] = sName.toString();
                messageArray[count] = message;
                scodeArray[count] = sCode;
                COArray[count] = res.getString(3);
                NO2Array[count] = res.getString(4);
                O3Array[count] = res.getString(5);
                SO2Array[count] = res.getString(6);
                PM25Array[count] = res.getString(7);
                radArray[count] = res.getString(8);
                aqiArray[count] = sAQI;
                count++;
            }
        }

        snameList = new ArrayList<>(Arrays.asList(snameArray));
        messageList = new ArrayList<>(Arrays.asList(messageArray));
        timeList = new ArrayList<>(Arrays.asList(timeArray));
        scodeList = new ArrayList<>(Arrays.asList(scodeArray));
        COList = new ArrayList<>(Arrays.asList(COArray));
        NO2List = new ArrayList<>(Arrays.asList(NO2Array));
        O3List = new ArrayList<>(Arrays.asList(O3Array));
        SO2List = new ArrayList<>(Arrays.asList(SO2Array));
        PM25List = new ArrayList<>(Arrays.asList(PM25Array));
        radList = new ArrayList<>(Arrays.asList(radArray));
        aqiList = new ArrayList<>(Arrays.asList(aqiArray));

        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                Toast.makeText(getActivity(), "Tapped " + snameList.get(position) + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DistrictActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("district", snameList.get(position));
                bundle.putString("scode", scodeList.get(position));
                bundle.putString("co", COList.get(position));
                bundle.putString("no2", NO2List.get(position));
                bundle.putString("o3", O3List.get(position));
                bundle.putString("so2", SO2List.get(position));
                bundle.putString("pm25", PM25List.get(position));
                bundle.putString("rad", radList.get(position));
                bundle.putString("tstamp", timeList.get(position));
                bundle.putString("aqi", aqiList.get(position));


                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onButton1Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Button1 in " + snameList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton2Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Button2 in " + snameList.get(position), Toast.LENGTH_SHORT).show();
            }
        };

        mAdapter = new CardViewAdapter(snameList, itemTouchListener);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);


        return v;
    }

    public String setAQItoMessage(String strAQI) {

        String message = "";
        int aqi = Integer.parseInt(strAQI);
        if (aqi > -1 && aqi < 51) {
            message = "สภาพอากาศคุณภาพดี";
        } else if (aqi > 50 && aqi < 101) {
            message = "สภาพอากาศปานกลาง";
        } else if (aqi > 99 && aqi < 201) {
            message = "สภาพอากาศมีผลกระทบต่อสุขภาพ";
        } else if (aqi > 200 && aqi < 301) {
            message = "สภาพอากาศมีผลกระทบต่อสุขภาพมาก";
        } else { // more 300
            message = "สภาพอากาศอันตราย";
        }
        return message;
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
            viewHolder.txtstatusOfS.setText(messageList.get(i));
            viewHolder.txtsname.setText(snameList.get(i));
            viewHolder.txtTime.setText(timeList.get(i));
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

            public ViewHolder(View itemView) {
                super(itemView);


                txtstatusOfS = (TextView) itemView.findViewById(R.id.txtrecommend);
                txtsname = (TextView) itemView.findViewById(R.id.txtsname);
                txtTime = (TextView) itemView.findViewById(R.id.txtTime);
                btnRemove = (Button) itemView.findViewById(R.id.btnBin);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                    }
                });
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.i(TAG, "Position: " + getActivity());
                            /*snameList.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            Log.d("Test" , "Array list =" + snameList);
                            mAdapter.notifyDataSetChanged();*/


                    }
                });
            }
        }
    }

}





