package com.example.freshkeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Set<Integer> likedComments; // 좋아요 상태를 저장하는 Set (댓글 ID 기준)
    private OnCommentInteractionListener interactionListener; // 좋아요 이벤트 리스너

    // 생성자
    public CommentAdapter(List<Comment> commentList, OnCommentInteractionListener listener) {
        this.commentList = commentList;
        this.likedComments = new HashSet<>(); // 좋아요 상태를 저장
        this.interactionListener = listener; // 인터랙션 리스너 초기화
    }

    // 댓글 리스트 업데이트 메서드 (DiffUtil 사용)
    public void updateCommentList(List<Comment> newCommentList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return commentList.size();
            }

            @Override
            public int getNewListSize() {
                return newCommentList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return commentList.get(oldItemPosition).getId() == newCommentList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Comment oldComment = commentList.get(oldItemPosition);
                Comment newComment = newCommentList.get(newItemPosition);
                return oldComment.equals(newComment); // equals()를 사용하여 내용 비교
            }
        });

        this.commentList.clear();
        this.commentList.addAll(newCommentList);
        diffResult.dispatchUpdatesTo(this); // RecyclerView 업데이트
    }

    // ViewHolder 생성
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    // ViewHolder 바인딩
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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

        // 댓글 데이터 바인딩
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
            if (likedComments.contains(comment.getId())) {
                likeButton.setImageResource(R.drawable.fk_heartfff); // 채워진 하트
            } else {
                likeButton.setImageResource(R.drawable.fk_heart); // 빈 하트
            }
            commentLikeCount.setText(String.valueOf(comment.getLikeCount()));
        }

        private void onLikeClick(View view) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            Comment comment = commentList.get(position);

            if (likedComments.contains(comment.getId())) {
                // 좋아요 취소
                likedComments.remove(comment.getId());
                comment.setLikeCount(comment.getLikeCount() - 1); // 좋아요 수 감소
                likeButton.setImageResource(R.drawable.fk_heart); // 빈 하트로 변경
            } else {
                // 좋아요 추가
                likedComments.add(comment.getId());
                comment.setLikeCount(comment.getLikeCount() + 1); // 좋아요 수 증가
                likeButton.setImageResource(R.drawable.fk_heartfff); // 채워진 하트로 변경
            }

            // 좋아요 수 갱신
            commentLikeCount.setText(String.valueOf(comment.getLikeCount()));

            // 상호작용 리스너 호출
            if (interactionListener != null) {
                interactionListener.onLikeClicked(comment);
            }
        }
    }

    // 댓글 상호작용 리스너 인터페이스
    public interface OnCommentInteractionListener {
        void onLikeClicked(Comment comment); // 좋아요 클릭 이벤트
    }
}
