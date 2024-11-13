package com.example.Matting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private final List<FeedItem> feedList;

    public FeedAdapter(List<FeedItem> feedList) {
        this.feedList = feedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedItem feedItem = feedList.get(position);

        // 사용자 이름
        holder.username.setText(feedItem.getUsername());

        // Timestamp
        holder.postTimestamp.setText(feedItem.getTimestamp());

        // 게시물 내용
        holder.postContent.setText(feedItem.getPostContent());

        // 게시물 이미지
        Glide.with(holder.imageView.getContext())
                .load(feedItem.getImageResource())
                .into(holder.imageView);

        // 댓글 수와 반응 수
        holder.commentCount.setText(String.valueOf(feedItem.getCommentCount()));
        holder.reactionCount.setText(String.valueOf(feedItem.getReactionCount()));
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView postTimestamp;
        TextView postContent;
        ImageView imageView;
        TextView commentCount;
        TextView reactionCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            postTimestamp = itemView.findViewById(R.id.postTimestamp);
            postContent = itemView.findViewById(R.id.postContent);
            imageView = itemView.findViewById(R.id.imageView);
            commentCount = itemView.findViewById(R.id.commentCount);
            reactionCount = itemView.findViewById(R.id.reactionCount);
        }
    }
}
 
