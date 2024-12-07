package com.example.Matting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {

    private Context context;
    private List<Community> communityList;

    private String base64Image = null;
    private String address;

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
        holder.tvInfo.setText(community.getLocation());
        holder.tvRestaurant.setText(community.getRestaurant());
        address = community.getAddress();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("review")
                .whereEqualTo("address", address) // 특정 주소와 일치하는 리뷰 검색
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) { // 결과가 있을 때
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                base64Image = document.getString("imageResource");

                                if (base64Image != null && !base64Image.isEmpty()) { // 유효한 이미지 확인
                                    // Base64 문자열을 디코딩
                                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                    // UI 업데이트
                                    holder.ivCommunity.setImageBitmap(decodedByte);
                                    return; // 첫 번째 유효한 이미지를 찾으면 중단
                                }
                            }
                            // 유효한 이미지가 없는 경우 기본 이미지 설정
                        } else {
                            Log.w("ReviewImage", "일치하는 리뷰가 없습니다.");
                        }
                    } else {
                        Log.e("FirestoreError", "리뷰 이미지를 가져오는 데 실패했습니다.", task.getException());
                    }
                });


        // 항목 클릭 시 documentId 전달
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Community_DetailActivity.class);
            intent.putExtra("documentId", community.getDocumentId()); // documentId 전달
            context.startActivity(intent);
            Log.d("getDocumentId", "CommunityAdapter: "+community.getDocumentId());
        });
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public static class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvInfo, tvRestaurant;
        ImageView ivCommunity;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            ivCommunity = itemView.findViewById(R.id.ivCommunity);
        }
    }
}
