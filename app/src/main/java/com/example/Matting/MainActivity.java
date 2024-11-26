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
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String CLIENT_ID = "WkJqg1dwU0AIZSFbQ4Ld"; // 네이버 애플리케이션 클라이언트 ID
    private static final String CLIENT_SECRET = "PvoSkMQnin"; // 네이버 애플리케이션 클라이언트 시크릿
    LocationManager locationManager;
    private FusedLocationSource locationSource;
    private String currentAddress = "서울특별시 중구 태평로1가"; // 초기값
    private double cur_lat = 37.5665; // 초기값 (서울 예시)
    private double cur_lon = 126.9780;
    private NaverMap naverMap;
    private Marker marker = new Marker();
    private RecyclerView restaurantRecyclerView;
    private MainAdapter mainAdapter;
    private List<Main> mainList;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView showBottomSheetButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);


        //로그인 확인
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // 로그인 페이지로 이동하고 결과를 기다림
            Intent loginIntent = new Intent(MainActivity.this, User_LoginActivity.class);
            startActivityForResult(loginIntent, 1001); // 1001은 요청 코드
        }


        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

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

        ImageButton searchButton = findViewById(R.id.search_button);
        EditText searchEditText = findViewById(R.id.search_edit_text);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                Log.d("search_bottom", "Query: " + query);
                bottomNavigationView.setVisibility(View.VISIBLE);
                callNaverSearchAPI(query + "맛집");
            }
        });

        // 버튼들을 가져오기
        AppCompatButton category1 = findViewById(R.id.category1);
        AppCompatButton category2 = findViewById(R.id.category2);
        AppCompatButton category3 = findViewById(R.id.category3);
        AppCompatButton category4 = findViewById(R.id.category4);

        // 카테고리 공통 리스너 설정
        View.OnClickListener searchClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatButton button = (AppCompatButton) view;
                String keyword = button.getText().toString(); // 버튼의 텍스트를 검색어로 사용

                // 현재 주소와 키워드 조합
                String combinedKeyword = currentAddress + " " + keyword;

                callNaverSearchAPI(combinedKeyword); // 클릭한 버튼의 텍스트로 API 호출
            }
        };

        // 각 버튼에 리스너 설정
        category1.setOnClickListener(searchClickListener);
        category2.setOnClickListener(searchClickListener);
        category3.setOnClickListener(searchClickListener);
        category4.setOnClickListener(searchClickListener);

        // BottomNavigationView 초기화
        bottomNavigationView.setSelectedItemId(R.id.nav_home); // 세 번째 아이템 선택

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText에 포커스가 있을 때 BottomNavigation 숨기기
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    // EditText 포커스를 잃을 때 BottomNavigation 보이기
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });
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
                } else if (itemId == R.id.nav_chat) {
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

    private void getReverseGeocode(final double latitude, final double longitude) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String query = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords="
                            + longitude + "," + latitude
                            + "&orders=roadaddr&output=json";

                    URL url = new URL(query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "7f3g9xuc1m");
                    conn.setRequestProperty("X-NCP-APIGW-API-KEY", "ISZs61UO1NxiPScknj1dN4Mgp6aq7kZrJBFL2oFd");

                    int responseCode = conn.getResponseCode();
                    Log.d("ReverseGeocode", "Response Code: " + responseCode);

                    BufferedReader bufferedReader;
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    String res = stringBuilder.toString();
                    Log.d("ReverseGeocode", "Response: " + res);

                    // JSON 응답에서 주소를 추출하고 검색어로 사용
                    String defaultSearchKeyword = parseAddressForSearch(res);
                    currentAddress = defaultSearchKeyword;
                    if (defaultSearchKeyword != null) {
                        // UI 스레드에서 디폴트 검색어로 API 호출
                        runOnUiThread(() -> callNaverSearchAPI(defaultSearchKeyword + " 맛집"));
                    }

                    bufferedReader.close();
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    Log.e("ReverseGeocode", "URL 형식 오류: " + e.getMessage());
                } catch (IOException e) {
                    Log.e("ReverseGeocode", "네트워크 통신 오류: " + e.getMessage());
                } catch (Exception e) {
                    Log.e("ReverseGeocode", "기타 오류: " + e);
                }
            }
        }).start();
    }

    private String parseAddressForSearch(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            if (resultsArray.length() > 0) {
                JSONObject firstResult = resultsArray.getJSONObject(0);
                JSONObject region = firstResult.getJSONObject("region");

                // 지역명 추출 (예: "서울특별시 중구 태평로1가")
                String area1 = region.getJSONObject("area1").getString("name"); // "서울특별시"
                String area2 = region.getJSONObject("area2").getString("name"); // "중구"
                String area3 = region.getJSONObject("area3").getString("name"); // "태평로1가"

                return area1 + " " + area2 + " " + area3; // 디폴트 검색어로 반환
            }
        } catch (Exception e) {
            Log.e("ParseError", "JSON 파싱 오류", e);
        }
        return null; // 주소를 가져오지 못한 경우
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (locationSource.isActivated()) {
                getLastKnownLocationAndUpdateMap(); // 권한 승인 후 위치 가져오기
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);

        // 위치 버튼 활성화
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        // 위치 추적 모드 설정
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        // 현재 위치 가져오기 및 지도 중심 이동
        getLastKnownLocationAndUpdateMap();
    }

    private void getLastKnownLocationAndUpdateMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // GPS_PROVIDER가 없으면 NETWORK_PROVIDER 사용
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                Log.d("LocationDebug", "현재 위치: lat = " + latitude + ", lon = " + longitude);

                // 현재 위치로 지도 중심 이동
                updateMapCenter(latitude, longitude);

                getReverseGeocode(latitude, longitude);
            } else {
                Log.d("LocationDebug", "현재 위치를 가져올 수 없음. 기본 위치(서울)로 설정");
                // 서울 중심 좌표로 지도 설정
                updateMapCenter(37.5665, 126.9780); // 서울 중심 좌표
            }
        } else {
            // 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void updateMapCenter(double latitude, double longitude) {
        if (naverMap != null) {
            CameraPosition cameraPosition = new CameraPosition(
                    new LatLng(latitude, longitude), // 위치 지정
                    15,                             // 줌 레벨
                    0,                              // 기울임 각도
                    0                               // 방향
            );
            naverMap.setCameraPosition(cameraPosition);

//            getReverseGeocode(latitude, longitude);
            // 선택적으로 마커를 표시
//            setMark(marker, R.drawable.baseline_place_24, 0, latitude, longitude);
        }
    }


    // 지도 마커
    private void setMark(Marker marker, int resourceID, int zIndex, double lat, double lon) {
        //원근감 표시
        marker.setIconPerspectiveEnabled(true);
        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(resourceID));
        //마커의 투명도
        marker.setAlpha(0.8f);
        //마커 위치
        marker.setPosition(new LatLng(lat, lon));
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
