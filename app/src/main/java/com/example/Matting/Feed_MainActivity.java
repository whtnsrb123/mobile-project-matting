package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton; // ImageButton 추가

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class Feed_MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private List<FeedItem> feedItems;
    private ImageButton searchButton;  // imageButton7을 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_main);

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        feedItems = new ArrayList<>();

        // 피드 데이터 추가
        feedItems.add(new FeedItem("user1", "첫 번째 게시물입니다.", R.drawable.feed_food_image1, 5, 10));
        feedItems.add(new FeedItem("user2", "두 번째 게시물입니다.", R.drawable.feed_food_image1, 3, 7));
        feedItems.add(new FeedItem("user3", "세 번째 게시물입니다.", R.drawable.feed_food_image1, 8, 12));
        feedItems.add(new FeedItem("user4", "네 번째 게시물입니다.", R.drawable.feed_food_image1, 1, 2));

        feedAdapter = new FeedAdapter(feedItems);
        recyclerView.setAdapter(feedAdapter);

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
                    Intent feedIntent = new Intent(Feed_MainActivity.this, Chat_ChatroomActivity.class);
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
}
