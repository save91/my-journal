package com.example.saverio.myjournal.ui.list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.saverio.myjournal.data.MyJournalRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MyJournalRepository mRepository;

    public MainViewModelFactory(MyJournalRepository repository) {
        mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}
