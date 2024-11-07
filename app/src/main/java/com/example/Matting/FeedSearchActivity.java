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
