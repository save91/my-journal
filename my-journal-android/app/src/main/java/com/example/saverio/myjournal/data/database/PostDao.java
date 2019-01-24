package com.example.saverio.myjournal.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(PostEntry... posts);

    @Query("SELECT * FROM post WHERE id = :id")
    PostEntry getPostById(int id);
}
