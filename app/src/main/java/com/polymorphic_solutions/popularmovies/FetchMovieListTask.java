package com.polymorphic_solutions.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class FetchMovieListTask extends AsyncTask<String, Void, List<Movie>> {
    private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();

    public AsynchReturnCall delegate;
    private final String API_KEY = "GET YOUR OWN";      // You can get this from tmdb.org...
    private final String MOVIE_POSTER_BASE = "http://image.tmdb.org/t/p/";
    private final String MOVIE_POSTER_SIZE = "w185";

    public FetchMovieListTask(AsynchReturnCall delegate){
        this.delegate = delegate;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch so that they get closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {

            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String MIN_VOTES = "vote_count.gte";
            final String SORT_BY = "sort_by";
            final String KEY = "api_key";
            String sortBy = params[0];
            String minVotes = params[1];

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(MIN_VOTES, minVotes)      // I added the Minimum Votes piece because of getting poor-quality results --> one vote wonders break things!
                    .appendQueryParameter(SORT_BY, sortBy)
                    .appendQueryParameter(KEY, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();
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
            return extractData(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> results) {
        if (results != null) {
            // This will execute the callback function on the delegate
            delegate.onTaskCompleted(results);
        }
    }

    private String getYear(String date){
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(df.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Integer.toString(cal.get(Calendar.YEAR));
    }

    private List<Movie> extractData(String moviesJsonStr) throws JSONException {
        final String ARRAY_OF_MOVIES = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String VOTE_COUNT = "vote_count";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(ARRAY_OF_MOVIES);
        int moviesCount =  moviesArray.length();
        List<Movie> movies = new ArrayList<Movie>();

        // Looping through the JSON object to get all of the movies...
        for(int i = 0; i < moviesCount; ++i) {
            JSONObject movie = moviesArray.getJSONObject(i);
            String title = movie.getString(ORIGINAL_TITLE);
            String poster = MOVIE_POSTER_BASE + MOVIE_POSTER_SIZE + movie.getString(POSTER_PATH);
            String overview = movie.getString(OVERVIEW);
            String rating = movie.getString(VOTE_AVERAGE);
            String releaseDate = getYear(movie.getString(RELEASE_DATE));
            String votes = movie.getString(VOTE_COUNT);

            if (overview == null || overview == "" || overview == "null"){
                overview = "No Summary for this movie can be found";
            }

            movies.add(new Movie(title, poster, overview, rating, releaseDate, votes));

        }

        return movies;
    }
}
