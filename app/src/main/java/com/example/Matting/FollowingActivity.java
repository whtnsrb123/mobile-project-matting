package com.example.Matting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends AppCompatActivity {

    private RecyclerView followingRecyclerView;
    private FollowersAdapter followingAdapter;
    private List<Follower> followingList; // 팔로잉 데이터
    private List<Follower> filteredList; // 검색된 결과 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);

        // RecyclerView 및 데이터 초기화
        followingList = new ArrayList<>();
        filteredList = new ArrayList<>();
        populateFollowingData();

        followingRecyclerView = findViewById(R.id.followingRecyclerView);
        followingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정
        followingAdapter = new FollowersAdapter(filteredList);
        followingAdapter.setOnItemClickListener(follower -> {
            // 클릭 효과: 사용자 이름을 Toast 메시지로 표시
            Toast.makeText(FollowingActivity.this, follower.getUsername() + " 클릭됨", Toast.LENGTH_SHORT).show();
        });
        followingRecyclerView.setAdapter(followingAdapter);

        // 검색창 처리
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFollowing(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 초기 필터링 (전체 리스트 표시)
        filterFollowing("");
    }

    // 팔로잉 데이터 초기화
    private void populateFollowingData() {
        followingList.add(new Follower("Following1", "팔로잉 설명 1", R.drawable.user));
        followingList.add(new Follower("Following2", "팔로잉 설명 2", R.drawable.user));
        followingList.add(new Follower("Following3", "팔로잉 설명 3", R.drawable.user));
        // 더 많은 데이터를 추가할 수 있습니다.
    }

    // 검색 필터링
    private void filterFollowing(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(followingList);
        } else {
            for (Follower follower : followingList) {
                if (follower.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(follower);
                }
            }
        }
        followingAdapter.notifyDataSetChanged();
    }
}
