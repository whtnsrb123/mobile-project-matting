package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ImageView profileImage = findViewById(R.id.profileImage);
        Glide.with(this)
                .load(R.drawable.profile_image)
                .circleCrop()
                .into(profileImage);

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_mypage);

        // 네비게이션 아이템 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    // 메인 액티비티로 이동
                    Intent homeIntent = new Intent(MyProfileActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    // 피드 액티비티로 이동
                    Intent feedIntent = new Intent(MyProfileActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent chatIntent = new Intent(MyProfileActivity.this, Chat_ChatroomActivity.class);
                    startActivity(chatIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    // 커뮤니티 액티비티로 이동
                    Intent communityIntent = new Intent(MyProfileActivity.this, CommunityActivity.class);
                    startActivity(communityIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    // 현재 액티비티 그대로 유지
                    return true;
                }
                return false;
            }
        });

        // RecyclerView 설정
        postRecyclerView = findViewById(R.id.postRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        postRecyclerView.setLayoutManager(gridLayoutManager);

        // **postList를 PostData.getPostList()로 초기화**
        postList = PostData.getPostList();

        // 어댑터 설정
        postAdapter = new PostAdapter(this, postList, true); // 이미지 전용 모드 활성화
        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MyProfileActivity.this, PostViewerActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        postRecyclerView.setAdapter(postAdapter);
    }
}
