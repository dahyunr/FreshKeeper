package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.List;

public class WritePostActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int MAX_IMAGE_COUNT = 5;

    private EditText titleEditText, contentEditText;
    private Button saveButton;
    private ImageView backButton, photoIcon;
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        // UI 초기화
        titleEditText = findViewById(R.id.title_input); // 제목 입력 필드
        contentEditText = findViewById(R.id.content_input); // 내용 입력 필드
        saveButton = findViewById(R.id.submit_button); // 작성 버튼
        backButton = findViewById(R.id.back_button); // 뒤로가기 버튼
        photoIcon = findViewById(R.id.photo_icon); // 사진 추가 버튼
        imageRecyclerView = findViewById(R.id.image_recycler_view); // 이미지 표시 RecyclerView

        // 이미지 리스트 및 어댑터 설정
        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUris, this::removeImage);

        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setAdapter(imageAdapter);

        // 뒤로가기 버튼 클릭 리스너 설정
        backButton.setOnClickListener(v -> {
            Log.d("WritePostActivity", "뒤로가기 버튼 클릭됨.");
            finish();
        });

        // 사진 추가 버튼 클릭 리스너 설정
        photoIcon.setOnClickListener(v -> {
            Log.d("WritePostActivity", "사진 추가 아이콘 클릭됨.");
            openGallery();
        });

        // 저장 버튼 클릭 리스너 설정
        saveButton.setOnClickListener(v -> onSaveButtonClick());
    }

    private void onSaveButtonClick() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        // 제목과 내용 유효성 검사
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

        // 버튼 중복 클릭 방지
        saveButton.setEnabled(false);

        // 이미지 URI 리스트 변환
        List<String> imageUrisAsString = new ArrayList<>();
        if (imageUris != null) {
            for (Uri uri : imageUris) {
                if (!imageUrisAsString.contains(uri.toString())) {
                    imageUrisAsString.add(uri.toString());
                }
            }
        }

        Log.d("WritePostActivity", "저장 중: 제목=" + title + ", 내용=" + content + ", 이미지 수=" + imageUrisAsString.size());

        // 게시글 저장 호출
        savePost(title, content, imageUrisAsString);
    }

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
                    if (imageUri != null && !imageUris.contains(imageUri)) {
                        imageUris.add(imageUri);
                        Log.d("WritePostActivity", "다중 선택된 이미지 URI: " + imageUri.toString());
                    } else {
                        Log.e("WritePostActivity", "잘못된 URI 발견");
                    }
                }
            } else if (data.getData() != null) { // 단일 선택
                Uri imageUri = data.getData();
                if (imageUri != null && !imageUris.contains(imageUri)) {
                    imageUris.add(imageUri);
                    Log.d("WritePostActivity", "단일 선택된 이미지 URI: " + imageUri.toString());
                } else {
                    Log.e("WritePostActivity", "잘못된 URI 발견");
                }
            }
            imageAdapter.notifyDataSetChanged();
        } else {
            Log.e("WritePostActivity", "이미지 선택 중 문제가 발생했습니다.");
        }
    }

    private void removeImage(Uri uri) {
        imageUris.remove(uri);
        imageAdapter.notifyDataSetChanged();
    }

    private void savePost(String title, String content, List<String> imageUris) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);

        // SharedPreferences에서 사용자 정보 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "default_user_id"); // 사용자 ID
        String userName = sharedPreferences.getString("userName", "익명 사용자"); // 사용자 닉네임
        String userIcon = sharedPreferences.getString("userIcon", "fk_mmm"); // 사용자 아이콘

        // CommunityPost 객체 생성
        CommunityPost post = new CommunityPost(
                0, // postId (DB에서 자동 생성)
                title != null ? title : "제목 없음", // 제목
                content != null ? content : "내용 없음", // 내용
                imageUris != null ? imageUris : new ArrayList<>(), // 이미지 URI 리스트
                userId, // 작성자 ID
                0, // 좋아요 수 초기값
                0, // 댓글 수 초기값
                false, // 좋아요 상태
                userName != null && !userName.isEmpty() ? userName : "익명 사용자", // 닉네임
                userIcon != null && !userIcon.isEmpty() ? userIcon : "fk_mmm" // 아이콘
        );

        // 데이터베이스에 저장
        if (dbHelper.isPostExists(title)) { // 중복 체크
            Toast.makeText(this, "이미 동일한 제목의 게시물이 존재합니다.", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true); // 버튼 다시 활성화
            return;
        }

        long postId = dbHelper.addCommunityPost(post);
        if (postId != -1) {
            Log.d("WritePostActivity", "게시글 저장 성공. ID: " + postId);

            // 작성된 게시글 ID와 데이터를 Intent에 담아 반환
            Intent resultIntent = new Intent();
            resultIntent.putExtra("postId", postId); // 게시글 ID 전달
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Log.e("WritePostActivity", "게시글 저장 실패.");
            Toast.makeText(this, "게시글 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true); // 버튼 다시 활성화
        }
    }
}