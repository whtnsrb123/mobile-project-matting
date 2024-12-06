package com.example.Matting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {

    private final List<Follower> followers;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Follower follower);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public FollowersAdapter(List<Follower> followers) {
        this.followers = followers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Follower follower = followers.get(position);

        holder.username.setText(follower.getUsername());
        holder.subText.setText(follower.getSubText());

        // 프로필 이미지 로드
        if (follower.getProfileImage() != null && !follower.getProfileImage().isEmpty()) {
            Glide.with(holder.profileImage.getContext())
                    .load(follower.getProfileImage())
                    .circleCrop()
                    .into(holder.profileImage);
        } else {
            // 기본 이미지 설정
            holder.profileImage.setImageResource(R.drawable.user);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(follower);
            }
        });

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(follower);
            }
        });
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView username, subText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.username);
            subText = itemView.findViewById(R.id.subText);
        }
    }
}
