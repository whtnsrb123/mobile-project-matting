package com.example.Matting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class Chat_MessageAdapter extends ArrayAdapter<Chat_Message> {
    private int resourceLayout;
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
            // 메시지의 userId에 따라 레이아웃 선택
            if (chatMessage.getUserId().equals(currentUserId)) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_item_right, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_item_left, parent, false);
            }

            TextView textViewUserId = convertView.findViewById(R.id.userId);
            TextView textViewMessage = convertView.findViewById(R.id.textViewMessage);

            // 메시지와 닉네임 설정
            chatMessage.getUserNickname(new Chat_Message.NicknameCallback() {
                @Override
                public void onCallback(String nickName) {
                    textViewUserId.setText(nickName);
                }
            });
            textViewMessage.setText(chatMessage.getMessage());
        }

        return convertView;
    }
}
