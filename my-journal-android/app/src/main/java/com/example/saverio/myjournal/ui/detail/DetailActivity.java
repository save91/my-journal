package com.example.saverio.myjournal.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.saverio.myjournal.R;
import com.example.saverio.myjournal.ui.common.PostAdapter;
import com.example.saverio.myjournal.ui.settings.SettingsActivity;
import com.example.saverio.myjournal.data.database.PostEntry;
import com.example.saverio.myjournal.utilities.InjectorUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsListener;
import com.outbrain.OBSDK.Outbrain;
import com.outbrain.OBSDK.Viewability.OBTextView;
import com.squareup.picasso.Picasso;

import tv.teads.sdk.android.InReadAdView;

public class DetailActivity extends AppCompatActivity implements
        PostAdapter.PostAdapterOnClickHandler {
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String OBDemoWidgetID3 = "SDK_3";
    private static final String OBFooterWidgetID = OBDemoWidgetID3;

    private DetailActivityViewModel mViewModel;
    private TextView mTitleDisplay;
    private ImageView mPostThunbnail;
    private WebView mWebView;
    private WebView mWebView2;
    private InReadAdView mTeadsAdView;
    private AdView mAdView;
    private AdView mAdView2;
    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private LinearLayout classicRecommendationsContainer;


    private OBRecommendationsResponse footerRecommendations;
    private boolean didGetRecommendations = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleDisplay = findViewById(R.id.tv_title);
        mPostThunbnail = findViewById(R.id.iv_post_thundbail);
        mWebView = findViewById(R.id.wv_body);
        mWebView2 = findViewById(R.id.wv_body2);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        Outbrain.register(this, getString(R.string.partner_key));
        Outbrain.setTestMode(true); // Skipping all billing, statistics, information gathering, and all other action mechanisms.
        classicRecommendationsContainer =  findViewById(R.id.classic_recommendations_container);
        fetchRecommendationsForFooter();

        mTeadsAdView = findViewById(R.id.teads_ad_view);
        mTeadsAdView.load();

        mAdView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView2 = findViewById(R.id.ad_view2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);

        mRecyclerView = findViewById(R.id.recyclerview_posts);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPostAdapter = new PostAdapter(this);
        mRecyclerView.setAdapter(mPostAdapter);

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

            mViewModel.getRelatedPosts().observe(this, posts -> {
                if (posts != null) {
                    mPostAdapter.setPostsData(posts);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        mTeadsAdView.clean();
        super.onDestroy();
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
        String html = "<link rel=\"stylesheet\" href=\"style.css\">" + postEntry.getBody();
        mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        mWebView2.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);

        Uri uri = Uri.parse(postEntry.getMediumUrl());
        Picasso.with(mPostThunbnail.getContext()).load(uri)
                .into(mPostThunbnail);
    }

    private void fetchRecommendationsForFooter() {
        OBRequest request = new OBRequest();
        request.setUrl("https://www.example.com");
        request.setWidgetId(OBFooterWidgetID);

        Outbrain.fetchRecommendations(request, new RecommendationsListener() {
            @Override
            public void onOutbrainRecommendationsSuccess(OBRecommendationsResponse recommendations) {
                didGetRecommendations = true;
                setRecommendationsForFooter(recommendations);
            }

            @Override
            public void onOutbrainRecommendationsFailure(Exception ex) {
                Log.e("OB", "failure = " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private void setRecommendationsForFooter(final OBRecommendationsResponse recs) {
        footerRecommendations = recs;
        LayoutInflater inflater = getLayoutInflater();

        // Remove all child views if exists
        classicRecommendationsContainer.removeAllViews();

        // Adding the header of the footer
        if (recs.getAll().size() > 0) {
            View child = inflater.inflate(R.layout.outbrain_classic_recommendation_header_view, classicRecommendationsContainer, false);
            classicRecommendationsContainer.addView(child);

            // Register OBTextView with Outbrain SDK
            OBTextView obTextView = (OBTextView)child.findViewById(R.id.widget_title_view);
            Outbrain.registerOBTextView(obTextView, OBFooterWidgetID, "https://www.example.com");
        }
        else {
            return;
        }

        FrameLayout whatIsWrapper = (FrameLayout) classicRecommendationsContainer.findViewById(R.id.recommended_by_wrapper);
        whatIsWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "click on outbrain");
            }
        });

        // Adding the actual recommandations
        for (final OBRecommendation item : recs.getAll()) {
            addClassicRecommendationView(item, inflater);
        }
    }

    private void addClassicRecommendationView(final OBRecommendation rec, LayoutInflater inflater) {
        View child = inflater.inflate(R.layout.outbrain_classic_recommendation_view, classicRecommendationsContainer, false);

        TextView title = child.findViewById(R.id.outbrain_layouts_title_text_label);
        TextView desc = child.findViewById(R.id.outbrain_layouts_author_text_label);
        ImageView disclosureImageView = child.findViewById(R.id.outbrain_rec_disclosure_image_view);
        ImageView imageView = child.findViewById(R.id.rec_image_view);
        RelativeLayout container = child.findViewById(R.id.outbrain_classic_recommendation_view_container);

        title.setText(rec.getContent());
        String text = String.format(getResources().getString(R.string.outbrain_source_name), rec.getSourceName());
        desc.setText(text);

        //Load image
        if (imageView != null) {
            Picasso.with(this).load(rec.getThumbnail().getUrl()).into(imageView);
        }
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "outbrain click");
            }
        });

        if (rec.isPaid() && rec.shouldDisplayDisclosureIcon()) {
            // Set the RTB disclosure icon image
            Picasso.with(this).load(rec.getDisclosure().getIconUrl()).into(disclosureImageView);
            disclosureImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String clickURL = rec.getDisclosure().getClickUrl();
                    Log.d(TAG, "outbrain click");
                }
            });
        }

        classicRecommendationsContainer.addView(child);
    }

    @Override
    public void onClick(String id) {
        Context context = this;
        Class destinationActivity = DetailActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(Intent.EXTRA_TEXT, id);

        startActivity(intent);
    }
}
