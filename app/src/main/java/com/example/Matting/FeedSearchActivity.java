package com.example.Matting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class FeedSearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private FeedSearchAdapter feedSearchAdapter;
    private List<String> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_search);

        // 초기화
        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 검색 결과 리스트 초기화
        searchResults = new ArrayList<>();
        feedSearchAdapter = new FeedSearchAdapter(searchResults);
        recyclerView.setAdapter(feedSearchAdapter);

        // 검색 버튼 클릭 리스너 설정
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 입력된 검색어 가져오기
                String query = searchEditText.getText().toString().trim();

                // 검색 수행
                performSearch(query);
            }
        });
        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_feed); // 세 번째 아이템 선택

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // 메인 액티비티로 이동
                    Intent homeIntent = new Intent(FeedSearchActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    // 피드 액티비티로 이동
                    Intent feedIntent = new Intent(FeedSearchActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    Intent feedIntent = new Intent(FeedSearchActivity.this, CommunityActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent feedIntent = new Intent(FeedSearchActivity.this, Chat_ChatroomActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(FeedSearchActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    // 검색 수행 (검색어가 없으면 기존 검색 기록 삭제)
    private void performSearch(String query) {
        if (query.isEmpty()) {
            // 검색어가 비어있으면 기존 검색 기록을 삭제
            searchResults.clear();
            Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            // 검색어가 있으면 더미 데이터를 검색 결과로 추가
            searchResults.clear();  // 기존 결과를 지우고 새로운 결과 추가
            for (int i = 0; i < 10; i++) {
                searchResults.add(query + " 결과 " + (i + 1));
            }
        }

        // 어댑터에 데이터가 변경되었음을 알림
        feedSearchAdapter.notifyDataSetChanged();
    }
}
