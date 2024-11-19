package com.example.Matting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {

    private Context context;
    private List<Community> communityList;

    public CommunityAdapter(Context context, List<Community> communityList) {
        this.context = context;
        this.communityList = communityList;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_comunity_recycler_item, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Community community = communityList.get(position);
        holder.tvTitle.setText(community.getTitle());
        holder.tvContent.setText(community.getContent());
        holder.tvInfo.setText(community.getInfo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title", community.getTitle());
                intent.putExtra("content", community.getContent());
                intent.putExtra("restaurant", community.getRestaurant());
                intent.putExtra("info", community.getInfo());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public static class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvInfo;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvInfo = itemView.findViewById(R.id.tvInfo);
        }
    }
}
