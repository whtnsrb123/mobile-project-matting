package com.example.Matting;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Matting.CommentBottomSheet;
import com.example.Matting.FeedItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private final List<FeedItem> feedItems;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore 인스턴스

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
        FeedItem feedItem = feedItems.get(position);

        holder.usernameTextView.setText(feedItem.getUsername());
        holder.postContentTextView.setText(feedItem.getPostContent());
        holder.reactionCountTextView.setText(String.valueOf(feedItem.getReactionCount()));
        holder.commentCountTextView.setText(String.valueOf(feedItem.getCommentCount()));

        // Timestamp를 상대적 시간으로 변환하여 표시
        if (feedItem.getTimestamp() != null) {
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(feedItem.getTimestamp().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            holder.timestampTextView.setText(relativeTime);
        } else {
            holder.timestampTextView.setText("Unknown");
        }

        // Glide로 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(feedItem.getImageUrl())
                .into(holder.postImageView);

        // 감정 버튼 클릭 이벤트 처리
        holder.reactionButton.setOnClickListener(v -> {
            boolean isReacted = !feedItem.isReacted(); // 감정 상태를 반전
            feedItem.setReacted(isReacted); // 상태 업데이트

            if (isReacted) {
                feedItem.setReactionCount(feedItem.getReactionCount() + 1); // 최초 클릭 시 +1
                holder.reactionButton.setImageResource(R.drawable.reaction_active); // 감정 반응 아이콘
            } else {
                feedItem.setReactionCount(Math.max(feedItem.getReactionCount() - 1, 0)); // 다시 클릭 시 -1
                holder.reactionButton.setImageResource(R.drawable.reaction_default); // 기본 아이콘
            }

            holder.reactionCountTextView.setText(String.valueOf(feedItem.getReactionCount())); // UI 업데이트

            // Firestore 업데이트
            db.collection("posts")
                    .document(feedItem.getDocumentId()) // 각 FeedItem의 문서 ID 필요
                    .update("reactionCount", feedItem.getReactionCount(), "reacted", feedItem.isReacted())
                    .addOnSuccessListener(aVoid -> {
                        // 업데이트 성공
                    })
                    .addOnFailureListener(e -> {
                        // 업데이트 실패
                        e.printStackTrace();
                    });
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
        return feedItems.size();
    }

    public void updateFeedItems(List<FeedItem> newFeedItems) {
        this.feedItems.clear();
        this.feedItems.addAll(newFeedItems);
        notifyDataSetChanged();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        TextView postContentTextView;
        TextView reactionCountTextView;
        TextView commentCountTextView;
        TextView timestampTextView;
        ImageView postImageView;
        ImageView reactionButton;
        ImageView commentButton;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTextView = itemView.findViewById(R.id.username);
            postContentTextView = itemView.findViewById(R.id.postContent);
            reactionCountTextView = itemView.findViewById(R.id.reactionCount);
            commentCountTextView = itemView.findViewById(R.id.commentCount);
            timestampTextView = itemView.findViewById(R.id.postTimestamp);
            postImageView = itemView.findViewById(R.id.imageView);
            reactionButton = itemView.findViewById(R.id.reactionButton);
            commentButton = itemView.findViewById(R.id.commentButton);
        }
    }
}
