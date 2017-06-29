package com.probypro.mario99ukdw.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mario99ukdw on 25.06.2017.
 */

public class Movie implements Parcelable {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    public static String INTENT_PARCEL_NAME = "MOVIE_PARCEL";

    String title;
    String posterPath; // Full URL
    String overview;
    double voteAverage;
    String releaseDate;

    public Movie(String vTitle, String vPosterPath, String vOverview, double vVoteAverage, String vReleaseDate)
    {
        this.title = vTitle;
        this.posterPath = vPosterPath;
        this.overview = vOverview;
        this.voteAverage = vVoteAverage;
        this.releaseDate = vReleaseDate;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    /**
     * Storing the movie data to Parcel object
     **/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
    }

    /**
     * Retrieving Movie data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private Movie(Parcel in){
        this.title = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
        this.releaseDate = in.readString();
    }
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getDateFormat() {
        return DATE_FORMAT;
    }

    public String getTitle() {
        return this.title;
    }
    public String getPosterPath() {
        return this.posterPath;
    }
    public String getOverview() {
        return this.overview;
    }
    public String getFormatedVoteAverage() {
        return String.valueOf(this.voteAverage);
    }
    public String getFormatedReleaseDate() {
        return this.releaseDate;
    }
}
