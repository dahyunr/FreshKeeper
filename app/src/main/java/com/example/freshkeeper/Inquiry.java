package com.example.freshkeeper;

public class Inquiry {
    private String category;
    private String content;
    private String status;
    private String response;

    // 생성자 및 Getter/Setter 메서드
    public Inquiry(String category, String content, String status, String response) {
        this.category = category;
        this.content = content;
        this.status = status;
        this.response = response;
    }

    public String getCategory() { return category; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public String getResponse() { return response; }
}
