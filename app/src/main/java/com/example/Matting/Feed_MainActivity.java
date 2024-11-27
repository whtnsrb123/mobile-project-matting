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
        feedAdapter = new FeedAdapter(feedItems);
        recyclerView.setAdapter(feedAdapter);

        db = FirebaseFirestore.getInstance();

        // 초기 데이터 로드
        loadFeedData();

        // SwipeRefreshLayout 새로고침 동작 설정
        swipeRefreshLayout.setOnRefreshListener(() -> loadFeedData());

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
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    // 피드 액티비티로 이동
                    return true;
                } else if (itemId == R.id.nav_community) {
                    Intent feedIntent = new Intent(Feed_MainActivity.this, CommunityActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent feedIntent = new Intent(Feed_MainActivity.this, Chat_ChatlistActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(Feed_MainActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        // imageButton7 찾기
        searchButton = findViewById(R.id.imageButton7);

        // imageButton7 클릭 시 FeedSearchActivity로 이동
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FeedSearchActivity로 이동
                Intent intent = new Intent(Feed_MainActivity.this, FeedSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    // Firestore에서 데이터 가져오기
    private void loadFeedData() {
        swipeRefreshLayout.setRefreshing(true);

        db.collection("posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // timestamp를 내림차순으로 정렬
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        feedItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String userId = document.getString("username"); // Firestore에서 username 가져오기
                            String postContent = document.getString("postContent");
                            String imageUrl = document.getString("imageResource");
                            int reactionCount = document.getLong("reactionCount").intValue();
                            int commentCount = document.getLong("commentCount").intValue();
                            Date timestamp = document.getTimestamp("timestamp").toDate();

                            // Realtime Database에서 nicknames 가져오기
                            fetchNicknameFromRealtimeDatabase(userId, nicknames -> {
                                // FeedItem 생성 및 추가
                                feedItems.add(new FeedItem(documentId, nicknames, postContent, imageUrl, reactionCount, commentCount, timestamp));
                                feedAdapter.notifyDataSetChanged();
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
