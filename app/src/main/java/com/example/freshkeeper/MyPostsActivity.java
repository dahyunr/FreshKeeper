package com.example.freshkeeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyPostsActivity extends BaseActivity {

    private ImageView backButton;
    private EditText titleInput, contentInput;
    private ImageView photoIcon;
    private Button submitButton;
    private TextView titleText;
    private static final int PICK_IMAGE_REQUEST = 1;  // 갤러리에서 이미지 선택 요청 코드
    private Uri imageUri;  // 선택한 이미지의 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        // UI 컴포넌트 초기화
        backButton = findViewById(R.id.back_button);
        titleText = findViewById(R.id.title);
        titleInput = findViewById(R.id.title_input);
        contentInput = findViewById(R.id.content_input);
        photoIcon = findViewById(R.id.photo_icon);
        submitButton = findViewById(R.id.submit_button);

        // 뒤로가기 버튼 클릭 시
        backButton.setOnClickListener(v -> onBackPressed());

        // 사진 아이콘 클릭 시 갤러리 열기
        photoIcon.setOnClickListener(v -> {
            // 갤러리에서 이미지를 선택할 수 있는 인텐트
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");  // 이미지 유형만 필터링
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // 작성하기 버튼 클릭 시
        submitButton.setOnClickListener(v -> {
            // 제목과 내용이 입력되었는지 확인 후 처리
            String title = titleInput.getText().toString().trim();
            String content = contentInput.getText().toString().trim();

            if (title.isEmpty()) {
                titleInput.setError("제목을 입력하세요.");
                return;
            }

            if (content.isEmpty()) {
                contentInput.setError("내용을 입력하세요.");
                return;
            }

            // 게시글 작성 처리 로직
            createPost(title, content);
        });
    }

    // 게시글 작성 처리 메서드
    private void createPost(String title, String content) {
        // 게시글 작성 후, 커뮤니티 페이지로 이동
        Intent intent = new Intent(MyPostsActivity.this, CommunityActivity.class);
        intent.putExtra("title", title);  // 제목 전달
        intent.putExtra("content", content);  // 내용 전달
        if (imageUri != null) {
            intent.putExtra("imageUri", imageUri.toString());  // 이미지 URI 전달
        }
        startActivity(intent);  // 커뮤니티 페이지로 이동
    }

    // 갤러리에서 이미지를 선택한 후 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 선택한 이미지의 URI를 가져옴
            imageUri = data.getData();
            try {
                // 선택한 이미지를 ImageView에 표시
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                photoIcon.setImageBitmap(bitmap);  // 이미지 설정
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지 불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
