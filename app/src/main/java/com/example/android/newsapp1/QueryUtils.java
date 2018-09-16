

package com.example.android.newsapp1;

import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import org.json.JSONObject;


    /**
     * Helper methods related to requesting and receiving earthquake data from USGS.
     */
    public class QueryUtils {
        private static final String TAG = QueryUtils.class.getSimpleName();

        /**
         * Create a private constructor because no one should ever create a {@link QueryUtils} object.
         * This class is only meant to hold static variables and methods, which can be accessed
         * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
         */
        private QueryUtils() {
        }

        private static URL createUrl(String requestedUrl) {
            URL url = null;
            try {
                url = new URL(requestedUrl);
            } catch (MalformedURLException moe) {
                Log.e(TAG, "createURL: Problem building URL", moe);
            }
            return url;
        }

        private static String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            //Check for null
            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            //Create the connection
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(TAG, "makeHTTPRequest: error Code: " + urlConnection.getResponseCode());
                }
            } catch (IOException ioe) {
                Log.e(TAG, "makeHTTPRequest: Couldn't retrieve JSON", ioe);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private static String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private static ArrayList<News> extractNewsFromJson(String jsonResponse) {
            //Check for JSON null
            if (TextUtils.isEmpty(jsonResponse)) {
                return null;
            }
            ArrayList<News> myNews = new ArrayList<>();
            try {
                JSONObject baseJSONResponse = new JSONObject(jsonResponse);

                JSONObject baseJSONResponseResult = baseJSONResponse.getJSONObject("response");

                JSONArray newsArray = baseJSONResponse.getJSONArray("results");
                for (int n = 0; n < newsArray.length(); n++) {
                    JSONObject currentNews = newsArray.getJSONObject(n);

                        //make items

                        String articleHeadline = currentNews.getString("webTitle");
                        String articleDate = currentNews.getString("webPublicationDate");
                        String articleSection = currentNews.getString("sectionId");
                        String articleAuthor = currentNews.getString("webTitle");

                       return new ArrayList<News>(articleAuthor,articleDate, articleHeadline, articleSection);

                    }

            } catch (JSONException je) {
                Log.e(TAG, "extractNewsFromJSON: Problem parsing result", je);
            }
            return myNews;
        }

        /**
         * Return a list of {@link News} objects that has been built up from
         * parsing a JSON response.
         */
        public static List<News> fetchNewsData(String requestedUrl) {
            URL url = createUrl(requestedUrl);
            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException ie) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(TAG, "fetchNewsData: Interrupted", ie);
            }
            List<News> myNews = extractNewsFromJson(jsonResponse);
            return myNews;


        }
    }