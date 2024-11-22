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
        buttonLogin.setOnClickListener(v -> handleLogin(sharedPreferences));

        // 회원가입 버튼 클릭 시 처리
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 비회원 로그인 버튼 처리
        buttonGuestLogin.setOnClickListener(v -> handleGuestLogin(sharedPreferences));

        // 비밀번호 찾기 버튼 클릭 시 처리
        buttonForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PwActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin(SharedPreferences sharedPreferences) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        } else {
            boolean isAuthenticated = dbHelper.authenticateUser(email, password);

            if (isAuthenticated) {
                // 데이터베이스에서 닉네임 가져오기
                String userName = dbHelper.getUserNameByEmail(email);

                // SharedPreferences에 사용자 정보 저장
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", userName != null ? userName : "익명 사용자"); // 닉네임 저장
                editor.putString("userEmail", email);
                editor.putBoolean("isLoggedIn", true);
                editor.putBoolean("isGuest", false); // 비회원 상태 아님
                editor.apply();

                Log.d(TAG, "userName: " + userName);
                Log.d(TAG, "userEmail: " + email);

                // 메인 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleGuestLogin(SharedPreferences sharedPreferences) {
        Random random = new Random();
        int randomNumber = random.nextInt(100001);
        String guestId = "guest" + randomNumber;

        Toast.makeText(LoginActivity.this, guestId + "으로 비회원 로그인합니다.", Toast.LENGTH_SHORT).show();

        // SharedPreferences에 비회원 정보 저장
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("guestId", guestId);
        editor.putString("userName", guestId); // 비회원 ID를 닉네임처럼 사용
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("isGuest", true); // 비회원 상태
        editor.apply();

        // 메인 화면으로 이동
        Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
        intent.putExtra("GUEST_ID", guestId);
        startActivity(intent);
        finish();
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
