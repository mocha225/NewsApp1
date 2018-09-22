package com.example.android.newsapp1;

import android.text.TextUtils;
import android.util.Log;
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
     * Helper methods related to requesting and receiving news data from USGS.
     */
    public class QueryUtils {

        //Tag for the log messages.
        private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private QueryUtils() {
    }
        /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

//Query the Guardian dataset and return a list of {@link News} objects.
        public static List<News> fetchNewsData(String requestUrl) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Create URL object.
            URL url = createUrl(requestUrl);

            //Perform HTTP request to the URL and receive a JSON response back.  If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException ie) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "fetchNewsData: Problem making the HTTP request", ie);
            }

            //Extract relevent fields from the JSON response and create a list of {@link News}s
            List<News> collectionNews = extractNewsFromJson(jsonResponse);
            Log.v("fetch method", "yes");

            //Returns the list of {@link News}s
            return collectionNews;
        }

        //Returns new URL object from the given string URL.
        private static URL createUrl (String stringUrl){
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException moe) {
                Log.e(LOG_TAG, "createURL: Problem building URL", moe);
            }
            return url;
        }

        private static String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            //Check for null, then return early.
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

            //If the request was successful (response code 200),
            //then read the input stream and parse the response.
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "makeHTTPRequest: error Code: " + urlConnection.getResponseCode());
                }
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "makeHTTPRequest: Couldn't retrieve JSON", ioe);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    //Closing the input stream could throw an IOException, which is why
                    //the makeHTTPRequest(URL url method signature specifies than an IOException
                    //could be thrown.
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        //Convert the (@link InputStream} into a String which contains the whole JSON response from the server.
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

        private static List<News> extractNewsFromJson(String newsJSON){
            //This will get the strings information.  If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(newsJSON)) {
                return null;
            }

            List<News> collectionNews = new ArrayList<>();
            try {
                JSONObject baseJsonResponse = new JSONObject(newsJSON);
                //This will go in and start from the root of the information to be collected and parsed down by key and cell.

                Log.v("jsonarray", "newsArray");

                //This creates 3 json array object from "response"
                JSONObject baseJsonResponseResult = baseJsonResponse.getJSONObject("response");

                //Create a json array object from "results"
                JSONArray currentNewsArray = baseJsonResponseResult.getJSONArray("results");

                //For each news in the newsArray, create an {@link News} object
                for (int n = 0; n < currentNewsArray.length(); n++) {
                    JSONObject currentNews = currentNewsArray.getJSONObject(n);

                    //make items and extract the values for the keys called "webTitle", "webPublicationDate", "sectionName",
                    //and webUrl.

                    String articleHeadline = currentNews.getString("webTitle");
                    String articleDate = currentNews.getString("webPublicationDate");
                    String articleSection = currentNews.getString("sectionName");
                    String articleUrl = currentNews.getString("webUrl");

                    //This goes a little deeper in "results" to find the author of the information if available.
                    JSONArray tagsArray = currentNews.getJSONArray("tags");
                    String articleAuthor = null;
                    if (tagsArray.length() == 1) {
                        JSONObject contributorTag = (JSONObject) tagsArray.get(0);
                        articleAuthor = contributorTag.getString("webTitle");
                    }
                    News news = new News(articleHeadline, articleDate, articleAuthor, articleSection, articleUrl);

                    collectionNews.add(news);
                }

            } catch (JSONException je) {
                Log.e("QueryUtils", "extractNewsFromJSON: Problem parsing result", je);
            }
            return collectionNews;
        }
    }

