package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class WritePostActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                // 입력값이 비어있는지 확인하여 빈 값일 경우 메시지 출력
                if (title.isEmpty()) {
                    titleEditText.setError("제목을 입력하세요.");
                    return;
                }
                if (content.isEmpty()) {
                    contentEditText.setError("내용을 입력하세요.");
                    return;
                }

                // 결과를 Intent에 담아 반환
                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("content", content);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
