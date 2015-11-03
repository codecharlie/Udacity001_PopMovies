package com.polymorphic_solutions.popularmovies;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polymorphic_solutions.popularmovies.db.Movie;
import com.polymorphic_solutions.popularmovies.db.PopularMoviesContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
*
* the main UI of MovieDetailActivity and handles the logic for the view
*
* */

public class MovieDetailFragment extends Fragment {
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;
    private View mRootView;
    private LayoutInflater mLayoutInflater;
    private String videoToShare;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movies_details")) {
            mMovie = (Movie)intent.getParcelableExtra("movies_details");
            DisplayInfo(mRootView);
            parseTrailers();
            parseReviews();
        }else{
            mRootView = inflater.inflate(R.layout.empty_movie_details, container, false);
        }

        return mRootView;
    }

    // This will populate all of the controls with the correct data...
    private void DisplayInfo(View v){
        TextView title = (TextView) v.findViewById(R.id.movie_title_view);
        ImageView poster = (ImageView) v.findViewById(R.id.poster_image_view);
        TextView releaseDate = (TextView) v.findViewById(R.id.release_date);
        TextView ratings = (TextView) v.findViewById(R.id.ratings_view);
        TextView overview = (TextView) v.findViewById(R.id.synopsis_view);
        final ImageButton favs = (ImageButton) v.findViewById(R.id.add_to_favs);

        if (checkFavoritesForMovie()){
            favs.setImageResource(R.mipmap.favorite_added_btn);
        }else{
            favs.setImageResource(R.mipmap.favorite_removed_btn);
        }
        title.setText(mMovie.getTitle());
        Picasso.with(getActivity()).load(mMovie.getPoster()).into(poster);
        releaseDate.setText(mMovie.getReleaseDate());
        ratings.setText(mMovie.getRating() + "/10   ( " + mMovie.getVoteCount() + "-Votes )");
        overview.setText(mMovie.getOverview());

        favs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFav = checkFavoritesForMovie();
                if (isFav){
                    deleteMovieFromFavorites();
                    favs.setImageResource(R.mipmap.favorite_removed_btn);
                }else{
                    addMovieToFavorites();
                    favs.setImageResource(R.mipmap.favorite_added_btn);
                }
            }
        });
    }

    // Gotta extend the option menu to include sharing

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.add(menu.NONE, R.id.action_share_trailer, 10, R.string.action_share_trailer);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        mShareActionProvider = new ShareActionProvider(getActivity());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "You have to watch this awesome trailer!");
        intent.putExtra(Intent.EXTRA_TEXT, "https://youtube.com/" + videoToShare);

        mShareActionProvider.setShareIntent(intent);
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
    }

    private void addMovieToFavorites() {
        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.clear();

        values.put(PopularMoviesContract.MovieEntry.MOVIE_ID, mMovie.getMovie_id());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_BACKDROP_URI, mMovie.getBackdrop());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_TITLE, mMovie.getTitle());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_POSTER, mMovie.getPoster());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_OVERVIEW, mMovie.getOverview());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_VOTE_AVERAGE, mMovie.getRating());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_VOTE_COUNT, mMovie.getVoteCount());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_REVIEWS, mMovie.getReviews());
        values.put(PopularMoviesContract.MovieEntry.MOVIE_TRAILERS, mMovie.getPreviews());

        resolver.insert(uri, values);
    }

    private void deleteMovieFromFavorites() {
        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();

        long noDeleted = resolver.delete(uri, PopularMoviesContract.MovieEntry.MOVIE_ID + " = ? ", new String[]{ Integer.toString(mMovie.getMovie_id()) });
    }

    private boolean checkFavoritesForMovie() {
        Uri uri = PopularMoviesContract.MovieEntry.buildMovieUri(mMovie.getMovie_id());
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = null;

        try {
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor == null) return false;
            if (cursor.moveToFirst()) return true;
        } finally {
            if(cursor != null) cursor.close();
        }

        return false;
    }

    // Parse out the JSON fragment we put into the Review attribute
    private void parseReviews(){
        if(mMovie.getReviews() == null) return;

        final String ARRAY_OF_REVIEW = "results";
        final String AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        try {
            JSONObject reviewsJson = new JSONObject(mMovie.getReviews());
            JSONArray reviewsArray = reviewsJson.getJSONArray(ARRAY_OF_REVIEW);
            int reviewsLength = reviewsArray.length();

            if (reviewsLength > 0){
                LinearLayout innerScrollLayout = (LinearLayout) mRootView.findViewById(R.id.inner_scroll_layout);
                View reviewsListView = mLayoutInflater.inflate(R.layout.review_list, innerScrollLayout, false);
                innerScrollLayout.addView(reviewsListView);
                LinearLayout reviewList = (LinearLayout) reviewsListView.findViewById(R.id.review_list);

                for (int i = 0; i < reviewsLength; ++i) {
                    View reviewItem = mLayoutInflater.inflate(R.layout.review_item, reviewList, false);

                    JSONObject review = reviewsArray.getJSONObject(i);
                    String reviewAuthor = review.getString(AUTHOR);
                    String reviewContent = review.getString(REVIEW_CONTENT);

                    TextView author = (TextView) reviewItem.findViewById(R.id.review_author);
                    TextView content = (TextView) reviewItem.findViewById(R.id.review_content);

                    author.setText(reviewAuthor);
                    content.setText(reviewContent);

                    reviewList.addView(reviewItem);
                }
            }
        }catch (JSONException e){
            Log.e(LOG_TAG, "ERROR PARSING JSON STRING");
        }
    }


    /*
    * used to parse the saved json string of movie trailers for the movie
    * */
    private void parseTrailers(){

        if (mMovie.getPreviews() == null) return;

        final String ARRAY_OF_TRAILERS = "youtube";
        final String TRAILER_ID = "source";
        final String TRAILER_TITLE = "name";

        try{
            JSONObject trailersJson = new JSONObject(mMovie.getPreviews());
            JSONArray trailersArray = trailersJson.getJSONArray(ARRAY_OF_TRAILERS);
            int trailersLength =  trailersArray.length();

            if(trailersLength > 0) {
                LinearLayout innerScrollLayout = (LinearLayout) mRootView.findViewById(R.id.inner_scroll_layout);
                View trailersListView = mLayoutInflater.inflate(R.layout.trailer_list, innerScrollLayout, false);
                innerScrollLayout.addView(trailersListView);
                LinearLayout trailerList = (LinearLayout) trailersListView.findViewById(R.id.trailers_list);

                for (int i = 0; i < trailersLength; ++i) {
                    View trailerItem = mLayoutInflater.inflate(R.layout.trailer_item, trailerList, false);

                    JSONObject trailer = trailersArray.getJSONObject(i);
                    final String trailerId = trailer.getString(TRAILER_ID);
                    String trailerTitle = trailer.getString(TRAILER_TITLE);
                    TextView videoTitle = (TextView) trailerItem.findViewById(R.id.video_title);

                    videoToShare = trailerId;
                    videoTitle.setText(trailerTitle);
                    trailerList.addView(trailerItem);

                    trailerItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerId));
                            intent.putExtra("VIDEO_ID", trailerId);
                            try{
                                startActivity(intent);
                            }catch (ActivityNotFoundException ex){
                                Log.i(LOG_TAG, "youtube app not installed");
                            }
                        }
                    });
                }
            }

        }catch (JSONException e){
            Log.e(LOG_TAG, "ERROR PARSING JSON STRING");
        }
    }
}
