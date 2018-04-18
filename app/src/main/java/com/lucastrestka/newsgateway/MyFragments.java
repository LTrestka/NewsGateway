package com.lucastrestka.newsgateway;

/**
 * Created by trest on 4/7/2018.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MyFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String SIZE = "SIZE";
    public static final String PAGE = "PAGE";


    public static String URL;

    public static final MyFragment newInstance(Articles article, int current, int size)
    {
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable(EXTRA_MESSAGE, article);
        bdl.putInt(PAGE, current);
        bdl.putInt(SIZE, size);
        f.setArguments(bdl);
        return f;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Articles article = (Articles) getArguments().getSerializable(EXTRA_MESSAGE);
        int pageNumber = getArguments().getInt(PAGE);
        int size = getArguments().getInt(SIZE);
        View v = inflater.inflate(R.layout.myfragment_layout, container, false);
        TextView title = (TextView)v.findViewById(R.id.ArticleTitle);
        TextView author = (TextView)v.findViewById(R.id.ArticleAuthor);
        TextView date = (TextView)v.findViewById(R.id.ArticleDate);
        TextView description = (TextView)v.findViewById(R.id.ArticleDescription);
        TextView page = (TextView) v.findViewById(R.id.page);

        final ImageView image = (ImageView) v.findViewById(R.id.ArticleImage);

        URL = article.getUrl();

        if(article.getAuthor().equals("null")){
            author.setText("No author listed");

        }
        else{
            author.setText(article.getAuthor());
        }
        if(article.getTitle().equals("null")){
            title.setText("No title listed");
        }
        else{
            title.setText(article.getTitle());
        }
        if(article.getWhenPublished().equals("null")){
            date.setText("No publishing date");
        }
        else{
            date.setText(article.getWhenPublished());
        }
        if(article.getDescription().equals("null") || article.getDescription().equals("")){
            description.setText("No description listed, click to read full article.");
        }
        else {
            description.setText(article.getDescription());
        }

        if (!article.getUrlToImage().equals("")) {
            image.setContentDescription(article.getUrl());
            Picasso picasso = new Picasso.Builder(this.getContext()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    // Here we try https if the http image attempt failed
                    final String changedUrl = article.getUrlToImage().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.ic_launcher_background)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(image);
                }
            }).build();
            picasso.load(article.getUrlToImage())
                    .error(R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(image);
        } else {
            image.setVisibility(View.INVISIBLE);
            /*Picasso.with(this.getContext()).load(article.getUrlToImage())
                    .error(R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(image);*/
        }


        page.setText((1+pageNumber) + " of " + size);

        return v;
    }


}