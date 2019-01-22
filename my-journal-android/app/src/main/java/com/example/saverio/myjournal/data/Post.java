package com.example.saverio.myjournal.data;

public class Post {
    private String id;
    private String title;
    private String body;
    private String thumbnailUrl;
    private String postThumbnailUrl;
    private String mediumUrl;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPostThumbnailUrl() {
        return postThumbnailUrl;
    }

    public void setPostThumbnailUrl(String postThumbnailUrl) {
        this.postThumbnailUrl = postThumbnailUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }
}
