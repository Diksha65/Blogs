package com.example.diksha.blogs;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    public static final String TAG = "DetailFragment";
    private Blog blog;
    private DataStash dataStash = DataStash.DATA_STASH;
    private TextView likes;
    private boolean attached;

    public static DetailFragment newInstance(Blog blog, DataStash.type type){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Blog", blog);
        bundle.putSerializable("Type", type);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    //Todo set the likes text in the detail fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        blog = (Blog)bundle.getSerializable("Blog");
        DataStash.type type = (DataStash.type)bundle.getSerializable("Type");

        Log.e(TAG, type.toString());
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView simpleDraweeView = (ImageView) view.findViewById(R.id.detail_blog_image);
        TextView title = (TextView)view.findViewById(R.id.detail_blog_title);
        TextView name = (TextView)view.findViewById(R.id.detail_blog_name);
        TextView description = (TextView)view.findViewById(R.id.detail_blog_description);
        likes = (TextView)view.findViewById(R.id.detail_blog_likes);
        Button button = (Button)view.findViewById(R.id.detail_blog_approve);
        ImageView download = (ImageView)view.findViewById(R.id.detail_blog_download);
        final ImageView likeButton = (ImageView)view.findViewById(R.id.detail_blog_like);

        Glide.with(getActivity()).load(blog.getPhotoUrl()).into(simpleDraweeView);
        title.setText(blog.getTitle());
        name.setText(blog.getBloggerName());
        description.setText(blog.getDescription());
        switch (type){
            case isPublic:
                likes.setText(String.valueOf(blog.getLikes()));
                button.setVisibility(View.GONE);
                break;
            case isAdmin:
                likes.setVisibility(View.GONE);
            case isMember:
                likes.setText(String.valueOf(blog.getLikes()));
                button.setVisibility(View.VISIBLE);
                button.setText(blog.getApproved().equals("true") ? "Approved" : "Not Approved");
                likeButton.setVisibility(View.GONE);
                download.setVisibility(View.GONE);
                break;
        }

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager wpm = WallpaperManager.getInstance(getActivity());
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                new SetWallPaperTask(blog.getPhotoUrl(), progressDialog, wpm)
                        .execute();
            }
        });

        BlogLikes.setInitialLikeButton(likeButton, blog, getActivity());

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlogLikes.onLikeClicked(likeButton, blog, getActivity());
            }
        });

        listenToChanges();
        return view;
    }

    private void listenToChanges(){
        if(dataStash.attachChildEventListener("LISTENLIKES",
                dataStash.database.child("VisibleToAll"),
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Blog blog = dataSnapshot.getValue(Blog.class);
                        likes.setText(String.valueOf(blog.getLikes()));
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                })){
            attached = true;
            Log.e(TAG, "success");
        } else {
            attached = false;
            Log.e(TAG, "error");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(attached)
            dataStash.detachChildEventListener("LISTENLIKES");
    }
}
