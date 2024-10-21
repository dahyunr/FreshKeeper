package com.example.freshkeeper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EditItemActivity extends BaseActivity {

    private EditText editName, editRegDate, editExpDate, editMemo;
    private ImageView itemImage;
    private int itemPosition;
    private FoodItem item;
    private String imagePath;  // 이미지 경로 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);  // 수정된 레이아웃 사용

        // UI 요소 초기화
        itemImage = findViewById(R.id.item_image);
        editName = findViewById(R.id.item_name);
        editRegDate = findViewById(R.id.item_reg_date);
        editExpDate = findViewById(R.id.item_exp_date);
        editMemo = findViewById(R.id.item_memo);

        // 전달된 데이터 가져오기
        Intent intent = getIntent();
        itemPosition = intent.getIntExtra("itemPosition", -1);
        item = (FoodItem) intent.getSerializableExtra("itemData");

        if (item != null) {
            imagePath = item.getImagePath();  // 이미지 경로 설정
            if (imagePath != null && !imagePath.isEmpty()) {
                Glide.with(this).load(Uri.parse(imagePath)).into(itemImage);  // 이미지 로드
            } else {
                itemImage.setImageResource(item.getImageResource());  // 기본 이미지 설정
            }
            editName.setText(item.getName());
            editRegDate.setText(item.getRegDate());
            editExpDate.setText(item.getExpDate());
            editMemo.setText(item.getMemo());
        }

        // 저장 버튼 클릭 리스너 설정
        findViewById(R.id.save_button).setOnClickListener(v -> saveItem());

        // 취소 버튼 클릭 리스너 설정
        findViewById(R.id.cancel_button).setOnClickListener(v -> cancelEdit());
    }

    private void saveItem() {
        // 수정된 내용 저장
        item.setName(editName.getText().toString());
        item.setRegDate(editRegDate.getText().toString());
        item.setExpDate(editExpDate.getText().toString());
        item.setMemo(editMemo.getText().toString());
        item.setImagePath(imagePath);  // 이미지 경로 유지

        // 수정된 데이터 반환
        Intent resultIntent = new Intent();
        resultIntent.putExtra("itemPosition", itemPosition);
        resultIntent.putExtra("updatedItem", item);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void cancelEdit() {
        // 취소 시, 아무런 결과도 반환하지 않고 현재 Activity 종료
        setResult(RESULT_CANCELED);
        finish();
    }
}
