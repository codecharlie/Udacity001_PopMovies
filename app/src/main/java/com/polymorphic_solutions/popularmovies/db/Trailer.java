package com.polymorphic_solutions.popularmovies.db;

/**
 * Created by cmorgan on 11/2/15.
 */
public class Trailer {
    private static final String LOG_TAG = Trailer.class.getSimpleName();

    private String trailerKey;
    private String trailerTitle;

    public Trailer(String trailerKey, String trailerTitle) {
        this.trailerKey = trailerKey;
        this.trailerTitle = trailerTitle;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getTrailerTitle() {
        return trailerTitle;
    }

    public void setTrailerTitle(String trailerTitle) {
        this.trailerTitle = trailerTitle;
    }
}
