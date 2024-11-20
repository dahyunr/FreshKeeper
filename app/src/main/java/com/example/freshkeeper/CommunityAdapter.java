package com.example.freshkeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private List<CommunityPost> postList;
    private Context context;
    private OnItemClickListener onItemClickListener; // Listener for item clicks
    private Set<String> postTitlesSet; // Set to track unique post titles

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(CommunityPost post); // CommunityPost 객체 전달
    }

    // Method to set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public CommunityAdapter(Context context, List<CommunityPost> postList) {
        this.context = context;
        this.postList = postList;
        this.postTitlesSet = new HashSet<>(); // Initialize the set
        // Add existing post titles to the set to prevent duplicates on future additions
        for (CommunityPost post : postList) {
            postTitlesSet.add(post.getTitle());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityPost post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Method to add a new post to the list
    public void addNewPost(CommunityPost newPost) {
        if (!postTitlesSet.contains(newPost.getTitle())) {
            postList.add(0, newPost); // Add to the top of the list
            postTitlesSet.add(newPost.getTitle()); // Add the post title to the set
            notifyItemInserted(0); // Notify the adapter to update the RecyclerView
        } else {
            // Handle the case where the post already exists (Optional: Show a Toast or log)
        }
    }

    // Method to update the list with a filtered list
    public void updateList(List<CommunityPost> filteredList) {
        postList = filteredList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView likeCountTextView;
        TextView commentCountTextView;
        ImageView imageView;
        ImageView heartIcon;
        ImageView authorIcon; // 작성자 아이콘 추가

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            imageView = itemView.findViewById(R.id.imageView);
            heartIcon = itemView.findViewById(R.id.heartIcon);
            authorIcon = itemView.findViewById(R.id.postAuthorIcon); // 작성자 아이콘 뷰 초기화

            // Set click listener for the entire item view
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommunityPost selectedPost = postList.get(position);
                        onItemClickListener.onItemClick(selectedPost); // CommunityPost 전달
                    }
                }
            });

            // Set click listener for the heart icon
            heartIcon.setOnClickListener(this::onHeartClick);
        }

        public void bind(CommunityPost post) {
            if (titleTextView != null) {
                titleTextView.setText(post.getTitle());
            }
            if (contentTextView != null) {
                contentTextView.setText(post.getContent());
            }
            if (likeCountTextView != null) {
                likeCountTextView.setText(String.valueOf(post.getLikeCount()));
            }
            if (commentCountTextView != null) {
                commentCountTextView.setText(String.valueOf(post.getCommentCount()));
            }

            // 작성자 아이콘 설정
            if (authorIcon != null) {
                Glide.with(context)
                        .load(post.getAuthorIcon())  // 작성자 아이콘을 Glide로 로드
                        .into(authorIcon);
            }

            // 이미지가 있을 경우에만 표시
            String firstImageUri = post.getFirstImageUri();
            if (imageView != null) {
                if (firstImageUri != null && !firstImageUri.isEmpty()) {
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(context).load(firstImageUri).into(imageView);
                } else {
                    imageView.setVisibility(View.GONE); // 이미지가 없을 경우 숨김
                }
            }

            // 좋아요 아이콘 설정
            if (heartIcon != null) {
                heartIcon.setImageResource(post.isLiked() ? R.drawable.fk_heartfff : R.drawable.fk_heart);
            }
        }

        public void onHeartClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CommunityPost post = postList.get(position);

                // Toggle like status
                post.setLiked(!post.isLiked());
                post.setLikeCount(post.isLiked() ? post.getLikeCount() + 1 : post.getLikeCount() - 1);

                // Update UI
                if (likeCountTextView != null) {
                    likeCountTextView.setText(String.valueOf(post.getLikeCount()));
                }
                if (heartIcon != null) {
                    heartIcon.setImageResource(post.isLiked() ? R.drawable.fk_heartfff : R.drawable.fk_heart);
                }
            }
        }
    }
}
