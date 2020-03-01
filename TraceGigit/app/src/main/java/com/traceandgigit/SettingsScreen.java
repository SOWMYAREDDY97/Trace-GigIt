package com.traceandgigit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

public class SettingsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        TextView customerType = findViewById(R.id.customerType);
        ParseUser user = ApplicationController.getCurrentUser(this);
        if(user != null){
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

    }
}
