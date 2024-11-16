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

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private List<CommunityPost> postList;
    private Context context;
    private OnItemClickListener onItemClickListener; // Listener for item clicks

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public CommunityAdapter(Context context, List<CommunityPost> postList) {
        this.context = context;
        this.postList = postList;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView likeCountTextView;
        TextView commentCountTextView;
        ImageView imageView;
        ImageView heartIcon;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            imageView = itemView.findViewById(R.id.imageView);
            heartIcon = itemView.findViewById(R.id.heartIcon);

            // Set click listener for the entire item view
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });

            // Set click listener for the heart icon
            heartIcon.setOnClickListener(this::onHeartClick);
        }

        public void bind(CommunityPost post) {
            titleTextView.setText(post.getTitle());
            contentTextView.setText(post.getContent());
            likeCountTextView.setText(String.valueOf(post.getLikeCount()));
            commentCountTextView.setText(String.valueOf(post.getCommentCount()));

            // Load first image using Glide (updated to handle multiple images)
            String firstImageUri = post.getFirstImageUri();
            if (firstImageUri != null && !firstImageUri.isEmpty()) {
                Glide.with(context).load(firstImageUri).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.default_image); // Default placeholder image
            }

            // Set heart icon based on like status
            heartIcon.setImageResource(post.isLiked() ? R.drawable.fk_heartfff : R.drawable.fk_heart);
        }

        public void onHeartClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CommunityPost post = postList.get(position);

                // Toggle like status
                post.setLiked(!post.isLiked());
                post.setLikeCount(post.isLiked() ? post.getLikeCount() + 1 : post.getLikeCount() - 1);

                // Update UI
                likeCountTextView.setText(String.valueOf(post.getLikeCount()));
                heartIcon.setImageResource(post.isLiked() ? R.drawable.fk_heartfff : R.drawable.fk_heart);
            }
        }
    }
}
