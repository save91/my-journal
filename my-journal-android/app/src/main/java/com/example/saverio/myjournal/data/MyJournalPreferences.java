package com.example.saverio.myjournal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.saverio.myjournal.R;

public class MyJournalPreferences {

    public static String getServer(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForLocation = context.getString(R.string.pref_server_key);
        String defaultLocation = context.getString(R.string.pref_server_default);

        return prefs.getString(keyForLocation, defaultLocation);
    }
}
