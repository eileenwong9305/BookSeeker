package com.example.android.bookseeker;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of books by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookLoader.class.getSimpleName();

    /**
     * Query URL in String format
     */
    private String mStringUrl;

    /**
     * Constructs a new {@link BookLoader} object
     *
     * @param context   of the activity
     * @param stringUrl to load data from
     */
    public BookLoader(Context context, String stringUrl) {
        super(context);
        mStringUrl = stringUrl;
    }

    /**
     * Thus is on background thread
     */
    @Override
    public List<Book> loadInBackground() {
        // Don't perform the request if there are no URLs, or if the first URL is null
        if (mStringUrl == null) {
            return null;
        }
        //Perform network request, parse the response, and extract a list of books
        List<Book> books = QueryUtils.fetchBookData(mStringUrl);
        return books;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
