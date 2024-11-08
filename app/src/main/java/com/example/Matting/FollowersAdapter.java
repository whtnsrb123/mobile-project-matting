package com.example.Matting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowerViewHolder> {

    private List<Follower> followers;

    // 생성자 수정: Follower 객체 리스트를 받음
    public FollowersAdapter(List<Follower> followers) {
        this.followers = followers;
    }

    @Override
    public FollowerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 아이템 레이아웃을 inflating (팔로워 항목)
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);
        return new FollowerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FollowerViewHolder holder, int position) {
        // 팔로워 정보 바인딩
        Follower follower = followers.get(position);
        holder.usernameTextView.setText(follower.getUsername());
        // 이미지 URL을 사용할 경우 로딩 코드 추가 (예: Picasso 또는 Glide 사용)
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    // 리스트 업데이트 메서드
    public void updateList(List<Follower> newList) {
        followers = newList;
        notifyDataSetChanged(); // 데이터 변경을 알리고 UI를 갱신
    }

    public static class FollowerViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        ImageView profileImageView;

        public FollowerViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
