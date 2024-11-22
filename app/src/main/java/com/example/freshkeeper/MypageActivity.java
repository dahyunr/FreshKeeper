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
    private TextView contactUsButton;  // 문의하기 버튼
    private TextView faqTextView;      // 자주 묻는 질문 버튼
    private TextView noticeButton;    // 공지사항 버튼
    private TextView logoutTextView;  // 로그아웃 버튼
    private TextView privacyPolicyButton;  // 개인정보 처리방침 버튼
    private TextView termsOfServiceButton; // 이용약관 버튼
    private DatabaseHelper dbHelper;
    private boolean isGuestUser;  // 비회원 여부 체크 변수

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

        // 로그아웃 클릭 리스너
        logoutTextView.setOnClickListener(v -> logout());

        // "자주 묻는 질문" 클릭 리스너
        faqTextView.setOnClickListener(v -> {
            Intent faqIntent = new Intent(MypageActivity.this, FAQActivity.class);
            startActivity(faqIntent);
        });

        // 공지사항 클릭 리스너
        noticeButton.setOnClickListener(v -> {
            Intent noticeIntent = new Intent(MypageActivity.this, NoticeActivity.class);
            startActivity(noticeIntent);
        });

        // 개인정보 처리방침 클릭 리스너
        privacyPolicyButton.setOnClickListener(v -> {
            Intent privacyPolicyIntent = new Intent(MypageActivity.this, PrivacyPolicyActivity.class);
            startActivity(privacyPolicyIntent);
        });

        // 이용약관 클릭 리스너
        termsOfServiceButton.setOnClickListener(v -> {
            Intent termsIntent = new Intent(MypageActivity.this, TermsOfServiceActivity.class);
            startActivity(termsIntent);
        });

        // 비회원 로그인 여부 확인 및 설정
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String guestId = sharedPreferences.getString("guestId", null);

        if (guestId != null) {
            isGuestUser = true;
            disableContactUsButton();
        } else {
            isGuestUser = false;
            contactUsButton.setOnClickListener(v -> {
                Intent contactIntent = new Intent(MypageActivity.this, QnaActivity.class);
                startActivity(contactIntent);
            });
        }

        profileImage.setOnClickListener(v -> openGallery());

        // 닉네임 클릭 리스너
        nicknameTextView.setOnClickListener(v -> showEditDialog("닉네임 변경", nicknameTextView));

        // 공통 하단 네비게이션 설정
        setupFooterNavigation();

        // 사용자 정보 로드
        loadUserInfo();
    }

    private void disableContactUsButton() {
        contactUsButton.setEnabled(false);
        contactUsButton.setAlpha(0.5f);
        contactUsButton.setOnClickListener(v -> {
            Toast.makeText(MypageActivity.this, "비회원은 문의하기 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", null);
        String guestId = sharedPreferences.getString("guestId", null);

        // 디버그 로그
        Log.d("SharedPreferences", "userName: " + userName);
        Log.d("SharedPreferences", "guestId: " + guestId);

        if (guestId != null) {
            nicknameTextView.setText(guestId);
        } else if (userName != null) {
            nicknameTextView.setText(userName);
        } else {
            nicknameTextView.setText("Unknown User");
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

                            // 닉네임 변경 시 SharedPreferences와 DB 업데이트
                            saveNickname(newText);
                            dbHelper.updateUserName(getCurrentUserEmail(), newText);
                            Toast.makeText(MypageActivity.this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MypageActivity.this, "입력 값이 비어 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        }
    }

    private String getCurrentUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", null);
    }

    private void saveNickname(String nickname) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", nickname);
        editor.apply();

        String userEmail = sharedPreferences.getString("userEmail", null);
        if (userEmail != null) {
            dbHelper.updateUserNameByEmail(userEmail, nickname);
        }
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
