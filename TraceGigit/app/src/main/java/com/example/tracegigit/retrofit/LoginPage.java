package com.example.tracegigit.retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tracegigit.R;

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
