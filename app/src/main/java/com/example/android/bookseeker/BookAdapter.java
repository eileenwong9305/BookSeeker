package com.example.android.bookseeker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eileen on 10/22/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();

    /**
     * Constructs a new (@link BookAdapter).
     *
     * @param context of the app
     * @param books   is the list of books, which is the data source of the adapter.
     */
    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    /**
     * Returns a list item view that displays information about the book at the given position
     * in the list of books.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (convertView) that can be reused.
        // If convertView is null, inflate a new list item layout.
        View listItemView = convertView;
        ViewHolder holder = new ViewHolder();
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.single_list_item, parent, false);
            holder.titleText = (TextView) listItemView.findViewById(R.id.title_text);
            holder.authorText = (TextView) listItemView.findViewById(R.id.author_text);
            holder.publishDateText = (TextView) listItemView.findViewById(R.id.publish_date_text);
            holder.smallThumbnail = (ImageView) listItemView.findViewById(R.id.small_thumbnail_image);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        // Get the book t the given position in the list of books.
        Book currentBook = getItem(position);

        // Display the title of the current book in the TextView
        holder.titleText.setText(currentBook.getTitle());
        // Display the authors of the current book in the TextView
        holder.authorText.setText(currentBook.getAuthor());
        // Display the published date of the current book in the TextView
        holder.publishDateText.setText(currentBook.getPublishedDate());
        // Display the image of the current book in the ImageView
        holder.smallThumbnail.setImageDrawable(currentBook.getBookImage());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    static class ViewHolder {
        TextView titleText;
        TextView authorText;
        TextView publishDateText;
        ImageView smallThumbnail;
    }

}
