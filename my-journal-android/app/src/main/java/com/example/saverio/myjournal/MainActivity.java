package com.example.saverio.myjournal;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.saverio.myjournal.utilities.NetworkUtils;
import com.example.saverio.myjournal.utilities.ProxyPostsJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView mPostsTextView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        mPostsTextView = findViewById(R.id.tv_posts);
        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mLoadingProgressBar = findViewById(R.id.pb_loading);

        loadPostsData();
    }

    private void loadPostsData() {
        showPostsDataView();

        new FetchPostsTask().execute();
    }

    private void showPostsDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mPostsTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mPostsTextView.setVisibility(View.INVISIBLE);
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

                mPostsTextView.setText("");
                for(String post : postsData) {
                    mPostsTextView.append(post + "\n\n\n");
                }

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
            mPostsTextView.setText("");
            loadPostsData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
