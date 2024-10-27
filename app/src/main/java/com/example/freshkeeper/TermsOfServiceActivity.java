package com.example.freshkeeper;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TermsOfServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        // 뒤로가기 버튼 설정
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed()); // 뒤로가기 동작

        // 이용약관 내용 설정
        TextView termsOfServiceContent = findViewById(R.id.terms_of_service_content);
        termsOfServiceContent.setText("1. 목적\n"
                + "이 이용약관(이하 \"약관\")은 Fresh Keeper(이하 \"서비스\" 또는 \"앱\")가 제공하는 서비스의 이용과 관련하여, 서비스와 사용자 간의 권리, 의무 및 책임 사항을 규정함을 목적으로 합니다.\n\n"
                + "2. 정의\n"
                + "\"서비스\"란 Fresh Keeper가 제공하는 모든 관련 기능과 정보를 말합니다.\n"
                + "\"사용자\"란 본 약관에 동의하고, Fresh Keeper를 통해 서비스를 이용하는 회원 및 비회원을 의미합니다.\n"
                + "\"회원\"은 서비스에 계정을 생성하고, 지속적으로 서비스를 이용할 수 있는 자를 말합니다.\n"
                + "\"비회원\"은 계정 생성 없이 서비스를 이용하는 자를 의미합니다.\n\n"
                + "3. 약관의 효력 및 변경\n"
                + "본 약관은 사용자가 서비스에 접속하거나 이용함으로써 그 효력이 발생합니다.\n"
                + "서비스는 필요 시 약관을 변경할 수 있으며, 변경된 약관은 앱 내 공지사항을 통해 사전에 공지됩니다. 사용자는 변경된 약관을 확인할 의무가 있으며, 공지된 후 서비스에 접속하거나 이용하면 변경된 약관에 동의한 것으로 간주됩니다.\n\n"
                + "4. 서비스 이용\n"
                + "사용자는 본 약관에 따라 서비스를 이용할 수 있습니다. 앱은 사용자의 개인정보를 포함한 모든 데이터의 안전성을 보장하기 위해 최선을 다합니다.\n"
                + "서비스 이용 시간은 특별한 사유가 없는 한 24시간 운영되며, 앱은 서비스 제공을 위하여 정기적인 점검, 업데이트, 서버 관리 등의 이유로 서비스 제공을 일시 중단할 수 있습니다.\n\n"
                + "5. 회원가입 및 계정 관리\n"
                + "사용자는 Fresh Keeper의 서비스 이용을 위해 회원가입을 해야 하며, 회원가입은 실명 및 실제 정보를 기초로 하여야 합니다.\n"
                + "회원은 자신의 계정 정보를 관리할 책임이 있으며, 이를 제3자에게 양도하거나 대여할 수 없습니다.\n\n"
                + "6. 사용자의 의무\n"
                + "사용자는 다음의 행위를 하지 않으며, 이를 위반할 경우 서비스 이용이 제한될 수 있습니다:\n"
                + "- 다른 사용자 또는 제3자의 권리 침해, 명예 훼손, 사생활 침해 등 부적절한 행위\n"
                + "- 서비스의 정상적인 운영을 방해하는 행위\n\n"
                + "7. 서비스 제공의 중단 및 종료\n"
                + "서비스는 다음과 같은 사유가 발생할 경우, 서비스 제공을 일시적 또는 영구적으로 중단할 수 있습니다.");
    }
}
