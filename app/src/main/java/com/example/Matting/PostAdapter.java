package com.example.Matting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Glide를 사용하는 경우 import 필요
import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private boolean imageOnlyMode;

    public PostAdapter(Context context, List<Post> postList, boolean imageOnlyMode) {
        this.context = context;
        this.postList = postList;
        this.imageOnlyMode = imageOnlyMode;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (imageOnlyMode) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_post_image_only, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_post, parent, false);
        }
        return new PostViewHolder(view, imageOnlyMode);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);



        if (imageOnlyMode) {
            // 이미지 전용 모드에서는 이미지만 표시
            if (post.getImageResource() != null) {
                // 이미지가 Base64 문자열인 경우 디코딩 필요
                Bitmap bitmap = decodeBase64(post.getImageResource());
                holder.postImage.setImageBitmap(bitmap);
            } else {
                holder.postImage.setImageResource(R.drawable.mosu);
            }
        } else {
            // 전체 게시물 표시 모드
            if (post.getUsername() != null) {
                holder.username.setText(post.getUsername());
            } else {
                holder.username.setText("Unknown User");
            }

            if (post.getPostContent() != null) {
                holder.postDescription.setText(post.getPostContent());
            } else {
                holder.postDescription.setText("No Description");
            }

            if (post.getTimestamp() != null) {
                holder.postTimestamp.setText(post.getFormattedTimestamp()); // 포맷된 시간 표시
            } else {
                holder.postTimestamp.setText("Unknown Time");
            }

            if (post.getImageResource() != null) {
                // 이미지가 Base64 문자열인 경우 디코딩 필요
                Bitmap bitmap = decodeBase64(post.getImageResource());
                holder.postImage.setImageBitmap(bitmap);
            } else {
                holder.postImage.setImageResource(R.drawable.mosu);
            }
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder 클래스 정의
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView username;
        TextView postDescription;
        TextView postTimestamp;

        public PostViewHolder(@NonNull View itemView, boolean imageOnlyMode) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);

            if (!imageOnlyMode) {
                username = itemView.findViewById(R.id.username);
                postDescription = itemView.findViewById(R.id.postDescription);
                postTimestamp = itemView.findViewById(R.id.postTimestamp);
            }
        }
    }

    // Base64 문자열을 Bitmap으로 변환하는 메서드
    private Bitmap decodeBase64(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}