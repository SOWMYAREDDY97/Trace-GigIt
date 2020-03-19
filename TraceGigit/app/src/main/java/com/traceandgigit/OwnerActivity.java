package com.traceandgigit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

public class OwnerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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
        }else if(id == R.id.notifications){
            Intent ini = new Intent(this, Owner_NotoficationsActivity.class);
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
