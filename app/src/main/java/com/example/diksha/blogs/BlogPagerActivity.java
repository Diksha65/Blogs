package com.example.diksha.blogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class BlogPagerActivity extends AppCompatActivity {

    private static final String TAG = "BlogPagerActivity";
    private ViewPager viewPager;
    private static DataStash dataStash = DataStash.DATA_STASH;
    private static final String EXTRA = "111";
    private FragmentStatePagerAdapter adapter;
    public static Intent newIntent(Context packageContext, Blog blog){
        Intent intent = new Intent(packageContext, BlogPagerActivity.class);
        intent.putExtra(EXTRA, blog);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_pager);

        Log.e(TAG, String.valueOf(dataStash.publicBlogList.size()));
        Blog blog = (Blog)getIntent().getSerializableExtra(EXTRA);

        viewPager = (ViewPager)findViewById(R.id.blog_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Blog blog = dataStash.publicBlogList.get(position);
                return DetailFragment.newInstance(blog, true);
            }

            @Override
            public int getCount() {
                return dataStash.publicBlogList.size();
            }
        };

        viewPager.setAdapter(adapter);
        for(int i=0;i<dataStash.publicBlogList.size(); ++i){
            if(dataStash.publicBlogList.get(i).getKey().equals(blog.getKey())){
                viewPager.setCurrentItem(i);
                break;
            }
        }
        addBLogsToList();
        adapter.notifyDataSetChanged();
    }

    private void addBLogsToList(){

        if(dataStash.attachChildEventListener("LALA",
                dataStash.database.child("VisibleToAll"),
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Blog blog = dataSnapshot.getValue(Blog.class);
                        if(notAdded(blog)) {
                            Log.e(TAG, blog.getTitle());
                            Log.e(TAG, "Changing");
                            dataStash.publicBlogList.add(blog);
                            adapter.notifyDataSetChanged();
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
            Toast.makeText(this, "Error occured. Try Again!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean notAdded(Blog blog){
        for(int i=0;i<dataStash.publicBlogList.size(); ++i)
            if(dataStash.publicBlogList.get(i).getKey().equals(blog.getKey()))
                return false;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataStash.detachChildEventListener("LALA");
    }
}