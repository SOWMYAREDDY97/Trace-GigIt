package com.traceandgigit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
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
    public static String object_id = null;
    public static String email = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Button newUserButton = findViewById(R.id.newUserButton);
        login = findViewById(R.id.log_in);
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
                    //makeSignInAPICall();

                    final ProgressDialog dlg = new ProgressDialog(SignInActivity.this);
                    dlg.setTitle("Please, wait a moment.");
                    dlg.setMessage("Logging in...");
                    dlg.show();


                    ParseUser.logInInBackground(userId.getText().toString(), password.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null) {
                                boolean deatcivate_status = parseUser.getBoolean("deactivate_account");
                                if (deatcivate_status == true) {
                                    dlg.dismiss();
                                    alertDisplayer("Sucessful Login", "Welcome back " + userId.getText().toString() + "!", parseUser.getBoolean(Constants.USER_TYPE));

                                    object_id = parseUser.getObjectId();
                                }
                                else {
                                    dlg.dismiss();
                                    ParseUser.logOut();
                                    Toast.makeText(SignInActivity.this, "you deactivated your account", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                dlg.dismiss();
                                ParseUser.logOut();
                                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });



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
        boolean status = false;
        if (userId.getText().toString().endsWith("@gmail.com")){
            status = true;
        }
        return userId != null
                && userId.getText() != null
                && !userId.getText().toString().isEmpty()
                && password != null
                && password.getText() != null
                && !password.getText().toString().isEmpty()
                && status;
    }



    private void alertDisplayer(String title, String message, final boolean isOwner){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        email= userId.getText().toString();
                        Intent intent = null;
                        if (!isOwner) {
                            intent = new Intent(SignInActivity.this, MainActivity.class);
                        }else{
                            intent = new Intent(SignInActivity.this, OwnerActivity.class);
                        }
                        intent.putExtra("email", userId.getText().toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}
