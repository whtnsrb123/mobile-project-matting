package com.example.Matting;
import static com.example.Matting.Chat_Chatmanage.addNewChatRoom;

import android.Manifest;
import com.example.Matting.User;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    LocationManager locationManager;
    private double cur_lat = 37.59056; // 초기값 (서울 예시)
    private double cur_lon = 127.03639;
    private NaverMap naverMap;

    private RecyclerView mRecyclerView;
    private ArrayList<RecyclerViewItem> mList;
    private DetailRecyclerViewAdapter mDetailRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String restaurant = getIntent().getStringExtra("restaurant");
        String info = getIntent().getStringExtra("info");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();

        // 지도 초기화
        initMap();

        ImageButton fullScreenMapButton = findViewById(R.id.btn_full_screen_map);
        fullScreenMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullScreenMap();
            }
        });

        // 같은 식당의 다른 모임 글
        initRecyclerView();

        // 페이지 이동 버튼
        TextView goRestaurant = findViewById(R.id.go_restaurant);
        TextView goProgile = findViewById(R.id.go_profile);
        Button goChat = findViewById(R.id.go_chat);
        setupClickListenerForPopup(goRestaurant, "식당 정보", "식당 정보 페이지로 이동합니다.");
        setupClickListenerForPopup(goProgile, "아이디", "프로필 페이지로 이동합니다.");
        setupClickListenerForPopup(goChat, "채팅", "채팅 페이지로 이동합니다.");
        goChat.setOnClickListener(v ->  newCommunityChat());


        // 게시글, 본문
        TextView restaurantName = findViewById(R.id.restaurant_name);
        TextView postTitle = findViewById(R.id.post_title);
        TextView postContent = findViewById(R.id.post_content);
        TextView meetDate = findViewById(R.id.date);
        TextView meetTime = findViewById(R.id.time);
        restaurantName.setText(restaurant);
        postTitle.setText(title);
        postContent.setText(content);
        meetDate.setText(date);
        meetTime.setText(time);

        // 뒤로가기
        ImageButton goBackButton = findViewById(R.id.go_back);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 이미지 클릭 시 전체 화면으로 이미지 보기
        ImageView thumbnailImage = findViewById(R.id.thumbnail_image);
        thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullScreenImage(R.drawable.food);  // 확대할 이미지 리소스를 전달
            }
        });
    }

    // 맵 위치 업데이트 메서드
    private void updateMapLocation() {
        if (naverMap != null) {
            CameraPosition cameraPosition = new CameraPosition(new LatLng(cur_lat, cur_lon), 13);
            naverMap.setCameraPosition(cameraPosition);
        }
    }

    // 지도 초기화 메서드
    private void initMap() {
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    // 지도 로드 완료 시 호출되는 콜백
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Basic);
        //건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(cur_lat, cur_lon),   // 위치 지정
                15,                           // 줌 레벨
                0,                          // 기울임 각도
                0                           // 방향
        );
        naverMap.setCameraPosition(cameraPosition);
    }

    // 전체 화면 지도 표시 메서드
    private void showFullScreenMap() {
        FullScreenMapFragment fullScreenMapFragment = new FullScreenMapFragment(cur_lat, cur_lon);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fullScreenMapFragment)  // 현재 화면 전체를 덮도록 설정
                .addToBackStack(null)  // 뒤로가기 시 원래 화면으로 돌아가기
                .commit();
    }

    // 같은 식당의 다른 모임 글
    // RecyclerView 초기화 메서드
    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            addItem("iconName", "게시글 제목"+i, "2024년 00월 00일");
        }

        mDetailRecyclerViewAdapter = new DetailRecyclerViewAdapter(mList, this);
        mRecyclerView.setAdapter(mDetailRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }

    // RecyclerView 항목 추가 메서드
    public void addItem(String imgName, String mainText, String subText){
        RecyclerViewItem item = new RecyclerViewItem();

        item.setImgName(imgName);
        item.setMainText(mainText);
        item.setSubText(subText);

        mList.add(item);
    }

    // 팝업창
    private void setupClickListenerForPopup(TextView textView, String title, String message) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog 생성 및 설정
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle(title);
                builder.setMessage(message);

                // 확인 버튼 추가
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 팝업 닫기
                    }
                });

                // 팝업 창 표시
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // 전체 화면으로 이미지 표시 메서드
    private void showFullScreenImage(int imageResId) {
        // Dialog 생성
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 다이얼로그 타이틀 제거
        dialog.setContentView(R.layout.dialog_fullscreen_image); // 전체 화면 이미지를 위한 레이아웃 설정

        // Dialog의 ImageView 설정
        ImageView fullScreenImageView = dialog.findViewById(R.id.fullscreen_image);
        fullScreenImageView.setImageResource(imageResId);  // 이미지 리소스 설정

        // Dialog 클릭 시 닫기
        fullScreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //채팅방 연결
    private void newCommunityChat(){
        User user = new User(this);
        TextView postTitle = findViewById(R.id.post_title);
        addNewChatRoom(postTitle.getText().toString(),user);
        Intent intent = new Intent(DetailActivity.this, Chat_ChatroomActivity.class);
        intent.putExtra("chatRoomId", postTitle.getText().toString());
        startActivity(intent);


        return;
    }
}