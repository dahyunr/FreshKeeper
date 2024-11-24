package com.example.freshkeeper;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private ArrayList<Uri> imageUris;
    private Context context;
    private OnImageRemoveListener onImageRemoveListener;

    public interface OnImageRemoveListener {
        void onImageRemove(Uri uri);
    }

    public ImageAdapter(ArrayList<Uri> imageUris, OnImageRemoveListener onImageRemoveListener) {
        this.imageUris = imageUris;
        this.onImageRemoveListener = onImageRemoveListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        context = parent.getContext();
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = imageUris.get(position);

        // Glide로 이미지 로드
        Glide.with(context)
                .load(uri)
                .into(holder.imageView);

        // 삭제 버튼 클릭 리스너
        holder.removeButton.setOnClickListener(v -> {
            if (onImageRemoveListener != null) {
                onImageRemoveListener.onImageRemove(uri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, removeButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}