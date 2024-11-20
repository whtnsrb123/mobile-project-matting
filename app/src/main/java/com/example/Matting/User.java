package com.example.Matting;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class User {
    private String userId;
    private Context context;
    private String email;

    // Context를 생성자로 받아서 초기화합니다.
    public User(Context context) {
        this.context = context;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            this.email = user.getEmail();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            this.userId = user.getUid();
        }
    }

    public String getEmail() { return this.email; }

    public String getUserId() {
        return this.userId;
    }
}
