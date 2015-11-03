package com.polymorphic_solutions.popularmovies.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cmorgan on 10/30/15.
 */
public class PopularMoviesContract {
    public static final String LOG_TAG = PopularMoviesContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.polymorphic_solutions.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static  final String PATH_MOVIES = "movies";
//    public static final String PATH_MOVIE_GENRE = "movie_genre";
//    public static final String PATH_GENRE = "genre";
//    public static final String PATH_ACTOR = "actor";
//    public static final String PATH_MOVIE_ACTOR = "movie_actor";
//    public static final String PATH_DIRECTOR = "director";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID = "id";
        public static final String MOVIE_BACKDROP_URI = "backdrop_path";
        public static final String MOVIE_TITLE = "original_title";
        public static final String MOVIE_POSTER = "poster_path";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_RELEASE_DATE = "release_date";
        public static final String MOVIE_VOTE_COUNT = "vote_count";
        public static final String MOVIE_REVIEWS = "reviews";
        public static final String MOVIE_TRAILERS = "trailers";

        public static final String[] PROJECTION_ALL = {MOVIE_ID, MOVIE_BACKDROP_URI, MOVIE_TITLE, MOVIE_POSTER, MOVIE_OVERVIEW, MOVIE_VOTE_AVERAGE, MOVIE_RELEASE_DATE, MOVIE_REVIEWS, MOVIE_TRAILERS};

        public static final String SORT_ORDER_DEFAULT = MOVIE_VOTE_AVERAGE + " ASC";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

//    public final static class MovieGenreEntry implements BaseColumns {
//        // Get the standard items defined...
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_GENRE).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_GENRE;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_GENRE;
//
//        // Table Name
//        public static final String TABLE_NAME = "movie_genres_lookup";
//
//        // *** Columns for this table ***
//        public static final String COLUMN_MOVIE_ID = "movie_id";    // This is the ID for the associated Movie
//        public static final String COLUMN_GENRE_ID = "genre_id";    // This is the ID for the associated Genre
//    }
//
//    public final static class GenreEntry implements BaseColumns {
//        // Get the standard items defined...
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRE).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;
//
//        // Table Name
//        public static final String TABLE_NAME = "genres";
//
//        // *** Columns for this table ***
//        public static final String COLUMN_GENRE_NAME = "genre_name";
//
//        public static Uri buildGenreUri(long id) {
//            // This proc takes the ID for a genre
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//    }
//
//    public final static class ActorEntry implements BaseColumns {
//        // Get the standard items defined...
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTOR).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTOR;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTOR;
//
//        // Table Name
//        public static final String TABLE_NAME = "actors";
//
//        // *** Columns for this table ***
//        public static final String COLUMN_ACTOR_ENTRY = "actor_entry";      // This is the path to the Actor on TMDB
//        public static final String COLUMN_NAME_FIRST = "name_first";        // Actor's First Name
//        public static final String COLUMN_NAME_LAST = "name_last";          // Actor's Last Name
//        public static final String COLUMN_NAME_REAL = "name_real";          // Actor's Real Name --> if different than their working name
//        public static final String COLUMN_BIO = "bio";                      // Brief Bio of the Actor
//        public static final String COLUMN_BIRTHDAY = "birthday";            // Actor's Birthday
//
//        public static Uri buildActorUri(long id) {
//            // This proc takes the ID for an Actor
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//    }
//
//    public final static class MovieActorEntry implements BaseColumns {
//        // Get the standard items defined...
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_ACTOR).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_ACTOR;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_ACTOR;
//
//        // Table Name
//        public static final String TABLE_NAME = "movie_actor_lookup";
//
//        // *** Columns for this table ***
//        public static final String COLUMN_MOVIE_ID = "movie_id";
//        public static final String COLUMN_ACTOR_ID = "actor_id";
//    }
//
//    public final static class DirectorEntry implements BaseColumns {
//        // Get the standard items defined...
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIRECTOR).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIRECTOR;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIRECTOR;
//
//        // Table Name
//        public static final String TABLE_NAME = "directors";
//
//        // *** Columns for this table ***
//        public static final String COLUMN_DIRECTOR_ENTRY = "director_entry";// This is the path to the Director on TMDB
//        public static final String COLUMN_NAME_FIRST = "name_first";        // Director's First Name
//        public static final String COLUMN_NAME_LAST = "name_last";          // Director's Last Name
//        public static final String COLUMN_BIO = "bio";                      // Brief Bio of the Director
//        public static final String COLUMN_BIRTHDAY = "birthday";            // Director's Birthdate
//        public static final String COLUMN_KNOWN_CREDITS = "known_credits";  // Total number of credits for this Director
//
//        public static Uri buildDirectorUri(long id) {
//            // This proc takes the ID for a Director
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//    }
}
