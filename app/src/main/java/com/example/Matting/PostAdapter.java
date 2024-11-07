package com.example.Matting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private boolean imageOnlyMode; // 이미지 전용 모드 플래그 추가
    private OnItemClickListener onItemClickListener;

    public PostAdapter(Context context, List<Post> postList, boolean imageOnlyMode) {
        this.context = context;
        this.postList = postList;
        this.imageOnlyMode = imageOnlyMode;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // imageOnlyMode에 따라 다른 레이아웃 사용
        int layoutId = imageOnlyMode ? R.layout.fragment_post_image_only : R.layout.fragment_post;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // 공통으로 설정할 부분 (이미지)
        Glide.with(context).load(post.getImageResId()).into(holder.postImage);

        // imageOnlyMode가 아닌 경우에만 추가 정보를 설정
        if (!imageOnlyMode) {
            holder.username.setText(post.getUsername());
            holder.postDescription.setText(post.getDescription());
            holder.postTimestamp.setText(post.getTimestamp());
        }

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView username;
        TextView postDescription;
        TextView postTimestamp;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);

            // imageOnlyMode에서는 다른 뷰들이 없으므로 null 검사 필요
            username = itemView.findViewById(R.id.username);
            postDescription = itemView.findViewById(R.id.postDescription);
            postTimestamp = itemView.findViewById(R.id.postTimestamp);
        }
    }
}
