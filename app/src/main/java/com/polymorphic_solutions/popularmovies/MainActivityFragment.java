package com.polymorphic_solutions.popularmovies;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.polymorphic_solutions.popularmovies.adapters.ImageAdapter;
import com.polymorphic_solutions.popularmovies.async.FetchMovieListTask;
import com.polymorphic_solutions.popularmovies.db.Movie;
import com.polymorphic_solutions.popularmovies.utils.Utility;

import java.util.ArrayList;

/**
 * A placeholder fragment containing the actual grid listing of the movies
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final String SAVED_MOVIES = "saved_movies";
    private SharedPreferences mPrefs;
    private ImageAdapter mPosterAdapter;
    private String mSortBy;
    private String mMinVotes;
    private ArrayList<Movie> mMovies = new ArrayList<Movie>();

    // Using this as a way to talk back to the other activity...
    public interface Callback {
        public void loadSelectedMovie(Movie movie);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        mSortBy = Utility.getPreferedSorting(getActivity());
        mMinVotes = Utility.getPreferedMinVotes(getActivity()); // I added the Minimum Votes piece because of getting poor-quality results --> one vote wonders break things!

        if(savedInstanceState != null){
            if(savedInstanceState.<Movie>getParcelableArrayList(SAVED_MOVIES) != null) {
                mMovies.clear();
                mMovies.addAll(savedInstanceState.<Movie>getParcelableArrayList(SAVED_MOVIES));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForPreferenceChanges();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPosterAdapter = new ImageAdapter(getActivity(), R.layout.movie_poster, R.id.movie_poster_imageview, new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.main_movie_grid);
        gridView.setAdapter(mPosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie details = mMovies.get(position);

                // Trying this instead of an Intent call..
                ((Callback) getActivity()).loadSelectedMovie(details);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkForPreferenceChanges();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_MOVIES, mMovies);
    }

    // This pulls the List of Movies based on the order selected in Prefs
    private void getMovies() {
        FetchMovieListTask fetchMovieList = new FetchMovieListTask(getActivity().getBaseContext(), mMovies, mPosterAdapter);
        fetchMovieList.execute();
    }

    // Need to provide a way to update the Movie Posters
    private void updatePosters() {
        mPosterAdapter.clear();
        for(Movie movie : mMovies) {
            mPosterAdapter.add(movie.getPoster());
        }
    }

    // Need a simple way of checking if we need to re-query the data
    private void checkForPreferenceChanges(){
        String prefSortOrder = Utility.getPreferedSorting(getActivity());
        String prefMinVotes = Utility.getPreferedMinVotes(getActivity());

        if(mMovies.size() == 0 || !prefSortOrder.equals(mSortBy) || !prefMinVotes.equals(mMinVotes) || prefSortOrder.equals("favorites")) {
            mSortBy = prefSortOrder;
            mMinVotes = prefMinVotes;
            getMovies();
        } else {
            updatePosters();
        }
    }
}
