package com.example.wassim.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.wassim.popularmovies.data.MovieContract.MovieEntry;
import com.example.wassim.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {

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
    private Uri youtubeTrailerUri;
    private RecyclerView reviewsRecyclerView;
    private ReviewListAdapter reviewListAdapter;
    private Bitmap mPosterBitmap;
    private String movieId;


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

        AsyncTask<String, Void, String> mGetTrailerId =
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        return QueryUtils.fetchTrailerId(strings[0]);
                    }

                    @Override
                    protected void onPostExecute(String trailerId) {
                        if (trailerId != null)
                            youtubeTrailerUri = buildYoutubeTrailerUrl(trailerId);
                    }
                }.execute(movieTrailerUri.toString());

        AsyncTask<String, Void, ArrayList<Review>> mGetReviews =
                new AsyncTask<String, Void, ArrayList<Review>>() {
                    @Override
                    protected ArrayList<Review> doInBackground(String... strings) {
                        return QueryUtils.fetchMovieReviews(strings[0]);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Review> reviews) {
                        if (reviews != null) {
                            reviewListAdapter = new ReviewListAdapter(reviews);
                            reviewsRecyclerView.setAdapter(MovieDetailsActivity.this.reviewListAdapter);
                        } else
                            Toast.makeText(MovieDetailsActivity.this
                                    , "No reviews for this movie yet.", Toast.LENGTH_LONG).show();
                    }
                }.execute(movieReviewsUri.toString());

        reviewsRecyclerView = ((RecyclerView) findViewById(R.id.reviews_recycler_view));
        reviewsRecyclerView.setLayoutManager(new GridLayoutManager(MovieDetailsActivity.this
                , 1));
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setVisibility(View.GONE);
        mBinding.toggleArrow.setBackgroundResource(R.drawable.ic_arrow_down_black_24px);
        mBinding.addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mMovie.getIsFavoriteMovie())
                    addMovieToFavorites();
                else
                    deleteMovieFromFavorites();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //mPosterBitmap is too large to parcel out. Reload image in on Resume instead.
        // No need to cache image in tempFile as it is cached by Picasso Lib.
        outState.putBoolean("isFavorite", mMovie.getIsFavoriteMovie());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //reload poster to keep from losing reference to mPosterBitmap
        loadPoster(BASE_IMAGE_URL + POSTER_FORMAT + mMovie.getmPosterPath());
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
            MovieDetailsActivity.this.deleteFile(movieId + ".png");
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
                addImageToFileSystem(MovieDetailsActivity.this, mPosterBitmap);
                favoriteMoviesArrayList.add(movieId);
                mMovie.setFavoriteMovie(true);
                getContentResolver().insert(
                        MovieEntry.CONTENT_URI, movieContentValues());
                return null;
            }
        }.execute();
    }

    /**
     * This is a helper method to add an image into fileSystem.
     */
    private void addImageToFileSystem(Context context, Bitmap mPosterBitmap) {
        String fileName = mMovie.getmID() + ".png";
        final FileOutputStream fos;
        try {
            fos = openFileOutput(fileName, context.MODE_PRIVATE);
            mPosterBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
        mBinding.playTrailerIv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(youtubeTrailerUri);
                MovieDetailsActivity.this.startActivity(intent);
            }
        });
        if (mMovie.getIsFavoriteMovie())
            mBinding.addImageButton.setImageResource(R.drawable.ic_done);
        String filePath = mMovie.getmID() + ".png";
        File imgFile = new File(this.getFilesDir(), filePath);
        if (imgFile.exists()) {
            mBinding.daMoviePoster.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            mBinding.thumbnail.setVisibility(View.GONE);
        } else {
            loadPoster(BASE_IMAGE_URL + POSTER_FORMAT + mMovie.getmPosterPath());
            loadThumbnail(BASE_IMAGE_URL + POSTER_FORMAT + mMovie.getmThumbnail());
        }
        // finally if user is is offline, disable button for playing movie trailer
        mBinding.playTrailerIv.setVisibility(!MainActivity.isConnected ? View.INVISIBLE : View.VISIBLE);
    }

    public void loadThumbnail(String path) {
        Picasso.with(this)
                .load(path)
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.error_image)
                .into(mBinding.thumbnail);
    }

    public void loadPoster(final String path) {
        // get bitmap ready for insertion into file system if mMovie is to be saved to database
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    mPosterBitmap = Picasso.with(MovieDetailsActivity.this).load(path).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            // waits for thread to finish so inserting bitmap into
            // filesystem won't throw null pointer exception
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mPosterBitmap != null)
            mBinding.daMoviePoster.setImageBitmap(mPosterBitmap);
    }

    private Uri buildMovieUrlWithId(String id, String path) {
        return Uri.parse(MainActivity.BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(path)
                .appendQueryParameter(MainActivity.LANGUAGE_PARAM, MainActivity.LANGUAGE_VALUE)
                .appendQueryParameter(MainActivity.API_KEY_PARAM, MainActivity.API_KEY)
                .build();
    }

    private Uri buildYoutubeTrailerUrl(String videoId) {
        return Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendPath(PATH_WATCH)
                .appendQueryParameter(VIDEO_PARAM, videoId)
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
}
