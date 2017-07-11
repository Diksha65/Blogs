package com.example.diksha.blogs;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */

public class PublicBlogFragment extends SingleFragmentRecyclerView {

    private static final String TAG = "PublicBlogFragment";
    private FirebaseRecyclerAdapter<Blog, BlogsHolder> adapter;
    private static DataStash dataStash = DataStash.DATA_STASH;
    public static PublicBlogFragment newInstance(){
        return new PublicBlogFragment();
    }

    @Override
    protected DataStash.type setViewType() {
        return DataStash.type.isPublic;
    }

    protected void createRecyclerViewAdapter(final RecyclerView recyclerView){

        displayInitialView(dataStash.database.child("VisibleToAll"), dataStash.publicBlogList);

        adapter = new FirebaseRecyclerAdapter<Blog, BlogsHolder>(
                Blog.class,
                R.layout.blog_item,
                BlogsHolder.class,
                dataStash.database.child("VisibleToAll").getRef()) {
            @Override
            protected void populateViewHolder(final BlogsHolder blogsHolder, final Blog blog, int i) {
                hideProgressDialog();
                blogsHolder.approval.setVisibility(View.GONE);
                blogsHolder.bindView(blog);
                BlogLikes.setInitialLikeButton(blogsHolder.likeButton, blog, getActivity());

                blogsHolder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WallpaperManager wpm = WallpaperManager.getInstance(getActivity());
                        ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        new SetWallPaperTask(blog.getPhotoUrl(), progressDialog, wpm)
                                .execute();
                    }
                });

                blogsHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BlogLikes.onLikeClicked(blogsHolder.likeButton, blog, getActivity());
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
