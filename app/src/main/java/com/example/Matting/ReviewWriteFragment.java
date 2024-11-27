package com.example.Matting;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReviewWriteFragment extends Fragment {
    private EditText etReviewContent;
    private Button btnSubmit;
    private ImageButton btnClose;
    private TextView tvRestaurant;
    private String address;
    private RatingBar ratingBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_fragment, container, false);

        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnClose = view.findViewById(R.id.btnClose);
        tvRestaurant = view.findViewById(R.id.tvRestaurant);
        etReviewContent = view.findViewById(R.id.etReviewContent);
        ratingBar = view.findViewById(R.id.ratingBar);
        Log.d("ratingBar", String.valueOf(ratingBar));

        // 전달받은 데이터 수신
        Bundle arguments = getArguments();
        if (arguments != null) {
            String restaurant = arguments.getString("restaurant");
            address = arguments.getString("address");
            if (restaurant != null) {
                tvRestaurant.setText(restaurant); // 전달받은 title 값을 EditText에 설정
            }
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = tvRestaurant.getText().toString();
                String content = etReviewContent.getText().toString();
                float rating = ratingBar.getRating();

                if (!content.isEmpty()) {
                    // Firebase Firestore에 리뷰 추가
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("review")
                            .add(new Review(address, content, Timestamp.now(), rating, "tmp_id")) // username은 예시로 고정값 설정
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getActivity(), "리뷰가 저장되었습니다", Toast.LENGTH_SHORT).show();

                                // 프래그먼트 종료 및 이전 프래그먼트로 돌아가기
                                getParentFragmentManager().popBackStack();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "리뷰 저장에 실패했습니다", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // 내용이 비어있을 경우 메시지 표시
                    Toast.makeText(getActivity(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CreateFragment를 닫고 BackStack에서 이전 프래그먼트(InfoFragment)를 다시 보여줌
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
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
