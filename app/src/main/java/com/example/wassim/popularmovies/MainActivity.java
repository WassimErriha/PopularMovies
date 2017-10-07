package com.example.wassim.popularmovies;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.wassim.popularmovies.data.MovieContract.MovieEntry;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks, MovieAdapter.onPosterClickListener {

    private static final int MOVIE_ARRAY_LIST_LOADER_ID = 1;
    private static final int CURSOR_LOADER_ID = 2;
    public static String BASE_URL = "https://api.themoviedb.org/3/movie";
    public static String API_KEY_PARAM = "api_key";
    public static String API_KEY = "<>API_KEY";  //TODO insert api key here
    public static String LANGUAGE_PARAM = "language";
    public static String LANGUAGE_VALUE = "en";
    public static ArrayList<String> favoriteMoviesArrayList;
    private static String POPULAR_MOVIES_PATH = "popular";
    private static String TOP_RATED_MOVIES_PATH = "top_rated";
    private static URL url;
    private static String FIRST_LOAD_PATH = POPULAR_MOVIES_PATH;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private ImageView noInternetConnection;
    private int mLoaderID = MOVIE_ARRAY_LIST_LOADER_ID;
    private String activityTitle = "Popular Movies";
    private String mMoviesPath = FIRST_LOAD_PATH;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            mLoaderID = savedInstanceState.getInt("loaderID");
            activityTitle = savedInstanceState.getString("activityTitle");
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noInternetConnection = (ImageView) findViewById(R.id.no_internet_connection);
        setTitle(activityTitle);
        recyclerView.setLayoutManager(new GridLayoutManager(this, Utility.calculateNoOfColumns(this)));
        recyclerView.setHasFixedSize(true);
        url = Utility.buildUrlWithPath(mMoviesPath);
        showProgressBar();
        boolean isConnected = Utility.isNetworkAvailable(this);
        if (isConnected) {
            getLoaderManager().initLoader(mLoaderID
                    , null
                    , this);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            showNoInternetConnectionView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("loaderID", mLoaderID);
        outState.putString("activityTitle", this.getTitle().toString());
    }

    protected void onPostResume() {
        //Update favoriteMoviesArrayList after navigating back from MovieDetailsActivity
        favoriteMoviesArrayList = new ArrayList<>();
        String[] favoriteMovieIdsProjection = {MovieEntry.COLUMN_MOVIE_ID};
        Cursor fmCursor = getContentResolver().query(MovieEntry.CONTENT_URI
                , favoriteMovieIdsProjection
                , null
                , null
                , null);
        try {
            while (fmCursor.moveToNext()) {
                String movieId = fmCursor.getString(fmCursor.getColumnIndex(
                        MovieEntry.COLUMN_MOVIE_ID));
                favoriteMoviesArrayList.add(movieId);
            }
        } finally {
            fmCursor.close();
        }
        super.onPostResume();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noInternetConnection.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        noInternetConnection.setVisibility(View.GONE);
    }

    private void showNoInternetConnectionView() {
        progressBar.setVisibility(View.GONE);
        noInternetConnection.setVisibility(View.VISIBLE);
    }

    public Loader onCreateLoader(int loaderId, Bundle args) {
        if (loaderId == MOVIE_ARRAY_LIST_LOADER_ID)
            return new MovieLoader(MainActivity.this, url.toString());
        else //loaderId == CURSOR_LOADER_ID
            return new FavoriteMoviesCursorLoader(MainActivity.this, MovieEntry.CONTENT_URI);
    }

    public void onLoadFinished(Loader loader, Object data) {
        if ((data != null) && (loader.getId() == MOVIE_ARRAY_LIST_LOADER_ID)) {
            mLoaderID = MOVIE_ARRAY_LIST_LOADER_ID;
            movieAdapter = new MovieAdapter(this, (ArrayList) data, this);
            recyclerView.setAdapter(movieAdapter);
            showRecyclerView();
        } else if ((data != null) && (loader.getId() == CURSOR_LOADER_ID)) {
            mLoaderID = CURSOR_LOADER_ID;
            movieAdapter = new MovieAdapter(this, (Cursor) data, this);
            recyclerView.setAdapter(movieAdapter);
            showRecyclerView();
        } else {

            showNoInternetConnectionView();
        }
    }

    public void onLoaderReset(Loader loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_popular_movies:
                url = Utility.buildUrlWithPath(POPULAR_MOVIES_PATH);
                setTitle(R.string.popular_movies_title);
                showProgressBar();
                getLoaderManager().restartLoader(MOVIE_ARRAY_LIST_LOADER_ID
                        , null,
                        this);
                break;
            case R.id.action_top_rated_movies:
                url = Utility.buildUrlWithPath(TOP_RATED_MOVIES_PATH);
                setTitle(R.string.top_rated_movies_title);
                showProgressBar();
                getLoaderManager().restartLoader(MOVIE_ARRAY_LIST_LOADER_ID
                        , null
                        , this);
                break;
            case R.id.action_favorite_movies:
                setTitle(R.string.favorite_movies_title);
                getLoaderManager().destroyLoader(MOVIE_ARRAY_LIST_LOADER_ID); // loader initialized in onCreate
                getLoaderManager().restartLoader(CURSOR_LOADER_ID
                        , null
                        , this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPosterClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }
}
