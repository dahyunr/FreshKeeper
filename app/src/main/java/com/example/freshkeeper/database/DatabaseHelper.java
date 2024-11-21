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
    private static final int DATABASE_VERSION = 22;

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
                    "commenter_name TEXT, " +  // 작성자 이름
                    "commenter_icon TEXT, " +  // 작성자 아이콘
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
            "CREATE TABLE IF NOT EXISTS " + COMMUNITY_POSTS_TABLE_NAME + " (" +
                    POST_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    POST_COLUMN_TITLE + " TEXT, " +
                    POST_COLUMN_CONTENT + " TEXT, " +
                    POST_COLUMN_IMAGE_URI + " TEXT, " +
                    POST_COLUMN_USER_ID + " TEXT, " +
                    POST_COLUMN_LIKE_COUNT + " INTEGER DEFAULT 0, " +
                    POST_COLUMN_COMMENT_COUNT + " INTEGER DEFAULT 0, " +
                    "author_name TEXT, " +  // 작성자 이름 추가
                    "author_icon TEXT);";   // 작성자 아이콘 추가


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
        db.execSQL(COMMUNITY_POSTS_TABLE_CREATE); // 수정된 부분
        db.execSQL(COMMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 21) { // 기존 버전이 21 미만인 경우
            try {
                db.execSQL("ALTER TABLE comments ADD COLUMN commenter_name TEXT;");
                db.execSQL("ALTER TABLE comments ADD COLUMN commenter_icon TEXT;");
                db.execSQL("ALTER TABLE community_posts ADD COLUMN author_name TEXT;");
                db.execSQL("ALTER TABLE community_posts ADD COLUMN author_icon TEXT;");
            } catch (SQLException e) {
                Log.e("DatabaseHelper", "컬럼 추가 오류: " + e.getMessage());
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
    public long addCommunityPost(CommunityPost post) {
        SQLiteDatabase db = getDatabase();

        if (isPostExists(post.getTitle())) {
            Log.e("DatabaseHelper", "중복된 제목의 게시글이 이미 존재합니다.");
            return -1; // 이미 존재하는 경우, -1 반환
        }

        ContentValues values = new ContentValues();

        // 기본값 처리
        String safeTitle = post.getTitle() != null && !post.getTitle().isEmpty() ? post.getTitle() : "제목 없음";
        String safeContent = post.getContent() != null && !post.getContent().isEmpty() ? post.getContent() : "내용 없음";
        String safeUserId = post.getUserId() != null && !post.getUserId().isEmpty() ? post.getUserId() : "default_user_id";

        values.put(POST_COLUMN_TITLE, safeTitle);
        values.put(POST_COLUMN_CONTENT, safeContent);
        values.put(POST_COLUMN_USER_ID, safeUserId);
        values.put(POST_COLUMN_LIKE_COUNT, post.getLikeCount());
        values.put(POST_COLUMN_COMMENT_COUNT, post.getCommentCount());

        // 이미지 URI 처리
        String imageUris = post.getImageUris() != null && !post.getImageUris().isEmpty()
                ? String.join(",", post.getImageUris())
                : "";
        values.put(POST_COLUMN_IMAGE_URI, imageUris);

        // 작성자 이름과 아이콘 처리
        values.put("author_name", post.getAuthorName() != null ? post.getAuthorName() : "익명 사용자");
        values.put("author_icon", post.getAuthorIcon() != null ? post.getAuthorIcon() : "fk_mmm");

        long newId = -1;
        try {
            newId = db.insert(COMMUNITY_POSTS_TABLE_NAME, null, values);
            if (newId == -1) {
                Log.e("DatabaseHelper", "게시글 삽입 오류");
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "게시글 삽입 오류: " + e.getMessage());
        }

        return newId; // 새로 생성된 게시물의 ID 반환
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

        String query = "SELECT p.*, u." + USER_COLUMN_NAME + " AS author_name, u.icon_path AS author_icon " +
                "FROM " + COMMUNITY_POSTS_TABLE_NAME + " p " +
                "LEFT JOIN " + USERS_TABLE_NAME + " u " +
                "ON p." + POST_COLUMN_USER_ID + " = u." + USER_COLUMN_ID +
                " ORDER BY p." + POST_COLUMN_ID + " DESC";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_TITLE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_CONTENT));
                    String imageUrisString = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_IMAGE_URI));
                    List<String> imageUris = (imageUrisString != null && !imageUrisString.isEmpty())
                            ? Arrays.asList(imageUrisString.split(","))
                            : new ArrayList<>(); // 이미지 없으면 빈 리스트로 처리

                    String userId = cursor.getString(cursor.getColumnIndexOrThrow(POST_COLUMN_USER_ID));
                    int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_LIKE_COUNT));
                    int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow(POST_COLUMN_COMMENT_COUNT));

                    String authorName = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
                    String authorIcon = cursor.getString(cursor.getColumnIndexOrThrow("author_icon"));

                    // 기본값 설정
                    authorName = (authorName != null && !authorName.isEmpty()) ? authorName : "익명 사용자";
                    authorIcon = (authorIcon != null && !authorIcon.isEmpty()) ? authorIcon : "fk_mmm";

                    CommunityPost post = new CommunityPost(
                            id,
                            title != null ? title : "제목 없음",
                            content != null ? content : "내용 없음",
                            imageUris,
                            userId != null ? userId : "default_user_id",
                            likeCount,
                            commentCount,
                            false,  // 기본값: 좋아요 상태 false
                            authorName,
                            authorIcon
                    );

                    postList.add(post);
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "게시글 데이터가 없습니다.");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "게시글 데이터 로드 중 오류 발생: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return postList;
    }


    // DatabaseHelper.java
    public void addComment(Comment comment) {
        SQLiteDatabase db = getDatabase();
        db.beginTransaction(); // 트랜잭션 시작
        try {
            ContentValues values = new ContentValues();

            // Null 및 빈 값 체크를 통해 안전한 값 설정
            String safeCommenterName = comment.getCommenterName() != null && !comment.getCommenterName().isEmpty()
                    ? comment.getCommenterName()
                    : "익명 사용자";
            String safeCommenterIcon = comment.getCommenterIcon() != null && !comment.getCommenterIcon().isEmpty()
                    ? comment.getCommenterIcon()
                    : "fk_mmm";

            // 댓글 정보 설정
            values.put(COMMENT_COLUMN_CONTENT, comment.getContent());
            values.put(COMMENT_COLUMN_USER_ID, comment.getUserId());
            values.put(COMMENT_COLUMN_POST_ID, comment.getPostId());
            values.put(COMMENT_COLUMN_LIKE_COUNT, comment.getLikeCount());
            values.put("commenter_name", comment.getCommenterName() != null ? comment.getCommenterName() : "익명 사용자");
            values.put("commenter_icon", comment.getCommenterIcon() != null ? comment.getCommenterIcon() : "fk_mmm");

            // 댓글 삽입
            long rowId = db.insert(COMMENTS_TABLE_NAME, null, values);
            if (rowId != -1) {
                Log.d("DatabaseHelper", "댓글 삽입 성공: " + comment.getContent());
                // 댓글 수 업데이트
                updateCommentCount(comment.getPostId());
                db.setTransactionSuccessful(); // 트랜잭션 성공으로 설정
            } else {
                Log.e("DatabaseHelper", "댓글 삽입 실패: 알 수 없는 오류");
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "댓글 삽입 오류: " + e.getMessage());
        } finally {
            db.endTransaction(); // 트랜잭션 종료
        }
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> commentList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // 댓글과 작성자 정보를 가져오는 SELECT 쿼리
        String query = "SELECT c.*, u." + USER_COLUMN_NAME + " AS commenter_name, u.icon_path AS commenter_icon " +
                "FROM " + COMMENTS_TABLE_NAME + " c " +
                "LEFT JOIN " + USERS_TABLE_NAME + " u " +
                "ON c." + COMMENT_COLUMN_USER_ID + " = u." + USER_COLUMN_ID + " " +
                "WHERE c." + COMMENT_COLUMN_POST_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)});

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 각 필드를 안전하게 가져오기
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_ID));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_CONTENT));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_USER_ID));
                    int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow(COMMENT_COLUMN_LIKE_COUNT));

                    // 작성자 이름 및 아이콘 값 처리 (null 또는 빈 값 체크 후 기본값 적용)
                    String commenterName = cursor.getString(cursor.getColumnIndexOrThrow("commenter_name"));
                    if (commenterName == null || commenterName.isEmpty()) {
                        commenterName = "익명 사용자";
                    }

                    String commenterIcon = cursor.getString(cursor.getColumnIndexOrThrow("commenter_icon"));
                    if (commenterIcon == null || commenterIcon.isEmpty()) {
                        commenterIcon = "fk_mmm";
                    }

                    // Comment 객체 생성 및 리스트에 추가
                    Comment comment = new Comment(id, content, userId, postId, likeCount, commenterName, commenterIcon);
                    commentList.add(comment);

                    // 로깅 추가
                    Log.d("DatabaseHelper", "불러온 댓글: ID=" + id + ", 내용=" + content);
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "해당 게시물에 댓글이 없습니다: postId=" + postId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "댓글 불러오기 중 오류 발생: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // 커서 닫기
            }
        }

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
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)});
            int count = 0;
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(POST_COLUMN_COMMENT_COUNT, count);

            db.update(COMMUNITY_POSTS_TABLE_NAME, values, POST_COLUMN_ID + " = ?", new String[]{String.valueOf(postId)});
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "댓글 수 업데이트 오류: " + e.getMessage());
        }
    }

    public boolean isPostExists(String title) {
        if (title == null || title.isEmpty()) return false; // 제목이 비어 있으면 중복 아님
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + COMMUNITY_POSTS_TABLE_NAME + " WHERE " + POST_COLUMN_TITLE + " = ?", new String[]{title});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void addComment(int postId, String commentContent, String authorName) {
        // 댓글을 데이터베이스에 추가하는 로직 구현
    }

    public void updateCommentCount(int postId, int newCommentCount) {
        // 게시글의 댓글 수를 데이터베이스에 업데이트하는 로직 구현
    }

    public int getCommentCount(int postId) {
        // 게시글의 댓글 수를 데이터베이스에서 가져오는 메서드 구현
        return 0; // 실제 구현 필요
    }

}
