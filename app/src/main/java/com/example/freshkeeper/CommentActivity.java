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

        // 뒤로가기 버튼 처리
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // DatabaseHelper 초기화
        dbHelper = new DatabaseHelper(this);

        // UI 초기화
        postTitle = findViewById(R.id.postTitle);
        postAuthor = findViewById(R.id.postAuthor);
        postContent = findViewById(R.id.postContent);
        postImage = findViewById(R.id.postImage);
        postAuthorIcon = findViewById(R.id.postAuthorIcon);
        noCommentsTextView = findViewById(R.id.noCommentsTextView);
        scrollView = findViewById(R.id.scrollView);

        // Intent 데이터 수신
        int postId = getIntent().getIntExtra("postId", -1);
        String title = getIntent().getStringExtra("postTitle");
        String content = getIntent().getStringExtra("postContent");
        String authorName = getIntent().getStringExtra("postAuthor");
        String imageUri = getIntent().getStringExtra("postImage");

        // 게시글 데이터 유효성 확인
        if (postId == -1) {
            finish();
            return;
        }

        // 게시글 데이터 표시
        postTitle.setText(title != null && !title.isEmpty() ? title : "제목 없음");
        postContent.setText(content != null && !content.isEmpty() ? content : "내용 없음");
        postAuthor.setText(authorName != null && !authorName.isEmpty() ? authorName : "익명 사용자");

        // 게시글 이미지 표시
        if (imageUri != null && !imageUri.isEmpty()) {
            postImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUri).into(postImage);
        } else {
            postImage.setVisibility(View.GONE);
        }

        // 작성자 아이콘 설정
        postAuthorIcon.setImageResource(R.drawable.fk_mmm);

        // 댓글 RecyclerView 초기화
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 댓글 데이터 로드
        commentList = dbHelper.getCommentsByPostId(postId);
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // 댓글이 없을 경우 "댓글이 없습니다" 메시지 표시
        if (commentList == null || commentList.isEmpty()) {
            noCommentsTextView.setVisibility(View.VISIBLE);
        } else {
            noCommentsTextView.setVisibility(View.GONE);
        }

        // 댓글 입력 및 전송
        commentInput = findViewById(R.id.comment_input);
        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(this::onSendClick);

        // 댓글 입력창 포커스 처리: 키보드가 올라올 때만 댓글창을 위로 올리기
        commentInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                scrollView.smoothScrollTo(0, commentInput.getBottom());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int postId = getIntent().getIntExtra("postId", -1);
        if (postId != -1) {
            // 댓글 데이터 다시 로드
            commentList = dbHelper.getCommentsByPostId(postId);

            if (commentAdapter != null) {
                commentAdapter.updateCommentList(commentList);
            } else {
                commentAdapter = new CommentAdapter(commentList);
                commentRecyclerView.setAdapter(commentAdapter);
            }

            // 댓글이 없을 경우 메시지 표시
            if (commentList.isEmpty()) {
                noCommentsTextView.setVisibility(View.VISIBLE);
            } else {
                noCommentsTextView.setVisibility(View.GONE);
            }

            // 로드한 댓글 로그로 확인하기
            Log.d("CommentActivity", "댓글 로드 완료, 총 댓글 수: " + commentList.size());
        }
    }

    private void onSendClick(View view) {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            int postId = getIntent().getIntExtra("postId", -1);
            if (postId == -1) return;

            // SharedPreferences에서 사용자 정보 가져오기
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1);
            String nickname = sharedPreferences.getString("userName", "고릴라");  // 로그인된 사용자 이름
            String iconUri = sharedPreferences.getString("userIcon", "fk_mmm"); // 로그인된 사용자 아이콘 URI

            // 댓글 객체 생성
            Comment newComment = new Comment(
                    commentText,
                    userId,
                    postId,
                    0,
                    nickname != null && !nickname.isEmpty() ? nickname : "고릴라", // 닉네임 설정
                    iconUri != null && !iconUri.isEmpty() ? iconUri : "fk_mmm" // 아이콘 URI 설정
            );

            // 댓글을 데이터베이스에 추가
            dbHelper.addComment(newComment);

            // RecyclerView에 댓글 추가 및 업데이트
            commentList.add(newComment);
            commentAdapter.notifyItemInserted(commentList.size() - 1);
            commentInput.setText("");
            commentInput.clearFocus();

            // 댓글이 추가되면 "댓글이 없습니다" 메시지 숨기기
            noCommentsTextView.setVisibility(View.GONE);

            // 업데이트된 댓글 수 전달
            int newCommentCount = commentList.size();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newCommentCount", newCommentCount);
            resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
            setResult(RESULT_OK, resultIntent);
        }
    }
}