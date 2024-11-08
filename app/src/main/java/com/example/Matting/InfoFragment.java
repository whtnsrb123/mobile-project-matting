package com.example.Matting;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class InfoFragment extends Fragment {
    private TextView tvTitle, tvCategory, tvDescription, tvRating;
    private ImageView btnClose;

    public static InfoFragment newInstance(String title, String category, String description, double rating) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("category", category);
        args.putString("description", description);
        args.putDouble("rating", rating);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);

        // UI 초기화
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCategory = view.findViewById(R.id.tvCategory);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvRating = view.findViewById(R.id.tvRating);
        btnClose = view.findViewById(R.id.btnClose);

        if (getArguments() != null) {
            tvTitle.setText(getArguments().getString("title"));
            tvCategory.setText(getArguments().getString("category"));
            tvDescription.setText(getArguments().getString("description"));
            tvRating.setText(String.valueOf(getArguments().getDouble("rating")));
        }

        // 닫기 버튼 설정
        btnClose.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }
}
