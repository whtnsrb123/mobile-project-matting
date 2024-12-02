package com.example.Matting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MeetingInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_info, container, false);

        // 툴바 설정
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("모임 정보");
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel); // 기본 뒤로가기 아이콘 설정
        toolbar.setNavigationOnClickListener(v -> {
            // 뒤로가기 클릭 시 프래그먼트 종료
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
