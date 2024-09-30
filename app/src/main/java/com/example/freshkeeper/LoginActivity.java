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

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGuestLogin = findViewById(R.id.buttonGuestLogin);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    int loginResult = login(email, password);
                    switch (loginResult) {
                        case 1:
                            saveLoginInfo(email, password); // 로그인 성공 시 정보 저장
                            Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
                            startActivity(intent);
                            finish(); // 로그인 액티비티 종료
                            break;
                        case -1:
                            Toast.makeText(LoginActivity.this, "이메일을 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                            break;
                        case -2:
                            Toast.makeText(LoginActivity.this, "비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "로그인 실패. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

        buttonGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int randomNumber = random.nextInt(100001);
                String guestId = "guest" + randomNumber;
                Toast.makeText(LoginActivity.this, guestId + "으로 로그인합니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, FkmainActivity.class);
                startActivity(intent);
                finish(); // 로그인 액티비티 종료
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PwActivity.class);
                startActivity(intent);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // 로그인 정보 저장 메서드
    private void saveLoginInfo(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.putBoolean("isLoggedIn", true);  // 로그인 상태 저장
        editor.apply();
    }

    // 로그인 검증 메서드
    private int login(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String registeredEmail = sharedPreferences.getString("Email", "");
        String registeredPassword = sharedPreferences.getString("Password", "");

        if (!email.equals(registeredEmail)) {
            return -1;
        } else if (!password.equals(registeredPassword)) {
            return -2;
        } else {
            return 1;
        }
    }
}
