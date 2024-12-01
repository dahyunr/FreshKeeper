package com.example.freshkeeper;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.freshkeeper.database.DatabaseHelper;

public class QnaActivity extends BaseActivity {

    private DatabaseHelper databaseHelper;
    private EditText emailInput; // 이메일 입력 필드 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        // DatabaseHelper 초기화
        databaseHelper = new DatabaseHelper(this);

        // 이메일 입력 필드 초기화
        emailInput = findViewById(R.id.email_input); // 여기에서 emailInput 초기화

        // 뒤로가기 버튼 설정
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로가기 동작
                onBackPressed();
            }
        });

        // 스피너 설정
        Spinner categorySpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 스피너 아이템 선택 시 동작
                if (position != 0) {
                    String selectedCategory = parent.getItemAtPosition(position).toString();
                    Toast.makeText(QnaActivity.this, selectedCategory + "이(가) 선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때
            }
        });

        // 문의 내용 텍스트 박스 설정
        EditText inquiryContent = findViewById(R.id.inquiry_content);
        inquiryContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 텍스트 박스에 포커스가 갈 때
                }
            }
        });

        // 버튼 클릭 이벤트 설정
        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                String emailText = emailInput.getText().toString().trim(); // 이메일 입력 필드 가져오기
                String inquiryText = inquiryContent.getText().toString();

                if (selectedCategory.equals("문의 유형을 선택하세요")) {
                    Toast.makeText(QnaActivity.this, "문의 유형을 선택하세요.", Toast.LENGTH_SHORT).show();
                } else if (emailText.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    Toast.makeText(QnaActivity.this, "올바른 이메일 주소를 입력하세요.", Toast.LENGTH_SHORT).show(); // 이메일 형식 검사
                } else if (inquiryText.isEmpty()) {
                    Toast.makeText(QnaActivity.this, "문의 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 문의 내용을 데이터베이스에 저장
                    databaseHelper.insertInquiry(selectedCategory, emailText, inquiryText);
                    Toast.makeText(QnaActivity.this, "문의한 내용의 답변은 이메일로 발송됩니다.", Toast.LENGTH_SHORT).show();

                    // 필요 시 입력값 초기화
                    categorySpinner.setSelection(0); // 스피너 초기화
                    emailInput.setText("");           // 이메일 입력 초기화
                    inquiryContent.setText("");      // 문의 내용 초기화

                    // MyPageActivity로 돌아가기
                    Intent intent = new Intent(QnaActivity.this, MypageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // 스택에서 MyPageActivity를 찾으면 해당 액티비티를 사용
                    startActivity(intent);
                    finish(); // QnaActivity 종료
                }
            }
        });

        // "문의하기 내역" 텍스트 설정 및 클릭 이벤트 설정
        TextView inquiryHistoryText = findViewById(R.id.inquiry_history_text);
        inquiryHistoryText.setPaintFlags(inquiryHistoryText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        inquiryHistoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QnaActivity.this, InquiryHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}