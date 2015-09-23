package com.polymorphic_solutions.popularmovies;

import java.util.List;

// Doing this so I have an interface to implement later as a CallBack after the AsyncTask returns
//      --> I am sure there is a prettier way to do this...

public interface AsynchReturnCall {
    void onTaskCompleted( List<Movie> results );
}
