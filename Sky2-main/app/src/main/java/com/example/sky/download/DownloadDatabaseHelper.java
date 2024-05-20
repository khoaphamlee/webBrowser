package com.example.sky.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "downloads.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "downloads";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_FILE_SIZE = "file_size";
    public static final String COLUMN_FILE_DATE = "file_date";
    public static final String COLUMN_FILE_URI = "file_uri";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FILE_NAME + " TEXT, " +
                    COLUMN_FILE_SIZE + " TEXT, " +
                    COLUMN_FILE_DATE + " TEXT, " +
                    COLUMN_FILE_URI + " TEXT);";

    public DownloadDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
