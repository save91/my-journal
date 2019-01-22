package com.example.saverio.myjournal.utilities;

import com.example.saverio.myjournal.R;
import com.example.saverio.myjournal.data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class ProxyPostsJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param postsJsonStr JSON response from server
     *
     * @return Array of Post
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Post[] getSimplePostsStringsFromJson(String postsJsonStr)
            throws JSONException {

        /* String array to hold each post's info String */
        Post[] parsedPostsData = null;

        JSONArray postsArray = new JSONArray(postsJsonStr);

        parsedPostsData = new Post[postsArray.length()];

        for (int i = 0; i < postsArray.length(); i++) {
            /* Get the JSON object representing the post */
            JSONObject jsonObject = postsArray.getJSONObject(i);
            Post post = mapFromJsonObjectToPost(jsonObject);

            parsedPostsData[i] = post;
        }

        return parsedPostsData;
    }

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param postJsonStr JSON response from server
     *
     * @return A single Post
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Post getSimplePostStringsFromJson(String postJsonStr) throws JSONException{
        JSONObject jsonObject = new JSONObject(postJsonStr);

        return mapFromJsonObjectToPost(jsonObject);
    }

    private static Post mapFromJsonObjectToPost(JSONObject jsonObject) throws JSONException {
        final String OWM_ID = "id";
        final String OWM_TITLE = "title";
        final String OWM_BODY = "body";
        final String OWM_FEATURE_MEDIA = "featured_media";
        final String OWM_THUMBNAIL_URL = "thumbnail_url";
        final String OWM_POST_THUMBNAIL_URL = "post_thumbnail_url";
        final String OWM_MEDIUM_URL = "medium_url";

        String id = jsonObject.getString(OWM_ID);
        String title = jsonObject.getString(OWM_TITLE);
        String body = jsonObject.getString(OWM_BODY);
        JSONObject JSONfeatureMedia = jsonObject.getJSONObject(OWM_FEATURE_MEDIA);
        String thumbnailUrl = JSONfeatureMedia.optString(OWM_THUMBNAIL_URL);
        String postThumbnailUrl = JSONfeatureMedia.optString(OWM_POST_THUMBNAIL_URL);
        String mediumUrl = JSONfeatureMedia.optString(OWM_MEDIUM_URL);

        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setBody(body);
        post.setThumbnailUrl(thumbnailUrl);
        post.setPostThumbnailUrl(postThumbnailUrl);
        post.setMediumUrl(mediumUrl);

        return post;
    }

}