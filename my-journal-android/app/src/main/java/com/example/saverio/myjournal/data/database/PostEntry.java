package com.example.saverio.myjournal.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "post")
public class PostEntry {

    @PrimaryKey
    private int id;
    private String title;
    private String body;
    private String date;
    private String thumbnailUrl;
    private String postThumbnailUrl;
    private String mediumUrl;

    public PostEntry(
            int id,
            String title,
            String body,
            String date,
            String thumbnailUrl,
            String postThumbnailUrl,
            String mediumUrl
    ) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
        this.thumbnailUrl = thumbnailUrl;
        this.postThumbnailUrl = postThumbnailUrl;
        this.mediumUrl = mediumUrl;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPostThumbnailUrl() {
        return postThumbnailUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }
}
