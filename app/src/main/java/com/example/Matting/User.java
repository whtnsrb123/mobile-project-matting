package com.example.Matting;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class User {
    private String userId;
    private Context context;

    // Context를 생성자로 받아서 초기화합니다.
    public User(Context context) {
        this.context = context;
        this.userId = make_user();
    }

    public String make_user() {
        // SharedPreferences를 사용하여 userId 불러오기
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.Matting.PREFERENCES", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        if (userId == null) {
            userId = "user_temp_" + UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userId", userId);
            editor.apply();
        }
        return userId;
    }

    public String getUserId() {
        return userId;
    }
}
