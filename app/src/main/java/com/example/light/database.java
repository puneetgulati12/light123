package com.example.light;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LightDatabase";
    private static final int DATABASE_VERSION = 2;
    //table names
    private static final String TABLE_KEY = "key2";
    private static final String TABLE_SATELLITE = "Satellite";

//table columns
    private static final String TABLE_KEY_ID = "district";
    private static final String KEY_VALUE = "value";
     //    private static final String KEY_POST_TEXT = "text";
     //user table columns
    private static final String KEY_SAT_MON = "Month";
    private static final String KEY_SAT_VIS = "VisMedian";
    private static final String KEY_SAT_COU = "count";
    private static final String KEY_SAT_YEAR = "Year";
    private static final String TAG = "";
    private static final String _ID = "ID";


    private static database sInstance;

    public static synchronized database getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new database(context.getApplicationContext());
        }
        return sInstance;
    }


    public database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_KEY +
                "(" +
                 _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                TABLE_KEY_ID + " TEXT, " + // Define a primary key
                KEY_VALUE + " TEXT" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_SATELLITE +
                "(" +
                KEY_SAT_MON + " TEXT," +
                KEY_SAT_COU + " TEXT," +
                KEY_SAT_VIS + " TEXT," + KEY_SAT_YEAR + " TEXT" +
                ")";

        sqLiteDatabase.execSQL(CREATE_POSTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {

        if (oldversion != newversion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_KEY);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SATELLITE);
            onCreate(sqLiteDatabase);
        }

    }

    public void addpost(Post post) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.beginTransaction();


        long userId = addOrUpdateUser(post.user);
        ContentValues values = new ContentValues();
//        sqLiteDatabase.insert("Satellite" , null , values);

        values.put(TABLE_KEY_ID, userId);
        values.put(KEY_VALUE, post.text);

        sqLiteDatabase.insertOrThrow(TABLE_KEY, null, values);
        sqLiteDatabase.setTransactionSuccessful();
    }

    public long addOrUpdateUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long userId = -1;

        sqLiteDatabase.beginTransaction();


        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SAT_MON, user.Month);
        contentValues.put(KEY_SAT_COU, user.count);
        contentValues.put(KEY_SAT_YEAR, user.year);
        contentValues.put(KEY_SAT_VIS, user.VisMedian);
//        contentValues.put(KEY_USER_PROFILE_PICTURE_URL, user.ProfilePictureUrl);

        // First update the user in case the user already exists in the database

        int rows = sqLiteDatabase.update(TABLE_SATELLITE, contentValues,
                KEY_SAT_COU
                        + "?", new String[]{user.count});

        // Check if update succeeded
        if (rows == 1) {
            String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?", KEY_SAT_COU, TABLE_SATELLITE, KEY_SAT_COU);

            Cursor cursor = sqLiteDatabase.rawQuery(usersSelectQuery, new String[]{String.valueOf(user.count)});

            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
                sqLiteDatabase.setTransactionSuccessful();
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } else {
            userId = sqLiteDatabase.insertOrThrow(TABLE_SATELLITE, null, contentValues);
            sqLiteDatabase.setTransactionSuccessful();
        }


        return userId;
    }

    public class Post {
        public User user;
        public String text;

    }

    public class User {
        public String count;
        public String year;
        public String Month;
        public String VisMedian;
    }

    // Get all posts in the database
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                        TABLE_KEY,
                        TABLE_SATELLITE,
                        TABLE_KEY, KEY_VALUE,
                        TABLE_SATELLITE, KEY_SAT_COU);
        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    User newUser = new User();
                    newUser.count = cursor.getString(cursor.getColumnIndex(KEY_SAT_COU));
                    newUser.year = cursor.getString(cursor.getColumnIndex(KEY_SAT_YEAR));
                    newUser.Month = cursor.getString(cursor.getColumnIndex(KEY_SAT_MON));
                    newUser.VisMedian = cursor.getString(cursor.getColumnIndex(KEY_SAT_VIS));
//                    newUser.ProfilePictureUrl = cursor.getString(cursor.getColumnIndex(KEY_USER_PROFILE_PICTURE_URL));

                    Post newPost = new Post();
//                    newPost.text = cursor.getString(cursor.getColumnIndex(KEY_POST_TEXT));
                    newPost.user = newUser;
                    posts.add(newPost);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error occur while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return posts;
    }
    // Update the user's profile picture url
//    public int updateUserProfilePicture(User user) {
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(KEY_USER_PROFILE_PICTURE_URL, user.ProfilePictureUrl);
//
//        // Updating profile picture url for user with that userName
//        return sqLiteDatabase.update(TABLE_USERS, contentValues, KEY_USER_NAME + " = ?",
//                new String[] { String.valueOf(user.username) });
//    }

    // Delete all posts and users in the database
    public void deleteAllPostsAndUsers() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            sqLiteDatabase.delete(TABLE_KEY, null, null);
            sqLiteDatabase.delete(TABLE_SATELLITE, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error occur while trying to delete all posts and users");
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }



}