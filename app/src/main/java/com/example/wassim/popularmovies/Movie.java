package com.example.wassim.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String mID;
    private String mThumbnail;
    private String mTitle;
    private String mOverview;
    private String mPosterPath;
    private String mVoteAverage;
    private String mVoteCount;
    private String mPopularity;
    private String mReleaseDate;
    private boolean isFavoriteMovie;

    public Movie(String id, String thumbnail, String title, String overview
            , String posterPath, String voteAverage, String voteCount, String popularity
            , String releaseDate, boolean favorite)
    {
        mID = id;
        mThumbnail = thumbnail;
        mTitle = title;
        mOverview = overview;
        mPosterPath = posterPath;
        mVoteAverage = voteAverage;
        mVoteCount = voteCount;
        mPopularity = popularity;
        mReleaseDate = releaseDate;
        isFavoriteMovie = favorite;
    }

    public String getmThumbnail()
    {
        return mThumbnail;
    }

    public String getmVoteAverage()
    {
        return mVoteAverage;
    }

    public String getmVoteCount()
    {
        return mVoteCount;
    }

    public String getmPopularity()
    {
        return mPopularity;
    }

    public String getmReleaseDate()
    {
        return mReleaseDate;
    }

    public String getmID()
    {
        return mID;
    }

    public String getmTitle()
    {
        return mTitle;
    }

    public String getmPosterPath()
    {
        return mPosterPath;
    }

    public String getmOverview()
    {
        return mOverview;
    }

    public boolean getIsFavoriteMovie()
    {
        return isFavoriteMovie;
    }

    public void setFavoriteMovie(boolean favoriteMovie) {isFavoriteMovie = favoriteMovie;}


    // parceling part
    public Movie(Parcel parcel)
    {
        mID = parcel.readString();
        mThumbnail = parcel.readString();
        mTitle = parcel.readString();
        mOverview = parcel.readString();
        mPosterPath = parcel.readString();
        mVoteAverage = parcel.readString();
        mVoteCount = parcel.readString();
        mPopularity = parcel.readString();
        mReleaseDate = parcel.readString();
        isFavoriteMovie = (parcel.readByte() != 0);
    }

    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mID);
        dest.writeString(mThumbnail);
        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeString(mPosterPath);
        dest.writeString(mVoteAverage);
        dest.writeString(mVoteCount);
        dest.writeString(mPopularity);
        dest.writeString(mReleaseDate);
        dest.writeByte((byte)(isFavoriteMovie ? 1 : 0));
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel parcel)
        {
            return new Movie(parcel);
        }

        public Movie[] newArray(int size)
        {
            return new Movie[size];
        }
    };

    public int describeContents()
    {
        return 0;
    }
}
