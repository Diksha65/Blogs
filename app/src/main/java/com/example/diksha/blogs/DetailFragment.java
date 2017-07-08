package com.example.diksha.blogs;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    public static final String TAG = "DetailFragment";
    private Blog blog;
    private DataStash dataStash = DataStash.DATA_STASH;
    private TextView likes;

    public static DetailFragment newInstance(Blog blog, boolean isPublic){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Blog", blog);
        bundle.putBoolean("Visible", isPublic);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        blog = (Blog)bundle.getSerializable("Blog");
        boolean isPublic = bundle.getBoolean("Visible");

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView simpleDraweeView = (ImageView) view.findViewById(R.id.detail_blog_image);
        TextView title = (TextView)view.findViewById(R.id.detail_blog_title);
        TextView name = (TextView)view.findViewById(R.id.detail_blog_name);
        TextView description = (TextView)view.findViewById(R.id.detail_blog_description);
        likes = (TextView)view.findViewById(R.id.detail_blog_likes);
        Button button = (Button)view.findViewById(R.id.detail_blog_approve);
        ImageView download = (ImageView)view.findViewById(R.id.detail_blog_download);
        final ImageView likeButton = (ImageView)view.findViewById(R.id.detail_blog_like);

        Glide.with(getActivity()).load(blog.getPhotoUrl()).into(simpleDraweeView);
        title.setText(blog.getTitle());
        name.setText(blog.getBloggerName());
        description.setText(blog.getDescription());
        likes.setText(String.valueOf(blog.getLikes()));
        button.setVisibility(isPublic ? View.GONE : View.VISIBLE);
        button.setText(blog.getApproved().equals("true") ? "Approved" : "Not Approved");
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager wpm = WallpaperManager.getInstance(getActivity());
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                new SetWallPaperTask(blog.getPhotoUrl(), progressDialog, wpm)
                        .execute();
            }
        });
        dataStash.userBase.child("User1").child(blog.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null && (boolean)dataSnapshot.getValue())
                            likeButton.setImageDrawable(
                                    getResources().getDrawable(R.drawable.ic_like_fill)
                            );
                        else
                            likeButton.setImageDrawable(
                                    getResources().getDrawable(R.drawable.ic_like_border)
                            );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataStash.userBase.child("User1").child(blog.getKey())
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue() == null) {
                                            likeButton.setImageDrawable(getResources()
                                                            .getDrawable(R.drawable.ic_like_border, null));
                                            onLikeClicked(blog, true);
                                        } else {
                                            likeButton.setImageDrawable(getResources()
                                                            .getDrawable(R.drawable.ic_like_fill, null));
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

        return view;
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
                        //likes.setText(String.valueOf(blog1.getLikes()));
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
}
