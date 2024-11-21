package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshkeeper.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.util.Arrays;


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

    // ActivityResultLauncher for WritePostActivity
    private final ActivityResultLauncher<Intent> writePostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    long postId = result.getData().getLongExtra("postId", -1);
                    if (postId != -1) {
                        // 데이터베이스에서 해당 게시글 조회
                        CommunityPost newPost = dbHelper.getCommunityPostById(postId);
                        if (newPost != null) {
                            postList.add(0, newPost); // 리스트 맨 앞에 추가
                            communityAdapter.notifyItemInserted(0); // RecyclerView 갱신
                            recyclerView.scrollToPosition(0); // 첫 번째 항목으로 스크롤
                        }
                    }
                }
            }
    );



    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void addPostNotification(String postTitle, String postId) {
        String title = "새로운 게시물";
        String content = "\"" + postTitle + "\" 게시물이 추가되었습니다.";
        String time = getCurrentTime(); // 현재 시간을 가져오는 함수
        String type = "new_post";

        NotificationItem newNotification = new NotificationItem(title, content, time, type, postId);

        // 알림을 리스트에 추가하여 UI 업데이트
        notificationList.add(0, newNotification);
        notificationAdapter.notifyItemInserted(0);
    }

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

        // DatabaseHelper 인스턴스 초기화
        dbHelper = DatabaseHelper.getInstance(this);

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>(); // 빈 리스트로 초기화
        communityAdapter = new CommunityAdapter(this, postList);
        recyclerView.setAdapter(communityAdapter);

        // 게시글 데이터 로드
        loadCommunityPosts();

        // 검색 기능 설정
        setupSearchFunctionality();

        // 플러스 버튼 설정
        setupPlusButton();

        // Footer Navigation 설정
        setupFooterNavigation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_COMMENT && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            int newCommentCount = data.getIntExtra("newCommentCount", 0);
            if (position != -1 && position < postList.size()) {
                postList.get(position).setCommentCount(newCommentCount);
                communityAdapter.notifyItemChanged(position);

                // Update comment count in the database
                dbHelper.updateCommentCount(postList.get(position).getId(), newCommentCount);
            }
        }
    }

    private List<CommunityPost> loadPosts() {
        List<CommunityPost> posts;
        try {
            posts = dbHelper.getAllCommunityPosts();
            if (posts == null || posts.isEmpty()) {
                Log.d("CommunityActivity", "로드된 게시글 없음");
                posts = new ArrayList<>();
            } else {
                Log.d("CommunityActivity", "데이터 로드 성공: 총 게시글 수 " + posts.size()); // 여기 추가
            }
        } catch (Exception e) {
            Log.e("CommunityActivity", "게시글 로드 중 오류 발생: " + e.getMessage());
            posts = new ArrayList<>(); // 기본값 반환
        }
        return posts;
    }

    private long addPostToDatabase(CommunityPost newPost) {
        if (newPost.getImageUris() == null) {
            newPost.setImageUris(new ArrayList<>());
        }

        long postId = dbHelper.addCommunityPost(newPost);
        if (postId != -1) {
            newPost.setId((int) postId);
            postList.add(0, newPost);
            communityAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            Log.d("CommunityActivity", "게시글 추가 성공: " + newPost.getTitle());
        } else {
            Log.e("CommunityActivity", "게시글 추가 실패: 데이터베이스 오류");
            Toast.makeText(this, "게시글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
        return postId;
    }

    private void setupPlusButton() {
        ImageView plusButton = findViewById(R.id.plus_button);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // 로그인 상태에 따라 버튼 활성화 여부 결정
        if (!isLoggedIn) {
            plusButton.setEnabled(false);
            plusButton.setVisibility(View.GONE);
        } else {
            plusButton.setOnClickListener(view -> {
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                startActivity(intent);
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

    public void onClickPlusButton(View view) {
        Log.d("CommunityActivity", "글쓰기 버튼 클릭됨");
        Intent intent = new Intent(this, WritePostActivity.class);
        writePostLauncher.launch(intent);
    }


    private void loadCommunityPosts() {
        List<CommunityPost> loadedPosts = dbHelper.getAllCommunityPosts();
        if (loadedPosts != null && !loadedPosts.isEmpty()) {
            postList.clear(); // 기존 데이터 초기화
            postList.addAll(loadedPosts); // 새 데이터 추가
            communityAdapter.notifyDataSetChanged();
            Log.d("CommunityActivity", "게시글 로드 성공: " + postList.size());
        } else {
            Log.d("CommunityActivity", "게시글이 없습니다.");
        }
    }

}
