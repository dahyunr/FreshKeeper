package com.example.freshkeeper.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Vision API 요청 데이터 구조를 정의하는 클래스
public class YourRequestType {

    @SerializedName("requests")
    private List<Request> requests;

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public static class Request {

        @SerializedName("image")
        private Image image;

        @SerializedName("features")
        private List<Feature> features;

        public void setImage(Image image) {
            this.image = image;
        }

        public void setFeatures(List<Feature> features) {
            this.features = features;
        }
    }

    public static class Image {

        @SerializedName("content")
        private String content;

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class Feature {

        @SerializedName("type")
        private String type;

        public void setType(String type) {
            this.type = type;
        }
    }
}
