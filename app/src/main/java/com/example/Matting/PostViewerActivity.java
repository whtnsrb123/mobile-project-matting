package com.example.Matting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostViewerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);

        // 게시글 리스트 가져오기
        postList = PostData.getPostList();

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정 (전체 정보 모드로 설정하기 위해 imageOnlyMode를 false로 전달)
        postAdapter = new PostAdapter(this, postList, false);
        recyclerView.setAdapter(postAdapter);

        // 클릭한 게시글 위치로 이동
        int startPosition = getIntent().getIntExtra("position", 0);
        recyclerView.scrollToPosition(startPosition);
    }
}
