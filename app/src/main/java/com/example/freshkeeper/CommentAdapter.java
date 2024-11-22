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

    // 생성자
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

    // 댓글 리스트 업데이트 메서드
    public void updateCommentList(List<Comment> newCommentList) {
        this.commentList.clear();
        this.commentList.addAll(newCommentList);
        notifyDataSetChanged();
    }

    // ViewHolder 클래스
    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;          // 댓글 내용
        TextView commenterNameTextView;    // 댓글 작성자 이름
        ImageView commenterIconImageView;  // 댓글 작성자 아이콘
        ImageView likeButton;              // 좋아요 버튼
        TextView commentLikeCount;         // 좋아요 수 표시

        CommentViewHolder(View itemView) {
            super(itemView);

            // View 초기화
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commenterNameTextView = itemView.findViewById(R.id.commenterNameTextView);
            commenterIconImageView = itemView.findViewById(R.id.commenterIconImageView);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentLikeCount = itemView.findViewById(R.id.commentLikeCount);

            // 좋아요 버튼 클릭 리스너 설정
            likeButton.setOnClickListener(this::onLikeClick);
        }

        void bind(Comment comment) {
            // 댓글 내용 설정
            commentTextView.setText(comment.getContent());

            // 댓글 작성자 이름 설정
            commenterNameTextView.setText(comment.getCommenterName() != null ? comment.getCommenterName() : "익명 사용자");

            // 댓글 작성자 아이콘 설정 (Glide 사용)
            if (comment.getCommenterIcon() != null && !comment.getCommenterIcon().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(comment.getCommenterIcon())
                        .placeholder(R.drawable.fk_mmm) // 기본 아이콘
                        .error(R.drawable.fk_mmm)      // 에러 발생 시 기본 아이콘
                        .into(commenterIconImageView);
            } else {
                commenterIconImageView.setImageResource(R.drawable.fk_mmm);
            }

            // 좋아요 버튼 이미지 및 좋아요 수 설정
            likeButton.setImageResource(comment.getLikeCount() > 0 ? R.drawable.fk_heartfff : R.drawable.fk_heart);
            commentLikeCount.setText(String.valueOf(comment.getLikeCount()));
        }

        private void onLikeClick(View view) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            Comment comment = commentList.get(position);
            int newLikeCount = comment.getLikeCount() + 1;
            comment.setLikeCount(newLikeCount);

            // 좋아요 버튼 이미지 변경 및 데이터 업데이트
            likeButton.setImageResource(R.drawable.fk_heartfff);
            notifyItemChanged(position);
        }

        public void addComment(Comment comment) {
            commentList.add(comment);
            notifyItemInserted(commentList.size() - 1);
        }
    }
}
