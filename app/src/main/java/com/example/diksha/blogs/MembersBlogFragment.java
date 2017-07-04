package com.example.diksha.blogs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
public class MembersBlogFragment extends Fragment {

    private static final String TAG = "MembersBlogFragment";
    private static final String LISTENER = "ChildEventListener";

    private static DataStash dataStash = DataStash.DATA_STASH;
    private FirebaseRecyclerAdapter<Blog, BlogsHolder> adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;
    private View view;


    public static MembersBlogFragment newInstance() {
        return new MembersBlogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        emptyText = (TextView)view.findViewById(R.id.emptyView);
        emptyText.setText("You havent created any blog. Create your first blog.");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        createRecyclerViewAdapter();
        createNewBlog();

        return view;
    }

    private void createRecyclerViewAdapter(){
        adapter = new FirebaseRecyclerAdapter<Blog, BlogsHolder>(
                Blog.class,
                R.layout.blog_item,
                BlogsHolder.class,
                dataStash.database.child("DikshaId").getRef()) {
            @Override
            protected void populateViewHolder(BlogsHolder blogsHolder, Blog blog, int i) {
                if(emptyText.getVisibility() == View.VISIBLE)
                    emptyText.setVisibility(View.GONE);
                blogsHolder.blogger.setText(blog.getBloggerName());
                blogsHolder.imageView.setImageURI(blog.getPhotoUrl());
                blogsHolder.title.setText(blog.getTitle());
                blogsHolder.approval.setText(blog.getApproved().equals("true") ? "Approved" : "Not Approved");
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void createNewBlog(){
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

    public static class BlogsHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView imageView;
        TextView title;
        TextView blogger;
        Button approval;

        public BlogsHolder(View itemView){
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

/*
public class MembersBlogFragment extends Fragment {

    private static final String TAG = "MembersBlogFragment";
    private static final String LISTENER = "ChildEventListener";

    private static DataStash dataStash = DataStash.DATA_STASH;
    private BlogsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;
    private View view;
    private ChildEventListener childEventListener = null;

    public static MembersBlogFragment newInstance() {
        return new MembersBlogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        emptyText = (TextView)view.findViewById(R.id.emptyView);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new BlogsAdapter(dataStash.membersBlogList);
        recyclerView.setAdapter(adapter);

        updateList();
        createNewBlog();
        checkIfEmpty();

        return view;
    }

    private void createNewBlog(){
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

    private int getItemIndex(Blog blog){
        int index = -1;
        for(int i=0; i<dataStash.membersBlogList.size(); ++i){
           if(dataStash.membersBlogList.get(i).getKey().equals(blog.getKey())) {
               index = i;
               break;
           }
        }
        return index;
    }

    private class BlogsHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView imageView;
        TextView title;
        TextView blogger;
        Button approval;

        public BlogsHolder(View itemView){
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.blog_image);
            title = (TextView)itemView.findViewById(R.id.blog_title);
            blogger = (TextView)itemView.findViewById(R.id.blog_name);
            approval = (Button)itemView.findViewById(R.id.blog_approve);
        }

    }

    public class BlogsAdapter extends RecyclerView.Adapter<BlogsHolder>{

        List<Blog> blogList;

        public BlogsAdapter(List<Blog> blogs){
            blogList = blogs;
        }

        @Override
        public BlogsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_item, viewGroup, false);
            return new BlogsHolder(view);
        }

        @Override
        public void onBindViewHolder(BlogsHolder blogsHolder, int i) {
            Blog blog = blogList.get(i);
            blogsHolder.blogger.setText(blog.getBloggerName());
            blogsHolder.imageView.setImageURI(blog.getPhotoUrl());
            blogsHolder.title.setText(blog.getTitle());
            blogsHolder.approval.setText(blog.getApproved().equals("true") ? "Approved" : "Not Approved");
        }

        @Override
        public int getItemCount() {
            return blogList.size();
        }
    }

    private void checkIfEmpty(){
        if(dataStash.membersBlogList.size() == 0){
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("You havent created any blog. Create your first blog.");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(childEventListener != null)
            dataStash.detachChildEventListener(LISTENER);
        dataStash.membersBlogList.clear();
    }
}

 */