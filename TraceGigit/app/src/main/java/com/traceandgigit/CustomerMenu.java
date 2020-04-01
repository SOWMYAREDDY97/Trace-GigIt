package com.traceandgigit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CustomerMenu extends AppCompatActivity {

    public TextView item_1_name, item_1_cost;
    public TextView item_2_name, item_2_cost;
    public TextView item_3_name, item_3_cost;
    public TextView item_4_name, item_4_cost;
    public Button back_cust_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_menu_view);

        item_1_name = findViewById(R.id.item_1_cust);
        item_1_cost = findViewById(R.id.item_1_cost_cust);
        item_2_name = findViewById(R.id.item_2_cust);
        item_2_cost = findViewById(R.id.item_2_cost_cust);
        item_3_name = findViewById(R.id.item_3_cust);
        item_3_cost = findViewById(R.id.item_3_cost_cust);
        item_4_name = findViewById(R.id.item_4_cust);
        item_4_cost = findViewById(R.id.item_4_cost_cust);
        back_cust_menu = findViewById(R.id.back_cust_menu);

        Intent i = getIntent();
        ParseUser user = (ParseUser)i.getParcelableExtra("sampleObject");

        String obeject_id = user.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("menu");
        query.whereEqualTo("user_object_name", obeject_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    item_1_name.setText(object.getString("item_1_name"));
                    item_1_cost.setText(object.getString("item_1_cost"));
                    item_2_name.setText(object.getString("item_2_name"));
                    item_2_cost.setText(object.getString("item_2_cost"));
                    item_3_name.setText(object.getString("item_3_name"));
                    item_3_cost.setText(object.getString("item_3_cost"));
                    item_4_name.setText(object.getString("item_4_name"));
                    item_4_cost.setText(object.getString("item_4_cost"));

                } else {
                    Toast.makeText(CustomerMenu.this, "unable to get menu details " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });


        back_cust_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CustomerMenu.this,MainActivity.class);
                startActivity(intent);

            }
        });



    }


}
