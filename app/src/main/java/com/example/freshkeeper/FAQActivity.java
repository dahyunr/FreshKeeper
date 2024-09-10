package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    ExpandableListView faqListView;
    FAQAdapter faqAdapter;
    List<String> faqQuestions;
    HashMap<String, List<String>> faqAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        // ExpandableListView를 XML에서 찾아서 초기화
        faqListView = findViewById(R.id.faq_list);

        // FAQ 데이터 초기화
        initFAQData();

        // 어댑터 설정
        faqAdapter = new FAQAdapter(this, faqQuestions, faqAnswers);
        faqListView.setAdapter(faqAdapter);

        // TabLayout 초기화 및 탭 추가
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("전체"));
        tabLayout.addTab(tabLayout.newTab().setText("냉장고"));
        tabLayout.addTab(tabLayout.newTab().setText("계정"));
        tabLayout.addTab(tabLayout.newTab().setText("서비스"));

        // 뒤로 가기 버튼 설정
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // 마이페이지로 돌아가는 인텐트
            Intent intent = new Intent(FAQActivity.this, MypageActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티를 종료하여 돌아가는 동작처럼 보이도록 함
        });
    }

    // FAQ 질문과 답변 데이터를 초기화하는 메서드
    private void initFAQData() {
        faqQuestions = new ArrayList<>();
        faqAnswers = new HashMap<>();

        // 질문 추가
        faqQuestions.add("식품류만 등록 가능한가요?");
        faqQuestions.add("유통기한 알림 시간 설정을 바꾸고 싶어요.");
        faqQuestions.add("회원 탈퇴를 하고 싶어요.");
        faqQuestions.add("큐알코드로 상품을 등록할 수 있나요?");

        // 각 질문에 대한 답변 추가
        List<String> answer1 = new ArrayList<>();
        answer1.add("아니요, 다양한 종류의 제품을 등록할 수 있습니다.");

        List<String> answer2 = new ArrayList<>();
        answer2.add("알림 설정에서 시간을 변경할 수 있습니다.");

        List<String> answer3 = new ArrayList<>();
        answer3.add("회원 탈퇴는 설정 메뉴에서 가능합니다.");

        List<String> answer4 = new ArrayList<>();
        answer4.add("네, 큐알코드로 상품을 등록할 수 있습니다.");

        // 질문과 답변을 연결
        faqAnswers.put(faqQuestions.get(0), answer1);
        faqAnswers.put(faqQuestions.get(1), answer2);
        faqAnswers.put(faqQuestions.get(2), answer3);
        faqAnswers.put(faqQuestions.get(3), answer4);
    }
}
