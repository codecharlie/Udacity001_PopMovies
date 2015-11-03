package com.polymorphic_solutions.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by cmorgan on 11/2/15.
 */
public class PopularMoviesProvider extends ContentProvider{
    private static final String LOG_TAG = PopularMoviesProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopularMoviesDbHelper mOpenHelper;

    private static final int MOVIES = 100;
    private static final int MOVIES_ID = 101;
//    private static final int MOVIES_TITLE = 102;
//    private static final int MOVIES_GENRE = 103;
//    private static final int MOVIES_ACTOR = 104;
//    private static final int MOVIES_DIRECTOR = 105;
//    private static final int MOVIES_RELEASE_YEAR = 106;
//    private static final int MOVIES_DIRECTOR_AND_NOTED = 107;
//    private static final int MOVIES_POPULAR = 108;
//
//    private static final int ACTOR = 200;
//    private static final int ACTOR_ID = 201;
//    private static final int ACTOR_LAST_NAME = 202;
//    private static final int ACTOR_FIRST_NAME = 203;
//    private static final int ACTOR_FULL_NAME = 204;
//    private static final int ACTOR_BIRTHDAY = 205;
//    private static final int ACTOR_REAL_NAME = 206;
//
//    private static final int DIRECTOR = 300;
//    private static final int DIRECTOR_ID = 301;
//    private static final int DIRECTOR_LAST_NAME = 302;
//    private static final int DIRECTOR_FIRST_NAME = 303;
//    private static final int DIRECTOR_FULL_NAME = 304;
//    private static final int DIRECTOR_BIRTHDAY = 305;
//    private static final int DIRECTOR_CREDITS = 306;
//
//    private static final int GENRE = 400;
//    private static final int GENRE_ID = 401;
//    private static final int GENRE_NAME = 402;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIES + "/#", MOVIES_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                return PopularMoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIES_ID:
                return PopularMoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (match) {
            case MOVIES: {
                builder.setTables(PopularMoviesContract.MovieEntry.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = PopularMoviesContract.MovieEntry.SORT_ORDER_DEFAULT;
                }
                break;
            }
            case MOVIES_ID: {
                builder.setTables(PopularMoviesContract.MovieEntry.TABLE_NAME);
                builder.appendWhere(PopularMoviesContract.MovieEntry.MOVIE_ID + " = " +
                        uri.getLastPathSegment());
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        retCursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri retUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(PopularMoviesContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    retUri = PopularMoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        PopularMoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(
                        PopularMoviesContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
