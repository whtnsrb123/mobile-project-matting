package com.example.Matting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // 초기화
        followersList = new ArrayList<>();
        filteredList = new ArrayList<>();

        followersRecyclerView = findViewById(R.id.followersRecyclerView);
        followersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        followersAdapter = new FollowersAdapter(filteredList);
        followersAdapter.setOnItemClickListener(follower ->
                Toast.makeText(FollowersActivity.this, follower.getUsername() + " 클릭됨", Toast.LENGTH_SHORT).show());
        followersRecyclerView.setAdapter(followersAdapter);

        // Firebase에서 데이터 로드
        fetchFollowersData();

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
    }

    private void fetchFollowersData() {
        String userId = getIntent().getStringExtra("userId"); // 전달된 사용자 ID 가져오기
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "유효하지 않은 사용자입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("followers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followersList.clear();
                        filteredList.clear(); // 초기화
                        for (DataSnapshot followerSnapshot : snapshot.getChildren()) {
                            String followerId = followerSnapshot.getKey();

                            if (followerId != null) {
                                databaseRef.child("users").child(followerId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                                String nickname = userSnapshot.child("nicknames").getValue(String.class);

                                                String profileImageUrl = userSnapshot.child("profileImage").getValue(String.class);

                                                if (nickname != null) {
                                                    followersList.add(new Follower(nickname, "뭘 넣을 수 있긴 한데 필요 없으면 없애도 됩니다", profileImageUrl != null ? profileImageUrl : ""));
                                                    filterFollowers(""); // RecyclerView 업데이트
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(FollowersActivity.this, "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FollowersActivity.this, "팔로워 데이터를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

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
        followersAdapter.notifyDataSetChanged(); // 어댑터에 변경 사항 알림
    }
}
