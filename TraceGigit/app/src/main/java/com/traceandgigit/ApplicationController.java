package com.traceandgigit;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.traceandgigit.requests.SharedUtils;

public class ApplicationController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setUpParseSDK();
    }


    private void setUpParseSDK() {
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.appID))
                // if desired
                .clientKey(getString(R.string.clientKey))
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }

    public static ParseUser getCurrentUser(Context mContext){
        ParseUser user = ParseUser.getCurrentUser();
        if(user == null){
            try {
                user = ParseUser.become(SharedUtils.getInstance(mContext).getString(Constants.SESSION_TOKEN));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
