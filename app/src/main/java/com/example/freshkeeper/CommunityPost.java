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
    private String authorName; // 작성자 이름
    private String authorIcon; // 작성자 아이콘 경로

    // 모든 필드를 포함한 생성자
    public CommunityPost(int id, String title, String content, List<String> imageUris, String userId, int likeCount, int commentCount, boolean isLiked, String authorName, String authorIcon) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUris = imageUris != null ? imageUris : new ArrayList<>(); // null일 경우 빈 리스트로 초기화
        this.userId = userId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
        this.authorName = authorName != null ? authorName : "익명 사용자"; // null일 경우 기본값 설정
        this.authorIcon = authorIcon != null ? authorIcon : "fk_mmm"; // null일 경우 기본 아이콘 설정
    }

    // 기본값으로 `isLiked`를 false로 설정하는 생성자
    public CommunityPost(int id, String title, String content, List<String> imageUris, String userId, int likeCount, int commentCount, String authorName, String authorIcon) {
        this(id, title, content, imageUris, userId, likeCount, commentCount, false, authorName, authorIcon);
    }

    // `id`가 자동 생성되거나 없는 경우에 대한 생성자
    public CommunityPost(String title, String content, List<String> imageUris, String userId, int likeCount, int commentCount, String authorName, String authorIcon) {
        this(0, title, content, imageUris, userId, likeCount, commentCount, false, authorName, authorIcon);
    }

    // 기본 생성자 (필요 시 추가)
    public CommunityPost() {
        this.id = 0;
        this.title = "";
        this.content = "";
        this.imageUris = new ArrayList<>();
        this.userId = "";
        this.likeCount = 0;
        this.commentCount = 0;
        this.isLiked = false;
        this.authorName = "익명 사용자"; // 기본 이름
        this.authorIcon = "fk_mmm"; // 기본 아이콘 설정
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName != null ? authorName : "익명 사용자"; // null일 경우 기본값 설정
    }

    public String getAuthorIcon() {
        return authorIcon;
    }

    public void setAuthorIcon(String authorIcon) {
        this.authorIcon = authorIcon != null ? authorIcon : "fk_mmm"; // null일 경우 기본 아이콘 설정
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
                ", authorName='" + authorName + '\'' +
                ", authorIcon='" + authorIcon + '\'' +
                '}';
    }
}
