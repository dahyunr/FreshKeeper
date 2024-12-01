package com.example.freshkeeper;

public class InquiryItem {
    private String category;
    private String email;
    private String content;
    private String response;
    private boolean isAnswered;

    public InquiryItem(String category, String email, String content, String response, boolean isAnswered) {
        this.category = category;
        this.email = email;
        this.content = content;
        this.response = response;
        this.isAnswered = isAnswered;
    }

    // getter와 setter 메소드 추가
    public String getCategory() {
        return category;
    }

    public String getEmail() {
        return email;
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