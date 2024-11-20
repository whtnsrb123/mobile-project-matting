package com.example.Matting;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class User_SignUpActivity extends AppCompatActivity {

    private static final String TAG = "User_SignUpActivity";

    private FirebaseAuth mAuth;
    private EditText idInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup_page);

        // FirebaseAuth 초기화
        mAuth = FirebaseAuth.getInstance();

        // 뷰 초기화
        idInput = findViewById(R.id.idInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signupButton = findViewById(R.id.signupButton);

        // 회원가입 버튼 클릭 이벤트
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = idInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(User_SignUpActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(User_SignUpActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(User_SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(User_SignUpActivity.this, "비밀번호는 최소 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase로 회원가입 처리
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(User_SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 회원가입 성공
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(User_SignUpActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                                    finish(); // 현재 액티비티 종료
                                } else {
                                    // 회원가입 실패
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(User_SignUpActivity.this, "회원가입 실패: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
