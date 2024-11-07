package com.example.Matting;

import java.util.ArrayList;
import java.util.List;

public class PostData {

    // 게시글 리스트 반환 메서드
    public static List<Post> getPostList() {
        List<Post> postList = new ArrayList<>();

        // 예시 게시글 데이터 추가 (작성자 이름, 설명, 이미지 리소스, 게시 날짜)
        postList.add(new Post("khkgogo", "맛있는 디저트를 먹었어요!", R.drawable.chick, "1시간 전"));
        postList.add(new Post("khkgogo", "즐거운 하루!", R.drawable.choi, "2시간 전"));
        postList.add(new Post("khkgogo", "멋진 풍경을 담았습니다.", R.drawable.dongpa, "어제"));
        postList.add(new Post("khkgogo", "여행 중 만난 특별한 장소!", R.drawable.mosu, "3일 전"));

        return postList;
    }
}
