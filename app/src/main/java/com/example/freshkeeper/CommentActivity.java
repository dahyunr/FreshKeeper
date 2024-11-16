package com.example.freshkeeper;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView postImage;
    private TextView postTitle, postAuthor, postContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // 뒤로가기 버튼 처리
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // DatabaseHelper 초기화
        dbHelper = new DatabaseHelper(this);

        // 게시글 정보 표시
        postTitle = findViewById(R.id.postTitle);
        postAuthor = findViewById(R.id.postAuthor);
        postContent = findViewById(R.id.postContent);
        postImage = findViewById(R.id.postImage);

        int postId = getIntent().getIntExtra("postId", -1);
        if (postId == -1) {
            finish();
            return;
        }

        CommunityPost post = dbHelper.getPostById(postId);
        if (post != null) {
            postTitle.setText(post.getTitle());
            postAuthor.setText(post.getUserId());
            postContent.setText(post.getContent());

            if (post.getFirstImageUri() != null && !post.getFirstImageUri().isEmpty()) {
                postImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(post.getFirstImageUri()).into(postImage);
            } else {
                postImage.setVisibility(View.GONE);
            }
        }

        // RecyclerView 초기화
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 댓글 데이터 로드
        commentList = dbHelper.getCommentsByPostId(postId);
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // 댓글 입력 및 전송
        commentInput = findViewById(R.id.comment_input);
        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(this::onSendClick);
    }

    private void onSendClick(View view) {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            int postId = getIntent().getIntExtra("postId", -1);
            if (postId == -1) return;

            int userId = 1; // 로그인 사용자 ID (임시)
            Comment newComment = new Comment(commentText, userId, postId, 0);
            dbHelper.addComment(newComment);

            // 댓글 목록에 추가 및 업데이트
            commentList.add(newComment);
            commentAdapter.notifyItemInserted(commentList.size() - 1);
            commentInput.setText("");
            commentInput.requestFocus();
        }
    }
}
