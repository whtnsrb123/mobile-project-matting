package com.example.Matting;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class CreateCommunityFragment extends Fragment {
    private EditText etTitle, etContent, etLocation, etRestaurant, etDate, etTime;
    private Button btnCreatePost, dateButton, timeButton;
    private ImageButton closeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_community, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etContent = view.findViewById(R.id.etContent);
        etLocation = view.findViewById(R.id.etLocation);
        etRestaurant = view.findViewById(R.id.etRestaurant);
        btnCreatePost = view.findViewById(R.id.btnCreatePost);
        closeButton = view.findViewById(R.id.btnClose);
        etDate = view.findViewById(R.id.etdate);
        etTime = view.findViewById(R.id.ettime);
        dateButton = view.findViewById(R.id.dateButton);
        timeButton = view.findViewById(R.id.timeButton);

        // 날짜/시간 변경 버튼 클릭 이벤트
        dateButton.setOnClickListener(v -> showDatePickerDialog());
        timeButton.setOnClickListener(v -> showTimePickerDialog());

        // 전달받은 데이터 수신
        Bundle arguments = getArguments();
        if (arguments != null) {
            String restaurant = arguments.getString("restaurant");
            if (restaurant != null) {
                etRestaurant.setText(restaurant); // 전달받은 title 값을 EditText에 설정
            }
        }

        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();
                String location = etLocation.getText().toString();
                String restaurant = etRestaurant.getText().toString();

                if (!title.isEmpty() && !content.isEmpty()) {
                    // CommunityActivity로 데이터 전달
                    Intent intent = new Intent(getActivity(), CommunityActivity.class);

                    intent.putExtra("title", title);
                    intent.putExtra("content", content);
                    intent.putExtra("location", location);
                    intent.putExtra("restaurant", restaurant);

                    // 스택을 초기화하고 새로운 액티비티를 시작하기 위한 플래그 설정
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // 제목 또는 내용이 비어있을 경우 메시지 표시
                    Toast.makeText(getActivity(), "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CreateFragment를 닫고 BackStack에서 이전 프래그먼트(InfoFragment)를 다시 보여줌
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void showDatePickerDialog() {
        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog 생성 및 표시
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = selectedYear + "년 " + (selectedMonth + 1) + "월 " + selectedDay + "일";
            etDate.setText(formattedDate); // 선택한 날짜를 EditText에 설정
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        // 현재 시간 가져오기
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // TimePickerDialog 생성 및 표시
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, selectedHour, selectedMinute) -> {
            String period = selectedHour < 12 ? "오전" : "오후";
            int adjustedHour = selectedHour % 12 == 0 ? 12 : selectedHour % 12; // 12시 형식 처리
            String formattedTime = period + " " + adjustedHour + "시 " + selectedMinute + "분";
            etTime.setText(formattedTime); // 선택한 시간을 EditText에 설정
        }, hour, minute, false);

        timePickerDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // MainActivity의 BottomNavigationView 숨기기
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigation);
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // InfoFragment가 화면을 떠날 때 BottomNavigationView 다시 표시
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigation);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}
