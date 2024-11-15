package com.example.Matting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

        holder.username.setText(feedItem.getUsername());
        holder.postTimestamp.setText(feedItem.getTimestamp());
        holder.postContent.setText(feedItem.getPostContent());

        Glide.with(holder.imageView.getContext())
                .load(feedItem.getImageResource())
                .into(holder.imageView);

        holder.commentCount.setText(String.valueOf(feedItem.getCommentCount()));
        holder.reactionCount.setText(String.valueOf(feedItem.getReactionCount()));

        // 감정 버튼 클릭 이벤트 처리
        holder.reactionButton.setOnClickListener(v -> {
            boolean isReacted = feedItem.toggleReaction(); // 감정 상태를 토글
            if (isReacted) {
                holder.reactionButton.setImageResource(R.drawable.reaction_active); // 감정 반응 아이콘
            } else {
                holder.reactionButton.setImageResource(R.drawable.reaction_default); // 기본 아이콘
            }
        });

        // 초기 상태에 따른 아이콘 설정
        if (feedItem.isReacted()) {
            holder.reactionButton.setImageResource(R.drawable.reaction_active);
        } else {
            holder.reactionButton.setImageResource(R.drawable.reaction_default);
        }

        // 댓글 버튼 클릭 이벤트 - BottomSheetDialogFragment 호출
        holder.commentButton.setOnClickListener(v -> {
            CommentBottomSheet bottomSheet = new CommentBottomSheet();
            bottomSheet.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), bottomSheet.getTag());
        });
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
        ImageView reactionButton;
        ImageView commentButton; // 댓글 버튼 추가
        TextView commentCount;
        TextView reactionCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            postTimestamp = itemView.findViewById(R.id.postTimestamp);
            postContent = itemView.findViewById(R.id.postContent);
            imageView = itemView.findViewById(R.id.imageView);
            reactionButton = itemView.findViewById(R.id.reactionButton);
            commentButton = itemView.findViewById(R.id.commentButton); // 댓글 버튼 초기화
            commentCount = itemView.findViewById(R.id.commentCount);
            reactionCount = itemView.findViewById(R.id.reactionCount);
        }
    }
}
