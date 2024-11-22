package com.example.freshkeeper;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREF_NAME = "UserPrefs"; // SharedPreferences 이름
    private SharedPreferences sharedPreferences;

    public UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 로그인한 사용자의 닉네임을 반환합니다.
     */
    public String getLoggedInNickname() {
        return sharedPreferences.getString("userName", "익명 사용자");
    }

    /**
     * 로그인한 사용자의 이메일을 반환합니다.
     */
    public String getLoggedInEmail() {
        return sharedPreferences.getString("userEmail", null);
    }

    /**
     * 로그인한 사용자의 프로필 아이콘 URI를 반환합니다.
     */
    public String getLoggedInUserIcon() {
        return sharedPreferences.getString("userIcon", "default_icon_uri"); // 기본값
    }

    /**
     * 사용자 정보를 저장합니다.
     */
    public void saveUserInfo(String userName, String userEmail, String userIcon) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.putString("userEmail", userEmail);
        editor.putString("userIcon", userIcon);
        editor.apply(); // 변경 내용을 비동기적으로 저장
    }

    /**
     * 사용자 정보를 초기화합니다 (로그아웃 시 호출).
     */
    public void clearUserInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
