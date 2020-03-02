package com.traceandgigit;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
// Parse Dependencies
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordActivity extends Activity {

    EditText email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email=findViewById(R.id.userId);

        
    }

    public void back(View v) {
        Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void forgot_pass(View view) {
        ParseUser.requestPasswordResetInBackground(email.getText().toString(),
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(ForgotPasswordActivity.this, "Please check your email.", Toast.LENGTH_LONG).show();
                            finish();
                            // An email was successfully sent with reset instructions.
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Error :: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            // Something went wrong. Look at the ParseException to see what's up.
                        }
                    }
                });
        Intent intent=new Intent(this,SignInActivity.class);
        startActivity(intent);
    }

}
