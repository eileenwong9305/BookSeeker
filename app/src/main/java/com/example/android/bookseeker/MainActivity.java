package com.example.android.bookseeker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find reference to the editText in the layout
        final EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
        // Find reference to the button in the layout
        Button searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new intent to open BookListActivity screen
                Intent bookListIntent = new Intent(MainActivity.this, BookListActivity.class);
                // Set String search query from editText to intent
                bookListIntent.putExtra("query", searchEditText.getText().toString());
                // Launch new activity
                startActivity(bookListIntent);
            }
        });
    }
}
