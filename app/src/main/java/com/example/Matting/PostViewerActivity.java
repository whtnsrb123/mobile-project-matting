package com.example.Matting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class PostViewerActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);

        postList = PostData.getPostList();  // 게시글 데이터를 가져오는 메서드
        int startPosition = getIntent().getIntExtra("position", 0);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL); // 세로 방향으로 설정
        PostPagerAdapter adapter = new PostPagerAdapter(this, postList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition, false);
    }
}
