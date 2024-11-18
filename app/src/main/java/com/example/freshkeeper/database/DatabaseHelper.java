package com.example.freshkeeper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.freshkeeper.CommunityPost;
import com.example.freshkeeper.InquiryItem;
import com.example.freshkeeper.Comment; // 사용자 정의 Comment 클래스 import

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FreshKeeper.db";
    private static final int DATABASE_VERSION = 18;

    private static final String USERS_TABLE_NAME = "users";
    private static final String USER_COLUMN_ID = "user_id";
    private static final String USER_COLUMN_NAME = "user_name";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_PASSWORD = "password";
    private static final String USER_COLUMN_PHONE = "phone";

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
    private static final String ITEM_COLUMN_CREATED_AT = "created_at";

    private static final String INQUIRIES_TABLE_NAME = "inquiries";
    private static final String INQUIRY_COLUMN_ID = "id";
    private static final String INQUIRY_COLUMN_CATEGORY = "category";
    private static final String INQUIRY_COLUMN_EMAIL = "email";
    private static final String INQUIRY_COLUMN_CONTENT = "content";
    private static final String INQUIRY_COLUMN_TIMESTAMP = "timestamp";
    private static final String INQUIRY_COLUMN_RESPONSE = "response";
    private static final String INQUIRY_COLUMN_STATUS = "status";

    private static final String COMMUNITY_POSTS_TABLE_NAME = "community_posts";
    private static final String POST_COLUMN_ID = "post_id";
    private static final String POST_COLUMN_TITLE = "title";
    private static final String POST_COLUMN_CONTENT = "content";
    private static final String POST_COLUMN_IMAGE_URI = "image_uri";
    private static final String POST_COLUMN_USER_ID = "user_id";
    private static final String POST_COLUMN_LIKE_COUNT = "like_count";
    private static final String POST_COLUMN_COMMENT_COUNT = "comment_count";

    private static final String COMMENTS_TABLE_NAME = "comments";
    private static final String COMMENT_COLUMN_ID = "comment_id";
    private static final String COMMENT_COLUMN_CONTENT = "content";
    private static final String COMMENT_COLUMN_USER_ID = "userId";
    private static final String COMMENT_COLUMN_POST_ID = "postId";
    private static final String COMMENT_COLUMN_LIKE_COUNT = "likeCount";

    private static final String COMMENTS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + COMMENTS_TABLE_NAME + " (" +
                    COMMENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COMMENT_COLUMN_CONTENT + " TEXT, " +
                    COMMENT_COLUMN_USER_ID + " INTEGER, " +
                    COMMENT_COLUMN_POST_ID + " INTEGER, " +
                    COMMENT_COLUMN_LIKE_COUNT + " INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(" + COMMENT_COLUMN_POST_ID + ") REFERENCES " + COMMUNITY_POSTS_TABLE_NAME + "(" + POST_COLUMN_ID + "));";

    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + USERS_TABLE_NAME + " (" +
                    USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_COLUMN_NAME + " TEXT, " +
                    USER_COLUMN_EMAIL + " TEXT UNIQUE, " +
                    USER_COLUMN_PASSWORD + " TEXT, " +
                    USER_COLUMN_PHONE + " TEXT);";

    private static final String ITEMS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + ITEMS_TABLE_NAME + " (" +
                    ITEM_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ITEM_COLUMN_NAME + " TEXT, " +
                    ITEM_COLUMN_REG_DATE + " TEXT, " +
                    ITEM_COLUMN_EXP_DATE + " TEXT, " +
                    ITEM_COLUMN_MEMO + " TEXT, " +
                    ITEM_COLUMN_QUANTITY + " INTEGER, " +
                    ITEM_COLUMN_STORAGE_METHOD + " INTEGER, " +
                    ITEM_COLUMN_IMAGE_PATH + " TEXT, " +
                    ITEM_COLUMN_BARCODE + " TEXT UNIQUE, " +
                    ITEM_COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

    private static final String INQUIRIES_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + INQUIRIES_TABLE_NAME + " (" +
                    INQUIRY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    INQUIRY_COLUMN_CATEGORY + " TEXT, " +
                    INQUIRY_COLUMN_EMAIL + " TEXT, " +
                    INQUIRY_COLUMN_CONTENT + " TEXT, " +
                    INQUIRY_COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    INQUIRY_COLUMN_RESPONSE + " TEXT, " +
                    INQUIRY_COLUMN_STATUS + " TEXT DEFAULT '답변예정');";

    private static final String COMMUNITY_POSTS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + COMMUNITY_POSTS_TABLE_NAME + " (" +
                    POST_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    POST_COLUMN_TITLE + " TEXT, " +
                    POST_COLUMN_CONTENT + " TEXT, " +
                    POST_COLUMN_IMAGE_URI + " TEXT, " +
                    POST_COLUMN_USER_ID + " TEXT, " +
                    POST_COLUMN_LIKE_COUNT + " INTEGER DEFAULT 0, " +
                    POST_COLUMN_COMMENT_COUNT + " INTEGER DEFAULT 0);";


    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 데이터베이스 인스턴스를 가져오는 메서드
    public SQLiteDatabase getDatabase() {
        if (database == null || !database.isOpen()) {
            database = this.getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(ITEMS_TABLE_CREATE);
        db.execSQL(INQUIRIES_TABLE_CREATE);
        db.execSQL(COMMUNITY_POSTS_TABLE_CREATE); // 수정된 부분
        db.execSQL(COMMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + COMMENTS_TABLE_NAME); // 댓글 테이블 먼저 삭제
        db.execSQL("DROP TABLE IF EXISTS " + COMMUNITY_POSTS_TABLE_NAME); // 게시글 테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INQUIRIES_TABLE_NAME);
        onCreate(db); // 테이블 재생성
    }

    // 이메일 중복 여부 확인 메서드
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = getDatabase();
        String selection = USER_COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(USERS_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 사용자 정보 삽입 메서드
    public long insertUser(String userName, String email, String password, String phone) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_NAME, userName);
        values.put(USER_COLUMN_EMAIL, email);
        values.put(USER_COLUMN_PASSWORD, password);
        values.put(USER_COLUMN_PHONE, phone);

        long result = -1;
        try {
            result = db.insertOrThrow(USERS_TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "사용자 삽입 오류: " + e.getMessage());
        }
        return result;
    }

    // 사용자 인증 메서드
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

    // 이메일을 통해 사용자 이름 조회 메서드
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

    // 사용자 비밀번호 업데이트 메서드
    public void updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_PASSWORD, newPassword);

        db.update(USERS_TABLE_NAME, values, USER_COLUMN_EMAIL + " = ?", new String[]{email});
    }

    // 사용자 삭제 메서드
    public boolean deleteUserByEmail(String email) {
        SQLiteDatabase db = getDatabase();
        return db.delete(USERS_TABLE_NAME, USER_COLUMN_EMAIL + " = ?", new String[]{email}) > 0;
    }

    // 아이템 삽입 또는 업데이트 메서드
    public long insertOrUpdateItem(Integer id, String name, String regDate, String expDate, String memo, int quantity, int storageMethod, String imagePath, String barcode) {
        SQLiteDatabase db = getDatabase();
        long result = -1;

        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(ITEM_COLUMN_NAME, name);
            values.put(ITEM_COLUMN_REG_DATE, regDate);
            values.put(ITEM_COLUMN_EXP_DATE, expDate);
            values.put(ITEM_COLUMN_MEMO, memo);
            values.put(ITEM_COLUMN_QUANTITY, quantity);
            values.put(ITEM_COLUMN_STORAGE_METHOD, storageMethod);
            values.put(ITEM_COLUMN_IMAGE_PATH, imagePath);
            values.put(ITEM_COLUMN_BARCODE, barcode);

            if (id != null) {
                int rowsAffected = db.update(ITEMS_TABLE_NAME, values, ITEM_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
                if (rowsAffected > 0) {
                    result = rowsAffected;
                } else {
                    result = db.insert(ITEMS_TABLE_NAME, null, values);
                }
            } else {
                result = db.insert(ITEMS_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "아이템 삽입 또는 업데이트 오류: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return result;
    }

    // 바코드로 아이템 조회 메서드
    public Cursor getItemByBarcode(String barcode) {
        SQLiteDatabase db = getDatabase();
        String selection = ITEM_COLUMN_BARCODE + " = ?";
        String[] selectionArgs = {barcode};
        return db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    // 아이템 삭제 메서드
    public boolean deleteItem(int id) {
        SQLiteDatabase db = getDatabase();
        boolean result = false;
        try {
            db.beginTransaction();
            int rowsAffected = db.delete(ITEMS_TABLE_NAME, ITEM_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            result = rowsAffected > 0;
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "아이템 삭제 오류: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    // 모든 아이템 조회 메서드
    public Cursor getAllItems() {
        SQLiteDatabase db = getDatabase();
        return db.query(ITEMS_TABLE_NAME, null, null, null, null, null, ITEM_COLUMN_CREATED_AT + " DESC");
    }

    // 저장 방식에 따른 아이템 조회 메서드
    public Cursor getItemsByStorageMethod(int storageMethod) {
        SQLiteDatabase db = getDatabase();
        String selection = ITEM_COLUMN_STORAGE_METHOD + " = ?";
        String[] selectionArgs = {String.valueOf(storageMethod)};
        return db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, ITEM_COLUMN_CREATED_AT + " DESC");
    }

    // 특정 유통기한을 갖는 아이템 조회 메서드
    public Cursor getItemsByExpDate(String expDate) {
        SQLiteDatabase db = getDatabase();
        String selection = ITEM_COLUMN_EXP_DATE + " = ?";
        String[] selectionArgs = {expDate};
        return db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, ITEM_COLUMN_CREATED_AT + " DESC");
    }

    // 유통기한 기간 내 아이템 조회 메서드
    public Cursor getItemsByExpDateRange(String startDate, String endDate) {
        SQLiteDatabase db = getDatabase();
        String selection = ITEM_COLUMN_EXP_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};
        return db.query(ITEMS_TABLE_NAME, null, selection, selectionArgs, null, null, ITEM_COLUMN_CREATED_AT + " DESC");
    }

    // 문의 내용 저장 메서드 (이메일 포함)
    public void insertInquiry(String category, String email, String content) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(INQUIRY_COLUMN_CATEGORY, category);
        values.put(INQUIRY_COLUMN_EMAIL, email); // email 컬럼 설정
        values.put(INQUIRY_COLUMN_CONTENT, content);
        values.put(INQUIRY_COLUMN_STATUS, "답변예정"); // 기본 상태 설정

        try {
            db.insert(INQUIRIES_TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "문의 삽입 오류: " + e.getMessage());
        }
    }

    // 모든 문의 내역 조회 메서드
    public List<InquiryItem> getAllInquiries() {
        List<InquiryItem> inquiries = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + INQUIRIES_TABLE_NAME + " ORDER BY " + INQUIRY_COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(INQUIRY_COLUMN_CATEGORY));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(INQUIRY_COLUMN_EMAIL));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(INQUIRY_COLUMN_CONTENT));
                String response = cursor.getString(cursor.getColumnIndexOrThrow(INQUIRY_COLUMN_RESPONSE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(INQUIRY_COLUMN_STATUS));

                boolean isAnswered = "답변 완료".equals(status);
                InquiryItem inquiryItem = new InquiryItem(category, email, content, response, isAnswered);

                inquiries.add(inquiryItem);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return inquiries;
    }

    // 게시글 추가 메서드
    public void addCommunityPost(CommunityPost post) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(POST_COLUMN_TITLE, post.getTitle());
        values.put(POST_COLUMN_CONTENT, post.getContent());

        // 이미지가 없는 경우 빈 문자열로 저장
        String imageUris = post.getImageUris() != null && !post.getImageUris().isEmpty()
                ? String.join(",", post.getImageUris())
                : "";
        values.put(POST_COLUMN_IMAGE_URI, imageUris);

        values.put(POST_COLUMN_USER_ID, post.getUserId());
        values.put(POST_COLUMN_LIKE_COUNT, post.getLikeCount());
        values.put(POST_COLUMN_COMMENT_COUNT, post.getCommentCount());

        try {
            db.insert(COMMUNITY_POSTS_TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "게시글 삽입 오류: " + e.getMessage());
        }
    }

    public List<CommunityPost> getAllCommunityPosts() {
        List<CommunityPost> postList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(COMMUNITY_POSTS_TABLE_NAME, null, null, null, null, null, POST_COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_CONTENT));
                String imageUrisString = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_IMAGE_URI));
                List<String> imageUris = (imageUrisString != null && !imageUrisString.isEmpty())
                        ? Arrays.asList(imageUrisString.split(","))
                        : new ArrayList<>();

                String userId = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_USER_ID));
                int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_LIKE_COUNT));
                int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_COMMENT_COUNT));

                CommunityPost post = new CommunityPost(title, content, imageUris, userId, likeCount, commentCount);
                postList.add(post);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return postList;
    }

    // DatabaseHelper.java
    public void addComment(Comment comment) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(COMMENT_COLUMN_CONTENT, comment.getContent());
        values.put(COMMENT_COLUMN_USER_ID, comment.getUserId());
        values.put(COMMENT_COLUMN_POST_ID, comment.getPostId());
        values.put(COMMENT_COLUMN_LIKE_COUNT, comment.getLikeCount());

        try {
            db.insert(COMMENTS_TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "댓글 삽입 오류: " + e.getMessage());
        }
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> commentList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(COMMENTS_TABLE_NAME, null, COMMENT_COLUMN_POST_ID + " = ?", new String[]{String.valueOf(postId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_CONTENT));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_USER_ID));
                int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_LIKE_COUNT));

                Comment comment = new Comment(content, userId, postId, likeCount);
                commentList.add(comment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return commentList;
    }

    // DatabaseHelper.java
    public CommunityPost getPostById(int postId) {
        SQLiteDatabase db = getDatabase();
        String selection = POST_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(postId)};
        Cursor cursor = db.query(COMMUNITY_POSTS_TABLE_NAME, null, selection, selectionArgs, null, null, null);

        CommunityPost post = null;
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_CONTENT));

            // 쉼표로 구분된 문자열을 `List<String>`로 변환
            List<String> imageUris = Arrays.asList(cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_IMAGE_URI)).split(","));

            String userId = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_USER_ID));
            int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_LIKE_COUNT));
            int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_COMMENT_COUNT));

            post = new CommunityPost(title, content, imageUris, userId, likeCount, commentCount);
        }
        cursor.close();
        return post;
    }
}
