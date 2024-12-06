package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class UserProfileActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 전달받은 userId 가져오기
        userId = getIntent().getStringExtra("username");
        Log.d("UserProfileActivity", "Received username: " + userId);

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TextView 초기화
        userNameTextView = findViewById(R.id.user_name);

        // postList 초기화 및 RecyclerView 설정
        postList = new ArrayList<>();
        setupRecyclerView();

        // 사용자 데이터 불러오기
        fetchUserData();

        // Firestore에서 게시글 로드
        fetchPosts();

        // 팔로워 및 팔로잉 수 불러오기
        fetchFollowerCount();
        fetchFollowingCount();

        // 액션바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        getSupportActionBar().setTitle(""); // 액션바 제목 비우기 또는 원하는 값으로 설정

        // 추가된 팔로워 및 팔로잉 버튼 동작 설정
        setupFollow_FollowingButtons();

        // 팔로우 버튼 설정
        setupFollowButton();
    }

    // 뒤로가기 버튼 동작 처리
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 현재 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // 유저 데이터 가져오기
    private void fetchUserData() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nickname = snapshot.child("nicknames").getValue(String.class);
                    userNameTextView.setText(nickname != null ? nickname : "Unknown User");
                } else {
                    Toast.makeText(UserProfileActivity.this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "사용자 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("UserProfileActivity", "Error fetching user data", error.toException());
            }
        });
    }
    // 유저 게시글 불러오기
    private void fetchPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .whereEqualTo("username", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        postList.clear();
                        queryDocumentSnapshots.forEach(document -> {
                            try {
                                String documentId = document.getString("documentId");
                                String username = document.getString("username");
                                String postContent = document.getString("postContent");
                                String imageResource = document.getString("imageResource");

                                int commentCount = document.contains("commentCount") ? document.getLong("commentCount").intValue() : 0;
                                int reactionCount = document.contains("reactionCount") ? document.getLong("reactionCount").intValue() : 0;
                                boolean reacted = document.contains("reacted") ? document.getBoolean("reacted") : false;

                                postList.add(new Post(
                                        documentId != null ? documentId : "",
                                        username != null ? username : "",
                                        postContent != null ? postContent : "",
                                        imageResource != null ? imageResource : "",
                                        document.getTimestamp("timestamp"),
                                        commentCount,
                                        reactionCount,
                                        reacted
                                ));
                            } catch (Exception e) {
                                Log.e("UserProfileActivity", "Error parsing post data", e);
                            }
                        });
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("UserProfileActivity", "No posts found for userId: " + userId);
                    }
                })
                .addOnFailureListener(e -> Log.e("UserProfileActivity", "Error fetching posts", e));
    }
    // 뷰
    private void setupRecyclerView() {
        postRecyclerView = findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        postAdapter = new PostAdapter(this, postList, true);
        postRecyclerView.setAdapter(postAdapter);
    }

    // 팔로워 및 팔로잉 버튼 클릭 이벤트 추가
    private void setupFollow_FollowingButtons() {
        LinearLayout followersLayout = findViewById(R.id.followersLayout);
        LinearLayout followingLayout = findViewById(R.id.followingLayout);

        // 팔로워 버튼 클릭
        followersLayout.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, FollowersActivity.class);
            intent.putExtra("userId", userId); // 현재 프로필의 userId 전달
            startActivity(intent);
        });

        // 팔로잉 버튼 클릭
        followingLayout.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, FollowingActivity.class);
            intent.putExtra("userId", userId); // 현재 프로필의 userId 전달
            startActivity(intent);
        });
    }

    private void setupFollowButton() {
        Button followButton = findViewById(R.id.followButton);

        // 현재 로그인된 사용자 ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // 팔로우 상태 확인
        databaseRef.child("users").child(userId).child("followers").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            followButton.setText("팔로잉");
                        } else {
                            followButton.setText("팔로우");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserProfileActivity", "Error checking follow status", error.toException());
                    }
                });

        // 버튼 클릭 이벤트
        followButton.setOnClickListener(v -> {
            databaseRef.child("users").child(userId).child("followers").child(currentUserId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // 팔로우 취소
                                databaseRef.child("users").child(userId).child("followers").child(currentUserId).removeValue()
                                        .addOnSuccessListener(unused -> {
                                            followButton.setText("팔로우");
                                            Toast.makeText(UserProfileActivity.this, "팔로우 취소", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> Log.e("UserProfileActivity", "Error removing follower", e));

                                // 내 following에서도 상대방 userId 제거
                                databaseRef.child("users").child(currentUserId).child("following").child(userId).removeValue()
                                        .addOnFailureListener(e -> Log.e("UserProfileActivity", "Error removing following", e));
                            } else {
                                // 팔로우 추가
                                databaseRef.child("users").child(userId).child("followers").child(currentUserId).setValue(true)
                                        .addOnSuccessListener(unused -> {
                                            followButton.setText("팔로잉");
                                            Toast.makeText(UserProfileActivity.this, "팔로우 성공", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> Log.e("UserProfileActivity", "Error adding follower", e));

                                // 내 following에도 상대방 userId 추가
                                databaseRef.child("users").child(currentUserId).child("following").child(userId).setValue(true)
                                        .addOnFailureListener(e -> Log.e("UserProfileActivity", "Error adding following", e));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("UserProfileActivity", "Error toggling follow status", error.toException());
                        }
                    });
        });
    }
    // 팔로잉 수 불러오기
    private void fetchFollowingCount() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("following")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount(); // 팔로잉 수 계산
                        TextView followingCountTextView = findViewById(R.id.followingCount);
                        followingCountTextView.setText(String.valueOf(count)); // UI에 표시
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserProfileActivity", "Error fetching following count", error.toException());
                    }
                });
    }
    // 팔로워 수 불러오기
    private void fetchFollowerCount() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("followers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount(); // 팔로워 수 계산
                        TextView followerCountTextView = findViewById(R.id.followerCount);
                        followerCountTextView.setText(String.valueOf(count)); // UI에 표시
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserProfileActivity", "Error fetching followers count", error.toException());
                    }
                });
    }
}
