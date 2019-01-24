package com.example.saverio.myjournal.ui.detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.saverio.myjournal.data.database.PostEntry;

public class DetailActivityViewModel extends ViewModel {
    private MutableLiveData<PostEntry> mPost;

    public DetailActivityViewModel() {
        mPost = new MutableLiveData<>();
    }

    public MutableLiveData<PostEntry> getPost() {
        return mPost;
    }

    public void setPost(PostEntry postEntry) {
        mPost.postValue(postEntry);
    }
}
