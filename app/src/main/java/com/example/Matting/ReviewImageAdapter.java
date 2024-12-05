package com.example.Matting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ViewHolder> {
    private Context context;
    private List<String> reviewImageBase64List;

    public ReviewImageAdapter(Context context, List<String> reviewImageBase64List) {
        this.context = context;
        this.reviewImageBase64List = reviewImageBase64List;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String base64Image = reviewImageBase64List.get(position);
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.reviewImage.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return reviewImageBase64List.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewImage;

        ViewHolder(View itemView) {
            super(itemView);
            reviewImage = itemView.findViewById(R.id.review_image);
        }
    }
}
