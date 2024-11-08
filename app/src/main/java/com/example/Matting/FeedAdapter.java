package com.example.Matting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private List<FeedItem> feedItems;
    private Map<Integer, List<String>> commentsMap; // 위치별 댓글을 저장하는 맵

    public FeedAdapter(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
        this.commentsMap = new HashMap<>();
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

        // 댓글 버튼 클릭 시 하단 시트 표시
        holder.commentButton.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext());
            View bottomSheetView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.comment_bottom_sheet, (ViewGroup) holder.itemView.getParent(), false);

            bottomSheetDialog.setContentView(bottomSheetView);

            // 하단 시트 내부 뷰 초기화
            RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerViewComments);
            EditText editTextComment = bottomSheetView.findViewById(R.id.editTextComment);
            Button buttonPostComment = bottomSheetView.findViewById(R.id.buttonPostComment);

            // 해당 위치의 댓글 목록 가져오기
            List<String> comments = commentsMap.getOrDefault(position, new ArrayList<>());

            // 댓글 어댑터 설정
            CommentAdapter adapter = new CommentAdapter(comments);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            recyclerView.setAdapter(adapter);

            // 댓글 추가 버튼 클릭 시 동작
            buttonPostComment.setOnClickListener(view -> {
                String newComment = editTextComment.getText().toString().trim();
                if (!newComment.isEmpty()) {
                    comments.add(newComment);
                    adapter.notifyItemInserted(comments.size() - 1);
                    editTextComment.setText("");
                    recyclerView.scrollToPosition(comments.size() - 1);

                    // 새로운 댓글을 맵에 저장 (메모리 상에만 저장)
                    commentsMap.put(position, comments);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            });

            bottomSheetDialog.show();
        });

        // 감정 버튼 클릭 시의 동작 (이미지 토글)
        holder.reactionButton.setOnClickListener(v -> {
            if (holder.isReacted) {
                // 원래 이미지로 돌아가기 (예: 비활성화 상태 이미지)
                holder.reactionButton.setImageResource(R.drawable.reaction_default);
            } else {
                // 다른 이미지로 변경 (예: 활성화 상태 이미지)
                holder.reactionButton.setImageResource(R.drawable.reaction_active);
            }
            holder.isReacted = !holder.isReacted; // 상태 반전
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
        public ImageView reactionButton;
        public boolean isReacted = false; // 감정 상태를 추적하는 변수

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
