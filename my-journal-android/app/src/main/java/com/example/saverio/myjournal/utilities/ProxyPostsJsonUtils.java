package com.example.saverio.myjournal.utilities;

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
    public static String[] getSimplePostsStringsFromJson(String postsJsonStr)
            throws JSONException {


        final String OWM_TITLE = "title";

        /* String array to hold each post's info String */
        String[] parsedPostsData = null;

        JSONArray postsArray = new JSONArray(postsJsonStr);

        parsedPostsData = new String[postsArray.length()];

        for (int i = 0; i < postsArray.length(); i++) {
            /* Get the JSON object representing the post */
            JSONObject post = postsArray.getJSONObject(i);

            String title = post.getString(OWM_TITLE);

            parsedPostsData[i] = title;
        }

        return parsedPostsData;
    }

}