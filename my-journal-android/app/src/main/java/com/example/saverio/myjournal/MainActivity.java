package com.example.saverio.myjournal;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saverio.myjournal.utilities.NetworkUtils;
import com.example.saverio.myjournal.utilities.ProxyPostsJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements PostAdapter.PostAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingProgressBar;

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

        loadPostsData();
    }

    private void loadPostsData() {
        showPostsDataView();

        new FetchPostsTask().execute();
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

    public class FetchPostsTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            URL postsUrl = NetworkUtils.buildPostsUrl();

            try {
                String jsonPostsResponse = NetworkUtils
                        .getResponseFromHttpUrl(postsUrl);

                String[] simpleJsonPostsData = ProxyPostsJsonUtils
                        .getSimplePostsStringsFromJson(jsonPostsResponse);

                return simpleJsonPostsData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] postsData) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (postsData != null) {
                showPostsDataView();
                mPostAdapter.setPostsData(postsData);
            } else {
                showErrorMessage();
            }
        }

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
            loadPostsData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
