package com.example.saverio.myjournal.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.saverio.myjournal.utilities.InjectorUtils;

public class MyJournalIntentService extends IntentService {
    public static final String EXTRA_SERVER = "server";
    private static final String TAG = MyJournalIntentService.class.getSimpleName();

    public MyJournalIntentService() {
        super("MyJournalIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent service started");
        MyJournalNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        String server = intent.getStringExtra(EXTRA_SERVER);
        networkDataSource.fetchPosts(server);
    }
}
