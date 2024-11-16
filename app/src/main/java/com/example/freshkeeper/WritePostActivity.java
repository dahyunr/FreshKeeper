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

        titleEditText = findViewById(R.id.title_input);
        contentEditText = findViewById(R.id.content_input);
        saveButton = findViewById(R.id.submit_button);
        backButton = findViewById(R.id.back_button);
        photoIcon = findViewById(R.id.photo_icon);
        imageRecyclerView = findViewById(R.id.image_recycler_view);

        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUris, this::removeImage);

        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setAdapter(imageAdapter);

        backButton.setOnClickListener(v -> finish());
        photoIcon.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (title.isEmpty()) {
                titleEditText.setError("제목을 입력하세요.");
                return;
            }

            if (content.isEmpty()) {
                contentEditText.setError("내용을 입력하세요.");
                return;
            }

            savePost(title, content);
        });
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
                    imageUris.add(imageUri);
                }
            } else if (data.getData() != null) { // 단일 선택인 경우
                if (imageUris.size() < MAX_IMAGE_COUNT) {
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                }
            }
            imageAdapter.notifyDataSetChanged();
        }
    }

    private void removeImage(Uri uri) {
        imageUris.remove(uri);
        imageAdapter.notifyDataSetChanged();
    }

    private void savePost(String title, String content) {
        if (imageUris.isEmpty()) {
            Toast.makeText(this, "최소 한 개의 이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 첫 번째 이미지 URI를 따로 저장
        Uri firstImageUri = imageUris.get(0);

        Toast.makeText(this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("firstImageUri", firstImageUri.toString()); // 첫 번째 이미지 URI 전달
        intent.putParcelableArrayListExtra("imageUris", imageUris); // 전체 이미지 URI 리스트 전달
        setResult(RESULT_OK, intent);
        finish();
    }
}
