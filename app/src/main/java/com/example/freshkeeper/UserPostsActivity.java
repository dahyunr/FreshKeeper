package com.example.freshkeeper;

// 필요한 클래스들 import
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;


import com.example.freshkeeper.database.DatabaseHelper;
import com.example.freshkeeper.CommunityAdapter;  // CommunityAdapter를 import합니다.

import java.util.List;

public class UserPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUserPosts;
    private CommunityAdapter adapter;  // CommunityPostAdapter 대신 CommunityAdapter 사용
    private List<CommunityPost> userPostsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);
        Log.d("UserPostsActivity", "UserPostsActivity 실행됨");

        // RecyclerView 설정
        recyclerViewUserPosts = findViewById(R.id.recyclerViewUserPosts);
        recyclerViewUserPosts.setLayoutManager(new LinearLayoutManager(this));

        // 내가 쓴 글 가져오기
        userPostsList = getUserPosts();

        // 어댑터 설정
        adapter = new CommunityAdapter(this, userPostsList);
        recyclerViewUserPosts.setAdapter(adapter);
    }


    // 내가 쓴 글 목록을 가져오는 메서드
    private List<CommunityPost> getUserPosts() {
        // DatabaseHelper 사용하여 사용자가 쓴 게시글 가져오기
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        return dbHelper.getUserPosts(); // 예시 메서드 호출
    }
}
