package com.example.saverio.myjournal.ui.detail;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saverio.myjournal.R;
import com.example.saverio.myjournal.SettingsActivity;
import com.example.saverio.myjournal.data.MyJournalPreferences;
import com.example.saverio.myjournal.data.Post;
import com.example.saverio.myjournal.data.database.PostEntry;
import com.example.saverio.myjournal.utilities.NetworkUtils;
import com.example.saverio.myjournal.utilities.ProxyPostsJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Post> {

    private static final int FORECAST_LOADER_ID = 23;
    private static final String TAG = DetailActivity.class.toString();
    private static final String KEY_ID = "id";

    private DetailActivityViewModel mViewModel;
    private TextView mTitleDisplay;
    private ImageView mPostThunbnail;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mViewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);
        mViewModel.getPost().observe(this, postEntry -> {
            if (postEntry != null) {
                bindPostToUI(postEntry);
            }
        });
        mTitleDisplay = findViewById(R.id.tv_title);
        mPostThunbnail = findViewById(R.id.iv_post_thundbail);
        mWebView = findViewById(R.id.wv_body);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String id = intent.getStringExtra(Intent.EXTRA_TEXT);
            int loaderId = FORECAST_LOADER_ID;
            LoaderManager.LoaderCallbacks<Post> callback = DetailActivity.this;
            Bundle bundleForLoader = new Bundle();
            bundleForLoader.putString(KEY_ID, id);
            getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            sharePost();
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sharePost() {
        String toShare = mTitleDisplay.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, toShare);
        sendIntent.setType("text/plain");

        startActivity(sendIntent);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Post> onCreateLoader(int i, @Nullable Bundle bundle) {
        final String id = bundle.getString(KEY_ID);
        return new AsyncTaskLoader<Post>(this) {
            Post mPostData = null;

            @Override
            protected void onStartLoading() {
                if (mPostData != null) {
                    deliverResult(mPostData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Post loadInBackground() {
                String server = MyJournalPreferences.getServer(DetailActivity.this);
                URL postUrl = NetworkUtils.buildPostUrl(server, id);

                try {
                    String jsonPostResponse = NetworkUtils
                            .getResponseFromHttpUrl(postUrl);

                    Post simpleJsonPostData = ProxyPostsJsonUtils
                            .getSimplePostStringsFromJson(jsonPostResponse);

                    return simpleJsonPostData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable Post data) {
                mPostData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Post> loader, Post post) {
        mTitleDisplay.setText(post.getTitle());

        String html = "<link rel=\"stylesheet\" href=\"style.css\">" + post.getBody();
        mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);

        Uri uri = Uri.parse(post.getMediumUrl());
        Picasso.with(mPostThunbnail.getContext()).load(uri)
                .into(mPostThunbnail);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Post> loader) {

    }

    private void bindPostToUI(PostEntry postEntry) {
        Log.d(TAG, "new post " + postEntry.getTitle());
    }
}
