package com.example.tracegigit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }
    public void onsignupclick(View v) {
        Intent ini = new Intent(this, SaloonsDisplayActivity.class);
        startActivity(ini);
    }
}
