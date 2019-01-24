package com.example.saverio.myjournal.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "post")
public class PostEntry {

    @PrimaryKey
    private int id;
    private String title;
    private String body;
    private String thumbnailUrl;
    private String postThumbnailUrl;
    private String mediumUrl;

    public PostEntry(
            int id,
            String title,
            String body,
            String thumbnailUrl,
            String postThumbnailUrl,
            String mediumUrl
    ) {
        this.id = id;
        this.title = title;
        this.body = body;
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
