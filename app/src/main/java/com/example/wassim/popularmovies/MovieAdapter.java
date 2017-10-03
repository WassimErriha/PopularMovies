package com.example.wassim.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wassim.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_FORMAT = "w185/";
    public onPosterClickListener mClickHandler;
    private ArrayList<Movie> mArrayListData;
    private Cursor mCursorData;
    private Context mContext;


    public MovieAdapter(Context context, ArrayList<Movie> data, onPosterClickListener clickHandler) {
        mArrayListData = data;
        mContext = context;
        mClickHandler = clickHandler;
    }

    public MovieAdapter(Context context, Cursor cursor, onPosterClickListener clickHandler) {
        mCursorData = cursor;
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean isFavoriteMovie = false;
        if (mArrayListData != null) {
            Movie movie = mArrayListData.get(position);
            String posterPath = movie.getmPosterPath();
            isFavoriteMovie = movie.getIsFavoriteMovie();
            loadImage(BASE_IMAGE_URL + POSTER_FORMAT + posterPath, holder);
        } else if (mCursorData.moveToPosition(position)) {
            String filePath = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)) + ".png";
            File imgFile = new File(mContext.getFilesDir(), filePath);
            if (imgFile.exists())
                holder.imageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            isFavoriteMovie = mCursorData.getInt(mCursorData
                    .getColumnIndex(MovieEntry.COLUMN_IS_FAVORITE_MOVIE)) == 1;
        }
        if (isFavoriteMovie)
            holder.favoriteMovieIcon.setVisibility(View.VISIBLE);
        else
            holder.favoriteMovieIcon.setVisibility(View.INVISIBLE);
    }

    public int getItemCount() {
        int itemCount = 0;
        if (mArrayListData != null && (mArrayListData.size() != 0))
            itemCount = mArrayListData.size();
        else if ((mCursorData != null) && (mCursorData.getCount() != 0))
            itemCount = mCursorData.getCount();
        return itemCount;
    }

    public void loadImage(String url, MovieAdapter.ViewHolder holder) {
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.error_image)
                .into(holder.imageView);
    }

    public interface onPosterClickListener {
        void onPosterClick(Movie paramMovie);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageView;
        private final ImageView favoriteMovieIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = ((ImageView) itemView.findViewById(R.id.movie_poster));
            favoriteMovieIcon = ((ImageView) itemView.findViewById(R.id.favorite_movie_icon));
        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movie;
            if (mArrayListData != null)
                movie = mArrayListData.get(position);
            else {
                mCursorData.moveToPosition(position);
                movie = constructObjectFromCursorRow();
            }
            mClickHandler.onPosterClick(movie);
        }

        /**
         * Helper method to construct a Movie object from a cursor row
         *
         * @return Movie object
         */
        private Movie constructObjectFromCursorRow() {
            String id = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
            String title = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_TITLE));
            String overview = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
            String thumbnailPath = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_THUMBNAIL_PATH));
            String posterPath = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
            String voteAverage = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
            String voteCount = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT));
            String popularity = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_POPULARITY));
            String releaseDate = mCursorData.getString(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
            boolean isFavoriteMovie = mCursorData.getInt(
                    mCursorData.getColumnIndex(MovieEntry.COLUMN_IS_FAVORITE_MOVIE)) == 1;

            return new Movie(id, thumbnailPath, title, overview, posterPath, voteAverage
                    , voteCount, popularity, releaseDate, isFavoriteMovie);
        }
    }
}
