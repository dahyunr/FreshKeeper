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
    private static final int DATABASE_VERSION = 1;

    // 테이블 이름 및 컬럼 이름 정의
    private static final String TABLE_NAME = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BARCODE = "barcode";

    // 테이블 생성 SQL 문
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_BARCODE + " TEXT UNIQUE);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_CREATE);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error creating table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 데이터 삽입 메소드 (중복 체크 포함)
    public long insertItem(String name, String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_BARCODE, barcode);

            result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (result == -1) {
                Log.d("DatabaseHelper", "Barcode already exists.");
            } else {
                Log.d("DatabaseHelper", "Item inserted successfully");
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error inserting item: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    // 데이터 조회 메소드
    public Cursor getItemByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, COLUMN_BARCODE + " = ?", new String[]{barcode}, null, null, null);
    }

    // 데이터 삭제 메소드
    public boolean deleteItem(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_BARCODE + " = ?", new String[]{barcode}) > 0;
    }
}
