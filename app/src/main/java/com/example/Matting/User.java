package com.example.Matting;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {
    private String userId;
    private Context context;
    private String email;
    private String nickName;

    // Context를 생성자로 받아서 초기화합니다.
    public User(Context context) {
        this.context = context;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // 이메일 주소 및 ID 설정
            this.email = user.getEmail();
            this.userId = user.getUid();

            // 사용자 닉네임 설정
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(this.userId).child("nicknames");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nickName = snapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 에러 처리
                }
            });
        }
    }

    public String getEmail() {
        return this.email;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getNickName() {
        return this.nickName;
    }
}
