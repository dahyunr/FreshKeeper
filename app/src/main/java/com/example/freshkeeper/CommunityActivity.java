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

    // ActivityResultLauncher for WritePostActivity (게시글 작성 후 결과를 받아 처리)
    private final ActivityResultLauncher<Intent> writePostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // 새 게시글 작성 후 데이터 처리
                    Log.d("CommunityActivity", "게시글 작성 완료 후 결과 처리");
                    loadCommunityPosts(); // 게시글을 새로 로드하여 즉시 반영
                }
            }
    );

    // ActivityResultLauncher for CommentActivity (글 삭제 후 결과를 받아 처리)
    private final ActivityResultLauncher<Intent> commentActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean postDeleted = result.getData().getBooleanExtra("postDeleted", false);
                    int deletedPostId = result.getData().getIntExtra("deletedPostId", -1);

                    if (postDeleted) {
                        // 삭제된 게시글을 리스트에서 제거
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == deletedPostId) {
                                postList.remove(i);
                                communityAdapter.notifyItemRemoved(i);
                                Log.d("CommunityActivity", "게시글 삭제됨: " + deletedPostId);
                                break;
                            }
                        }
                    }
                }
            }
    );

    @Override
    protected void setupFooterNavigation() {
        findViewById(R.id.icon_ref).setOnClickListener(view -> navigateTo(FkmainActivity.class));
        findViewById(R.id.icon_calendar).setOnClickListener(view -> navigateTo(CalendarActivity.class));
        findViewById(R.id.icon_barcode).setOnClickListener(view -> navigateTo(BarcodeScanActivity.class));
        findViewById(R.id.icon_community).setOnClickListener(null); // 현재 페이지 유지
        findViewById(R.id.icon_mypage).setOnClickListener(view -> navigateTo(MypageActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCommunityPosts(); // 삭제된 게시글 반영을 위해 리스트 새로 로드
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        dbHelper = DatabaseHelper.getInstance(this);

        setupRecyclerView();
        loadCommunityPosts(); // 초기 커뮤니티 게시글을 로드
        setupPlusButton();
        setupSearchFunctionality();
        setupFooterNavigation();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        communityAdapter = new CommunityAdapter(this, postList);
        recyclerView.setAdapter(communityAdapter);

        // 게시글 클릭 시 댓글 페이지로 이동하는 리스너 설정
        communityAdapter.setOnItemClickListener(post -> {
            Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("postTitle", post.getTitle());
            intent.putExtra("postContent", post.getContent());
            intent.putExtra("postAuthor", post.getAuthorName());
            intent.putExtra("postImageUri", post.getFirstImageUri());
            commentActivityLauncher.launch(intent); // CommentActivity 실행
        });
    }

    private void setupPlusButton() {
        ImageView plusButton = findViewById(R.id.plus_button);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            plusButton.setEnabled(false);
            plusButton.setVisibility(View.GONE);
        } else {
            plusButton.setOnClickListener(view -> {
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                writePostLauncher.launch(intent); // 게시글 작성 페이지 실행
            });
        }
    }

    private void setupSearchFunctionality() {
        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    communityAdapter.updateData(postList);
                } else {
                    filterPosts(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterPosts(String query) {
        List<CommunityPost> filteredList = dbHelper.searchPosts(query); // 데이터베이스에서 검색
        for (CommunityPost post : postList) {
            if (post.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    post.getContent().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(post);
            }
        }
        communityAdapter.updateData(filteredList); // 검색 결과로 RecyclerView 업데이트
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(CommunityActivity.this, activityClass);
        startActivity(intent);
        finish();
    }

    private void loadCommunityPosts() {
        List<CommunityPost> loadedPosts = dbHelper.getAllCommunityPosts();
        if (loadedPosts != null && !loadedPosts.isEmpty()) {
            postList.clear();
            postList.addAll(loadedPosts);
            communityAdapter.notifyDataSetChanged();
        } else {
            Log.d("CommunityActivity", "게시글이 없습니다.");
        }
    }
}