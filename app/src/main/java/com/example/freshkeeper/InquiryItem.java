package com.example.freshkeeper;

public class InquiryItem {
    private String category;
    private String content;
    private String response;
    private boolean isAnswered;

    public InquiryItem(String category, String content, String response, boolean isAnswered) {
        this.category = category;
        this.content = content;
        this.response = response;
        this.isAnswered = isAnswered;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public String getResponse() {
        return response;
    }

    public boolean isAnswered() {
        return isAnswered;
    }
}
