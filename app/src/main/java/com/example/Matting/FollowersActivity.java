package com.example.Matting;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFollowers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 더미 팔로워 데이터 생성
        List<Follower> followers = new ArrayList<>();
        followers.add(new Follower("User1", "https://via.placeholder.com/48"));
        followers.add(new Follower("User2", "https://via.placeholder.com/48"));
        followers.add(new Follower("User3", "https://via.placeholder.com/48"));
        followers.add(new Follower("User4", "https://via.placeholder.com/48"));
        followers.add(new Follower("User5", "https://via.placeholder.com/48"));

        // 어댑터 설정
        FollowersAdapter adapter = new FollowersAdapter(followers);
        recyclerView.setAdapter(adapter);
    }
}
