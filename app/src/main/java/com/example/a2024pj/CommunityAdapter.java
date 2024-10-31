package com.example.a2024pj;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        holder.tvSubtitle.setText(community.getSubtitle());
        holder.tvInfo.setText(community.getInfo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업창 표시
//                Toast.makeText(context, "토스트 메시지입니다.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title", community.getTitle());
                intent.putExtra("subtitle", community.getSubtitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public static class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvInfo;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvInfo = itemView.findViewById(R.id.tvInfo);
        }
    }
}
