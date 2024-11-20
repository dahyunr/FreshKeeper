package com.example.freshkeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        TextView commenterName; // 닉네임 추가
        ImageView commenterIcon; // 아이콘 추가
        ImageView likeButton;

        CommentViewHolder(View itemView) {
            super(itemView);
            commentContent = itemView.findViewById(R.id.commentContent);
            commenterName = itemView.findViewById(R.id.commenterName); // 닉네임 뷰 초기화
            commenterIcon = itemView.findViewById(R.id.commenterIcon); // 아이콘 뷰 초기화
            likeButton = itemView.findViewById(R.id.likeButton);
            likeButton.setOnClickListener(this::onLikeClick);
        }

        void bind(Comment comment) {
            // 댓글 내용 설정
            commentContent.setText(comment.getContent());

            // 닉네임 설정
            commenterName.setText(comment.getCommenterName() != null ? comment.getCommenterName() : "익명 사용자");

            // 아이콘 설정 (Glide로 이미지 로드)
            if (comment.getCommenterIcon() != null && !comment.getCommenterIcon().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(comment.getCommenterIcon())
                        .into(commenterIcon);
            } else {
                commenterIcon.setImageResource(R.drawable.fk_mmm); // 기본 아이콘 설정
            }

            // 좋아요 버튼 설정
            likeButton.setImageResource(comment.getLikeCount() > 0 ? R.drawable.fk_heartfff : R.drawable.fk_heart);
        }

        private void onLikeClick(View view) {
            Comment comment = commentList.get(getAdapterPosition());
            int newLikeCount = comment.getLikeCount() + 1;
            comment.setLikeCount(newLikeCount);
            likeButton.setImageResource(R.drawable.fk_heartfff); // 좋아요가 눌렸을 때 이미지 변경
            notifyItemChanged(getAdapterPosition()); // 좋아요 버튼 클릭 후 갱신된 데이터 반영
        }
    }
}
