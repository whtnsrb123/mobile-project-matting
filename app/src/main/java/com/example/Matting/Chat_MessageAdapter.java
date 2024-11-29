package com.example.Matting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Chat_MessageAdapter extends ArrayAdapter<Chat_Message> {
    private String currentUserId; // 현재 사용자 ID

    public Chat_MessageAdapter(@NonNull Context context, @NonNull List<Chat_Message> objects, String currentUserId) {
        super(context, 0, objects);
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Chat_Message chatMessage = getItem(position);

        if (chatMessage != null) {
            if (chatMessage.getUserId().equals(currentUserId)) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_item_right, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_item_left, parent, false);
            }

            TextView textViewUserId = convertView.findViewById(R.id.userId);
            TextView textViewMessage = convertView.findViewById(R.id.textViewMessage);
            ImageView imageViewProfile = convertView.findViewById(R.id.profileimage);

            if (imageViewProfile == null) {
                throw new NullPointerException("ImageView profileimage is null. Ensure the ID is correct.");
            }

            chatMessage.getUserNickname(new Chat_Message.NicknameCallback() {
                @Override
                public void onCallback(String nickName) {
                    textViewUserId.setText(nickName);
                }
            });

            // Firebase Realtime Database에서 Base64 인코딩된 프로필 이미지 가져오기
            DatabaseReference profileImageRef = FirebaseDatabase.getInstance().getReference("users").child(chatMessage.getUserId()).child("profileImage");
            profileImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String profileImageBase64 = snapshot.getValue(String.class);
                    if (profileImageBase64 != null) {
                        byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageViewProfile.setImageBitmap(decodedByte);
                    } else {
                        imageViewProfile.setImageResource(R.drawable.default_profile_image); // 기본 이미지 설정
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 에러 처리
                }
            });

            textViewMessage.setText(chatMessage.getMessage());
        }

        return convertView;
    }
}
