package com.example.saverio.myjournal.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.saverio.myjournal.data.MyJournalRepository;
import com.example.saverio.myjournal.data.database.PostEntry;

public class DetailActivityViewModel extends ViewModel {
    private LiveData<PostEntry> mPost;
    private LiveData<PostEntry[]> mRelatedPosts;

    public DetailActivityViewModel(MyJournalRepository repository, int id) {
        mPost = repository.getPostById(id);
        mRelatedPosts = repository.getRelatedPosts(id);
    }

    public LiveData<PostEntry> getPost() {
        return mPost;
    }
    public LiveData<PostEntry[]> getRelatedPosts() {
        return mRelatedPosts;
    }
}
