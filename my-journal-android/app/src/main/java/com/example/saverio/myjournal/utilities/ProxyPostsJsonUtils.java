package com.example.saverio.myjournal.utilities;

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
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Post[] getSimplePostsStringsFromJson(String postsJsonStr)
            throws JSONException {

        final String OWM_ID = "id";
        final String OWM_TITLE = "title";
        final String OWM_FEATURE_MEDIA = "featured_media";
        final String OWM_THUMBNAIL_URL = "thumbnail_url";

        /* String array to hold each post's info String */
        Post[] parsedPostsData = null;

        JSONArray postsArray = new JSONArray(postsJsonStr);

        parsedPostsData = new Post[postsArray.length()];

        for (int i = 0; i < postsArray.length(); i++) {
            /* Get the JSON object representing the post */
            JSONObject JSONpost = postsArray.getJSONObject(i);

            String id = JSONpost.getString(OWM_ID);
            String title = JSONpost.getString(OWM_TITLE);
            JSONObject JSONfeatureMedia = JSONpost.getJSONObject(OWM_FEATURE_MEDIA);
            String thumbnailUrl = JSONfeatureMedia.getString(OWM_THUMBNAIL_URL);

            Post post = new Post();
            post.setId(id);
            post.setTitle(title);
            post.setThumbnailUrl(thumbnailUrl);


            parsedPostsData[i] = post;
        }

        return parsedPostsData;
    }

}