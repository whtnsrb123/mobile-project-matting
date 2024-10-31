package com.example.a2024pj;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_community); // 세 번째 아이템 선택

        // BottomNavigationView 아이템 선택 리스너 설정
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.nav_home:
//                    // HomeActivity로 이동하는 코드 작성
//                    return true;
//                case R.id.nav_feed:
//                    // FeedActivity로 이동하는 코드 작성
//                    return true;
//                case R.id.nav_community:
//                    // 이미 CommunityActivity에 있으므로 동작 없음
//                    return true;
//                case R.id.nav_mypage:
//                    // MyPageActivity로 이동하는 코드 작성
//                    return true;
//            }
//            return false;
//        });
    }
}
