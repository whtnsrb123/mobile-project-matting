package com.example.Matting;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<String> commentList;
    private EditText editTextComment;
    private Button buttonPostComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);

        recyclerView = findViewById(R.id.recyclerViewComments);
        editTextComment = findViewById(R.id.editTextComment);
        buttonPostComment = findViewById(R.id.buttonPostComment);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newComment = editTextComment.getText().toString().trim();
                if (!newComment.isEmpty()) {
                    commentList.add(newComment);
                    adapter.notifyItemInserted(commentList.size() - 1);
                    editTextComment.setText("");
                    recyclerView.scrollToPosition(commentList.size() - 1);
                } else {
                    Toast.makeText(CommentActivity.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
