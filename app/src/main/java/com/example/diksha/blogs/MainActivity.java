package com.example.diksha.blogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity{

    private static final String FRAGMENT_HOME = "Home";
    private static final String FRAGMENT_OTHER = "Other";
    private BottomNavigationView navigation;
    private FragmentManager fragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewFragment(PublicBlogFragment.newInstance(), FRAGMENT_HOME);
                    break;
                case R.id.navigation_dashboard:
                    viewFragment(MembersBlogFragment.newInstance(), FRAGMENT_OTHER);
                    break;
                case R.id.navigation_notifications:
                    viewFragment(AdminBlogFragment.newInstance(), FRAGMENT_OTHER);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_containcer, PublicBlogFragment.newInstance()).commit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void viewFragment(Fragment fragment, String name){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_containcer, fragment);
        // 1. Know how many fragments there are in the stack
        final int count = fragmentManager.getBackStackEntryCount();
        // 2. If the fragment is **not** "home type", save it to the stack
        if( name.equals( FRAGMENT_OTHER) ) {
            fragmentTransaction.addToBackStack(name);
        }
        // Commit !
        fragmentTransaction.commit();
        // 3. After the commit, if the fragment is not an "home type" the back stack is changed, triggering the
        // OnBackStackChanged callback
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // If the stack decreases it means I clicked the back button
                if( fragmentManager.getBackStackEntryCount() <= count){
                    // pop all the fragment and remove the listener
                    fragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    // set the home button selected
                    navigation.getMenu().getItem(0).setChecked(true);
                }
            }
        });
    }
}
/*
public class MainActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private static final int PICK_IMAGE_REQUEST = 234;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private List<Blog> blogList;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("images");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("blogs");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.blogs_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BlogsAdapter(blogList));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filPath = data.getData();
            storageReference.child(filPath.getLastPathSegment()).putFile(filPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Blog blog = new Blog("Beautiful Image",
                                    downloadUrl.toString(), null, "Diksha Agarwal");
                            blogList.add(blog);
                            databaseReference.push().setValue(blog);
                        }
                    });

        }
    }

    private class BlogsHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView descriptionTextView;
        TextView bloggerTextView;
        SimpleDraweeView photoView;

        public BlogsHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.blog_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.blog_description);
            bloggerTextView = (TextView) itemView.findViewById(R.id.blog_name);
            photoView = (SimpleDraweeView) itemView.findViewById(R.id.blog_image);
        }
    }

    private class BlogsAdapter extends RecyclerView.Adapter<BlogsHolder> {

        private List<Blog> blogList;

        public BlogsAdapter(List<Blog> blogs) {
            blogList = blogs;
        }

        @Override
        public BlogsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.blog_item, viewGroup, false);
            return new BlogsHolder(view);
        }

        @Override
        public void onBindViewHolder(BlogsHolder blogsHolder, int i) {
            Blog blog = blogList.get(i);
            blogsHolder.bloggerTextView.setText(blog.bloggerName);
            blogsHolder.descriptionTextView.setText(blog.getDescription());
            blogsHolder.titleTextView.setText(blog.getTitle());
            Uri uri = Uri.parse(blog.getPhotoUrl());
            blogsHolder.photoView.setImageURI(uri);
        }

        @Override
        public int getItemCount() {
            return blogList.size();
        }
    }
}


package com.example.diksha.blogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
*/
/**
 * Created by diksha on 28/6/17.
 */
/*
public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 123;
    private static final int CLICK_IMAGE_REQUEST = 234;
    private String name, describe;
    private Blog blog;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Blogs");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Photos");

    private EditText title, description;
    private ImageView camera, attach, simpleDraweeView;
    private Button createBlog;
    private Uri path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_blog);

        title = (EditText)findViewById(R.id.blog_name);
        description = (EditText)findViewById(R.id.blog_description);
        camera = (ImageView)findViewById(R.id.blog_camera);
        attach = (ImageView)findViewById(R.id.blog_attach);
        simpleDraweeView = (ImageView) findViewById(R.id.blog_image);
        createBlog = (Button)findViewById(R.id.blog_create);

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                describe = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CLICK_IMAGE_REQUEST);
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        createBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference sr = storageReference.child(path.getLastPathSegment());
                sr.putFile(path)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri url = taskSnapshot.getDownloadUrl();
                                blog = new Blog(name, url.toString(), describe, "Diksha");
                                databaseReference.push().setValue(blog);
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null){
                path = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    simpleDraweeView.setImageBitmap(bitmap);
                } catch (IOException ioe){
                    ioe.printStackTrace();
                }
            } else if(requestCode == CLICK_IMAGE_REQUEST && data != null && data.getData() != null){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                simpleDraweeView.setImageBitmap(photo);
            }
        }
    }
}
*/