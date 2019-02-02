package com.example.saverio.myjournal.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.saverio.myjournal.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_general);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (p instanceof EditTextPreference) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /* Register the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Unregister the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (preference instanceof EditTextPreference) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
            if (preference instanceof SwitchPreference) {
                boolean isOn = sharedPreferences.getBoolean(key, false);
                if (isOn) {
                    // The preference key matches the following key for the associated instructor in
                    // FCM. For example, the key for Lyla is key_lyla (as seen in
                    // following_squawker.xml). The topic for Lyla's messages is /topics/key_lyla

                    // Subscribe
                    FirebaseMessaging.getInstance().subscribeToTopic(key);
                } else {
                    // Un-subscribe
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                }
            }
        }
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        preference.setSummary(stringValue);
    }
}
