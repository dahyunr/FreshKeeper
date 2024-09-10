package com.example.freshkeeper.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// UPCitemDB API의 응답 데이터를 처리하는 클래스
public class MyResponseType {

    @SerializedName("items")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        @SerializedName("title")
        private String title;

        @SerializedName("brand")
        private String brand;

        @SerializedName("image")
        private String imageUrl;

        public String getTitle() {
            return title;
        }

        public String getBrand() {
            return brand;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
