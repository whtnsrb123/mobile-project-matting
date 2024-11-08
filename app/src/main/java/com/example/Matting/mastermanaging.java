package com.example.Matting;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class mastermanaging {
    User user;
    private DatabaseReference db;

    // Context를 생성자로 받아서 초기화합니다.
    mastermanaging(Context context) {
        user = new User(context);
        addchatuser();
    }

    void addchatuser() {
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserId());

        // 배열 초기화 및 값 추가
        List<String> chats = new ArrayList<>();
        chats.add("testroom");
        chats.add("test2");
        // Firebase에 저장
        db.child("chats").setValue(chats);
    }
}
