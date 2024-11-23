package com.example.freshkeeper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.SharedPreferences;


import com.example.freshkeeper.CommunityPost;
import com.example.freshkeeper.InquiryItem;
import com.example.freshkeeper.Comment; // 사용자 정의 Comment 클래스 import

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FreshKeeper.db";
    private static final int DATABASE_VERSION = 24; // 기존 22에서 23으로 업데이트
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
            "CREATE TABLE IF NOT EXISTS comments (" +
                    "comment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "content TEXT, " +
                    "userId INTEGER, " +
                    "postId INTEGER, " +
                    "likeCount INTEGER DEFAULT 0, " +
                    "commenter_name TEXT, " + // 작성자 이름
                    "commenter_icon TEXT, " + // 작성자 아이콘
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(postId) REFERENCES community_posts(post_id));";

    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + USERS_TABLE_NAME + " (" +
                    USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_COLUMN_NAME + " TEXT, " +
                    USER_COLUMN_EMAIL + " TEXT UNIQUE, " +
                    USER_COLUMN_PASSWORD + " TEXT, " +
                    USER_COLUMN_PHONE + " TEXT, " +
                    "icon_path TEXT DEFAULT NULL);"; // 아이콘 경로 추가

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
            "CREATE TABLE IF NOT EXISTS community_posts (" +
                    "post_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT, " +
                    "content TEXT, " +
                    "image_uri TEXT, " +
                    "user_id TEXT, " +
                    "like_count INTEGER DEFAULT 0, " +
                    "comment_count INTEGER DEFAULT 0, " +
                    "author_name TEXT, " + // 작성자 이름
                    "author_icon TEXT);";  // 작성자 아이콘

    private static final String COMMENT_LIKES_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS comment_likes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "comment_id INTEGER NOT NULL, " +
                    "user_id TEXT NOT NULL, " +
                    "FOREIGN KEY(comment_id) REFERENCES comments(comment_id));";

    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // context 필드를 초기화합니다.
    }


    // 데이터베이스 인스턴스를 가져오는 메서드
    public SQLiteDatabase getDatabase() {
        if (database == null || !database.isOpen()) {
            database = this.getWritableDatabase();
        }
        return database;
    }

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(ITEMS_TABLE_CREATE);
        db.execSQL(INQUIRIES_TABLE_CREATE);
        db.execSQL(COMMUNITY_POSTS_TABLE_CREATE);
        db.execSQL(COMMENTS_TABLE_CREATE);
        db.execSQL(COMMENT_LIKES_TABLE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 23) { // 버전 23 이하에서만 실행
            try {
                // comments 테이블 수정
                db.execSQL("ALTER TABLE comments ADD COLUMN commenter_name TEXT;");
                db.execSQL("ALTER TABLE comments ADD COLUMN commenter_icon TEXT;");
                db.execSQL("ALTER TABLE comments ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;");
                db.execSQL("ALTER TABLE comments ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;");

                // community_posts 테이블 수정
                db.execSQL("ALTER TABLE community_posts ADD COLUMN author_name TEXT;");
                db.execSQL("ALTER TABLE community_posts ADD COLUMN author_icon TEXT;");

                // 새 테이블 추가
                db.execSQL(COMMENT_LIKES_TABLE_CREATE); // comment_likes 테이블 생성
            } catch (SQLException e) {
                Log.e("DatabaseHelper", "onUpgrade 중 오류 발생: " + e.getMessage());
            }
        }
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
        SQLiteDatabase db = this.getReadableDatabase();
        String userName = null;

        Cursor cursor = db.query(USERS_TABLE_NAME,
                new String[]{"user_name"}, // user_name 컬럼 포함
                "email = ?",
                new String[]{email},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("user_name");
            if (columnIndex != -1) { // user_name 컬럼이 존재하는 경우
                userName = cursor.getString(columnIndex);
            }
            cursor.close();
        }
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

    private Context context;


    public List<CommunityPost> getUserPosts() {
        List<CommunityPost> userPosts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // context를 사용해 SharedPreferences 접근
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userId", "default_user_id");

        String query = "SELECT * FROM community_posts WHERE userId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{currentUserId});

        if (cursor.moveToFirst()) {
            do {
                CommunityPost post = new CommunityPost();
                post.setId(cursor.getInt(cursor.getColumnIndexOrThrow("post_id")));
                post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                post.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
                userPosts.add(post);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userPosts;
    }

    public List<CommunityPost> getAllCommunityPosts() {
        List<CommunityPost> postList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM community_posts ORDER BY post_id DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
                List<String> imageUris = imageUri != null && !imageUri.isEmpty()
                        ? Arrays.asList(imageUri.split(","))
                        : new ArrayList<>();
                String userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
                int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
                int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
                String authorName = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
                String authorIcon = cursor.getString(cursor.getColumnIndexOrThrow("author_icon"));

                CommunityPost post = new CommunityPost(
                        id,
                        title,
                        content,
                        imageUris,
                        userId,
                        likeCount,
                        commentCount,
                        false,
                        authorName != null && !authorName.isEmpty() ? authorName : "익명 사용자",
                        authorIcon != null && !authorIcon.isEmpty() ? authorIcon : "fk_mmm"
                );
                postList.add(post);
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "게시물이 없습니다.");
        }
        cursor.close();
        return postList;
    }

    public void setOnDatabaseChangeListener(OnDatabaseChangeListener listener) {
        this.onDatabaseChangeListener = listener;
    }

    private OnDatabaseChangeListener onDatabaseChangeListener;
    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> commentList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // SQL 쿼리: 댓글 데이터와 사용자 이름 및 아이콘 정보 가져오기
        String query = "SELECT c.*, u." + USER_COLUMN_NAME + " AS commenter_name, u.icon_path AS commenter_icon " +
                "FROM " + COMMENTS_TABLE_NAME + " c " +
                "LEFT JOIN " + USERS_TABLE_NAME + " u " +
                "ON c." + COMMENT_COLUMN_USER_ID + " = u." + USER_COLUMN_ID + " " +
                "WHERE c." + COMMENT_COLUMN_POST_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)});

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 댓글 작성자 이름 가져오기 (익명 처리 포함)
                    String commenterName = cursor.getString(cursor.getColumnIndexOrThrow("commenter_name"));
                    if (commenterName == null || commenterName.isEmpty()) {
                        commenterName = "익명 사용자";
                    }

                    // 댓글 작성자 아이콘 가져오기 (기본 아이콘 처리 포함)
                    String commenterIcon = cursor.getString(cursor.getColumnIndexOrThrow("commenter_icon"));
                    if (commenterIcon == null || commenterIcon.isEmpty()) {
                        commenterIcon = "fk_mmm";
                    }

                    // Comment 객체 생성
                    Comment comment = new Comment(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_ID)), // 댓글 ID
                            cursor.getString(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_CONTENT)), // 댓글 내용
                            cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_USER_ID)), // 작성자 ID
                            postId, // 게시글 ID
                            cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_LIKE_COUNT)), // 좋아요 수
                            commenterName, // 작성자 이름
                            commenterIcon // 작성자 아이콘
                    );

                    // 댓글 리스트에 추가
                    commentList.add(comment);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "댓글 불러오기 오류: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // 커서 닫기
            }
        }

        // 로깅 추가 (불러온 댓글 수 확인)
        Log.d("DatabaseHelper", "댓글 목록 로드 완료: " + commentList.size() + "개");

        return commentList;
    }


    // DatabaseHelper.java
    public CommunityPost getPostById(int postId) {
        SQLiteDatabase db = getReadableDatabase(); // 읽기 모드 데이터베이스 가져오기

        String query = "SELECT p.*, u." + USER_COLUMN_NAME + " AS author_name, u.icon_path AS author_icon " +
                "FROM " + COMMUNITY_POSTS_TABLE_NAME + " p " +
                "LEFT JOIN " + USERS_TABLE_NAME + " u " +
                "ON p." + POST_COLUMN_USER_ID + " = u." + USER_COLUMN_ID + " " +
                "WHERE p." + POST_COLUMN_ID + " = ?";

        String[] selectionArgs = {String.valueOf(postId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        CommunityPost post = null;

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_CONTENT));
            String imageUrisString = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_IMAGE_URI));
            List<String> imageUris = (imageUrisString != null && !imageUrisString.isEmpty())
                    ? Arrays.asList(imageUrisString.split(","))
                    : new ArrayList<>();

            String userId = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_USER_ID));
            int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_LIKE_COUNT));
            int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_COMMENT_COUNT));
            boolean isLiked = false; // 기본값

            String authorName = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
            if (authorName == null || authorName.isEmpty()) {
                authorName = "익명 사용자";
            }

            String authorIcon = cursor.getString(cursor.getColumnIndexOrThrow("author_icon"));
            if (authorIcon == null || authorIcon.isEmpty()) {
                authorIcon = "fk_mmm";
            }

            post = new CommunityPost(postId, title, content, imageUris, userId, likeCount, commentCount, isLiked, authorName, authorIcon);
        }

        cursor.close();
        return post;
    }

    public String getNicknameByUserId(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        String nickname = "익명 사용자"; // 기본 닉네임
        Cursor cursor = db.rawQuery("SELECT user_name FROM users WHERE user_id = ?", new String[]{userId});
        if (cursor.moveToFirst()) {
            nickname = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
        }
        cursor.close();
        return nickname;
    }

    public String getUserIconByUserId(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        String iconPath = "fk_mmm"; // 기본 아이콘 경로
        Cursor cursor = db.rawQuery("SELECT icon_path FROM users WHERE user_id = ?", new String[]{userId});
        if (cursor.moveToFirst()) {
            iconPath = cursor.getString(cursor.getColumnIndexOrThrow("icon_path"));
        }
        cursor.close();
        return iconPath;
    }

    public void updateCommentCount(int postId) {
        SQLiteDatabase db = getDatabase();
        try {
            String query = "SELECT COUNT(*) AS count FROM " + COMMENTS_TABLE_NAME +
                    " WHERE " + COMMENT_COLUMN_POST_ID + " = ?";
            try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)})) {
                int count = 0;
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
                }

                ContentValues values = new ContentValues();
                values.put(POST_COLUMN_COMMENT_COUNT, count);

                int rowsAffected = db.update(COMMUNITY_POSTS_TABLE_NAME, values,
                        POST_COLUMN_ID + " = ?",
                        new String[]{String.valueOf(postId)});
                if (rowsAffected > 0) {
                    Log.d("DatabaseHelper", "댓글 수 업데이트 성공: postId=" + postId + ", count=" + count);
                } else {
                    Log.w("DatabaseHelper", "댓글 수 업데이트 실패: postId=" + postId);
                }
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "댓글 수 업데이트 오류: " + e.getMessage());
        }
    }

    public boolean isPostExists(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false; // 제목이 비어 있으면 중복 아님
        }
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + COMMUNITY_POSTS_TABLE_NAME +
                        " WHERE " + POST_COLUMN_TITLE + " = ?",
                new String[]{title});
        boolean exists = cursor.moveToFirst(); // 첫 번째 결과가 존재하면 true
        cursor.close();
        return exists;
    }
    // 댓글 삽입 메서드
    public void addComment(Comment comment) {
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        Log.d("DatabaseHelper", "notifyCommentAdded 호출됨");

        try {
            ContentValues values = new ContentValues();
            values.put(COMMENT_COLUMN_CONTENT, comment.getContent());
            values.put(COMMENT_COLUMN_USER_ID, comment.getUserId());
            values.put(COMMENT_COLUMN_POST_ID, comment.getPostId());
            values.put(COMMENT_COLUMN_LIKE_COUNT, comment.getLikeCount());
            values.put("commenter_name", comment.getCommenterName());
            values.put("commenter_icon", comment.getCommenterIcon());

            long rowId = db.insert(COMMENTS_TABLE_NAME, null, values);
            if (rowId != -1) {
                Log.d("DatabaseHelper", "댓글 삽입 성공: " + comment.getContent());

                // 댓글 수 업데이트
                updateCommentCount(comment.getPostId());

                // **리스너 호출로 실시간 반영**
                notifyCommentAdded();  // 이 코드가 실행되는지 확인
            } else {
                Log.e("DatabaseHelper", "댓글 삽입 실패");
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "댓글 삽입 오류: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    // 댓글 추가 이벤트 트리거 메서드
    private void notifyCommentAdded() {
        if (onDatabaseChangeListener != null) {
            onDatabaseChangeListener.onCommentAdded();
        }
    }

    public void updateCommentCount(int postId, int newCommentCount) {
        // 게시글의 댓글 수를 데이터베이스에 업데이트하는 로직 구현
    }

    public int getCommentCount(int postId) {
        // 게시글의 댓글 수를 데이터베이스에서 가져오는 메서드 구현
        return 0; // 실제 구현 필요
    }

    public List<CommunityPost> getUserPosts(String userId) {
        List<CommunityPost> userPosts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + COMMUNITY_POSTS_TABLE_NAME + " WHERE " + POST_COLUMN_USER_ID + " = ? ORDER BY " + POST_COLUMN_ID + " DESC";

        try (Cursor cursor = db.rawQuery(query, new String[]{userId})) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_TITLE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_CONTENT));
                    String imageUrisString = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_IMAGE_URI));
                    List<String> imageUris = (imageUrisString != null && !imageUrisString.isEmpty())
                            ? Arrays.asList(imageUrisString.split(","))
                            : new ArrayList<>();

                    CommunityPost post = new CommunityPost(
                            id,
                            title,
                            content,
                            imageUris,
                            userId,
                            cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_LIKE_COUNT)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_COMMENT_COUNT)),
                            false, // 기본 좋아요 상태
                            "익명 사용자", // 필요시 수정
                            "fk_mmm" // 필요시 수정
                    );

                    userPosts.add(post);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "사용자 게시글 데이터 로드 중 오류 발생: " + e.getMessage());
        }

        return userPosts;
    }

    public long addCommunityPost(CommunityPost post) {
        if (post == null || post.getTitle() == null || post.getContent() == null) {
            Log.e("DatabaseHelper", "게시글 데이터가 null이거나 제목 또는 내용이 없습니다.");
            return -1; // 데이터가 없는 경우 삽입 중단
        }

        SQLiteDatabase db = getDatabase();
        long newId = -1;

        try {
            ContentValues values = new ContentValues();

            // 제목과 내용 저장
            values.put("title", post.getTitle());
            values.put("content", post.getContent());

            // 이미지 URI 리스트를 쉼표로 연결하여 저장
            List<String> imageUris = post.getImageUris();
            String imageUrisString = (imageUris != null && !imageUris.isEmpty())
                    ? String.join(",", imageUris)
                    : null;
            values.put("image_uri", imageUrisString);

            // 나머지 게시글 데이터 저장
            values.put("user_id", post.getUserId());
            values.put("like_count", post.getLikeCount());
            values.put("comment_count", post.getCommentCount());
            values.put("author_name", post.getAuthorName());
            values.put("author_icon", post.getAuthorIcon());

            // 데이터 삽입
            newId = db.insert("community_posts", null, values);

            if (newId != -1) {
                Log.d("DatabaseHelper", "게시글 저장 성공. ID: " + newId);

                // 실시간 반영 콜백 호출
                notifyPostAdded();
            } else {
                Log.e("DatabaseHelper", "게시글 저장 실패.");
            }

        } catch (Exception e) {
            Log.e("DatabaseHelper", "게시글 저장 중 오류 발생: " + e.getMessage());
        }

        return newId;
    }


    public interface OnDatabaseChangeListener {
        void onPostAdded();
        void onCommentAdded(); // 댓글 추가 시 호출되는 메서드 추가
    }



    private void notifyPostAdded() {
        if (onDatabaseChangeListener != null) {
            onDatabaseChangeListener.onPostAdded();
        }
    }


    public CommunityPost getLatestCommunityPost() {
        SQLiteDatabase db = this.getReadableDatabase();
        CommunityPost post = null;

        // 쿼리를 통해 가장 최신 게시물 하나를 가져오는 코드
        Cursor cursor = db.rawQuery("SELECT * FROM " + COMMUNITY_POSTS_TABLE_NAME + " ORDER BY " + POST_COLUMN_ID + " DESC LIMIT 1", null);

        if (cursor != null && cursor.moveToFirst()) {
            // CommunityPost 객체 생성 및 필드 설정
            post = new CommunityPost(
                    cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_CONTENT)),
                    // 추가 필드가 있다면 여기에 추가하세요 (예: 이미지 URI 리스트, 작성자 이름 등)
                    new ArrayList<>(), // 이미지 URI 리스트 (필요 시 수정)
                    cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_USER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_LIKE_COUNT)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_COMMENT_COUNT)),
                    false, // 기본 좋아요 상태 (필요 시 수정)
                    cursor.getString(cursor.getColumnIndexOrThrow("author_name")), // 작성자 이름 (필요 시 수정)
                    cursor.getString(cursor.getColumnIndexOrThrow("author_icon"))  // 작성자 아이콘 (필요 시 수정)
            );
        }

        if (cursor != null) {
            cursor.close(); // 커서를 닫아서 메모리 누수를 방지
        }

        return post; // 최신 게시물 하나 반환
    }




    public CommunityPost getCommunityPostById(long postId) {
        SQLiteDatabase db = getReadableDatabase();
        CommunityPost post = null;

        String query = "SELECT * FROM community_posts WHERE post_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)});

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
            List<String> imageUris = imageUriString != null && !imageUriString.isEmpty()
                    ? Arrays.asList(imageUriString.split(","))
                    : new ArrayList<>();
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
            int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
            int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
            String authorName = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
            String authorIcon = cursor.getString(cursor.getColumnIndexOrThrow("author_icon"));

            post = new CommunityPost(
                    (int) postId, title, content, imageUris, userId, likeCount, commentCount, false, authorName, authorIcon
            );
        }

        cursor.close();
        return post;
    }

    public void updateUserName(String email, String newUserName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_name", newUserName);

        int rows = db.update(USERS_TABLE_NAME, values, "email = ?", new String[]{email});
        if (rows > 0) {
            Log.d("DatabaseHelper", "닉네임 업데이트 성공: " + newUserName);
        } else {
            Log.e("DatabaseHelper", "닉네임 업데이트 실패: 이메일=" + email);
        }
    }

    // 이메일을 기반으로 닉네임 업데이트
    public void updateUserNameByEmail(String email, String newUserName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_name", newUserName);

        int rows = db.update(USERS_TABLE_NAME, values, "email = ?", new String[]{email});
        if (rows > 0) {
            Log.d("DatabaseHelper", "닉네임 업데이트 성공: " + newUserName);
        } else {
            Log.e("DatabaseHelper", "닉네임 업데이트 실패: 이메일=" + email);
        }
    }

    public Comment addCommentAndGet(Comment comment) {
        SQLiteDatabase db = getDatabase();
        Comment insertedComment = null;

        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(COMMENT_COLUMN_CONTENT, comment.getContent());
            values.put(COMMENT_COLUMN_USER_ID, comment.getUserId());
            values.put(COMMENT_COLUMN_POST_ID, comment.getPostId());
            values.put(COMMENT_COLUMN_LIKE_COUNT, comment.getLikeCount());
            values.put("commenter_name", comment.getCommenterName());
            values.put("commenter_icon", comment.getCommenterIcon());

            long rowId = db.insert(COMMENTS_TABLE_NAME, null, values);

            if (rowId != -1) {
                db.setTransactionSuccessful();

                // 방금 삽입된 댓글 가져오기
                String query = "SELECT * FROM " + COMMENTS_TABLE_NAME + " WHERE comment_id = ?";
                Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(rowId)});
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_ID));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_CONTENT));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_USER_ID));
                    int postId = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_POST_ID));
                    int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_LIKE_COUNT));
                    String commenterName = cursor.getString(cursor.getColumnIndexOrThrow("commenter_name"));
                    String commenterIcon = cursor.getString(cursor.getColumnIndexOrThrow("commenter_icon"));

                    insertedComment = new Comment(id, content, userId, postId, likeCount, commenterName, commenterIcon);
                }
                cursor.close();
            } else {
                Log.e("DatabaseHelper", "댓글 삽입 실패");
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "댓글 삽입 오류: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return insertedComment;
    }

    private CommunityPost getCommunityPostFromCursor(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return null;
        }

        int id = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
        String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
        List<String> imageUris = (imageUriString != null && !imageUriString.isEmpty())
                ? Arrays.asList(imageUriString.split(","))
                : new ArrayList<>();
        String userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
        int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
        int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
        String authorName = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
        String authorIcon = cursor.getString(cursor.getColumnIndexOrThrow("author_icon"));

        return new CommunityPost(
                id,
                title != null ? title : "제목 없음",
                content != null ? content : "내용 없음",
                imageUris,
                userId != null ? userId : "익명 사용자",
                likeCount,
                commentCount,
                false,
                authorName != null ? authorName : "익명 사용자",
                authorIcon != null ? authorIcon : "fk_mmm"
        );
    }

    public List<CommunityPost> searchPosts(String keyword) {
        List<CommunityPost> posts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM community_posts WHERE title LIKE ? OR content LIKE ? ORDER BY post_id DESC";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + keyword + "%", "%" + keyword + "%"});

        while (cursor.moveToNext()) {
            posts.add(getCommunityPostFromCursor(cursor));
        }
        cursor.close();
        return posts;
    }

    // DatabaseHelper.java

    public boolean deletePostById(int postId) {
        SQLiteDatabase db = getDatabase();
        boolean isDeleted = false;

        try {
            db.beginTransaction();

            // 해당 게시글의 댓글 먼저 삭제
            db.delete(COMMENTS_TABLE_NAME, COMMENT_COLUMN_POST_ID + " = ?", new String[]{String.valueOf(postId)});

            // 게시글 삭제
            int rowsAffected = db.delete(COMMUNITY_POSTS_TABLE_NAME, POST_COLUMN_ID + " = ?", new String[]{String.valueOf(postId)});
            isDeleted = rowsAffected > 0;

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "게시글 삭제 실패: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return isDeleted;
    }

    public void updateLikeCount(int commentId, boolean isLiked) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        String columnName = isLiked ? "likeCount" : "likeCount";

        // 좋아요 증가 또는 감소
        if (isLiked) {
            db.execSQL("UPDATE comments SET likeCount = likeCount + 1 WHERE comment_id = ?", new Object[]{commentId});
        } else {
            db.execSQL("UPDATE comments SET likeCount = likeCount - 1 WHERE comment_id = ?", new Object[]{commentId});
        }

        Log.d("DatabaseHelper", "댓글 ID: " + commentId + ", 좋아요 상태 업데이트: " + (isLiked ? "추가" : "취소"));
    }

    public void updateCommentLike(int commentId, String userId, boolean isLiked) {
        SQLiteDatabase db = getDatabase();
        try {
            if (isLiked) {
                // 좋아요 추가
                ContentValues values = new ContentValues();
                values.put("comment_id", commentId);
                values.put("user_id", userId);
                db.insert("comment_likes", null, values);
            } else {
                // 좋아요 취소
                db.delete("comment_likes", "comment_id = ? AND user_id = ?", new String[]{String.valueOf(commentId), userId});
            }
            // 좋아요 수 업데이트
            updateLikeCount(commentId);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "댓글 좋아요 업데이트 오류: " + e.getMessage());
        }
    }
    private void updateLikeCount(int commentId) {
        SQLiteDatabase db = getDatabase();
        String query = "SELECT COUNT(*) FROM comment_likes WHERE comment_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(commentId)});
        if (cursor.moveToFirst()) {
            int likeCount = cursor.getInt(0); // 현재 좋아요 개수
            ContentValues values = new ContentValues();
            values.put("likeCount", likeCount);
            db.update("comments", values, "comment_id = ?", new String[]{String.valueOf(commentId)});
        }
        cursor.close();
    }

    public boolean isCommentLikedByUser(int commentId, String userId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM comment_likes WHERE comment_id = ? AND user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(commentId), userId});
        boolean isLiked = false;
        if (cursor.moveToFirst()) {
            isLiked = cursor.getInt(0) > 0;
        }
        cursor.close();
        return isLiked;
    }
}