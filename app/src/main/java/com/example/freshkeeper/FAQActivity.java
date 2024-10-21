package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.freshkeeper.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAQActivity extends BaseActivity {

    ExpandableListView faqListView;
    FAQAdapter faqAdapter;
    List<String> faqQuestions;
    HashMap<String, List<String>> faqAnswers;
    private DatabaseHelper dbHelper;  // DatabaseHelper 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        faqListView = findViewById(R.id.faq_list);

        // DatabaseHelper 초기화
        dbHelper = new DatabaseHelper(this);

        initFAQData();

        faqAdapter = new FAQAdapter(this, faqQuestions, faqAnswers);
        faqListView.setAdapter(faqAdapter);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(FAQActivity.this, MypageActivity.class);
            startActivity(intent);
            finish();
        });

        faqListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (faqQuestions.get(groupPosition).equals("회원 탈퇴를 하고 싶어요.") && childPosition == 0) {
                showWithdrawalDialog();
                return true;
            }
            return false;
        });
    }

    private void initFAQData() {
        faqQuestions = new ArrayList<>();
        faqAnswers = new HashMap<>();

        faqQuestions.add("식품류만 등록 가능한가요?");
        faqQuestions.add("유통기한 알림 시간 설정을 바꾸고 싶어요.");
        faqQuestions.add("회원 탈퇴를 하고 싶어요.");
        faqQuestions.add("큐알코드로 상품을 등록할 수 있나요?");

        List<String> answer1 = new ArrayList<>();
        answer1.add("아니요, 다양한 종류의 제품을 등록할 수 있습니다.");

        List<String> answer2 = new ArrayList<>();
        answer2.add("알림 설정에서 시간을 변경할 수 있습니다.");

        List<String> answer3 = new ArrayList<>();
        answer3.add("회원 탈퇴는 FAQ에서 처리할 수 있습니다.");

        List<String> answer4 = new ArrayList<>();
        answer4.add("네, 큐알코드로 상품을 등록할 수 있습니다.");

        faqAnswers.put(faqQuestions.get(0), answer1);
        faqAnswers.put(faqQuestions.get(1), answer2);
        faqAnswers.put(faqQuestions.get(2), answer3);
        faqAnswers.put(faqQuestions.get(3), answer4);
    }

    // 회원 탈퇴 다이얼로그를 호출하는 메서드 - public으로 변경
    public void showWithdrawalDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.activity_dialog_withdrawal, null);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("확인", (dialog, which) -> {
                    // 회원 탈퇴 처리
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    String userEmail = sharedPreferences.getString("userEmail", null);

                    if (userEmail != null) {
                        boolean isDeleted = dbHelper.deleteUserByEmail(userEmail);
                        if (isDeleted) {
                            Toast.makeText(this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "회원 탈퇴 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // 로그인 상태를 false로 설정 (로그아웃 처리)
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();

                    // LoginActivity로 이동하고 기존 액티비티 스택을 모두 지우기
                    Intent intent = new Intent(FAQActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // 기존 스택 제거
                    Log.d("FAQActivity", "LoginActivity로 이동하는 인텐트 실행 중");
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
