package com.example.Matting;

import java.util.ArrayList;
import java.util.List;

public class PostData {

    // 게시글 리스트 반환 메서드
    public static List<Post> getPostList() {
        List<Post> postList = new ArrayList<>();

        // 예시 게시글 데이터 추가
        postList.add(new Post("게시글 설명 1", R.drawable.chick));
        postList.add(new Post("게시글 설명 2", R.drawable.choi));
        postList.add(new Post("게시글 설명 3", R.drawable.dongpa));
        postList.add(new Post("게시글 설명 4", R.drawable.mosu));

        return postList;
    }
}
