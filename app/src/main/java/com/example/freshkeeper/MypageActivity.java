package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.freshkeeper.database.DatabaseHelper;

import java.io.IOException;

public class MypageActivity extends BaseActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private TextView nicknameTextView;
    private ImageView iconRef, iconCalendar, iconBarcode, iconMypage;
    private TextView notificationSettingsTextView;
    private TextView contactUsButton;  // 문의하기 버튼 변수 추가
    private TextView faqTextView;  // 자주 묻는 질문 버튼
    private TextView logoutTextView;  // 로그아웃 버튼 변수 추가
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // DB 헬퍼 초기화
        dbHelper = new DatabaseHelper(this);

        // UI 요소 초기화
        profileImage = findViewById(R.id.profile_image);
        nicknameTextView = findViewById(R.id.profile_nickname);
        iconRef = findViewById(R.id.icon_ref);
        iconCalendar = findViewById(R.id.icon_calendar);
        iconBarcode = findViewById(R.id.icon_barcode);
        iconMypage = findViewById(R.id.icon_mypage);
        notificationSettingsTextView = findViewById(R.id.notification_settings);

        // 로그아웃 텍스트뷰 초기화 및 클릭 리스너 추가
        logoutTextView = findViewById(R.id.logout);
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();  // 로그아웃 처리
            }
        });

        // "자주 물어보는 질문" 텍스트뷰 초기화 및 클릭 리스너 추가
        faqTextView = findViewById(R.id.button_faq);
        faqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent faqIntent = new Intent(MypageActivity.this, FAQActivity.class);
                startActivity(faqIntent);
            }
        });

        // 문의하기 버튼 초기화 및 클릭 리스너 추가
        contactUsButton = findViewById(R.id.button_contact_us);
        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(MypageActivity.this, QnaActivity.class);
                startActivity(contactIntent);
            }
        });

        profileImage.setOnClickListener(v -> openGallery());

        // 닉네임 클릭 리스너 - 다이얼로그로 변경
        nicknameTextView.setOnClickListener(v -> {
            showEditDialog("닉네임 변경", nicknameTextView);
        });

        // 하단바 아이콘 클릭 리스너
        final Intent intent = new Intent();  // Intent 변수 재사용
        iconRef.setOnClickListener(v -> {
            intent.setClass(MypageActivity.this, FkmainActivity.class);
            startActivity(intent);
        });

        iconCalendar.setOnClickListener(v -> {
            intent.setClass(MypageActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        iconBarcode.setOnClickListener(v -> {
            intent.setClass(MypageActivity.this, BarcodeScanActivity.class);
            startActivity(intent);
        });

        iconMypage.setOnClickListener(v -> Toast.makeText(this, "이미 마이페이지에 있습니다", Toast.LENGTH_SHORT).show());

        notificationSettingsTextView.setOnClickListener(v -> {
            Intent notificationIntent = new Intent(MypageActivity.this, NotificationSettingsActivity.class);
            startActivity(notificationIntent);
        });

        // SharedPreferences에서 사용자 정보 로드
        loadUserInfo();
    }

    private void loadUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Unknown User");

        // UI 업데이트
        nicknameTextView.setText(userName);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

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

    private void showEditDialog(String title, TextView textViewToEdit) {
        if (textViewToEdit != null) {  // Null 체크 추가
            EditText input = new EditText(MypageActivity.this);  // this 대신 MypageActivity.this 사용
            input.setText(textViewToEdit.getText());

            new AlertDialog.Builder(MypageActivity.this)  // this 대신 MypageActivity.this 사용
                    .setTitle(title)
                    .setView(input)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newText = input.getText().toString();
                            if (!newText.isEmpty()) {
                                textViewToEdit.setText(newText);
                                Toast.makeText(MypageActivity.this, title + "이(가) 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MypageActivity.this, "입력 값이 비어 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        } else {
            Toast.makeText(MypageActivity.this, "잘못된 항목입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 로그아웃 메서드 (회원 정보는 삭제하지 않음)
    private void logout() {
        // 로그인 상태를 false로 설정 (로그아웃 처리)
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);  // 로그아웃 상태 저장
        editor.apply();

        // LoginActivity로 이동하고 기존 액티비티 스택을 모두 지우기
        Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // 기존 스택 제거
        startActivity(intent);
        finish();
    }
}
