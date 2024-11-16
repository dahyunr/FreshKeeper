package com.example.freshkeeper;

import java.util.ArrayList;
import java.util.List;

public class CommunityPost {

    private int id; // 게시글 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private List<String> imageUris; // 여러 이미지 URI를 저장하는 리스트
    private String userId; // 사용자 ID
    private int likeCount; // 좋아요 수
    private int commentCount; // 댓글 수
    private boolean isLiked; // 좋아요 여부

    // 모든 필드를 포함한 생성자
    public CommunityPost(int id, String title, String content, List<String> imageUris, String userId, int likeCount, int commentCount, boolean isLiked) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUris = imageUris != null ? imageUris : new ArrayList<>(); // null일 경우 빈 리스트로 초기화
        this.userId = userId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
    }

    // 기본값으로 `isLiked`를 false로 설정하는 생성자
    public CommunityPost(int id, String title, String content, List<String> imageUris, String userId, int likeCount, int commentCount) {
        this(id, title, content, imageUris, userId, likeCount, commentCount, false);
    }

    // `id`가 자동 생성되거나 없는 경우에 대한 생성자
    public CommunityPost(String title, String content, List<String> imageUris, String userId, int likeCount, int commentCount) {
        this(0, title, content, imageUris, userId, likeCount, commentCount, false);
    }

    // Getter와 Setter
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
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }

    // 첫 번째 이미지를 가져오는 메서드
    public String getFirstImageUri() {
        return imageUris != null && !imageUris.isEmpty() ? imageUris.get(0) : null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public String toString() {
        return "CommunityPost{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imageUris=" + imageUris +
                ", userId='" + userId + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", isLiked=" + isLiked +
                '}';
    }
}
