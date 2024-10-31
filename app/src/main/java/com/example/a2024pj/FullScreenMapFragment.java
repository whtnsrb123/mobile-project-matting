package com.example.a2024pj;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

public class FullScreenMapFragment extends Fragment implements OnMapReadyCallback {
    private double cur_lat, cur_lon;
    private NaverMap naverMap;

    public FullScreenMapFragment(double latitude, double longitude) {
        this.cur_lat = latitude;
        this.cur_lon = longitude;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Full screen layout 생성
        View view = inflater.inflate(R.layout.fragment_full_screen_map, container, false);

        // 닫기 버튼 설정
        Button closeButton = view.findViewById(R.id.btn_close_map);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FragmentManager를 통해 현재 Fragment를 제거하여 이전 화면으로 돌아갑니다.
                getParentFragmentManager().popBackStack();
            }
        });

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.full_screen_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.full_screen_map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setMapType(NaverMap.MapType.Basic);
        CameraPosition cameraPosition = new CameraPosition(new LatLng(cur_lat, cur_lon), 13);
        naverMap.setCameraPosition(cameraPosition);
    }
}

