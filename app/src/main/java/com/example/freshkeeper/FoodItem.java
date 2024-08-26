package com.example.freshkeeper;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private int imageResource;
    private String name;
    private String regDate;
    private String expDate;
    private String countdown;
    private String memo; // 추가된 필드

    public FoodItem(int imageResource, String name, String regDate, String expDate, String countdown, String memo) {
        this.imageResource = imageResource;
        this.name = name;
        this.regDate = regDate;
        this.expDate = expDate;
        this.countdown = countdown;
        this.memo = memo;
    }

    // Getters and Setters

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCountdown() {
        return countdown;
    }

    public void setCountdown(String countdown) {
        this.countdown = countdown;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
