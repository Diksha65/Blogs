package com.example.diksha.blogs;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by diksha on 3/7/17.
 */

public class RecyclerViewModel extends BaseObservable{

    private boolean isMember, isEmptyTextVisible;
    private String text;

    public RecyclerViewModel() {
    }

    @Bindable
    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
        notifyPropertyChanged(com.example.diksha.blogs.BR.member);
    }

    @Bindable
    public boolean isEmptyTextVisible() {
        return isEmptyTextVisible;
    }

    public void setEmptyTextVisible(boolean emptyTextVisible) {
        this.isEmptyTextVisible = emptyTextVisible;
        notifyPropertyChanged(com.example.diksha.blogs.BR.emptyTextVisible);
    }

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(com.example.diksha.blogs.BR.text);
    }
}
