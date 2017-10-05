package com.example.wassim.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.wassim.popularmovies.MainActivity.API_KEY;
import static com.example.wassim.popularmovies.MainActivity.API_KEY_PARAM;
import static com.example.wassim.popularmovies.MainActivity.BASE_URL;
import static com.example.wassim.popularmovies.MainActivity.LANGUAGE_PARAM;
import static com.example.wassim.popularmovies.MainActivity.LANGUAGE_VALUE;

public class Utility {

    /**
     * Returns true if network is available or about to become available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * helper method for building Url
     *
     * @param path popularMovies or topRatedMovies endpoints
     * @return query url.
     */
    public static URL buildUrlWithPath(String path) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Helper Method to change recyclerView number of columns dynamically.
     *
     * @return number of columns
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }
}
