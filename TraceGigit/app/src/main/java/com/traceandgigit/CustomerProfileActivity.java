package com.traceandgigit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CustomerProfileActivity extends AppCompatActivity {

   private EditText full_name;
   private EditText user_email;
   private EditText gender;
   private EditText user_number ;
   Button cancle ;
   Button save ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        full_name = findViewById(R.id.profile_full_name);
        user_email = findViewById(R.id.profile_email_edit);
        gender = findViewById(R.id.profile_gender);
        user_number = findViewById(R.id.phone_number);
        cancle = findViewById(R.id.cancleButton);
        save = findViewById(R.id.saveButton);


        ParseUser user = ParseUser.getCurrentUser();

        user_email.setText(user.getEmail());
        full_name.setText(user.getUsername());
        gender.setText(user.getString("gender"));
        user_number.setText(user.getString("Mobile"));


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(CustomerProfileActivity.this,MainActivity.class);
                    startActivity(intent);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_name = full_name.getText().toString();
                String update_email = user_email.getText().toString();

                if (!user_name.isEmpty() && !update_email.isEmpty() &&
                        !user_number.getText().toString().isEmpty() && user_number.getText().toString().length() ==10
                        && !gender.getText().toString().isEmpty()){
                    ParseUser user = ParseUser.getCurrentUser();
                    user.setUsername(user_name);
                    user.setEmail(update_email);
                    user.put("Mobile", user_number.getText().toString());
                    user.put("gender", gender.getText().toString());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null){
                                Toast.makeText(CustomerProfileActivity.this, "succefully updated details", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CustomerProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(CustomerProfileActivity.this, "unable to updated details " + e, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
                else{
                    Toast.makeText(CustomerProfileActivity.this, "unable to updated details please check the details you entered" , Toast.LENGTH_SHORT).show();
                }



            }
        });







    }
}
