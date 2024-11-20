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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.freshkeeper.database.DatabaseHelper;

import java.io.IOException;

public class MypageActivity extends BaseActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private TextView nicknameTextView;
    private TextView notificationSettingsTextView;
    private TextView contactUsButton;  // 문의하기 버튼 변수 추가
    private TextView faqTextView;  // 자주 묻는 질문 버튼
    private TextView noticeButton;  // 공지사항 버튼 추가
    private TextView logoutTextView;  // 로그아웃 버튼 변수 추가
    private TextView privacyPolicyButton;  // 개인정보 처리방침 버튼
    private TextView termsOfServiceButton;  // 이용약관 버튼
    private DatabaseHelper dbHelper;
    private boolean isGuestUser;  // 비회원 여부 체크 변수 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // DB 헬퍼 초기화
        dbHelper = new DatabaseHelper(this);

        // UI 요소 초기화
        profileImage = findViewById(R.id.profile_image);
        nicknameTextView = findViewById(R.id.profile_nickname);
        notificationSettingsTextView = findViewById(R.id.notification_settings);
        contactUsButton = findViewById(R.id.button_contact_us);
        faqTextView = findViewById(R.id.button_faq);
        noticeButton = findViewById(R.id.button_notice);
        logoutTextView = findViewById(R.id.logout);
        privacyPolicyButton = findViewById(R.id.button_privacy_policy);
        termsOfServiceButton = findViewById(R.id.button_terms_of_service);

        // 로그아웃 텍스트뷰 초기화 및 클릭 리스너 추가
        logoutTextView.setOnClickListener(v -> logout());

        // "자주 묻는 질문" 텍스트뷰 초기화 및 클릭 리스너 추가
        faqTextView.setOnClickListener(v -> {
            Intent faqIntent = new Intent(MypageActivity.this, FAQActivity.class);
            startActivity(faqIntent);
        });

        // 공지사항 버튼 초기화 및 클릭 리스너 추가
        noticeButton.setOnClickListener(v -> {
            Intent noticeIntent = new Intent(MypageActivity.this, NoticeActivity.class);
            startActivity(noticeIntent);
        });

        // 개인정보 처리방침 버튼 초기화 및 클릭 리스너 추가
        privacyPolicyButton.setOnClickListener(v -> {
            Intent privacyPolicyIntent = new Intent(MypageActivity.this, PrivacyPolicyActivity.class);
            startActivity(privacyPolicyIntent);
        });

        // 이용약관 버튼 초기화 및 클릭 리스너 추가
        termsOfServiceButton.setOnClickListener(v -> {
            Intent termsIntent = new Intent(MypageActivity.this, TermsOfServiceActivity.class);
            startActivity(termsIntent);
        });

        // 비회원 로그인 여부 확인
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String guestId = sharedPreferences.getString("guestId", null);

        if (guestId != null) {
            isGuestUser = true;  // 비회원 로그인 상태
            disableContactUsButton();  // 비회원일 때 문의하기 버튼 비활성화
        } else {
            isGuestUser = false;  // 회원 로그인 상태
            // 문의하기 버튼 클릭 리스너 추가 (비회원이 아닐 경우에만 활성화)
            contactUsButton.setOnClickListener(v -> {
                Intent contactIntent = new Intent(MypageActivity.this, QnaActivity.class);
                startActivity(contactIntent);
            });
        }

        profileImage.setOnClickListener(v -> openGallery());

        // 닉네임 클릭 리스너 - 다이얼로그로 변경
        nicknameTextView.setOnClickListener(v -> showEditDialog("닉네임 변경", nicknameTextView));

        // 공통 하단 네비게이션 설정
        setupFooterNavigation();

        // SharedPreferences에서 사용자 정보 로드
        loadUserInfo();
    }

    // 비회원일 때 문의하기 버튼 비활성화
    private void disableContactUsButton() {
        contactUsButton.setEnabled(false);  // 버튼 비활성화
        contactUsButton.setAlpha(0.5f);  // 버튼을 회색으로 표시 (비활성화된 느낌)
        contactUsButton.setOnClickListener(v -> {
            Toast.makeText(MypageActivity.this, "비회원은 문의하기 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", null);
        String guestId = sharedPreferences.getString("guestId", null);

        // 사용자 이름이나 guestId가 있는 경우 닉네임으로 설정
        if (guestId != null) {
            nicknameTextView.setText(guestId);  // guestId 표시
        } else if (userName != null) {
            nicknameTextView.setText(userName);  // 로그인된 사용자 이름 표시
        } else {
            nicknameTextView.setText("Unknown User");  // 기본값
        }
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
        if (textViewToEdit != null) {
            EditText input = new EditText(MypageActivity.this);
            input.setText(textViewToEdit.getText());

            new AlertDialog.Builder(MypageActivity.this)
                    .setTitle(title)
                    .setView(input)
                    .setPositiveButton("확인", (dialog, which) -> {
                        String newText = input.getText().toString();
                        if (!newText.isEmpty()) {
                            textViewToEdit.setText(newText);
                            saveNickname(newText);
                            Toast.makeText(MypageActivity.this, title + "이(가) 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MypageActivity.this, "입력 값이 비어 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        } else {
            Toast.makeText(MypageActivity.this, "잘못된 항목입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNickname(String nickname) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", nickname);
        editor.apply();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userName");
        editor.remove("guestId");
        editor.apply();

        Toast.makeText(MypageActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
