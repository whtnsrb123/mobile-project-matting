package com.example.Matting;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PostViewerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private int startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        startPosition = getIntent().getIntExtra("position", 0);

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 게시글 데이터 초기화
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList, false); // imageOnlyMode를 false로 설정
        recyclerView.setAdapter(postAdapter);

        // Firestore에서 게시글 데이터를 가져옵니다.
        fetchPostsFromFirestore();
    }

    private void fetchPostsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("posts")
                .whereEqualTo("username", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    queryDocumentSnapshots.forEach(document -> {
                        try {
                            // Firestore 문서에서 필드 가져오기
                            String documentId = document.getId();
                            String userId  = document.getString("username");
                            String postContent = document.getString("postContent"); // 필드 이름 확인 필요
                            String imageResource = document.getString("imageResource");
                            Timestamp timestamp = document.getTimestamp("timestamp"); // 문자열로 저장된 시간
                            int commentCount = document.contains("commentCount") ? document.getLong("commentCount").intValue() : 0;
                            int reactionCount = document.contains("reactionCount") ? document.getLong("reactionCount").intValue() : 0;
                            boolean reacted = document.contains("reacted") ? document.getBoolean("reacted") : false;

                            // UserID를 기반으로 닉네임 가져오기
                            fetchNicknameFromRealtimeDatabase(userId, nickname -> {
                                // 닉네임으로 Post 객체 생성
                                Post post = new Post(documentId, nickname, postContent, imageResource, timestamp, commentCount, reactionCount, reacted);
                                postList.add(post);

                                // RecyclerView 업데이트
                                postAdapter.notifyDataSetChanged();
                            });
                        } catch (Exception e) {
                            e.printStackTrace(); // 데이터 변환 중 오류 처리
                        }
                    });
                    postAdapter.notifyDataSetChanged(); // RecyclerView 갱신

                    recyclerView.scrollToPosition(startPosition);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "게시글 데이터를 가져오는 데 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Realtime Database에서 nicknames 가져오기
    private void fetchNicknameFromRealtimeDatabase(String userId, OnNicknameFetchedListener listener) {
        DatabaseReference realtimeDbRef = FirebaseDatabase.getInstance().getReference();
        realtimeDbRef.child("users").child(userId).child("nicknames")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nickname = snapshot.exists() ? snapshot.getValue(String.class) : "Unknown User";
                        listener.onNicknameFetched(nickname);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("RealtimeDB", "Error fetching nicknames for userId: " + userId, error.toException());
                        listener.onNicknameFetched("Unknown User"); // 기본값 설정
                    }
                });
    }
    // 닉네임 데이터 콜백을 처리하기 위한 인터페이스
    public interface OnNicknameFetchedListener {
        void onNicknameFetched(String nickname);
    }
}
