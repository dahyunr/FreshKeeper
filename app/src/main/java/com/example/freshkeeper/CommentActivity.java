package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freshkeeper.database.DatabaseHelper;

import java.util.List;

public class CommentActivity extends BaseActivity {
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private DatabaseHelper dbHelper;
    private EditText commentInput;
    private ImageView sendButton;
    private ImageView postImage, postAuthorIcon;
    private TextView postTitle, postAuthor, postContent;
    private TextView noCommentsTextView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // DatabaseHelper 초기화
        dbHelper = new DatabaseHelper(this);

        // 뒤로가기 버튼 처리
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        // UI 초기화
        scrollView = findViewById(R.id.scrollView);
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        postAuthor = findViewById(R.id.postAuthor);
        postImage = findViewById(R.id.postImage);
        noCommentsTextView = findViewById(R.id.noCommentsTextView);
        commentInput = findViewById(R.id.comment_input);
        sendButton = findViewById(R.id.send_button);

        // Intent 데이터 수신
        int postId = getIntent().getIntExtra("postId", -1);
        String title = getIntent().getStringExtra("postTitle");
        String content = getIntent().getStringExtra("postContent");
        String authorName = getIntent().getStringExtra("postAuthor");
        String imageUri = getIntent().getStringExtra("postImage");

        if (postId == -1) {
            finish();
            return;
        }

        // 게시물 데이터 표시
        postTitle.setText(title != null ? title : "제목 없음");
        postContent.setText(content != null ? content : "내용 없음");
        postAuthor.setText(authorName != null ? authorName : "익명 사용자");

        if (imageUri != null && !imageUri.isEmpty()) {
            Glide.with(this).load(imageUri).into(postImage);
            postImage.setVisibility(View.VISIBLE);
        } else {
            postImage.setVisibility(View.GONE);
        }

        // 댓글 RecyclerView 초기화
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 댓글 데이터 로드
        commentList = dbHelper.getCommentsByPostId(postId);
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // "댓글이 없습니다" 메시지 처리
        noCommentsTextView.setVisibility(commentList == null || commentList.isEmpty() ? View.VISIBLE : View.GONE);

        // 댓글 전송 처리
        sendButton.setOnClickListener(v -> onSendClick(v));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(this);
        }

        int postId = getIntent().getIntExtra("postId", -1);
        if (postId != -1) {
            commentList = dbHelper.getCommentsByPostId(postId);

            if (commentAdapter != null) {
                commentAdapter.updateCommentList(commentList);
            }

            noCommentsTextView.setVisibility(commentList == null || commentList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void onSendClick(View view) {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            int postId = getIntent().getIntExtra("postId", -1);
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1);
            String nickname = sharedPreferences.getString("userName", "익명 사용자");
            String iconUri = sharedPreferences.getString("userIcon", "fk_mmm");

            Comment newComment = new Comment(
                    commentText,
                    userId,
                    postId,
                    0,
                    nickname,
                    iconUri
            );

            dbHelper.addComment(newComment);
            commentList.add(newComment);
            commentAdapter.notifyItemInserted(commentList.size() - 1);

            commentInput.setText("");
            commentInput.clearFocus();

            noCommentsTextView.setVisibility(View.GONE);
        }
    }
}
