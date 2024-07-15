package com.example.freshkeeper;

public class FoodItem {
    private int imageResource;
    private String name;
    private String regDate;
    private String expDate;
    private String countdown;

    public FoodItem(int imageResource, String name, String regDate, String expDate, String countdown) {
        this.imageResource = imageResource;
        this.name = name;
        this.regDate = regDate;
        this.expDate = expDate;
        this.countdown = countdown;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }

    public String getRegDate() {
        return regDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getCountdown() {
        return countdown;
    }
}
