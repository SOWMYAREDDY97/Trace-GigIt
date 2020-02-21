package com.traceandgigit;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
// Parse Dependencies
import com.parse.Parse;
import com.parse.ParseUser;

public class ForgotPasswordActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        
    }
}
