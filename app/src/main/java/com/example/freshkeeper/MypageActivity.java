package com.example.freshkeeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MypageActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private TextView nicknameTextView, emailTextView;
    private ImageView iconRef, iconCalendar, iconBarcode, iconMypage;
    private TextView withdrawalTextView, notificationSettingsTextView;
    private TextView contactUsButton;  // 문의하기 버튼 변수 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);  // your mypage.xml 파일과 연결

        // UI 요소들 초기화
        profileImage = findViewById(R.id.profile_image);
        nicknameTextView = findViewById(R.id.profile_nickname);
        emailTextView = findViewById(R.id.profile_email);
        iconRef = findViewById(R.id.icon_ref);
        iconCalendar = findViewById(R.id.icon_calendar);
        iconBarcode = findViewById(R.id.icon_barcode);
        iconMypage = findViewById(R.id.icon_mypage);
        withdrawalTextView = findViewById(R.id.withdrawal);  // 회원탈퇴 텍스트뷰
        notificationSettingsTextView = findViewById(R.id.notification_settings); // 알림 설정 텍스트뷰

        // 문의하기 버튼 초기화 및 클릭 리스너 추가
        contactUsButton = findViewById(R.id.button_contact_us);
        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 문의하기 페이지로 이동하는 인텐트 생성 및 시작
                Intent intent = new Intent(MypageActivity.this, QnaActivity.class);
                startActivity(intent);
            }
        });

        // 프로필 이미지 클릭 리스너
        profileImage.setOnClickListener(v -> openGallery());

        // 닉네임 클릭 리스너 - 다이얼로그로 변경
        nicknameTextView.setOnClickListener(v -> {
            showEditDialog("닉네임 변경", nicknameTextView);
        });

        // 이메일 클릭 리스너 - 다이얼로그로 변경
        emailTextView.setOnClickListener(v -> {
            showEditDialog("이메일 변경", emailTextView);
        });

        // 하단바 아이콘 클릭 리스너
        iconRef.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, FkmainActivity.class);
            startActivity(intent);
        });

        iconCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        iconBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, BarcodeScanActivity.class);
            startActivity(intent);
        });

        iconMypage.setOnClickListener(v -> Toast.makeText(this, "이미 마이페이지에 있습니다", Toast.LENGTH_SHORT).show());

        // 회원탈퇴 텍스트뷰 클릭 리스너 추가
        withdrawalTextView.setOnClickListener(v -> showWithdrawalDialog());

        // 알림 설정 텍스트뷰 클릭 리스너 추가
        notificationSettingsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, NotificationSettingsActivity.class);
            startActivity(intent);
        });
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

    // 탈퇴 확인 다이얼로그 표시 메서드
    private void showWithdrawalDialog() {
        new AlertDialog.Builder(this)
                .setTitle("정말 탈퇴하시겠습니까?")
                .setMessage("탈퇴 처리 후 모든 데이터가 삭제되며, 언제라도 다시 가입 가능합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 탈퇴 로직 추가
                        Toast.makeText(MypageActivity.this, "탈퇴가 처리되었습니다.", Toast.LENGTH_SHORT).show();
                        // 실제 탈퇴 처리를 위한 로직을 여기에 추가하십시오.
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // 닉네임 또는 이메일 변경을 위한 다이얼로그 표시 메서드
    private void showEditDialog(String title, TextView textViewToEdit) {
        EditText input = new EditText(this);
        input.setText(textViewToEdit.getText());

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textViewToEdit.setText(input.getText().toString());
                        Toast.makeText(MypageActivity.this, title + "이(가) 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
