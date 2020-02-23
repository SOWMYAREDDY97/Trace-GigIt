package com.traceandgigit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class NavigationActivity2 extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;
    NavigationView navigationview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation2);
        mDrawerLayout = findViewById(R.id.drawer);
        mToogle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        navigationview = (NavigationView) findViewById(R.id.nav_view);
//        navigationview.setNavigationItemSelectedListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.drawermenu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

//        if(mToogle.onOptionsItemSelected(item)){
        if(item.getItemId()==R.id.profile_edit){
            Intent intent=new Intent(this,SignInActivity.class);
            startActivity(intent);

        }



//        }
        return true;
    }}
