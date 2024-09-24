package com.example.freshkeeper;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {

    private List<FoodItem> foodItems;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FoodItemAdapter(Context context, List<FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodItemViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        FoodItem currentItem = foodItems.get(position);

        holder.nameTextView.setText(currentItem.getName());
        holder.regDateTextView.setText("등록일: " + currentItem.getRegDate());
        holder.expDateTextView.setText("유통기한: " + currentItem.getExpDate());

        // 이미지 URI 설정
        if (currentItem.getImagePath() != null && !currentItem.getImagePath().isEmpty()) {
            Uri imageUri = Uri.parse(currentItem.getImagePath());
            holder.imageView.setImageURI(imageUri);
        } else {
            holder.imageView.setImageResource(R.drawable.fk_gallery); // 기본 이미지
        }

        // D-Day 계산
        String expDate = currentItem.getExpDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date expirationDate = dateFormat.parse(expDate);
            Date today = new Date();
            if (expirationDate == null) {
                holder.countdownTextView.setText("D-??");
                return;
            }

            long diffInMillis = expirationDate.getTime() - today.getTime();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (daysLeft == 0) {
                holder.countdownTextView.setText("D-day");
                holder.countdownTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            } else if (daysLeft > 0) {
                holder.countdownTextView.setText("D-" + daysLeft);
                holder.countdownTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
            } else {
                holder.countdownTextView.setText("D+" + Math.abs(daysLeft));
                holder.countdownTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public void updateList(List<FoodItem> newList) {
        foodItems = newList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        foodItems.remove(position);
        notifyItemRemoved(position);
    }

    public class FoodItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;
        public TextView regDateTextView;
        public TextView expDateTextView;
        public TextView countdownTextView;

        public FoodItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            nameTextView = itemView.findViewById(R.id.item_name);
            regDateTextView = itemView.findViewById(R.id.item_reg_date);
            expDateTextView = itemView.findViewById(R.id.item_exp_date);
            countdownTextView = itemView.findViewById(R.id.item_countdown);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
