package com.example.Matting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_POST_ID = "postId";
    private String postId;
    private FirebaseFirestore db;
    private DatabaseReference realtimeDbRef;
    private String userId; // 현재 사용자 ID (Firebase Authentication의 UID 사용)

    public static CommentBottomSheet newInstance(String postId) {
        CommentBottomSheet fragment = new CommentBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
        }
        db = FirebaseFirestore.getInstance();
        realtimeDbRef = FirebaseDatabase.getInstance().getReference();

        // Firebase Authentication에서 현재 사용자 UID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid(); // Firebase Authentication의 UID 사용
        } else {
            userId = null;
            Log.e("FirebaseAuth", "User is not logged in.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_comment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Comment> commentList = new ArrayList<>();
        CommentAdapter adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);

        // Firestore에서 댓글 데이터 가져오기
        db.collection("posts").document(postId).collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Listen failed.", e);
                        return;
                    }
                    commentList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Comment comment = doc.toObject(Comment.class);

                        // Realtime Database에서 nicknames 가져오기
                        String userId = comment.getUsername();
                        realtimeDbRef.child("users").child(userId).child("nicknames")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String nickname = snapshot.exists() ? snapshot.getValue(String.class) : "익명";
                                        comment.setUsername(nickname); // 화면에서만 닉네임으로 변경
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("RealtimeDB", "Error fetching nicknames for userId: " + userId, error.toException());
                                    }
                                });
                        commentList.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                });

        // 댓글 입력
        EditText editTextComment = view.findViewById(R.id.editTextComment);
        ImageButton buttonSubmit = view.findViewById(R.id.buttonSubmitComment);
        buttonSubmit.setOnClickListener(v -> {
            String content = editTextComment.getText().toString();
            if (!content.isEmpty()) {
                addCommentToFirestore(userId, content, editTextComment);
            }
        });

        return view;
    }

    // Firestore에 댓글 저장
    private void addCommentToFirestore(String userId, String content, EditText editTextComment) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("username", userId); // Firestore의 username 필드에 userId 저장
        comment.put("content", content);
        comment.put("timestamp", new Date());

        db.collection("posts").document(postId).collection("comments")
                .add(comment)
                .addOnSuccessListener(docRef -> {
                    editTextComment.setText(""); // 입력 필드 초기화
                    Log.d("Firestore", "Comment added successfully.");
                })
                .addOnFailureListener(error -> Log.e("Firestore", "Error adding comment", error));
    }
}
