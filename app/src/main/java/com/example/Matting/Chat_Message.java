package com.example.Matting;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chat_Message {
    private String userId;
    private String message;
    private long timestamp;
    private String nickName;

    public Chat_Message() {
    }

    public Chat_Message(String userId, String message, long timestamp) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void getUserNickname(final NicknameCallback callback) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("nicknames");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nickName = snapshot.getValue(String.class);
                callback.onCallback(nickName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public interface NicknameCallback {
        void onCallback(String nickName);
    }
}
