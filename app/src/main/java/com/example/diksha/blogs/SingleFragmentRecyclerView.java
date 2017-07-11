package com.example.diksha.blogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by diksha on 9/7/17.
 */

abstract public class SingleFragmentRecyclerView extends Fragment {

    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView emptyText;
    public static boolean isPagerOn, attached;
    private static DataStash dataStash = DataStash.DATA_STASH;
    private static DataStash.type viewType;

    protected abstract DataStash.type setViewType();
    protected abstract void createRecyclerViewAdapter(RecyclerView recyclerView);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        isPagerOn = false;
        emptyText = (TextView)view.findViewById(R.id.emptyView);
        emptyText.setText("No blogs");
        showProgressDialog("Fetching Data");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        viewType = setViewType();
        createRecyclerViewAdapter(recyclerView);
        createNewBlog(view);

        return view;
    }

    public static class BlogsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SimpleDraweeView imageView;
        TextView title, blogger, likes;
        Button approval;
        ImageView download, likeButton;
        Blog blog;

        public BlogsHolder(View itemView){
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.blog_image);
            title = (TextView)itemView.findViewById(R.id.blog_title);
            blogger = (TextView)itemView.findViewById(R.id.blog_name);
            likes = (TextView)itemView.findViewById(R.id.blog_likes);
            approval = (Button)itemView.findViewById(R.id.blog_approve);
            download = (ImageView)itemView.findViewById(R.id.blog_download);
            likeButton = (ImageView) itemView.findViewById(R.id.blog_like);
            itemView.setOnClickListener(this);
        }

        public void bindView(Blog blog){
            blogger.setText(blog.getBloggerName());
            imageView.setImageURI(blog.getPhotoUrl());
            title.setText(blog.getTitle());
            likes.setText(String.valueOf(blog.getLikes()));
            this.blog = blog;

        }

        @Override
        public void onClick(View v) {
            Intent intent = BlogPagerActivity.newIntent(v.getContext(), blog, viewType);
            Context context = v.getContext();
            if(context instanceof FragmentActivity){
                isPagerOn = true;
                context.startActivity(intent);

            }
        }

    }

    public void displayInitialView(DatabaseReference ref, final List<Blog> list){

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    Log.e("Single", dataSnapshot.getValue().toString());
                    hideProgressDialog();
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Blog blog = snapshot.getValue(Blog.class);
                        Log.e("Blog", blog.getKey());
                        if(notAdded(blog, list) && !isPagerOn)
                            list.add(blog);
                    }
                }
                Log.e("Single", String.valueOf(list.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        if(dataStash.attachValueEventListener("ValueEventListener",
                ref, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){
                            Log.e("Single", dataSnapshot.getValue().toString());
                            hideProgressDialog();
                            emptyText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyText.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Blog blog = snapshot.getValue(Blog.class);
                                Log.e("Blog", blog.getKey());
                                if(notAdded(blog, list) && !isPagerOn)
                                    list.add(blog);
                            }
                        }
                        Log.e("Single", String.valueOf(list.size()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                })) {
            Log.e("SingleFragment", "LALA");
            attached = true;
        } else {
            attached = false;
            Log.e("singleFragment", "error occurred");
        }
*/
    }
    public void createNewBlog(View view){

    }

    public boolean notAdded(Blog blog, List<Blog> list){
        for(int i=0;i<list.size(); ++i)
            if(list.get(i).getKey().equals(blog.getKey()))
                return false;
        return true;
    }

    public int getItemIndex(Blog blog, List<Blog> list){
        int index = -1;
        for(int i=0; i<list.size(); ++i){
            if(list.get(i).getKey().equals(blog.getKey())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static void notifyUser(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(msg);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
