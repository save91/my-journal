package com.example.saverio.myjournal.data.network;

import android.support.annotation.Nullable;

import com.example.saverio.myjournal.data.database.PostEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProxyPostsJsonParser {
    private static final String OWM_ID = "id";
    private static final String OWM_TITLE = "title";
    private static final String OWM_BODY = "body";
    private static final String OWM_DATE = "date";
    private static final String OWM_FEATURE_MEDIA = "featured_media";
    private static final String OWM_THUMBNAIL_URL = "thumbnail_url";
    private static final String OWM_POST_THUMBNAIL_URL = "post_thumbnail_url";
    private static final String OWM_MEDIUM_URL = "medium_url";

    private PostEntry[] mapFromProxyResponseToPosts(String response) throws JSONException {
        PostEntry[] posts;
        JSONArray postsJsonArray = new JSONArray(response);

        posts = new PostEntry[postsJsonArray.length()];

        for (int i = 0; i < postsJsonArray.length(); i++) {
            /* Get the JSON object representing the post */
            JSONObject jsonObject = postsJsonArray.getJSONObject(i);
            PostEntry post = mapFromJsonObjectToPost(jsonObject);

            posts[i] = post;
        }

        return posts;
    }

    private PostEntry mapFromJsonObjectToPost(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString(OWM_ID);
        String title = jsonObject.getString(OWM_TITLE);
        String body = jsonObject.getString(OWM_BODY);
        int date = jsonObject.getInt(OWM_DATE);
        JSONObject JSONfeatureMedia = jsonObject.getJSONObject(OWM_FEATURE_MEDIA);
        String thumbnailUrl = JSONfeatureMedia.optString(OWM_THUMBNAIL_URL);
        String postThumbnailUrl = JSONfeatureMedia.optString(OWM_POST_THUMBNAIL_URL);
        String mediumUrl = JSONfeatureMedia.optString(OWM_MEDIUM_URL);

        PostEntry post = new PostEntry(
                Integer.parseInt(id),
                title,
                body,
                date,
                thumbnailUrl,
                postThumbnailUrl,
                mediumUrl
        );

        return post;
    }

    @Nullable
    public PostsResponse parse(final String postsJsonStr) throws JSONException {
        PostEntry[] posts = mapFromProxyResponseToPosts(postsJsonStr);

        return new PostsResponse(posts);
    }

}
