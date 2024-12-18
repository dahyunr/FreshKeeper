package com.example.freshkeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private List<CommunityPost> postList;
    private final Context context;

    // 클릭 이벤트 인터페이스
    public interface OnItemClickListener {
        void onItemClick(CommunityPost post);
    }

    private OnItemClickListener onItemClickListener;

    // 클릭 이벤트 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public CommunityAdapter(Context context, List<CommunityPost> postList) {
        this.context = context;
        this.postList = postList != null ? postList : new ArrayList<>();
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
            Log.e("CommunityAdapter", "Invalid position: " + position);
            return;
        }
        CommunityPost post = postList.get(position);
        Log.d("CommunityAdapter", "Binding Post - ID: " + post.getId() + ", Title: " + post.getTitle());
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public void updateData(List<CommunityPost> updatedList) {
        if (postList == null) {
            postList = new ArrayList<>(updatedList);
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return postList.size();
                }

                @Override
                public int getNewListSize() {
                    return updatedList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return postList.get(oldItemPosition).getId() == updatedList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return postList.get(oldItemPosition).equals(updatedList.get(newItemPosition));
                }
            });
            postList.clear();
            postList.addAll(updatedList);
            diffResult.dispatchUpdatesTo(this);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, likeCountTextView, commentCountTextView;
        ImageView imageView, heartIcon;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            imageView = itemView.findViewById(R.id.imageView);
            heartIcon = itemView.findViewById(R.id.heartIcon);

            // 아이템 전체 클릭 리스너
            itemView.setOnClickListener(v -> onItemClick());

            // 좋아요 버튼 클릭 리스너
            heartIcon.setOnClickListener(this::onHeartClick);
        }

        public void bind(CommunityPost post) {
            titleTextView.setText(post.getTitle() != null ? post.getTitle() : "제목 없음");
            contentTextView.setText(post.getContent() != null ? post.getContent() : "내용 없음");

            likeCountTextView.setText(String.valueOf(Math.max(post.getLikeCount(), 0)));
            commentCountTextView.setText(String.valueOf(Math.max(post.getCommentCount(), 0)));

            Glide.with(context).clear(imageView);
            String firstImageUri = post.getFirstImageUri();
            if (firstImageUri != null && !firstImageUri.trim().isEmpty()) {
                imageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(Uri.parse(firstImageUri))
                        .placeholder(R.drawable.fk_mmm)
                        .error(R.drawable.fk_mmm)
                        .fitCenter()
                        .into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
            }

            // 좋아요 상태 초기화
            SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String currentUserId = prefs.getString("userId", "default_user_id");
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
            boolean isLiked = dbHelper.isPostLikedByUser(post.getId(), currentUserId);

            heartIcon.setImageResource(isLiked ? R.drawable.fk_heartfff : R.drawable.fk_heart);
        }

        private void onItemClick() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onItemClickListener != null && position < postList.size()) {
                CommunityPost clickedPost = postList.get(position);

                // 중복 호출 방지
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(clickedPost);
                }
            }
        }

        private void onHeartClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            CommunityPost post = postList.get(position);

            // 좋아요 상태 토글
            SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String currentUserId = prefs.getString("userId", "default_user_id");

            DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
            boolean isLiked = !dbHelper.isPostLikedByUser(post.getId(), currentUserId);
            dbHelper.toggleLikeStatus(post.getId(), currentUserId);

            // UI 업데이트
            post.setLiked(isLiked);
            post.setLikeCount(isLiked ? post.getLikeCount() + 1 : post.getLikeCount() - 1);
            notifyItemChanged(position);
        }
    }
}