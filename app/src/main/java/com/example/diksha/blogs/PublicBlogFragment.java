package com.example.diksha.blogs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicBlogFragment extends Fragment {

    private static final String TAG = "PublicBlogFragment";
    private RecyclerView recyclerView;
    private TextView emptyText;
    private FirebaseRecyclerAdapter<Blog, PublicBlogHolder> adapter;
    private static DataStash dataStash = DataStash.DATA_STASH;

    public static PublicBlogFragment newInstance(){
        return new PublicBlogFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        emptyText = (TextView)view.findViewById(R.id.emptyView);
        emptyText.setText("There are no blogs");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        createRecyclerViewAdapter();

        return view;
    }

    private void createRecyclerViewAdapter(){
        adapter = new FirebaseRecyclerAdapter<Blog, PublicBlogHolder>(
                Blog.class,
                R.layout.blog_item,
                PublicBlogHolder.class,
                dataStash.database.child("VisibleToAll").getRef()) {
            @Override
            protected void populateViewHolder(PublicBlogHolder publicBlogHolder, Blog blog, int i) {
                if(emptyText.getVisibility() == View.VISIBLE)
                    emptyText.setVisibility(View.GONE);
                publicBlogHolder.blogger.setText(blog.getBloggerName());
                publicBlogHolder.imageView.setImageURI(blog.getPhotoUrl());
                publicBlogHolder.title.setText(blog.getTitle());
                publicBlogHolder.approval.setVisibility(View.GONE);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class PublicBlogHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;
        TextView title;
        TextView blogger;
        Button approval;

        public PublicBlogHolder(View itemView){
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.blog_image);
            title = (TextView)itemView.findViewById(R.id.blog_title);
            blogger = (TextView)itemView.findViewById(R.id.blog_name);
            approval = (Button)itemView.findViewById(R.id.blog_approve);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
