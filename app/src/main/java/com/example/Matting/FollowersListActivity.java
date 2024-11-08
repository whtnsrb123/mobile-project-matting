package com.example.Matting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FollowersListActivity extends AppCompatActivity {

    private FollowersAdapter adapter;
    private List<Follower> followersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_list);

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFollowers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 더미 팔로워 데이터 생성
        followersList = new ArrayList<>();
        followersList.add(new Follower("User1", "https://via.placeholder.com/48"));
        followersList.add(new Follower("User2", "https://via.placeholder.com/48"));
        followersList.add(new Follower("User3", "https://via.placeholder.com/48"));
        followersList.add(new Follower("User4", "https://via.placeholder.com/48"));
        followersList.add(new Follower("User5", "https://via.placeholder.com/48"));

        // 어댑터 설정
        adapter = new FollowersAdapter(followersList);
        recyclerView.setAdapter(adapter);

        // 검색 EditText 설정
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 필터링된 목록 업데이트
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // 입력된 검색어를 기준으로 리스트를 필터링하는 메서드
    private void filterList(String query) {
        List<Follower> filteredList = new ArrayList<>();
        for (Follower follower : followersList) {
            if (follower.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(follower);
            }
        }
        // 어댑터에 새로운 필터링된 리스트를 설정
        adapter.updateList(filteredList);
    }
}
