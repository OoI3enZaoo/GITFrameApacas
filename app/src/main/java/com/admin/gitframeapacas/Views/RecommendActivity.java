package com.admin.gitframeapacas.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.gitframeapacas.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 19/11/2559.
 */

public class RecommendActivity extends AppCompatActivity {

    public static int mItem = 1;
    public static CardViewAdapterAQI mAdapterAQI;
    public static CardViewAdapterRAD mAdapterRAD;
    public static ArrayList<String> snameArray = new ArrayList<String>();
    private static ArrayList<String> aqiArray_AQI = new ArrayList<>();
    private static ArrayList<Integer> picArray_AQI = new ArrayList<>();
    private static ArrayList<String> statucArray_AQI = new ArrayList<>();
    private static ArrayList<String> aqiArray_RAD = new ArrayList<>();
    private static ArrayList<Integer> picArray_RAD = new ArrayList<>();
    private static ArrayList<String> statucArray_RAD = new ArrayList<>();
    private String TAG = "BENRecommendActivity";
    private TextView txtAQI;
    private TextView txtStatusAQI;
    private TextView txtRAD;
    private TextView txtStatusRAD;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("การปฏิบัติตัว");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtStatusAQI = (TextView) findViewById(R.id.txtStatus);
        txtAQI = (TextView) findViewById(R.id.txtAQI);

        txtStatusRAD = (TextView) findViewById(R.id.txtStatusRAD);
        txtRAD = (TextView) findViewById(R.id.txtRAD);


