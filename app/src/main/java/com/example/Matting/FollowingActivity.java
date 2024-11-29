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

        followingAdapter = new FollowersAdapter(filteredList);
        followingAdapter.setOnItemClickListener(follower ->
                Toast.makeText(FollowingActivity.this, follower.getUsername() + " 클릭됨", Toast.LENGTH_SHORT).show());
        followingRecyclerView.setAdapter(followingAdapter);

        fetchFollowingData();

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

        filterFollowing("");
    }

    private void fetchFollowingData() {
        String userId = getIntent().getStringExtra("userId");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "유효하지 않은 사용자입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("following")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        for (DataSnapshot followingSnapshot : snapshot.getChildren()) {
                            String followingId = followingSnapshot.getKey();

                            if (followingId != null) {
                                // 닉네임과 프로필 이미지 가져오기
                                databaseRef.child("users").child(followingId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        String nickname = userSnapshot.child("nicknames").getValue(String.class);
                                        String profileImageUrl = userSnapshot.child("profileImage").getValue(String.class);

                                        if (nickname != null) {
                                            followingList.add(new Follower(nickname, "설명 없음", profileImageUrl));
                                            filterFollowing(""); // RecyclerView 업데이트
                                        }
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
