package com.example.freshkeeper;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // 뒤로 가기 버튼 설정
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 개인정보 처리방침 내용 설정
        TextView privacyPolicyText = findViewById(R.id.privacy_policy_text);
        privacyPolicyText.setText("Fresh keeper는 개인정보 보호법을 준수하여 사용자의 개인정보를 보호하기 위해 최선을 다하고 있습니다. 본 방침은 사용자가 제공한 개인정보가 어떻게 처리되는지에 대해 설명합니다.\n\n" +
                "1. 수집하는 개인정보 항목\n- 사용자 이름, 이메일 주소, 전화번호\n- 사용 기록 및 로그 데이터\n\n" +
                "2. 개인정보 수집 목적\n- 사용자 인증 및 서비스 제공\n- 앱 사용 통계 및 분석을 통한 앱 개선\n\n" +
                "3. 개인정보 보관 기간\n- 계정 삭제 시 즉시 삭제\n- 사용 통계 데이터는 익명화하여 영구 보관\n\n" +
                "4. 개인정보 제3자 제공\n본 앱은 수집된 개인정보를 제3자에게 제공하지 않습니다.\n\n" +
                "5. 개인정보 보호 조치\n사용자의 개인정보는 암호화되어 안전하게 저장됩니다.\n\n" +
                "6. 사용자의 권리\n사용자는 개인정보의 열람, 수정, 삭제를 요청할 수 있습니다.\n\n" +
                "7. 문의\n개인정보 관련 문의는 문의하기를 통해 연락 주시기 바랍니다.");
    }
}
