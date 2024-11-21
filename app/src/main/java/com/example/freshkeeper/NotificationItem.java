package com.example.freshkeeper;

public class NotificationItem {
    private String title;
    private String content;
    private String time;
    private String type;
    private String postId; // 추가


    public NotificationItem(String title, String content, String time, String type, String postId) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.type = type;
        this.postId = postId; // 추가
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getPostId() { // 추가
        return postId;
    }
}
