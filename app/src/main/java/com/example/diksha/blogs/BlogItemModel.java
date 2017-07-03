package com.example.diksha.blogs;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;

import com.facebook.drawee.view.SimpleDraweeView;


/**
 * Created by diksha on 3/7/17.
 */

public class BlogItemModel extends BaseObservable{

    private Blog blog;
    private boolean publicBlog;

    public BlogItemModel() {
    }

    @Bindable
    public boolean isPublicBlog() {
        return publicBlog;
    }

    public void setPublicBlog(boolean publicBlog) {
        this.publicBlog = publicBlog;
        notifyPropertyChanged(com.example.diksha.blogs.BR.publicBlog);
    }

    @Bindable
    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
        notifyPropertyChanged(com.example.diksha.blogs.BR.blog);
    }

    public String getName(){
        return blog.getBloggerName();
    }

    public String getTitle(){
        return blog.getTitle();
    }

    public String getApprove(){
        if(blog.getApproved().equals("true"))
            return "Approved";
        return "Not Approved";
    }

    public String getImageUrl(){
        return blog.getPhotoUrl();
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(SimpleDraweeView view, String imageUrl){
        view.setImageURI(imageUrl);
    }
}
