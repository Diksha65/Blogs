package com.example.diksha.blogs;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MembersBlogFragment extends Fragment {

    private static final String TAG = "MembersBlogFragment";
    private RecyclerView recyclerView;
    private static List<Blog> listOfBlogs;
    private static Map<String, List<Blog>> mapOfBlogs;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Blogs");
    private BlogsAdapter adapter;
    private TextView emptyText;

    public MembersBlogFragment() {
        // Required empty public constructor
    }

    public static MembersBlogFragment newInstance() {
        return new MembersBlogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fresco.initialize(getActivity());
        listOfBlogs = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        emptyText = (TextView)view.findViewById(R.id.emptyView);
        recyclerView = (RecyclerView)view.findViewById(R.id.blogs_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new BlogsAdapter(listOfBlogs);
        recyclerView.setAdapter(adapter);

        updateList();

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = CreateBlogFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_containcer, fragment)
                        .addToBackStack(null).commit();
            }
        });

        checkIfEmpty();
        return view;
    }

    public void updateList(){
        databaseReference.child("DikshaId")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.e(TAG, dataSnapshot.toString());
                        listOfBlogs.add(dataSnapshot.getValue(Blog.class));
                        adapter.notifyDataSetChanged();
                        Log.e(TAG, String.valueOf(listOfBlogs.size()));
                        checkIfEmpty();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Blog blog = dataSnapshot.getValue(Blog.class);
                        int index = getItemIndex(blog);
                        listOfBlogs.set(index, blog);
                        Log.e("Changed", String.valueOf(listOfBlogs.size()));
                        adapter.notifyItemChanged(index);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        checkIfEmpty();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private int getItemIndex(Blog blog){
        int index = -1;
        for(int i=0; i<listOfBlogs.size(); ++i){
           if(listOfBlogs.get(i).getKey().equals(blog.getKey())) {
               index = i;
               break;
           }
        }
        return index;
    }

    private void checkIfEmpty(){
        if(listOfBlogs.size() == 0){
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
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
}
