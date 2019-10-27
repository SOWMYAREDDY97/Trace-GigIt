package com.example.tracegigit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }
public void onloginclick(View v) {
    Intent ini = new Intent(this, SaloonsDisplayActivity.class);
    startActivity(ini);
}

    public void onnewuserclick(View v) {
        Intent ini = new Intent(this, SignUpActivity.class);
        startActivity(ini);
    }
}
