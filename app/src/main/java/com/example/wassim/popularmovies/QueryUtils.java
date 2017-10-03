package com.example.wassim.popularmovies;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QueryUtils {
    public static ArrayList<Movie> fetchMovies(String url) {
        ArrayList<Movie> movies = null;
        String httpResponse = null;
        try {
            httpResponse = getResponseFromHttpUrl(buildUrl(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (httpResponse != null) {
            movies = extractMovies(httpResponse);
        }
        return movies;
    }

    public static URL buildUrl(String string) {
        Uri builtURL = Uri.parse(string).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtURL.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url)
            throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String str;
            if (hasInput) {
                return scanner.next();
            }
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static ArrayList<Movie> extractMovies(String jsonResponse) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                String movieID = movie.getString("id");
                String thumbnail = movie.getString("backdrop_path");
                String title = movie.getString("title");
                String overview = movie.getString("overview");
                String posterPath = movie.getString("poster_path");
                String voteAverage = movie.getString("vote_average");
                String voteCount = movie.getString("vote_count");
                String popularity = movie.getString("popularity");
                String releaseDate = movie.getString("release_date");
                boolean isFavoriteMovie = isMovieInFavoritesArray(movieID, MainActivity.favoriteMoviesArrayList);

                movies.add(new Movie(movieID, thumbnail, title, overview, posterPath
                        , voteAverage, voteCount, popularity, releaseDate
                        , isFavoriteMovie));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private static boolean isMovieInFavoritesArray(String movieID, ArrayList<String> favoriteMoviesArrayList) {
        if ((favoriteMoviesArrayList != null) &&
                (favoriteMoviesArrayList.size() != 0) &&
                (favoriteMoviesArrayList.contains(movieID))) {
            return true;
        }
        return false;
    }

    public static String extractMovieTrailerId(String jsonResponse) {
        String movieId = null;
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray("results");
            JSONObject movie = results.getJSONObject(0);
            movieId = movie.getString("key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movieId;
    }

    public static String fetchTrailerId(String url) {
        String httpResponse = null;
        String trailerId = null;
        try {
            httpResponse = getResponseFromHttpUrl(buildUrl(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (httpResponse != null) {
            trailerId = extractMovieTrailerId(httpResponse);
        }
        return trailerId;
    }

    public static ArrayList<Review> fetchMovieReviews(String url) {
        String httpResponse = null;
        ArrayList<Review> movieReviews = null;
        try {
            httpResponse = getResponseFromHttpUrl(buildUrl(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (httpResponse != null) {
            movieReviews = extractReviews(httpResponse);
        }
        return movieReviews;
    }

    public static ArrayList<Review> extractReviews(String jsonResponse) {
        ArrayList<Review> fakeDataArrayList = new ArrayList();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray("results");
            if (results.length() == 0) {
                return null;
            }
            for (int i = 0; i < results.length(); i++) {
                JSONObject review = results.getJSONObject(i);
                String author = review.getString("author");
                String content = review.getString("content");
                fakeDataArrayList.add(new Review(author, content));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fakeDataArrayList;
    }
}
