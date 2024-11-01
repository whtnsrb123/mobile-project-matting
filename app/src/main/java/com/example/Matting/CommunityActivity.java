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

public class CommunityActivity extends AppCompatActivity {

    private RecyclerView postsRecyclerView;
    private CommunityAdapter communityAdapter;
    private List<Community> communityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community); // XML 파일도 activity_community로 수정해야 함

        // RecyclerView 설정
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터 초기화 및 어댑터 연결
        communityList = new ArrayList<>();
        communityList.add(new Community("고기먹으러 같이가요", "정릉역 1번출구...", "2024.10.31 · 58분 전 · 3/4"));
        communityList.add(new Community("중식당 같이가요", "국민대 근처...", "2024.10.31 · 18시간 전 · 2/4"));
        // 필요시 더 많은 데이터를 추가

        communityAdapter = new CommunityAdapter(this, communityList);
        postsRecyclerView.setAdapter(communityAdapter);

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_community); // 세 번째 아이템 선택

        // 네비게이션 아이템 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // 메인 액티비티로 이동
                    Intent homeIntent = new Intent(CommunityActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    // 피드 액티비티로 이동
                    Intent feedIntent = new Intent(CommunityActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {

                    return true;
                }
                else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent feedIntent = new Intent(CommunityActivity.this, Chat_ChatroomActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(CommunityActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
}
