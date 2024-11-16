package com.example.freshkeeper;

public class Comment {
    private String content;
    private int userId;
    private int postId;
    private int likeCount;

    public Comment(String content, int userId, int postId, int likeCount) {
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.likeCount = likeCount;
    }

    public String getContent() {
        return content;
    }

    public int getUserId() {
        return userId;
    }

    public int getPostId() {
        return postId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
