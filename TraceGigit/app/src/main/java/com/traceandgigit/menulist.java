package com.traceandgigit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class menulist extends AppCompatActivity {
    private LinearLayout parentLinearLayout;
    public EditText item_1_name, item_1_cost;
    public EditText item_2_name, item_2_cost;
    public EditText item_3_name, item_3_cost;
    public EditText item_4_name, item_4_cost;
    public String object_id;
    public Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulist);
        //parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);

                    item_1_name = findViewById(R.id.item_one_name);
                    item_1_cost = findViewById(R.id.item_one_cost);
                    item_2_name = findViewById(R.id.item_two_name);
                    item_2_cost = findViewById(R.id.item_two_cost);
                    item_3_name = findViewById(R.id.item_three_name);
                    item_3_cost = findViewById(R.id.item_three_cost);
                    item_4_name = findViewById(R.id.item_four_name);
                    item_4_cost = findViewById(R.id.item_four_cost);
                    save = findViewById(R.id.save_menu);
                    object_id = SignInActivity.object_id;
                    if (object_id == null){
                        object_id = SignUpActivity.object_id;
                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("menu");
                    query.whereEqualTo("user_object_name", object_id);
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
                                Toast.makeText(menulist.this, "unable to get menu details " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ParseQuery<ParseObject> query = ParseQuery.getQuery("menu");
                query.whereEqualTo("user_object_name", object_id);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {

                            object.put("item_1_name", item_1_name.getText().toString());
                            object.put("item_1_cost", item_1_cost.getText().toString());
                            object.put("item_2_name", item_2_name.getText().toString());
                            object.put("item_2_cost", item_2_cost.getText().toString());
                            object.put("item_3_name", item_3_name.getText().toString());
                            object.put("item_3_cost", item_3_cost.getText().toString());
                            object.put("item_4_name", item_4_name.getText().toString());
                            object.put("item_4_cost", item_4_cost.getText().toString());
                            object.saveInBackground();
                            Intent ini = new Intent(getApplicationContext(), OwnerActivity.class);
                            startActivity(ini);
                            Toast.makeText(menulist.this, "menu details updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            final ParseObject menu = new ParseObject("menu");
                            menu.put("user_object_name", object_id);
                            menu.put("item_1_name", item_1_name.getText().toString());
                            menu.put("item_1_cost",item_1_cost.getText().toString());
                            menu.put("item_2_name",item_2_name.getText().toString());
                            menu.put("item_2_cost",item_2_cost.getText().toString());
                            menu.put("item_3_name",item_3_name.getText().toString());
                            menu.put("item_3_cost",item_3_cost.getText().toString());
                            menu.put("item_4_name",item_4_name.getText().toString());
                            menu.put("item_4_cost",item_4_cost.getText().toString());
                            menu.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null){
                                        Intent ini = new Intent(getApplicationContext(), OwnerActivity.class);
                                        startActivity(ini);
                                        Toast.makeText(menulist.this, "menu details added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(menulist.this, "unable to add menu details " + e, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                });





            }
        });








    }
//    public void onAddField(View v) {
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View rowView = inflater.inflate(R.layout.field, null);
//        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
//    }
//
//    public void onDelete(View v) {
//        parentLinearLayout.removeView((View) v.getParent());
//    }

}