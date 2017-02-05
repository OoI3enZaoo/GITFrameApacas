package com.admin.gitframeapacas.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admin.gitframeapacas.R;

/**
 * Created by Admin on 2/1/2560.
 */

public class FeedFavoriteFragment extends Fragment {
    public FeedFavoriteFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_favorite, container, false);


        return v;

    }

/*
    public class RecyclrViewAdapter extends RecyclerView.Adapter<RecommendActivity.ViewHolder> {

        @Override
        public RecommendActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
            return new FeedFavoriteFragment().ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecommendActivity.ViewHolder holder, int position) {
            //   holder.location.setText("ตลาดไทย");
            //holder.status.setText("อากาศเยี่ยมยอด");
        }

        @Override
        public int getItemCount() {

            return 1;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtRecom;
        ImageView imgRecom;


        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("ben", "5");
            txtRecom = (TextView) itemView.findViewById(R.id.txt_recom);
            imgRecom = (ImageView) itemView.findViewById(R.id.img_recom);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("click", "Click: " + position);
            Toast.makeText(getApplicationContext(), "click: " + position, Toast.LENGTH_SHORT).show();
        }*/

}
