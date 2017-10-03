package com.example.wassim.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.ArrayList;

/**
 * This class take care of fetching movie data from the server on a background thread
 */

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private String mUrl;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        if (mUrl == null) return null;

        return QueryUtils.fetchMovies(mUrl);
    }


}
