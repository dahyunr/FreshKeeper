package com.example.freshkeeper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {

    private List<FoodItem> foodItems;
    private Context context;
    private OnItemClickListener clickListener;
    private OnItemDeleteListener deleteListener;

    public FoodItemAdapter(Context context, List<FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    public String getItemName(int position) {
        if (position >= 0 && position < foodItems.size()) {
            return foodItems.get(position).getName();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position >= 0 && position < foodItems.size()) {
            return foodItems.get(position).getId();
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodItemViewHolder(itemView, clickListener, deleteListener);
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        FoodItem currentItem = foodItems.get(position);

        holder.nameTextView.setText(currentItem.getName());
        holder.regDateTextView.setText("등록일: " + currentItem.getRegDate());
        holder.expDateTextView.setText("유통기한: " + currentItem.getExpDate());

        if (currentItem.getImagePath() != null && !currentItem.getImagePath().isEmpty()) {
            Uri imageUri = Uri.parse(currentItem.getImagePath());
            Glide.with(context).load(imageUri).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.fk_default_image);
        }

        String countdown = calculateDDay(currentItem.getExpDate());
        holder.countdownTextView.setText(countdown);

        if (countdown.equals("D-day") || countdown.startsWith("D+")) {
            holder.countdownTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.countdownTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public void updateList(List<FoodItem> newList) {
        foodItems = newList;
        notifyDataSetChanged();
        Log.d("FoodItemAdapter", "updateList: 새로운 리스트로 업데이트됨, 총 항목 수: " + foodItems.size());
    }

    public void removeItem(int position) {
        foodItems.remove(position);
        notifyItemRemoved(position);
    }

    private String calculateDDay(String expDate) {
        SimpleDateFormat dateFormat8 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        SimpleDateFormat dateFormat6 = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        try {
            Date expirationDate;
            if (expDate.length() == 8) {
                expirationDate = dateFormat8.parse(expDate);
            } else if (expDate.length() == 6) {
                expirationDate = dateFormat6.parse(expDate);
            } else {
                return "D-??";
            }

            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(Calendar.MINUTE, 0);
            todayCalendar.set(Calendar.SECOND, 0);
            todayCalendar.set(Calendar.MILLISECOND, 0);
            Date today = todayCalendar.getTime();

            Calendar expirationCalendar = Calendar.getInstance();
            expirationCalendar.setTime(expirationDate);
            expirationCalendar.set(Calendar.HOUR_OF_DAY, 0);
            expirationCalendar.set(Calendar.MINUTE, 0);
            expirationCalendar.set(Calendar.SECOND, 0);
            expirationCalendar.set(Calendar.MILLISECOND, 0);
            expirationDate = expirationCalendar.getTime();

            long diffInMillis = expirationDate.getTime() - today.getTime();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (daysLeft == 0) {
                return "D-day";
            } else if (daysLeft > 0) {
                return "D-" + daysLeft;
            } else {
                return "D+" + Math.abs(daysLeft);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("FoodItemAdapter", "calculateDDay: 날짜 파싱 오류 - " + e.getMessage());
        }
        return "D-??";
    }

    public class FoodItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;
        public TextView regDateTextView;
        public TextView expDateTextView;
        public TextView countdownTextView;

        public FoodItemViewHolder(View itemView, final OnItemClickListener clickListener, final OnItemDeleteListener deleteListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            nameTextView = itemView.findViewById(R.id.item_name);
            regDateTextView = itemView.findViewById(R.id.item_reg_date);
            expDateTextView = itemView.findViewById(R.id.item_exp_date);
            countdownTextView = itemView.findViewById(R.id.item_countdown);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteListener.onItemDelete(position);
                    }
                }
                return true;
            });
        }
    }
}