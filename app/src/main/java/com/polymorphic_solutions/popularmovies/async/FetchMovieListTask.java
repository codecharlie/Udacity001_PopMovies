package com.polymorphic_solutions.popularmovies.async;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.polymorphic_solutions.popularmovies.R;
import com.polymorphic_solutions.popularmovies.utils.Utility;
import com.polymorphic_solutions.popularmovies.adapters.ImageAdapter;
import com.polymorphic_solutions.popularmovies.db.Movie;
import com.polymorphic_solutions.popularmovies.db.PopularMoviesContract;

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
import java.util.Locale;


public class FetchMovieListTask extends AsyncTask<String, Void, ArrayList<Movie>> {
    private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();

    private final String MOVIE_POSTER_BASE = "http://image.tmdb.org/t/p/";
    private final String MOVIE_POSTER_SIZE = "w185";
    private final Context mContext;
    private final String mApiKey;
    private ArrayList<Movie> mMovies;
    private ImageAdapter mPosterAdapter;
    private String mSortBy;
    private String mMinVotes;

    public FetchMovieListTask(Context context, ArrayList<Movie> movies, ImageAdapter posterAdapter) {
        this.mContext = context;
        this.mMovies = movies;
        this.mPosterAdapter = posterAdapter;

        this.mApiKey = context.getString(R.string.my_api_key);
        this.mSortBy = Utility.getPreferedSorting(context);
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        mSortBy = Utility.getPreferedSorting(mContext);
        mMinVotes = Utility.getPreferedMinVotes(mContext);

        if (mSortBy.equals("favorites")) {
            getFavorites();
            return mMovies;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {

            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String MIN_VOTES = "vote_count.gte";
            final String SORT_BY = "sort_by";
            final String KEY = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(MIN_VOTES, mMinVotes)      // I added the Minimum Votes piece because of getting poor-quality results --> one vote wonders break things!
                    .appendQueryParameter(SORT_BY, mSortBy)
                    .appendQueryParameter(KEY, mApiKey)
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

            if (buffer.length() == 0) return null;

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
    protected void onPostExecute(ArrayList<Movie> results) {
        // Finally got rid of that AsyncCallBack Class!!
        if (results != null && mPosterAdapter != null) {
            mPosterAdapter.clear();
            for(Movie movie : results) {
                // Using this to trigger the overall update of the UI
                mPosterAdapter.add(movie.getPoster());
            }
        }
    }

    private String getYear(String date) {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(df.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Integer.toString(cal.get(Calendar.YEAR));
    }

    private ArrayList<Movie> extractData(String moviesJsonStr) throws JSONException {
        final String ARRAY_OF_MOVIES = "results";
        final String ORIGINAL_ID = "id";
        final String BACKDROP_PATH = "backdrop_path";
        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String VOTE_COUNT = "vote_count";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(ARRAY_OF_MOVIES);
        int moviesCount = moviesArray.length();
        mMovies.clear();

        // Looping through the JSON object to get all of the Movies from the returned data...
        for (int i = 0; i < moviesCount; ++i) {
            JSONObject movie = moviesArray.getJSONObject(i);
            int movie_id = movie.getInt(ORIGINAL_ID);
            String backdrop = MOVIE_POSTER_BASE + MOVIE_POSTER_SIZE + movie.getString(BACKDROP_PATH);
            String title = movie.getString(ORIGINAL_TITLE);
            String poster = MOVIE_POSTER_BASE + MOVIE_POSTER_SIZE + movie.getString(POSTER_PATH);
            String overview = movie.getString(OVERVIEW);
            String rating = movie.getString(VOTE_AVERAGE);
            String releaseDate = getYear(movie.getString(RELEASE_DATE));
            String votes = movie.getString(VOTE_COUNT);

            if (overview == null || overview == "" || overview == "null") {
                overview = "No Summary for this movie can be found";    // Surprisingly enough, there are a few that get caught by this...
            }

            Movie tmpMovie = new Movie(movie_id, backdrop, title, poster, overview, rating, releaseDate, votes);

            // I think I will combine these into a single AsyncTask in the future, might actually
            // be able to get them both with a single call --> have to play with the API some more first...
            FetchMovieDetails movieDetails = new FetchMovieDetails(tmpMovie, mContext);
            movieDetails.execute();

            mMovies.add(tmpMovie);

        }

        return mMovies;
    }

    private void getFavorites() {
        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = null;

        try {
            cursor = resolver.query(uri, null, null, null, null);

            // Clear the ArrayList and repopulate it with Favs
            mMovies.clear();

            if (cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));

                    movie.setReviews(cursor.getString(9));
                    movie.setPreviews(cursor.getString(10));
                    mMovies.add(movie);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }

    }
}