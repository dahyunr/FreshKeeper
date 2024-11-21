package com.example.freshkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freshkeeper.database.DatabaseHelper;

import java.util.Random;

public class LoginActivity extends BaseActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView buttonGuestLogin;
    private TextView buttonForgotPassword;
    private TextView buttonRegister;
    private DatabaseHelper dbHelper;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 앱 시작 시 로그인 상태 확인
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // 이미 로그인되어 있으면 메인 화면으로 이동
            Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
            startActivity(intent);
            finish();  // LoginActivity 종료
            return;
        }

        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: 시작");

        dbHelper = new DatabaseHelper(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGuestLogin = findViewById(R.id.buttonGuestLogin);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // 로그인 버튼 클릭 시 처리
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isAuthenticated = dbHelper.authenticateUser(email, password);
                    String userName = dbHelper.getUserNameByEmail(email);

                    if (isAuthenticated) {
                        // 로그인 성공 시 사용자 정보 저장
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", userName);
                        editor.putString("userEmail", email);
                        editor.putBoolean("isLoggedIn", true);
                        editor.putBoolean("isGuest", false);  // 비회원 아님

                        // 비회원 데이터 삭제
                        editor.remove("guestId");

                        editor.apply();

                        // FkmainActivity로 이동
                        Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 회원가입 버튼 클릭 시 처리
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 비회원 로그인 버튼 처리
        buttonGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int randomNumber = random.nextInt(100001);
                String guestId = "guest" + randomNumber;
                Toast.makeText(LoginActivity.this, guestId + "으로 로그인합니다", Toast.LENGTH_SHORT).show();

                // guestId와 isGuest를 SharedPreferences에 저장
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("guestId", guestId);
                editor.putBoolean("isLoggedIn", true);
                editor.putBoolean("isGuest", true);  // 비회원 상태 저장
                editor.apply();

                // FkmainActivity로 이동
                Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
                intent.putExtra("GUEST_ID", guestId);
                startActivity(intent);
                finish();
            }
        });

        // 비밀번호 찾기 버튼 클릭 시 처리
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PwActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 비밀번호 변경 후 돌아왔을 때 로그인 상태 유지 확인
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("userEmail", null);
        if (savedEmail != null) {
            editTextEmail.setText(savedEmail);
        }
    }
}