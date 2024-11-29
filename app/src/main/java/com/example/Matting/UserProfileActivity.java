package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

        // 하단 네비게이션 바 초기화
        setupBottomNavigationBar();

        // 액션바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        getSupportActionBar().setTitle(""); // 액션바 제목 비우기 또는 원하는 값으로 설정
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

    private void setupRecyclerView() {
        postRecyclerView = findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        postAdapter = new PostAdapter(this, postList, true);
        postRecyclerView.setAdapter(postAdapter);
    }

    private void setupBottomNavigationBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_feed); // 현재 위치는 피드로 설정

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // 메인 액티비티로 이동
                    Intent homeIntent = new Intent(UserProfileActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    Intent feedIntent = new Intent(UserProfileActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    Intent feedIntent = new Intent(UserProfileActivity.this, CommunityActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent feedIntent = new Intent(UserProfileActivity.this, Chat_ChatlistActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(UserProfileActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
}