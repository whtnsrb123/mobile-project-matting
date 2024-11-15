package com.example.Matting;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

        // Timestamp를 상대적 시간으로 변환하여 표시
        if (feedItem.getTimestamp() != null) {
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                    feedItem.getTimestamp().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS
            );
            holder.timestampTextView.setText(relativeTime);
        } else {
            holder.timestampTextView.setText("Unknown");
        }

        // Glide로 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(feedItem.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // 로딩 중 기본 이미지
                .error(R.drawable.error_image)             // 로드 실패 시 기본 이미지
                .into(holder.postImageView);

        // Firestore에서 댓글 개수 가져오기
        db.collection("posts")
                .document(feedItem.getDocumentId())
                .collection("comments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int commentCount = querySnapshot.size();
                            holder.commentCountTextView.setText(String.valueOf(commentCount));
                            feedItem.setCommentCount(commentCount); // FeedItem에도 업데이트
                        }
                    }
                });

        // Firestore에서 감정 상태 초기화
        db.collection("posts")
                .document(feedItem.getDocumentId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Firestore에서 감정 상태 가져오기
                        Boolean reacted = documentSnapshot.getBoolean("reacted");
                        Long reactionCount = documentSnapshot.getLong("reactionCount");

                        // FeedItem 및 UI 업데이트
                        if (reacted != null) feedItem.setReacted(reacted);
                        if (reactionCount != null) feedItem.setReactionCount(reactionCount.intValue());

                        holder.reactionCountTextView.setText(String.valueOf(feedItem.getReactionCount()));
                        holder.reactionButton.setImageResource(feedItem.isReacted() ? R.drawable.reaction_active : R.drawable.reaction_default);
                    }
                });

        // 감정 버튼 클릭 이벤트 처리
        holder.reactionButton.setOnClickListener(v -> {
            boolean isReacted = !feedItem.isReacted(); // 감정 상태를 반전
            feedItem.setReacted(isReacted); // 상태 업데이트

            if (isReacted) {
                feedItem.setReactionCount(feedItem.getReactionCount() + 1); // 최초 클릭 시 +1
            } else {
                feedItem.setReactionCount(Math.max(feedItem.getReactionCount() - 1, 0)); // 다시 클릭 시 -1
            }

            // Firestore 업데이트
            db.collection("posts")
                    .document(feedItem.getDocumentId()) // 각 FeedItem의 문서 ID 필요
                    .update(
                            "reactionCount", feedItem.getReactionCount(),
                            "reacted", feedItem.isReacted()
                    )
                    .addOnSuccessListener(aVoid -> {
                        // UI 업데이트
                        holder.reactionCountTextView.setText(String.valueOf(feedItem.getReactionCount()));
                        holder.reactionButton.setImageResource(feedItem.isReacted() ? R.drawable.reaction_active : R.drawable.reaction_default);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error updating reaction", e);
                    });
        });

        // 초기 상태에 따른 아이콘 설정
        holder.reactionButton.setImageResource(feedItem.isReacted() ? R.drawable.reaction_active : R.drawable.reaction_default);

        // 댓글 버튼 클릭 이벤트 - BottomSheetDialogFragment 호출
        holder.commentButton.setOnClickListener(v -> {
            // 댓글 BottomSheetDialog를 호출하고 게시물 ID를 전달
            CommentBottomSheet bottomSheet = CommentBottomSheet.newInstance(feedItem.getDocumentId());
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
