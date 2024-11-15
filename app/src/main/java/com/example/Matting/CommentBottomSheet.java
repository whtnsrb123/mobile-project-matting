package com.example.Matting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Matting.CommentAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
                        commentList.add(doc.toObject(Comment.class)); // Comment 객체로 변환
                    }
                    adapter.notifyDataSetChanged();
                });

        // 댓글 입력
        EditText editTextComment = view.findViewById(R.id.editTextComment);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmitComment);
        buttonSubmit.setOnClickListener(v -> {
            String content = editTextComment.getText().toString();
            if (!content.isEmpty()) {
                Map<String, Object> comment = new HashMap<>();
                comment.put("username", "내가 작성한 댓글"); // 실제 사용자 이름
                comment.put("content", content);
                comment.put("timestamp", new Date());

                db.collection("posts").document(postId).collection("comments")
                        .add(comment)
                        .addOnSuccessListener(docRef -> editTextComment.setText(""))
                        .addOnFailureListener(error -> Log.e("Firestore", "Error adding comment", error));
            }
        });

        return view;
    }
}
