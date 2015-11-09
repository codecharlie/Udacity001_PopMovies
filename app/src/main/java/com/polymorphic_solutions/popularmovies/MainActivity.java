package com.polymorphic_solutions.popularmovies;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.polymorphic_solutions.popularmovies.db.Movie;
import com.polymorphic_solutions.popularmovies.utils.Utility;

/*
*
* Main activity and entry point into the app --> Inflates MainActivityFragment for the main UI which
* gives us the list/flow
*
* */

public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DETAILFRAG";
    private FragmentManager mFragmentManager = getFragmentManager();
    private boolean mIsDualPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Now we are going to check if we have a wide-display/tablet
        // I need to structure this in a more elegant fashion...
        if(findViewById(R.id.movie_detail_container) != null){
            mIsDualPane = true;
            if(savedInstanceState == null){
                mFragmentManager.beginTransaction()
                        .add(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }else{
            mIsDualPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadSelectedMovie(Movie movie) {
        if (mIsDualPane) {
            Bundle args = new Bundle();

            // Doing this way because I am wanting to communicate with an existing fragment...
            // seems a bit like a hack, but I'm using it until I find a more elegant solution
            args.putParcelable(Utility.MOV_DETAILS, movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            mFragmentManager.beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class).putExtra(Utility.MOV_DETAILS, movie);
            startActivity(intent);
        }
    }
}
