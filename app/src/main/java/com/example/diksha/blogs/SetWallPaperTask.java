package com.example.diksha.blogs;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by diksha on 8/7/17.
 */

public class SetWallPaperTask extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;
    private String url;
    private WallpaperManager wpm;

    public SetWallPaperTask(String url, ProgressDialog progressDialog, WallpaperManager wallpaperManager) {
        super();
        this.url = url;
        this.progressDialog = progressDialog;
        wpm = wallpaperManager;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Changing Wallpaper...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        Log.e("Wallpaper", "Wallpaper set successfully");
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            InputStream ins = new URL(url).openStream();
            wpm.setStream(ins);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Executed";
    }
}

/*
    public class SetWallPaperTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
            try {
                wallpaperManager.setBitmap(bitmap);
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Set wallpaper successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap result= null;
            try {
                result = Picasso.with(getActivity()).load(blog.getPhotoUrl()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
            try {
                wallpaperManager.setBitmap(result);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return result;
        }
    }*/
