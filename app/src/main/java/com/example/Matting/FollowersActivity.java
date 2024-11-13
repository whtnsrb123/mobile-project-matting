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

public class FollowersActivity extends AppCompatActivity {

    private RecyclerView followersRecyclerView;
    private FollowersAdapter followersAdapter;
    private List<Follower> followersList; // 팔로워 데이터
    private List<Follower> filteredList; // 검색된 결과 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_list);

        // RecyclerView 및 데이터 초기화
        followersList = new ArrayList<>();
        filteredList = new ArrayList<>();
        populateFollowersData();

        followersRecyclerView = findViewById(R.id.followersRecyclerView);
        followersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정
        followersAdapter = new FollowersAdapter(filteredList);
        followersAdapter.setOnItemClickListener(follower -> {
            // 클릭 효과: 사용자 이름을 Toast 메시지로 표시
            Toast.makeText(FollowersActivity.this, follower.getUsername() + " 클릭됨", Toast.LENGTH_SHORT).show();
        });
        followersRecyclerView.setAdapter(followersAdapter);

        // 검색창 처리
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFollowers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 초기 필터링 (전체 리스트 표시)
        filterFollowers("");
    }

    // 팔로워 데이터 초기화
    private void populateFollowersData() {
        followersList.add(new Follower("Follower1", "팔로워 설명 1", R.drawable.user));
        followersList.add(new Follower("Follower2", "팔로워 설명 2", R.drawable.user));
        followersList.add(new Follower("Follower3", "팔로워 설명 3", R.drawable.user));
        // 더 많은 데이터를 추가할 수 있습니다.
    }

    // 검색 필터링
    private void filterFollowers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(followersList);
        } else {
            for (Follower follower : followersList) {
                if (follower.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(follower);
                }
            }
        }
        followersAdapter.notifyDataSetChanged();
    }
}
