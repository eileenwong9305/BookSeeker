package com.example.android.bookseeker;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving book data from Google Book API.
 */
public final class QueryUtils {

    /**
     * Tag for log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Google book API keys
    private static final String ITEM_KEY = "items";
    private static final String VOLUME_INFO_KEY = "volumeInfo";
    private static final String TITLE_KEY = "title";
    private static final String AUTHORS_KEY = "authors";
    private static final String PUBLISHED_DATE_KEY = "publishedDate";
    private static final String IMAGE_LINKS_KEY = "imageLinks";
    private static final String SMALL_THUMBNAIL_KEY = "smallThumbnail";
    private static final String THUMBNAIL_KEY = "thumbnail";
    private static final String PUBLISHER_KEY = "publisher";
    private static final String DESCRIPTION_DATE_KEY = "description";
    private static final String PAGE_COUNT_KEY = "pageCount";
    private static final String CATEGORIES_KEY = "categories";
    private static final String LANGUAGE_KEY = "language";
    private static final String SMALL_IMAGE_KEY = "small";
    private static final String SELF_LINK_KEY = "selfLink";

    /** Read time out in milliseconds */
    private static final int READ_TIMEOUT = 10000;

    /** Connect time out in milliseconds */
    private static final int CONEECT_TIMEOUT = 15000;

    /** Success response code */
    private static final int SUCCESS_RESPONSE_CODE = 200;


    private QueryUtils() {
    }

    /**
     * Query Google Book API and return a list of {@link Book} objects.
     *
     * @param stringUrl to load data from
     * @return list of books
     */
    public static List<Book> fetchBookData(String stringUrl) {
        // Create URL object from String
        URL url = createUrl(stringUrl);

        // Perform HTTP request to the URL and receive back JSON response.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in closing input stream", e);
        }

