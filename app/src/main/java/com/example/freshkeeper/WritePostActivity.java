package com.example.freshkeeper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WritePostActivity extends BaseActivity {

    private EditText titleEditText, contentEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        // UI 컴포넌트 초기화
        titleEditText = findViewById(R.id.title_input);  // XML의 id와 일치하도록 수정
        contentEditText = findViewById(R.id.content_input);  // XML의 id와 일치하도록 수정
        saveButton = findViewById(R.id.submit_button);  // XML의 id와 일치하도록 수정

        // 작성하기 버튼 클릭 시
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (title.isEmpty()) {
                titleEditText.setError("제목을 입력하세요.");
                return;
            }

            if (content.isEmpty()) {
                contentEditText.setError("내용을 입력하세요.");
                return;
            }

            // 게시글 저장 처리 (여기서는 예시로 토스트 메시지)
            savePost(title, content);
        });
    }

    // 게시글 저장 처리 메서드
    private void savePost(String title, String content) {
        // 서버에 데이터 전송 혹은 DB 저장 작업 등
        // 예시로 토스트 메시지로 작성 완료 알림
        Toast.makeText(this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
