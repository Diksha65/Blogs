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
public class AdminBlogFragment extends Fragment {

    private static final String TAG = "AdminBlogFragment";
    private RecyclerView recyclerView;
    private TextView emptyText;
    private FirebaseRecyclerAdapter<Blog, AdminBlogHolder> adapter;
    private static DataStash dataStash = DataStash.DATA_STASH;

    public static AdminBlogFragment newInstance(){
        return new AdminBlogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        emptyText = (TextView)view.findViewById(R.id.emptyView);
        emptyText.setText("Wait for new blogs");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        createRecyclerViewAdapter();

/*
        Button button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = DetailFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentUtils.attachFragment(fragment, R.id.fragment_containcer, fragmentManager);
            }
        });*/

        return view;
    }

    /**
     *  ToDo This adapter will change. This needs to show list of lists. So dont go with it.
     */

    private void createRecyclerViewAdapter(){
        adapter = new FirebaseRecyclerAdapter<Blog, AdminBlogHolder>(
                Blog.class,
                R.layout.blog_item,
                AdminBlogHolder.class,
                dataStash.database.child("Unapproved Blogs").getRef()) {
            @Override
            protected void populateViewHolder(AdminBlogHolder adminBlogHolder, Blog blog, int i) {
                if(emptyText.getVisibility() == View.VISIBLE)
                    emptyText.setVisibility(View.GONE);
                adminBlogHolder.blogger.setText(blog.getBloggerName());
                adminBlogHolder.imageView.setImageURI(blog.getPhotoUrl());
                adminBlogHolder.title.setText(blog.getTitle());
                adminBlogHolder.approval.setVisibility(View.GONE);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class AdminBlogHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;
        TextView title;
        TextView blogger;
        Button approval;

        public AdminBlogHolder(View itemView){
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
