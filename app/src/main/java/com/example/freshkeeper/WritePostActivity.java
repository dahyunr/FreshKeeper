package com.example.freshkeeper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        // UI 초기화
        titleEditText = findViewById(R.id.title_input);
        contentEditText = findViewById(R.id.content_input);
        saveButton = findViewById(R.id.submit_button);
        backButton = findViewById(R.id.back_button);
        photoIcon = findViewById(R.id.photo_icon);
        imageRecyclerView = findViewById(R.id.image_recycler_view);

        // 이미지 리스트 및 어댑터 설정
        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUris, this::removeImage);

        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setAdapter(imageAdapter);

        // 버튼 클릭 리스너 설정
        backButton.setOnClickListener(v -> finish());
        photoIcon.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> onSaveButtonClick());
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
            if (data.getClipData() != null) { // 다중 선택인 경우
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count && imageUris.size() < MAX_IMAGE_COUNT; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    if (!imageUris.contains(imageUri)) { // 중복 방지
                        imageUris.add(imageUri);
                    }
                }
            } else if (data.getData() != null) { // 단일 선택인 경우
                if (imageUris.size() < MAX_IMAGE_COUNT) {
                    Uri imageUri = data.getData();
                    if (!imageUris.contains(imageUri)) { // 중복 방지
                        imageUris.add(imageUri);
                    }
                }
            }
            imageAdapter.notifyDataSetChanged();
        }
    }

    private void removeImage(Uri uri) {
        imageUris.remove(uri);
        imageAdapter.notifyDataSetChanged();
    }

    private void onSaveButtonClick() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        // 제목 유효성 검사
        if (title.isEmpty()) {
            titleEditText.setError("제목을 입력하세요.");
            titleEditText.requestFocus();
            return;
        }

        // 내용 유효성 검사
        if (content.isEmpty()) {
            contentEditText.setError("내용을 입력하세요.");
            contentEditText.requestFocus();
            return;
        }

        // 게시글 저장
        savePost(title, content);
    }

    private void savePost(String title, String content) {
        // 결과 전달을 위한 Intent 생성
        Intent intent = new Intent();
        intent.putExtra("title", title);
        intent.putExtra("content", content);

        // 이미지가 있을 경우 URI 전달
        if (!imageUris.isEmpty()) {
            Uri firstImageUri = imageUris.get(0);
            intent.putExtra("firstImageUri", firstImageUri.toString());
            intent.putParcelableArrayListExtra("imageUris", imageUris);
        }

        // 결과 설정 및 액티비티 종료
        setResult(RESULT_OK, intent);
        Toast.makeText(this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
