package com.example.saverio.myjournal.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.saverio.myjournal.AppExecutors;
import com.example.saverio.myjournal.data.database.PostEntry;

import java.net.URL;

public class MyJournalNetworkDataSource {
    private static final String TAG = MyJournalNetworkDataSource.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static MyJournalNetworkDataSource sInstance;
    private final Context mContext;

    private final MutableLiveData<PostEntry[]> mDownloadedPosts;
    private final AppExecutors mExecutors;

    private MyJournalNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedPosts = new MutableLiveData<>();
    }

    public static MyJournalNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MyJournalNetworkDataSource(context.getApplicationContext(), executors);
                Log.d(TAG, "Made new network data source");
            }
        }

        return sInstance;
    }

    public LiveData<PostEntry[]> getCurrentPosts() {
        return mDownloadedPosts;
    }

    public void startFetchWeatherService() {
        Intent intentToFetch = new Intent(mContext, MyJournalIntentService.class);
        mContext.startService(intentToFetch);
        Log.d(TAG, "Service created");
    }

    public void fetchPosts(String server) {
        mExecutors.networkIO().execute(() -> {
            try {
                URL postsRequestUrl = NetworkUtils.buildPostsUrl(server);

                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(postsRequestUrl);

                PostsResponse response = new ProxyPostsJsonParser().parse(jsonResponse);
                Log.d(TAG, "JSON Parsing finished");

                if (response != null && response.getPosts().length != 0) {
                    Log.d(TAG, "JSON not null and has " + response.getPosts().length
                            + " values");
                    // When you are off of the main thread and want to update LiveData, use postValue.
                    // It posts the update to the main thread.
                    mDownloadedPosts.postValue(response.getPosts());

                    // If the code reaches this point, we have successfully performed our sync
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