        Intent intent = getIntent();
        String aqi = intent.getStringExtra("aqi").toString();
        txtAQI.setText("AQI: " + aqi);
        String status = getStatusAQI(aqi);
        txtStatusAQI.setText(status);

        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
            }

            @Override
            public void onButton1Click(View view, int position) {

            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };


        mAdapterAQI = new CardViewAdapterAQI(statucArray_AQI, itemTouchListener);
        RecyclerView recyclerViewAQI = (RecyclerView) findViewById(R.id.recycler_recommend_AQI);
        recyclerViewAQI.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewAQI.setAdapter(mAdapterAQI);
        picArray_AQI.clear();
        aqiArray_AQI.clear();
        statucArray_AQI.clear();

        aqiArray_AQI.add(aqi);
        picArray_AQI.add(getImageRecommentAQI(aqi, 1));
        statucArray_AQI.add(getTextRecommendAQI(aqi, 1));


        aqiArray_RAD.clear();
        picArray_RAD.clear();
        statucArray_RAD.clear();
        mAdapterRAD = new CardViewAdapterRAD(statucArray_RAD, itemTouchListener);
        RecyclerView recyclerViewRAD = (RecyclerView) findViewById(R.id.recycler_recommend_RAD);
        recyclerViewRAD.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewRAD.setAdapter(mAdapterRAD);
        String rad = intent.getStringExtra("rad").toString();
        txtRAD.setText("Radioactive: " + rad + " uSv/h");
        String statusrad = getStatusRAD(rad);
        txtStatusRAD.setText(statusrad);
        aqiArray_RAD.add(rad);
        picArray_RAD.add(getImageRecommentRAD(rad));
        statucArray_RAD.add(getTextRecommendRAD(rad));

    }


    private int getImageRecommentAQI(String aqi1, int round) {
        int aqi = Integer.parseInt(aqi1);
        if (aqi >= -1 && aqi <= 50) {
            if (round == 0) {
                return R.drawable.emo_1;
            } else {
                return R.drawable.emo_1;
            }
        } else if (aqi >= 51 && aqi <= 100) {
            if (round == 0) {
                return R.drawable.emo_2;
            } else {
                return R.drawable.emo_2;
            }
        } else if (aqi >= 101 && aqi <= 200) {
            if (round == 0) {
                return R.drawable.emo_3;
            } else {
                return R.drawable.emo_3;
            }
        } else if (aqi >= 201 && aqi <= 300) {
            if (round == 0) {
                return R.drawable.emo_4;
            } else {
                return R.drawable.emo_4;
            }
        } else if (aqi >= 300) {
            if (round == 0) {
                return R.drawable.emo_5;
            } else {
                return R.drawable.emo_5;
            }
        }

        return R.drawable.ic_assignment_blue_24dp;
    }

    private String getTextRecommendAQI(String aqi1, int round) {
        int aqi = Integer.parseInt(aqi1);
        if (aqi >= -1 && aqi <= 50) {
            return "ไม่มีผลกระทบต่อสุขภาพ";
        } else if (aqi >= 51 && aqi <= 100) {

            return "ไม่มีผลกระทบต่อสุขภาพ";

        } else if (aqi >= 101 && aqi <= 200) {

            return "ผู้ป่วยโรคระบบทางเดินหายใจ ควรหลีกเลี่ยงการออกกำลังภายนอกอาคาร" +
                    "บุคคลทั่วไป โดยเฉพาะเด็กและผู้สูงอายุ ไม่ควรทำกิจกรรมภายนอกอาคารเป็นเวลานาน";

        } else if (aqi >= 201 && aqi <= 300) {

            return "ผู้ป่วยโรคระบบทางเดินหายใจ ควรหลีกเลี่ยงกิจกรรมภายนอกอาคาร " +
                    "บุคคลทั่วไป โดยเฉพาะเด็กและผู้สูงอายุ ควรจำกัดการออกกำลังภายนอกอาคาร";

        } else if (aqi >= 300) {

            return "ุคคลทั่วไป ควรหลีกเลี่ยงการออกกำลังภายนอกอาคาร " +
                    "สำหรับผู้ป่วยโรคระบบทางเดินหายใจ ควรอยู่ภายในอาคาร";

        }

        return "";
    }

    private String getStatusAQI(String mAQI) {

        int aqi = Integer.parseInt(mAQI);
        if (aqi >= -1 && aqi <= 50) {
            return "สภาพอากาศคุณภาพดี";
        } else if (aqi >= 51 && aqi <= 100) {
            return "สภาพอากาศคุณภาพปานปลาง";
        } else if (aqi >= 101 && aqi <= 200) {
            return "สภาพอากาศมีผลกระทบต่อสุขภาพ";
        } else if (aqi >= 201 && aqi <= 300) {
            return "สภาพอากาศมีผลกระทบต่อสุขภาพมาก";
        } else if (aqi >= 300) {
            return "สภาพอากาศอันตราย";
        }

        return "";
    }


    private String getStatusRAD(String mAQI) {

        float rad = Float.parseFloat(mAQI);
        if (rad >= -1 && rad <= 0.5f) {
            return "คุณภาพดี";
        } else if (rad >= 0.5f && rad <= 0.9f) {
            return "เฝ้าระวัง";
        } else if (rad >= 1) {
            return "อันตราย";
        }
        return "";
    }

    private int getImageRecommentRAD(String rad2) {
        float rad = Float.parseFloat(rad2);
        if (rad >= -1 && rad <= 0.5f) {
            return R.drawable.emo_1;
        } else if (rad >= 0.5f && rad <= 0.9f) {
            return R.drawable.emo_3;
        } else if (rad >= 1) {
            return R.drawable.emo_5;
        }

        return R.drawable.emo_5;

    }

    private String getTextRecommendRAD(String rad2) {
        float rad = Float.parseFloat(rad2);
        if (rad >= -1 && rad <= 0.5f) {
            return "ระดับกัมมันตรังสีปกติในธรรมชาติที่มนุษย์สามารถรับได้โดยไม่มีอันตรายต่อสุขภาพ";
        } else if (rad >= 0.5f && rad <= 0.9f) {
            return "ระดับกัมมันตรังสีเกิน 5 เท่าของค่าเฉลี่ยทั้งปี ณ บริเวณนั้นๆ " +
                    "โดยประชาชนควรหลีกเลี่ยงบริเวณดังกล่าว เนื่องจากอาจทำให้มีอัตรายต่อสุขภาพและปริมาณเม็ดเลือดขาวในร่างกายลดลง";
        } else if (rad >= 1) {
            return "ระดับกัมมันตรังสีเกินเกณฑ์สูงสุดที่อนุญาตให้ประชาชนรับได้ " +
                    "ประชาชนไม่ควรเดินทางเข้าสู่บริเวณดังกล่าว เนื่องจากอาจ " +
                    "มีอาการเคลื่อนเหียน อาเจียน และปริมาณเม็ดเลือดขาวในร่างกายลดลง " +
                    "ถ้ารับสารกัมมันตรังสีในปริมาณมากๆ อย่างต่อเนื่องอาจเป็นอันตรายถึงชีวิต";
        }
        return "";
    }


    public interface OnItemTouchListener {

        void onCardViewTap(View view, int position);

        void onButton1Click(View view, int position);

        void onButton2Click(View view, int position);
    }

    public class CardViewAdapterAQI extends RecyclerView.Adapter<CardViewAdapterAQI.ViewHolder> {
        private List<String> cardAQI;
        private OnItemTouchListener onItemTouchListener;

        public CardViewAdapterAQI(List<String> cards, OnItemTouchListener onItemTouchListener) {
            this.cardAQI = cards;
            this.onItemTouchListener = onItemTouchListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recommend_aqi, viewGroup, false);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.txtHowAQI.setText(statucArray_AQI.get(i));
            viewHolder.imgHowAQI.setImageResource(picArray_AQI.get(i));
        }

        @Override
        public int getItemCount() {
            return cardAQI == null ? 0 : cardAQI.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView imgHowAQI;
            private TextView txtHowAQI;

            public ViewHolder(View itemView) {
                super(itemView);
                txtHowAQI = (TextView) itemView.findViewById(R.id.txtHowAQI);
                imgHowAQI = (ImageView) itemView.findViewById(R.id.imgHowAQI);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                    }
                });
            }
        }
    }


    public class CardViewAdapterRAD extends RecyclerView.Adapter<CardViewAdapterRAD.ViewHolder> {
        private List<String> cardRAD;
        private OnItemTouchListener onItemTouchListener;

        public CardViewAdapterRAD(List<String> cards, OnItemTouchListener onItemTouchListener) {
            this.cardRAD = cards;
            this.onItemTouchListener = onItemTouchListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recommend_rad, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.txtHowRAD.setText(statucArray_RAD.get(i));
            viewHolder.imgHowRAD.setImageResource(picArray_RAD.get(i));
        }

        @Override
        public int getItemCount() {
            return cardRAD == null ? 0 : cardRAD.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imgHowRAD;
            private TextView txtHowRAD;

            public ViewHolder(View itemView) {
                super(itemView);
                txtHowRAD = (TextView) itemView.findViewById(R.id.txtHowRAD);
                imgHowRAD = (ImageView) itemView.findViewById(R.id.imgHowRAD);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                    }
                });
            }
        }
    }
}
