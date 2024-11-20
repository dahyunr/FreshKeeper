package com.example.freshkeeper;

public class Comment {
    private int id;
    private String content;
    private int userId;
    private int postId;
    private int likeCount;
    private String commenterName; // 댓글 작성자 이름
    private String commenterIcon; // 댓글 작성자 아이콘 URI

    // 모든 필드를 포함한 생성자
    public Comment(int id, String content, int userId, int postId, int likeCount, String commenterName, String commenterIcon) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.likeCount = likeCount;
        this.commenterName = commenterName;
        this.commenterIcon = commenterIcon;
    }

    // commenterName과 commenterIcon 없이 사용하는 생성자
    public Comment(int id, String content, int userId, int postId, int likeCount) {
        this(id, content, userId, postId, likeCount, null, null);
    }

    // 댓글 ID 없이 생성하는 생성자 (새로운 댓글 추가 시 사용)
    public Comment(String content, int userId, int postId, int likeCount, String commenterName, String commenterIcon) {
        this(0, content, userId, postId, likeCount, commenterName, commenterIcon);
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
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommenterIcon() {
        return commenterIcon;
    }

    public void setCommenterIcon(String commenterIcon) {
        this.commenterIcon = commenterIcon;
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
                '}';
    }
}
