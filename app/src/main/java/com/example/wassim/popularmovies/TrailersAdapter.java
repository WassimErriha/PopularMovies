package com.example.wassim.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private ArrayList<String> mData;
    private onTrailerButtonClickListener mClickListener;

    public TrailersAdapter(ArrayList<String> data, onTrailerButtonClickListener clickListener) {
        mData = data;
        mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.playTrailerTV.setText("Play Trailer " + (position + 1));

    }

    @Override
    public int getItemCount() {
        if (mData == null || mData.size() == 0) {
            return 0;
        }
        return mData.size();
    }

    public interface onTrailerButtonClickListener {
        void onTrailerClick(String trailerId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView playTrailerTV;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            playTrailerTV = (TextView) itemView.findViewById(R.id.play_trailer_tv);
        }

        @Override
        public void onClick(View v) {
            String trailerId = mData.get(getAdapterPosition());
            mClickListener.onTrailerClick(trailerId);
        }
    }
}
