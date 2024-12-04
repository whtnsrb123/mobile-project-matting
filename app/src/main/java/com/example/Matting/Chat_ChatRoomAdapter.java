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
    private List<String> chatRoomNames; // 채팅방 이름
    private List<String> chatRoomIds;   // 채팅방 ID

    public Chat_ChatRoomAdapter(@NonNull Context context, @NonNull List<String> names, @NonNull List<String> ids) {
        super(context, 0, names);
        this.context = context;
        this.chatRoomNames = names;
        this.chatRoomIds = ids;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_room_item, parent, false);
        }

        // 현재 위치의 채팅방 이름과 ID 가져오기
        String chatRoomName = chatRoomNames.get(position);
        String chatRoomId = chatRoomIds.get(position);

        // 채팅방 이름 설정
        TextView textViewChatRoomName = convertView.findViewById(R.id.chatRoomName);
        textViewChatRoomName.setText(chatRoomName);

        // 채팅방 클릭 이벤트
        textViewChatRoomName.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat_ChatroomActivity.class);
            intent.putExtra("chatRoomId", chatRoomId); // chatRoomId 전달
            context.startActivity(intent);
        });

        // 삭제 버튼
        ImageButton btnDeleteChat = convertView.findViewById(R.id.btn_delete_chat);
        btnDeleteChat.setOnClickListener(v -> {
            // 채팅방 삭제 로직
            chatRoomNames.remove(position);
            chatRoomIds.remove(position);
            notifyDataSetChanged();

            // Firebase에서 데이터 삭제
            User user = new User(context);

            // 채팅방 유저 목록에서 삭제
            DatabaseReference chatdb = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(chatRoomId)
                    .child("users");
            chatdb.orderByValue().equalTo(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 에러 처리
                }
            });

            // 유저의 채팅 리스트에서 삭제
            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUserId())
                    .child("chats");

            dbRef.orderByKey().equalTo(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
