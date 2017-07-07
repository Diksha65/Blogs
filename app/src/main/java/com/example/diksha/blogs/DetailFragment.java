package com.example.diksha.blogs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    public static final String TAG = "DetailFragment";
    private Blog blog;

    public static DetailFragment newInstance(){
        return new DetailFragment();
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
        Button button = (Button)view.findViewById(R.id.detail_blog_approve);

        Glide.with(getActivity()).load(blog.getPhotoUrl()).into(simpleDraweeView);
        title.setText(blog.getTitle());
        name.setText(blog.getBloggerName());
        description.setText(blog.getDescription());
        button.setVisibility(isPublic ? View.GONE : View.VISIBLE);
        button.setText(blog.getApproved().equals("true") ? "Approved" : "Not Approved");

        return view;
    }

}
