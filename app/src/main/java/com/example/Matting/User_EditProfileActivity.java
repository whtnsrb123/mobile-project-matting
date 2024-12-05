package com.example.Matting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class User_EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button buttonSave;
    private User user;
    private EditText editNickname;
    private String base64Image;
    private ImageButton profileImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.user_editprofile);

        // 액션바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화

        // 프로필 이미지 업로드
        profileImageButton = findViewById(R.id.profile_image_button);
        profileImageButton.setOnClickListener(v -> openImageChooser());

        // 프로필 변경사항 저장
        user = new User(this);
        buttonSave = findViewById(R.id.buttonSave);
        editNickname = findViewById(R.id.editNickname);
        buttonSave.setOnClickListener(v -> {
            // 닉네임 변경
            DatabaseReference db;
            db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserId());
            db.child("nicknames").setValue(editNickname.getText().toString());
            // 프로필 사진 변경 추가
            if (base64Image != null) {
                db.child("profileImage").setValue(base64Image);
            }
            // MyProfileActivity 재실행
            Intent intent = new Intent(User_EditProfileActivity.this, MyProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageButton.setImageBitmap(bitmap);

                // 이미지 압축 및 Base64 인코딩
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int quality = 100; // 초기 압축 품질
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                while (baos.toByteArray().length >= 1024 * 1024) {
                    // 1MB 이하가 될 때까지 품질을 낮춤
                    quality -= 5;
                    baos.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                }
                byte[] imageBytes = baos.toByteArray();
                base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 뒤로가기 버튼 클릭 시 이전 액티비티로 이동
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
