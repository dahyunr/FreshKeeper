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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freshkeeper.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends BaseActivity {
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private DatabaseHelper dbHelper;
    private EditText commentInput;
    private ImageView sendButton, backButton, postImage, postAuthorIcon;
    private TextView postTitle, postAuthor, postContent, noCommentsTextView;
    private SharedPreferences sharedPreferences;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // 데이터베이스 초기화
        dbHelper = DatabaseHelper.getInstance(this);

        // 데이터 변경 리스너 설정
        // 데이터 변경 리스너 설정 부분
        dbHelper.setOnDatabaseChangeListener(new DatabaseHelper.OnDatabaseChangeListener() {
            @Override
            public void onPostAdded() {
                // 필요하다면 게시글 추가 시 처리할 코드 작성
            }

            @Override
            public void onCommentAdded() {
                runOnUiThread(() -> {
                    if (postId != -1) {
                        loadComments(postId); // 댓글이 추가되었을 때 댓글을 다시 불러옵니다.
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (postId != -1) {
            loadComments(postId);
        }
    }

    /**
     * UI 컴포넌트를 초기화합니다.
     */
    private void initializeUI() {
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        postAuthor = findViewById(R.id.postAuthor);
        postAuthorIcon = findViewById(R.id.postAuthorIcon);
        postImage = findViewById(R.id.postImage);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        noCommentsTextView = findViewById(R.id.noCommentsTextView);
        commentInput = findViewById(R.id.comment_input);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Intent로 전달된 게시글 데이터를 수신하고 UI를 업데이트합니다.
     */
    private void handlePostData() {
        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", -1);
        String title = intent.getStringExtra("postTitle");
        String content = intent.getStringExtra("postContent");
        String authorName = intent.getStringExtra("postAuthor");
        String authorIcon = intent.getStringExtra("postAuthorIcon");
        String imageUri = intent.getStringExtra("postImageUri");

        // 게시글 작성자 닉네임 처리
        if (authorName == null || authorName.isEmpty()) {
            String userEmail = sharedPreferences.getString("userEmail", null);
            if (userEmail != null) {
                authorName = dbHelper.getUserNameByEmail(userEmail);
            }
        }

        if (postId == -1) {
            finish();
            return;
        }

        // UI 업데이트
        updatePostUI(title, content, authorName, authorIcon, imageUri);

        // 댓글 로드
        loadComments(postId);

        // 댓글 작성 버튼 리스너 설정
        sendButton.setOnClickListener(view -> {
            String commentText = commentInput.getText().toString().trim();
            if (!TextUtils.isEmpty(commentText)) {
                String commenterName = "익명 사용자";
                String commenterIcon = "icon_uri";
                int userId = -1;

                Comment newComment = new Comment(commentText, userId, postId, 0, commenterName, commenterIcon);
                dbHelper.addComment(newComment);
                commentList.add(newComment);
                commentAdapter.notifyItemInserted(commentList.size() - 1);
                commentRecyclerView.scrollToPosition(commentList.size() - 1);
                commentInput.setText("");
                Log.d("CommentActivity", "새 댓글 추가: " + newComment.toString());
            } else {
                Toast.makeText(this, "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 게시글 UI를 업데이트합니다.
     */
    private void updatePostUI(String title, String content, String authorName, String authorIcon, String imageUri) {
        postTitle.setText(!TextUtils.isEmpty(title) ? title : "제목 없음");
        postContent.setText(!TextUtils.isEmpty(content) ? content : "내용 없음");
        postAuthor.setText(!TextUtils.isEmpty(authorName) ? authorName : "익명 사용자");

        if (!TextUtils.isEmpty(authorIcon)) {
            Glide.with(this)
                    .load(authorIcon)
                    .placeholder(R.drawable.fk_mmm)
                    .into(postAuthorIcon);
        } else {
            postAuthorIcon.setImageResource(R.drawable.fk_mmm);
        }

        if (!TextUtils.isEmpty(imageUri)) {
            postImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.default_image)
                    .into(postImage);
        } else {
            postImage.setVisibility(View.GONE);
        }
    }

    /**
     * 특정 게시글의 댓글 데이터를 로드합니다.
     */
    private void loadComments(int postId) {
        commentList = dbHelper.getCommentsByPostId(postId);

        if (commentList == null) {
            commentList = new ArrayList<>();
        }

        if (commentAdapter == null) {
            commentAdapter = new CommentAdapter(commentList);
            commentRecyclerView.setAdapter(commentAdapter);
        } else {
            commentAdapter.updateCommentList(commentList);
        }

        noCommentsTextView.setVisibility(commentList.isEmpty() ? View.VISIBLE : View.GONE);
        Log.d("CommentActivity", "댓글 로드 완료: 총 " + commentList.size() + "개");
    }
}
