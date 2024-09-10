package com.example.freshkeeper.network;

import java.util.List;

public class MyRequestType {

    private List<Request> requests;

    public static class Request {
        private Image image;
        private List<Feature> features;

        public static class Image {
            private String content; // base64로 인코딩된 이미지 데이터

            // Getters and Setters
            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }

        public static class Feature {
            private String type; // 예: "TEXT_DETECTION"

            // Getters and Setters
            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

        // Getters and Setters for Request class
        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public List<Feature> getFeatures() {
            return features;
        }

        public void setFeatures(List<Feature> features) {
            this.features = features;
        }
    }

    // Getters and Setters for MyRequestType class
    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }
}
