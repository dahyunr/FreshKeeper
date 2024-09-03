package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

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

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: 시작");

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
                    Log.d(TAG, "onClick: 로그인 시도 - 이메일: " + email);
                    // 로그인 처리 로직
                    int loginResult = login(email, password);
                    switch (loginResult) {
                        case 1:
                            Log.d(TAG, "onClick: 로그인 성공");
                            Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
                            startActivity(intent);
                            finish(); // 로그인 액티비티 종료
                            break;
                        case -1:
                            Log.d(TAG, "onClick: 이메일 불일치");
                            Toast.makeText(LoginActivity.this, "이메일을 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                            break;
                        case -2:
                            Log.d(TAG, "onClick: 비밀번호 불일치");
                            Toast.makeText(LoginActivity.this, "비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Log.d(TAG, "onClick: 로그인 실패");
                            Toast.makeText(LoginActivity.this, "로그인 실패. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

        buttonGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비회원 로그인 버튼 클릭 시 동작하는 코드
                Random random = new Random();
                int randomNumber = random.nextInt(100001); // 0부터 100000까지의 랜덤 숫자 생성
                String guestId = "guest" + randomNumber;
                Toast.makeText(LoginActivity.this, guestId + "으로 로그인합니다", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: 비회원 로그인 - guestId: " + guestId);
                Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
                startActivity(intent);
                finish(); // 로그인 액티비티 종료
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호 찾기 버튼 클릭 시 PwActivity로 이동
                Intent intent = new Intent(LoginActivity.this, PwActivity.class);
                startActivity(intent);
                Log.d(TAG, "onClick: 비밀번호 찾기 이동");
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 버튼 클릭 시 동작하는 코드
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                Log.d(TAG, "onClick: 회원가입 이동");
            }
        });

        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Google 로그인 버튼 클릭 시 동작하는 코드
                Toast.makeText(LoginActivity.this, "Google 로그인 기능을 구현하세요", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: Google 로그인");
            }
        });

        buttonKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kakao 로그인 버튼 클릭 시 동작하는 코드
                Toast.makeText(LoginActivity.this, "Kakao 로그인 기능을 구현하세요", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: Kakao 로그인");
            }
        });

        buttonNaverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naver 로그인 버튼 클릭 시 동작하는 코드
                Toast.makeText(LoginActivity.this, "Naver 로그인 기능을 구현하세요", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: Naver 로그인");
            }
        });
    }

    // 실제 로그인 처리 로직
    private int login(String email, String password) {
        // 저장된 사용자 데이터 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String registeredEmail = sharedPreferences.getString("Email", "");
        String registeredPassword = sharedPreferences.getString("Password", "");

        // 이메일과 비밀번호 확인
        if (!email.equals(registeredEmail)) {
            return -1; // 이메일 불일치
        } else if (!password.equals(registeredPassword)) {
            return -2; // 비밀번호 불일치
        } else {
            return 1; // 로그인 성공
        }
    }
}
