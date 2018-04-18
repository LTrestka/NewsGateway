package com.lucastrestka.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by trest on 4/9/2018.
 */

public class services extends Service {

    private int x = 0;
    private int y = 0;
    private static final String TAG = "services";
    private boolean isRunning = true;

    private BroadcastReceiver receiver;

    private final String sourcesUrl = "https://newsapi.org/v1/sources?language=en&country=us&apiKey=518e0059f70e4cc49dce2c6ab28867b3";

    private final String articlesWOKey = "https://newsapi.org/v1/articles?source=";
    private final String apiKey = "&apiKey=518e0059f70e4cc49dce2c6ab28867b3";
    private String newsID;
    // Concatenate articlesWOKey + newsID + apiKey for final url.

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {

        final Intent intent = i;

        Log.d(TAG, "Services Started");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR

        new Thread(new Runnable() {
            @Override
            public void run() {

                    if (!intent.getAction().isEmpty()) {

                        if (intent.hasExtra("PARSE_SOURCES")) {
                            if (intent.getStringExtra("PARSE_SOURCES").equals("Go")) {
                                intent.removeExtra("PARCE_SOURCES");

                                //In this example we are just displaying a log message every 1000ms

                                if (isRunning) {
                                    if (x == 0) {
                                        Intent intent = new Intent();
                                        ArrayList<Sources> srcs;
                                        JsonParser jp = new JsonParser();
                                        StringBuilder sb = new StringBuilder();
                                        try {
                                            URL url = new URL(sourcesUrl);
                                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                            conn.setRequestMethod("GET");
                                            Log.d(TAG, "run: made connection");
                                            if (conn.getInputStream() != null) {
                                                InputStream is = conn.getInputStream();  // this one
                                                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                                                String line;
                                                while ((line = reader.readLine()) != null) {
                                                    sb.append(line).append('\n');
                                                }
                                            } else {
                                                Log.d(TAG, " source read: failed");
                                            }
                                        } catch (Exception e) {
                                            Log.e(TAG, "source read: exception ", e);
                                            return;
                                        }
                                        srcs = jp.parseSources(sb.toString());
                                        intent.setAction("SOURCE_REPORT");
                                        intent.putExtra("SOURCE_LIST", srcs);
                                        Log.d(TAG, "run: Sources sent!");
                                        x++;
                                        sendBroadcast(intent);
                                    }
                                }
                            }
                        }
                        if (intent.getAction().equals("PARSE_ARTICLES")) {

                            int position = intent.getIntExtra("POSITION",0);
                            newsID = intent.getStringExtra("NEWS-ID");
                            final String articlesURL = articlesWOKey + newsID + apiKey;

                            if (isRunning) {
                                Intent intent = new Intent();
                                ArrayList<Articles> articles;
                                JsonParser jp = new JsonParser();
                                StringBuilder sb = new StringBuilder();
                                try {
                                    URL url = new URL(articlesURL);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("GET");
                                    Log.d(TAG, "run: made connection");
                                    if (conn.getInputStream() != null) {
                                        InputStream is = conn.getInputStream();  // this one
                                        BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            sb.append(line).append('\n');
                                        }
                                    } else {
                                        Log.d(TAG, " source read: failed");
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "source read: exception ", e);
                                    return;
                                }
                                articles = jp.parseArticles(sb.toString());
                                intent.setAction("ARTICLE_REPORT");
                                intent.putExtra("ARTICLE_LIST", articles);
                                intent.putExtra("POSITION", position);
                                Log.d(TAG, "run: Articles sent!");
                                sendBroadcast(intent);
                            }
                        }
                }
            }
        }).start();
        Log.d(TAG, "onStartCommand: ended");
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        isRunning = false;
    }


}
