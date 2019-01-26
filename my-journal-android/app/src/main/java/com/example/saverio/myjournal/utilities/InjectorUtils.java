package com.example.saverio.myjournal.utilities;

import android.content.Context;

import com.example.saverio.myjournal.AppExecutors;
import com.example.saverio.myjournal.data.MyJournalRepository;
import com.example.saverio.myjournal.data.database.MyJournalDatabase;
import com.example.saverio.myjournal.data.network.MyJournalNetworkDataSource;
import com.example.saverio.myjournal.ui.detail.DetailViewModelFactory;
import com.example.saverio.myjournal.ui.list.MainViewModelFactory;

public class InjectorUtils {

    public static MyJournalRepository provideRepository(Context context) {
        MyJournalDatabase database = MyJournalDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        MyJournalNetworkDataSource networkDataSource =
                MyJournalNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return MyJournalRepository.getInstance(database.postDao(), networkDataSource, executors);
    }

    public static MyJournalNetworkDataSource provideNetworkDataSource(Context context) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return MyJournalNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, int id) {
        MyJournalRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, id);
    }

    public static MainViewModelFactory provideMainViewModelFactory(Context context) {
        MyJournalRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }
}