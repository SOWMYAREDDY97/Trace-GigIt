package com.traceandgigit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.traceandgigit.model.DeviceRegData;
import com.traceandgigit.requests.DeviceRegistration;
import com.traceandgigit.requests.SharedUtils;
import com.traceandgigit.requests.UserSignUpCall;
import com.traceandgigit.retrofit.APICallback;
import com.traceandgigit.retrofit.APIResponses;
import com.traceandgigit.retrofit.APIService;

public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button newUserButton = (Button) findViewById(R.id.newUserButton);

        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });


        makeSignUpCall();
    }

    private void makeSignUpCall(){

        UserSignUpCall.Params params = new UserSignUpCall.Params("","","","","","");
        UserSignUpCall registration = new UserSignUpCall(params, new APICallback<DeviceRegData>() {
            @Override
            public void onResponse(APIResponses<DeviceRegData> response) {
                if(response != null && response.body() != null && response.body().clientKey != null){
                    SharedUtils.getInstance(SignUpActivity.this).setString(AppConstants.CLIENT_KEY,response.body().clientKey);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorCode) {
                Log.d(this.getClass().getCanonicalName(),t.toString());
            }
        });
        APIService.getInstance().execute(registration);

    }

}
