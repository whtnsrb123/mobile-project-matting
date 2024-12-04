package com.example.Matting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

public class MeetingInfoFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_CHATROOM_ID = "chatroomId";
    private static final String ARG_DOCUMENT_ID = "documentId";
    private String chatroomId;
    private String documentId;
    private String restaurant;
    private String location;
    private String date;
    private String time;
    private double cur_lat, cur_lon;

    private Marker marker = new Marker();
    private NaverMap naverMap;

    private TextView restaurantname;
    private TextView tvLocation;
    private TextView meetDate;
    private TextView meetTime;

    public static MeetingInfoFragment newInstance(String chatroomId, String documentId) {
        MeetingInfoFragment fragment = new MeetingInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHATROOM_ID, chatroomId);
        args.putString(ARG_DOCUMENT_ID, documentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_info, container, false);

        // 툴바 설정
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("모임 정보");
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel); // 뒤로가기 아이콘 설정
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().beginTransaction().remove(MeetingInfoFragment.this).commit());

        // 전달된 chatroomId 및 documentId 설정
        if (getArguments() != null) {
            chatroomId = getArguments().getString(ARG_CHATROOM_ID);
            documentId = getArguments().getString(ARG_DOCUMENT_ID);
        }

        // Firestore에서 데이터 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("community").document(documentId);

        // TextView 초기화
        restaurantname = view.findViewById(R.id.restaurant_name);
        tvLocation = view.findViewById(R.id.tvLocation);
        meetDate = view.findViewById(R.id.date);
        meetTime = view.findViewById(R.id.time);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("getDocumentId", "DocumentSnapshot data: " + document.getData());
                    location = document.getString("location");
                    date = document.getString("date");
                    time = document.getString("time");
                    restaurant = document.getString("restaurant");
                    cur_lon = document.getString("mapx") != null ? Double.parseDouble(document.getString("mapx")) / 10_000_000.0 : 0.0;
                    cur_lat = document.getString("mapy") != null ? Double.parseDouble(document.getString("mapy")) / 10_000_000.0 : 0.0;

                    // TextView에 데이터 설정
                    restaurantname.setText(restaurant);
                    tvLocation.setText(location);
                    meetDate.setText(date);
                    meetTime.setText(time);

                    // Firestore 데이터가 로드된 후 지도 업데이트
                    if (naverMap != null) {
                        updateMap();
                    }
                } else {
                    Log.d("getDocumentId", "No such document");
                }
            } else {
                Log.d("getDocumentId", "get failed with ", task.getException());
            }
        });

        // 지도 초기화
        initMap();

        return view;
    }

    private void initMap() {
        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this); // OnMapReadyCallback 구현 필요
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        // Firestore 데이터가 이미 로드되었는지 확인 후 지도 업데이트
        updateMap();
    }

    private void updateMap() {
        if (cur_lat != 0.0 && cur_lon != 0.0 && naverMap != null) {
            // 지도 설정
            CameraPosition cameraPosition = new CameraPosition(
                    new LatLng(cur_lat, cur_lon),   // 위치 지정
                    16,                           // 줌 레벨
                    0,                          // 기울임 각도
                    0                           // 방향
            );
            naverMap.setCameraPosition(cameraPosition);

            marker.setPosition(new LatLng(cur_lat, cur_lon));
            marker.setMap(naverMap);
            marker.setCaptionText(restaurant);
        }
    }
}
