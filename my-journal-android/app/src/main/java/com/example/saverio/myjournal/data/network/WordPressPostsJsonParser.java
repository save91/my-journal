package com.example.saverio.myjournal.data.network;

import android.support.annotation.Nullable;
import android.text.Html;

import com.example.saverio.myjournal.data.database.PostEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WordPressPostsJsonParser {
    private static final String OWM_ID = "id";
    private static final String OWM_TITLE = "title";
    private static final String OWM_BODY = "content";
    private static final String OWM_FEATURE_MEDIA = "featured_media";
    private static final String OWM_THUMBNAIL_URL = "thumbnail_url";
    private static final String OWM_POST_THUMBNAIL_URL = "post_thumbnail_url";
    private static final String OWM_MEDIUM_URL = "medium_url";

    private static final String OWM_RENDERED = "rendered";

    private PostEntry[] mapFromWordPressResponseToPosts(String response) throws JSONException {
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

        JSONObject jsonTitle = jsonObject.getJSONObject(OWM_TITLE);
        String title = Html.fromHtml(jsonTitle.getString(OWM_RENDERED)).toString();

        JSONObject jsonBody = jsonObject.getJSONObject(OWM_BODY);
        String body = jsonBody.getString(OWM_RENDERED);

        JSONObject jsonEmbedded = jsonObject.getJSONObject("_embedded");
        JSONArray jsonArrayMedia = jsonEmbedded.getJSONArray("wp:featuredmedia");
        JSONObject jsonMedia = jsonArrayMedia.getJSONObject(0);
        JSONObject jsonMediaDetails = jsonMedia.getJSONObject("media_details");
        JSONObject jsonSizes = jsonMediaDetails.getJSONObject("sizes");

        JSONObject jsonThumbnail = jsonSizes.optJSONObject("thumbnail");
        String thumbnailUrl = "";
        if (jsonThumbnail != null) {
            thumbnailUrl = jsonThumbnail.getString("source_url");
        }

        JSONObject jsonPostThumbnail = jsonSizes.optJSONObject("post-thumbnail");
        String postThumbnailUrl = "";
        if (jsonPostThumbnail != null) {
            postThumbnailUrl = jsonPostThumbnail.getString("source_url");
        }

        JSONObject jsonMedium = jsonSizes.optJSONObject("medium");
        String mediumUrl = "";
        if (jsonMedium != null) {
            mediumUrl = jsonMedium.getString("source_url");
        }

        PostEntry post = new PostEntry(
                Integer.parseInt(id),
                title,
                body,
                thumbnailUrl,
                postThumbnailUrl,
                mediumUrl
        );

        return post;
    }

    @Nullable
    public PostsResponse parse(final String postsJsonStr) throws JSONException {
        PostEntry[] posts = mapFromWordPressResponseToPosts(postsJsonStr);

        return new PostsResponse(posts);
    }

}
