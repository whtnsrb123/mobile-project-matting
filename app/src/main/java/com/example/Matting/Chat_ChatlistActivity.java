package com.example.Matting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class Chat_ChatlistActivity extends AppCompatActivity {
    private ListView listViewChatRooms;
    private List<String> chatRoomList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.chat_chatroomlist);
        User user = new User(this);

        listViewChatRooms = findViewById(R.id.listViewChatRooms);
        chatRoomList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatRoomList);
        listViewChatRooms.setAdapter(adapter);

        // Firebase에서 채팅방 목록을 로드
        FirebaseDatabase.getInstance().getReference("users").child(user.getUserId()).child("chats").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatRoomList.clear();
                for (DataSnapshot chatRoomSnapshot : snapshot.getChildren()) {
                    String chatRoomId = chatRoomSnapshot.getValue(String.class);
                    if (chatRoomId != null) {chatRoomList.add(chatRoomId); }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });


        listViewChatRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedChatRoomId = chatRoomList.get(position);
                Intent intent = new Intent(Chat_ChatlistActivity.this, Chat_ChatroomActivity.class);
                intent.putExtra("chatRoomId", selectedChatRoomId);
                startActivity(intent);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton addButton = findViewById(R.id.btn_add_chat);
        addButton.setOnClickListener(v -> addNewChatRoom());




        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_chat); // 채팅 탭 선택

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(Chat_ChatlistActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    Intent feedIntent = new Intent(Chat_ChatlistActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    Intent communityIntent = new Intent(Chat_ChatlistActivity.this, CommunityActivity.class);
                    startActivity(communityIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    // 채팅 액티비티로 이동
                    Intent chatIntent = new Intent(Chat_ChatlistActivity.this, Chat_ChatlistActivity.class);
                    startActivity(chatIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    Intent mypageIntent = new Intent(Chat_ChatlistActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
    private void addNewChatRoom() {
        DatabaseReference db;
        String newChatRoomId = "chatRoom_" + System.currentTimeMillis();
        User user = new User(this);
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserId());

        // 기존 chats 목록을 가져와서 새로운 chatRoomId 추가
        db.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> chats = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        String chatRoomId = chatSnapshot.getValue(String.class);
                        if (chatRoomId != null) {
                            chats.add(chatRoomId);
                        }
                    }
                }
                chats.add(newChatRoomId);

                // Firebase에 저장
                db.child("chats").setValue(chats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });
    }

}
