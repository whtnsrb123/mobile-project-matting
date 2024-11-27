package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityActivity extends AppCompatActivity {

    private static final String TAG = "CommunityActivity";
    private FirebaseFirestore db;
    private CollectionReference communityRef;

    private RecyclerView postsRecyclerView;
    private CommunityAdapter communityAdapter;
    private List<Community> communityList;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);


        //로그인 확인
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // 로그인 페이지로 이동하고 결과를 기다림
            Intent loginIntent = new Intent(CommunityActivity.this, User_LoginActivity.class);
            startActivityForResult(loginIntent, 1001); // 1001은 요청 코드
        }

        db = FirebaseFirestore.getInstance();
        communityRef = db.collection("community"); // Firebase Firestore의 "community" 컬렉션 참조

        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        communityList = new ArrayList<>();
        communityAdapter = new CommunityAdapter(this, communityList);
        postsRecyclerView.setAdapter(communityAdapter);

        // Firestore에서 데이터를 불러오는 메소드 호출
        loadPostsFromFirestore();

        // Intent에서 새로운 게시물 데이터 가져오기
        Intent intent = getIntent();
        String newTitle = intent.getStringExtra("title");
        String newContent = intent.getStringExtra("content");
        String newLocation = intent.getStringExtra("location");
        String newRestaurant = intent.getStringExtra("restaurant");
        String newDate = intent.getStringExtra("date");
        String newTime = intent.getStringExtra("time");
        String newMapX = intent.getStringExtra("mapx");
        String newMapY = intent.getStringExtra("mapy");

        if (newTitle != null && newContent != null) {
            addPostToFirestore(newTitle, newContent, newLocation, newRestaurant, newDate, newTime, newMapX, newMapY);
        }

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_community); // 세 번째 아이템 선택

        // 네비게이션 아이템 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // 메인 액티비티로 이동
                    Intent homeIntent = new Intent(CommunityActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    // 피드 액티비티로 이동
                    Intent feedIntent = new Intent(CommunityActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {

                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent feedIntent = new Intent(CommunityActivity.this, Chat_ChatlistActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(CommunityActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void addPostToFirestore(String title, String content, String location, String restaurant, String date, String time, String mapx, String mapy) {
        Map<String, Object> post = new HashMap<>();
        post.put("title", title.trim());
        post.put("content", content);
        post.put("location", location);
        post.put("restaurant", restaurant);
        post.put("date", date);
        post.put("time", time);
        post.put("mapx", mapx);
        post.put("mapy", mapy);


        Log.d("mapxy", "loadPostsFromFirestore" + mapx + mapy);

        communityRef.add(post)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    loadPostsFromFirestore();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    private void loadPostsFromFirestore() {
        communityRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        communityList.clear(); // 기존 리스트 비우기
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String content = document.getString("content");
                            String location = document.getString("location");
                            String restaurant = document.getString("restaurant");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            String mapx = document.getString("mapx");
                            String mapy = document.getString("mapy");
                            Log.d("mapxy", "loadPostsFromFirestore");
                            communityList.add(new Community(title, content, location, restaurant, date, time, mapx, mapy)); // Firestore에서 불러온 데이터를 리스트에 추가
                        }
                        communityAdapter.notifyDataSetChanged(); // RecyclerView 업데이트
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(this, "게시물을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
