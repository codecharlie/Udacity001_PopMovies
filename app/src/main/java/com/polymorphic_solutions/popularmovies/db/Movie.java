package com.polymorphic_solutions.popularmovies.db;

import android.os.Parcel;
import android.os.Parcelable;

/*
*
* An object to store all the information for a movie
*
* Implements Parcelable so that it can be passed between intents more easily
*
* */

public class Movie implements Parcelable {
    private static final String LOG_TAG = Movie.class.getSimpleName();

    private int movie_id;
    private String backdrop;
    private String title;
    private String poster;
    private String overview;
    private String rating;
    private String releaseDate;
    private String voteCount;
    private String reviews;
    private String previews;

    public Movie(int movie_id, String backdrop, String title, String poster, String overview, String rating, String releaseDate, String votes){
        this.movie_id = movie_id;
        this.backdrop = backdrop;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.voteCount = votes;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getPreviews() {
        return previews;
    }

    public void setPreviews(String previews) {
        this.previews = previews;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(movie_id);
        out.writeString(backdrop);
        out.writeString(title);
        out.writeString(poster);
        out.writeString(overview);
        out.writeString(rating);
        out.writeString(releaseDate);
        out.writeString(voteCount);
        out.writeString(previews);
        out.writeString(reviews);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Movie(Parcel in) {
        movie_id = in.readInt();
        backdrop = in.readString();
        title = in.readString();
        poster = in.readString();
        overview = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        voteCount = in.readString();
        previews = in.readString();
        reviews = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}