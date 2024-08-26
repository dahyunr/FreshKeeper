package com.example.freshkeeper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private FoodItemAdapter mAdapter;
    private Paint mPaint;
    private Context mContext;

    public SwipeToDeleteCallback(FoodItemAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT);
        mAdapter = adapter;
        mContext = context;
        mPaint = new Paint();
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.removeItem(position);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        float height = (float) itemView.getBottom() - (float) itemView.getTop();
        float width = height / 3;

        // 배경 색상 설정
        mPaint.setColor(Color.parseColor("#BDBDBD"));
        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
        c.drawRect(background, mPaint);

        // 휴지통 아이콘 그리기
        Drawable deleteIcon = ContextCompat.getDrawable(mContext, R.drawable.fk_delete);
        int iconTop = itemView.getTop() + ((itemView.getBottom() - itemView.getTop()) - (int) width) / 2;
        int iconMargin = (int) ((height - width) / 2);
        int iconLeft = itemView.getRight() - iconMargin - (int) width;
        int iconRight = itemView.getRight() - iconMargin;
        int iconBottom = iconTop + (int) width;

        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        deleteIcon.draw(c);
    }
}
