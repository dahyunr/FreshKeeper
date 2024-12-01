package com.example.freshkeeper;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;

public class FAQAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> faqQuestions;
    private HashMap<String, List<String>> faqAnswers;

    public FAQAdapter(Context context, List<String> faqQuestions, HashMap<String, List<String>> faqAnswers) {
        this.context = context;
        this.faqQuestions = faqQuestions;
        this.faqAnswers = faqAnswers;
    }

    @Override
    public int getGroupCount() {
        return faqQuestions.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return faqAnswers.get(faqQuestions.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return faqQuestions.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return faqAnswers.get(faqQuestions.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String question = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        }
        TextView questionTextView = convertView.findViewById(android.R.id.text1);
        questionTextView.setText(question);
        questionTextView.setTextSize(18);
        questionTextView.setPadding(60, 20, 20, 20);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String answer = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_2, null);
        }

        TextView answerTextView = convertView.findViewById(android.R.id.text1);

        // "회원 탈퇴" 텍스트에 스타일 적용
        if (answer.contains("회원 탈퇴")) {
            SpannableString spannableString = new SpannableString(answer);

            // 파란색으로 텍스트 색 변경
            ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(Color.BLUE);
            spannableString.setSpan(blueColorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // "회원 탈퇴" 부분에만 적용

            // 기울임 효과 추가
            StyleSpan italicSpan = new StyleSpan(android.graphics.Typeface.ITALIC);
            spannableString.setSpan(italicSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 클릭 이벤트 추가
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    // FAQActivity의 showWithdrawalDialog()를 호출하여 다이얼로그 표시
                    if (context instanceof FAQActivity) {
                        ((FAQActivity) context).showWithdrawalDialog();
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);  // 밑줄 추가
                }
            };
            spannableString.setSpan(clickableSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            answerTextView.setText(spannableString);
            answerTextView.setMovementMethod(LinkMovementMethod.getInstance());  // 클릭 가능한 텍스트 설정
        } else {
            answerTextView.setText(answer);
        }

        answerTextView.setTextSize(16);
        answerTextView.setPadding(80, 15, 15, 15);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}