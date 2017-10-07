package com.example.wassim.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private static final String THUMBNAIL_BASE_URL = "http://img.youtube.com/vi";
    private static final String THUMBNAIL_PATH = "0.jpg";
    private ArrayList<String> mData;
    private onTrailerButtonClickListener mClickListener;
    private Context mContext;

    public TrailersAdapter(Context context, ArrayList<String> data,
                           onTrailerButtonClickListener clickListener) {
        mContext = context;
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
        String trailerId = mData.get(position);
        String thumbnailUrl = buildTrailerThumbnail(trailerId).toString();
        loadThumbnail(thumbnailUrl, holder);
    }

    private void loadThumbnail(String thumbnailUrl, ViewHolder holder) {
        Picasso.with(mContext)
                .load(thumbnailUrl)
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.error_image)
                .into(holder.thumbnail);
    }

    private Uri buildTrailerThumbnail(String trailerId) {
        // http://img.youtube.com/vi/jc86EFjLFV4/0.jpg
        return Uri.parse(THUMBNAIL_BASE_URL).buildUpon()
                .appendPath(trailerId)
                .appendPath(THUMBNAIL_PATH)
                .build();
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
        final ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.play_trailer_Image_view);
        }

        @Override
        public void onClick(View v) {
            String trailerId = mData.get(getAdapterPosition());
            mClickListener.onTrailerClick(trailerId);
        }
    }
}
