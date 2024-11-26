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
import com.google.firebase.auth.FirebaseUser;
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

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_main);


        //로그인 확인
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // 로그인 페이지로 이동하고 결과를 기다림
            Intent loginIntent = new Intent(Feed_MainActivity.this, User_LoginActivity.class);
            startActivityForResult(loginIntent, 1001); // 1001은 요청 코드
        }


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
        // 새로고침 로딩 상태 시작
        swipeRefreshLayout.setRefreshing(true);

        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        feedItems.clear(); // 기존 데이터 초기화
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String username = document.getString("username");
                            String postContent = document.getString("postContent");
                            String imageUrl = document.getString("imageResource");
                            int reactionCount = document.getLong("reactionCount").intValue();
                            int commentCount = document.getLong("commentCount").intValue();
                            Date timestamp = document.getTimestamp("timestamp").toDate();

                            feedItems.add(new FeedItem(documentId, username, postContent, imageUrl, reactionCount, commentCount, timestamp));
                        }
                        feedAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("Feed_MainActivity", "Error getting documents.", task.getException());
                    }
                    // 새로고침 로딩 상태 종료
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Log.e("Feed_MainActivity", "Error loading data", e);
                    // 실패 시에도 새로고침 로딩 상태 종료
                    swipeRefreshLayout.setRefreshing(false);
                });
    }
}
