package com.example.Matting;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

public class User_LoginActivity extends AppCompatActivity {

    private static final String TAG = "User_LoginActivity";
    private FirebaseAuth mAuth;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        // FirebaseAuth 초기화
        mAuth = FirebaseAuth.getInstance();

        // 뷰 초기화
        emailInput = findViewById(R.id.idInput); // 이메일 입력 필드
        passwordInput = findViewById(R.id.passwordInput); // 비밀번호 입력 필드
        loginButton = findViewById(R.id.loginButton); // 로그인 버튼
        signupButton = findViewById(R.id.signupButton); // 회원가입 버튼

        // 로그인 버튼 클릭 이벤트
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(User_LoginActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(User_LoginActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase 로그인 처리
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(User_LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 로그인 성공
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // 로그인 실패
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(User_LoginActivity.this, "로그인 실패: 이메일 또는 비밀번호를 확인하세요.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
        });

        // 회원가입 버튼 클릭 이벤트
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 액티비티로 이동
                Intent signupIntent = new Intent(User_LoginActivity.this, User_SignUpActivity.class);
                startActivity(signupIntent);
            }
        });
    }

    // 로그인 결과 처리 메서드
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // 로그인 성공 시 결과 전달 및 액티비티 종료
            Intent resultIntent = new Intent();
            resultIntent.putExtra("userEmail", user.getEmail()); // 필요 시 이메일 정보를 전달
            setResult(Activity.RESULT_OK, resultIntent); // RESULT_OK와 데이터를 이전 액티비티에 전달
            finish(); // 현재 액티비티 종료
        } else {
            // 로그인 실패 처리 (별도 메시지를 띄울 수도 있음)
            Toast.makeText(this, "로그인 정보를 확인하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
