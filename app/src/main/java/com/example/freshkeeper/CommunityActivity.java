package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshkeeper.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunityActivity extends BaseActivity {
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
                            : new ArrayList<>();

                    // Create a new CommunityPost instance
                    CommunityPost newPost = new CommunityPost(
                            title,
                            content,
                            imageUris,
                            userId,
                            0, // Initial like count
                            0, // Initial comment count
                            "익명 사용자", // Default author name
                            "fk_mmm" // Default author icon
                    );

                    // Add the new post to the database and list
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

        // Fetch all posts from the database
        postList = dbHelper.getAllCommunityPosts();

        // Initialize the adapter and set it to the RecyclerView
        communityAdapter = new CommunityAdapter(this, postList);
        recyclerView.setAdapter(communityAdapter);

        // Set the item click listener
        communityAdapter.setOnItemClickListener(selectedPost -> {
            Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);

            // Pass the post data to CommentActivity
            intent.putExtra("postId", selectedPost.getId()); // 게시글 ID 전달
            intent.putExtra("postTitle", selectedPost.getTitle()); // 게시글 제목 전달
            intent.putExtra("postContent", selectedPost.getContent()); // 게시글 내용 전달
            intent.putExtra("postAuthor", selectedPost.getAuthorName()); // 작성자 이름 전달
            intent.putExtra("postImage", selectedPost.getFirstImageUri()); // 첫 번째 이미지 URI 전달

            startActivity(intent);
        });

        // Set up the Plus button with a popup menu
        ImageView plusButton = findViewById(R.id.plus_button);

        // SharedPreferences를 사용하여 로그인 여부를 확인
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // 로그인하지 않은 사용자인 경우 글쓰기 버튼 비활성화
        if (!isLoggedIn) {
            plusButton.setEnabled(false);
            plusButton.setVisibility(View.GONE); // 버튼을 숨김 처리
        } else {
            plusButton.setOnClickListener(this::showPopupMenu);
        }

        // Set up search functionality
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

        // 하단바 아이콘 클릭 이벤트 설정
        ImageView iconRef = findViewById(R.id.icon_ref);
        ImageView iconCalendar = findViewById(R.id.icon_calendar);
        ImageView iconBarcode = findViewById(R.id.icon_barcode);
        ImageView iconCommunity = findViewById(R.id.icon_community);
        ImageView iconMypage = findViewById(R.id.icon_mypage);

        // 냉장고 아이콘 클릭 시 냉장고 페이지로 이동
        iconRef.setOnClickListener(view -> {
            Intent intent = new Intent(CommunityActivity.this, FkmainActivity.class);
            startActivity(intent);
            finish();
        });

        // 캘린더 아이콘 클릭 시 캘린더 페이지로 이동
        iconCalendar.setOnClickListener(view -> {
            Intent intent = new Intent(CommunityActivity.this, CalendarActivity.class);
            startActivity(intent);
            finish();
        });

        // 바코드 아이콘 클릭 시 바코드 페이지로 이동
        iconBarcode.setOnClickListener(view -> {
            Intent intent = new Intent(CommunityActivity.this, BarcodeScanActivity.class);
            startActivity(intent);
            finish();
        });

        // 커뮤니티 아이콘 클릭 시 현재 페이지로 이동
        iconCommunity.setOnClickListener(view -> {
            // 이미 커뮤니티 페이지이므로 추가 작업 필요 없음
        });

        // 마이페이지 아이콘 클릭 시 마이페이지로 이동
        iconMypage.setOnClickListener(view -> {
            Intent intent = new Intent(CommunityActivity.this, MypageActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Method to display a popup menu when the Plus button is clicked
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_community, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_write_post) {
                // Launch the WritePostActivity
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                writePostLauncher.launch(intent);
                return true;
            } else if (itemId == R.id.menu_my_posts) {
                // Launch the MyPostsActivity
                Intent myPostsIntent = new Intent(CommunityActivity.this, MyPostsActivity.class);
                startActivity(myPostsIntent);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    // Method to filter posts based on search input
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

    // Method to add the post to the database and handle duplicates
    private void addPostToDatabase(CommunityPost newPost) {
        // Check if the post already exists by title
        boolean isPostExists = dbHelper.isPostExists(newPost.getTitle());

        if (!isPostExists) {
            // Add the new post to the database
            dbHelper.addCommunityPost(newPost);

            // Add the new post to the post list
            postList.add(0, newPost);

            // Notify the adapter that a new post has been inserted at the top
            communityAdapter.notifyItemInserted(0);

            // Scroll to the top of the RecyclerView to show the new post
            recyclerView.scrollToPosition(0);
        } else {
            // Show a message that the post already exists (Optional)
            Toast.makeText(this, "이미 등록된 글입니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
