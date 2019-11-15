package com.traceandgigit.requests;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Assert;

public class SharedUtils {

    private SharedPreferences sharedPreferences;
    private final String FILE_NAME = "shared_pref";


    public static SharedUtils _self;

    private SharedUtils(Context mContext){
        sharedPreferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

    }

    public static SharedUtils getInstance(Context mContext) {
        if (_self == null) {
            _self = new SharedUtils(mContext);
        }
        return _self;
    }

    public void setString(final String key, final String value) {
        Assert.assertNotNull(key);
        Assert.assertNotNull(value);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        Assert.assertNotNull(key);
        return sharedPreferences.getString(key, null);
    }

    public long getLong(String key) {
        Assert.assertNotNull(key);
        return sharedPreferences.getLong(key, 0);
    }

    public long getLong(String key, long value) {
        Assert.assertNotNull(key);
        return sharedPreferences.getLong(key, value);
    }

    public void setLong(final String key, final long value) {
        Assert.assertNotNull(key);
        Assert.assertNotNull(value);
        sharedPreferences.edit().putLong(key, value).commit();

    }


    public void setInt(final String key, final int value) {
        Assert.assertNotNull(key);
        Assert.assertNotNull(value);
        sharedPreferences.edit().putInt(key, value).apply();

    }

    public int getInt(final String key, final int defValue) {
        Assert.assertNotNull(key);
        return sharedPreferences.getInt(key, defValue);

    }

    public void setBoolean(final String key, final boolean value) {
        Assert.assertNotNull(key);
        Assert.assertNotNull(value);
        sharedPreferences.edit().putBoolean(key, value).apply();

    }

    public boolean getBoolean(final String key, boolean defaultValue) {
        Assert.assertNotNull(key);
        return sharedPreferences.getBoolean(key, defaultValue);

    }



}
