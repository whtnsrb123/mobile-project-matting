package com.example.Matting;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // 댓글 데이터 초기화
        commentList = new ArrayList<>();

        // 기본 댓글 추가
        populateComments();

        // RecyclerView 설정
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // 댓글 입력 및 전송
        commentInput = findViewById(R.id.commentInput);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String newComment = commentInput.getText().toString().trim();
            if (!newComment.isEmpty()) {
                // 현재 시간을 가져와 댓글 추가
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                commentList.add(new Comment("current_user", newComment, currentTime));
                commentAdapter.notifyItemInserted(commentList.size() - 1);
                commentRecyclerView.scrollToPosition(commentList.size() - 1);
                commentInput.setText(""); // 입력 필드 초기화
            }
        });
    }

    private void populateComments() {
        // 기본 댓글 추가
        commentList.add(new Comment("bo_illak", "한글 안 적혀 있었으면 한국인줄 몰랐을 거예요! 😮❤️", "4주 전"));
        commentList.add(new Comment("woody_oo", "@bo_illak 가성비 너무 괜찮죠!!!", "4주 전"));
        commentList.add(new Comment("soi___ya", "와 가성비 최고네요", "4주 전"));
        commentList.add(new Comment("woody_oo", "@soi___ya 완전요 😍", "4주 전"));
    }
}
