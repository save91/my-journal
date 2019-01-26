package com.example.saverio.myjournal.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saverio.myjournal.R;
import com.example.saverio.myjournal.ui.settings.SettingsActivity;
import com.example.saverio.myjournal.data.database.PostEntry;
import com.example.saverio.myjournal.utilities.InjectorUtils;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private DetailActivityViewModel mViewModel;
    private TextView mTitleDisplay;
    private ImageView mPostThunbnail;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleDisplay = findViewById(R.id.tv_title);
        mPostThunbnail = findViewById(R.id.iv_post_thundbail);
        mWebView = findViewById(R.id.wv_body);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String id = intent.getStringExtra(Intent.EXTRA_TEXT);
            DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this, Integer.parseInt(id));
            mViewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel.class);
            mViewModel.getPost().observe(this, postEntry -> {
                if (postEntry != null) {
                    bindPostToUI(postEntry);
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
    }
}
