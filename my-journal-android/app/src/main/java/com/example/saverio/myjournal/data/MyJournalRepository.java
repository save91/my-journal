package com.example.saverio.myjournal.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.saverio.myjournal.AppExecutors;
import com.example.saverio.myjournal.data.database.PostDao;
import com.example.saverio.myjournal.data.database.PostEntry;
import com.example.saverio.myjournal.data.network.MyJournalNetworkDataSource;

public class MyJournalRepository {
    private static final String TAG = MyJournalRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static int mCurrentPage;
    private static MyJournalRepository sInstance;
    private final PostDao mPostDao;
    private final MyJournalNetworkDataSource mMyJournalNetworkDataSource;
    private final AppExecutors mExecutors;

    private MyJournalRepository(
            PostDao postDao,
            MyJournalNetworkDataSource networkDataSource,
            AppExecutors executors
    ) {
        mPostDao = postDao;
        mMyJournalNetworkDataSource = networkDataSource;
        mExecutors = executors;

        LiveData<PostEntry[]> networkData = mMyJournalNetworkDataSource.getCurrentPosts();
        networkData.observeForever(newPostsFromNetwork -> {
            mExecutors.diskIO().execute(() -> {
                mPostDao.bulkInsert(newPostsFromNetwork);
                Log.d(TAG, "New values inserted");
            });
        });
    }

    public synchronized static MyJournalRepository getInstance(
            PostDao postDao,
            MyJournalNetworkDataSource networkDataSource,
            AppExecutors executors
    ) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new MyJournalRepository(postDao, networkDataSource, executors);
                }
            }
        }

        return  sInstance;
    }

    public LiveData<PostEntry> getPostById(int id) {
        return mPostDao.getPostById(id);
    }

    private void invalidateData() {
        mExecutors.diskIO().execute(() -> mPostDao.deleteAllPost());
    }

    public LiveData<PostEntry[]> getPosts(String server) {
        initializeData(server);
        return mPostDao.getPosts();
    }

    public synchronized void getMorePosts(String server) {
        mCurrentPage += 1;
        startFetchPostsService(server, mCurrentPage);
    }

    public LiveData<Boolean> isLoadingPosts() {
        return mMyJournalNetworkDataSource.isLoadingPosts();
    }

    public LiveData<Boolean> onError() {
        return mMyJournalNetworkDataSource.onError();
    }

    private synchronized void initializeData(String server) {
        invalidateData();
        mCurrentPage = 1;
        startFetchPostsService(server, mCurrentPage);
    }
    private void startFetchPostsService(String server, int page) {
        mMyJournalNetworkDataSource.startFetchPostsService(server, page);
    }
}
