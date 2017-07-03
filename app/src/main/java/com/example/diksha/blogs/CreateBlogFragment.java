package com.example.diksha.blogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by diksha on 28/6/17.
 */
public class CreateBlogFragment extends PermissionFragment {

    protected static final int REQUEST_PERMISSIONS     = 0;
    private static final int PICK_IMAGE_REQUEST      = 1;
    private static final int CLICK_IMAGE_REQUEST     = 2;

    private static final int STORAGE_REQUEST_CODE    = 3;
    private static final int CAMERA_REQUEST_CODE     = 4;

    private static final String TAG = "CreateBlogFragment";
    private String name, describe;
    private Blog blog;
    private DataStash dataStash = DataStash.DATA_STASH;

    private EditText title, description;
    private ImageView simpleDraweeView;
    private Uri path = null;
    private Uri currentImageUri = null;
    private File image;
    private Context context = getActivity();

    public static CreateBlogFragment newInstance(){
        return new CreateBlogFragment();
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_blog, container, false);

        title = (EditText) view.findViewById(R.id.blog_name);
        description = (EditText) view.findViewById(R.id.blog_description);
        simpleDraweeView = (ImageView) view.findViewById(R.id.blog_imageview);

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

        view.findViewById(R.id.blog_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions(getActivity())){
                    onPermissionGranted();
                } else
                    requestPermissions(getActivity(), REQUEST_PERMISSIONS);
            }
        });


        view.findViewById(R.id.blog_attach).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        view.findViewById(R.id.blog_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path != null && title != null) {
                    addPhotoToStorage(path);
                } else if(currentImageUri != null && title != null){
                    addPhotoToStorage(currentImageUri);
                } else {
                    notifyUser(v, "Data missing. Re-check data.");
                }
            }
        });

        view.findViewById(R.id.cancel_blog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyUser(v, "Cancelled");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return view;
    }

    private void notifyUser(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case PICK_IMAGE_REQUEST:
                    path = data.getData();
                    simpleDraweeView.setImageURI(path);
                    break;
                case CLICK_IMAGE_REQUEST:
                    simpleDraweeView.setImageURI(currentImageUri);
                    break;
            }
        }
    }

    private Uri getImageFileUri(){
        File imagePath = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyProject");
        if (! imagePath.exists()){
            if (! imagePath.mkdirs()){
                return null;
            }else{
                //create new folder
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        image = new File(imagePath,"MyProject_"+ timeStamp + ".jpg");
        return FileProvider.getUriForFile(context, getActivity().getApplicationContext().getPackageName() + ".provider", image);
    }

    protected void onPermissionGranted(){
        currentImageUri = getImageFileUri();
        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri); // set the image file name
        startActivityForResult(intentPicture, CLICK_IMAGE_REQUEST);
    }

    private void addToDatabase(Uri uri){
        blog = new Blog(name, uri.toString(), describe, "Diksha", "false");
        String key = dataStash.database.child("DikshaId").push().getKey();
        blog.setKey(key);
        dataStash.database.child("DikshaId").child(key).setValue(blog);
        dataStash.database.child("UnapprovedBlogs").child("DikshaId").child(key).setValue(blog);
    }

    private void addPhotoToStorage(Uri path){
        StorageReference sr = dataStash.storage.child(path.getLastPathSegment());
        sr.putFile(path)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        addToDatabase(taskSnapshot.getDownloadUrl());
                    }
                });
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
    }

}


 /*

  private Uri getImageFileUri(){
        File imagePath = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyProject");
        if (! imagePath.exists()){
            if (! imagePath.mkdirs()){
                return null;
            }else{
                //create new folder
            }
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        image = new File(imagePath,"MyProject_"+ timeStamp + ".jpg");

        if(!image.exists()){
            try {
                image.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Create an File Uri
        //FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", createImageFile());
        //Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile());
        return FileProvider.getUriForFile(context, getActivity().getApplicationContext().getPackageName() + ".provider", image);
    }

 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null){
                path = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                    simpleDraweeView.setImageBitmap(bitmap);
                } catch (IOException ioe){
                    ioe.printStackTrace();
                }
            } else if(requestCode == CLICK_IMAGE_REQUEST){
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), currentImageUri);
                    simpleDraweeView.setImageBitmap(bitmap);
                } catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
                    }else{
                        currentImageUri = getImageFileUri();
                        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri); // set the image file name
                        // start the image capture Intent
                        startActivityForResult(intentPicture, CLICK_IMAGE_REQUEST);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(),"Doesn't have have permission... ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case STORAGE_REQUEST_CODE : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentImageUri = getImageFileUri();
                    Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intentPicture, CLICK_IMAGE_REQUEST);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(),"Doesn't have have permission... ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


        view.findViewById(R.id.blog_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                else{
                    if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
                    else {
                        currentImageUri = getImageFileUri();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
                        startActivityForResult(intent, CLICK_IMAGE_REQUEST);
                    }
                }
            }
        });

    void setReducedImageSize() {
        int targetImageViewWidth = simpleDraweeView.getWidth();
        int targetImageViewHeight = simpleDraweeView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        simpleDraweeView.setImageBitmap(photoReducedSizeBitmp);
    }
 */