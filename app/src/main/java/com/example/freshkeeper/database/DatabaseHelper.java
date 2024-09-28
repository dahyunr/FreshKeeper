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

    private static final String TABLE_NAME = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_REG_DATE = "reg_date";
    private static final String COLUMN_EXP_DATE = "exp_date";
    private static final String COLUMN_MEMO = "memo";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_STORAGE_METHOD = "storage_method";
    private static final String COLUMN_IMAGE_PATH = "image_path";

    // 테이블 생성 SQL 쿼리
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT UNIQUE, " +
                    COLUMN_REG_DATE + " TEXT, " +
                    COLUMN_EXP_DATE + " TEXT, " +
                    COLUMN_MEMO + " TEXT, " +
                    COLUMN_QUANTITY + " INTEGER, " +
                    COLUMN_STORAGE_METHOD + " INTEGER, " +
                    COLUMN_IMAGE_PATH + " TEXT);";

    // 생성자
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 데이터베이스 최초 생성 시 호출되는 메서드
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_CREATE); // 테이블 생성 쿼리 실행
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "테이블 생성 오류: " + e.getMessage());
        }
    }

    // 데이터베이스 업그레이드 시 호출되는 메서드
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // 기존 테이블 삭제
            onCreate(db); // 새 테이블 생성
        }
    }

    // 데이터 중복 확인 메서드 (name 기준)
    public boolean checkIfItemExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NAME}, COLUMN_NAME + " = ?", new String[]{name}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close(); // 커서 닫기
        return exists;
    }

    // 항목 삽입 또는 업데이트 (중복 방지 및 트랜잭션 사용)
    public long insertOrUpdateItem(String name, String regDate, String expDate, String memo, int quantity, int storageMethod, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        try {
            db.beginTransaction();  // 트랜잭션 시작
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_REG_DATE, regDate);
            values.put(COLUMN_EXP_DATE, expDate);
            values.put(COLUMN_MEMO, memo);
            values.put(COLUMN_QUANTITY, quantity);
            values.put(COLUMN_STORAGE_METHOD, storageMethod);
            values.put(COLUMN_IMAGE_PATH, imagePath);

            // 중복 항목 처리
            if (checkIfItemExists(name)) {
                result = db.update(TABLE_NAME, values, COLUMN_NAME + " = ?", new String[]{name});
                Log.d("DatabaseHelper", "항목 업데이트됨: " + name);
            } else {
                result = db.insert(TABLE_NAME, null, values);
                Log.d("DatabaseHelper", "항목 삽입됨: " + name);
            }

            db.setTransactionSuccessful();  // 트랜잭션 성공
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "항목 삽입/업데이트 오류: " + e.getMessage());
        } finally {
            db.endTransaction();  // 트랜잭션 종료
        }

        return result;
    }

    // 모든 항목 가져오기 메서드
    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "항목 조회 오류: " + e.getMessage());
        }
        return cursor;
    }

    // 저장 방법에 따른 항목 가져오기
    public Cursor getItemsByStorageMethod(int storageMethod) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_STORAGE_METHOD + " = ?";
        String[] selectionArgs = {String.valueOf(storageMethod)};
        return db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    // 항목 삭제 메서드
    public boolean deleteItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        try {
            success = db.delete(TABLE_NAME, COLUMN_NAME + " = ?", new String[]{name}) > 0;
            if (success) {
                Log.d("DatabaseHelper", "항목 삭제됨: " + name);
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "항목 삭제 오류: " + e.getMessage());
        }
        return success;
    }
}
