package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chat_ChatroomActivity extends AppCompatActivity {
    private ListView listViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;
    private Chat_MessageAdapter chatMessageAdapter;
    private List<Chat_Message> chatMessageList;
    private DatabaseReference db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.chat_chatroom);

        listViewMessages = findViewById(R.id.listViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        user = new User(this);
        db = FirebaseDatabase.getInstance().getReference("chat");
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

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_chat); // 세 번째 아이템 선택

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // 메인 액티비티로 이동
                    Intent homeIntent = new Intent(Chat_ChatroomActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    // 피드 액티비티로 이동
                    Intent feedIntent = new Intent(Chat_ChatroomActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent chatIntent = new Intent(Chat_ChatroomActivity.this, Chat_ChatroomActivity.class);
                    startActivity(chatIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    // 커뮤니티 액티비티로 이동
                    Intent communityIntent = new Intent(Chat_ChatroomActivity.this, CommunityActivity.class);
                    startActivity(communityIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(Chat_ChatroomActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void sendMessage(String message) {
        Chat_Message chatMessageData = new Chat_Message(user.getUserId(), message, System.currentTimeMillis());
        db.push().setValue(chatMessageData);
    }

    private void receiveMessages() {
        db.addValueEventListener(new ValueEventListener() {
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
