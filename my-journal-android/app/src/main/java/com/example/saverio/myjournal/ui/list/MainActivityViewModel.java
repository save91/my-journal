package com.example.saverio.myjournal.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.saverio.myjournal.data.MyJournalRepository;
import com.example.saverio.myjournal.data.database.PostEntry;

public class MainActivityViewModel extends ViewModel {
    private MyJournalRepository mRepository;

    public MainActivityViewModel(MyJournalRepository repository) {
        mRepository = repository;
    }

    public LiveData<PostEntry[]> getPosts(String server) {
        return mRepository.getPosts(server);
    }

    public void getMorePosts(String server) {
        mRepository.getMorePosts(server);
    }

    public LiveData<Boolean> isLoadingPosts() {
        return mRepository.isLoadingPosts();
    }

    public LiveData<Boolean> onError() {
        return mRepository.onError();
    }

}
