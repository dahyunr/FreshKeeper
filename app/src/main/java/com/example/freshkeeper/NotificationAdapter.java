package com.example.freshkeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> notificationList;
    private Context context;

    public NotificationAdapter(Context context, List<NotificationItem> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notificationList.get(position);
        holder.title.setText(notification.getTitle());
        holder.content.setText(notification.getContent());
        holder.time.setText(notification.getTime());

        // 알림 타입에 따라 다른 아이콘 또는 UI로 표시
        switch (notification.getType()) {
            case "like":
                holder.icon.setImageResource(R.drawable.fk_heart); // 기존 좋아요 아이콘 사용
                break;
            case "comment":
                holder.icon.setImageResource(R.drawable.fk_chat); // 기존 댓글 아이콘 사용
                break;
        }

        // 알림 항목 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            Intent intent = null;
            switch (notification.getType()) {
                case "like":
                case "comment":
                    // 댓글이나 좋아요 알림의 경우, 게시물로 이동
                    intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", notification.getPostId()); // 게시물 ID 전달
                    break;
            }
            if (intent != null) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, time;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvNotificationTitle);
            content = itemView.findViewById(R.id.tvNotificationContent);
            time = itemView.findViewById(R.id.tvNotificationTime);
            icon = itemView.findViewById(R.id.ivNotificationIcon);
        }
    }
}
