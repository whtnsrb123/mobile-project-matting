package com.example.Matting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class PostFragment extends Fragment {
    private Post post;

    public PostFragment(Post post) {
        this.post = post;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        ImageView postImage = view.findViewById(R.id.postImage);
        TextView postDescription = view.findViewById(R.id.postDescription);

        // getImageResId()로 변경
        Glide.with(this).load(post.getImageResId()).into(postImage);
        postDescription.setText(post.getDescription());

        return view;
    }
}
