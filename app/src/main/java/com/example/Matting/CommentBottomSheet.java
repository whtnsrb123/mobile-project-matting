package com.example.Matting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class CommentBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private ImageButton sendButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_comment, container, false);

        // 댓글 데이터 초기화
        commentList = new ArrayList<>();
        populateComments();

        // RecyclerView 설정
        commentRecyclerView = view.findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // 댓글 입력 및 전송
        commentInput = view.findViewById(R.id.commentInput);
        sendButton = view.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String newComment = commentInput.getText().toString().trim();
            if (!newComment.isEmpty()) {
                commentList.add(new Comment("current_user", newComment, "방금 전"));
                commentAdapter.notifyItemInserted(commentList.size() - 1);
                commentRecyclerView.scrollToPosition(commentList.size() - 1);
                commentInput.setText(""); // 입력 필드 초기화
            }
        });

        return view;
    }

    private void populateComments() {
        commentList.add(new Comment("bo_illak", "한글 안 적혀 있었으면 한국인줄 몰랐을 거예요! 😮❤️", "4주 전"));
        commentList.add(new Comment("woody_oo", "@bo_illak 가성비 너무 괜찮죠!!!", "4주 전"));
        commentList.add(new Comment("soi___ya", "와 가성비 최고네요", "4주 전"));
        commentList.add(new Comment("woody_oo", "@soi___ya 완전요 😍", "4주 전"));
    }
}
