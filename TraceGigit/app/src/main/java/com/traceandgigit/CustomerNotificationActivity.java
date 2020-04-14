package com.traceandgigit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerNotificationActivity extends AppCompatActivity {


    private TextView textView2;
    private Button submitButton, cancelButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner__notofications);

        textView2 = findViewById(R.id.textView2);
        submitButton = findViewById(R.id.button2);
        cancelButton = findViewById(R.id.button3);

        if(getIntent() != null){
            String message  = getIntent().getStringExtra("message");
            if(message != null){
                textView2.setText(message);
            }
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomerNotificationActivity.this, "Accepted",Toast.LENGTH_LONG).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomerNotificationActivity.this, "canceled",Toast.LENGTH_LONG).show();
            }
        });

    }


}
