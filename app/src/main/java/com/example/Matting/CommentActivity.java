package com.example.Matting;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private ImageButton sendButton;
    private FirebaseFirestore db;
    private String postId = "post1"; // 게시물 ID (테스트용)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Firestore 초기화
        db = FirebaseFirestore.getInstance();

        // 댓글 데이터 초기화
        commentList = new ArrayList<>();

        // RecyclerView 설정
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // Firestore에서 댓글 가져오기
        loadCommentsFromFirestore();

        // 댓글 입력 및 전송
        commentInput = findViewById(R.id.commentInput);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String newComment = commentInput.getText().toString().trim();
            if (!newComment.isEmpty()) {
                // Firestore에 댓글 추가
                Date currentTime = new Date();
                Map<String, Object> comment = new HashMap<>();
                comment.put("username", "current_user"); // 현재 사용자 이름
                comment.put("content", newComment);
                comment.put("timestamp", currentTime);

                db.collection("posts").document(postId).collection("comments")
                        .add(comment)
                        .addOnSuccessListener(docRef -> {
                            Log.d("Firestore", "Comment added with ID: " + docRef.getId());
                            // Firestore에서 다시 댓글 가져오기
                            loadCommentsFromFirestore();
                            commentInput.setText(""); // 입력 필드 초기화
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Error adding comment", e));
            }
        });
    }

    private void loadCommentsFromFirestore() {
        db.collection("posts").document(postId).collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        commentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            commentList.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting comments: ", task.getException());
                    }
                });
    }
}
