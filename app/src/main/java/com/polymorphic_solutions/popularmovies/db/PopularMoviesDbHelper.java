package com.polymorphic_solutions.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.polymorphic_solutions.popularmovies.db.PopularMoviesContract.MovieEntry;
/**
 * Created by cmorgan on 10/30/15.
 */
public class PopularMoviesDbHelper extends SQLiteOpenHelper{
    private static final String LOG_TAG = PopularMoviesDbHelper.class.getSimpleName();

    private  static  final int DATABASE_VERSION = 3;    // this moved to Rev-3 to fix an issue with nulls for reviews and trailers...
    static final String DATABASE_NAME = "popularMovies.db";

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.MOVIE_ID + " TEXT UNIQUE NOT NULL," +
                MovieEntry.MOVIE_BACKDROP_URI + " TEXT NOT NULL," +
                MovieEntry.MOVIE_TITLE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_POSTER + " TEXT NOT NULL," +
                MovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.MOVIE_VOTE_AVERAGE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_REVIEWS + " TEXT," +
                MovieEntry.MOVIE_VOTE_COUNT + " TEXT NOT NULL," +
                MovieEntry.MOVIE_TRAILERS + " TEXT," +
                "UNIQUE (" + MovieEntry.MOVIE_ID +") ON CONFLICT IGNORE"+
                " );";

//        final String SQL_CREATE_ACTORS_TABLE = "CREATE TABLE " + ActorEntry.TABLE_NAME + " (" +
//                ActorEntry._ID + " INTEGER PRIMARY KEY, " +
//                ActorEntry.COLUMN_ACTOR_ENTRY + " TEXT NOT NULL, " +
//                ActorEntry.COLUMN_NAME_FIRST + " TEXT NOT NULL, " +
//                ActorEntry.COLUMN_NAME_LAST + " TEXT NOT NULL, " +
//                ActorEntry.COLUMN_NAME_REAL + " TEXT, " +
//                ActorEntry.COLUMN_BIO + " TEXT NOT NULL, " +
//                ActorEntry.COLUMN_BIRTHDAY + " TEXT NOT NULL " +
//                "UNIQUE (" + ActorEntry.COLUMN_ACTOR_ENTRY + ") ON CONFLICT REPLACE " +
//                ");";
//
//        final String SQL_CREATE_GENRES_TABLE = "CREATE TABLE " + GenreEntry.TABLE_NAME + " (" +
//                GenreEntry._ID + " INTEGER PRIMARY KEY, " +
//                GenreEntry.COLUMN_GENRE_NAME + " TEXT NOT NULL " +
//                "UNIQUE (" + GenreEntry.COLUMN_GENRE_NAME + ") ON CONFLICT REPLACE " +
//                ");";
//
//        final String SQL_CREATE_DIRECTORS_TABLE = "CREATE TABLE " + DirectorEntry.TABLE_NAME + " (" +
//                DirectorEntry._ID + "INTEGER PRIMARY KEY, " +
//                DirectorEntry.COLUMN_DIRECTOR_ENTRY + " TEXT NOT NULL, " +
//                DirectorEntry.COLUMN_NAME_FIRST + " TEXT NOT NULL, " +
//                DirectorEntry.COLUMN_NAME_LAST + " TEXT NOT NULL, " +
//                DirectorEntry.COLUMN_BIO + " TEXT NOT NULL, " +
//                DirectorEntry.COLUMN_BIRTHDAY + " TEXT NOT NULL, " +
//                DirectorEntry.COLUMN_KNOWN_CREDITS + " INTEGER NOT NULL " +
//                "UNIQUE (" + DirectorEntry.COLUMN_DIRECTOR_ENTRY + ") ON CONFLICT REPLACE " +
//                ");";
//
//        final String SQL_CREATE_MOVIE_ACTOR_TABLE = "CREATE TABLE " + MovieActorEntry.TABLE_NAME + " (" +
//                MovieActorEntry._ID + " INTEGER PRIMARY KEY, " +
//                MovieActorEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
//                MovieActorEntry.COLUMN_ACTOR_ID + " INTEGER NOT NULL, " +
//                "FOREIGN KEY (" + MovieActorEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "), " +
//                "FOREIGN KEY (" + MovieActorEntry.COLUMN_ACTOR_ID + ") REFERENCES " + ActorEntry.TABLE_NAME + " (" + ActorEntry._ID + ") " +
//                "UNIQUE (" + MovieActorEntry.COLUMN_MOVIE_ID + ", " + MovieActorEntry.COLUMN_ACTOR_ID + ") ON CONFLICT REPLACE " +
//                ");";
//
//        final String SQL_CREATE_MOVIE_GENRE_TABLE = "CREATE TABLE " + MovieGenreEntry.TABLE_NAME + " (" +
//                MovieGenreEntry._ID + " INTEGER PRIMARY KEY, " +
//                MovieGenreEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
//                MovieGenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
//                "FOREIGN KEY (" + MovieGenreEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "), " +
//                "FOREIGN KEY (" + MovieGenreEntry.COLUMN_GENRE_ID + ") REFERENCES " + GenreEntry.TABLE_NAME + " (" + GenreEntry._ID + ") " +
//                "UNIQUE (" + MovieGenreEntry.COLUMN_MOVIE_ID + ", " + MovieGenreEntry.COLUMN_GENRE_ID + ") ON CONFLICT REPLACE " +
//                ");";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
//        db.execSQL(SQL_CREATE_ACTORS_TABLE);
//        db.execSQL(SQL_CREATE_GENRES_TABLE);
//        db.execSQL(SQL_CREATE_DIRECTORS_TABLE);
//        db.execSQL(SQL_CREATE_MOVIE_ACTOR_TABLE);
//        db.execSQL(SQL_CREATE_MOVIE_GENRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since this table is only being used for caching --> Later we might want to make this more
        // upgrade-friendly, like when I finish implementing the genre, actors, and directors...
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + ActorEntry.TABLE_NAME + ";");
//        db.execSQL("DROP TABLE IF EXISTS " + GenreEntry.TABLE_NAME + ";");
//        db.execSQL("DROP TABLE IF EXISTS " + DirectorEntry.TABLE_NAME + ";");
//        db.execSQL("DROP TABLE IF EXISTS " + MovieActorEntry.TABLE_NAME + ";");
//        db.execSQL("DROP TABLE IF EXISTS " + MovieGenreEntry.TABLE_NAME + ";");


        // Finally, recreate all of the tables
        onCreate(db);
    }
}
