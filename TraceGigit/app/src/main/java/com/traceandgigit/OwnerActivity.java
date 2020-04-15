package com.traceandgigit;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.traceandgigit.services.CustomerNotificationService;
import com.traceandgigit.services.OwnerNotificationService;

public class OwnerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int FETCH_BOOKINS_JOB_ID = 1;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);
        ParseUser user = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        user.setACL(acl);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
        JobScheduler jobScheduler =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(FETCH_BOOKINS_JOB_ID,
                new ComponentName(this, OwnerNotificationService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(300)
                .build());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.profile_edit) {
            Intent ini = new Intent(this, SaloonProfileActivity.class);
            startActivity(ini);
        }else if(id == R.id.menuupload){
            Intent ini = new Intent(this, menulist.class);
            startActivity(ini);
        }else if(id == R.id.settings){
            Intent ini = new Intent(this, SettingsScreen.class);
            startActivity(ini);
        }else if(id == R.id.home){
            Intent ini = new Intent(this, OwnerActivity.class);
            startActivity(ini);
        }

        else if(id == R.id.contactus){
            Intent ini = new Intent(this,ContactUsActivity.class);
            startActivity(ini);
        }
        else if(id == R.id.logout){
            ParseUser.logOut();
            Intent ini = new Intent(this,SignInActivity.class);
            startActivity(ini);
            finish();
        }
        return false;
    }
}
