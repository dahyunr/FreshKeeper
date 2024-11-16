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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshkeeper.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunityActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<CommunityPost> postList;
    private DatabaseHelper dbHelper;

    // ActivityResultLauncher for WritePostActivity
    private final ActivityResultLauncher<Intent> writePostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String title = data.getStringExtra("title");
                    String content = data.getStringExtra("content");
                    String imageUri = data.getStringExtra("imageUri");
                    String userId = data.getStringExtra("userId");

                    // Convert imageUri to List<String> for CommunityPost
                    List<String> imageUris = imageUri != null
                            ? Collections.singletonList(imageUri)
                            : new ArrayList<>();

                    // Create a new CommunityPost and save to the database
                    CommunityPost newPost = new CommunityPost(title, content, imageUris, userId, 0, 0);
                    dbHelper.addCommunityPost(newPost);

                    // Add the new post to the list and update RecyclerView
                    postList.add(0, newPost); // Add to the top of the list
                    communityAdapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0); // Scroll to the top to show the new post
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all posts from the database
        postList = dbHelper.getAllCommunityPosts();

        // Initialize CommunityAdapter and set it to RecyclerView
        communityAdapter = new CommunityAdapter(this, postList);
        recyclerView.setAdapter(communityAdapter);

        // Set item click listener for RecyclerView
        communityAdapter.setOnItemClickListener(position -> {
            CommunityPost selectedPost = postList.get(position);
            Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);
            intent.putExtra("postId", selectedPost.getId()); // Use getId() method to pass postId
            startActivity(intent);
        });

        // Set up the Plus button with a popup menu
        ImageView plusButton = findViewById(R.id.plus_button);
        plusButton.setOnClickListener(this::showPopupMenu);
    }

    // Method to show a popup menu on Plus button click
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_community, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_write_post) {
                // Open WritePostActivity
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                writePostLauncher.launch(intent);
                return true;
            } else if (itemId == R.id.menu_my_posts) {
                // Open MyPostsActivity
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
