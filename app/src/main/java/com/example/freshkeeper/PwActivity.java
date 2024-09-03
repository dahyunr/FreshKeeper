package com.example.freshkeeper;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class PwActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText phoneField;
    private EditText verificationCodeField;
    private EditText newPasswordField;
    private EditText confirmPasswordField;
    private Button sendCodeButton;
    private Button verifyButton;
    private Button saveButton;

    private String generatedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw);

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
                    Toast.makeText(PwActivity.this, "인증번호: " + generatedCode, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(PwActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PwActivity.this, "인증코드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 저장 버튼 클릭 리스너
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // 비밀번호를 저장하는 로직 구현 (예: 서버에 저장)
                    Toast.makeText(PwActivity.this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 전화번호 유효성 검사
    private boolean validatePhone() {
        String phone = phoneField.getText().toString();
        if (TextUtils.isEmpty(phone)) { // "콜"을 "phone"으로 변경
            Toast.makeText(this, "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 추가적인 전화번호 형식 검사를 여기서 할 수 있습니다.
        // 예를 들어, 전화번호가 특정 형식을 따르는지 확인할 수 있습니다.

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
}
