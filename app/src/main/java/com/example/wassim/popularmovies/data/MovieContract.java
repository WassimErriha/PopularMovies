package com.example.wassim.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.wassim.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://com.example.wassim.popularmovies");
    public static final String PATH_FAVORITES = "favorites";

    public static final class MovieEntry
            implements BaseColumns {
        public static final Uri CONTENT_URI = MovieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath("favorites")
                .build();
        public static final String TABLE_NAME = "favorites";
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_THUMBNAIL_PATH = "thumbnail_path";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_IS_FAVORITE_MOVIE = "is_favorite_movie";
    }
}
