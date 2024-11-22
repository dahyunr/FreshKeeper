package com.example.freshkeeper;

import java.util.ArrayList;
import java.util.List;

public class CommunityPost {

    private int id; // Post ID
    private String title; // Post title
    private String content; // Post content
    private List<String> imageUris; // List of image URIs
    private String userId; // User ID
    private int likeCount; // Like count
    private int commentCount; // Comment count
    private boolean isLiked; // Liked status
    private String authorName; // Author name
    private String authorIcon; // Author icon path

    // Constructor: Full initialization
    public CommunityPost(int id, String title, String content, List<String> imageUris, String userId,
                         int likeCount, int commentCount, boolean isLiked, String authorName, String authorIcon) {
        this.id = id;
        this.title = title != null ? title : "제목 없음"; // 기본값 처리
        this.content = content != null ? content : "내용 없음"; // 기본값 처리
        this.imageUris = imageUris != null ? imageUris : new ArrayList<>(); // Ensure list is not null
        this.userId = userId != null ? userId : "default_user_id"; // 기본값 처리
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
        this.authorName = authorName != null ? authorName : "익명 사용자"; // Default to anonymous if null
        this.authorIcon = authorIcon != null ? authorIcon : "fk_mmm"; // Default to "fk_mmm" if null
    }

    // Constructor: Simplified initialization
    public CommunityPost(String title, String content, List<String> imageUris, String userId,
                         int likeCount, int commentCount, String authorName, String authorIcon) {
        this(0, title, content, imageUris, userId, likeCount, commentCount, false, authorName, authorIcon);
    }

    // Constructor: Default initialization
    public CommunityPost() {
        this(0, "제목 없음", "내용 없음", new ArrayList<>(), "default_user_id", 0, 0, false, "익명 사용자", "fk_mmm");
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title : "제목 없음"; // 기본값 처리
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content != null ? content : "내용 없음"; // 기본값 처리
    }

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris != null ? imageUris : new ArrayList<>(); // 기본값 처리
    }

    // Get the first valid image URI
    public String getFirstImageUri() {
        if (imageUris != null && !imageUris.isEmpty()) {
            for (String uri : imageUris) {
                if (uri != null && !uri.isEmpty()) {
                    return uri;
                }
            }
        }
        return ""; // 안전한 기본값
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId != null ? userId : "default_user_id"; // 기본값 처리
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    // Increment the comment count
    public void incrementCommentCount() {
        this.commentCount++;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getAuthorName() {
        return authorName != null && !authorName.isEmpty() ? authorName : "익명 사용자";
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName != null ? authorName : "익명 사용자";
    }

    public String getAuthorIcon() {
        return authorIcon != null && !authorIcon.isEmpty() ? authorIcon : "fk_mmm";
    }

    public void setAuthorIcon(String authorIcon) {
        this.authorIcon = authorIcon != null ? authorIcon : "fk_mmm";
    }

    @Override
    public String toString() {
        return "CommunityPost {" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imageUris=" + imageUris +
                ", userId='" + userId + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", isLiked=" + isLiked +
                ", authorName='" + authorName + '\'' +
                ", authorIcon='" + authorIcon + '\'' +
                '}';
    }
}