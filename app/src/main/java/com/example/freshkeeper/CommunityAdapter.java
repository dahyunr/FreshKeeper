package com.example.freshkeeper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        } else {
            postList.clear();
            postList.addAll(updatedList);
        }
        notifyDataSetChanged(); // RecyclerView 전체 갱신
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
            // 제목 및 내용 기본값 처리
            titleTextView.setText(post.getTitle() != null && !post.getTitle().trim().isEmpty() ? post.getTitle() : "제목 없음");
            contentTextView.setText(post.getContent() != null && !post.getContent().trim().isEmpty() ? post.getContent() : "내용 없음");

            // 좋아요 및 댓글 수 기본값 처리
            likeCountTextView.setText(String.valueOf(Math.max(post.getLikeCount(), 0)));
            commentCountTextView.setText(String.valueOf(Math.max(post.getCommentCount(), 0)));

            // 이미지 로드 처리
            Glide.with(context).clear(imageView);
            String firstImageUri = post.getFirstImageUri();
            if (firstImageUri != null && !firstImageUri.trim().isEmpty()) {
                imageView.setVisibility(View.VISIBLE); // 이미지가 있으면 보이게 처리
                Glide.with(context)
                        .load(Uri.parse(firstImageUri))
                        .placeholder(R.drawable.fk_mmm)
                        .error(R.drawable.fk_mmm)
                        .fitCenter() // 이미지 크기 조정
                        .into(imageView);
            } else {
                imageView.setVisibility(View.GONE); // 이미지가 없으면 숨김 처리
            }

            // 좋아요 상태에 따른 아이콘 설정
            heartIcon.setImageResource(post.isLiked() ? R.drawable.fk_heartfff : R.drawable.fk_heart);
        }


        private void onItemClick() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onItemClickListener != null && position < postList.size()) {
                Log.d("CommunityAdapter", "Item clicked. Position: " + position + ", Title: " + postList.get(position).getTitle());
                onItemClickListener.onItemClick(postList.get(position));
            } else {
                Log.e("CommunityAdapter", "Invalid item click. Position: " + position);
            }
        }

        private void onHeartClick(View view) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || position >= postList.size()) {
                Log.e("CommunityAdapter", "Invalid heart click. Position: " + position);
                return;
            }
            CommunityPost post = postList.get(position);
            boolean isLiked = !post.isLiked();
            post.setLiked(isLiked);
            post.setLikeCount(isLiked ? post.getLikeCount() + 1 : Math.max(post.getLikeCount() - 1, 0));
            notifyItemChanged(position);

            Log.d("CommunityAdapter", "Heart icon clicked. Post ID: " + post.getId() + ", Liked: " + isLiked);
        }
    }
}
