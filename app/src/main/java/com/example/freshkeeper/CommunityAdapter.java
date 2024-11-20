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

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private List<CommunityPost> postList;
    private Context context;

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

    public void updateList(List<CommunityPost> updatedList) {
        postList = updatedList;
        notifyDataSetChanged(); // RecyclerView 데이터 업데이트
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
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(postList.get(position));
                }
            });

            // 좋아요 버튼 클릭 리스너
            heartIcon.setOnClickListener(this::onHeartClick);
        }

        public void bind(CommunityPost post) {
            titleTextView.setText(post.getTitle() != null ? post.getTitle() : "제목 없음");
            contentTextView.setText(post.getContent() != null ? post.getContent() : "내용 없음");
            likeCountTextView.setText(String.valueOf(post.getLikeCount()));
            commentCountTextView.setText(String.valueOf(post.getCommentCount()));

            // 이미지 로드 처리
            Glide.with(context).clear(imageView);
            String firstImageUri = post.getFirstImageUri();
            if (firstImageUri != null && !firstImageUri.isEmpty()) {
                imageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(Uri.parse(firstImageUri))
                        .placeholder(R.drawable.fk_mmm)
                        .error(R.drawable.fk_mmm)
                        .into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
            }

            // 좋아요 상태에 따른 아이콘 설정
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
