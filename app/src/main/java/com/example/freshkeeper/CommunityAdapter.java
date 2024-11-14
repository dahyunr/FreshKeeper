package com.example.freshkeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private List<CommunityPost> postList;

    public CommunityAdapter(List<CommunityPost> postList) {
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

    // 데이터 업데이트 메서드
    public void updateData(List<CommunityPost> newPostList) {
        this.postList = newPostList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView likeCountTextView;
        TextView commentCountTextView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        // bind 메서드를 통해 데이터를 설정
        public void bind(CommunityPost post) {
            titleTextView.setText(post.getTitle());
            contentTextView.setText(post.getContent());
            likeCountTextView.setText(String.valueOf(post.getLikeCount()));
            commentCountTextView.setText(String.valueOf(post.getCommentCount()));
            imageView.setImageResource(post.getImageResId());
        }
    }
}
