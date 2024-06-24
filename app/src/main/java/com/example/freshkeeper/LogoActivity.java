package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // 로고 페이지가 보여지고 일정 시간 후에 로그인 페이지로 넘어갑니다.
        // 예를 들어, 3초 후에 로그인 페이지로 이동하도록 설정합니다.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // LogoActivity에서 LoginActivity로 전환합니다.
                Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                startActivity(intent);
                // LogoActivity를 종료합니다.
                finish();
            }
        }, 3000); // 3초 후에 실행됩니다.
    }
}
