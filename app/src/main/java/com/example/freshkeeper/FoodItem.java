package com.example.freshkeeper;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private int id;  // ID 필드 추가
    private int imageResource; // 이미지 리소스
    private String name;
    private String regDate;
    private String expDate;
    private String countdown; // 남은 기한 계산을 위한 필드
    private String memo;
    private String imagePath;  // 이미지 경로 필드 추가
    private int quantity;      // 수량 필드 추가
    private int storageMethod; // 저장 방법 추가

    public FoodItem(int id, int imageResource, String name, String regDate, String expDate, String countdown, String memo, String imagePath, int quantity, int storageMethod) {
        this.id = id;  // ID 필드 추가
        this.imageResource = imageResource;
        this.name = name;
        this.regDate = regDate;
        this.expDate = expDate;
        this.countdown = countdown;
        this.memo = memo;
        this.imagePath = imagePath;
        this.quantity = quantity;    // 생성자에 수량 추가
        this.storageMethod = storageMethod; // 저장 방법 추가
    }

    // 두 번째 생성자 추가 (ID 없이 생성)
    public FoodItem(int imageResource, String name, String regDate, String expDate, String countdown, String memo, String imagePath, int quantity, int storageMethod) {
        this.imageResource = imageResource;
        this.name = name;
        this.regDate = regDate;
        this.expDate = expDate;
        this.countdown = countdown;
        this.memo = memo;
        this.imagePath = imagePath;
        this.quantity = quantity;
        this.storageMethod = storageMethod;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // 수량 관련 Getter 및 Setter 추가
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 저장 방법 관련 Getter 및 Setter 추가
    public int getStorageMethod() {
        return storageMethod;
    }

    public void setStorageMethod(int storageMethod) {
        this.storageMethod = storageMethod;
    }
}
