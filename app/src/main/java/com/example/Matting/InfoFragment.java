package com.example.Matting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {
    private TextView tvTitle, tvCategory, tvDescription, tvLink, tvRating, tvMapX, tvMapY;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private ImageView btnClose;

    public static InfoFragment newInstance(String title, String category, String description, String link, double rating, int map_x, int map_y) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("category", category);
        args.putString("description", description);
        args.putString("link", link);
        args.putDouble("rating", rating);
        args.putInt("map_x", map_x);
        args.putInt("map_y", map_y);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);

        // UI 초기화
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCategory = view.findViewById(R.id.tvCategory);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvLink = view.findViewById(R.id.tvLink);
        tvRating = view.findViewById(R.id.tvRating);
        tvMapX = view.findViewById(R.id.tvMapX);
        tvMapY = view.findViewById(R.id.tvMapY);
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        btnClose = view.findViewById(R.id.btnClose); // 닫기 버튼 초기화

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance();

        // RecyclerView 설정
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewRecyclerView.setAdapter(reviewAdapter);

        // Arguments 확인
        if (getArguments() != null) {
            tvTitle.setText(getArguments().getString("title"));
            tvCategory.setText(getArguments().getString("category"));
            tvDescription.setText(getArguments().getString("description"));
            tvLink.setText(getArguments().getString("link"));
            tvRating.setText(String.valueOf(getArguments().getDouble("rating")));
            tvMapX.setText(String.valueOf(getArguments().getInt("map_x")));
            tvMapY.setText(String.valueOf(getArguments().getInt("map_y")));
        } else {
            Log.e("InfoFragment", "getArguments() returned null");
        }

        // Firestore에서 리뷰 데이터 가져오기
        fetchReviewsFromFirestore();

        // 닫기 버튼 동작
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // InfoFragment를 닫음
                getParentFragmentManager().popBackStack();

                // MainActivity의 메서드를 호출하여 BottomSheet 상태를 STATE_EXPANDED로 설정
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).showBottomSheetExpanded();
                } else {
                    Log.e("BottomSheetDebug", "MainActivity instance not found");
                }
            }
        });


        return view;
    }

    private void fetchReviewsFromFirestore() {
        firestore.collection("review")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Review review = document.toObject(Review.class);
                            reviewList.add(review);
                        }
                        reviewAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreError", "리뷰 데이터를 가져오는 데 실패했습니다.");
                    }
                });
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
