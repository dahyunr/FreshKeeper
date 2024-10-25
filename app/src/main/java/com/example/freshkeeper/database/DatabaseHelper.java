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
    private static final int DATABASE_VERSION = 12; // 개선: 버전 실시 증가

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
    private static final String ITEM_COLUMN_BARCODE = "barcode";
    private static final String ITEM_COLUMN_CREATED_AT = "created_at"; // 추가: 등록 시간

    // users 테이블 생성 SQL 코드
    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE " + USERS_TABLE_NAME + " (" +
                    USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_COLUMN_NAME + " TEXT, " +
                    USER_COLUMN_EMAIL + " TEXT UNIQUE, " +
                    USER_COLUMN_PASSWORD + " TEXT, " +
                    USER_COLUMN_PHONE + " TEXT);";

    // items 테이블 생성 SQL 코드
    private static final String ITEMS_TABLE_CREATE =
            "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
                    ITEM_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ITEM_COLUMN_NAME + " TEXT, " +
                    ITEM_COLUMN_REG_DATE + " TEXT, " +
                    ITEM_COLUMN_EXP_DATE + " TEXT, " +
                    ITEM_COLUMN_MEMO + " TEXT, " +
                    ITEM_COLUMN_QUANTITY + " INTEGER, " +
                    ITEM_COLUMN_STORAGE_METHOD + " INTEGER, " +
                    ITEM_COLUMN_IMAGE_PATH + " TEXT, " +
                    ITEM_COLUMN_BARCODE + " TEXT UNIQUE, " +
                    ITEM_COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);"; // 추가: 등록 시간 코드

    // 데이터베이스 참조용 멤버 변수
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 데이터베이스 객체 얻기
    public SQLiteDatabase getDatabase() {
        if (database == null || !database.isOpen()) {
            database = this.getWritableDatabase();  // 쓰기 가능한 데이터베이스 열기
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(USERS_TABLE_CREATE);  // users 테이블 생성
            Log.d("DatabaseHelper", "Users 테이블 생성 완료");
            db.execSQL(ITEMS_TABLE_CREATE);  // items 테이블 생성
            Log.d("DatabaseHelper", "Items 테이블 생성 완료");
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "테이블 생성 오류: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            try {
                if (oldVersion < 12) {
                    // created_at 필드가 있는지 확인하고 없으면 추가
                    Cursor cursor = db.rawQuery("PRAGMA table_info(" + ITEMS_TABLE_NAME + ")", null);
                    boolean columnExists = false;

                    while (cursor.moveToNext()) {
                        String columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        if (ITEM_COLUMN_CREATED_AT.equals(columnName)) {
                            columnExists = true;
                            break;
                        }
                    }
                    cursor.close();

                    if (!columnExists) {
                        db.execSQL("ALTER TABLE " + ITEMS_TABLE_NAME + " ADD COLUMN " + ITEM_COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP");
                        Log.d("DatabaseHelper", "created_at 필드 추가 완료");
                    }
                }
            } catch (SQLException e) {
                Log.e("DatabaseHelper", "테이블 업그레이드 오류: " + e.getMessage());
            }
        }
    }

    // 이메일 중복 확인 메서드 (users 테이블)
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = getDatabase();
        String selection = USER_COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(USERS_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 사용자 등록 메서드 (users 테이블)
    public long insertUser(String userName, String email, String password, String phone) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_NAME, userName);
        values.put(USER_COLUMN_EMAIL, email);
        values.put(USER_COLUMN_PASSWORD, password);
        values.put(USER_COLUMN_PHONE, phone);

        return db.insert(USERS_TABLE_NAME, null, values);
    }

    // 사용자 이메일과 비밀번호를 확인하는 메서드 (로그인)
    public boolean authenticateUser(String email, String password) {
        SQLiteDatabase db = getDatabase();
        String[] columns = {USER_COLUMN_ID};
        String selection = USER_COLUMN_EMAIL + " = ? AND " + USER_COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(USERS_TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        boolean isAuthenticated = cursor.getCount() > 0;
        cursor.close();
        return isAuthenticated;
    }

    // 이메일로 사용자 이름 검색
    public String getUserNameByEmail(String email) {
        SQLiteDatabase db = getDatabase();
        String userName = "Unknown User";
        String query = "SELECT " + USER_COLUMN_NAME + " FROM " + USERS_TABLE_NAME + " WHERE " + USER_COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_NAME));
        }
        cursor.close();
        return userName;
    }

    // 사용자 비밀번호 업데이트 메서드 (비밀번호 찾기 기능에 사용)
    public void updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_PASSWORD, newPassword);

        db.update(USERS_TABLE_NAME, values, USER_COLUMN_EMAIL + " = ?", new String[]{email});
    }

    // 사용자 탈퇴 시 users 테이블에서 사용자 삭제하는 메서드 추가
    public boolean deleteUserByEmail(String email) {
        SQLiteDatabase db = getDatabase();
        return db.delete(USERS_TABLE_NAME, USER_COLUMN_EMAIL + " = ?", new String[]{email}) > 0;
    }

    // items 테이블에 항목 삽입 또는 업데이트 메서드
    public long insertOrUpdateItem(Integer id, String name, String regDate, String expDate, String memo, int quantity, int storageMethod, String imagePath, String barcode) {
        SQLiteDatabase db = getDatabase();
        long result = -1;

        try {
            db.beginTransaction();  // 트랜잭션 시작
            ContentValues values = new ContentValues();
            values.put(ITEM_COLUMN_NAME, name);
            values.put(ITEM_COLUMN_REG_DATE, regDate);
            values.put(ITEM_COLUMN_EXP_DATE, expDate);
            values.put(ITEM_COLUMN_MEMO, memo);
            values.put(ITEM_COLUMN_QUANTITY, quantity);
            values.put(ITEM_COLUMN_STORAGE_METHOD, storageMethod);
            values.put(ITEM_COLUMN_IMAGE_PATH, imagePath);
            values.put(ITEM_COLUMN_BARCODE, barcode);

            Log.d("DatabaseHelper", "삽입/업데이트 시도 - 아이템 이름: " + name);

            if (id != null) {
                int rowsAffected = db.update(ITEMS_TABLE_NAME, values, ITEM_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
                if (rowsAffected > 0) {
                    result = rowsAffected;  // 업데이트된 행 수 반환
                    Log.d("DatabaseHelper", "아이템 업데이트 성공 - 아이템 ID: " + id);
                } else {
                    result = db.insert(ITEMS_TABLE_NAME, null, values);  // 업데이트가 안 된 경우 삽입
                    Log.d("DatabaseHelper", "아이템 삽입 성공 - 아이템 이름: " + name);
                }
            } else {
                result = db.insert(ITEMS_TABLE_NAME, null, values);  // 새 아이템 삽입
                Log.d("DatabaseHelper", "아이템 삽입 성공 - 아이템 이름: " + name);
            }
            db.setTransactionSuccessful();  // 트랜잭션 성공
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "아이템 삽입 또는 업데이트 오류: " + e.getMessage());
        } finally {
            db.endTransaction();  // 트랜잭션 종료
        }

        return result;
    }

    // items 테이블에서 바코드로 항목 검색 메서드 추가
    public Cursor getItemByBarcode(String barcode) {
        SQLiteDatabase db = getDatabase();
        String selection = ITEM_COLUMN_BARCODE + " = ?";
        String[] selectionArgs = {barcode};
        return db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    // items 테이블에서 항목 삭제 메서드 (ID 기반으로 수정)
    public boolean deleteItem(int id) {
        SQLiteDatabase db = getDatabase();
        boolean result = false;
        try {
            db.beginTransaction();
            int rowsAffected = db.delete(ITEMS_TABLE_NAME, ITEM_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            if (rowsAffected > 0) {
                result = true;
                Log.d("DatabaseHelper", "아이템 삭제 성공: ID = " + id);
            } else {
                Log.d("DatabaseHelper", "아이템 삭제 실패: ID = " + id);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "아이템 삭제 오류: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    // items 테이블에서 모든 항목을 검색하는 메서드 (created_at을 기준으로 내림차순 정리)
    public Cursor getAllItems() {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, ITEM_COLUMN_CREATED_AT + " DESC");
        Log.d("DatabaseHelper", "getAllItems 호출 - 항목 수: " + cursor.getCount());
        return cursor;
    }

    // 저장 방법에 따른 items 테이블 항목을 검색하는 메서드 (created_at을 기준으로 내림차순 정리)
    public Cursor getItemsByStorageMethod(int storageMethod) {
        SQLiteDatabase db = getDatabase();
        String selection = ITEM_COLUMN_STORAGE_METHOD + " = ?";
        String[] selectionArgs = {String.valueOf(storageMethod)};
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, ITEM_COLUMN_CREATED_AT + " DESC");
        Log.d("DatabaseHelper", "getItemsByStorageMethod 호출 - 저장 방식: " + storageMethod + ", 항목 수: " + cursor.getCount());
        return cursor;
    }

    // items 테이블에서 특정 유통기한을 갖지는 항목을 검색하는 메서드 (카레디너 기능)
    public Cursor getItemsByExpDate(String expDate) {
        SQLiteDatabase db = getDatabase();
        String selection = ITEM_COLUMN_EXP_DATE + " = ?";
        String[] selectionArgs = {expDate};
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, ITEM_COLUMN_CREATED_AT + " DESC");
        Log.d("DatabaseHelper", "getItemsByExpDate 호출 - 유통기한: " + expDate + ", 항목 수: " + cursor.getCount());
        return cursor;
    }

    // 특정 기간 내 유통기한을 갖는 항목을 검색하는 메서드 (카레디너 기능 확장)
    public Cursor getItemsByExpDateRange(String startDate, String endDate) {
        SQLiteDatabase db = getDatabase();
        String selection = ITEM_COLUMN_EXP_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, ITEM_COLUMN_CREATED_AT + " DESC");
        Log.d("DatabaseHelper", "getItemsByExpDateRange 호출 - 시작 날짜: " + startDate + ", 종료 날짜: " + endDate + ", 항목 수: " + cursor.getCount());
        return cursor;
    }
}
