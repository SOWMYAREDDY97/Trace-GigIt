package com.traceandgigit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
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
    public static String email, object_id = null;


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
                    //makeSignUpCall();

                    final ParseUser sing_up_user = new ParseUser();
                    sing_up_user.setEmail(userEmail.getText().toString());
                    sing_up_user.setPassword(userPassword.getText().toString());
                    sing_up_user.setUsername(userFullname.getText().toString());
                    //sing_up_user.put("Mobile",(mobile.getText().toString()));
                    //sing_up_user.put("Address",(address.getText().toString()));

                    sing_up_user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //dlg.dismiss();
                                object_id = sing_up_user.getObjectId();
                                alertDisplayer("Sucessful Signup","Successfully signed up " + userEmail.getText().toString() + "!");

                            } else {
                                //dlg.dismiss();
                                ParseUser.logOut();
                                Toast.makeText(SignUpActivity.this, "error message", Toast.LENGTH_LONG).show();
                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


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


    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        email= userEmail.getText().toString();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.putExtra("email", userEmail.getText().toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

}
