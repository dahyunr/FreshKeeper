package com.example.freshkeeper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshkeeper.database.DatabaseHelper;

import java.util.ArrayList;

public class WritePostActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int MAX_IMAGE_COUNT = 5;

    private EditText titleEditText, contentEditText;
    private Button saveButton;
    private ImageView backButton, photoIcon;
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imageUris;

    private DatabaseHelper dbHelper;
    private int postId = -1; // 기본값: 새 게시글 작성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        // DatabaseHelper 초기화
        dbHelper = new DatabaseHelper(this);

        // UI 초기화
        titleEditText = findViewById(R.id.title_input);
        contentEditText = findViewById(R.id.content_input);
        saveButton = findViewById(R.id.submit_button);
        backButton = findViewById(R.id.back_button);
        photoIcon = findViewById(R.id.photo_icon);
        imageRecyclerView = findViewById(R.id.image_recycler_view);

        // 이미지 리스트 및 어댑터 초기화
        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUris, this::removeImage);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setAdapter(imageAdapter);

        // 버튼 리스너 설정
        backButton.setOnClickListener(v -> finish());
        photoIcon.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> onSaveButtonClick());

        // 게시글 수정 모드 처리
        handleEditMode();
    }

    /**
     * 수정 모드 처리
     */
    private void handleEditMode() {
        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", -1);
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        ArrayList<String> receivedImageUris = intent.getStringArrayListExtra("imageUris");

        if (postId != -1) { // 수정 모드
            titleEditText.setText(title != null ? title : "");
            contentEditText.setText(content != null ? content : "");

            // 전달받은 이미지 URI 리스트 처리
            if (receivedImageUris != null) {
                for (String uriString : receivedImageUris) {
                    imageUris.add(Uri.parse(uriString));
                }
                imageAdapter.notifyDataSetChanged(); // 어댑터 업데이트
            }
        }
    }

    /**
     * 갤러리 열기
     */
    private void openGallery() {
        if (imageUris.size() >= MAX_IMAGE_COUNT) {
            Toast.makeText(this, "최대 " + MAX_IMAGE_COUNT + "개의 이미지를 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 다중 선택 허용
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) { // 다중 선택
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count && imageUris.size() < MAX_IMAGE_COUNT; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    if (!imageUris.contains(imageUri)) {
                        imageUris.add(imageUri);
                    }
                }
            } else if (data.getData() != null) { // 단일 선택
                Uri imageUri = data.getData();
                if (!imageUris.contains(imageUri) && imageUris.size() < MAX_IMAGE_COUNT) {
                    imageUris.add(imageUri);
                }
            }

            // 디버깅용 로그
            for (Uri uri : imageUris) {
                Log.d("WritePostActivity", "선택된 이미지 URI: " + uri.toString());
            }

            imageAdapter.notifyDataSetChanged(); // 변경 반영
        }
    }

    /**
     * 이미지 삭제 처리
     */
    private void removeImage(Uri uri) {
        imageUris.remove(uri);
        imageAdapter.notifyDataSetChanged();
    }

    /**
     * 저장 버튼 클릭 처리
     */
    private void onSaveButtonClick() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        // 제목 및 내용 유효성 검사
        if (title.isEmpty()) {
            titleEditText.setError("제목을 입력하세요.");
            titleEditText.requestFocus();
            return;
        }
        if (content.isEmpty()) {
            contentEditText.setError("내용을 입력하세요.");
            contentEditText.requestFocus();
            return;
        }

        // 이미지 URI 디버깅 로그
        for (Uri uri : imageUris) {
            Log.d("WritePostActivity", "저장될 이미지 URI: " + uri.toString());
        }

        // 이미지 URI를 String 리스트로 변환
        ArrayList<String> imageUriStrings = new ArrayList<>();
        for (Uri uri : imageUris) {
            imageUriStrings.add(uri.toString());
        }

        if (postId == -1) {
            // 새 게시글 추가
            dbHelper.addCommunityPost(new CommunityPost(-1, title, content, imageUriStrings, "userId", 0, 0, false, "익명 사용자", "fk_mmm"));
            Toast.makeText(this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 기존 게시글 수정
            boolean isUpdated = dbHelper.updatePost(postId, title, content, imageUriStrings);
            if (isUpdated) {
                Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "게시글 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        // 결과 전달 및 화면 종료
        Intent resultIntent = new Intent();
        resultIntent.putExtra("postId", postId);
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("content", content);
        resultIntent.putStringArrayListExtra("imageUris", imageUriStrings);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
