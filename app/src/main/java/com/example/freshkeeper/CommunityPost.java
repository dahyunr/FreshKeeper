package com.example.freshkeeper;

public class CommunityPost {
    private String title;
    private String content;
    private int imageResId;
    private int likeCount;
    private int commentCount;

    public CommunityPost(String title, String content, int imageResId, int likeCount, int commentCount) {
        this.title = title;
        this.content = content;
        this.imageResId = imageResId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getImageResId() {
        return imageResId;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
