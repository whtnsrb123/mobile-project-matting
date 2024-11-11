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
    private TextView tvTitle, tvCategory, tvDescription, tvLink, tvRating, tvMapX, tvMapY;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);

        // UI 초기화
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCategory = view.findViewById(R.id.tvCategory);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvLink = view.findViewById(R.id.tvLink);
        tvRating = view.findViewById(R.id.tvRating);
        tvMapX = view.findViewById(R.id.tvMapX);
        tvMapY = view.findViewById(R.id.tvMapY);
        btnClose = view.findViewById(R.id.btnClose);

        if (getArguments() != null) {
            tvTitle.setText(getArguments().getString("title"));
            tvCategory.setText(getArguments().getString("category"));
            tvDescription.setText(getArguments().getString("description"));
            tvLink.setText(getArguments().getString("link"));
            tvRating.setText(String.valueOf(getArguments().getDouble("rating")));
            tvMapX.setText(String.valueOf(getArguments().getInt("map_x")));
            tvMapY.setText(String.valueOf(getArguments().getInt("map_y")));
        }

        // 닫기 버튼 설정
        btnClose.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }
}
