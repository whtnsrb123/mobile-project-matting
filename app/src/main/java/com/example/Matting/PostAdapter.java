package com.example.Matting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

// Glide를 사용하는 경우 import 필요
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    private OnPostClickListener onPostClickListener;

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.onPostClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);



        holder.itemView.setOnClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onPostClick(post);
            }
        });

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
            // deleteButton null 확인
            if (holder.deleteButton == null) {
                Log.e("PostAdapter", "Delete button is not initialized properly.");
            }
            holder.deleteButton.setOnClickListener(v -> {
                if (context instanceof PostViewerActivity) {
                    ((PostViewerActivity) context).confirmDeletePost(post, position);
                } else {
                    Log.e("PostAdapter", "Context is not PostViewerActivity");
                }
            });
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

            // 좋아요 버튼 초기 상태 설정
            holder.reactionButton.setImageResource(post.isReacted() ? R.drawable.reaction_active : R.drawable.reaction_default);
            holder.reactionCountTextView.setText(String.valueOf(post.getReactionCount()));

            // 좋아요 버튼 클릭 이벤트 처리
            holder.reactionButton.setOnClickListener(v -> {
                boolean isReacted = !post.isReacted(); // 상태 반전
                post.setReacted(isReacted);

                if (isReacted) {
                    post.setReactionCount(post.getReactionCount() + 1); // 좋아요 증가
                } else {
                    post.setReactionCount(Math.max(post.getReactionCount() - 1, 0)); // 좋아요 감소
                }

                // Firestore 업데이트
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("posts")
                        .document(post.getDocumentId()) // `Post` 클래스에 `documentId` 추가 필요
                        .update(
                                "reactionCount", post.getReactionCount(),
                                "reacted", post.isReacted()
                        )
                        .addOnSuccessListener(aVoid -> {
                            // UI 업데이트
                            holder.reactionCountTextView.setText(String.valueOf(post.getReactionCount()));
                            holder.reactionButton.setImageResource(post.isReacted() ? R.drawable.reaction_active : R.drawable.reaction_default);
                        })
                        .addOnFailureListener(e -> {
                            // 에러 처리
                            e.printStackTrace();
                        });
            });

            // 댓글 개수 Firestore에서 로드
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("posts")
                    .document(post.getDocumentId())
                    .collection("comments")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int commentCount = task.getResult().size();
                            holder.commentCountTextView.setText(String.valueOf(commentCount));
                        }
                    });

            // 댓글 버튼 클릭 이벤트
            holder.commentButton.setOnClickListener(v -> {
                // 댓글 다이얼로그 (BottomSheetDialogFragment) 호출
                CommentBottomSheet bottomSheet = CommentBottomSheet.newInstance(post.getDocumentId());
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
            });
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
        ImageView deleteButton; // 삭제 버튼 추가

        // 추가된 뷰 요소
        ImageView reactionButton;
        ImageView commentButton;
        TextView reactionCountTextView;
        TextView commentCountTextView;

        public PostViewHolder(@NonNull View itemView, boolean imageOnlyMode) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);


            if (!imageOnlyMode) {
                deleteButton = itemView.findViewById(R.id.deleteButton);
                username = itemView.findViewById(R.id.username);
                postDescription = itemView.findViewById(R.id.postDescription);
                postTimestamp = itemView.findViewById(R.id.postTimestamp);

                // 추가된 뷰 요소 초기화
                reactionButton = itemView.findViewById(R.id.reactionButton);
                commentButton = itemView.findViewById(R.id.commentButton);
                reactionCountTextView = itemView.findViewById(R.id.reactionCount);
                commentCountTextView = itemView.findViewById(R.id.commentCount);
            }
        }
    }

    // Base64 문자열을 Bitmap으로 변환하는 메서드
    private Bitmap decodeBase64(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}