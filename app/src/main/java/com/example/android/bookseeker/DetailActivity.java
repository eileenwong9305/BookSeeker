package com.example.android.bookseeker;

import android.app.LoaderManager;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Book> {

    /**
     * Constant value for the single book loader ID
     */
    private static final int SINGLE_BOOK_LOADER_ID = 0;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * ScrollView display description and additional information
     */
    private ScrollView mScrollView;

    /**
     * Self link of book
     */
    private String selfLink;

    /**
     * Description of book
     */
    private String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get String selfLink from intent extra
        selfLink = getIntent().getStringExtra(BookListActivity.KEY_LINK);
        // Get String description from intent extra
        description = getIntent().getStringExtra(BookListActivity.KEY_DESC);

        // Find reference to the empty state TextView in the layout
        mEmptyStateTextView = (TextView) findViewById(R.id.detail_empty_text);

        // Find reference to the scrollVIew in the layout
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);

        // Check internet connection and start loader to fetch single book data and update ui.
        fetchData();
    }

    private void fetchData() {
        // Set the relative layout with progress bar visible while waiting for data to be fetched
        RelativeLayout detailProgressBarLayout = (RelativeLayout) findViewById(R.id.detail_progress_layout);
        detailProgressBarLayout.setVisibility(View.VISIBLE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            // Hide the error message
            mEmptyStateTextView.setVisibility(View.GONE);

            // Get a reference to the LoaderManager, in order to interact with
            LoaderManager loaderManager = getLoaderManager();

            // Initiate the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter.
            getLoaderManager().initLoader(SINGLE_BOOK_LOADER_ID, null, this);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            detailProgressBarLayout.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(getString(R.string.no_internet));
        }
    }

    /**
     * Save current position of ScrollView
     * Credit to: https://stackoverflow.com/questions/29208086/save-the-position-of-scrollview-when-the-orientation-changes/39898595#39898595
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    /**
     * Restore position of ScrollView
     * Credit to: https://stackoverflow.com/questions/29208086/save-the-position-of-scrollview-when-the-orientation-changes/39898595#39898595
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        if (position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }

    @Override
    public Loader<Book> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the self Link
        return new SingleBookLoader(DetailActivity.this, selfLink);
    }

    @Override
    public void onLoadFinished(Loader<Book> loader, Book book) {
        // Hide relative layout with progress bar because the data has been loaded
        RelativeLayout detailProgressBarLayout = (RelativeLayout) findViewById(R.id.detail_progress_layout);
        detailProgressBarLayout.setVisibility(View.GONE);

        // If there is a valid {@link Book}, update the ui. Else, show error message
        if (book != null) {
            updateUI(book);
        } else {
            // Set empty state text to display "No book found."
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(getString(R.string.no_book_found));
        }
    }

    @Override
    public void onLoaderReset(Loader<Book> loader) {
        loader.abandon();
    }

    /**
     * Update ui when called
     *
     * @param book selected book
     */
    private void updateUI(Book book) {
        // Display the title of single {@link Book) to the TextView
        TextView titleText = (TextView) findViewById(R.id.title_detail_text);
        titleText.setText(book.getTitle());

        // Display the authors of single {@link Book) to the TextView
        TextView authorText = (TextView) findViewById(R.id.author_detail_text);
        authorText.setText(getString(R.string.by_author, book.getAuthor()));

        // Display the published date of single {@link Book) to the TextView
        TextView publishedDateText = (TextView) findViewById(R.id.date_detail_text);
        publishedDateText.setText(book.getPublishedDate());

        // Display the description of single {@link Book) to the TextView is the description string
        // is not empty, else remove the CardView associated with description.
        TextView descriptionText = (TextView) findViewById(R.id.description_text);
        if (!TextUtils.isEmpty(description)) {
            descriptionText.setText(description);
        } else {
            findViewById(R.id.description_card).setVisibility(View.GONE);
        }

        // Display the publisher of single {@link Book) to the TextView
        TextView publisherText = (TextView) findViewById(R.id.publisher_text);
        publisherText.setText(getString(R.string.publisher, book.getPublisher()));

        // Display the language of single {@link Book) to the TextView
        TextView languageText = (TextView) findViewById(R.id.language_text);
        languageText.setText(getString(R.string.language, book.getLanguage()));

        // Display the page count of single {@link Book) to the TextView
        TextView pageCountText = (TextView) findViewById(R.id.page_count_text);
        pageCountText.setText(getString(R.string.page_count, book.getPageCount()));

        // Display the categories of single {@link Book) to the TextView
        TextView categoryText = (TextView) findViewById(R.id.category_text);
        categoryText.setText(getString(R.string.categories, book.getCategory()));

        // Display the image of single {@link Book) to the ImageView
        ImageView bookImage = (ImageView) findViewById(R.id.book_full_image);
        bookImage.setImageDrawable(book.getBookImage());
    }

}
