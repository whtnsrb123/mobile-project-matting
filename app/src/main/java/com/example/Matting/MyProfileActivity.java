package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        setContentView(R.layout.activity_my_profile); // activity_main.xml 파일과 연결

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_mypage); // 세 번째 아이템 선택

        // 네비게이션 아이템 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // 메인 액티비티로 이동
                    Intent homeIntent = new Intent(MyProfileActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    // 피드 액티비티로 이동
                    Intent feedIntent = new Intent(MyProfileActivity.this, MainActivity.class);
                    startActivity(feedIntent);
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent feedIntent = new Intent(MyProfileActivity.this, Chat_ChatroomActivity.class);
                    startActivity(feedIntent);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    // 커뮤니티 액티비티로 이동
                    Intent communityIntent = new Intent(MyProfileActivity.this, CommunityActivity.class);
                    startActivity(communityIntent);
                    return true;
                }else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(MyProfileActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    return true;
                }
                return false;
            }
        });

        // RecyclerView 설정
        postRecyclerView = findViewById(R.id.postRecyclerView);
        // 한 줄에 3개의 아이템을 표시하는 GridLayoutManager 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        postRecyclerView.setLayoutManager(gridLayoutManager);

        // 게시글 데이터 설정
        postList = new ArrayList<>();
        postList.add(new Post("게시글 설명 1", R.drawable.ic_launcher_background));
        postList.add(new Post("게시글 설명 2", R.drawable.ic_launcher_background));
        postList.add(new Post("게시글 설명 3", R.drawable.ic_launcher_background));
        postList.add(new Post("게시글 설명 4", R.drawable.ic_launcher_background));

        // 어댑터 설정
        postAdapter = new PostAdapter(this, postList);
        postRecyclerView.setAdapter(postAdapter);
    }
}
