package com.example.saverio.myjournal.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.saverio.myjournal.utilities.InjectorUtils;

public class MyJournalIntentService extends IntentService {
    private static final String TAG = MyJournalIntentService.class.getSimpleName();

    public MyJournalIntentService() {
        super("MyJournalIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent service started");
        MyJournalNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchPosts("http://10.0.2.2");
    }
}
