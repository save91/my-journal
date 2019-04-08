package com.example.saverio.myjournal.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.example.saverio.myjournal.R;
import com.example.saverio.myjournal.ui.settings.SettingsActivity;
import com.example.saverio.myjournal.data.database.PostEntry;
import com.example.saverio.myjournal.utilities.InjectorUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private DetailActivityViewModel mViewModel;
    private TextView mTitleDisplay;
    private ImageView mPostThunbnail;
    private AdView mAdView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleDisplay = findViewById(R.id.tv_title);
        mPostThunbnail = findViewById(R.id.iv_post_thundbail);
        mWebView = findViewById(R.id.wv_body);

        mAdView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            int id;
            try {
                id =  Integer.parseInt(intent.getStringExtra(Intent.EXTRA_TEXT));
            } catch (Exception e) {
                id = 0;
            }
            DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this, id);
            mViewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel.class);
            mViewModel.getPost().observe(this, postEntry -> {
                if (postEntry != null) {
                    bindPostToUI(postEntry);
                } else {
                    showErrorMessage();
                }
            });
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

    private void bindPostToUI(PostEntry postEntry) {
        if (postEntry == null) {
            return;
        }

        mTitleDisplay.setText(postEntry.getTitle());
        String html = "<link rel=\"stylesheet\" href=\"style.css\">" +
                "<link href=\"https://fonts.googleapis.com/css?family=Source+Sans+Pro\" rel=\"stylesheet\">" +
                postEntry.getBody();
        mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);

        Uri uri = Uri.parse(postEntry.getMediumUrl());
        Picasso.with(mPostThunbnail.getContext()).load(uri)
                .into(mPostThunbnail);
    }

    private void showErrorMessage() {
        mTitleDisplay.setText(R.string.post_not_found);
    }
}
