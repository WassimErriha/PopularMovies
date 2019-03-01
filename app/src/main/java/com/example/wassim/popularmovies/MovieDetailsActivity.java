package com.example.wassim.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.wassim.popularmovies.data.MovieContract.MovieEntry;
import com.example.wassim.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity
        implements TrailersAdapter.onTrailerButtonClickListener {

    public static final int IS_FAVORITE_TRUE = 1;
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_FORMAT = "original/";
    private static final String VIDEOS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";
    private static final String BASE_YOUTUBE_URL = "https://m.youtube.com";
    private static final String PATH_WATCH = "watch";
    private static final String VIDEO_PARAM = "v";
    public static ArrayList<String> favoriteMoviesArrayList;
    private static Movie mMovie;
    public ActivityMovieDetailsBinding mBinding;
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter reviewsAdapter;
    private String movieId;
    private RecyclerView trailersRecyclerView;
    private TrailersAdapter trailersAdapter;


    @SuppressLint("StaticFieldLeak")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mMovie = getIntent().getExtras().getParcelable("movie");
            mMovie.setFavoriteMovie(savedInstanceState.getBoolean("isFavorite"));
        } else {
            mMovie = getIntent().getExtras().getParcelable("movie");
        }

        setTitle("Details Activity");
        bindDataToViews();
        movieId = mMovie.getmID();
        Uri movieTrailerUri = buildMovieUrlWithId(movieId, VIDEOS_PATH);
        Uri movieReviewsUri = buildMovieUrlWithId(movieId, REVIEWS_PATH);
        favoriteMoviesArrayList = new ArrayList<>();
        favoriteMoviesArrayList.addAll(MainActivity.favoriteMoviesArrayList);

        AsyncTask<String, Void, ArrayList<String>> getTrailerIds =
                new AsyncTask<String, Void, ArrayList<String>>() {
                    @Override
                    protected ArrayList<String> doInBackground(String... strings) {
                        return QueryUtils.fetchTrailerIds(strings[0]);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<String> trailerIds) {
                        if (trailerIds != null) {
                            trailersAdapter = new TrailersAdapter(MovieDetailsActivity.this
                                    , trailerIds, MovieDetailsActivity.this);
                            trailersRecyclerView.setAdapter(trailersAdapter);
                        }
                    }
                }.execute(movieTrailerUri.toString());

        AsyncTask<String, Void, ArrayList<Review>> getReviews =
                new AsyncTask<String, Void, ArrayList<Review>>() {
                    @Override
                    protected ArrayList<Review> doInBackground(String... strings) {
                        return QueryUtils.fetchMovieReviews(strings[0]);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Review> reviews) {
                        if (reviews != null) {
                            reviewsAdapter = new ReviewsAdapter(reviews);
                            reviewsRecyclerView.setAdapter(reviewsAdapter);
                        } else
                            Toast.makeText(MovieDetailsActivity.this
                                    , "No reviews for this movie yet.", Toast.LENGTH_LONG).show();
                    }
                }.execute(movieReviewsUri.toString());

        reviewsRecyclerView = (RecyclerView) findViewById(R.id.reviews_recycler_view);
        reviewsRecyclerView.setLayoutManager(new GridLayoutManager(MovieDetailsActivity.this
                , 1));
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setVisibility(View.GONE);
        mBinding.toggleArrow.setBackgroundResource(R.drawable.ic_arrow_down_black_24px);
        mBinding.addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mMovie.getIsFavoriteMovie()) addMovieToFavorites();
                else deleteMovieFromFavorites();
            }
        });
        trailersRecyclerView = (RecyclerView) findViewById(R.id.movie_trailers_recycler_view);
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        trailersRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isFavorite", mMovie.getIsFavoriteMovie());
        super.onSaveInstanceState(outState);
    }

    /**
     * This is a helper method to delete a movie from database,
     * delete its id from favoriteMoviesArrayList, and delete its poster image from FileSystem
     */
    private void deleteMovieFromFavorites() {
        Uri movieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI
                , Long.parseLong(movieId));
        int numberOfRowsDeleted = getContentResolver().delete(
                movieUri, null, null);
        if ((favoriteMoviesArrayList.remove(movieId)) && (numberOfRowsDeleted > 0)) {
            mBinding.addImageButton.setImageResource(R.drawable.ic_add);
            Toast.makeText(MovieDetailsActivity.this
                    , "Deleted from favorites", Toast.LENGTH_LONG).show();
            mMovie.setFavoriteMovie(false);
        }
    }

    /**
     * This is a helper method to insert a movie into database,
     * add its id to favoriteMoviesArrayList, and insert its poster image into FileSystem
     */
    private void addMovieToFavorites() {
        // Insertion is slowing main thread. show toast message before inserting row
        mBinding.addImageButton.setImageResource(R.drawable.ic_done);
        Toast.makeText(MovieDetailsActivity.this
                , "Added to favorites", Toast.LENGTH_LONG).show();

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> at = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                favoriteMoviesArrayList.add(movieId);
                mMovie.setFavoriteMovie(true);
                getContentResolver().insert(MovieEntry.CONTENT_URI, movieContentValues());
                return null;
            }
        }.execute();
    }

    public void toggleReviewsRecyclerView(View v) {
        reviewsRecyclerView.setVisibility(reviewsRecyclerView.isShown() ? View.GONE : View.VISIBLE);
        mBinding.toggleArrow
                .setBackgroundResource(reviewsRecyclerView.isShown()
                        ? R.drawable.ic_arrow_up_black_24px
                        : R.drawable.ic_arrow_down_black_24px);
    }

    private void bindDataToViews() {
        mBinding = (DataBindingUtil.setContentView(this, R.layout.activity_movie_details));
        String overview = "\t\t\t" + mMovie.getmOverview();
        String vote = mMovie.getmVoteAverage() + "/10";
        mBinding.movieOverview.setText(overview);
        mBinding.movieTitle.setText(mMovie.getmTitle());
        mBinding.voteAverageTv.setText(vote);
        mBinding.voteCountTv.setText(mMovie.getmVoteCount());
        mBinding.popularityCountTv.setText(mMovie.getmPopularity());
        mBinding.releaseDateTv2.setText(mMovie.getmReleaseDate());
        if (mMovie.getIsFavoriteMovie())
            mBinding.addImageButton.setImageResource(R.drawable.ic_done);
        loadPoster(BASE_IMAGE_URL + POSTER_FORMAT + mMovie.getmPosterPath());
        loadThumbnail(BASE_IMAGE_URL + POSTER_FORMAT + mMovie.getmThumbnail());
        // finally if user is is offline, hide movieTrailersRecyclerView
        mBinding.movieTrailersRecyclerView.setVisibility(
                !Utility.isNetworkAvailable(this) ? View.GONE : View.VISIBLE);
    }

    public void loadThumbnail(String path) {
        Picasso.with(this)
                .load(path)
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.error_image)
                .into(mBinding.thumbnail);
    }

    public void loadPoster(final String path) {
        Picasso.with(this)
                .load(path)
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.error_image)
                .into(mBinding.daMoviePoster);
    }

    private Uri buildMovieUrlWithId(String id, String path) {
        return Uri.parse(MainActivity.BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(path)
                .appendQueryParameter(MainActivity.LANGUAGE_PARAM, MainActivity.LANGUAGE_VALUE)
                .appendQueryParameter(MainActivity.API_KEY_PARAM, MainActivity.API_KEY)
                .build();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // or onBackPressed()
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This is a helper method used for inserting movie object into SQLite database
     */
    private ContentValues movieContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MOVIE_ID, mMovie.getmID());
        values.put(MovieEntry.COLUMN_TITLE, mMovie.getmTitle());
        values.put(MovieEntry.COLUMN_OVERVIEW, mMovie.getmOverview());
        values.put(MovieEntry.COLUMN_POSTER_PATH, mMovie.getmPosterPath());
        values.put(MovieEntry.COLUMN_THUMBNAIL_PATH, mMovie.getmThumbnail());
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getmVoteAverage());
        values.put(MovieEntry.COLUMN_VOTE_COUNT, mMovie.getmVoteCount());
        values.put(MovieEntry.COLUMN_POPULARITY, mMovie.getmPopularity());
        values.put(MovieEntry.COLUMN_RELEASE_DATE, mMovie.getmReleaseDate());
        values.put(MovieEntry.COLUMN_IS_FAVORITE_MOVIE, IS_FAVORITE_TRUE);
        return values;
    }

    private Uri buildYoutubeTrailerUri(String videoId) {
        return Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendPath(PATH_WATCH)
                .appendQueryParameter(VIDEO_PARAM, videoId)
                .build();
    }

    @Override
    public void onTrailerClick(String trailerId) {
        Uri youtubeTrailerUri = buildYoutubeTrailerUri(trailerId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(youtubeTrailerUri);
        MovieDetailsActivity.this.startActivity(intent);
    }
}
