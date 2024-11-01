package com.example.Matting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private List<com.example.Matting.FeedItem> feedItems;

    public FeedAdapter(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FeedItem item = feedItems.get(position);
        holder.usernameTextView.setText(item.getUsername());
        holder.postContentTextView.setText(item.getPostContent());
        holder.imageView.setImageResource(item.getImageResource());
        holder.commentCountTextView.setText(String.valueOf(item.getCommentCount()));
        holder.reactionCountTextView.setText(String.valueOf(item.getReactionCount()));

        // 댓글 버튼 클릭 시의 동작
        holder.commentButton.setOnClickListener(v -> {
            // 댓글 버튼 클릭 시 동작 추가
        });

        // 감정 버튼 클릭 시의 동작
        holder.reactionButton.setOnClickListener(v -> {
            // 감정 버튼 클릭 시 동작 추가
        });
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public TextView postContentTextView;
        public ImageView imageView;
        public TextView commentCountTextView;
        public TextView reactionCountTextView;
        public View commentButton;
        public View reactionButton;

        public FeedViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            postContentTextView = itemView.findViewById(R.id.postContent);
            imageView = itemView.findViewById(R.id.imageView);
            commentCountTextView = itemView.findViewById(R.id.commentCount);
            reactionCountTextView = itemView.findViewById(R.id.reactionCount);
            commentButton = itemView.findViewById(R.id.commentButton);
            reactionButton = itemView.findViewById(R.id.reactionButton);
        }
    }
}
