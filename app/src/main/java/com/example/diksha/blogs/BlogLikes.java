package com.example.diksha.blogs;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by diksha on 9/7/17.
 */

public class BlogLikes {

    private static int drawableId;
    private static final String TAG = "BlogLikes";
    private static DataStash dataStash = DataStash.DATA_STASH;

    public static void setInitialLikeButton(final ImageView likeButton,
                                            Blog blog, final Context context){
        dataStash.userBase.child("User1").child(blog.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null && (boolean)dataSnapshot.getValue())
                            drawableId = R.drawable.ic_like_fill;
                        else
                            drawableId = R.drawable.ic_like_border;
                        likeButton.setImageDrawable(context.getResources().getDrawable(drawableId));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void onLikeClicked(final ImageView likeButton,
                                     final Blog blog, final Context context){
        dataStash.userBase.child("User1").child(blog.getKey())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() == null) {
                                    drawableId = R.drawable.ic_like_fill;
                                    updateLike(blog, true);
                                } else {
                                    drawableId = R.drawable.ic_like_border;
                                    updateLike(blog, false);
                                }
                                likeButton.setImageDrawable(context.getResources()
                                                .getDrawable(drawableId));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
    }

    private static void updateLike(final Blog blog, final boolean isLiking){
        dataStash.database.child("VisibleToAll").child(blog.getKey())
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Blog blog1 = mutableData.getValue(Blog.class);
                        if(blog1 != null) {
                            if (isLiking) {
                                blog1.addLike();
                                dataStash.userBase.child("User1").child(blog.getKey()).setValue(true);
                            } else {
                                blog1.removeLike();
                                dataStash.userBase.child("User1").child(blog.getKey()).setValue(null);
                            }
                            dataStash.database.child(blog1.getBloggerId()).child(blog.getKey()).setValue(blog1);
                            int index = getItemIndex(blog1);
                            dataStash.publicBlogList.set(index, blog1);
                            mutableData.setValue(blog1);
                            return Transaction.success(mutableData);
                        } else
                            return Transaction.abort();
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.e(TAG, "likeTransaction:onComplete:" + databaseError);
                    }
                });
    }

    private static int getItemIndex(Blog blog){
        int index = -1;
        for(int i=0; i<dataStash.publicBlogList.size(); ++i){
            if(dataStash.publicBlogList.get(i).getKey().equals(blog.getKey())) {
                index = i;
                break;
            }
        }
        return index;
    }

}
