package com.example.freshkeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InquiryHistoryAdapter extends RecyclerView.Adapter<InquiryHistoryAdapter.ViewHolder> {

    private List<InquiryItem> inquiryList;

    public InquiryHistoryAdapter(List<InquiryItem> inquiryList) {
        this.inquiryList = inquiryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inquiry_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InquiryItem inquiry = inquiryList.get(position);

        // 문의 유형과 내용을 설정
        holder.categoryTextView.setText(inquiry.getCategory());
        holder.contentTextView.setText(inquiry.getContent());

        // 상태에 따라 statusTextView에 텍스트 설정
        holder.statusTextView.setText(inquiry.isAnswered() ? "답변 완료" : "답변 예정");
    }

    @Override
    public int getItemCount() {
        return inquiryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        TextView contentTextView;
        TextView statusTextView; // 답변 상태 텍스트뷰

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.category_text);
            contentTextView = itemView.findViewById(R.id.content_text);
            statusTextView = itemView.findViewById(R.id.status_text); // 답변 상태 텍스트뷰
        }
    }
}
