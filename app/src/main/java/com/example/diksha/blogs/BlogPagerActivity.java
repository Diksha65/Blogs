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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BlogPagerActivity extends AppCompatActivity {

    private static final String TAG = "BlogPagerActivity";
    private ViewPager viewPager;
    private static DataStash dataStash = DataStash.DATA_STASH;
    private static final String EXTRA = "111", TYPE = "222";
    private DataStash.type type;
    private List<Blog> list;
    private FragmentStatePagerAdapter adapter;
    public static Intent newIntent(Context packageContext, Blog blog, DataStash.type viewType){
        Intent intent = new Intent(packageContext, BlogPagerActivity.class);
        intent.putExtra(EXTRA, blog);
        intent.putExtra(TYPE, viewType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_pager);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Blog blog = (Blog)getIntent().getSerializableExtra(EXTRA);
        type = (DataStash.type)getIntent().getSerializableExtra(TYPE);

        setListType();
        Log.e(TAG, list.toString());

        viewPager = (ViewPager)findViewById(R.id.blog_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Blog blog = list.get(position);
                return DetailFragment.newInstance(blog, type);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        };

        viewPager.setAdapter(adapter);
        for(int i=0;i<list.size(); ++i){
            if(list.get(i).getKey().equals(blog.getKey())){
                viewPager.setCurrentItem(i);
                break;
            }
        }
        addBLogsToList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setListType(){
        switch (type){
            case isPublic:
                list = dataStash.publicBlogList;
                break;
            case isAdmin:
                list = dataStash.adminBlogList;
                break;
            case isMember:
                list = dataStash.membersBlogList;
                break;
            default:
                Log.e(TAG, "typeError");
        }
    }

    private void addBLogsToList(){

        if(dataStash.attachValueEventListener("LALA",
                dataStash.database.child("VisibleToAll"),
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Blog blog = snapshot.getValue(Blog.class);
                            if (notAdded(blog)) {
                                dataStash.publicBlogList.add(blog);
                                adapter.notifyDataSetChanged();
                            }
                        }
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
        for(int i=0;i<list.size(); ++i)
            if(list.get(i).getKey().equals(blog.getKey()))
                return false;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataStash.detachValueEventListener("LALA");
    }
}