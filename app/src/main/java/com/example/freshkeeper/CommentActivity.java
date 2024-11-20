package com.example.freshkeeper;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;
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
    private ImageView postImage, postAuthorIcon; // 작성자 아이콘 추가
    private TextView postTitle, postAuthor, postContent;
    private TextView noCommentsTextView; // "댓글이 없습니다" 메시지 추가
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

        // 게시글 정보 표시를 위한 UI 초기화
        postTitle = findViewById(R.id.postTitle);
        postAuthor = findViewById(R.id.postAuthor);
        postContent = findViewById(R.id.postContent);
        postImage = findViewById(R.id.postImage);
        postAuthorIcon = findViewById(R.id.postAuthorIcon);
        noCommentsTextView = findViewById(R.id.noCommentsTextView);
        scrollView = findViewById(R.id.scrollView);  // ScrollView 참조 추가

        // Intent 데이터 수신
        int postId = getIntent().getIntExtra("postId", -1);
        String title = getIntent().getStringExtra("postTitle");
        String content = getIntent().getStringExtra("postContent");
        String authorName = getIntent().getStringExtra("postAuthor");
        String imageUri = getIntent().getStringExtra("postImage");

        // 게시글 데이터 표시
        if (postId == -1) {
            finish();
            return;
        }

        // 게시글 제목, 내용, 작성자 이름 설정
        postTitle.setText(title != null ? title : "제목 없음");
        postContent.setText(content != null ? content : "내용 없음");
        postAuthor.setText(authorName != null ? authorName : "익명 사용자");

        // 게시글 이미지 표시
        if (imageUri != null && !imageUri.isEmpty()) {
            postImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUri).into(postImage);
        } else {
            postImage.setVisibility(View.GONE);
        }

        // 작성자 아이콘 설정
        if (authorName != null && !authorName.isEmpty()) {
            postAuthorIcon.setImageResource(R.drawable.fk_mmm); // 기본 아이콘 설정
        }

        // RecyclerView 초기화
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 댓글 데이터 로드
        commentList = dbHelper.getCommentsByPostId(postId);
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // 댓글이 없을 경우 "댓글이 없습니다" 메시지 표시
        if (commentList.isEmpty()) {
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
                // 키보드가 올라오면 ScrollView가 댓글을 보이도록 스크롤
                scrollView.smoothScrollTo(0, commentInput.getBottom());
            }
        });
    }

    private void onSendClick(View view) {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            int postId = getIntent().getIntExtra("postId", -1);
            if (postId == -1) return;

            int userId = 1; // 로그인 사용자 ID (임시)

            // 닉네임과 아이콘 정보 가져오기
            String nickname = dbHelper.getNicknameByUserId(String.valueOf(userId)); // 로그인 사용자의 닉네임
            String iconUri = dbHelper.getUserIconByUserId(String.valueOf(userId)); // 로그인 사용자의 아이콘

            // 댓글 객체 생성 (닉네임과 아이콘 포함)
            Comment newComment = new Comment(
                    commentText,
                    userId,
                    postId,
                    0,
                    nickname != null ? nickname : "익명 사용자",
                    iconUri != null ? iconUri : "default_user_icon"
            );

            dbHelper.addComment(newComment);

            // 댓글 목록에 추가 및 업데이트
            commentList.add(newComment);
            commentAdapter.notifyItemInserted(commentList.size() - 1);
            commentInput.setText("");
            commentInput.requestFocus();

            // 댓글이 추가되면 "댓글이 없습니다" 메시지 숨기기
            noCommentsTextView.setVisibility(View.GONE);
        }
    }
}
