package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshkeeper.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<CommunityPost> postList;
    private DatabaseHelper dbHelper;
    private EditText searchEditText;

    // 글 작성 ActivityResultLauncher
    private final ActivityResultLauncher<Intent> writePostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handlePostResult(result.getData(), true); // 새 글 작성 처리
                }
            }
    );

    // 글 수정 ActivityResultLauncher
    private final ActivityResultLauncher<Intent> editPostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handlePostResult(result.getData(), false); // 글 수정 처리
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        dbHelper = new DatabaseHelper(this);

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 게시글 로드
        postList = loadPosts();
        communityAdapter = new CommunityAdapter(this, postList);
        recyclerView.setAdapter(communityAdapter);

        // 게시글 클릭 리스너 설정
        communityAdapter.setOnItemClickListener(selectedPost -> {
            Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);
            intent.putExtra("postId", selectedPost.getId());
            intent.putExtra("postTitle", selectedPost.getTitle());
            intent.putExtra("postContent", selectedPost.getContent());
            intent.putExtra("postAuthor", selectedPost.getAuthorName());
            intent.putExtra("postImage", selectedPost.getFirstImageUri());
            startActivity(intent);
        });

        // 추가 버튼 및 검색 기능 설정
        setupPlusButton();
        setupSearchFunctionality();
        setupFooterNavigation();
    }

    /**
     * 게시글 로드
     */
    private List<CommunityPost> loadPosts() {
        try {
            return dbHelper.getAllCommunityPosts();
        } catch (Exception e) {
            Log.e("CommunityActivity", "게시글 로드 중 오류 발생: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 글 작성/수정 결과 처리
     */
    private void handlePostResult(Intent data, boolean isNewPost) {
        String title = data.getStringExtra("title");
        String content = data.getStringExtra("content");
        ArrayList<String> imageUris = data.getStringArrayListExtra("imageUris");
        int postId = data.getIntExtra("postId", -1);

        if (isNewPost) {
            // 새 글 작성
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String loggedInUserName = sharedPreferences.getString("username", "익명 사용자");
            String loggedInUserIcon = sharedPreferences.getString("userIcon", "fk_mmm");

            CommunityPost newPost = new CommunityPost(
                    title != null && !title.isEmpty() ? title : "제목 없음",
                    content != null && !content.isEmpty() ? content : "내용 없음",
                    imageUris != null ? imageUris : new ArrayList<>(),
                    "default_user_id",
                    0,
                    0,
                    loggedInUserName,
                    loggedInUserIcon
            );

            if (!dbHelper.isPostExists(newPost.getTitle())) {
                dbHelper.addCommunityPost(newPost);
                postList.add(0, newPost);
                communityAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
            } else {
                Toast.makeText(this, "이미 등록된 글입니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 글 수정
            dbHelper.updatePost(postId, title, content, imageUris);
            for (CommunityPost post : postList) {
                if (post.getId() == postId) {
                    post.setTitle(title);
                    post.setContent(content);
                    post.setImageUris(imageUris);
                    communityAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    /**
     * 댓글 화면 열기
     */
    private void openCommentActivity(CommunityPost selectedPost) {
        Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);

        intent.putExtra("postId", selectedPost.getId());
        intent.putExtra("postTitle", selectedPost.getTitle());
        intent.putExtra("postContent", selectedPost.getContent());
        intent.putExtra("postAuthor", selectedPost.getAuthorName());
        intent.putExtra("postImage", selectedPost.getFirstImageUri());

        startActivity(intent);
    }

    /**
     * 글 작성 버튼 설정
     */
    private void setupPlusButton() {
        ImageView plusButton = findViewById(R.id.plus_button);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            plusButton.setEnabled(false);
            plusButton.setVisibility(View.GONE);
        } else {
            plusButton.setOnClickListener(v -> {
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                writePostLauncher.launch(intent);
            });
        }
    }

    /**
     * 검색 기능 설정
     */
    private void setupSearchFunctionality() {
        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * 게시글 필터링
     */
    private void filterPosts(String text) {
        List<CommunityPost> filteredList = new ArrayList<>();
        for (CommunityPost post : postList) {
            if (post.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    post.getContent().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(post);
            }
        }
        communityAdapter.updateList(filteredList);
    }
}
