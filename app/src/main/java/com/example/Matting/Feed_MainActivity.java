package com.example.Matting;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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
    private com.example.Matting.FeedAdapter feedAdapter;
    private List<com.example.Matting.FeedItem> feedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_main);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        feedItems = new ArrayList<>();

        // 피드  데이터 추가 맨 뒤 count는 일단 상수 값 넣음
        feedItems.add(new com.example.Matting.FeedItem("user1", "첫 번째 게시물입니다.", R.drawable.feed_food_image1, 5, 10));
        feedItems.add(new com.example.Matting.FeedItem("user2", "두 번째 게시물입니다.", R.drawable.feed_food_image1, 3, 7));
        feedItems.add(new com.example.Matting.FeedItem("user3", "세 번째 게시물입니다.", R.drawable.feed_food_image1, 8, 12));
        feedItems.add(new com.example.Matting.FeedItem("user4", "네 번째 게시물입니다.", R.drawable.feed_food_image1, 1, 2));


        feedAdapter = new com.example.Matting.FeedAdapter(feedItems);
        recyclerView.setAdapter(feedAdapter);


        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_feed); // 세 번째 아이템 선택

        // 네비게이션 아이템 선택 리스너 설정
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
                }
                else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent feedIntent = new Intent(Feed_MainActivity.this, Chat_ChatroomActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(Feed_MainActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }



}
