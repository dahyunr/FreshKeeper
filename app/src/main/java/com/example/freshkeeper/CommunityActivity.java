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
import androidx.appcompat.app.AlertDialog;
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

    @Override
    protected void onResume() {
        super.onResume();
        String currentQuery = searchEditText.getText().toString().trim();
        if (currentQuery.isEmpty()) {
            loadCommunityPosts(); // 전체 게시글 로드
        } else {
            List<CommunityPost> filteredPosts = dbHelper.searchPosts(currentQuery);
            communityAdapter.updateData(filteredPosts);
        }
    }


    @Override
    protected void setupFooterNavigation() {
        findViewById(R.id.icon_ref).setOnClickListener(view -> navigateTo(FkmainActivity.class));
        findViewById(R.id.icon_calendar).setOnClickListener(view -> navigateTo(CalendarActivity.class));
        findViewById(R.id.icon_barcode).setOnClickListener(view -> navigateTo(BarcodeScanActivity.class));

        // 커뮤니티 버튼 클릭 이벤트
        findViewById(R.id.icon_community).setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            boolean isGuest = sharedPreferences.getBoolean("isGuest", false);

            if (isLoggedIn && !isGuest) {
                // 회원만 접근 가능
                navigateTo(CommunityActivity.class);
            } else {
                // 비회원 차단
                showLoginRequiredMessage();
            }
        });

        findViewById(R.id.icon_mypage).setOnClickListener(view -> navigateTo(MypageActivity.class));
    }

    private void showLoginRequiredMessage() {
        Toast.makeText(this, "회원만 이용 가능한 서비스입니다. 로그인 후 이용해주세요.", Toast.LENGTH_LONG).show();

        // 선택적으로 다이얼로그 추가
        new AlertDialog.Builder(this)
                .setTitle("로그인 필요")
                .setMessage("커뮤니티 서비스를 이용하려면 로그인이 필요합니다. 로그인 하시겠습니까?")
                .setPositiveButton("로그인", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        communityAdapter = new CommunityAdapter(this, postList);
        recyclerView.setAdapter(communityAdapter);

        communityAdapter.setOnItemClickListener(post -> {
            Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("postTitle", post.getTitle());
            intent.putExtra("postContent", post.getContent());
            intent.putExtra("postAuthor", post.getAuthorName());
            intent.putExtra("postImageUri", post.getFirstImageUri());
            commentActivityLauncher.launch(intent);
        });
    }

    private void setupPlusButton() {
        ImageView plusButton = findViewById(R.id.plus_button);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);

        // 사용자 이름 가져오기 (게스트 여부에 따라)
        String userEmail = sharedPreferences.getString("userEmail", null);
        String userName = "게스트";

        if (isLoggedIn && userEmail != null) {
            userName = dbHelper.getUserNameByEmail(userEmail);
        }

        Log.d("CommunityActivity", "사용자 이름: " + userName);

        if (!isLoggedIn || isGuest) {
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
                    loadCommunityPosts(); // 검색어가 없으면 전체 게시글을 다시 로드
                } else {
                    List<CommunityPost> filteredPosts = dbHelper.searchPosts(s.toString());
                    communityAdapter.updateData(filteredPosts);
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
        } else {
            Log.d("CommunityActivity", "게시글이 없습니다.");
        }
    }
}
