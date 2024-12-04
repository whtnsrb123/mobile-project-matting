package com.example.Matting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MeetingInfoFragment extends Fragment {

    private static final String ARG_CHATROOM_ID = "chatroomId";
    private String chatroomId;

    public static MeetingInfoFragment newInstance(String chatroomId) {
        MeetingInfoFragment fragment = new MeetingInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHATROOM_ID, chatroomId);
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
        toolbar.setNavigationOnClickListener(v -> {
            // 프래그먼트 종료
            getParentFragmentManager().beginTransaction().remove(MeetingInfoFragment.this).commit();
        });

        // 전달된 chatroomId 설정
        if (getArguments() != null) {
            chatroomId = getArguments().getString(ARG_CHATROOM_ID);
        }

        // chatroomId를 사용한 작업 수행 예:
        TextView textViewMeetingInfo = view.findViewById(R.id.textViewMeetingInfo);
        textViewMeetingInfo.setText("Chatroom ID: " + chatroomId);

        return view;
    }
}
