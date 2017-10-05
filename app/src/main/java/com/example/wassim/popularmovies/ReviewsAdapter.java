package com.example.wassim.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Wassim on 2017-09-19
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private ArrayList<Review> mData;

    public ReviewsAdapter(ArrayList<Review> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = mData.get(position);
        String reviewAuthor = review.getReviewAuthor();
        String reviewText = review.getReviewText();
        holder.nameTextView.setText(reviewAuthor);
        holder.reviewTextView.setText(reviewText);

    }

    @Override
    public int getItemCount() {
        if (mData == null || mData.size() == 0) {
            return 0;
        }
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView reviewTextView;

        public ViewHolder(View itemView) {

            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.review_author_name);
            reviewTextView = (TextView) itemView.findViewById(R.id.review_text);

        }
    }
}
