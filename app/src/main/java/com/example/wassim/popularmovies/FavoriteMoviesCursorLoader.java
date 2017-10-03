package com.example.wassim.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class FavoriteMoviesCursorLoader
        extends AsyncTaskLoader<Cursor> {
    Uri mUri;

    public FavoriteMoviesCursorLoader(Context context, Uri uri) {
        super(context);
        mUri = uri;
    }

    protected void onStartLoading() {
        forceLoad();
    }

    public Cursor loadInBackground() {
        if (mUri == null) {
            return null;
        }
        return getContext().getContentResolver().query(mUri
                , null
                , null
                , null
                , null);
    }
}
