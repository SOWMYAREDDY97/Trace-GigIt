package com.example.tracegigit.retrofit;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tracegigit.R;
import com.traceandgigit.retrofit.RetrofitClientInstance;


/*
    Splash Screen, that is opened when the app launches.
    This is cause it is Declared as Launcher Activity in the Manifest
 */
public class SplashActivity extends Activity {

    ImageView appLogo;
    private boolean shouldNavigateToMain = true;
    private EditText scheme, host;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appLogo = findViewById(R.id.appLogo);
        appLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                shouldNavigateToMain = false;
                showConfigUI();
                return true;
            }
        });
        checkDeviceRegistration();
        startTimerToLaunchMain();
    }

    private void checkDeviceRegistration(){


    }

    //Method that shows Config UI if the Image in SplashScreen is Long Clicked
    private void showConfigUI(){

        LinearLayout dynamicHostLayout = findViewById(R.id.dynamicHostLayout);
        dynamicHostLayout.setVisibility(View.VISIBLE);

        scheme = findViewById(R.id.scheme);
        host = findViewById(R.id.host);

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!scheme.getText().toString().isEmpty() && !host.getText().toString().isEmpty()){
                    RetrofitClientInstance.BASE_SCHEME = scheme.getText().toString();
                    RetrofitClientInstance.BASE_HOST = host.getText().toString();
                }else{
                    Toast.makeText(SplashActivity.this,"Please enter Scheme and Host",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    private void startTimerToLaunchMain() {
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(shouldNavigateToMain){
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.postDelayed(r, 2500);
    }


}
