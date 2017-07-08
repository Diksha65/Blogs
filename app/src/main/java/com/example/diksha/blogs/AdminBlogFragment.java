package com.example.diksha.blogs;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        createRecyclerViewAdapter();
        return view;
    }

    private void createRecyclerViewAdapter(){
        adapter = new FirebaseRecyclerAdapter<Blog, AdminBlogHolder>(
                Blog.class,
                R.layout.blog_item,
                AdminBlogHolder.class,
                dataStash.database.child("UnapprovedBlogs").getRef()) {
            @Override
            protected void populateViewHolder(AdminBlogHolder adminBlogHolder, Blog blog, int i) {
                if(emptyText.getVisibility() == View.VISIBLE)
                    emptyText.setVisibility(View.GONE);
                adminBlogHolder.bindView(blog);
                adminBlogHolder.blog = blog;
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class AdminBlogHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SimpleDraweeView imageView;
        TextView title;
        TextView blogger;
        Button approval;
        Blog blog;

        public AdminBlogHolder(final View itemView){
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.blog_image);
            title = (TextView)itemView.findViewById(R.id.blog_title);
            blogger = (TextView)itemView.findViewById(R.id.blog_name);
            approval = (Button)itemView.findViewById(R.id.blog_approve);
            itemView.setOnClickListener(this);
        }

        private void bindView(final Blog blog){
            blogger.setText(blog.getBloggerName());
            imageView.setImageURI(blog.getPhotoUrl());
            title.setText(blog.getTitle());
            approval.setText(blog.getApproved().equals("true") ? "Approved" : "Not Approved");
            approval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    notifyUser(v, "Clicked");
                    dataStash.database.child("EditingLocks")
                            .child(blog.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean value = (boolean)dataSnapshot.child("lock").getValue();
                                    if(value){
                                        notifyUser(v, dataSnapshot.child("blockerName")
                                                .getValue().toString() + " is using the blog. Wait.");
                                    } else {
                                        //if lock is false then set lock to true and do the editing.
                                        editBlog(dataStash.database.child("EditingLocks")
                                                .child(blog.getKey()), blog, itemView);
                                        notifyUser(v, "yeah");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            });
        }

        @Override
        public void onClick(View v) {
            Fragment fragment = DetailFragment.newInstance(blog, false);
            Context context = v.getContext();
            if(context instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity)context;
                FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
                FragmentUtils.attachFragment(fragment, R.id.fragment_containcer, fragmentManager);
            }
        }
    }

    private static void editBlog(DatabaseReference ref, final Blog blog, final View view){
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                boolean value = (boolean)mutableData.child("lock").getValue();
                notifyUser(view, String.valueOf(value));
                if(value)
                    return Transaction.abort();
                value = true;
                mutableData.child("lock").setValue(value);
                mutableData.child("blockerName").setValue(blog.getBloggerName());
                notifyUser(view, "success");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(b) {
                    notifyUser(view, "complete");
                    createConfirmationAlertDialog(view, blog);
                }
            }
        });
    }

    private static void createConfirmationAlertDialog(final View itemView, final Blog blog){
        AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                .setTitle("Approval Confirmation")
                .setMessage("Are you sure to approve this blog?")
                .setIcon(R.drawable.ic_approve)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyUser(itemView.getRootView(), "Cancelled Approval");
                        dataStash.database.child("EditingLocks").child(blog.getKey()).child("lock").setValue(false);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDatabase(blog);
                    }
                })
                .create();
        alertDialog.show();
    }

    private static void updateDatabase(Blog blog){
        blog.setApproved("true");
        dataStash.database.child("UnapprovedBlogs").child(blog.getKey()).removeValue();
        dataStash.database.child("VisibleToAll").child(blog.getKey()).setValue(blog);
        dataStash.database.child(blog.getBloggerId()).child(blog.getKey()).setValue(blog);
        dataStash.database.child("EditingLocks").child(blog.getKey()).setValue(null);
    }

    private static void notifyUser(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

}
