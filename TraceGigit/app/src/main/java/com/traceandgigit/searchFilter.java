package com.traceandgigit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class searchFilter extends AppCompatActivity  {

    public ImageButton search_action ;
    public EditText search_text ;
    List<ParseUser> matched_users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        search_action = findViewById(R.id.search_action);
        search_text = findViewById(R.id.search_text);




        search_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String search_string = search_text.getText().toString();
                ParseQuery<ParseUser> saloonQuery = ParseUser.getQuery();
                try{
                    saloonQuery.whereEqualTo(Constants.USER_TYPE,true);
                    saloonQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {

                            for (int i=0; i< objects.size(); i++){
                                ParseUser user = objects.get(i);
                                String shop_name = user.getString("shop_name");
                                if (shop_name.contains(search_string)){
                                    matched_users.add(user);
                                }

                            }

                            //findTheNearestSaloons(objects, latitude, longitude);

                        }
                    });

                    send_data_to_main();

                }catch (Exception e){
                    e.printStackTrace();
                }




            }
        });




    }


    public void send_data_to_main(){
        MainActivity main = new MainActivity();
        main.addDataToRecyclerView(matched_users);
    }




}
