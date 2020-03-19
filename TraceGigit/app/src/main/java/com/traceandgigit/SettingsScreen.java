package com.traceandgigit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingsScreen extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);

        TextView setting_screen_username = findViewById(R.id.setting_user_name);
        TextView delete_account = findViewById(R.id.delete_account);
        TextView customerType = findViewById(R.id.customerType);

        ParseUser user = ApplicationController.getCurrentUser(this);
        if(user != null){
            setting_screen_username.setText(user.getUsername());
            if(user.get(Constants.USER_TYPE) != null){
                if(user.getBoolean(Constants.USER_TYPE)){
                    customerType.setText("Owner");
                }else{
                    customerType.setText("Customer");
                }
            }
        }

        TextView contactus = findViewById(R.id.contactus);
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsScreen.this,ContactUsActivity.class);
                startActivity(intent);
            }
        });

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseUser user = ParseUser.getCurrentUser();
                user.put("deactivate_account", false);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Toast.makeText(SettingsScreen.this, "succefully deactivated account", Toast.LENGTH_SHORT).show();
                            ParseUser.logOut();
                            Intent ini = new Intent(SettingsScreen.this,SignInActivity.class);
                            startActivity(ini);
                            finish();
                        }
                        else{
                            Toast.makeText(SettingsScreen.this, "unable to deactivate your account " + e, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }
}
