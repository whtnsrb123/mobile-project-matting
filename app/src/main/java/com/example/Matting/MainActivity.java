package com.example.Matting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    LocationManager locationManager;
    private double cur_lat = 37.5665; // 초기값 (서울 예시)
    private double cur_lon = 126.9780;
    private NaverMap naverMap;
    private Marker marker = new Marker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위치 요청
        getLocation();

        // 지도 초기화
        initMap();

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_home); // 세 번째 아이템 선택

        // 네비게이션 아이템 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_feed) {
                    // 피드 액티비티로 이동
                    Intent feedIntent = new Intent(MainActivity.this, Feed_MainActivity.class);
                    startActivity(feedIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    // 커뮤니티 액티비티로 이동
                    Intent communityIntent = new Intent(MainActivity.this, CommunityActivity.class);
                    startActivity(communityIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }else if (itemId == R.id.nav_chat) {
                    // 챗 액티비티로 이동
                    Intent communityIntent = new Intent(MainActivity.this, Chat_ChatroomActivity.class);
                    startActivity(communityIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_mypage) {
                    // 마이페이지 액티비티로 이동
                    Intent mypageIntent = new Intent(MainActivity.this, MyProfileActivity.class);
                    startActivity(mypageIntent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    // 위치 가져오기 메서드
    private void getLocation() {
        // 위치 권한이 있는지 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location loc_Current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (loc_Current != null) {
                cur_lat = loc_Current.getLatitude();
                cur_lon = loc_Current.getLongitude();
                updateMapLocation();
            } else {
                // 위치를 가져올 수 없는 경우의 처리
                cur_lat = 37.5665; // 기본값 (서울)
                cur_lon = 126.9780;
            }
        } else {
            // 권한이 없을 경우 사용자에게 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
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
                13,                           // 줌 레벨
                0,                          // 기울임 각도
                0                           // 방향
        );
        naverMap.setCameraPosition(cameraPosition);
        setMark(marker, R.drawable.baseline_place_24, 0);
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우
                getLocation();
            } else {
                // 권한이 거부된 경우 기본 위치 사용
                cur_lat = 37.5665; // 서울
                cur_lon = 126.9780;
            }
        }
    }

    // 지도 마커
    private void setMark(Marker marker, int resourceID, int zIndex)
    {
        //원근감 표시
        marker.setIconPerspectiveEnabled(true);
        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(resourceID));
        //마커의 투명도
        marker.setAlpha(0.8f);
        //마커 위치
        marker.setPosition(new LatLng(cur_lat, cur_lon));
        //마커 우선순위
        marker.setZIndex(zIndex);
        //마커 표시
        marker.setMap(naverMap);
    }
}
