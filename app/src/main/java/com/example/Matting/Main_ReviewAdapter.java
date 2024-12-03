package com.example.Matting;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Main_ReviewAdapter extends RecyclerView.Adapter<Main_ReviewAdapter.ReviewViewHolder> {
    private List<Main_Review> mainReviewList;

    public Main_ReviewAdapter(List<Main_Review> mainReviewList) {
        this.mainReviewList = mainReviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Main_Review mainReview = mainReviewList.get(position);
        holder.tvReviewerName.setText(mainReview.getUsername());
        holder.tvReviewContent.setText(mainReview.getContent());
        holder.tvReviewRating.setText("평점: " + mainReview.getRating());

        // 이미지 URL 로드
        if (mainReview.getImageResource() != null && !mainReview.getImageResource().isEmpty()) {
            // Base64 문자열을 디코딩
            byte[] decodedString = Base64.decode(mainReview.getImageResource(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            holder.ivReviewImage.setImageBitmap(decodedByte);
        } else {
            holder.ivReviewImage.setImageResource(R.drawable.ic_launcher_background); // 기본 이미지 설정
        }
    }

    @Override
    public int getItemCount() {
        return mainReviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewContent, tvReviewRating;
        ImageView ivReviewImage;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewContent = itemView.findViewById(R.id.tvReviewContent);
            tvReviewRating = itemView.findViewById(R.id.tvReviewRating);
            ivReviewImage = itemView.findViewById(R.id.ivReviewImage);
        }
    }
}
