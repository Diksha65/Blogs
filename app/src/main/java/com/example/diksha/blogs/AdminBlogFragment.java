package com.example.diksha.blogs;


import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;


/**
 * A simple {@link Fragment} subclass.
 */

public class AdminBlogFragment extends SingleFragmentRecyclerView {

    private static final String TAG = "AdminBlogFragment";
    private FirebaseRecyclerAdapter<Blog, SingleFragmentRecyclerView.BlogsHolder> adapter;
    private static DataStash dataStash = DataStash.DATA_STASH;

    public static AdminBlogFragment newInstance(){
        return new AdminBlogFragment();
    }

    @Override
    protected DataStash.type setViewType() {
        return DataStash.type.isAdmin;
    }

    @Override
    protected void createRecyclerViewAdapter(RecyclerView recyclerView) {

        displayInitialView(dataStash.database.child("UnapprovedBlogs"), dataStash.adminBlogList);

        adapter = new FirebaseRecyclerAdapter<Blog, BlogsHolder>(
                Blog.class,
                R.layout.blog_item,
                BlogsHolder.class,
                dataStash.database.child("UnapprovedBlogs")
        ) {
            @Override
            protected void populateViewHolder(BlogsHolder blogsHolder, final Blog blog, int i) {
                hideProgressDialog();
                blogsHolder.approval.setText(blog.getApproved().equals("true") ? "Approved" : "Not Approved");
                blogsHolder.likes.setVisibility(View.GONE);
                blogsHolder.likeButton.setVisibility(View.GONE);
                blogsHolder.download.setVisibility(View.GONE);
                blogsHolder.bindView(blog);

                blogsHolder.approval.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTransaction(dataStash.database.child("EditingLocks").child(blog.getKey()), v, blog);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void startTransaction(DatabaseReference ref, final View view, final Blog blog){
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.e(TAG, String.valueOf(mutableData.child("lock").getValue()));
                boolean value = (boolean)mutableData.child("lock").getValue();
                Log.e("Value", String .valueOf(value));
                if(value){
                    Log.e(TAG, "aborted");
                    return Transaction.abort();
                } else {
                    //If lock is false, set it to true and show the alert dialog.
                    mutableData.child("lock").setValue(true);
                    mutableData.child("blockerName").setValue(blog.getBloggerName());
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError == null){
                    createConfirmationAlertDialog(view, blog);
                } else {
                    Log.e(TAG, dataSnapshot.getKey());
                    notifyUser(view, dataSnapshot.child("blockerName").getValue() + " is editing the blog. Wait");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
    /*
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
*/
}
