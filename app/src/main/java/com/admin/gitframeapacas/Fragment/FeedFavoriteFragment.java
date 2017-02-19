package com.admin.gitframeapacas.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
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

import com.admin.gitframeapacas.Data.FavoriteResponse;
import com.admin.gitframeapacas.Data.LastDataResponse;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBFavorite;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.admin.gitframeapacas.Views.DistrictActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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

    private String TAG = "BENFeedFavoriteFragment";
    private int myposition = 0;

    /*public void clearData() {
        snameArray.clear(); //clear list
        mAdapterAQI.notifyDataSetChanged(); //let your adapter know about the changes and reload view.

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
//                Toast.makeText(getActivity(), "Tapped " + snameArray.get(position) + "", Toast.LENGTH_SHORT).show();
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
        DBUser dbUser = new DBUser(getActivity());
        Cursor res = dbFavorite.getAllData();

        if (res.getCount() == 0) {
            Log.i(TAG, "count: " + res.getCount());
            Log.i(TAG, "usertype: " + dbUser.getUserType());
            if (dbUser.getUserType().equals("member")) {
                Log.i(TAG, "usertype come");
                String userid = dbUser.getUserID();
                Log.i(TAG, "userid: " + userid);
                new checkFavoriteTask().execute(userid);
            }
        } else {
            while (res.moveToNext()) {
                String sName = res.getString(0);
                String sCode = res.getString(1);
                String sAQI = res.getString(2);
                String sTime = res.getString(9);
                Log.i(TAG, "sname: " + sName + " sAQI: " + sAQI + " stime: " + sTime + " scode: " + sCode);
                String message = getAQItoMessage(sAQI);
                timeArray.add(sTime.toString());
                snameArray.add(sName.toString());
                messageArray.add(message);
                scodeArray.add(sCode);
                COArray.add(res.getString(3));
                NO2Array.add(res.getString(4));
                O3Array.add(res.getString(5));
                SO2Array.add(res.getString(6));
                PM25Array.add(res.getString(7));
                radArray.add(res.getString(8));
                aqiArray.add(sAQI);
                aqiColorArray.add(getAQIColor(sAQI));

            }
        }

        return v;
    }

    public interface OnItemTouchListener {

        void onCardViewTap(View view, int position);

        void onButton1Click(View view, int position);

        void onButton2Click(View view, int position);
    }

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

    private class checkFavoriteTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... userid) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("uid", userid[0])
                    .build();
            Request request = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/api/getFavorite.jsp")
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String result2 = response.body().string();


                result2 = result2.replaceAll("\\s+", "");

                Gson gson = new Gson();
                Type collectionType = new TypeToken<Collection<FavoriteResponse>>() {
                }.getType();
                Collection<FavoriteResponse> enums = gson.fromJson(result2, collectionType);
                FavoriteResponse[] result = enums.toArray(new FavoriteResponse[enums.size()]);

                Log.i(TAG, "Result2: " + result2 + " resultt.length: " + result.length);
                for (int i = 0; i < result.length; i++) {
                    Request request2 = new Request.Builder()
                            .url("http://sysnet.utcc.ac.th/aparcas/api/LastDataInRecord.jsp?scode=" + result[i].getScode())
                            .build();
                    Response response2 = client.newCall(request2).execute();
                    String result3 = response2.body().string();
                    Log.i(TAG, "result3: " + result3);
                    Gson gson2 = new Gson();
                    Type collectionType2 = new TypeToken<Collection<LastDataResponse>>() {
                    }.getType();
                    Collection<LastDataResponse> enums2 = gson2.fromJson(result3, collectionType2);
                    LastDataResponse[] res = enums2.toArray(new LastDataResponse[enums2.size()]);
                    timeArray.add(res[0].getTstamp());
                    snameArray.add(res[0].getSname());
                    String message = getAQItoMessage(res[0].getAqi());
                    messageArray.add(message);
                    scodeArray.add(result[i].getScode());
                    COArray.add(res[0].getCo());
                    NO2Array.add(res[0].getNo2());
                    O3Array.add(res[0].getO3());
                    SO2Array.add(res[0].getSo2());
                    PM25Array.add(res[0].getPm25());
                    radArray.add(res[0].getRad());
                    aqiArray.add(res[0].getAqi());
                    aqiColorArray.add(getAQIColor(res[0].getAqi()));
                    DBFavorite dbFavorite = new DBFavorite(getActivity());
                    dbFavorite.insertData(res[0].getSname(), result[i].getScode(), res[0].getAqi(), res[0].getCo(), res[0].getNo2(), res[0].getO3(), res[0].getSo2(), res[0].getPm25(), res[0].getRad(), res[0].getTstamp());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mAdapter.notifyDataSetChanged();
        }
    }
}