        // Extract relevant fields from JSON Response and create a list of books
        List<Book> books = extractDataFromJSON(jsonResponse);
        // Return the list of books
        return books;
    }

    /**
     * Query Google Book API and return single {@link Book} object
     *
     * @param stringUrl to load data from
     * @return single {@link Book} object
     */
    public static Book fetchSingleBookData(String stringUrl) {
        // Create an URL object from String
        URL url = createUrl(stringUrl);

        // Make HTTP request and receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in closing input stream", e);
        }

        // Extract relevant fields from the JSON Response and create a {@link Book} object
        Book book = extractBookDataFromJSON(jsonResponse);
        // Return single {@link Book} object
        return book;
    }

    /**
     * Create and return new URL object from input string url.
     *
     * @param stringUrl string url
     * @return URL object
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating url", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     *
     * @param url book query url
     * @return json response string
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(CONEECT_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request is successful (response code 200), read the input stream
            // and parse the response.
            if (urlConnection.getResponseCode() == SUCCESS_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in retrieving JSON result", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON Response from
     * server.
     *
     * @param inputStream
     * @return json response in String
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list {@link Book} objects that has been built up from parsing a JSON response.
     *
     * @param jsonResponse json response in String
     * @return list of {@link Book} objects
     */
    private static ArrayList<Book> extractDataFromJSON(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList for adding books to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse json response. If there is problem with the way the JSON is formatted,
        // a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash and print the error message to log
        try {
            // Create a JSONObject from the jsonResponse string
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            // If the baseJsonResponse has key called "items", proceed with extracting data, else
            // set {@Book} object to be null
            if(baseJsonResponse.has(ITEM_KEY)) {
                // Extract JSONArray associated with the key called "items"
                JSONArray itemsArray = baseJsonResponse.optJSONArray(ITEM_KEY);

                // For each book in the itemsArray, create a {@link Book} object
                for (int i = 0; i < itemsArray.length(); i++) {
                    // Get the item at position i
                    JSONObject item = itemsArray.getJSONObject(i);
                    // Extract JSONObject associated with key called "volumeInfo"
                    JSONObject volumeInfo = item.getJSONObject(VOLUME_INFO_KEY);

                    // If the volumeInfo has key called "title", extract the String title
                    String title = "";
                    if (volumeInfo.has(TITLE_KEY)) {
                        title = volumeInfo.getString(TITLE_KEY);
                    }

                    // If the volumeInfo has key called "authors", extract the JSONArray associated to
                    // it. Add each author into arrayList authors and reformat the arrayList into
                    // String of authors separated by comma.
                    String author = "";
                    if (volumeInfo.has(AUTHORS_KEY)) {
                        JSONArray authorArray = volumeInfo.getJSONArray(AUTHORS_KEY);
                        ArrayList<String> authors = new ArrayList<>();
                        for (int j = 0; j < authorArray.length(); j++) {
                            authors.add(authorArray.getString(j));
                        }
                        // Join each String in the arrayList to form a single String separated by comma
                        author = joinString(authors);
                    }

                    // If the volumeInfo has key called "publishDate", extract the String published date
                    String publishedDate = "";
                    if (volumeInfo.has(PUBLISHED_DATE_KEY)) {
                        publishedDate = volumeInfo.getString(PUBLISHED_DATE_KEY);
                    }

                    // If the volumeInfo has key called "description", extract the String description
                    String description = "";
                    if (volumeInfo.has(DESCRIPTION_DATE_KEY)) {
                        description = volumeInfo.getString(DESCRIPTION_DATE_KEY);
                    }

                    // If the volumeInfo has key called "imageLinks", extract the JSONObject.
                    Drawable bookImage = null;
                    if (volumeInfo.has(IMAGE_LINKS_KEY)) {
                        JSONObject imageLink = volumeInfo.getJSONObject(IMAGE_LINKS_KEY);
                        // If the imageLink has key called "thumbnail", extract the String thumbnail,
                        // else extract the String associated with "smallThumbnail"
                        String thumbnail;
                        if (volumeInfo.has(THUMBNAIL_KEY)) {
                            thumbnail = imageLink.getString(THUMBNAIL_KEY);
                        } else {
                            thumbnail = imageLink.getString(SMALL_THUMBNAIL_KEY);
                        }
                        // Create image drawable from the String thumbnail
                        bookImage = createImageDrawable(thumbnail);
                    }

                    // If the item has key called "selflink", extract the String selfLink
                    String selfLink = "";
                    if (item.has(SELF_LINK_KEY)) {
                        selfLink = item.getString(SELF_LINK_KEY);
                    }

                    // Create a new {@link Book} object with title, authors, published date,
                    // description, image of book and self link from jsonResponse
                    books.add(new Book(title, author, publishedDate, description, bookImage, selfLink));
                }
            }else{
                books = null;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON result", e);
        }
        // Return the list of books
        return books;
    }

    /**
     * Return a {@link Book} objects that has been built up from parsing a JSON response.
     *
     * @param jsonResponse json response in String
     * @return {@link Book} objects
     */
    private static Book extractBookDataFromJSON(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        Book book = null;

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the jsonResponse string
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            // Extract JSONObject associated with key called "volumeInfo"
            JSONObject volumeInfo = baseJsonResponse.getJSONObject(VOLUME_INFO_KEY);

            // If the volumeInfo has key called "title", extract the String title
            String title = "";
            if (volumeInfo.has(TITLE_KEY)) {
                title = volumeInfo.getString(TITLE_KEY);
            }

            // If the volumeInfo has key called "authors", extract the JSONArray associated to
            // it. Add each author into arrayList authors and reformat the arrayList into
            // String of authors separated by comma.
            String author = "";
            if (volumeInfo.has(AUTHORS_KEY)) {
                JSONArray authorArray = volumeInfo.getJSONArray(AUTHORS_KEY);
                ArrayList<String> authors = new ArrayList<>();
                for (int j = 0; j < authorArray.length(); j++) {
                    authors.add(authorArray.getString(j));
                }
                author = joinString(authors);
            }

            // If the volumeInfo has key called "publishedDate", extract the String published date
            String publishedDate = "";
            if (volumeInfo.has(PUBLISHED_DATE_KEY)) {
                publishedDate = volumeInfo.getString(PUBLISHED_DATE_KEY);
            }

            // If the volumeInfo has key called "imageLinks", extract the JSONObject.
            Drawable bookImage = null;
            if (volumeInfo.has(IMAGE_LINKS_KEY)) {
                JSONObject imageLink = volumeInfo.getJSONObject(IMAGE_LINKS_KEY);
                // If the imageLink has key called "small", extract the String thumbnail associated
                // with it, else if imageLink has key called "thumbnail", extract the thumbnail
                // associated with it
                // else extract the String associated with "smallThumbnail"
                String smallImage;
                if (volumeInfo.has(SMALL_IMAGE_KEY)) {
                    smallImage = imageLink.getString(SMALL_IMAGE_KEY);
                } else if (volumeInfo.has(THUMBNAIL_KEY)) {
                    smallImage = imageLink.getString(THUMBNAIL_KEY);
                } else {
                    smallImage = imageLink.getString(SMALL_THUMBNAIL_KEY);
                }
                // Create image drawable from the String thumbnail
                bookImage = createImageDrawable(smallImage);
            }

            // If the volumeInfo has key called "publisher", extract the String publisher
            String publisher = "-";
            if (volumeInfo.has(PUBLISHER_KEY)) {
                publisher = volumeInfo.getString(PUBLISHER_KEY);
            }

            // If the volumeInfo has key called "description", extract the String description
            String description = "";
            if (volumeInfo.has(DESCRIPTION_DATE_KEY)) {
                description = volumeInfo.getString(DESCRIPTION_DATE_KEY);
            }

            // If the volumeInfo has key called "pageCount", extract the integer page count
            int pageCount = 0;
            if (volumeInfo.has(PAGE_COUNT_KEY)) {
                pageCount = volumeInfo.getInt(PAGE_COUNT_KEY);
            }

            // If the volumeInfo has key called "language", extract the String language
            String language = "-";
            if (volumeInfo.has(LANGUAGE_KEY)) {
                language = volumeInfo.getString(LANGUAGE_KEY);
            }

            // If the volumeInfo has key called "categories", extract the JSONArray associated to
            // it. Add each category into arrayList categories and reformat the arrayList into
            // String of category separated by comma.
            String category = "-";
            if (volumeInfo.has(CATEGORIES_KEY)) {
                JSONArray categoryArray = volumeInfo.getJSONArray(CATEGORIES_KEY);
                ArrayList<String> categories = new ArrayList<>();
                for (int j = 0; j < categoryArray.length(); j++) {
                    categories.add(categoryArray.getString(j));
                }
                category = joinString(categories);
            }

            // Create a new {@link Book} object with the title, authors, published date, image of
            // book, publisher, description, page count, categories and language from the
            // JSON response.
            book = new Book(title, author, publishedDate, bookImage, publisher, description, pageCount, category, language);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON result", e);
        }
        // Return book
        return book;
    }

    /**
     * Create image drawable from string url
     *
     * @param imageStringUrl string url to extract drawable from
     * @return drawable image
     */
    private static Drawable createImageDrawable(String imageStringUrl) {
        if (TextUtils.isEmpty(imageStringUrl)) {
            return null;
        }
        URL imageUrl;
        InputStream content = null;
        try {
            imageUrl = new URL(imageStringUrl);
            content = (InputStream) imageUrl.getContent();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating image url");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with obtain image content");
        }
        return Drawable.createFromStream(content, "src");
    }

    /**
     * Join string using comma in arrayList to from a single String sentences.
     *
     * @param words arrayList of authors
     * @return String author
     */
    private static String joinString(ArrayList<String> words) {
        StringBuilder word = new StringBuilder();
        word.append(words.get(0));
        for (int j = 1; j < words.size(); j++) {
            word.append(", ").append(words.get(j));
        }
        return word.toString();
    }
}
