package com.example.diksha.blogs;

/**
 * Created by diksha on 27/6/17.
 */

public class Blog {

    String title;
    String photoUrl;
    String description;
    String bloggerName;
    String approved;
    String key;

    public Blog(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Blog(String title, String photoUrl, String description, String bloggerName, String approved) {
        this.title = title;
        this.photoUrl = photoUrl;
        this.description = description;
        this.bloggerName = bloggerName;
        this.approved = approved;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getBloggerName() {
        return bloggerName;
    }

    public void setBloggerName(String bloggerName) {
        this.bloggerName = bloggerName;
    }
}
