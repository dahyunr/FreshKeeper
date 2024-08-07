package com.example.freshkeeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MypageActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private TextView nicknameEditText, emailEditText;
    private ImageView iconRef, iconCalendar, iconBarcode, iconMypage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);  // your mypage.xml 파일과 연결

        // UI 요소들 초기화
        profileImage = findViewById(R.id.profile_image);
        nicknameEditText = findViewById(R.id.profile_nickname);
        emailEditText = findViewById(R.id.profile_email);
        iconRef = findViewById(R.id.icon_ref);
        iconCalendar = findViewById(R.id.icon_calendar);
        iconBarcode = findViewById(R.id.icon_barcode);
        iconMypage = findViewById(R.id.icon_mypage);

        // 프로필 이미지 클릭 리스너
        profileImage.setOnClickListener(v -> openGallery());

        // 닉네임 클릭 리스너
        nicknameEditText.setOnClickListener(v -> {
            // 예: EditText로 변경하여 사용자가 입력할 수 있도록 설정
            // 실제 구현에서는 다이얼로그 등을 사용하여 닉네임을 입력받는 방법도 가능
            EditText input = new EditText(this);
            input.setText(nicknameEditText.getText());
            nicknameEditText.setText(input.getText().toString());
        });

        // 이메일 클릭 리스너
        emailEditText.setOnClickListener(v -> {
            // 예: EditText로 변경하여 사용자가 입력할 수 있도록 설정
            // 실제 구현에서는 다이얼로그 등을 사용하여 이메일을 입력받는 방법도 가능
            EditText input = new EditText(this);
            input.setText(emailEditText.getText());
            emailEditText.setText(input.getText().toString());
        });

        // 하단바 아이콘 클릭 리스너
        iconRef.setOnClickListener(v -> Toast.makeText(this, "냉장고 클릭됨", Toast.LENGTH_SHORT).show());
        iconCalendar.setOnClickListener(v -> Toast.makeText(this, "캘린더 클릭됨", Toast.LENGTH_SHORT).show());
        iconBarcode.setOnClickListener(v -> Toast.makeText(this, "바코드 클릭됨", Toast.LENGTH_SHORT).show());
        iconMypage.setOnClickListener(v -> Toast.makeText(this, "이미 마이페이지에 있습니다", Toast.LENGTH_SHORT).show());
    }

    // 갤러리 열기 메서드
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    // 선택한 이미지 처리 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
