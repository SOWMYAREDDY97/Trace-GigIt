package com.traceandgigit.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.traceandgigit.Constants;
import com.traceandgigit.Owner_NotoficationsActivity;
import com.traceandgigit.Utils;

public class OwnerNotificationService extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {

        ParseUser user = ParseUser.getCurrentUser();
        Log.e("JOB","OWNER RUNNING");
        Log.e("JOB",user.toString());
        Intent notificationIntent = new Intent(getApplicationContext(), Owner_NotoficationsActivity.class);

        if(!TextUtils.isEmpty((String)user.get(Constants.BOOKING_AT_10))){
            Utils.showNotification(getApplicationContext(),"Booking","There is a booking for you at 10 o Clock.",notificationIntent);
            user.put(Constants.BOOKING_AT_10, "");
            ParseQuery<ParseUser> customer = ParseUser.getQuery().whereEqualTo("objectId",user.get(Constants.BOOKING_AT_10));
            try {
                ParseUser custUser =  customer.get((String) user.get(Constants.BOOKING_AT_10));
                custUser.put(Constants.CUSTOMER_BOOKING,"booked");
                custUser.saveInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if(!TextUtils.isEmpty((String)user.get(Constants.BOOKING_AT_11))){
            Utils.showNotification(getApplicationContext(),"Booking","There is a booking for you at 11 o Clock.",notificationIntent);
            user.put(Constants.BOOKING_AT_11,"");
            ParseQuery<ParseUser> customer = ParseUser.getQuery().whereEqualTo("objectId",user.get(Constants.BOOKING_AT_11));
            try {
                ParseUser custUser =  customer.get((String) user.get(Constants.BOOKING_AT_11));
                custUser.put(Constants.CUSTOMER_BOOKING,"booked");
                custUser.saveInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        else if(!TextUtils.isEmpty((String)user.get(Constants.BOOKING_AT_12))){
            Utils.showNotification(getApplicationContext(),"Booking","There is a booking for you at 12 o Clock.",notificationIntent);
            user.put(Constants.BOOKING_AT_12,"");
            ParseQuery<ParseUser> customer = ParseUser.getQuery().whereEqualTo("objectId",user.get(Constants.BOOKING_AT_12));
            try {
                ParseUser custUser =  customer.get((String) user.get(Constants.BOOKING_AT_12));
                custUser.put(Constants.CUSTOMER_BOOKING,"booked");
                custUser.saveInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        else if(!TextUtils.isEmpty((String)user.get(Constants.BOOKING_AT_1))){
            Utils.showNotification(getApplicationContext(),"Booking","There is a booking for you at 1 o Clock.",notificationIntent);
            user.put(Constants.BOOKING_AT_1,"");
            ParseQuery<ParseUser> customer = ParseUser.getQuery().whereEqualTo("objectId",user.get(Constants.BOOKING_AT_1));
            try {
                ParseUser custUser =  customer.get((String) user.get(Constants.BOOKING_AT_1));
                custUser.put(Constants.CUSTOMER_BOOKING,"booked");
                custUser.saveInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
