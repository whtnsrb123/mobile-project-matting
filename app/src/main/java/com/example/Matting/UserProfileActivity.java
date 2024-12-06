package com.example.Matting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView followerCountTextView, followingCountTextView;
    private ImageView profileImageView; // 프로필 이미지를 표시할 ImageView
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // View 초기화
        profileImageView = findViewById(R.id.profileImage);
        userNameTextView = findViewById(R.id.user_name);
        followerCountTextView = findViewById(R.id.followerCount);
        followingCountTextView = findViewById(R.id.followingCount);

        // 전달받은 userId 가져오기
        userId = getIntent().getStringExtra("username");
        Log.d("UserProfileActivity", "Received username: " + userId);

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // postList 초기화 및 RecyclerView 설정
        postList = new ArrayList<>();
        setupRecyclerView();

        // 사용자 데이터 및 프로필 이미지 불러오기
        fetchUserData();
        fetchProfileImage(); // 프로필 이미지 불러오기

        // Firestore에서 게시글 로드
        fetchPosts();

        // 팔로워 및 팔로잉 수 불러오기
        fetchFollowerCount();
        fetchFollowingCount();

        // 액션바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        getSupportActionBar().setTitle(""); // 액션바 제목 비우기 또는 원하는 값으로 설정

        // 팔로워 및 팔로잉 버튼 동작 설정
        setupFollow_FollowingButtons();

        // 팔로우 버튼 설정
        setupFollowButton();


        loadProfileImage();
    }
    // 뒤로가기 버튼 동작 처리
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 현재 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchUserData() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nickname = snapshot.child("nicknames").getValue(String.class);
                    userNameTextView.setText(nickname != null && !nickname.isEmpty() ? nickname : userId);
                } else {
                    Toast.makeText(UserProfileActivity.this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "사용자 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("UserProfileActivity", "Error fetching user data", error.toException());
            }
        });
    }

    private void fetchProfileImage() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("profileImage")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String base64Image = snapshot.getValue(String.class); // Base64로 저장된 이미지
                            if (base64Image != null && !base64Image.isEmpty()) {
                                try {
                                    byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT); // Base64 디코딩
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length); // Bitmap 변환
                                    profileImageView.setImageBitmap(bitmap); // 이미지 설정
                                } catch (Exception e) {
                                    Log.e("UserProfileActivity", "Error decoding Base64 image", e);
                                    setDefaultProfileImage();
                                }
                            } else {
                                setDefaultProfileImage();
                            }
                        } else {
                            setDefaultProfileImage();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserProfileActivity", "Error loading profile image", error.toException());
                        setDefaultProfileImage();
                    }
                });
    }

    private void setDefaultProfileImage() {
        Glide.with(this)
                .load(R.drawable.default_profile_image) // 기본 이미지
                .circleCrop()
                .into(profileImageView);
    }

    // Firestore에서 게시글 불러오기
    private void fetchPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .whereEqualTo("username", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    queryDocumentSnapshots.forEach(document -> {
                        try {
                            String documentId = document.getString("documentId");
                            String username = document.getString("username");
                            String postContent = document.getString("postContent");
                            String imageResource = document.getString("imageResource");

                            int commentCount = document.contains("commentCount") ? document.getLong("commentCount").intValue() : 0;
                            int reactionCount = document.contains("reactionCount") ? document.getLong("reactionCount").intValue() : 0;
                            boolean reacted = document.contains("reacted") ? document.getBoolean("reacted") : false;

                            postList.add(new Post(
                                    documentId != null ? documentId : "",
                                    username != null ? username : "",
                                    postContent != null ? postContent : "",
                                    imageResource != null ? imageResource : "",
                                    document.getTimestamp("timestamp"),
                                    commentCount,
                                    reactionCount,
                                    reacted
                            ));
                        } catch (Exception e) {
                            Log.e("UserProfileActivity", "Error parsing post data", e);
                        }
                    });
                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("UserProfileActivity", "Error fetching posts", e));
    }

    // RecyclerView 설정
    private void setupRecyclerView() {
        postRecyclerView = findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        postAdapter = new PostAdapter(this, postList, true);
        postRecyclerView.setAdapter(postAdapter);
    }



    // 팔로워 및 팔로잉 버튼 설정
    private void setupFollow_FollowingButtons() {
        LinearLayout followersLayout = findViewById(R.id.followersLayout);
        LinearLayout followingLayout = findViewById(R.id.followingLayout);

        followersLayout.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, FollowersActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        followingLayout.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, FollowingActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    // 팔로워 수 불러오기
    private void fetchFollowerCount() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                followerCountTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserProfileActivity", "Error fetching followers count", error.toException());
            }
        });
    }

    // 팔로잉 수 불러오기
    private void fetchFollowingCount() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                followingCountTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserProfileActivity", "Error fetching following count", error.toException());
            }
        });
    }

    // 팔로우 버튼 설정
    private void setupFollowButton() {
        Button followButton = findViewById(R.id.followButton);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child("users").child(userId).child("followers").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followButton.setText(snapshot.exists() ? "팔로잉" : "팔로우");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserProfileActivity", "Error checking follow status", error.toException());
                    }
                });

        followButton.setOnClickListener(v -> {
            databaseRef.child("users").child(userId).child("followers").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Unfollow
                        databaseRef.child("users").child(userId).child("followers").child(currentUserId).removeValue();
                        databaseRef.child("users").child(currentUserId).child("following").child(userId).removeValue();
                        followButton.setText("팔로우");
                    } else {
                        // Follow
                        databaseRef.child("users").child(userId).child("followers").child(currentUserId).setValue(true);
                        databaseRef.child("users").child(currentUserId).child("following").child(userId).setValue(true);
                        followButton.setText("팔로우 취소");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UserProfileActivity", "Error toggling follow status", error.toException());
                }
            });
        });
    }
    // 프로필 이미지
    private void loadProfileImage() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(userId).child("profileImage")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String base64Image = snapshot.getValue(String.class); // Base64로 저장된 이미지
                            if (base64Image != null && !base64Image.isEmpty()) {
                                try {
                                    byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT); // Base64 디코딩
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length); // Bitmap 변환
                                    profileImageView.setImageBitmap(bitmap); // 이미지 설정
                                } catch (Exception e) {
                                    Log.e("UserProfileActivity", "Error decoding Base64 image", e);
                                    profileImageView.setImageResource(R.drawable.default_profile_image); // 기본 이미지 설정
                                }
                            } else {
                                profileImageView.setImageResource(R.drawable.default_profile_image); // 기본 이미지 설정
                            }
                        } else {
                            profileImageView.setImageResource(R.drawable.default_profile_image); // 기본 이미지 설정
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserProfileActivity", "Error loading profile image", error.toException());
                        profileImageView.setImageResource(R.drawable.default_profile_image); // 기본 이미지 설정
                    }
                });
    }
}
