package com.example.saverio.myjournal.ui.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.saverio.myjournal.ui.detail.DetailActivity;
import com.example.saverio.myjournal.R;
import com.example.saverio.myjournal.ui.settings.SettingsActivity;
import com.example.saverio.myjournal.data.MyJournalPreferences;
import com.example.saverio.myjournal.utilities.InjectorUtils;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements
        PostAdapter.PostAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private TextView mErrorMessageTextView;
    private MainActivityViewModel mViewModel;
    private SwipeRefreshLayout mSwipe;
    private Boolean mLoading;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mRecyclerView = findViewById(R.id.recyclerview_posts);
        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mSwipe = findViewById(R.id.swipe);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String server = MyJournalPreferences.getServer(MainActivity.this);
                mViewModel.getPosts(server);
            }
        });

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int totalItemCount = layoutManager.getItemCount();
                        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                        if (!mLoading && totalItemCount <= (lastVisibleItem + 3)) {
                            String server = MyJournalPreferences.getServer(MainActivity.this);
                            mViewModel.getMorePosts(server);
                        }
                    }
                }
        );

        /*
         * The PostAdapter is responsible for linking our post data with the Views that
         * will end up displaying our post data.
         */
        mPostAdapter = new PostAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mPostAdapter);

        MainViewModelFactory factory = InjectorUtils.provideMainViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);
        String server = MyJournalPreferences.getServer(MainActivity.this);
        mViewModel.getPosts(server).observe(this, posts -> {
            Log.d(TAG, "There are " + posts.length + " posts");
            mPostAdapter.setPostsData(posts);
            if (posts.length > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
            }
        });

        mViewModel.isLoadingPosts().observe(this, loading -> {
            if (loading == null) return;
            mLoading = loading;
            mSwipe.setRefreshing(mLoading);
        });

        mViewModel.onError().observe(this, error -> {
            if (error == null) return;
            if (error) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mErrorMessageTextView.setVisibility(View.VISIBLE);
            } else {
                mErrorMessageTextView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        processExtraData();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            String server = MyJournalPreferences.getServer(MainActivity.this);
            mViewModel.getPosts(server);
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
    public void onClick(String id) {
        openPost(id);
    }

    private void openPost(String id) {
        Context context = this;
        Class destinationActivity = DetailActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(Intent.EXTRA_TEXT, id);

        startActivity(intent);
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
            String server = MyJournalPreferences.getServer(MainActivity.this);
            mViewModel.getPosts(server);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void processExtraData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("id")) {
            String id = extras.getString("id");
            openPost(id);
        }
    }

}
