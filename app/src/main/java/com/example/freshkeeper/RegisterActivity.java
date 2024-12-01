package com.example.freshkeeper;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.freshkeeper.database.DatabaseHelper;

public class RegisterActivity extends BaseActivity {

    EditText editTextName, editTextEmail, editTextPhone, editTextPassword, editTextPasswordConfirm;
    Button buttonRegister;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // DatabaseHelper 초기화
        dbHelper = new DatabaseHelper(this);

        // EditText 및 Button 요소 찾기
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        buttonRegister = findViewById(R.id.buttonRegister);

        // 회원가입 버튼 클릭 시 이벤트 처리
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 사용자 입력 가져오기
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String passwordConfirm = editTextPasswordConfirm.getText().toString().trim();

                // 간단한 유효성 검사 (빈 필드 및 비밀번호 일치 확인)
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(passwordConfirm)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                } else {
                    // 이메일 중복 검사
                    if (dbHelper.isEmailExists(email)) {
                        Toast.makeText(RegisterActivity.this, "이미 존재하는 이메일입니다", Toast.LENGTH_SHORT).show();
                    } else {
                        // 사용자 데이터베이스에 저장
                        long result = dbHelper.insertUser(name, email, password, phone);

                        if (result > 0) {
                            // 회원가입 성공 메시지
                            Toast.makeText(RegisterActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();

                            // 회원가입 성공 후 로그인 화면으로 이동
                            finish();
                        } else {
                            // 회원가입 실패 시 메시지
                            Toast.makeText(RegisterActivity.this, "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}