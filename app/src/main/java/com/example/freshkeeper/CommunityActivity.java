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
                    Intent data = result.getData();
                    String title = data.getStringExtra("title");
                    String content = data.getStringExtra("content");
                    String imageUri = data.getStringExtra("firstImageUri");
                    String userId = data.getStringExtra("userId");

                    // Handle null or empty image URIs
                    List<String> imageUris = (imageUri != null && !imageUri.isEmpty())
                            ? Collections.singletonList(imageUri)
                            : new ArrayList<>(); // 기본값으로 빈 리스트 처리

                    // Fetch logged-in user's name and icon from SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    String loggedInUserName = sharedPreferences.getString("username", "고릴라"); // 기본값: "고릴라"
                    String loggedInUserIcon = sharedPreferences.getString("userIcon", "fk_mmm");

                    // Create a new post with the data
                    CommunityPost newPost = new CommunityPost(
                            title != null && !title.isEmpty() ? title : "제목 없음",
                            content != null && !content.isEmpty() ? content : "내용 없음",
                            imageUris,
                            userId != null && !userId.isEmpty() ? userId : "default_user_id",
                            0, // likeCount
                            0, // commentCount
                            loggedInUserName, // 사용자 이름
                            loggedInUserIcon // 사용자 아이콘
                    );

                    long postId = addPostToDatabase(newPost);
                    if (postId != -1) {
                        addPostNotification(newPost.getTitle(), String.valueOf(postId)); // 알림 추가
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        Log.d("CommunityActivity", "CommunityActivity 실행됨");

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize the RecyclerView for community posts
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = loadPosts();
        communityAdapter = new CommunityAdapter(this, postList);
        recyclerView.setAdapter(communityAdapter);

        // Set the item click listener
        communityAdapter.setOnItemClickListener(selectedPost -> {
            Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);
            intent.putExtra("postId", selectedPost.getId());
            intent.putExtra("position", postList.indexOf(selectedPost));
            intent.putExtra("postTitle", selectedPost.getTitle());
            intent.putExtra("postContent", selectedPost.getContent());
            intent.putExtra("postAuthor", selectedPost.getAuthorName());
            intent.putExtra("postImage", selectedPost.getFirstImageUri());
            startActivityForResult(intent, REQUEST_CODE_COMMENT);
        });

        // Set up the Plus button and My Posts button
        setupPlusButton();

        // Set up search functionality
        setupSearchFunctionality();

        // Set up bottom navigation bar icons
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
        } catch (Exception e) {
            Log.e("CommunityActivity", "게시글 로드 중 오류 발생: " + e.getMessage(), e);
            posts = new ArrayList<>();
        }
        if (posts == null || posts.isEmpty()) {
            Log.d("CommunityActivity", "게시글 데이터가 없습니다.");
            posts = new ArrayList<>();
        }
        return posts;
    }

    private long addPostToDatabase(CommunityPost newPost) {
        if (newPost.getImageUris() == null) {
            newPost.setImageUris(new ArrayList<>());
        }
        if (!dbHelper.isPostExists(newPost.getTitle())) {
            long postId = dbHelper.addCommunityPost(newPost);
            if (postId != -1) {
                newPost.setId((int) postId);
                postList.add(0, newPost);
                communityAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
            } else {
                Toast.makeText(this, "게시물 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            return postId; // 새로 생성된 게시물의 ID 반환
        } else {
            Toast.makeText(this, "이미 등록된 글입니다.", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    // Plus 버튼 초기화 후 OnClickListener 설정
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
                // 버튼을 클릭했을 때 showPopupMenu 메소드 호출, view를 전달
                showPopupMenu(view);
            });
        }
    }


    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_community, popupMenu.getMenu());

        Log.d("CommunityActivity", "팝업 메뉴 생성됨"); // 팝업 메뉴가 생성되었는지 확인

        popupMenu.setOnMenuItemClickListener(item -> {
            Log.d("CommunityActivity", "팝업 메뉴 항목 클릭됨");
            int itemId = item.getItemId();

            if (itemId == R.id.menu_write_post) {
                Log.d("CommunityActivity", "글쓰기 메뉴 선택됨");
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_my_posts) {
                Log.d("CommunityActivity", "내가 쓴 글 보기 메뉴 선택됨");
                Intent myPostsIntent = new Intent(CommunityActivity.this, UserPostsActivity.class);
                startActivity(myPostsIntent);
                return true;
            } else {
                Log.d("CommunityActivity", "알 수 없는 메뉴 항목 선택됨: " + item.getTitle());
            }
            return false;
        });

        Log.d("CommunityActivity", "팝업 메뉴 보여주기 전");
        popupMenu.show();
        Log.d("CommunityActivity", "팝업 메뉴 보여준 후");
    }







    private void setupSearchFunctionality() {
        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    communityAdapter.updateList(postList);
                } else {
                    filterPosts(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

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

    @Override
    protected void setupFooterNavigation() {
        ImageView iconRef = findViewById(R.id.icon_ref);
        ImageView iconCalendar = findViewById(R.id.icon_calendar);
        ImageView iconBarcode = findViewById(R.id.icon_barcode);
        ImageView iconCommunity = findViewById(R.id.icon_community);
        ImageView iconMypage = findViewById(R.id.icon_mypage);

        iconRef.setOnClickListener(view -> navigateTo(FkmainActivity.class));
        iconCalendar.setOnClickListener(view -> navigateTo(CalendarActivity.class));
        iconBarcode.setOnClickListener(view -> navigateTo(BarcodeScanActivity.class));
        iconCommunity.setOnClickListener(view -> {}); // Stay on the same page
        iconMypage.setOnClickListener(view -> navigateTo(MypageActivity.class));
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(CommunityActivity.this, activityClass);
        startActivity(intent);
        finish();
    }
}
