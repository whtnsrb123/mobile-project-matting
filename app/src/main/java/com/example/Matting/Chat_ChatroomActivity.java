package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat_ChatroomActivity extends AppCompatActivity {
    private ListView listViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;
    private Chat_MessageAdapter chatMessageAdapter;
    private List<Chat_Message> chatMessageList;
    private DatabaseReference db;
    private User user;
    private String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.chat_chatroom);

        // 액션바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 뒤로가기 버튼 활성화
        getSupportActionBar().setTitle(chatroomId);


        listViewMessages = findViewById(R.id.listViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        user = new User(this);
        // Intent로부터 채팅방 ID를 가져옴
        Intent intent = getIntent();
        chatroomId = intent.getStringExtra("chatRoomId");

        db = FirebaseDatabase.getInstance().getReference().child("chatroomlist").child(chatroomId);
        db.child("id").setValue(chatroomId);
        chatMessageList = new ArrayList<>();
        chatMessageAdapter = new Chat_MessageAdapter(this, chatMessageList, user.getUserId());
        listViewMessages.setAdapter(chatMessageAdapter);

        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                editTextMessage.setText("");
            }
        });

        receiveMessages();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 뒤로가기 버튼 클릭 시 이전 액티비티로 이동
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(String message) {
        String messageId = db.child("messages").push().getKey();
        Chat_Message chatMessageData = new Chat_Message(user.getUserId(), message, System.currentTimeMillis());
        db.child("messages").child(messageId).setValue(chatMessageData);
    }

    private void receiveMessages() {
        db.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessageList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Chat_Message chatMessage = messageSnapshot.getValue(Chat_Message.class);
                    if (chatMessage != null) {
                        chatMessageList.add(chatMessage);
                    }
                }
                chatMessageAdapter.notifyDataSetChanged();
                listViewMessages.setSelection(chatMessageAdapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
