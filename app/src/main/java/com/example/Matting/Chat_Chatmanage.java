package com.example.Matting;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat_Chatmanage {
    static void addNewChatRoom(String chatRoomId, User user) {
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserId());

        // 기존 chats 목록을 가져와서 새로운 chatRoomId 추가
        db.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> chats = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        String existingChatRoomId = chatSnapshot.getValue(String.class);
                        if (existingChatRoomId != null) {
                            chats.add(existingChatRoomId);
                        }
                    }
                }
                chats.add(chatRoomId);

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
