package com.example.freshkeeper;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QnaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

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
                String inquiryText = inquiryContent.getText().toString();

                if (selectedCategory.equals("문의 유형을 선택하세요")) {
                    Toast.makeText(QnaActivity.this, "문의 유형을 선택하세요.", Toast.LENGTH_SHORT).show();
                } else if (inquiryText.isEmpty()) {
                    Toast.makeText(QnaActivity.this, "문의 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 여기에서 문의 내용을 서버로 전송하거나, 데이터베이스에 저장할 수 있습니다.
                    Toast.makeText(QnaActivity.this, "문의가 성공적으로 제출되었습니다.", Toast.LENGTH_SHORT).show();

                    // 필요 시 입력값 초기화
                    categorySpinner.setSelection(0); // 스피너 초기화
                    inquiryContent.setText("");      // 문의 내용 초기화
                }
            }
        });
    }
}
