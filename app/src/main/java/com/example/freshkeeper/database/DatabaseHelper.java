package com.example.freshkeeper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FreshKeeper.db";
    private static final int DATABASE_VERSION = 3;

    // users 테이블 정보 (회원 관련)
    private static final String USERS_TABLE_NAME = "users";
    private static final String USER_COLUMN_ID = "user_id";
    private static final String USER_COLUMN_NAME = "user_name";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_PASSWORD = "password";
    private static final String USER_COLUMN_PHONE = "phone";

    // items 테이블 정보 (아이템 관련)
    private static final String ITEMS_TABLE_NAME = "items";
    private static final String ITEM_COLUMN_ID = "item_id";
    private static final String ITEM_COLUMN_NAME = "name";
    private static final String ITEM_COLUMN_REG_DATE = "reg_date";
    private static final String ITEM_COLUMN_EXP_DATE = "exp_date";
    private static final String ITEM_COLUMN_MEMO = "memo";
    private static final String ITEM_COLUMN_QUANTITY = "quantity";
    private static final String ITEM_COLUMN_STORAGE_METHOD = "storage_method";
    private static final String ITEM_COLUMN_IMAGE_PATH = "image_path";

    // users 테이블 생성 SQL 쿼리
    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE " + USERS_TABLE_NAME + " (" +
                    USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_COLUMN_NAME + " TEXT, " +
                    USER_COLUMN_EMAIL + " TEXT UNIQUE, " +
                    USER_COLUMN_PASSWORD + " TEXT, " +
                    USER_COLUMN_PHONE + " TEXT);";

    // items 테이블 생성 SQL 쿼리
    private static final String ITEMS_TABLE_CREATE =
            "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
                    ITEM_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ITEM_COLUMN_NAME + " TEXT UNIQUE, " +
                    ITEM_COLUMN_REG_DATE + " TEXT, " +
                    ITEM_COLUMN_EXP_DATE + " TEXT, " +
                    ITEM_COLUMN_MEMO + " TEXT, " +
                    ITEM_COLUMN_QUANTITY + " INTEGER, " +
                    ITEM_COLUMN_STORAGE_METHOD + " INTEGER, " +
                    ITEM_COLUMN_IMAGE_PATH + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // users 테이블과 items 테이블 생성
            db.execSQL(USERS_TABLE_CREATE);  // users 테이블 생성
            db.execSQL(ITEMS_TABLE_CREATE);  // items 테이블 생성
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "테이블 생성 오류: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);  // 기존 users 테이블 삭제
            db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);  // 기존 items 테이블 삭제
            onCreate(db);  // 새 테이블 생성
        }
    }

    // 이메일 중복 확인 메서드 (users 테이블)
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = USER_COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(USERS_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 사용자 등록 메서드 (users 테이블)
    public long insertUser(String userName, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_NAME, userName);
        values.put(USER_COLUMN_EMAIL, email);
        values.put(USER_COLUMN_PASSWORD, password);
        values.put(USER_COLUMN_PHONE, phone);

        return db.insert(USERS_TABLE_NAME, null, values);
    }

    // items 테이블에 항목 삽입 또는 업데이트 메서드
    public long insertOrUpdateItem(String name, String regDate, String expDate, String memo, int quantity, int storageMethod, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        try {
            db.beginTransaction(); // 트랜잭션 시작
            ContentValues values = new ContentValues();
            values.put(ITEM_COLUMN_NAME, name);
            values.put(ITEM_COLUMN_REG_DATE, regDate);
            values.put(ITEM_COLUMN_EXP_DATE, expDate);
            values.put(ITEM_COLUMN_MEMO, memo);
            values.put(ITEM_COLUMN_QUANTITY, quantity);
            values.put(ITEM_COLUMN_STORAGE_METHOD, storageMethod);
            values.put(ITEM_COLUMN_IMAGE_PATH, imagePath);

            // 이미 존재하면 업데이트, 없으면 삽입
            int rowsAffected = db.update(ITEMS_TABLE_NAME, values, ITEM_COLUMN_NAME + " = ?", new String[]{name});
            if (rowsAffected == 0) {
                result = db.insert(ITEMS_TABLE_NAME, null, values);  // 중복 항목이 없을 경우 삽입
            } else {
                result = rowsAffected;  // 업데이트된 행 수 반환
            }
            db.setTransactionSuccessful(); // 트랜잭션 성공
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 트랜잭션 종료
        }

        return result;
    }

    // items 테이블에서 항목 삭제 메서드
    public boolean deleteItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ITEMS_TABLE_NAME, ITEM_COLUMN_NAME + " = ?", new String[]{name}) > 0;
    }

    // items 테이블에서 모든 항목을 가져오는 메서드
    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);
    }

    // 저장 방법에 따른 items 테이블 항목을 가져오는 메서드
    public Cursor getItemsByStorageMethod(int storageMethod) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ITEM_COLUMN_STORAGE_METHOD + " = ?";
        String[] selectionArgs = {String.valueOf(storageMethod)};
        return db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }
}
