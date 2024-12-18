package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freshkeeper.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommentActivity extends BaseActivity {
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private DatabaseHelper dbHelper;
    private EditText commentInput;
    private ImageView sendButton, backButton, postImage, postAuthorIcon, fkVertIcon;
    private TextView postTitle, postAuthor, postContent;
    private SharedPreferences sharedPreferences;
    private int postId;
    private Set<Integer> likedComments = new HashSet<>(); // 초기화 추가


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // DatabaseHelper 초기화
        dbHelper = DatabaseHelper.getInstance(this);

        // DatabaseHelper에 Listener 연결
        dbHelper.setOnDatabaseChangeListener(new DatabaseHelper.OnDatabaseChangeListener() {
            @Override
            public void onPostAdded() {
                // 게시글 추가 시 동작
            }

            @Override
            public void onCommentAdded() {
                Log.d("CommentActivity", "onCommentAdded 호출됨");

                // UI 스레드에서 RecyclerView 갱신
                runOnUiThread(() -> {
                    commentList.clear();
                    commentList.addAll(dbHelper.getCommentsByPostId(postId));

                    if (commentAdapter != null) {
                        commentAdapter.updateCommentList(commentList);
                    } else {
                        Log.e("CommentActivity", "commentAdapter가 초기화되지 않음");
                    }

                });
            }
        });

        // UI 초기화
        initializeUI();

        // 게시글 및 댓글 데이터 로드
        handlePostData();

        // 뒤로가기 버튼 설정
        backButton.setOnClickListener(view -> finish());

        // fk_vert 아이콘 클릭 리스너 설정
        fkVertIcon.setOnClickListener(v -> showPopupMenu(v));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (postId != -1) {
            loadComments(postId);
        }
    }

    /**
     * UI 초기화 메서드
     */
    private void initializeUI() {
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        postAuthor = findViewById(R.id.postAuthor);
        postAuthorIcon = findViewById(R.id.postAuthorIcon);
        postImage = findViewById(R.id.postImage);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentInput = findViewById(R.id.comment_input);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);
        fkVertIcon = findViewById(R.id.fk_vert_icon);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 게시글 데이터를 처리하고 UI 업데이트
     */
    private void handlePostData() {
        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", -1);
        String title = intent.getStringExtra("postTitle");
        String content = intent.getStringExtra("postContent");
        String authorName = intent.getStringExtra("postAuthor");
        String authorIcon = intent.getStringExtra("postAuthorIcon");
        String imageUri = intent.getStringExtra("postImageUri");

        if (postId == -1) {
            finish();
            return;
        }

        updatePostUI(title, content, authorName, authorIcon, imageUri);
        loadComments(postId);

        // 댓글 작성 버튼 리스너
        sendButton.setOnClickListener(view -> {
            String commentText = commentInput.getText().toString().trim();
            if (!TextUtils.isEmpty(commentText)) {
                // 새로운 댓글 생성
                Comment newComment = new Comment(commentText, -1, postId, 0, "익명", "icon_uri");

                // 댓글 삽입 (실시간 반영은 DatabaseHelper에서 처리됨)
                dbHelper.addComment(newComment);

                // 입력창 초기화
                commentInput.setText("");
            } else {
                Toast.makeText(this, "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 게시글 UI 업데이트 메서드
     */
    private void updatePostUI(String title, String content, String authorName, String authorIcon, String imageUri) {
        postTitle.setText(!TextUtils.isEmpty(title) ? title : "제목 없음");
        postContent.setText(!TextUtils.isEmpty(content) ? content : "내용 없음");
        postAuthor.setText(!TextUtils.isEmpty(authorName) ? authorName : "익명");

        if (!TextUtils.isEmpty(authorIcon)) {
            Glide.with(this).load(authorIcon).placeholder(R.drawable.fk_mmm).into(postAuthorIcon);
        } else {
            postAuthorIcon.setImageResource(R.drawable.fk_mmm);
        }

        if (!TextUtils.isEmpty(imageUri)) {
            postImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUri).placeholder(R.drawable.default_image).into(postImage);
        } else {
            postImage.setVisibility(View.GONE);
        }
    }

    /**
     * 댓글 데이터 로드 메서드
     */
    private void loadComments(int postId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userId", "default_user_id");

        commentList = dbHelper.getCommentsByPostId(postId);

        if (commentAdapter == null) {
            commentAdapter = new CommentAdapter(commentList, comment -> {
                // 필요 시 좋아요 클릭 이벤트 추가
            });
            commentRecyclerView.setAdapter(commentAdapter);
        } else {
            commentAdapter.updateCommentList(commentList);
        }
    }

    /**
     * 팝업 메뉴 표시 메서드
     */
    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.menu_comment_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete_post) {
                showDeleteConfirmationDialog();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    /**
     * 삭제 확인 다이얼로그 메서드
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("글 삭제")
                .setMessage("이 글을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> deletePostAndComments())
                .setNegativeButton("취소", null)
                .show();
    }

    /**
     * 게시글 및 댓글 삭제 메서드
     */
    private void deletePostAndComments() {
        boolean success = dbHelper.deletePostById(postId);
        if (success) {
            Toast.makeText(this, "글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("postDeleted", true);
            resultIntent.putExtra("deletedPostId", postId);
            setResult(RESULT_OK, resultIntent);

            finish();
        } else {
            Toast.makeText(this, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}