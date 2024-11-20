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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunityActivity extends BaseActivity {
    private static final int REQUEST_CODE_COMMENT = 1001;
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<CommunityPost> postList;
    private DatabaseHelper dbHelper;
    private EditText searchEditText;

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

                    addPostToDatabase(newPost);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch posts from the database
        postList = loadPosts();

        communityAdapter = new CommunityAdapter(this, postList);
        recyclerView.setAdapter(communityAdapter);

        // Set the item click listener
        communityAdapter.setOnItemClickListener(selectedPost -> {
            Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);

            // Pass the post data to CommentActivity
            intent.putExtra("postId", selectedPost.getId());
            intent.putExtra("position", postList.indexOf(selectedPost));
            intent.putExtra("postTitle", selectedPost.getTitle());
            intent.putExtra("postContent", selectedPost.getContent());
            intent.putExtra("postAuthor", selectedPost.getAuthorName());
            intent.putExtra("postImage", selectedPost.getFirstImageUri());

            startActivityForResult(intent, REQUEST_CODE_COMMENT);
        });

        // Set up the Plus button with a popup menu
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

    private void setupPlusButton() {
        ImageView plusButton = findViewById(R.id.plus_button);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // If the user is not logged in, disable the write post button
        if (!isLoggedIn) {
            plusButton.setEnabled(false);
            plusButton.setVisibility(View.GONE);
        } else {
            plusButton.setOnClickListener(this::showPopupMenu);
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
                    communityAdapter.updateList(postList); // Restore full list if search is cleared
                } else {
                    filterPosts(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_community, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_write_post) {
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                writePostLauncher.launch(intent);
                return true;
            } else if (itemId == R.id.menu_my_posts) {
                Intent myPostsIntent = new Intent(CommunityActivity.this, MyPostsActivity.class);
                startActivity(myPostsIntent);
                return true;
            }
            return false;
        });

        popupMenu.show();
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

    private void addPostToDatabase(CommunityPost newPost) {
        if (newPost.getImageUris() == null) {
            newPost.setImageUris(new ArrayList<>());
        }
        if (!dbHelper.isPostExists(newPost.getTitle())) {
            dbHelper.addCommunityPost(newPost);
            postList.add(0, newPost);
            communityAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
        } else {
            Toast.makeText(this, "이미 등록된 글입니다.", Toast.LENGTH_SHORT).show();
        }
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
