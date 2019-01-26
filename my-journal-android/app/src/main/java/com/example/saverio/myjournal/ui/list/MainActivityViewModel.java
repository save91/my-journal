package com.example.saverio.myjournal.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.saverio.myjournal.data.MyJournalRepository;
import com.example.saverio.myjournal.data.database.PostEntry;

public class MainActivityViewModel extends ViewModel {
    private LiveData<PostEntry[]> mPosts;

    public MainActivityViewModel(MyJournalRepository repository) {
        mPosts = repository.getPosts();
    }

    public LiveData<PostEntry[]> getPosts() {
        return mPosts;
    }
}
