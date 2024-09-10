package com.example.freshkeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

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
        answerTextView.setText(answer);
        answerTextView.setTextSize(16);
        answerTextView.setPadding(80, 15, 15, 15);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
