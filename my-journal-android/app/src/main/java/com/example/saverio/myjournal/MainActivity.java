package com.example.saverio.myjournal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.saverio.myjournal.data.MyJournalPreferences;
import com.example.saverio.myjournal.data.Post;
import com.example.saverio.myjournal.utilities.NetworkUtils;
import com.example.saverio.myjournal.utilities.ProxyPostsJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        PostAdapter.PostAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Post[]>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int FORECAST_LOADER_ID = 22;
    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingProgressBar;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        mRecyclerView = findViewById(R.id.recyclerview_posts);
        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The PostAdapter is responsible for linking our post data with the Views that
         * will end up displaying our post data.
         */
        mPostAdapter = new PostAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mPostAdapter);
        mLoadingProgressBar = findViewById(R.id.pb_loading);

        int loaderId = FORECAST_LOADER_ID;

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<Post[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<Post[]> callback = MainActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader = null;

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED == true) {
            mPostAdapter.setPostsData(null);
            invalidateData();
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    @Override
    public void onClick(String title) {
        Context context = this;
        Class destinationActivity = DetailActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(Intent.EXTRA_TEXT, title);

        startActivity(intent);
    }

    private void showPostsDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Post[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<Post[]>(this) {
            Post[] mPostsData = null;

            @Override
            protected void onStartLoading() {
                showPostsDataView();
                if (mPostsData != null) {
                    deliverResult(mPostsData);
                } else {
                    mLoadingProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public Post[] loadInBackground() {
                String server = MyJournalPreferences.getServer(MainActivity.this);
                URL postsUrl = NetworkUtils.buildPostsUrl(server);

                try {
                    String jsonPostsResponse = NetworkUtils
                            .getResponseFromHttpUrl(postsUrl);

                    Post[] simpleJsonPostsData = ProxyPostsJsonUtils
                            .getSimplePostsStringsFromJson(jsonPostsResponse);

                    return simpleJsonPostsData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable Post[] data) {
                mPostsData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Post[]> loader, Post[] data) {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        mPostAdapter.setPostsData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showPostsDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Post[]> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.posts, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mPostAdapter.setPostsData(null);
            invalidateData();
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void invalidateData() {
        mPostAdapter.setPostsData(null);
    }

}
