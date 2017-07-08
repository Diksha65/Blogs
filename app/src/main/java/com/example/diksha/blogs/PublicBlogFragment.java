package com.example.diksha.blogs;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */

public class PublicBlogFragment extends Fragment {

    private ProgressDialog progressDialog;
    private static final String TAG = "PublicBlogFragment";
    private RecyclerView recyclerView;
    private TextView emptyText;
    public static boolean isPagerOn = false;
    private FirebaseRecyclerAdapter<Blog, PublicBlogHolder> adapter;
    private static DataStash dataStash = DataStash.DATA_STASH;

    public static PublicBlogFragment newInstance(){
        return new PublicBlogFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        isPagerOn = false;
        emptyText = (TextView)view.findViewById(R.id.emptyView);
        emptyText.setText("There are no blogs");
        showProgressDialog("Fetching Data");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        createRecyclerViewAdapter();

        return view;
    }

    private void createRecyclerViewAdapter(){

        //Todo use value event listener here

        if(dataStash.attachChildEventListener("LALALA",
                dataStash.database.child("VisibleToAll"),
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.getValue() == null){
                            hideProgressDialog();
                            emptyText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyText.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            Blog blog = dataSnapshot.getValue(Blog.class);
                            if(notAdded(blog) && !isPagerOn)
                                dataStash.publicBlogList.add(blog);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
            Log.d(TAG, "LALA");
        } else {
            Toast.makeText(getActivity(), "Value Error occured. Try Again!", Toast.LENGTH_SHORT).show();
        }

        adapter = new FirebaseRecyclerAdapter<Blog, PublicBlogHolder>(
                Blog.class,
                R.layout.blog_item,
                PublicBlogHolder.class,
                dataStash.database.child("VisibleToAll").getRef()) {
            @Override
            protected void populateViewHolder(final PublicBlogHolder publicBlogHolder, final Blog blog, int i) {
                hideProgressDialog();
                publicBlogHolder.blogger.setText(blog.getBloggerName());
                publicBlogHolder.imageView.setImageURI(blog.getPhotoUrl());
                publicBlogHolder.title.setText(blog.getTitle());
                publicBlogHolder.likes.setText(String.valueOf(blog.getLikes()));
                publicBlogHolder.approval.setVisibility(View.GONE);
                publicBlogHolder.blog = blog;
                dataStash.userBase.child("User1").child(blog.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() != null && (boolean)dataSnapshot.getValue())
                                    publicBlogHolder.likeButton.setImageDrawable(
                                            getResources().getDrawable(R.drawable.ic_like_fill)
                                    );
                                else
                                    publicBlogHolder.likeButton.setImageDrawable(
                                            getResources().getDrawable(R.drawable.ic_like_border)
                                    );
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                publicBlogHolder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WallpaperManager wpm = WallpaperManager.getInstance(getActivity());
                        ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        new SetWallPaperTask(blog.getPhotoUrl(), progressDialog, wpm)
                                .execute();
                    }
                });
                publicBlogHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataStash.userBase.child("User1").child(blog.getKey())
                                .addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.getValue() == null) {
                                                    Log.e(TAG, "Liked");
                                                    publicBlogHolder.likeButton
                                                            .setImageDrawable(getResources()
                                                                    .getDrawable(R.drawable.ic_like_fill, null));
                                                    onLikeClicked(blog, true);
                                                } else {
                                                    Log.e(TAG, "Unliked");
                                                    publicBlogHolder.likeButton
                                                            .setImageDrawable(getResources()
                                                                    .getDrawable(R.drawable.ic_like_border, null));
                                                    onLikeClicked(blog, false);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        }
                                );
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void onLikeClicked(final Blog blog, final boolean isLiking){
        dataStash.database.child("VisibleToAll").child(blog.getKey())
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Blog blog1 = mutableData.getValue(Blog.class);
                        if(blog1 == null)
                            return Transaction.abort();
                        if(isLiking){
                            blog1.addLike();
                            dataStash.userBase.child("User1").child(blog.getKey()).setValue(true);
                            dataStash.database.child(blog1.getBloggerId()).child(blog.getKey()).setValue(blog1);
                        } else {
                            blog1.removeLike();
                            dataStash.userBase.child("User1").child(blog.getKey()).setValue(null);
                            dataStash.database.child(blog1.getBloggerId()).child(blog.getKey()).setValue(blog1);
                        }
                        int index = getItemIndex(blog1);
                        dataStash.publicBlogList.set(index, blog1);
                        mutableData.setValue(blog1);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.e(TAG, "likeTransaction:onComplete:" + databaseError);
                    }
                });
    }

    private int getItemIndex(Blog blog){
        int index = -1;
        for(int i=0; i<dataStash.publicBlogList.size(); ++i){
            if(dataStash.publicBlogList.get(i).getKey().equals(blog.getKey())) {
                index = i;
                break;
            }
        }
        return index;
    }

    private boolean notAdded(Blog blog){
        for(int i=0;i<dataStash.publicBlogList.size(); ++i)
            if(dataStash.publicBlogList.get(i).getKey().equals(blog.getKey()))
                return false;
        return true;
    }

    public static class PublicBlogHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SimpleDraweeView imageView;
        TextView title, blogger, likes;
        Button approval;
        Blog blog;
        ImageView download, likeButton;

        public PublicBlogHolder(View itemView){
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

        @Override
        public void onClick(View v) {
            Intent intent = BlogPagerActivity.newIntent(v.getContext(), blog);
            Context context = v.getContext();
            if(context instanceof FragmentActivity){
                isPagerOn = true;
                context.startActivity(intent);

            }
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataStash.detachChildEventListener("LALALA");
        //dataStash.detachValueEventListener("LIST");
        adapter.cleanup();
    }
}
