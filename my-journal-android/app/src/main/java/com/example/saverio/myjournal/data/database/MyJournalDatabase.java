package com.example.saverio.myjournal.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {PostEntry.class}, version = 2, exportSchema = false)
public abstract class MyJournalDatabase extends RoomDatabase {
    public abstract PostDao postDao();

    private static final String DATABASE_NAME = "my-journal";
    private static final Object LOCK = new Object();
    private static volatile MyJournalDatabase sInstance;

    public static MyJournalDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            MyJournalDatabase.class, MyJournalDatabase.DATABASE_NAME)
                                .fallbackToDestructiveMigration()
                                .build();
                }
            }
        }

        return sInstance;
    }
}
