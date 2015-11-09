package com.polymorphic_solutions.popularmovies.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.polymorphic_solutions.popularmovies.R;
import com.polymorphic_solutions.popularmovies.db.Movie;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cmorgan on 11/2/15.
 */
public class FetchMovieDetails extends AsyncTask<String, Void, String> {
    private static final String LOG_TAG = FetchMovieDetails.class.getSimpleName();
    private static final String MOVIE_BASE = "http://api.themoviedb.org/3/movie/";
    private String mApiKey;
    private Movie mMovie;

    public FetchMovieDetails(Movie movie, Context context){
        this.mMovie = movie;
        this.mApiKey = context.getString(R.string.my_api_key);
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String strData = null;

        final String KEY = "api_key";
        final String APPEND = "append_to_response";

        try {
            Uri builtUri = Uri.parse(MOVIE_BASE + mMovie.getMovie_id()).buildUpon()
                    .appendQueryParameter(KEY, mApiKey)
                    .appendQueryParameter(APPEND, "trailers,reviews")
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) return null;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) return null;

            strData = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return extractData(strData);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String rslt) {
        //Log.d(LOG_TAG, "Result: " + rslt);
    }

    private String extractData(String jsonRawData) throws JSONException {
        final String ARRAY_OF_TRAILERS = "trailers";
        final String ARRAY_OF_REVIEWS = "reviews";

        JSONObject jsonData = new JSONObject(jsonRawData);
        JSONObject trailers = jsonData.getJSONObject(ARRAY_OF_TRAILERS);
        JSONObject reviews = jsonData.getJSONObject(ARRAY_OF_REVIEWS);

        // I'm setting the attributes to the un-parsed string values for now,
        // later I will make a few tables to hold the Reviews and Trailers and
        // link everything together --> Then the Movie object will hold arrays
        // of Review and Trailer objects...
        mMovie.setReviews(reviews.toString());
        mMovie.setPreviews(trailers.toString());

        // This is only getting returned because we need to return something...
        // It does have some debugging value
        return jsonData.toString();
    }
}

