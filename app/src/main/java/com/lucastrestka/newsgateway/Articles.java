package com.lucastrestka.newsgateway;

import java.io.Serializable;

/**
 * Created by trest on 4/10/2018.
 */

public class Articles implements Serializable {

    private String Author;
    private String Title;
    private String Description;
    private String Url;
    private String urlToImage;
    private String whenPublished;
    private int currentPage;

    public Articles(String author, String title, String description,String url, String urlToImage, String whenPublished) {
        this.Author = author;
        this.Title = title;
        this.Description = description;
        this.Url = url;
        this.urlToImage = urlToImage;
        this.whenPublished = whenPublished;
    }
    public int getCurrentPage(){
        return currentPage;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getWhenPublished() {
        return whenPublished;
    }

    public void setWhenPublished(String whenPublished) {
        this.whenPublished = whenPublished;
    }
}
