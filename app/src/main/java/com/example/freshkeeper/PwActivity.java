package com.example.freshkeeper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.freshkeeper.database.DatabaseHelper;

import java.util.Random;

public class PwActivity extends BaseActivity {

    private EditText emailField;
    private EditText phoneField;
    private EditText verificationCodeField;
    private EditText newPasswordField;
    private EditText confirmPasswordField;
    private Button sendCodeButton;
    private Button verifyButton;
    private Button saveButton;

    private String generatedCode;
    private boolean isCodeVerified = false;  // 인증 성공 여부를 저장하는 변수 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw);

        // 권한 체크
        checkSmsPermission();

        // View 초기화
        emailField = findViewById(R.id.email);
        phoneField = findViewById(R.id.phone);
        verificationCodeField = findViewById(R.id.verification_code);
        newPasswordField = findViewById(R.id.new_password);
        confirmPasswordField = findViewById(R.id.confirm_new_password);
        sendCodeButton = findViewById(R.id.send_code);
        verifyButton = findViewById(R.id.verify);
        saveButton = findViewById(R.id.save_button);

        // 코드 전송 버튼 클릭 리스너
        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePhone()) {
                    generatedCode = generateVerificationCode();
                    sendSms(phoneField.getText().toString(), generatedCode);
                    Toast.makeText(PwActivity.this, "인증번호가 전송되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 인증하기 버튼 클릭 리스너
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = verificationCodeField.getText().toString();
                if (TextUtils.isEmpty(enteredCode)) {
                    Toast.makeText(PwActivity.this, "인증코드를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (enteredCode.equals(generatedCode)) {
                    isCodeVerified = true;  // 인증 성공 시 플래그를 true로 설정
                    Toast.makeText(PwActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    isCodeVerified = false;  // 인증 실패 시 플래그를 false로 유지
                    Toast.makeText(PwActivity.this, "인증코드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 저장 버튼 클릭 리스너
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    if (isCodeVerified) {  // 인증이 완료되었을 경우에만 비밀번호 변경을 허용
                        String email = emailField.getText().toString();
                        String newPassword = newPasswordField.getText().toString();
                        updatePassword(email, newPassword);

                        Toast.makeText(PwActivity.this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();

                        // 로그인 화면으로 이동하는 Intent 추가
                        Intent intent = new Intent(PwActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // 현재 액티비티를 종료하여 뒤로가기 시 비밀번호 화면으로 돌아가지 않게 함
                    } else {
                        Toast.makeText(PwActivity.this, "인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // 런타임 권한 요청 메서드
    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }

    // 전화번호 유효성 검사
    private boolean validatePhone() {
        String phone = phoneField.getText().toString();
        if (TextUtils.isEmpty((phone))) {
            Toast.makeText(this, "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // 입력된 정보 유효성 검사
    private boolean validateInputs() {
        String email = emailField.getText().toString();
        String newPassword = newPasswordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "새 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999 사이의 숫자
        return String.valueOf(code);
    }

    // SMS 전송 메서드
    private void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS 전송 완료!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS 전송 실패, 다시 시도하세요.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // 비밀번호 업데이트 메서드 추가
    private void updatePassword(String email, String newPassword) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.updateUserPassword(email, newPassword);  // DatabaseHelper 클래스에 있는 메서드를 호출
    }
}