package com.example.Matting;

import android.content.Context;
import android.content.Intent;

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
                    if (snapshot.exists()) {
                        nickName = snapshot.getValue(String.class);
                    } else {
                        nickName = "Unknown"; // 기본값 설정
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 에러 처리
                }
            });
        } else {
            // 로그인 페이지로 이동하고 결과를 기다림
            Intent loginIntent = new Intent(context, User_LoginActivity.class);
            context.startActivity(loginIntent);
            // 현재 액티비티가 어떤 액티비티이든 종료
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).finish();
            }
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
