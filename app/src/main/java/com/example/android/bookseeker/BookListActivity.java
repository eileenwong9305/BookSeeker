package com.example.android.bookseeker;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    /**
     * Constant value for the book loader ID
     */
    private static final int BOOK_LOADER_ID = 0;
    /**
     * Base url for query
     */
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    /**
     * List view layout
     */
    private ListView listView;
    /**
     * Query url
     */
    private String queryUrl = "";
    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * name of self link data
     */
    public static final String KEY_LINK = "link";

    /**
     * name of description data
     */
    public static final String KEY_DESC = "desccription";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);

        // Get the String query from intent extra
        String query = getIntent().getStringExtra("query");

        // Join the String queryUrl with query word
        queryUrl = BASE_URL + query;
        Log.e("BookList", queryUrl);

        // Find reference to the {@link ListView} in the layout
        listView = (ListView) findViewById(R.id.list);

        // Find reference to the empty state TextView in the layout
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_text);

        // Set the mEmptyStateTextView on the {@link ListView}
        listView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes empty list of book as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set adapter on the {@link ListView}
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current book that was clicked
                Book currentBook = mAdapter.getItem(position);

                // Create a new intent to open detail activity layout file
                Intent detailIntent = new Intent(BookListActivity.this, DetailActivity.class);

                // Add self link and description of the current book to the intent if currentBook
                // is not null
                if (currentBook != null) {
                    detailIntent.putExtra(KEY_LINK, currentBook.getSelfLink());
                    detailIntent.putExtra(KEY_DESC, currentBook.getDescription());
                }
                // Send intent to launch detail activity
                startActivity(detailIntent);
            }
        });

        // Check internet connection and start loader to fetch book data and update the adapter.
        // If there is a network connection, fetch data
        if (hasInternet()) {
            // Hide the error message
            mEmptyStateTextView.setVisibility(View.GONE);

            // Get a reference to the LoaderManager, in order to interact with
            LoaderManager loaderManager = getLoaderManager();

            // Restart the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter.
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            RelativeLayout listProgressBarLayout = (RelativeLayout) findViewById(R.id.list_progress_layout);
            listProgressBarLayout.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(getString(R.string.no_internet));
        }

    }

    /**
     * Inflate search bar and hook up listener when search is performed
     * Credit to: https://guides.codepath.com/android/Extended-ActionBar-Guide
     *
     * @param menu xml
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        // Get the SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // Customize edittext color and hint text
        EditText editText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.WHITE);
        editText.setHint(R.string.search_book_title);

        // Hook up a listener to searchView for when a search is performed
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Join the String queryUrl with query word
                queryUrl = BASE_URL + query;
                // Perform query
                doSearch();
                // Workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Restart loader to fetch book data if there is internet when called
     */
    private void doSearch() {
        // If there is a network connection, fetch data
        if (hasInternet()) {
            // Hide the error message
            mEmptyStateTextView.setVisibility(View.GONE);

            // Get a reference to the LoaderManager, in order to interact with
            LoaderManager loaderManager = getLoaderManager();

            // Restart the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter.
            loaderManager.restartLoader(BOOK_LOADER_ID, null, this);

        } else {
            mAdapter.clear();
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            RelativeLayout listProgressBarLayout = (RelativeLayout) findViewById(R.id.list_progress_layout);
            listProgressBarLayout.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(getString(R.string.no_internet));
        }
    }

    /**
     * Check if there is internet connection
     */
    private boolean hasInternet() {
        // Set the relative layout with progress bar visible while waiting for data to be fetched
        RelativeLayout listProgressBarLayout = (RelativeLayout) findViewById(R.id.list_progress_layout);
        listProgressBarLayout.setVisibility(View.VISIBLE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the query url
        return new BookLoader(BookListActivity.this, queryUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        // Hide relative layout with progress bar because the data has been loaded
        RelativeLayout listProgressBarLayout = (RelativeLayout) findViewById(R.id.list_progress_layout);
        listProgressBarLayout.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // IF there is a valid list of {@link Book}s, then add them to the adapter's
        // data set This will trigger the ListView to update. Else, show error message
        if (data != null && !data.isEmpty()) {
            // Update listView
            mAdapter.addAll(data);
        } else {
            // Set empty state text to display "No book found."
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(getString(R.string.no_book_found));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}
