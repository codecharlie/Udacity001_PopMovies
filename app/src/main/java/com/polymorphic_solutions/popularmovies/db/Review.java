package com.polymorphic_solutions.popularmovies.db;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by cmorgan on 11/2/15.
 */
public class Review {
    private static final String LOG_TAG = Review.class.getSimpleName();

    private String author;
    private String content;
    private String rating;
    private String review_date;

    public Review(String author, String content){
        this.author = author;
        this.content = content;;
    }

    public Review(String author, String content, String rating, String review_date) {
        this.author = author;
        this.content = content;
        this.rating = rating;
        this.review_date = review_date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }

    public String getHash() {
        // This is so we can do a quick compare to de-dupe reviews...
        MessageDigest digest=null;
        String msg = this.author.concat(this.content);

        try{
            digest = MessageDigest.getInstance("SHA-256");
        }catch (NoSuchAlgorithmException e1){
            Log.e(LOG_TAG, "SHA-256 Hashing unavailable on this device");
            return null;
        }

        digest.reset();
        return digest.digest(msg.getBytes()).toString();
    }
}
