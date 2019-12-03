package com.traceandgigit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.traceandgigit.model.UserSignIn;
import com.traceandgigit.requests.SharedUtils;
import com.traceandgigit.requests.UserSignInCall;
import com.traceandgigit.retrofit.APICallback;
import com.traceandgigit.retrofit.APIResponses;
import com.traceandgigit.retrofit.APIService;
import com.traceandgigit.retrofit.RetrofitClientInstance;

public class SignInActivity extends Activity {

    private EditText userId;
    private EditText password;
    private  Button login,forgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Button newUserButton = findViewById(R.id.newUserButton);
        login = findViewById(R.id.login);
        userId = findViewById(R.id.userId);
        password = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId.getText().toString() != null
                        && !userId.getText().toString().isEmpty()){
                    Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                    intent.putExtra("email",userId.getText().toString());
                    startActivity(intent);
                }else{
                    Toast.makeText(SignInActivity.this,"Please enter your Email ID",Toast.LENGTH_LONG).show();
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkNoEmptyFields()){
                    makeSignInAPICall();
                }else{
                    Toast.makeText(SignInActivity.this, "Email or Password is empty. Please check",Toast.LENGTH_LONG).show();
                }
            }
        });
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void makeSignInAPICall() {
        UserSignInCall.Params params = new UserSignInCall.Params(userId.getText().toString(),
                password.getText().toString(),
                SharedUtils.getInstance(this).getString(AppConstants.CLIENT_KEY));

        UserSignInCall userSignInCall = new UserSignInCall(params, new APICallback<UserSignIn>() {
            @Override
            public void onResponse(APIResponses<UserSignIn> response) {
                if(response != null && response.body() != null){
                    if(response.body().code == 200){
                        launchMainActivity();
                    }else if(response.body().code == 400){
                        Toast.makeText(SignInActivity.this, response.body().message,Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t, int errorCode) {

            }
        });
        APIService.getInstance().execute(userSignInCall);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean checkNoEmptyFields(){
        return userId != null
                && userId.getText() != null
                && !userId.getText().toString().isEmpty()
                && password != null
                && password.getText() != null
                && !password.getText().toString().isEmpty();
    }
}
