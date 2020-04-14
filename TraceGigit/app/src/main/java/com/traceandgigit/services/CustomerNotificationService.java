package com.traceandgigit.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.parse.ParseUser;
import com.traceandgigit.Constants;
import com.traceandgigit.CustomerNotificationActivity;
import com.traceandgigit.Owner_NotoficationsActivity;
import com.traceandgigit.Utils;

public class CustomerNotificationService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        if(ParseUser.getCurrentUser() != null){
            Intent notificationIntent = new Intent(getApplicationContext(), CustomerNotificationActivity.class);

            if(!TextUtils.isEmpty((String)ParseUser.getCurrentUser().get(Constants.CUSTOMER_BOOKING))){
                Utils.showNotification(getApplicationContext(),"Booking","Your Booking has been accepted",notificationIntent);
            }
        }
        Log.e("JOB","CUSTOMER RUNNING");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
