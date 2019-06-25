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
    private static final String DATABASE_NAME = "postsDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_USERS = "users";


    private static final String KEY_POST_ID = "id";
    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_TEXT = "text";

    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";
    private static final String TAG = "";


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

        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS +
                "(" +
                KEY_POST_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_POST_TEXT + " TEXT" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
                ")";

        sqLiteDatabase.execSQL(CREATE_POSTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {

        if (oldversion != newversion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(sqLiteDatabase);
        }

    }

    public void addpost(Post post) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();


        long userId = addOrUpdateUser(post.user);
        ContentValues values = new ContentValues();

        values.put(KEY_POST_USER_ID_FK, userId);
        values.put(KEY_POST_TEXT, post.text);

        sqLiteDatabase.insertOrThrow(TABLE_POSTS, null, values);
        sqLiteDatabase.setTransactionSuccessful();
    }

    public long addOrUpdateUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long userId = -1;

        sqLiteDatabase.beginTransaction();


        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USER_NAME, user.username);
        contentValues.put(KEY_USER_PROFILE_PICTURE_URL, user.ProfilePictureUrl);

        // First update the user in case the user already exists in the database

        int rows = sqLiteDatabase.update(TABLE_USERS, contentValues,
                KEY_USER_NAME
                        + "?", new String[]{user.username});

        // Check if update succeeded
        if (rows == 1) {
            String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?", KEY_USER_ID, TABLE_USERS, KEY_USER_NAME);

            Cursor cursor = sqLiteDatabase.rawQuery(usersSelectQuery, new String[]{String.valueOf(user.username)});

            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
                sqLiteDatabase.setTransactionSuccessful();
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } else {
            userId = sqLiteDatabase.insertOrThrow(TABLE_USERS, null, contentValues);
            sqLiteDatabase.setTransactionSuccessful();
        }


        return userId;
    }

    public class Post {
        public User user;
        public String text;

    }

    public class User {
        public String username;
        public String ProfilePictureUrl;
    }

    // Get all posts in the database
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                        TABLE_POSTS,
                        TABLE_USERS,
                        TABLE_POSTS, KEY_POST_USER_ID_FK,
                        TABLE_USERS, KEY_USER_ID);
        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    User newUser = new User();
                    newUser.username = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
                    newUser.ProfilePictureUrl = cursor.getString(cursor.getColumnIndex(KEY_USER_PROFILE_PICTURE_URL));

                    Post newPost = new Post();
                    newPost.text = cursor.getString(cursor.getColumnIndex(KEY_POST_TEXT));
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
    public int updateUserProfilePicture(User user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USER_PROFILE_PICTURE_URL, user.ProfilePictureUrl);

        // Updating profile picture url for user with that userName
        return sqLiteDatabase.update(TABLE_USERS, contentValues, KEY_USER_NAME + " = ?",
                new String[] { String.valueOf(user.username) });
    }

    // Delete all posts and users in the database
    public void deleteAllPostsAndUsers() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            sqLiteDatabase.delete(TABLE_POSTS, null, null);
            sqLiteDatabase.delete(TABLE_USERS, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error occur while trying to delete all posts and users");
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }



}