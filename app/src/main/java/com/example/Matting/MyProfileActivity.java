package com.example.Matting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {

    private static final String MEDIA_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;

    private static final String PREFS_NAME = "profile_prefs";
    private static final String PROFILE_IMAGE_URI_KEY = "profile_image_uri";

    private ImageView profileImage;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    // ActivityResultLauncher 선언
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    saveProfileImageUri(imageUri); // 선택한 이미지 URI를 저장
                    setProfileImage(imageUri); // 선택한 이미지 설정
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(this, "갤러리에 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        profileImage = findViewById(R.id.profileImage);
        loadProfileImage(); // 앱이 시작될 때 저장된 프로필 이미지 로드

        // 프로필 수정 버튼
        Button editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> checkPermissionAndOpenGallery());

        // 팔로워 버튼
        Button followersButton = findViewById(R.id.followersListButton);
        followersButton.setOnClickListener(this::openFollowersList); // 팔로워 화면 이동

        // 팔로잉 버튼
        Button followingButton = findViewById(R.id.followingListButton);
        followingButton.setOnClickListener(this::openFollowingList); // 팔로잉 화면 이동

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_mypage);

        // 네비게이션 아이템 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(MyProfileActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_feed) {
                    Intent feedIntent = new Intent(MyProfileActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    Intent chatIntent = new Intent(MyProfileActivity.this, Chat_ChatroomActivity.class);
                    startActivity(chatIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    Intent communityIntent = new Intent(MyProfileActivity.this, CommunityActivity.class);
                    startActivity(communityIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    return true;
                }
                return false;
            }
        });

        // RecyclerView 설정
        setupRecyclerView();
    }

    // 팔로워 화면 이동 메서드
    public void openFollowersList(View view) {
        Intent intent = new Intent(MyProfileActivity.this, FollowersActivity.class);
        startActivity(intent);
    }

    // 팔로잉 화면 이동 메서드
    public void openFollowingList(View view) {
        Intent intent = new Intent(MyProfileActivity.this, FollowingActivity.class);
        startActivity(intent);
    }

    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, MEDIA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(MEDIA_PERMISSION);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 이미지 파일만 표시
        galleryLauncher.launch(intent);
    }

    private void saveProfileImageUri(Uri imageUri) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_IMAGE_URI_KEY, imageUri.toString()); // URI를 문자열로 저장
        editor.apply();
    }

    private void loadProfileImage() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String imageUriString = sharedPreferences.getString(PROFILE_IMAGE_URI_KEY, null);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            setProfileImage(imageUri); // 저장된 URI로 프로필 이미지 설정
        } else {
            Glide.with(this)
                    .load(R.drawable.profile_image)
                    .circleCrop()
                    .into(profileImage);
        }
    }

    private void setProfileImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .circleCrop() // 이미지를 원형으로 자르기
                .into(profileImage);
    }

    private void setupRecyclerView() {
        postRecyclerView = findViewById(R.id.postRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        postRecyclerView.setLayoutManager(gridLayoutManager);

        postList = PostData.getPostList();
        postAdapter = new PostAdapter(this, postList, true);
        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MyProfileActivity.this, PostViewerActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        postRecyclerView.setAdapter(postAdapter);
    }
}
