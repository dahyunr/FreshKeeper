package com.example.freshkeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentContent;
        ImageView likeButton;

        CommentViewHolder(View itemView) {
            super(itemView);
            commentContent = itemView.findViewById(R.id.commentContent);
            likeButton = itemView.findViewById(R.id.likeButton);
            likeButton.setOnClickListener(this::onLikeClick);
        }

        void bind(Comment comment) {
            commentContent.setText(comment.getContent());
            likeButton.setImageResource(comment.getLikeCount() > 0 ? R.drawable.fk_heartfff : R.drawable.fk_heart);
        }

        private void onLikeClick(View view) {
            Comment comment = commentList.get(getAdapterPosition());
            int newLikeCount = comment.getLikeCount() + 1;
            comment.setLikeCount(newLikeCount);
            likeButton.setImageResource(R.drawable.fk_heartfff);
            notifyItemChanged(getAdapterPosition());
        }
    }
}
