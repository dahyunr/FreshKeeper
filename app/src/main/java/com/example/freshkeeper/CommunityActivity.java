package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshkeeper.database.DatabaseHelper;

import java.util.List;

public class CommunityActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<CommunityPost> postList;
    private DatabaseHelper dbHelper;

    // ActivityResultLauncher를 사용하여 startActivityForResult 대체
    private final ActivityResultLauncher<Intent> writePostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String title = data.getStringExtra("title");
                    String content = data.getStringExtra("content");

                    // 작성된 글을 데이터베이스에 추가
                    dbHelper.addCommunityPost(new CommunityPost(title, content, R.drawable.default_image, 0, 0));

                    // 게시글 리스트 갱신
                    postList = dbHelper.getAllCommunityPosts();
                    communityAdapter.updateData(postList); // updateData 메서드를 통해 데이터 업데이트
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터베이스에서 모든 글 불러오기
        postList = dbHelper.getAllCommunityPosts();
        communityAdapter = new CommunityAdapter(postList);
        recyclerView.setAdapter(communityAdapter);

        // 플로팅 액션 버튼 클릭 시 팝업 메뉴 표시
        ImageView plusButton = findViewById(R.id.plus_button);
        plusButton.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_community, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_write_post) {
                // 글쓰기 화면으로 이동
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                writePostLauncher.launch(intent); // ActivityResultLauncher 사용
                return true;
            } else if (itemId == R.id.menu_my_posts) {
                // 내가 쓴 글 보기 화면으로 이동
                Intent myPostsIntent = new Intent(CommunityActivity.this, MyPostsActivity.class);
                startActivity(myPostsIntent);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }
}
