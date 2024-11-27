package com.example.Matting;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PostData {

    public static void fetchPosts(FirebaseFirestore db, List<Post> postList, Runnable onComplete) {
        if (postList == null) {
            throw new IllegalArgumentException("postList cannot be null");
        }

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear(); // postList가 null이 아님을 보장
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String documentId = document.getString("documentId");
                            String username = document.getString("username");
                            String postContent = document.getString("postContent");
                            String imageResource = document.getString("imageResource");
                            Timestamp timestampObject = document.getTimestamp("timestamp");
                            String timestamp = timestampObject != null
                                    ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampObject.toDate())
                                    : "Unknown";
                            int commentCount = document.contains("commentCount") ? document.getLong("commentCount").intValue() : 0;
                            int reactionCount = document.contains("reactionCount") ? document.getLong("reactionCount").intValue() : 0;
                            boolean reacted = document.contains("reacted") ? document.getBoolean("reacted") : false;

                            postList.add(new Post(documentId,username, postContent, imageResource, timestampObject, commentCount, reactionCount, reacted));
                        } catch (Exception e) {
                            e.printStackTrace(); // 데이터 변환 오류 처리
                        }
                    }
                    onComplete.run(); // 완료 콜백 실행
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace(); // Firestore 오류 처리
                });
    }
}

