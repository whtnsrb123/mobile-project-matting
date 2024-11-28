package com.example.Matting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Community_DetailRecyclerViewAdapter extends RecyclerView.Adapter<Community_DetailRecyclerViewAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView_item;
        TextView txt_main;
        TextView txt_sub;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
            txt_main = (TextView) itemView.findViewById(R.id.txt_main);
            txt_sub = (TextView) itemView.findViewById(R.id.txt_sub);
        }
    }

    private ArrayList<Community_RecyclerViewItem> mList = null;
    private Context mContext;

    public Community_DetailRecyclerViewAdapter(ArrayList<Community_RecyclerViewItem> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.activity_detail_recycler_item, parent, false);
        Community_DetailRecyclerViewAdapter.ViewHolder vh = new Community_DetailRecyclerViewAdapter.ViewHolder(view);
        return vh;
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull Community_DetailRecyclerViewAdapter.ViewHolder holder, int position) {
        Community_RecyclerViewItem item = mList.get(position);

        holder.imgView_item.setImageResource(R.drawable.food);   // 사진 없어서 기본 파일로 이미지 띄움
        holder.txt_main.setText(item.getMainText());
        holder.txt_sub.setText(item.getSubText());

        // 아이템 클릭 이벤트 설정
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업창 표시
//                setupClickListenerForPopup(item.getMainText(), item.getSubText());
                Intent intent = new Intent(v.getContext(), Community_DetailActivity.class);
                intent.putExtra("documentId", item.getDocumentId()); // Firestore documentId 전달
                Log.d("getDocumentId", item.getDocumentId());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // 팝업창 생성 메서드
    private void setupClickListenerForPopup(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);

        // 확인 버튼 추가
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // 팝업 닫기
            }
        });

        // 팝업 창 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}