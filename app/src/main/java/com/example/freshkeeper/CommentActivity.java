package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
    private ImageView sendButton, postImage, postAuthorIcon;
    private TextView postTitle, postAuthor, postContent, noCommentsTextView;
    private ScrollView scrollView;

    private int postId;
    private String postTitleText, postContentText;
    private List<String> imageUris;

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
        postId = getIntent().getIntExtra("postId", -1);
        postTitleText = getIntent().getStringExtra("postTitle");
        postContentText = getIntent().getStringExtra("postContent");
        String authorName = getIntent().getStringExtra("postAuthor");
        String imageUriString = getIntent().getStringExtra("postImage");

        // 게시글 데이터 유효성 확인
        if (postId == -1) {
            finish();
            return;
        }

        // 게시글 데이터 표시
        postTitle.setText(postTitleText != null && !postTitleText.isEmpty() ? postTitleText : "제목 없음");
        postContent.setText(postContentText != null && !postContentText.isEmpty() ? postContentText : "내용 없음");
        postAuthor.setText(authorName != null && !authorName.isEmpty() ? authorName : "익명 사용자");

        // 게시글 이미지 표시
        if (imageUriString != null && !imageUriString.isEmpty()) {
            postImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUriString).into(postImage);

            // 이미지 URI를 리스트로 변환
            imageUris = new ArrayList<>();
            imageUris.add(imageUriString); // 단일 URI를 리스트로 처리
        } else {
            postImage.setVisibility(View.GONE);
            imageUris = new ArrayList<>();
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

        // fk_vert 버튼 클릭 이벤트 처리
        ImageView fkVertIcon = findViewById(R.id.fk_vert_icon);
        fkVertIcon.setOnClickListener(this::showOptionMenu);
    }

    // fk_vert 클릭 시 옵션 메뉴 표시
    private void showOptionMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.post_options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.option_edit) {
                editPost();
                return true;
            } else if (item.getItemId() == R.id.option_delete) {
                deletePost();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    // 글 수정 메서드
    private void editPost() {
        Intent intent = new Intent(this, WritePostActivity.class);
        intent.putExtra("postId", postId);
        intent.putExtra("title", postTitleText);
        intent.putExtra("content", postContentText);
        intent.putStringArrayListExtra("imageUris", (ArrayList<String>) imageUris); // 이미지 URI 리스트 전달
        startActivityForResult(intent, 2000);
    }

    // 글 삭제 메서드
    private void deletePost() {
        boolean isDeleted = dbHelper.deletePost(postId);

        if (isDeleted) {
            Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(); // 삭제된 결과를 전달
            setResult(RESULT_OK, intent);
            finish(); // 현재 화면 종료
        } else {
            Toast.makeText(this, "게시글 삭제 실패", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        commentList = dbHelper.getCommentsByPostId(postId);
        commentAdapter.updateCommentList(commentList);

        if (commentList.isEmpty()) {
            noCommentsTextView.setVisibility(View.VISIBLE);
        } else {
            noCommentsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000 && resultCode == RESULT_OK && data != null) {
            postTitleText = data.getStringExtra("title");
            postContentText = data.getStringExtra("content");

            postTitle.setText(postTitleText);
            postContent.setText(postContentText);

            // 이미지 URI 업데이트
            imageUris = data.getStringArrayListExtra("imageUris");
            if (imageUris != null && !imageUris.isEmpty()) {
                postImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(imageUris.get(0)).into(postImage); // 첫 번째 이미지 표시
            } else {
                postImage.setVisibility(View.GONE);
            }
        }
    }

    private void onSendClick(View view) {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1);
            String nickname = sharedPreferences.getString("userName", "익명 사용자");
            String iconUri = sharedPreferences.getString("userIcon", "fk_mmm");

            Comment newComment = new Comment(
                    commentText, // 댓글 내용
                    userId,      // 사용자 ID
                    postId,      // 게시글 ID
                    0,           // 좋아요 수
                    nickname,    // 사용자 닉네임
                    iconUri      // 사용자 아이콘
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
