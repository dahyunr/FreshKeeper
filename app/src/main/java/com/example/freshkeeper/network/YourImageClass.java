package com.example.freshkeeper.network;

import java.util.List;

public class YourImageClass {

    private String image; // Base64 인코딩된 이미지
    private List<String> features; // 분석할 기능들

    // Constructor, Getters and Setters
    public YourImageClass(String image, List<String> features) {
        this.image = image;
        this.features = features;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
