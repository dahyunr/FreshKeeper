package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends BaseActivity {

    private EditText editName, editRegDate, editExpDate, editMemo;
    private ImageView itemImage;
    private int itemPosition;
    private FoodItem item;

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
            itemImage.setImageResource(item.getImageResource());
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
