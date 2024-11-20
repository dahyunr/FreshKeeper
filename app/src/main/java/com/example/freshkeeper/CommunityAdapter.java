package com.example.freshkeeper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freshkeeper.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private List<CommunityPost> postList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private Set<String> postTitlesSet;

    public interface OnItemClickListener {
        void onItemClick(CommunityPost post);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public CommunityAdapter(Context context, List<CommunityPost> postList) {
        this.context = context;
        this.postList = postList;
        this.postTitlesSet = new HashSet<>();
        if (postList != null) {
            for (CommunityPost post : postList) {
                postTitlesSet.add(post.getTitle());
            }
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
        if (postList == null || position >= postList.size()) {
            return;
        }
        CommunityPost post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public void addNewPost(CommunityPost newPost) {
        if (!postTitlesSet.contains(newPost.getTitle())) {
            postList.add(0, newPost);
            postTitlesSet.add(newPost.getTitle());
            notifyItemInserted(0);
        }
    }

    public void updateList(List<CommunityPost> filteredList) {
        postList = filteredList;
        notifyDataSetChanged();
    }

    public void updateCommentCount(int postId, int newCommentCount) {
        for (int i = 0; i < postList.size(); i++) {
            CommunityPost post = postList.get(i);
            if (post.getId() == postId) {
                post.setCommentCount(newCommentCount);
                notifyItemChanged(i);
                break;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, likeCountTextView, commentCountTextView;
        ImageView imageView, heartIcon, authorIcon;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            imageView = itemView.findViewById(R.id.imageView);
            heartIcon = itemView.findViewById(R.id.heartIcon);
            authorIcon = itemView.findViewById(R.id.postAuthorIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < postList.size()) {
                    CommunityPost selectedPost = postList.get(position);
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(selectedPost);
                    }
                }
            });

            heartIcon.setOnClickListener(this::onHeartClick);
        }

        public void bind(CommunityPost post) {
            titleTextView.setText(post.getTitle() != null ? post.getTitle() : "제목 없음");
            contentTextView.setText(post.getContent() != null ? post.getContent() : "내용 없음");
            likeCountTextView.setText(String.valueOf(post.getLikeCount()));
            commentCountTextView.setText(String.valueOf(post.getCommentCount()));

            // Post Image 처리
            Glide.with(context).clear(imageView); // 이전 이미지 정리
            String firstImageUri = post.getFirstImageUri();
            if (firstImageUri != null && !firstImageUri.isEmpty()) {
                imageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(firstImageUri)
                        .error(R.drawable.fk_mmm) // 에러 시 기본 이미지
                        .into(imageView);
            } else {
                imageView.setVisibility(View.GONE); // 이미지가 없으면 숨김
            }

            // 좋아요 상태에 따라 아이콘 설정
            heartIcon.setImageResource(post.isLiked() ? R.drawable.fk_heartfff : R.drawable.fk_heart);
        }




        public void onHeartClick(View view) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || position >= postList.size()) {
                return;
            }
            CommunityPost post = postList.get(position);
            post.setLiked(!post.isLiked());
            post.setLikeCount(post.isLiked() ? post.getLikeCount() + 1 : post.getLikeCount() - 1);
            likeCountTextView.setText(String.valueOf(post.getLikeCount()));
            heartIcon.setImageResource(post.isLiked() ? R.drawable.fk_heartfff : R.drawable.fk_heart);
        }

    }
}
