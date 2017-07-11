package com.example.diksha.blogs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MembersBlogFragment extends SingleFragmentRecyclerView {

    private static final String TAG = "MembersBlogFragment";
    private static final String LISTENER = "ChildEventListener";

    private static DataStash dataStash = DataStash.DATA_STASH;
    private FirebaseRecyclerAdapter<Blog, BlogsHolder> adapter;

    public static MembersBlogFragment newInstance() {
        return new MembersBlogFragment();
    }

    @Override
    protected DataStash.type setViewType() {
        return DataStash.type.isMember;
    }

    @Override
    public void createNewBlog(View view) {
        view.findViewById(R.id.add).setVisibility(View.VISIBLE);
        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = CreateBlogFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentUtils.attachFragment(fragment, R.id.fragment_containcer, fragmentManager);
            }
        });
    }

    @Override
    protected void createRecyclerViewAdapter(RecyclerView recyclerView){

        displayInitialView(dataStash.database.child("DikshaId"), dataStash.membersBlogList);

        adapter = new FirebaseRecyclerAdapter<Blog, BlogsHolder>(
                Blog.class,
                R.layout.blog_item,
                BlogsHolder.class,
                dataStash.database.child("DikshaId").getRef()) {
            @Override
            protected void populateViewHolder(BlogsHolder blogsHolder, Blog blog, int i) {
                hideProgressDialog();
                blogsHolder.approval.setText(blog.getApproved().equals("true") ? "Approved" : "Not Approved");
                blogsHolder.likeButton.setVisibility(View.GONE);
                blogsHolder.download.setVisibility(View.GONE);
                blogsHolder.bindView(blog);
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

/*
    public void updateList(){

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, dataSnapshot.toString());
                dataStash.membersBlogList.add(dataSnapshot.getValue(Blog.class));
                checkIfEmpty();
                adapter.notifyDataSetChanged();
                Log.e(TAG, String.valueOf(dataStash.membersBlogList.size()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Blog blog = dataSnapshot.getValue(Blog.class);
                int index = getItemIndex(blog);
                dataStash.membersBlogList.set(index, blog);
                Log.e("Changed", String.valueOf(dataStash.membersBlogList.size()));
                adapter.notifyItemChanged(index);
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
        };

        if(dataStash.attachChildEventListener(LISTENER,
                dataStash.database.child("DikshaId"),
                childEventListener))
            Log.e(TAG, "Listener called successfully");
    }
 */