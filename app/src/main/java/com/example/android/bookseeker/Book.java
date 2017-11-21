package com.example.android.bookseeker;

import android.graphics.drawable.Drawable;

/**
 * {@link Book} represents book that users want to read.
 * It contains detail information for each book.
 */
public class Book {

    /**
     * Title of the book
     */
    private String mTitle;

    /**
     * Author of the book
     */
    private String mAuthor;

    /**
     * Published date
     */
    private String mPublishedDate;

    /**
     * Url of the book
     */
    private String mSelfLink;

    /**
     * Image of the book
     */
    private Drawable mBookImage;

    /**
     * Description of the book
     */
    private String mDescription;

    /**
     * Publisher of the book
     */
    private String mPublisher;

    /**
     * Page count of the book
     */
    private int mPageCount;

    /**
     * Language of book
     */
    private String mLanguage;

    /**
     * Categories of the book
     */
    private String mCategory;

    /**
     * Create a new book object
     *
     * @param title         of book
     * @param author        of book
     * @param publishedDate of book
     * @param description   of book
     * @param bookImage     of book
     * @param selfLink      url link to the book details
     */
    public Book(String title, String author, String publishedDate, String description,
                Drawable bookImage, String selfLink) {
        mTitle = title;
        mAuthor = author;
        mPublishedDate = publishedDate;
        mDescription = description;
        mBookImage = bookImage;
        mSelfLink = selfLink;
    }

    /**
     * Create a new book object
     *
     * @param title         of book
     * @param author        of book
     * @param publishedDate of book
     * @param bookImage     Image of book cover
     * @param publisher     of book
     * @param description   of book
     * @param pageCount     Number of page of book
     * @param categories    of book
     * @param language      of book
     */
    public Book(String title, String author, String publishedDate, Drawable bookImage,
                String publisher, String description, int pageCount,
                String categories, String language) {
        mTitle = title;
        mAuthor = author;
        mPublishedDate = publishedDate;
        mBookImage = bookImage;
        mPublisher = publisher;
        mDescription = description;
        mPageCount = pageCount;
        mCategory = categories;
        mLanguage = language;
    }

    /**
     * @return title of the book
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return author of the book
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * @return published date of the book
     */
    public String getPublishedDate() {
        return mPublishedDate;
    }

    /**
     * @return link to detail of the book
     */
    public String getSelfLink() {
        return mSelfLink;
    }

    /**
     * @return image of the book
     */
    public Drawable getBookImage() {
        return mBookImage;
    }

    /**
     * @return description of the book
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @return publisher of the book
     */
    public String getPublisher() {
        return mPublisher;
    }

    /**
     * @return language of the book
     */
    public String getLanguage() {
        return mLanguage;
    }

    /**
     * @return page count of the book
     */
    public int getPageCount() {
        return mPageCount;
    }

    /**
     * @return category of the book
     */
    public String getCategory() {
        return mCategory;
    }
}
