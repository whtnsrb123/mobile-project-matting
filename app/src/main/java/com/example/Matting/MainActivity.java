package com.example.Matting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource locationSource;

    private double cur_lat = 37.5665; // 초기값 (서울 예시)
    private double cur_lon = 126.9780;
    private NaverMap naverMap;
    private Marker marker = new Marker();
    LocationManager locationManager;

    private RecyclerView restaurantRecyclerView;
    private MainAdapter mainAdapter;
    private List<Main> mainList;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView showBottomSheetButton;

    private static final String CLIENT_ID = "WkJqg1dwU0AIZSFbQ4Ld"; // 네이버 애플리케이션 클라이언트 ID
    private static final String CLIENT_SECRET = "PvoSkMQnin"; // 네이버 애플리케이션 클라이언트 시크릿

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // 위치 요청
        getLocation();
        // 지도 초기화
        initMap();

        // BottomSheet View 초기화
        View bottomSheet = findViewById(R.id.activity_main_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // 버튼 초기화
        showBottomSheetButton = findViewById(R.id.infoIcon);

        // 버튼 클릭 이벤트 설정
        showBottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });

        // RecyclerView 설정
        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터 초기화 및 어댑터 연결
        mainList = new ArrayList<>();
        mainAdapter = new MainAdapter(this, mainList);
        restaurantRecyclerView.setAdapter(mainAdapter);

        // 디폴트 검색어로 초기 API 호출
        callNaverSearchAPI("서울 성북구");

        // 버튼들을 가져오기
        AppCompatButton category1 = findViewById(R.id.category1);
        AppCompatButton category2 = findViewById(R.id.category2);
        AppCompatButton category3 = findViewById(R.id.category3);
        AppCompatButton category4 = findViewById(R.id.category4);

        // 공통 리스너 설정
        View.OnClickListener searchClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatButton button = (AppCompatButton) view;
                String keyword = button.getText().toString(); // 버튼의 텍스트를 검색어로 사용
                callNaverSearchAPI(keyword); // 클릭한 버튼의 텍스트로 API 호출
            }
        };

        // 각 버튼에 리스너 설정
        category1.setOnClickListener(searchClickListener);
        category2.setOnClickListener(searchClickListener);
        category3.setOnClickListener(searchClickListener);
        category4.setOnClickListener(searchClickListener);

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
                    Intent communityIntent = new Intent(MainActivity.this, Chat_ChatlistActivity.class);
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
        Log.d("LocationDebug", "getLocation");
        // 위치 권한이 있는지 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Log.d("LocationDebug", "권한있음");
            Location loc_Current = (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null)
                    ? locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    : locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc_Current != null) {
                cur_lat = loc_Current.getLatitude();
                cur_lon = loc_Current.getLongitude();
                Log.d("LocationDebug", "Location updated in onLocationChanged: lat = " + cur_lat + ", lon = " + cur_lon);
                updateMapLocation();
            } else {
                Log.d("LocationDebug", "위치 못가져옴");
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
        Log.d("LocationDebug", "initMap");
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment_main);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment_main, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    // 지도 로드 완료 시 호출되는 콜백
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);

        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Basic);
        //건물 표시
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true);
        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(cur_lat, cur_lon),   // 위치 지정
                15,                           // 줌 레벨
                0,                          // 기울임 각도
                0                           // 방향
        );
        naverMap.setCameraPosition(cameraPosition);
//        setMark(marker, R.drawable.baseline_place_24, 0);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        NaverMapOptions options = new NaverMapOptions().locationButtonEnabled(true);

        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
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

    // 네이버 API 호출 메서드
    private String getNaverSearch(String keyword) {
        String text;
        try {
            text = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/local?query=" + text + "&display=5"; // JSON 결과
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", CLIENT_ID);
        requestHeaders.put("X-Naver-Client-Secret", CLIENT_SECRET);

        String responseBody = get(apiURL, requestHeaders);
        // JSON 응답을 파싱하고 리스트에 추가
        parseAndAddToList(responseBody);

        return responseBody;
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다: " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다: " + apiUrl, e);
        }
    }

    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);
        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }

    private void parseAndAddToList(String jsonResponse) {
        try {
            // 중복 방지를 위해 리스트 초기화
            mainList.clear();

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);

                String title = item.getString("title").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", ""); // HTML 태그 제거
                String category = item.getString("category");
                String address = item.optString("address", item.optString("roadAddress", "주소 없음")); // address 우선, 없으면 roadAddress 사용
                String link = item.getString("link");
                // 예시 평점 (데이터에 따라 변경 가능)
                double rating = 4.26;
                int map_x = item.getInt("mapx");
                int map_y = item.getInt("mapy");
                // Main 객체를 생성하고 리스트에 추가
                mainList.add(new Main(title, category, address, link, rating, map_x, map_y));
            }

            // RecyclerView에 데이터 갱신 알림
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainAdapter.notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // API 호출 메서드 (검색어를 받아서 호출하는 공통 메서드)
    private void callNaverSearchAPI(String keyword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = getNaverSearch(keyword);

                // UI 업데이트는 메인 스레드에서 실행해야 함
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null && !result.isEmpty()) {
                            parseAndAddToList(result);
                        } else {
                            Toast.makeText(MainActivity.this, "검색 결과를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void toggleBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showBottomSheet();  // BottomSheet을 보이게 설정
        } else {
            hideBottomSheet();    // BottomSheet을 숨기기
        }
    }

    // BottomSheet 숨기기
    public void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    // BottomSheet 보이기
    public void showBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    public void showBottomSheetExpanded() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}
