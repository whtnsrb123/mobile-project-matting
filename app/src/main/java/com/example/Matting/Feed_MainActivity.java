package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Feed_MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private List<FeedItem> feedItems;
    private ImageButton searchButton; // imageButton7을 위한 변수
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout; // SwipeRefreshLayout 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // SwipeRefreshLayout 초기화

        feedItems = new ArrayList<>();

        // FeedAdapter 초기화 및 클릭 리스너 설정
        feedAdapter = new FeedAdapter(feedItems, username -> {
            // 유저네임 클릭 시 UserProfileActivity로 이동
            Intent intent = new Intent(Feed_MainActivity.this, UserProfileActivity.class);
            if (username != null) {
                intent.putExtra("username", username);
            } else {
                intent.putExtra("username", "Unknown User"); // 기본값 전달
            }
            startActivity(intent);
        });
        recyclerView.setAdapter(feedAdapter);

        db = FirebaseFirestore.getInstance();

        // 초기 데이터 로드
        loadFeedData();

        // SwipeRefreshLayout 새로고침 동작 설정
        swipeRefreshLayout.setOnRefreshListener(this::loadFeedData);

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_feed); // 세 번째 아이템 선택

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // 메인 액티비티로 이동
                    Intent homeIntent = new Intent(Feed_MainActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_community) {
                    Intent feedIntent = new Intent(Feed_MainActivity.this, CommunityActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent feedIntent = new Intent(Feed_MainActivity.this, Chat_ChatlistActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(Feed_MainActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });

        // imageButton7 찾기
        searchButton = findViewById(R.id.imageButton7);

        // imageButton7 클릭 시 FeedSearchActivity로 이동
        searchButton.setOnClickListener(v -> {
            // FeedSearchActivity로 이동
            Intent intent = new Intent(Feed_MainActivity.this, FeedSearchActivity.class);
            startActivity(intent);
        });
    }

    private void loadFeedData() {
        swipeRefreshLayout.setRefreshing(true);

        // 현재 로그인한 사용자의 userId 가져오기
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // timestamp를 내림차순으로 정렬
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        feedItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String username = document.getString("username"); // Firestore에서 username 가져오기

                            // 자신이 작성한 게시물 제외
                            if (username.equals(currentUserId)) {
                                continue;
                            }

                            String postContent = document.getString("postContent");
                            String imageUrl = document.getString("imageResource");
                            int reactionCount = document.getLong("reactionCount").intValue();
                            int commentCount = document.getLong("commentCount").intValue();
                            Date timestamp = document.getTimestamp("timestamp").toDate();

                            // Realtime Database에서 username의 nicknames과 profileImage 가져오기
                            DatabaseReference userRef = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(username); // username으로 users/{username}/ 접근

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String nickname = snapshot.child("nicknames").exists()
                                            ? snapshot.child("nicknames").getValue(String.class)
                                            : "Unknown User";

                                    String profileImage = snapshot.child("profileImage").exists()
                                            ? snapshot.child("profileImage").getValue(String.class)
                                            : null;

                                    // FeedItem 생성 및 추가
                                    feedItems.add(new FeedItem(documentId, username, nickname, postContent, imageUrl, reactionCount, commentCount, timestamp, profileImage));
                                    feedAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Feed_MainActivity", "Error fetching user data", error.toException());
                                }
                            });
                        }
                    } else {
                        Log.w("Feed_MainActivity", "Error getting documents.", task.getException());
                    }
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Log.e("Feed_MainActivity", "Error loading data", e);
                    swipeRefreshLayout.setRefreshing(false);
                });
    }
}
