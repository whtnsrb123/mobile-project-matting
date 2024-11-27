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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity implements WritePostFragment.OnPostUploadedListener {

    private static final String MEDIA_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PREFS_NAME = "profile_prefs";
    private static final String PROFILE_IMAGE_URI_KEY = "profile_image_uri";
    private ImageView profileImage;
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
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;


    private FirebaseAuth mAuth;

    @Override
    public void onPostUploaded(Map<String, Object> newPost) {
        // 새로운 게시글 데이터를 postList에 추가
        Post post = new Post(
                (String) newPost.get("documentId"),
                (String) newPost.get("username"),
                (String) newPost.get("postContent"),
                (String) newPost.get("imageResource"),
                (Timestamp) newPost.get("timestamp"),
                ((Long) newPost.get("commentCount")).intValue(),
                ((Long) newPost.get("reactionCount")).intValue(),
                (Boolean) newPost.get("reacted")
        );

        postList.add(0, post); // 가장 최신 게시글을 맨 위에 추가
        postAdapter.notifyItemInserted(0); // RecyclerView 업데이트
        postRecyclerView.scrollToPosition(0); // RecyclerView를 맨 위로 스크롤
    }

    //    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        //로그인 확인
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // 로그인 페이지로 이동하고 결과를 기다림
            Intent loginIntent = new Intent(MyProfileActivity.this, User_LoginActivity.class);
            startActivityForResult(loginIntent, 1001); // 1001은 요청 코드
        }

        // postList 초기화
        postList = new ArrayList<>();
        setupRecyclerView();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Firestore에서 게시글 로드
        PostData.fetchPosts(db, postList, () -> {
            if (postAdapter != null) {
                postAdapter.notifyDataSetChanged();
            }
        });

        profileImage = findViewById(R.id.profileImage);
        loadProfileImage(); // 앱이 시작될 때 저장된 프로필 이미지 로드

        // 프로필 수정 버튼
        Button editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, User_EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃 버튼
        ImageButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MyProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        // 팔로워 버튼 영역
        LinearLayout followersLayout = findViewById(R.id.followersLayout);
        followersLayout.setOnClickListener(this::openFollowersList); // 팔로워 화면 이동

        // 팔로잉 버튼 영역
        LinearLayout followingLayout = findViewById(R.id.followingLayout);
        followingLayout.setOnClickListener(this::openFollowingList); // 팔로잉 화면 이동

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
        postRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList, true);

        // 클릭 리스너 설정
        postAdapter.setOnPostClickListener(post -> {
            int position = postList.indexOf(post);
            Intent intent = new Intent(MyProfileActivity.this, PostViewerActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        });
        postRecyclerView.setAdapter(postAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String documentId = document.getString("documentId");
                            String username = document.getString("username");
                            String postContent = document.getString("postContent");
                            String imageResource = document.getString("imageResource");

                            // Timestamp를 그대로 사용
                            Timestamp timestampObject = document.getTimestamp("timestamp");

                            int commentCount = document.contains("commentCount") ? document.getLong("commentCount").intValue() : 0;
                            int reactionCount = document.contains("reactionCount") ? document.getLong("reactionCount").intValue() : 0;
                            boolean reacted = document.contains("reacted") ? document.getBoolean("reacted") : false;

                            // Timestamp 객체를 그대로 전달
                            postList.add(new Post(documentId, username, postContent, imageResource, timestampObject, commentCount, reactionCount, reacted));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
    }


    public void onWritePostClick(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new WritePostFragment()) // `fragment_container`는 프래그먼트를 담을 뷰 ID
                .addToBackStack(null) // 뒤로 가기 지원
                .commit();
    }
}
