package com.example.Matting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

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
//        holder.tvLink.setText(main.getDescription());
        holder.tvRating.setText(String.valueOf(main.getRating()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoFragment infoFragment = InfoFragment.newInstance(
                        main.getTitle(),
                        main.getCategory(),
                        main.getDescription(),
                        main.getLink(),
                        main.getRating(),
                        main.getMapX(),
                        main.getMapY()
                );

                // Activity를 가져와서 BottomSheet 숨기기
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(activity.findViewById(R.id.bottom_sheet_layout));
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                ((MainActivity) activity).hideBottomSheet();

                // 전체 화면으로 InfoFragment 표시
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.info_fragment_container, infoFragment)
                        .addToBackStack(null)
                        .commit();
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
