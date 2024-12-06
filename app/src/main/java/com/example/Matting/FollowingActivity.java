package com.example.Matting;

import android.content.Intent;
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

public class FollowingActivity extends AppCompatActivity {

    private RecyclerView followingRecyclerView;
    private FollowersAdapter followingAdapter;
    private List<Follower> followingList; // 팔로잉 데이터
    private List<Follower> filteredList; // 검색된 결과 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);

        followingList = new ArrayList<>();
        filteredList = new ArrayList<>();

        followingRecyclerView = findViewById(R.id.followingRecyclerView);
        followingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정
        followingAdapter = new FollowersAdapter(filteredList);
        followingAdapter.setOnItemClickListener(follower -> {
            // 클릭된 팔로잉 사용자의 프로필로 이동
            Intent intent = new Intent(FollowingActivity.this, UserProfileActivity.class);
            intent.putExtra("username", follower.getUserId()); // 클릭한 사용자의 userId 전달
            startActivity(intent);
        });
        followingRecyclerView.setAdapter(followingAdapter);

        // 전달된 userId를 기반으로 데이터 가져오기
        String userId = getIntent().getStringExtra("userId");
        if (userId != null && !userId.isEmpty()) {
            fetchFollowingData(userId);
        } else {
            Toast.makeText(this, "사용자 ID를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }

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
    }

    private void fetchFollowingData(String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("following")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        filteredList.clear(); // 초기화
                        for (DataSnapshot followingSnapshot : snapshot.getChildren()) {
                            String followingId = followingSnapshot.getKey(); // 팔로잉 ID 가져오기

                            if (followingId != null) {
                                databaseRef.child("users").child(followingId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                                String nickname = userSnapshot.child("nicknames").getValue(String.class);
                                                String profileImageUrl = userSnapshot.child("profileImage").getValue(String.class);

                                                // 닉네임과 프로필 이미지 추가
                                                followingList.add(new Follower(
                                                        followingId, // userId 전달
                                                        nickname != null ? nickname : "Unknown User",
                                                        "뭘 넣을 수 있긴 한데 필요 없으면 없애도 됩니다",
                                                        profileImageUrl != null ? profileImageUrl : "")); // 이미지 없으면 빈 문자열
                                                filterFollowing(""); // RecyclerView 업데이트
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(FollowingActivity.this, "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FollowingActivity.this, "팔로잉 데이터를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

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
