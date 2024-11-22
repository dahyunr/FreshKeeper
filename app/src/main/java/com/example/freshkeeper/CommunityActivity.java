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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommunityActivity extends BaseActivity {
    private static final int REQUEST_CODE_COMMENT = 1001;
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<CommunityPost> postList;
    private DatabaseHelper dbHelper;
    private EditText searchEditText;

    // 알림 리스트 관련
    private List<NotificationItem> notificationList = new ArrayList<>();
    private NotificationAdapter notificationAdapter;

    // ActivityResultLauncher for WritePostActivity (게시글 작성 후 결과를 받아 처리)
    private final ActivityResultLauncher<Intent> writePostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // 새 게시글 작성 후 데이터 처리 (여기서 바로 최신 데이터를 불러와도 됩니다.)
                    Log.d("CommunityActivity", "게시글 작성 완료 후 결과 처리");
                    loadCommunityPosts(); // 게시글을 새로 로드하여 즉시 반영
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        dbHelper = DatabaseHelper.getInstance(this);

        // 데이터 변경 리스너 설정
        dbHelper.setOnDatabaseChangeListener(new DatabaseHelper.OnDatabaseChangeListener() {
            @Override
            public void onPostAdded() {
                // 새 게시글이 추가될 때 실시간으로 리스트 갱신
                runOnUiThread(() -> {
                    CommunityPost latestPost = dbHelper.getLatestCommunityPost();
                    if (latestPost != null && !postList.contains(latestPost)) {
                        postList.add(0, latestPost);
                        communityAdapter.notifyItemInserted(0);
                        recyclerView.scrollToPosition(0); // 첫 번째 항목으로 스크롤
                        Log.d("CommunityActivity", "게시글 추가됨: " + latestPost.getTitle());
                    }
                });
            }

            @Override
            public void onCommentAdded() {
                // 댓글이 추가되었을 때의 동작을 여기에 정의하세요.
                runOnUiThread(() -> {
                    Log.d("CommunityActivity", "댓글이 추가되었습니다.");
                    // 필요하다면 댓글 관련 UI 갱신 로직을 여기에 추가하세요.
                });
            }
        });

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
                writePostLauncher.launch(intent);
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
        List<CommunityPost> filteredList = new ArrayList<>();
        for (CommunityPost post : postList) {
            if (post.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    post.getContent().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(post);
            }
        }
        communityAdapter.updateData(filteredList);
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

            // 게시글 클릭 시 댓글 페이지로 이동하는 리스너 설정
            communityAdapter.setOnItemClickListener(post -> {
                Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);
                intent.putExtra("postId", post.getId());
                intent.putExtra("postTitle", post.getTitle());
                intent.putExtra("postContent", post.getContent());
                intent.putExtra("postAuthor", post.getAuthorName());
                intent.putExtra("postImage", post.getFirstImageUri());
                startActivity(intent);
            });
        } else {
            Log.d("CommunityActivity", "게시글이 없습니다.");
        }
    }
}
