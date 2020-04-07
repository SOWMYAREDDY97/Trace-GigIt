package com.traceandgigit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class searchFilter extends AppCompatActivity implements OnQueryTextListener {

    public ImageButton search_action ;
    public EditText search_text ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        search_action = findViewById(R.id.search_action);
        search_text = findViewById(R.id.search_text);

        search_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_text.getText().clear();
            }
        });


        search_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String search_string = search_text.getText().toString();


            }
        });



    }



    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        return false;
    }
}
