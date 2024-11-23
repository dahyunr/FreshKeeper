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
import com.example.freshkeeper.database.DatabaseHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<Comment> commentList;
    private final Set<Integer> likedComments;

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
        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userId", "default_user_id");

        Comment comment = commentList.get(position);
        holder.bind(comment, currentUserId);
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
        void bind(Comment comment, String currentUserId) {
            // AtomicBoolean으로 좋아요 상태 관리
            AtomicBoolean isLiked = new AtomicBoolean(
                    DatabaseHelper.getInstance(itemView.getContext()).isCommentLikedByUser(comment.getId(), currentUserId)
            );

            // 좋아요 버튼 초기 상태 업데이트
            likeButton.setImageResource(isLiked.get() ? R.drawable.fk_heartfff : R.drawable.fk_heart);
            commentLikeCount.setText(String.valueOf(comment.getLikeCount()));

            // 좋아요 버튼 클릭 리스너
            likeButton.setOnClickListener(view -> {
                boolean newIsLiked = !isLiked.get(); // 현재 상태 반전
                DatabaseHelper db = DatabaseHelper.getInstance(view.getContext());

                // 데이터베이스에서 좋아요 상태 업데이트
                db.updateCommentLike(comment.getId(), currentUserId, newIsLiked);

                // 좋아요 상태 및 UI 업데이트
                isLiked.set(newIsLiked); // AtomicBoolean 값 변경
                likeButton.setImageResource(newIsLiked ? R.drawable.fk_heartfff : R.drawable.fk_heart);
                comment.setLikeCount(comment.getLikeCount() + (newIsLiked ? 1 : -1));
                commentLikeCount.setText(String.valueOf(comment.getLikeCount()));

                // 리스너 호출
                if (interactionListener != null) {
                    interactionListener.onLikeClicked(comment);
                }
            });
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

                // 데이터베이스에 반영
                DatabaseHelper.getInstance(view.getContext()).updateLikeCount(comment.getId(), false);
            } else {
                // 좋아요 추가
                likedComments.add(comment.getId());
                comment.setLikeCount(comment.getLikeCount() + 1); // 좋아요 수 증가
                likeButton.setImageResource(R.drawable.fk_heartfff); // 채워진 하트로 변경

                // 데이터베이스에 반영
                DatabaseHelper.getInstance(view.getContext()).updateLikeCount(comment.getId(), true);
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