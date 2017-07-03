package com.example.diksha.blogs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by diksha on 3/7/17.
 */

public class DataStash {

    DatabaseReference database;
    StorageReference storage;

    List<Blog> publicBlogList;
    List<Blog> membersBlogList;
    List<Blog> adminBlogList;

    private static DataStash sDataStash;

    static DataStash getDataStash(){
        if(sDataStash == null)
            sDataStash = new DataStash();
        return sDataStash;
    }

    private DataStash(){
        database = FirebaseDatabase.getInstance().getReference("Blogs");
        storage = FirebaseStorage.getInstance().getReference("Photos");

        childEventListeners = new ConcurrentHashMap<>();
        valueEventListeners = new ConcurrentHashMap<>();

        publicBlogList = new ArrayList<>();
        membersBlogList = new ArrayList<>();
        adminBlogList = new ArrayList<>();
    }


    /**
     *      Creating Fragments
     */

    public void beginFragment(Fragment fragment, int containerId, FragmentManager fragmentManager){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }


    /**
     *      Value Event Listeners
     */

    private Map<String,
            Pair<DatabaseReference, ValueEventListener>> valueEventListeners;

    public boolean attachValueEventListener(String name, DatabaseReference ref,
                                            ValueEventListener listener){
        if(valueEventListeners.containsKey(name))
            return false;

        valueEventListeners.put(name, new Pair<>(ref, listener));
        valueEventListeners.get(name).first
                .addValueEventListener(valueEventListeners.get(name).second);
        return true;
    }

    public void detachValueEventListener(String name){
        Pair<DatabaseReference, ValueEventListener> refListenerPair = valueEventListeners.get(name);
        refListenerPair.first.removeEventListener(refListenerPair.second);
        valueEventListeners.remove(name);
    }

    /**
     *      Child Event Listeners
     */

    private Map<String,
            Pair<DatabaseReference, ChildEventListener>> childEventListeners;

    public boolean attachChildEventListener(String name, DatabaseReference ref,
                                            ChildEventListener listener){
        if(childEventListeners.containsKey(name))
            return false;

        childEventListeners.put(name, new Pair<>(ref, listener));
        childEventListeners.get(name).first
                .addChildEventListener(childEventListeners.get(name).second);
        return true;
    }

    public void detachChildEventListener(String name){
        Pair<DatabaseReference, ChildEventListener> refListenerPair = childEventListeners.get(name);
        refListenerPair.first.removeEventListener(refListenerPair.second);
        childEventListeners.remove(name);
    }

    /**
     *      Event Listeners
     */

    public void detachEventlisteners(){
        for(String name: valueEventListeners.keySet())
            detachValueEventListener(name);

        for(String name: childEventListeners.keySet())
            detachChildEventListener(name);
    }

}
