package com.traceandgigit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.traceandgigit.model.UserSignUp;
import com.traceandgigit.requests.SharedUtils;

import com.traceandgigit.requests.UserSignUpCall;
import com.traceandgigit.retrofit.APICallback;
import com.traceandgigit.retrofit.APIResponses;
import com.traceandgigit.retrofit.APIService;

public class SignUpActivity extends Activity {

    private EditText userFullname;
    private EditText userEmail;
    private EditText userPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button newUserButton = findViewById(R.id.newUserButton);
        Button loginButton = findViewById(R.id.login);
        userFullname = findViewById(R.id.fullName);
        userEmail = findViewById(R.id.userId);
        userPassword = findViewById(R.id.password);

        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!areFieldsEmpty()){
                    makeSignUpCall();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(SignUpActivity.this,"Please do not leave fields empty",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private boolean areFieldsEmpty() {
        return userEmail.getText().toString().isEmpty()
                && userFullname.getText().toString().isEmpty()
                && userPassword.getText().toString().isEmpty();
    }

    private void makeSignUpCall(){

        UserSignUpCall.Params params = new UserSignUpCall.Params(userEmail.getText().toString(),
                userPassword.getText().toString(),userFullname.getText().toString(),
                SharedUtils.getInstance(this).getString(AppConstants.CLIENT_KEY));

        UserSignUpCall registration = new UserSignUpCall(params, new APICallback<UserSignUp>() {
            @Override
            public void onResponse(APIResponses<UserSignUp> response) {
                if(response != null && response.body() != null){
                    if(response.body().code == 200){
                        launchMainActivity();
                    }else if(response.body().code == 401){
                        Toast.makeText(SignUpActivity.this, response.body().message,Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t, int errorCode) {
                Log.d(this.getClass().getCanonicalName(),t.toString());
            }
        });
        APIService.getInstance().execute(registration);

    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
