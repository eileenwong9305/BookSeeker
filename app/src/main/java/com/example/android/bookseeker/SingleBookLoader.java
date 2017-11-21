package com.example.android.bookseeker;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Loads single book by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class SingleBookLoader extends AsyncTaskLoader<Book> {
    /**
     * Query URL in String format
     */
    private String mStringUrl;

    /**
     * Constructs a new {@link SingleBookLoader} object
     *
     * @param context   of the activity
     * @param stringUrl to load data from
     */
    public SingleBookLoader(Context context, String stringUrl) {
        super(context);
        mStringUrl = stringUrl;
    }

    /**
     * Thus is on background thread
     */
    @Override
    public Book loadInBackground() {
        if (mStringUrl == null) {
            return null;
        }
        return QueryUtils.fetchSingleBookData(mStringUrl);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
