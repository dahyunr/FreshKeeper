package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView buttonGuestLogin;
    private TextView buttonForgotPassword;
    private TextView buttonRegister;
    private Button buttonGoogleLogin;
    private Button buttonKakaoLogin;
    private Button buttonNaverLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGuestLogin = findViewById(R.id.buttonGuestLogin);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        buttonKakaoLogin = findViewById(R.id.buttonKakaoLogin);
        buttonNaverLogin = findViewById(R.id.buttonNaverLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 버튼 클릭 시 동작하는 코드
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    // 로그인 처리 로직
                }
            }
        });

        buttonGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비회원 로그인 버튼 클릭 시 동작하는 코드
                Toast.makeText(LoginActivity.this, "비회원으로 로그인합니다", Toast.LENGTH_SHORT).show();
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호 찾기 버튼 클릭 시 동작하는 코드
                Toast.makeText(LoginActivity.this, "비밀번호 찾기 기능을 구현하세요", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 버튼 클릭 시 동작하는 코드
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Google 로그인 버튼 클릭 시 동작하는 코드
                Toast.makeText(LoginActivity.this, "Google 로그인 기능을 구현하세요", Toast.LENGTH_SHORT).show();
            }
        });

        buttonKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kakao 로그인 버튼 클릭 시 동작하는 코드
                Toast.makeText(LoginActivity.this, "Kakao 로그인 기능을 구현하세요", Toast.LENGTH_SHORT).show();
            }
        });

        buttonNaverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naver 로그인 버튼 클릭 시 동작하는 코드
                Toast.makeText(LoginActivity.this, "Naver 로그인 기능을 구현하세요", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
