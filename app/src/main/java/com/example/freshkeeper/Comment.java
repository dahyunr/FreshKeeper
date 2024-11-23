package com.example.freshkeeper;

import java.io.Serializable;

public class Comment implements Serializable {
    private int id;                 // 댓글 ID
    private String content;         // 댓글 내용
    private int userId;             // 작성자 사용자 ID
    private int postId;             // 게시글 ID
    private int likeCount;          // 좋아요 수
    private String commenterName;   // 댓글 작성자 이름
    private String commenterIcon;   // 댓글 작성자 아이콘 URI
    private String createdAt;       // 작성 시간
    private String updatedAt;       // 수정 시간

    // 모든 필드를 포함한 생성자
    public Comment(int id, String content, int userId, int postId, int likeCount, String commenterName, String commenterIcon, String createdAt, String updatedAt) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.likeCount = likeCount;
        this.commenterName = commenterName;
        this.commenterIcon = commenterIcon;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // createdAt, updatedAt 없이 생성하는 생성자
    public Comment(int id, String content, int userId, int postId, int likeCount, String commenterName, String commenterIcon) {
        this(id, content, userId, postId, likeCount, commenterName, commenterIcon, null, null);
    }

    // 댓글 ID 없이 생성하는 생성자 (새로운 댓글 추가 시 사용)
    public Comment(String content, int userId, int postId, int likeCount, String commenterName, String commenterIcon) {
        this(0, content, userId, postId, likeCount, commenterName, commenterIcon, null, null);
    }

    // content와 postId만 사용하는 간단한 생성자
    public Comment(String content, int postId) {
        this.content = content;
        this.postId = postId;
    }

    // Getter와 Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommenterName() {
        return commenterName != null ? commenterName : "익명";
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommenterIcon() {
        return commenterIcon != null ? commenterIcon : "https://example.com/default-icon.png";
    }

    public void setCommenterIcon(String commenterIcon) {
        this.commenterIcon = commenterIcon;
    }

    public String getCreatedAt() {
        return createdAt != null ? createdAt : "Unknown"; // 기본값 반환
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt != null ? updatedAt : "Unknown"; // 기본값 반환
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", postId=" + postId +
                ", likeCount=" + likeCount +
                ", commenterName='" + commenterName + '\'' +
                ", commenterIcon='" + commenterIcon + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}