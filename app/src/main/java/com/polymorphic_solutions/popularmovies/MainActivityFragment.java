package com.polymorphic_solutions.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing the actual grid listing of the movies
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private final String SAVED_MOVIES = "saved_movies";
    private SharedPreferences mPrefs;
    private ImageAdapter mMoviePosterAdapter;
    String sortOrder;
    List<Movie> movies = new ArrayList<Movie>();

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortOrder = mPrefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_order_default_value));

        if(savedInstanceState != null){
            ArrayList<Movie> savedMovies = new ArrayList<Movie>();
            savedMovies = savedInstanceState.<Movie>getParcelableArrayList(SAVED_MOVIES);
            movies.clear();
            movies.addAll(savedMovies);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMoviePosterAdapter = new ImageAdapter(
                getActivity(),
                R.layout.movie_poster,
                R.id.movie_poster_imageview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.main_movie_grid);
        gridView.setAdapter(mMoviePosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie details = movies.get(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("movies_details", details);
                startActivity(intent);
            }

        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // get sort order
        String prefSortOrder = mPrefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_order_default_value));

        if(movies.size() > 0 && prefSortOrder.equals(sortOrder)) {
            updatePosterAdapter();
        }else{
            sortOrder = prefSortOrder;
            getMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> storedMovies = new ArrayList<Movie>();
        storedMovies.addAll(movies);
        outState.putParcelableArrayList(SAVED_MOVIES, storedMovies);
    }

    // This pulls the List of Movies based on the order selected in Prefs
    private void getMovies() {
        FetchMovieListTask fetchMovieList = new FetchMovieListTask(new AsynchReturnCall() {
            @Override
            public void onTaskCompleted(List<Movie> results) {
                movies.clear();
                movies.addAll(results);
                updatePosterAdapter();
            }
        });
        fetchMovieList.execute(sortOrder);
    }

    // Need to provide a way to update the Movie Posters
    private void updatePosterAdapter() {
        mMoviePosterAdapter.clear();
        for(Movie movie : movies) {
            mMoviePosterAdapter.add(movie.getPoster());
        }
    }
}
