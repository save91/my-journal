package com.example.saverio.myjournal.data.network;

import android.support.annotation.NonNull;

import com.example.saverio.myjournal.data.database.PostEntry;

public class PostsResponse {
    @NonNull
    private final PostEntry[] mPosts;

    public PostsResponse(@NonNull final PostEntry[] posts) {
        mPosts = posts;
    }

    public PostEntry[] getPosts() {
        return mPosts;
    }
}
