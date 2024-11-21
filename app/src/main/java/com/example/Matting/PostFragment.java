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

        // View 요소들 연결
        ImageView profileImage = view.findViewById(R.id.profileImage);
        TextView username = view.findViewById(R.id.username);
        ImageView postImage = view.findViewById(R.id.postImage);
        ImageView likeButton = view.findViewById(R.id.likeButton);
        ImageView commentButton = view.findViewById(R.id.commentButton);
        TextView postDescription = view.findViewById(R.id.postDescription);
        TextView postTimestamp = view.findViewById(R.id.postTimestamp);

        // 게시글 데이터 설정
        username.setText(post.getUsername());  // 사용자 이름 설정
        Glide.with(this).load(post.getImageResource()).into(postImage); // 게시글 이미지 설정
        postDescription.setText(post.getPostContent()); // 게시글 본문 설정
        // Post 클래스의 메서드로 Timestamp 변환
        postTimestamp.setText(post.getFormattedTimestamp());

        // 좋아요 버튼과 댓글 버튼은 여기서 기능을 추가할 수도 있음

        return view;
    }
}
