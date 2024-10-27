package com.example.freshkeeper;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TermsOfServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        // 뒤로 가기 버튼 설정
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 서비스 이용약관 내용 설정
        TextView termsText = findViewById(R.id.terms_text);
        termsText.setText("서비스 이용약관\n\n" +
                "1. 목적\n" +
                "- 본 서비스는 Fresh Keeper의 이용자들에게 냉장고 관리 기능을 제공합니다.\n\n" +
                "2. 이용자 의무\n" +
                "- 이용자는 서비스 이용 시 본 약관을 준수해야 합니다.\n\n" +
                "3. 책임 제한\n" +
                "- Fresh Keeper는 서비스 이용과 관련된 모든 문제에 대해 책임지지 않습니다.\n\n" +
                "4. 서비스 이용 제한\n" +
                "- 서비스는 대한민국 내에서만 이용 가능합니다.\n\n" +
                "5. 변경 사항\n" +
                "- 본 약관은 서비스 운영상의 필요에 따라 변경될 수 있습니다.\n\n" +
                "6. 기타\n" +
                "- 자세한 내용은 고객센터로 문의 바랍니다.");
    }
}
