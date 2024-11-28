package com.example.Matting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Chat_ChatRoomAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> chatRooms;

    public Chat_ChatRoomAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
        this.context = context;
        this.chatRooms = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_room_item, parent, false);
        }

        String chatRoomName = chatRooms.get(position);

        TextView textViewChatRoomName = convertView.findViewById(R.id.chatRoomName);
        textViewChatRoomName.setText(chatRoomName);
        textViewChatRoomName.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat_ChatroomActivity.class); // 여기서 context를 사용
            intent.putExtra("chatRoomId", chatRoomName);
            context.startActivity(intent); // 여기서 context를 사용
        });

        ImageButton btnDeleteChat = convertView.findViewById(R.id.btn_delete_chat);
        btnDeleteChat.setOnClickListener(v -> {
            // 삭제 버튼 클릭 시 동작 정의
            chatRooms.remove(position);
            notifyDataSetChanged();
            // Firebase에서 해당 채팅방 삭제 (필요 시)
            User user = new User(context);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUserId()).child("chats");

            // 특정 문자열을 가지는 데이터를 삭제
            String targetString = chatRoomName;

            dbRef.orderByValue().equalTo(targetString).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // 해당 데이터 삭제
                        snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 에러 처리
                }
            });
        });

        return convertView;
    }
}
