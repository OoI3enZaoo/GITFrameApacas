package com.admin.gitframeapacas.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.admin.gitframeapacas.R;

/**
 * Created by Admin on 2/1/2560.
 */

public class FeedFavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    public FeedFavoriteFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclearView);
        recyclerView.setAdapter(new RecyclerViewAdapter());

        return v;

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //View v = LayoutInflater.from(getActivity().getLayoutInflater(R.layout.item_favorite,parent,false));
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.item_favorite, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.sname.setText("ben");

        }
        @Override
        public int getItemCount() {
            return 2;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sname;
        public ViewHolder(View itemView) {
            super(itemView);
            sname = (TextView) itemView.findViewById(R.id.txtsname);
        }
    }



}
