package com.example.diksha.blogs;

import java.io.Serializable;

/**
 * Created by diksha on 27/6/17.
 */

public class Blog implements Serializable{

    String title;
    String photoUrl;
    String description;
    String bloggerName;
    String approved;
    String key;
    String bloggerId;
    int likes;

    public Blog(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Blog(String title, String photoUrl, String description, String bloggerName, String approved, int likes) {
        this.title = title;
        this.photoUrl = photoUrl;
        this.description = description;
        this.bloggerName = bloggerName;
        this.approved = approved;
        this.likes = likes;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void addLike(){
        likes += 1;
    }
    public void removeLike() {
        if(likes > 0)
            likes -= 1;
    }

    public String getBloggerId() {
        return bloggerId;
    }

    public void setBloggerId(String bloggerId) {
        this.bloggerId = bloggerId;
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
