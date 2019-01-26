package com.example.saverio.myjournal.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.saverio.myjournal.data.MyJournalRepository;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MyJournalRepository mRepository;
    private final int mId;

    public DetailViewModelFactory(MyJournalRepository repository, int id) {
        mRepository = repository;
        mId = id;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailActivityViewModel(mRepository, mId);
    }
}
