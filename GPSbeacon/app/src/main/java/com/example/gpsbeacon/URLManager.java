package com.example.gpsbeacon;

import android.content.Context;
import android.content.SharedPreferences;

public class URLManager {

    private static final String PREFS_NAME = "URLPrefs";
    private static final String KEY_URL = "saved_url";

    private SharedPreferences sharedPreferences;

    public URLManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveURL(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_URL, url);
        editor.apply();
    }
    public String loadURL() {
        return sharedPreferences.getString(KEY_URL, null);
    }


}
