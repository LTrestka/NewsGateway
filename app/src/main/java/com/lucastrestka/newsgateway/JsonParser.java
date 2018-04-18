package com.lucastrestka.newsgateway;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trest on 4/9/2018.
 */

public class JsonParser implements Serializable{
    private static final String TAG = "JsonParser";

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<Sources> parseSources(String json) {

        ArrayList<Sources> sources = new ArrayList<>();
        String id = "";
        String name = "";
        String url = "";
        String category = "";

        try {

            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("sources");

            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i) != null) {
                    JSONObject newsSource = array.getJSONObject(i);
                    if (newsSource.has("id")) {
                        id = newsSource.getString("id");
                    }
                    if (newsSource.has("name")) {
                        name = newsSource.getString("name");
                    }
                    if (newsSource.has("url")) {
                        url = newsSource.getString("url");
                    }
                    if (newsSource.has("category")) {
                        category = newsSource.getString("category");
                    }
                    Sources s = new Sources(id, name, url, category);
                    sources.add(s);
                }
            }
            return sources;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sources;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<Articles> parseArticles(String json) {

        ArrayList<Articles> articles = new ArrayList<>();

        String author = "";
        String title = "";
        String description = "";
        String url = "";
        String urlToImage = "";
        String publishedAt = "";

        try {

            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("articles");

            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i) != null) {
                    JSONObject newsSource = array.getJSONObject(i);
                    if (newsSource.has("author")) {
                        if (!newsSource.getString("author").equals(null)) {
                            author = newsSource.getString("author");
                        }
                        else {
                            author = "No author provided";
                        }
                    }
                    if (newsSource.has("title")) {
                        title = newsSource.getString("title");
                    }
                    if (newsSource.has("description")) {
                        description = newsSource.getString("description");
                    }
                    if (newsSource.has("url")) {
                        url = newsSource.getString("url");
                    }
                    if (newsSource.has("urlToImage")) {
                        urlToImage = newsSource.getString("urlToImage");
                    }
                    if (newsSource.has("publishedAt")) {
                        publishedAt = newsSource.getString("publishedAt");
                    }

                    Articles a = new Articles(author, title, description, url, urlToImage, publishedAt);
                    articles.add(a);
                    Log.d(TAG, "parseArticles: stuff");
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }
}
