package com.example.Matting;

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

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Context context;
    private List<Main> mainList;

    public MainAdapter(Context context, List<Main> mainList) {
        this.context = context;
        this.mainList = mainList;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main_info_recycler_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Main main = mainList.get(position);
        holder.tvTitle.setText(main.getTitle());
        holder.tvCategory.setText(main.getCategory());
        holder.tvDescription.setText(main.getDescription());
        holder.tvRating.setText(String.valueOf(main.getRating()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // 팝업창 표시
            Toast.makeText(context, "토스트 메시지입니다.",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvDescription, tvRating;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
