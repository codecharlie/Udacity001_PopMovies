package com.polymorphic_solutions.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This will take care of all encompass all of the common functions
 */
public class Utility {
    public static String getPreferedSorting(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_order_default_value));
    }

    public static String getPreferedMinVotes(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_votes_key), context.getString(R.string.pref_votes_default_value));
    }
}
