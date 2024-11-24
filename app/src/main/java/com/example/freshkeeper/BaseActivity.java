package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // 상태바와 네비게이션 바 숨기기
        hideSystemBars();
    }

    // 상태바와 네비게이션 바 숨기기
    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            getWindow().getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsetsController controller = v.getWindowInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
                return insets;
            });
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    // 하단 네비게이션 바 클릭 리스너 설정
    protected void setupFooterNavigation() {
        // 하단바 아이콘 초기화
        ImageView iconRef = findViewById(R.id.icon_ref);
        ImageView iconCalendar = findViewById(R.id.icon_calendar);
        ImageView iconBarcode = findViewById(R.id.icon_barcode);
        ImageView iconCommunity = findViewById(R.id.icon_community);
        ImageView iconMypage = findViewById(R.id.icon_mypage);

        if (iconRef != null) {
            iconRef.setOnClickListener(view -> navigateToActivity(FkmainActivity.class));
        }

        if (iconCalendar != null) {
            iconCalendar.setOnClickListener(view -> navigateToActivity(CalendarActivity.class));
        }

        if (iconBarcode != null) {
            iconBarcode.setOnClickListener(view -> navigateToActivity(BarcodeScanActivity.class));
        }

        if (iconCommunity != null) {
            iconCommunity.setOnClickListener(view -> {
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                boolean isGuest = sharedPreferences.getBoolean("isGuest", false);

                if (isLoggedIn && !isGuest) {
                    // 회원만 접근 가능
                    navigateToActivity(CommunityActivity.class);
                } else {
                    // 비회원 및 게스트 로그인 차단
                    showLoginRequiredMessage();
                }
            });
        }

        if (iconMypage != null) {
            iconMypage.setOnClickListener(view -> {
                if (!(this instanceof MypageActivity)) {
                    navigateToActivity(MypageActivity.class);
                } else {
                    Toast.makeText(this, "이미 마이페이지에 있습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 커뮤니티 접근 제한 시 메시지
    private void showLoginRequiredMessage() {
        Toast.makeText(this, "회원만 이용 가능한 서비스입니다. 로그인 후 이용해주세요.", Toast.LENGTH_LONG).show();
    }

    // 액티비티 전환 메소드
    protected void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // 사용자 닉네임 가져오기
    protected String getUserNickname() {
        if (sharedPreferences == null) {
            return "익명 사용자";
        }
        return sharedPreferences.getString("userName", "익명 사용자");
    }

    // 사용자 아이콘 가져오기
    protected String getUserIcon() {
        if (sharedPreferences == null) {
            return "fk_mmm";
        }
        return sharedPreferences.getString("userIcon", "fk_mmm");
    }
}